package com.velik.recommend.log;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileAccessLog implements AccessLog {
	private static final Logger LOGGER = Logger.getLogger(FileAccessLog.class.getName());

	static final long FORMAT_VERSION_PREFIX = 0x47114711;
	static final int ITEM_SEPARATOR = -2;

	private static final int CURRENT_FORMAT_VERSION = 2;

	private DataOutputStream stream;

	private String fileName;

	private volatile AtomicBoolean isWriting = new AtomicBoolean();

	protected boolean append;

	public FileAccessLog(String fileNameWithoutSuffix, boolean append) {
		this.append = append;

		setFileName(fileNameWithoutSuffix);
	}

	protected FileAccessLog() {
	}

	protected void setFileName(String fileName) {
		if (fileName.equals(this.fileName)) {
			return;
		}

		if (stream != null) {
			close();
		}

		this.fileName = fileName;

		try {
			File file = new File(fileName);

			try {
				if (!file.getCanonicalFile().getParentFile().exists()) {
					LOGGER.log(Level.WARNING, "The log file directory " + file.getParentFile().getAbsolutePath()
							+ " did not exist. Creating it.");
					file.getParentFile().mkdirs();
				}
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "While checking for directory existence or while creating it (log file: "
						+ file + "): " + e, e);
			}

			boolean appending = file.exists() && append;

			stream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file, append), 16000));

			if (!appending) {
				stream.writeLong(FileAccessLog.FORMAT_VERSION_PREFIX);
				stream.writeInt(CURRENT_FORMAT_VERSION);
			}

			LOGGER.log(Level.INFO, "Initialized access log " + file.getCanonicalPath() + ".");
		} catch (IOException e) {
			LOGGER.log(Level.WARNING,
					"Could not create today's log " + fileName + ". Will stop logging: " + e.getMessage(), e);

			stream = new DataOutputStream(new NullOutputStream());
		}
	}

	@Override
	public void log(Access access) {
		// don't wait to avoid any blocking of threads.
		if (isWriting.getAndSet(true)) {
			return;
		}

		try {
			unsynchronizedLog(access);
		} finally {
			isWriting.set(false);
		}
	}

	protected void unsynchronizedLog(Access access) {
		try {
			long googleUserId = access.getUserId();
			int minorId = access.getMinorId();
			int majorId = access.getMajorId();
			long date = access.getDate();

			stream.writeLong(googleUserId);
			stream.writeByte(majorId);
			stream.writeInt(minorId);
			stream.writeLong(date);
			stream.write(ITEM_SEPARATOR);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "While writing to " + fileName + ": " + e.getMessage()
					+ ". Will give up writing log.");

			stream = new DataOutputStream(new NullOutputStream());
		}
	}

	@Override
	public void close() {
		try {
			stream.close();
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "While closing " + fileName + ":" + e.getMessage(), e);
		}
	}

	public File getFile() {
		return new File(fileName);
	}

	public String toString() {
		return fileName;
	}
}
