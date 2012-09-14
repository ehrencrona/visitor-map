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

import com.velik.recommend.util.FetchingIterator;

public class AccessLogReader implements Iterable<Access> {
	private static final Logger LOGGER = Logger.getLogger(AccessLogReader.class.getName());
	private File file;
	private long initialAccessId;

	public AccessLogReader(File file, long initialAccessId) {
		this.file = file;
		this.initialAccessId = initialAccessId;
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

		int nonFinalFormatVersion = 1;

		try {
			long potentialFormatPrefix = stream.readLong();

			if (potentialFormatPrefix == FileAccessLog.FORMAT_VERSION_PREFIX) {
				nonFinalFormatVersion = stream.readInt();
			} else {
				stream.reset();
			}
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "While reading header of " + file + ": " + e.getMessage(), e);
		}

		final int formatVersion = nonFinalFormatVersion;

		return new FetchingIterator<Access>() {
			long accessId = initialAccessId;

			@Override
			protected Access fetch() {
				try {
					long userId = stream.readLong();
					int major = stream.readByte();
					int minor = stream.readInt();

					long date = -1;

					if (formatVersion > 1) {
						date = stream.readLong();
					}

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

					return new DefaultAccess(major, minor, userId, accessId++, date);
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
