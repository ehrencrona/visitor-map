package com.velik.recommend.factory;

import java.util.Iterator;

import com.velik.util.FetchingIterator;

public class NaiveShorteningStemmer implements Stemmer {
	private static final int MAX_SUFFIX_LENGTH = 3;
	private static final int MIN_WORD_LENGTH = 3;

	@Override
	public Iterator<String> getPotentialStems(final String word) {
		return new FetchingIterator<String>() {
			int i = MAX_SUFFIX_LENGTH;

			@Override
			protected String fetch() {
				if (word.length() - i < MIN_WORD_LENGTH) {
					i = word.length() - MIN_WORD_LENGTH;
				}

				if (i > 0) {
					try {
						return word.substring(0, word.length() - i);
					} finally {
						i--;
					}
				}

				return null;
			}
		};
	}
}
