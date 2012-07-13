package com.velik.recommend.spider;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class PersistedVisitedUrls extends VisitedUrls {
	private UrlFileWriter file;

	public PersistedVisitedUrls(File file, UrlShortener shortener) throws IOException {
		super(shortener);

		UrlFileReader reader = new UrlFileReader(file);

		for (URL url : reader) {
			super.add(url);
		}

		reader.close();

		System.out.println("Read " + size() + " URLs from " + file + ".");

		this.file = new UrlFileWriter(file, true);
	}

	@Override
	public boolean add(URL url) {
		boolean result = super.add(url);

		if (result) {
			try {
				file.write(url);
			} catch (IOException e) {
				throw new RuntimeException("Writing to " + file + " failed.");
			}
		}

		return result;
	}

	public void close() {
		file.close();
	}
}
