package com.velik.recommend.spider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.velik.recommend.util.Utf8String;

public class UrlsToVisit extends VisitedUrls implements UrlCollection {
	private static final Logger LOGGER = Logger.getLogger(UrlsToVisit.class.getName());

	public UrlsToVisit() {
		super(new IdentityUrlShortener());
	}

	public boolean contains(HashableUrl url) {
		return contains(url.toUrl());
	}

	public URL next(String host) throws NoMoreUrlsException {
		Set<Utf8String> paths = pathsByHost.get(host.toLowerCase());

		if (paths == null) {
			throw new NoMoreUrlsException();
		}

		Iterator<Utf8String> it = paths.iterator();

		if (!it.hasNext()) {
			throw new NoMoreUrlsException();
		}

		Utf8String path = it.next();

		it.remove();

		try {
			return new URL("http", host, path.toString());
		} catch (MalformedURLException e) {
			LOGGER.log(Level.WARNING, "For " + path + ": " + e.getMessage(), e);

			return next(host);
		}
	}

}
