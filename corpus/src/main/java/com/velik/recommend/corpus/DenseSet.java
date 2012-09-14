package com.velik.recommend.corpus;

import java.io.Serializable;

import com.velik.recommend.model.TransientCorpusHolder;

public class DenseSet extends TransientCorpusHolder implements WordSet, Serializable {
	private boolean[] contained;
	private static final long serialVersionUID = 0L;

	public DenseSet(Corpus corpus) {
		super(corpus);

		contained = new boolean[corpus.size()];
	}

	@Override
	public boolean contains(String word) {
		try {
			return contained[corpus.indexOf(word)];
		} catch (NoSuchWordException e) {
			return false;
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}

	@Override
	public int add(String word) {
		int index = corpus.add(word);

		if (contained.length <= index) {
			boolean[] newContained = new boolean[(int) Math.max(corpus.size() + 10000, corpus.size() * 1.3)];

			System.arraycopy(contained, 0, newContained, 0, contained.length);

			contained = newContained;
		}

		contained[index] = true;

		return index;
	}

	@Override
	public int indexOf(String word) throws NoSuchWordException {
		int i = corpus.indexOf(word);

		if (i >= contained.length || !contained[i]) {
			throw new NoSuchWordException();
		}

		return i;
	}

	public void setCorpus(Corpus corpus) {
		this.corpus = corpus;
	}
}
