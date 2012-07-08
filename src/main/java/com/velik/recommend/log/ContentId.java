package com.velik.recommend.log;

public class ContentId {
	private int major;
	private int minor;

	public ContentId(int major, int minor) {
		this.major = major;
		this.minor = minor;
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

}
