package com.velik.recommend.log;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AccessLoggingFilter implements Filter {
	private static final String GOOGLE_ANALYTICS_COOKIE = "__utmz";

	private static final Logger LOGGER = Logger.getLogger(AccessLoggingFilter.class.getName());

	private static final String LOG_FILE_PARAMETER = "logFile";

	private static final String DEFAULT_LOG_FILE = "read-articles-by-user.log";

	private static final int MAX_RUNTIME_EXCEPTIONS = 50;

	private static final int MAX_TIME_MS = 10;

	private static final String CONTROL_KEY = "qwerty";

	private boolean isLogging = true;

	private volatile int noOfLogs;

	private AccessLog accessLog;

	private int logInvalidRequests = 100;

	private volatile int runtimeExceptions;

	@Override
	public void destroy() {
		accessLog.close();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpServletResponse httpResponse = (HttpServletResponse) response;

			if (isControlParameter(httpRequest, httpResponse)) {
				return;
			}

			if (isLogging) {
				long startTime = System.currentTimeMillis();

				try {
					innerLog(httpRequest);

					long totalTime = System.currentTimeMillis() - startTime;

					if (totalTime > MAX_TIME_MS * noOfLogs++ && noOfLogs > 500) {
						LOGGER.log(Level.SEVERE, "Logging took an average of " + (totalTime / noOfLogs)
								+ " ms per request, which is too slow. Stopping logging.");

						isLogging = false;
					}
				} catch (Throwable e) {
					LOGGER.log(Level.WARNING, "Runtime exception logging "
							+ getRequestDescription(httpRequest) + ": " + e, e);

					if (runtimeExceptions++ > MAX_RUNTIME_EXCEPTIONS) {
						LOGGER.log(Level.WARNING, "Too many runtime exceptions. Will stop logging.");
						isLogging = false;
					}
				}
			}
		}

		filterChain.doFilter(request, response);
	}

	private boolean isControlParameter(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		if (CONTROL_KEY.equals(request.getParameter("key"))) {
			String logging = request.getParameter("logging");

			if (logging != null) {
				isLogging = Boolean.parseBoolean(logging);
			}

			String logInvalid = request.getParameter("loginvalid");

			if (logInvalid != null) {
				logInvalidRequests = Integer.parseInt(logInvalid);
			}

			response.setContentType("text/html");

			response.getOutputStream().print(
					"<body><ul><li>isLogging: " + isLogging + "</li>" + "<li>logInvalidRequests: "
							+ logInvalidRequests + "</li>" + "<li>noOfLogs: " + noOfLogs + "</li>"
							+ "<li>runtimeExceptions: " + runtimeExceptions + "</li></ul></body>");

			return true;
		} else {
			return false;
		}
	}

	private void innerLog(HttpServletRequest httpRequest) {
		try {
			ContentId contentId = getContentId(httpRequest);

			long googleUserId = getGoogleUserId(httpRequest);

			accessLog.log(new DefaultAccess(contentId.getMajor(), contentId.getMinor(), googleUserId));
		} catch (InvalidRequestException e) {
			if (logInvalidRequests-- > 0) {
				LOGGER.log(Level.WARNING, "When logging request " + getRequestDescription(httpRequest) + ": "
						+ e.getMessage());
			}
		} catch (NoTrackingCookieException e) {
			// fine.
		}
	}

	private String getRequestDescription(HttpServletRequest request) {
		return request.getRequestURI() + " from " + request.getRemoteAddr();
	}

	private long getGoogleUserId(HttpServletRequest request) throws InvalidRequestException,
			NoTrackingCookieException {
		String googleCode = null;

		for (Cookie cookie : request.getCookies()) {
			if (cookie.getName().equals(GOOGLE_ANALYTICS_COOKIE)) {
				googleCode = cookie.getValue();
			}
		}

		if (googleCode == null) {
			throw new NoTrackingCookieException("Request did not provide a Google Analytics cookie.");
		}

		int i = googleCode.indexOf('.');

		if (i > 0) {
			googleCode = googleCode.substring(0, i);
		}

		try {
			return Long.parseLong(googleCode.substring(0, i));
		} catch (NumberFormatException e) {
			throw new InvalidRequestException("Google Analytics cookie until first dot \"" + googleCode
					+ "\" was not a number.");
		}
	}

	private ContentId getContentId(HttpServletRequest request) throws InvalidRequestException {
		String parameter = request.getParameter("a");

		if (parameter == null) {
			parameter = request.getParameter("d");

			if (parameter == null) {
				throw new InvalidRequestException("Got no \"a\" or \"d\" parameters with article ID.");
			}

			if (parameter.startsWith("/")) {
				parameter = parameter.substring(1);
			}

			int i = parameter.lastIndexOf('/');

			if (i > 0) {
				parameter = parameter.substring(i + 1);
			}
		}

		if (!(parameter.startsWith("1.") || parameter.startsWith("2.")) || parameter.length() <= 2) {
			throw new InvalidRequestException("The request parameter \"" + parameter
					+ "\" does not seem to be a content ID (query string " + request.getQueryString() + ").");
		}

		try {
			return new ContentId(Integer.parseInt(parameter.substring(0, 1)), Integer.parseInt(parameter
					.substring(2)));
		} catch (NumberFormatException e) {
			throw new InvalidRequestException("Minor of article ID \"" + parameter + "\" was not a number.");
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		String logFileName = config.getInitParameter(LOG_FILE_PARAMETER);

		if (logFileName == null) {
			LOGGER.log(Level.WARNING, "No log file name speciifed as " + LOG_FILE_PARAMETER + " parameter.");

			logFileName = DEFAULT_LOG_FILE;
		}

		accessLog = new RotatingFileAccessLog(logFileName, true);
	}
}
