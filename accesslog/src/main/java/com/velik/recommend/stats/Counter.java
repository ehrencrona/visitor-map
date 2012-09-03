package com.velik.recommend.stats;

public interface Counter<T> extends Comparable<T> {
	int getCount();
}
