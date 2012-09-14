package com.velik.recommend.map.ui;

public class Obfuscator {
	private static final boolean ENABLE = false;

	public static String obfuscate(String key) {
		if (!ENABLE) {
			return key;
		}

		if (key.length() < 2) {
			return "**";
		}

		return key.substring(0, 1) + stars(key.length() - 2) + key.substring(key.length() - 1);
	}

	private static StringBuffer stars(int size) {
		StringBuffer result = new StringBuffer(size);

		for (int i = 0; i < size; i++) {
			result.append("*");
		}

		return result;
	}

}
