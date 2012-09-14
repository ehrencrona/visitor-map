package com.velik.recommend.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Persistence {

	public static void store(Object object, String fileName) throws IOException {
		ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File(fileName)));

		os.writeObject(object);

		os.close();

		System.out.println("Wrote object to " + fileName + ".");
	}

	public static Object load(String fileName) throws IOException {
		ObjectInputStream is = new ObjectInputStream(new FileInputStream(fileName));

		try {
			return is.readObject();
		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		} finally {
			is.close();
			System.out.println("Read object from " + fileName + ".");
		}
	}
}
