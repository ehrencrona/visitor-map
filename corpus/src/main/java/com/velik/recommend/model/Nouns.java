package com.velik.recommend.model;

import java.io.Serializable;

import com.velik.recommend.corpus.Corpus;

public class Nouns extends AbstractWordClass implements Serializable {

	public Nouns() {
		super();
	}

	public void add(String word, int total) {
		frequency.set(corpus.add(word), total);
	}

	public boolean contains(String word) {
		return corpus.contains(word);
	}

	public Corpus getCorpus() {
		return corpus;
	}

	public int getFrequency(String word) {
		return frequency.get(word);
	}

}
