package com.velik.recommend.corpus;

import java.io.Serializable;

public class PersonNames implements Serializable {
	private static final long serialVersionUID = 0L;

	private Corpus corpus = new Corpus();

	public DenseSet firsts = new DenseSet(corpus);
	public DenseSet lasts = new DenseSet(corpus);
	public DenseSet middles = new DenseSet(corpus);
	public DenseSet titles = new DenseSet(corpus);

	public PhraseSet full = new PhraseSet();

	public Corpus epithets = new Corpus();

	public PersonNames() {
	}

	public Corpus getCorpus() {
		return corpus;
	}

	public void deserialized() {
		firsts.setCorpus(corpus);
		lasts.setCorpus(corpus);
		middles.setCorpus(corpus);
		titles.setCorpus(corpus);
	}
}
