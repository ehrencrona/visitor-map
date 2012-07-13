package com.velik.recommend.spider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.velik.util.FetchingIterator;

public class UrlFileReader implements Iterable<URL> {
	private static final Logger LOGGER = Logger.getLogger(UrlFileReader.class.getName());
	private BufferedReader reader;
	private File file;

	public UrlFileReader(File file) throws IOException {
		this.file = file;

		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		} catch (FileNotFoundException e) {
			// fine.
		}
	}

	@Override
	public Iterator<URL> iterator() {
		if (reader == null) {
			return Collections.<URL> emptyList().iterator();
		}

		return new FetchingIterator<URL>() {
			@Override
			protected URL fetch() {

				try {
					String line = reader.readLine();

					if (line == null) {
						try {
							reader.close();
						} catch (IOException e) {
						}

						return null;
					}

					return new URL(line);
				} catch (MalformedURLException e) {
					LOGGER.log(Level.WARNING, "In file " + file + ": " + e, e);

					return fetch();
				} catch (IOException e) {
					LOGGER.log(Level.WARNING, "In file " + file + ": " + e, e);
					return null;
				}

			}
		};
	}

	public void close() {
		try {
			reader.close();
		} catch (IOException e) {
		}
	}
}
