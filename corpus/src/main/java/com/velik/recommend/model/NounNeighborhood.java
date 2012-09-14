package com.velik.recommend.model;

import com.velik.recommend.corpus.Corpus;
import com.velik.recommend.corpus.SparseCorrelation;
import com.velik.recommend.corpus.SparseSymmetricalCorrelation;

public class NounNeighborhood extends TransientCorpusHolder {
	private SparseSymmetricalCorrelation correlation;

	public NounNeighborhood(Corpus words) {
		super(words);
	}

	@Override
	public void setCorpus(Corpus corpus) {
		super.setCorpus(corpus);

		if (correlation == null) {
			correlation = new SparseSymmetricalCorrelation(corpus);
		} else {
			correlation.setCorpus(corpus);
		}
	}

	public void encountered(String word1, String word2) {
		correlation.encountered(word1, word2);
	}

	public int get(String word1, String word2) {
		return correlation.get(word1, word2);
	}

	public SparseCorrelation getCorrelations() {
		return correlation;
	}

}
