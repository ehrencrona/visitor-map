package com.velik.recommend.log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.velik.util.FetchingIterator;

public class AccessLogReader implements Iterable<Access> {
	private static final Logger LOGGER = Logger.getLogger(AccessLogReader.class.getName());
	private File file;

	AccessLogReader(File file) {
		this.file = file;
	}

	@SuppressWarnings("resource")
	public Iterator<Access> iterator() {
		final DataInputStream stream;

		try {
			stream = new DataInputStream(new BufferedInputStream(new FileInputStream(file), 4 * 1024 * 1024));
		} catch (FileNotFoundException e) {
			LOGGER.log(Level.WARNING, e.toString(), e);

			return Collections.<Access> emptyList().iterator();
		}

		return new FetchingIterator<Access>() {
			@Override
			protected Access fetch() {
				try {
					long userId = stream.readLong();
					int major = stream.readByte();
					int minor = stream.readInt();
					int separator = (byte) stream.read();

					if (separator != FileAccessLog.ITEM_SEPARATOR) {
						LOGGER.log(Level.WARNING, "File seems messed up. Skipping to next item separator");

						int skipped = 0;
						int read;

						do {
							read = stream.read();
							skipped++;
						} while (read != FileAccessLog.ITEM_SEPARATOR && read != -1);

						if (read == -1) {
							LOGGER.log(Level.WARNING, "File ended without another separator.");
							return null;
						}

						LOGGER.log(Level.WARNING, "Skipped " + skipped + " ints.");
					}

					return new DefaultAccess(major, minor, userId);
				} catch (EOFException e) {
					// fine. end.
				} catch (IOException e) {
					LOGGER.log(Level.WARNING, e.toString(), e);
				}

				return null;
			}
		};
	}
}
