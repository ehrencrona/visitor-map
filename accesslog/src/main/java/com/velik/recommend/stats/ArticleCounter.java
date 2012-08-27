package com.velik.recommend.stats;

public class ArticleCounter implements Comparable<ArticleCounter> {
	public int minor;

	public ArticleCounter(int minor) {
		this.minor = minor;
	}

	int count;

	@Override
	public int compareTo(ArticleCounter o) {
		return count - o.count;
	}

	public String toString() {
		return "minor " + minor + ", " + count;
	}
}