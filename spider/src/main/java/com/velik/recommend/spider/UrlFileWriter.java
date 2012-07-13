package com.velik.recommend.spider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UrlFileWriter {
	private static final Logger LOGGER = Logger.getLogger(UrlFileWriter.class.getName());
	private BufferedWriter writer;
	private File file;

	public UrlFileWriter(File file, boolean append) throws FileNotFoundException {
		this.file = file;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), "UTF-8"),
					1000 * 1000);
		} catch (UnsupportedEncodingException e) {
			throw new FileNotFoundException(e.toString());
		}
	}

	public void write(URL url) throws IOException {
		writer.write(url.toString());
		writer.write('\n');
	}

	public void close() {
		try {
			writer.close();
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, e.getMessage(), e);
		}
	}

	public String toString() {
		return file.toString();
	}
}
