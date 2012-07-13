package com.velik.recommend.spider;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Spider {
	private static final Logger LOGGER = Logger.getLogger(Spider.class.getName());
	private static final int TIME_BETWEEN_REVISITS = 2000;

	private HostSpecificUrlShortener shortener;
	private PersistedUrlsToVisit urlsToVisit;
	private PersistedVisitedUrls visitedUrls;
	private SpideredHtmlFile spideredHtmlFile;
	private List<UrlFilter> urlFilters = new ArrayList<UrlFilter>();

	private Random random = new Random();

	private UrlExtractor urlExtractor = new UrlExtractor();
	private String[] HOSTS = new String[] { "www.sueddeutsche.de", "www.zeit.de", "www.spiegel.de",
			"www.n24.de" };

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

		try {
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
						url = urlsToVisit.next(host);
						queueEmpty = false;

						System.out.println(readUrls + ": " + url);

						String html = load(url);

						spideredHtmlFile.write(url, html);
						visitedUrls.add(url);
						readUrls++;

						if (readUrls % 500 == 0) {
							urlsToVisit.persist();
						}

						if (readUrls >= 50) {
							break done;
						}

						int found = 0;

						nextUrl: for (HashableUrl foundUrl : urlExtractor.extract(html)) {
							for (UrlFilter urlFilter : urlFilters) {
								if (!urlFilter.isFollow(foundUrl.toUrl(), url)) {
									continue nextUrl;
								}
							}

							if (!visitedUrls.contains(foundUrl) && !urlsToVisit.contains(foundUrl)) {
								urlsToVisit.add(foundUrl);
								found++;
							}
						}

						System.out.println("Found " + found + " links.");
					} catch (NoMoreUrlsException e) {
					} catch (IOException e) {
						LOGGER.log(Level.WARNING, "Loading URL " + url + ": " + e.toString());
					}
				}
			} while (!queueEmpty);
		} finally {
			visitedUrls.close();
			urlsToVisit.persist();
			spideredHtmlFile.close();
		}
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

		try {
			charBuffer.clear();

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),
					charset), 100000);

			StringBuffer result = new StringBuffer(1000000);

			String line;

			while ((line = reader.readLine()) != null) {
				result.append(line);
			}

			System.out.println(result.length() + " bytes");

			return result.toString();
		} catch (UnsupportedEncodingException e) {
			throw new IOException("Charset " + charset + " returned as encoding is unknown.");
		}
	}

	private void initialize() throws IOException {
		shortener = new HostSpecificUrlShortener();

		shortener.put("www.sueddeutsche.de", new SueddeutscheUrlShortener());
		shortener.put("www.faz.net", new FazUrlShortener());
		shortener.put("www.spiegel.de", new FazUrlShortener());
		// zeit can't be shortened.

		visitedUrls = new PersistedVisitedUrls(new File("visited-urls.txt"), shortener);

		spideredHtmlFile = new SpideredHtmlFile(new File("spidered-html.txt"));

		urlsToVisit = new PersistedUrlsToVisit(new File("urls-to-visit.txt"));

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

		urlFilters.add(new SameHostUrlFilter());
		urlFilters.add(new OnlyHtmlUrlFilter());
		urlFilters.add(new OnlyFirstPageUrlFilter());
		urlFilters.add(new DoesNotContainUrlFilter("cgi-bin"));
		urlFilters.add(new DoesNotContainUrlFilter("fotostrecken"));
	}
}
