package com.velik.recommend.corpus;

import java.io.Serializable;

public class Correlation implements Serializable {
	private static final long serialVersionUID = 0L;

	private int[][] frequencies;
	private transient Corpus corpus;

	private long corpusId;

	public Correlation(Corpus corpus) {
		this.corpus = corpus;
		frequencies = new int[corpus.size() + 10000][corpus.size() + 10000];
		corpusId = corpus.getId();
	}

	public void setCorpus(Corpus corpus) {
		if (corpus.getId() != corpusId) {
			throw new IncompatibleCorpusException();
		}

		this.corpus = corpus;
	}

	public void encountered(String from, String to) {
		int i = indexOf(from);
		int j = indexOf(to);

		ensureExists(i, j);

		frequencies[i][j]++;
	}

	private int indexOf(String word) {
		try {
			return corpus.indexOf(word);
		} catch (NoSuchWordException e) {
			return corpus.add(word);
		}
	}

	private void ensureExists(int i, int j) {
		if (i >= frequencies.length) {
			int[][] newFrequencies = new int[(int) (i * 1.3)][];

			System.arraycopy(frequencies, 0, newFrequencies, 0, frequencies.length);

			for (int k = frequencies.length; k < newFrequencies.length; k++) {
				newFrequencies[k] = new int[frequencies[0].length];
			}

			frequencies = newFrequencies;
		}

		if (j >= frequencies[0].length) {
			for (int k = 0; k < frequencies.length; k++) {
				int[] newEntry = new int[(int) (j * 1.3)];

				System.arraycopy(frequencies[k], 0, newEntry, 0, frequencies[k].length);

				frequencies[k] = newEntry;
			}
		}
	}

	public int get(String from, String to) {
		try {
			return frequencies[corpus.indexOf(from)][corpus.indexOf(to)];
		} catch (NoSuchWordException e) {
			return 0;
		} catch (ArrayIndexOutOfBoundsException e) {
			return 0;
		}
	}
}
