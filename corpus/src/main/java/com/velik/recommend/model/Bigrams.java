package com.velik.recommend.model;

import java.io.Serializable;

import com.velik.recommend.corpus.Corpus;
import com.velik.recommend.corpus.Pairs;

public class Bigrams extends TransientCorpusHolder implements Serializable {
	private static final long serialVersionUID = 6442749980975623980L;

	private Pairs pairs;

	public Bigrams(Corpus corpus) {
		super(corpus);

		pairs = new Pairs(corpus);
	}

	@Override
	public void setCorpus(Corpus corpus) {
		if (pairs != null) {
			pairs.setCorpus(corpus);
		}
	}

	public Pairs getPairs() {
		return pairs;
	}

	public void add(String word1, String word2) {
		pairs.add(word1, word2);
	}

	public boolean contains(String word1, String word2) {
		return pairs.contains(word1, word2);
	}
}
