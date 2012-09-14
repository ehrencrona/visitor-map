package com.velik.recommend.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.velik.util.Factory;

public class AbstractContext {
	private static final Logger LOGGER = Logger.getLogger(AbstractContext.class.getName());

	private Map<String, Object> cache = new HashMap<String, Object>();
	protected boolean persist;

	public AbstractContext(boolean persist) {
		this.persist = persist;
	}

	protected synchronized <T> T readOrCreate(String fileName, Factory<T> factory) {
		try {
			return (T) read(fileName);
		} catch (FileNotFoundException e) {
			LOGGER.log(Level.WARNING, fileName + " did not exist. Calling " + factory.getClass().getSimpleName());

			return store(factory.create(), fileName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected Object read(String filename) throws IOException {
		if (cache.containsKey(filename)) {
			return cache.get(filename);
		}

		if (persist) {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(filename)));

			try {
				System.out.println("Reading from file " + filename + "...");

				Object result = ois.readObject();

				System.out.println("Done.");

				cache.put(filename, result);

				return result;
			} catch (IOException e) {
				if (!(e instanceof FileNotFoundException)) {
					LOGGER.log(Level.WARNING, "Reading " + filename + ": " + e.getMessage(), e);
				}

				throw e;
			} catch (ClassNotFoundException e) {
				LOGGER.log(Level.WARNING, "Reading " + filename + ": " + e.getMessage(), e);

				throw new IOException(e);
			} finally {
				ois.close();
			}
		} else {
			throw new IOException(filename + " is not in the cache.");
		}
	}

	protected <T> T store(T object, String filename) {
		cache.put(filename, object);

		if (persist) {
			System.out.println("Storing " + filename + "...");
			File file = new File(filename);

			try {
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));

				oos.writeObject(object);

				oos.close();
			} catch (IOException e) {
				LOGGER.log(Level.WARNING, "While writing to " + filename + ": " + e.getMessage(), e);
			}

			System.out.println("Done. " + file.length() + " bytes.");
		}

		return object;
	}

}
