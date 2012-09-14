package com.velik.recommend.corpus;

public interface WordSet {

	int add(String word);

	boolean contains(String word);

	int indexOf(String middle) throws NoSuchWordException;

}
