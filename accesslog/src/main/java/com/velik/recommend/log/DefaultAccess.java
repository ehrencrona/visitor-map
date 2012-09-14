package com.velik.recommend.log;


public class DefaultAccess implements Access {
	private int minor;
	private int major;
	private long userId;
	private long accessId;
	private long date;

	public DefaultAccess(int major, int minor, long userId, long accessId, long date) {
		this.major = major;
		this.minor = minor;
		this.userId = userId;
		this.accessId = accessId;
		this.date = date;
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

	@Override
	public long getAccessId() {
		return accessId;
	}

	@Override
	public long getDate() {
		return date;
	}

	@Override
	public String toString() {
		return userId + " accessed " + major + "." + minor;
	}
}
