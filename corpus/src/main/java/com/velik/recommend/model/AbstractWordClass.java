package com.velik.recommend.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import com.velik.recommend.corpus.Corpus;
import com.velik.recommend.corpus.Frequency;

public abstract class AbstractWordClass implements Serializable {
	private static final long serialVersionUID = -5664918822323158168L;

	protected Frequency frequency;
	protected Corpus corpus;

	public AbstractWordClass() {
		this.corpus = new Corpus();
		this.frequency = new Frequency(corpus);
	}

	public Corpus getCorpus() {
		return corpus;
	}

	public Frequency getFrequency() {
		return frequency;
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		frequency.setCorpus(corpus);
	}
}
