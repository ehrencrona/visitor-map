package com.velik.recommend.corpus;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.velik.recommend.model.TransientCorpusHolder;
import com.velik.util.FetchingIterator;

/**
 * TODO: currently does not grow with the corpus.
 */
public class SparseCorrelation extends TransientCorpusHolder implements Iterable<SingleCorrelation> {
	private static final long serialVersionUID = 1409701565878020222L;

	private static final Logger LOGGER = Logger.getLogger(SparseCorrelation.class.getName());

	private static final int GROWTH = 5;
	private int[] size;
	private int[][] correlations;
	private byte[][] frequencies;

	public SparseCorrelation(Corpus corpus) {
		super(corpus);

		correlations = new int[corpus.size()][GROWTH];
		size = new int[corpus.size()];
		frequencies = new byte[corpus.size()][GROWTH];
	}

	public void encountered(String word1, String word2) {
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

		byte[] oldFrequencies = frequencies[line];

		if (row < 0) {
			row = -row - 1;

			if (oldCorrelations.length - size[line] == 0) {
				int[] newCorrelations = new int[oldCorrelations.length + GROWTH];
				byte[] newFrequencies = new byte[oldFrequencies.length + GROWTH];

				System.arraycopy(oldCorrelations, 0, newCorrelations, 0, row);
				System.arraycopy(oldFrequencies, 0, newFrequencies, 0, row);
				System.arraycopy(oldCorrelations, row, newCorrelations, row + 1, size[line] - row);
				System.arraycopy(oldFrequencies, row, newFrequencies, row + 1, size[line] - row);

				correlations[line] = newCorrelations;
				frequencies[line] = newFrequencies;
			} else if (size[line] - row > 0) {
				System.arraycopy(oldCorrelations, row, oldCorrelations, row + 1, size[line] - row);
				System.arraycopy(oldFrequencies, row, oldFrequencies, row + 1, size[line] - row);
				oldFrequencies[row] = 0;
			}

			size[line]++;
			correlations[line][row] = wordIndex;
		}

		if (frequencies[line][row] < 127) {
			frequencies[line][row]++;
		}
	}

	public int get(String word1, String word2) {
		try {
			int i = corpus.indexOf(word1);
			int j = corpus.indexOf(word2);

			int at = indexOf(j, correlations[i], size[i]);

			if (at >= 0) {
				return frequencies[i][at];
			} else {
				return 0;
			}
		} catch (NoSuchWordException e) {
			return 0;
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
						return frequencies[line][row];
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
