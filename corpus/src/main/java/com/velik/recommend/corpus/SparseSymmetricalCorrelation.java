package com.velik.recommend.corpus;

import java.io.Serializable;

public class SparseSymmetricalCorrelation extends SparseCorrelation implements Serializable {

	public SparseSymmetricalCorrelation(Corpus corpus) {
		super(corpus);
	}

	@Override
	public void encountered(String word1, String word2) {
		if (word1.compareTo(word2) > 0) {
			String tmp = word1;

			word1 = word2;
			word2 = tmp;
		}

		super.encountered(word1, word2);
	}

	@Override
	public int get(String word1, String word2) {
		if (word1.compareTo(word2) > 0) {
			String tmp = word1;

			word1 = word2;
			word2 = tmp;
		}

		return super.get(word1, word2);
	}

}
