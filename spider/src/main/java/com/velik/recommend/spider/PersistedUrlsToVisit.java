package com.velik.recommend.spider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PersistedUrlsToVisit extends UrlsToVisit {
	private static final Logger LOGGER = Logger.getLogger(PersistedUrlsToVisit.class.getName());

	private File file;

	public PersistedUrlsToVisit(File file) throws IOException {
		this.file = file;
		UrlFileReader reader = new UrlFileReader(file);

		for (URL url : reader) {
			super.add(url);
		}

		reader.close();

		System.out.println("Read " + size() + " URLs from " + file + ".");
	}

	public boolean add(HashableUrl url) {
		return add(url.toUrl());
	}

	public void persist() {
		try {
			UrlFileWriter writer = new UrlFileWriter(file, false);

			for (URL url : this) {
				writer.write(url);
			}

			writer.close();
		} catch (FileNotFoundException e) {
			LOGGER.log(Level.WARNING, "Could not persist visited URLs to " + file + ": " + e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Could not persist visited URLs to " + file + ": " + e.getMessage(), e);
		}
	}

}
