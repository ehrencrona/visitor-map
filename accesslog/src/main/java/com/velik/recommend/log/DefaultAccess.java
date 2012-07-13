package com.velik.recommend.log;

public class DefaultAccess implements Access {
	private int minor;
	private int major;
	private long userId;

	public DefaultAccess(int major, int minor, long userId) {
		this.major = major;
		this.minor = minor;
		this.userId = userId;
	}

	@Override
	public int getMajorId() {
		return major;
	}

	@Override
	public int getMinorId() {
		return minor;
	}

	@Override
	public long getUserId() {
		return userId;
	}

}
