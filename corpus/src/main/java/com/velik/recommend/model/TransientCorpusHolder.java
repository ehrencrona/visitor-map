package com.velik.recommend.model;

import java.io.Serializable;

import com.velik.recommend.corpus.Corpus;
import com.velik.recommend.corpus.IncompatibleCorpusException;

public abstract class TransientCorpusHolder implements Serializable {
	protected transient Corpus corpus;
	private long corpusId;

	public TransientCorpusHolder(Corpus corpus) {
		setCorpus(corpus);
	}

	public void setCorpus(Corpus corpus) {
		if (corpusId != 0 && corpus.getId() != corpusId) {
			throw new IncompatibleCorpusException();
		}

		corpusId = corpus.getId();

		this.corpus = corpus;
	}

	public Corpus getCorpus() {
		return corpus;
	}

}
