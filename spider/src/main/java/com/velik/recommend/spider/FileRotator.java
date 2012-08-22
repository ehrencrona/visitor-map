package com.velik.recommend.spider;

import java.io.File;

public class FileRotator {
	private static final int INDEX_LENGTH = 3;
	private String baseFile;
	private String fileSuffix;
	private int fileIndex;

	public FileRotator(File file, boolean goToNextFree) {
		baseFile = file.getName();
		int i = baseFile.lastIndexOf('.');

		if (i != -1) {
			fileSuffix = baseFile.substring(i);
			baseFile = baseFile.substring(0, i);
		} else {
			fileSuffix = "";
		}

		fileIndex = 1;

		if (goToNextFree) {
			while (fileExists()) {
				fileIndex++;
			}
		}

		System.out.println("Next file index is " + fileIndex + ".");
	}

	boolean fileExists() {
		return generateFileName().exists() || generateFileName(".zip").exists();
	}

	private File generateFileName(String otherFileSuffix) {
		return new File(baseFile + "." + pad(fileIndex) + otherFileSuffix);
	}

	public File generateFileName() {
		return generateFileName(fileSuffix);
	}

	private String pad(int integer) {
		String result = Integer.toString(integer);

		while (result.length() < INDEX_LENGTH) {
			result = "0" + result;
		}

		return result;
	}

	public void next() {
		fileIndex++;
	}

}
