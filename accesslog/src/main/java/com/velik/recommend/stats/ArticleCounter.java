package com.velik.recommend.stats;

import java.io.Serializable;

public class ArticleCounter implements Counter<ArticleCounter>, Serializable {
	private static final long serialVersionUID = -8514774304529034787L;

	public int minor;
	int count;

	public ArticleCounter(int minor) {
		this.minor = minor;
	}

	public ArticleCounter(int minor, int count) {
		this(minor);

		this.count = count;
	}

	@Override
	public int compareTo(ArticleCounter o) {
		return count - o.count;
	}

	public String toString() {
		return "minor " + minor + ", " + count;
	}

	public int getCount() {
		return count;
	}
}