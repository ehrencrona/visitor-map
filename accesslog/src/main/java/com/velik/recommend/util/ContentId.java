package com.velik.recommend.util;

public class ContentId {
	private int major;
	private int minor;

	public ContentId(int major, int minor) {
		this.major = major;
		this.minor = minor;
	}

	public ContentId(String contentIdString) throws InvalidContentIdException {
		if (!(contentIdString.startsWith("1.") || contentIdString.startsWith("2."))
				|| contentIdString.length() <= 2) {
			throw new InvalidContentIdException("\"" + contentIdString + "\" is not a content ID.");
		}

		try {
			major = Integer.parseInt(contentIdString.substring(0, 1));
			minor = Integer.parseInt(contentIdString.substring(2));
		} catch (NumberFormatException e) {
			throw new InvalidContentIdException("Minor of content ID \"" + contentIdString
					+ "\" was not a number.");
		}
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

}
