package com.velik.recommend.spider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;

public class SpideredHtmlFile {
	private static final int DEFAULT_MAX_SIZE = 512 * 1024 * 1024;
	private static final int SIZE_CHECK_INTERVAL = 10;

	private int maxSize = DEFAULT_MAX_SIZE;
	private int bufferSize = 1024 * 1024;

	private int timeToCheck = SIZE_CHECK_INTERVAL;

	private File file;
	private PrintWriter writer;

	private FileRotator rotator;

	public SpideredHtmlFile(File file) throws FileNotFoundException {
		rotator = new FileRotator(file);

		open();
	}

	private void open() throws FileNotFoundException {
		this.file = rotator.generateFileName();

		try {
			Writer streamWriter = new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8");

			if (bufferSize > 0) {
				streamWriter = new BufferedWriter(streamWriter, bufferSize);
			}

			writer = new PrintWriter(streamWriter);
		} catch (UnsupportedEncodingException e) {
			throw new FileNotFoundException(e.toString());
		}
	}

	public void close() {
		writer.close();
	}

	public void write(URL url, String html) {
		writer.println(url);
		writer.println(html.replace('\n', ' ').replace('\r', ' '));

		if (timeToCheck-- <= 0) {
			timeToCheck = SIZE_CHECK_INTERVAL;
			writer.flush();

			if (file.length() > maxSize) {
				close();

				rotator.next();

				try {
					open();
				} catch (FileNotFoundException e) {
					throw new RuntimeException("Cannot open rotated file " + rotator.generateFileName()
							+ ": " + e);
				}
			}
		}
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public void setBufferSize(int bufferSize) throws FileNotFoundException {
		this.bufferSize = bufferSize;

		close();
		open();
	}
}
