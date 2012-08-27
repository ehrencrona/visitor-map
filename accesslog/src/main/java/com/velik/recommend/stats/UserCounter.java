package com.velik.recommend.stats;

public class UserCounter implements Comparable<UserCounter> {
	private long id;

	public UserCounter(long id) {
		this.id = id;
	}

	int count;

	@Override
	public int compareTo(UserCounter o) {
		return count - o.count;
	}

	public String toString() {
		return "user " + id + ", " + count;
	}
}