package com.velik.recommend.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.velik.util.FetchingIterator;

public class SpideredHtmlFileReader {
	private static final Logger LOGGER = Logger.getLogger(SpideredHtmlFileReader.class.getName());

	private BufferedReader reader;

	public SpideredHtmlFileReader(BufferedReader reader) {
		this.reader = reader;
	}

	public Iterator<SpideredDocument> iterator() {
		return new FetchingIterator<SpideredDocument>() {

			@Override
			protected SpideredDocument fetch() {
				try {
					String line = reader.readLine();

					if (line == null) {
						return null;
					}

					final URL url;

					try {
						url = new URL(line);
					} catch (MalformedURLException e) {
						if (line.length() > 100) {
							line = line.substring(0, 100);
						}

						LOGGER.log(Level.WARNING, "For " + line + ": " + e, e);

						// probably out of synch with the lines.
						return fetch();
					}

					final String html = reader.readLine();

					return new SpideredDocument() {
						@Override
						public URL getUrl() {
							return url;
						}

						@Override
						public String getHtml() {
							return html;
						}
					};
				} catch (IOException e) {
					LOGGER.log(Level.WARNING, e.getMessage(), e);

					try {
						reader.close();
					} catch (IOException closeE) {
						LOGGER.log(Level.WARNING, closeE.toString(), closeE);
					}

					return null;
				}
			}

		};

	}
}
