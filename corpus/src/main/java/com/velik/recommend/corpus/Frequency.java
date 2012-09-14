package com.velik.recommend.corpus;

import java.io.Serializable;

import com.velik.recommend.model.TransientCorpusHolder;

public class Frequency extends TransientCorpusHolder implements Serializable {
	private static final long serialVersionUID = 2848131112441981645L;

	protected int[] frequencies;
	private int total;

	public Frequency(Corpus corpus) {
		super(corpus);

		frequencies = new int[corpus.size() + 10000];
	}

	public int encountered(String word) {
		int i = indexOf(word);

		ensureExists(i);

		frequencies[i]++;

		return i;
	}

	protected int indexOf(String word) {
		try {
			return corpus.indexOf(word);
		} catch (NoSuchWordException e) {
			return corpus.add(word);
		}
	}

	private void ensureExists(int i) {
		if (i >= frequencies.length) {
			expand(i);
		}
	}

	protected void expand(int i) {
		int[] newFrequencies = new int[(int) (i * 1.3)];

		System.arraycopy(frequencies, 0, newFrequencies, 0, frequencies.length);

		frequencies = newFrequencies;
	}

	public int get(String word) {
		try {
			return frequencies[corpus.indexOf(word)];
		} catch (NoSuchWordException e) {
			return 0;
		} catch (ArrayIndexOutOfBoundsException e) {
			return 0;
		}
	}

	public void add(int i, int frequency) {
		ensureExists(i);

		frequencies[i] += frequency;
		total += frequency;
	}

	public void add(String word, int frequency) {
		add(indexOf(word), frequency);
	}

	public void set(int i, int frequency) {
		ensureExists(i);

		total -= frequencies[i];
		frequencies[i] = frequency;
		total += frequency;
	}

	public void set(String word, int frequency) {
		set(indexOf(word), frequency);
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int get(int wordIndex) {
		try {
			return frequencies[wordIndex];
		} catch (ArrayIndexOutOfBoundsException e) {
			return 0;
		}
	}

	public void print(int minimumFrequency) {
		for (String word : getCorpus()) {
			int freq = get(word);

			if (freq > minimumFrequency) {
				System.out.println(word + " (" + freq + ")");
			}
		}
	}

	public boolean contains(String word) {
		return corpus.contains(word);
	}
}
