package com.velik.recommend.stats;

public class UserCounter implements Counter<UserCounter> {
	private static final long serialVersionUID = 0L;

	private long id;
	public int count;

	public UserCounter(long id) {
		this.id = id;
	}

	public UserCounter(long id, int count) {
		this(id);

		this.count = count;
	}

	@Override
	public int compareTo(UserCounter o) {
		return count - o.count;
	}

	public String toString() {
		return "user " + id + ", " + count;
	}

	@Override
	public int getCount() {
		return count;
	}
}