package com.velik.recommend.spider;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipFile;

import com.velik.recommend.parser.SpideredDocument;
import com.velik.recommend.parser.SpideredHtmlZipFileReader;

public class Spider {
	private static final Logger LOGGER = Logger.getLogger(Spider.class.getName());
	private static final int TIME_BETWEEN_REVISITS = 10000;

	private HostSpecificUrlShortener shortener;
	private PersistedUrlsToVisit urlsToVisit;
	private PersistedVisitedUrls visitedUrls;
	private SpideredHtmlFile spideredHtmlFile;
	private List<UrlFilter> urlFilters = new ArrayList<UrlFilter>();

	private Random random = new Random();

	private UrlExtractor urlExtractor = new UrlExtractor();
	private String[] HOSTS = new String[] { "www.sueddeutsche.de", "www.zeit.de", "www.spiegel.de", "www.n24.de",
			"www.rp-online.de" };

	private CharBuffer charBuffer = CharBuffer.allocate(1000000);

	public static void main(String[] args) {
		new Spider().run();
	}

	private void run() {
		try {
			initialize();
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, e.toString(), e);
			return;
		}

		boolean queueEmpty;
		int readUrls = 0;
		long lastVisit = 0;

		done: do {
			queueEmpty = true;

			long revisitTime = lastVisit + random.nextInt(2 * TIME_BETWEEN_REVISITS);

			long wait = revisitTime - System.currentTimeMillis();

			if (wait > 0) {
				try {
					Thread.sleep(wait);
				} catch (InterruptedException e) {
					break done;
				}
			}

			lastVisit = System.currentTimeMillis();

			for (String host : HOSTS) {
				URL url = null;

				try {
					do {
						url = urlsToVisit.next(host);
					} while (!isSane(url));

					queueEmpty = false;

					String html = load(url);

					spideredHtmlFile.write(url, html);
					visitedUrls.add(url);
					readUrls++;

					if (readUrls % 500 == 0) {
						urlsToVisit.persist();
					}

					int found = extractLinks(url, html);

					System.out.println(visitedUrls.size() + ": " + url + ", " + html.length() + " bytes, " + found
							+ " new links. Queue is now " + urlsToVisit.size() + ".");
				} catch (NoMoreUrlsException e) {
				} catch (IOException e) {
					LOGGER.log(Level.WARNING, "Loading URL " + url + ": " + e.toString());
				}
			}
		} while (!queueEmpty);
	}

	private int extractLinks(URL url, String html) {
		int found = 0;

		nextUrl: for (HashableUrl foundUrl : urlExtractor.extract(html, url)) {
			for (UrlFilter urlFilter : urlFilters) {
				if (!urlFilter.isFollow(foundUrl.toUrl(), url)) {
					continue nextUrl;
				}
			}

			if (!visitedUrls.contains(foundUrl) && !urlsToVisit.contains(foundUrl) && isSane(foundUrl.toUrl())) {
				urlsToVisit.add(foundUrl);
				found++;
			}
		}

		return found;
	}

	private boolean isSane(URL foundUrl) {
		String urlString = foundUrl.toString();

		if (urlString.length() > 150) {
			return false;
		}

		if (urlString.indexOf('?') > 0) {
			return false;
		}

		if (foundUrl.getPath().contains(foundUrl.getHost())) {
			return false;
		}

		return true;
	}

	private String load(URL url) throws IOException {
		URLConnection connection = url.openConnection();

		connection.setConnectTimeout(15000);
		connection.setReadTimeout(15000);

		String contentType = connection.getHeaderField("Content-Type");
		String charset = "UTF-8";

		if (contentType != null) {
			for (String param : contentType.replace(" ", "").split(";")) {
				if (param.startsWith("charset=")) {
					charset = param.split("=", 2)[1];
					break;
				}
			}
		}

		if (connection instanceof HttpURLConnection) {
			int responseCode = ((HttpURLConnection) connection).getResponseCode();

			if (responseCode >= 400 && responseCode < 600) {
				throw new IOException("Got response code " + responseCode + ".");
			}
		}

		try {
			charBuffer.clear();

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), charset),
					100000);

			StringBuffer result = new StringBuffer(1000000);

			String line;

			while ((line = reader.readLine()) != null) {
				result.append(line);
			}

			reader.close();

			return result.toString();
		} catch (UnsupportedEncodingException e) {
			throw new IOException("Charset " + charset + " returned as encoding is unknown.");
		}
	}

	private void initialize() throws IOException {
		shortener = new HostSpecificUrlShortener();

		shortener.put("www.sueddeutsche.de", new SueddeutscheUrlShortener());
		shortener.put("www.faz.net", new FazUrlShortener());
		shortener.put("www.spiegel.de", new SpiegelUrlShortener());
		shortener.put("www.n24.de", new N24UrlShortener());
		shortener.put("www.rp-online.de", new RpoUrlShortener());
		// zeit can't be shortened.

		urlFilters.add(new SameHostUrlFilter());
		urlFilters.add(new OnlyHtmlUrlFilter());
		urlFilters.add(new OnlyFirstPageUrlFilter());
		urlFilters.add(new DoesNotContainUrlFilter("cgi-bin"));
		urlFilters.add(new DoesNotContainUrlFilter("fotostrecken"));
		urlFilters.add(new DoesNotContainUrlFilter("sptv"));
		urlFilters.add(new DoesNotContainUrlFilter("video"));
		urlFilters.add(new DoesNotContainUrlFilter("-druck.html")); // spiegel.de
		urlFilters.add(new TopLevelUrlFilter("www.spiegel.de", "thema", "politik", "wirtschaft", "panorama", "sport",
				"kultur", "wissenschaft", "reise", "auto"));
		urlFilters.add(new TopLevelUrlFilter("www.sueddeutsche.de", "thema", "politik", "panorama", "kultur",
				"wirtschaft", "geld", "sport", "wissen", "digital"));
		urlFilters.add(new TopLevelUrlFilter("www.n24.de", "news", "archiv"));
		urlFilters.add(new TopLevelUrlFilter("www.zeit.de", "schlagworte", "politik", "wirtschaft", "kultur",
				"meinung", "gesellschaft", "wissen", "digital", "reisen", "auto", "sport"));
		urlFilters.add(new TopLevelUrlFilter("www.rp-online.de", "politik", "wirtschaft", "panorama", "sport",
				"digitales", "gesellschaft", "kultur", "wissen", "gesundheit", "auto", "reise", "hobby", "bauen"));

		visitedUrls = new PersistedVisitedUrls(new File("visited-urls.txt"), shortener);

		spideredHtmlFile = new SpideredHtmlFile(new File("spidered-html.txt"));

		urlsToVisit = new PersistedUrlsToVisit(new File("urls-to-visit.txt"));

		if (urlsToVisit.isEmpty()) {
			FileRotator rotator = new FileRotator(new File("spidered-html.zip"), false);

			int missingFiles = 0;

			while (missingFiles < 10) {
				if (rotator.fileExists()) {
					SpideredHtmlZipFileReader reader = new SpideredHtmlZipFileReader(new ZipFile(
							rotator.generateFileName()));

					int found = 0;

					for (SpideredDocument document : reader) {
						found += extractLinks(document.getUrl(), document.getHtml());
					}

					System.out.println(rotator.generateFileName() + ": found " + found + " links.");

					if (urlsToVisit.size() > 100000) {
						break;
					}
				} else {
					missingFiles++;
				}

				rotator.next();
			}

			urlsToVisit.persist();
		}

		try {
			for (String host : HOSTS) {
				URL homePage = new URL("http://" + host);

				if (!visitedUrls.contains(homePage)) {
					urlsToVisit.add(homePage);
				}
			}
		} catch (MalformedURLException e) {
			LOGGER.log(Level.WARNING, e.toString(), e);
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				System.out.println("Persisting data...");

				visitedUrls.close();
				urlsToVisit.persist();
				spideredHtmlFile.close();

				System.out.println("Done.");
			}

		});

	}
}
