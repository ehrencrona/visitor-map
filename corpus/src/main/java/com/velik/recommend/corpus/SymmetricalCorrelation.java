package com.velik.recommend.corpus;

import java.io.Serializable;
import java.util.Iterator;

import com.velik.util.FetchingIterator;

public class SymmetricalCorrelation implements Serializable, Iterable<SingleCorrelation> {

	private static final double GROWTH = 1.5;

	private static final long serialVersionUID = 0L;

	private byte[][] frequencies;
	private transient Corpus corpus;

	public SymmetricalCorrelation(Corpus corpus, int reserveSize) {
		this.corpus = corpus;

		frequencies = new byte[corpus.size() + reserveSize][];

		for (int i = 0; i < frequencies.length; i++) {
			frequencies[i] = new byte[i + 1];
		}
	}

	public void setCorpus(Corpus corpus) {
		this.corpus = corpus;
	}

	public void encountered(String from, String to) {
		int i = indexOf(from);
		int j = indexOf(to);

		int h = hi(i, j);
		int l = lo(i, j);
		ensureExists(h, l);

		if (frequencies[h][l] < 127) {
			frequencies[h][l]++;
		}
	}

	private int lo(int i, int j) {
		if (i < j) {
			return i;
		} else {
			return j;
		}
	}

	private int hi(int i, int j) {
		if (i < j) {
			return j;
		} else {
			return i;
		}
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
			byte[][] newFrequencies = new byte[(byte) ((i + 1) * GROWTH)][];

			System.arraycopy(frequencies, 0, newFrequencies, 0, frequencies.length);

			for (int k = frequencies.length; k < newFrequencies.length; k++) {
				newFrequencies[k] = new byte[k + 1];
			}

			frequencies = newFrequencies;
		}
	}

	public byte get(String from, String to) {
		try {
			int i = corpus.indexOf(from);
			int j = corpus.indexOf(to);

			int h = hi(i, j);
			int l = lo(i, j);

			return frequencies[h][l];
		} catch (NoSuchWordException e) {
			return 0;
		} catch (ArrayIndexOutOfBoundsException e) {
			return 0;
		}
	}

	@Override
	public Iterator<SingleCorrelation> iterator() {
		return new FetchingIterator<SingleCorrelation>() {
			byte i = -1;
			byte j = -1;
			byte[] line;

			@Override
			protected SingleCorrelation fetch() {
				while (line != null && j < line.length - 1) {
					j++;

					if (line[j] > 0) {
						return new SingleCorrelation() {

							@Override
							public String getTo() {
								return corpus.get(j);
							}

							@Override
							public String getFrom() {
								return corpus.get(i);
							}

							@Override
							public int getFrequency() {
								return (int) line[j];
							}

							public String toString() {
								return "(" + getFrom() + "," + getTo() + "," + getFrequency() + ")";
							}

							@Override
							public int getFromIndex() {
								return i;
							}

							@Override
							public int getToIndex() {
								return j;
							}
						};
					}
				}

				if (i < frequencies.length - 1) {
					line = frequencies[++i];
					j = -1;
					return fetch();
				}

				return null;
			}

		};
	}
}
