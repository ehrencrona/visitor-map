package com.velik.recommend.util;

import java.nio.charset.Charset;

public class Utf8String {
	private static final Charset UTF_8 = Charset.forName("UTF-8");
	private byte[] bytes;

	public Utf8String(String string) {
		bytes = string.getBytes(UTF_8);
	}

	public boolean equals(Object o) {
		if (o instanceof Utf8String) {
			byte[] otherBytes = ((Utf8String) o).bytes;

			if (otherBytes.length != bytes.length) {
				return false;
			}

			int length = bytes.length;

			for (int i = 0; i < length; i++) {
				if (bytes[i] != otherBytes[i]) {
					return false;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int result = 1;

		int length = bytes.length;

		for (int i = 0; i < length; i++) {
			result = result * 47 + bytes[i];
		}

		return result;
	}

	public String toString() {
		return new String(bytes, UTF_8);
	}
}
