package com.velik.recommend.corpus;

import java.io.Serializable;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.velik.recommend.model.TransientCorpusHolder;
import com.velik.util.FetchingIterator;

/**
 * TODO: currently does not grow with the corpus.
 */
public class Pairs extends TransientCorpusHolder implements Iterable<SingleCorrelation>, Serializable {
	private static final long serialVersionUID = 0;

	private static final Logger LOGGER = Logger.getLogger(Pairs.class.getName());

	private static final int GROWTH = 5;
	private int[] size;
	private int[][] correlations;

	public Pairs(Corpus corpus) {
		super(corpus);

		correlations = new int[corpus.size()][GROWTH];
		size = new int[corpus.size()];
	}

	public void add(String word1, String word2) {
		int line;
		int wordIndex;
		try {
			line = corpus.indexOf(word1);
			wordIndex = corpus.indexOf(word2);
		} catch (NoSuchWordException e) {
			LOGGER.log(Level.WARNING, "Encountered unknown word (" + word1 + " or " + word2
					+ "). Currently unsupported.");
			return;
		}

		int[] oldCorrelations = correlations[line];
		int row = indexOf(wordIndex, oldCorrelations, size[line]);

		if (row < 0) {
			row = -row - 1;

			if (oldCorrelations.length - size[line] == 0) {
				int[] newCorrelations = new int[oldCorrelations.length + GROWTH];

				System.arraycopy(oldCorrelations, 0, newCorrelations, 0, row);
				System.arraycopy(oldCorrelations, row, newCorrelations, row + 1, size[line] - row);

				correlations[line] = newCorrelations;
			} else if (size[line] - row > 0) {
				System.arraycopy(oldCorrelations, row, oldCorrelations, row + 1, size[line] - row);
			}

			size[line]++;
			correlations[line][row] = wordIndex;
		}
	}

	public boolean contains(String word1, String word2) {
		try {
			int i = corpus.indexOf(word1);
			int j = corpus.indexOf(word2);

			int at = indexOf(j, correlations[i], size[i]);

			return at >= 0;
		} catch (NoSuchWordException e) {
			return false;
		}
	}

	private int indexOf(int x, int[] a, int length) {
		int low = 0;
		int high = length - 1;
		int mid;

		while (low <= high) {
			mid = (low + high) / 2;

			if (a[mid] < x) {
				low = mid + 1;
			} else if (a[mid] > x) {
				high = mid - 1;
			} else {
				return mid;
			}
		}

		int[] shortArr = new int[length];

		System.arraycopy(a, 0, shortArr, 0, length);

		return -low - 1;
	}

	public Iterator<SingleCorrelation> iterator() {
		return new FetchingIterator<SingleCorrelation>() {
			int line = 0;
			int row = -1;

			@Override
			protected SingleCorrelation fetch() {
				row++;

				while (line < correlations.length && row >= size[line]) {
					line++;
					row = 0;
				}

				if (line >= correlations.length) {
					return null;
				}

				return new SingleCorrelation() {

					@Override
					public String getTo() {
						return corpus.get(correlations[line][row]);
					}

					@Override
					public String getFrom() {
						return corpus.get(line);
					}

					@Override
					public int getFrequency() {
						return 1;
					}

					public String toString() {
						return "(" + getFrom() + "," + getTo() + "," + getFrequency() + ")";
					}

					@Override
					public int getFromIndex() {
						return line;
					}

					@Override
					public int getToIndex() {
						return correlations[line][row];
					}
				};
			}
		};
	}
}
