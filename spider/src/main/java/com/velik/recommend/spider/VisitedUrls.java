package com.velik.recommend.spider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.velik.recommend.util.Utf8String;
import com.velik.util.FetchingIterator;

public class VisitedUrls implements UrlCollection {
	private static final Logger LOGGER = Logger.getLogger(VisitedUrls.class.getName());

	protected Map<String, Set<Utf8String>> pathsByHost = new HashMap<String, Set<Utf8String>>();
	private UrlShortener shortener;

	public VisitedUrls(UrlShortener shortener) {
		this.shortener = shortener;
	}

	public boolean contains(HashableUrl url) {
		return contains(url.toUrl());
	}

	public int size() {
		int result = 0;

		for (Set<Utf8String> s : pathsByHost.values()) {
			result += s.size();
		}

		return result;
	}

	@Override
	public boolean contains(URL url) {
		Set<Utf8String> paths = pathsByHost.get(url.getHost().toLowerCase());

		if (paths == null) {
			return false;
		}

		String path = shortener.shorten(url).getPath();

		if (path.equals("")) {
			path = "/";
		}

		return paths.contains(new Utf8String(path));
	}

	public boolean add(HashableUrl url) {
		return add(url.toUrl());
	}

	@Override
	public boolean add(URL url) {
		Set<Utf8String> paths = pathsByHost.get(url.getHost().toLowerCase());

		if (paths == null) {
			paths = new HashSet<Utf8String>();

			pathsByHost.put(url.getHost(), paths);
		}

		String path = shortener.shorten(url).getPath();

		if (path.equals("")) {
			path = "/";
		}

		return paths.add(new Utf8String(path));
	}

	@Override
	public Iterator<URL> iterator() {
		return new FetchingIterator<URL>() {
			private Iterator<Entry<String, Set<Utf8String>>> entryIterator = pathsByHost.entrySet().iterator();
			private Iterator<Utf8String> pathIterator = null;
			private String domain;

			@Override
			protected URL fetch() {
				while (pathIterator == null || !pathIterator.hasNext()) {
					if (!entryIterator.hasNext()) {
						return null;
					}

					Entry<String, Set<Utf8String>> nextEntry = entryIterator.next();
					domain = nextEntry.getKey();
					pathIterator = nextEntry.getValue().iterator();
				}

				try {
					return new URL("http", domain, pathIterator.next().toString());
				} catch (MalformedURLException e) {
					LOGGER.log(Level.WARNING, e.toString(), e);

					return fetch();
				}
			}
		};
	}

	public boolean isEmpty() {
		for (Entry<String, Set<Utf8String>> entry : pathsByHost.entrySet()) {
			if (!entry.getValue().isEmpty()) {
				return false;
			}
		}

		return true;
	}
}
