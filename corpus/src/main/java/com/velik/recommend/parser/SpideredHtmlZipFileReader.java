package com.velik.recommend.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.velik.util.FetchingIterator;

public class SpideredHtmlZipFileReader implements Iterable<SpideredDocument> {
	private static final Logger LOGGER = Logger.getLogger(SpideredHtmlZipFileReader.class.getName());

	private ZipFile file;

	public SpideredHtmlZipFileReader(ZipFile file) {
		this.file = file;
	}

	@Override
	public Iterator<SpideredDocument> iterator() {
		return new FetchingIterator<SpideredDocument>() {
			private Iterator<SpideredDocument> delegate;
			private Enumeration<? extends ZipEntry> en = file.entries();

			@Override
			protected SpideredDocument fetch() {
				if (delegate != null && delegate.hasNext()) {
					return delegate.next();
				}

				while (en.hasMoreElements()) {
					ZipEntry entry = en.nextElement();

					if (!entry.isDirectory()) {
						System.out.println(entry);
						try {
							delegate = new SpideredHtmlFileReader(new BufferedReader(new InputStreamReader(
									file.getInputStream(entry), "UTF-8"), 100000)).iterator();

							return fetch();
						} catch (UnsupportedEncodingException e) {
							// can't happen.
						} catch (IOException e) {
							LOGGER.log(Level.WARNING, "For " + entry + ": " + e, e);
							return fetch();
						}
					}
				}

				return null;
			}
		};
	}

}
