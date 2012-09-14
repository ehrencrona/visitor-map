package com.velik.recommend.corpus;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import com.velik.util.FetchingIterator;

public class PhraseSet implements Serializable {
	private static final long serialVersionUID = 2449379324571178555L;

	private int size = 0;
	private int[][] phrases = new int[1000][];

	private static Comparator<int[]> comparator = new Comparator<int[]>() {
		@Override
		public int compare(int[] o1, int[] o2) {
			if (o1.length == o2.length) {
				for (int i = 0; i < o1.length; i++) {
					if (o1[i] != o2[i]) {
						return o1[i] - o2[i];
					}
				}

				return 0;
			} else {
				return o1.length - o2.length;
			}
		}
	};

	public int add(int[] phrase) {
		int i = Arrays.binarySearch(phrases, 0, size, phrase, comparator);

		if (i >= 0) {
			return i;
		}

		i = -1 - i;

		if (size == phrases.length) {
			int[][] newPhrases = new int[Math.max((int) (phrases.length * 1.3), phrases.length + 1000)][];

			System.arraycopy(phrases, 0, newPhrases, 0, phrases.length);

			phrases = newPhrases;
		}

		System.arraycopy(phrases, i, phrases, i + 1, size - i);

		phrases[i] = phrase;
		size++;

		return i;
	}

	public boolean contains(int[] phrase) {
		return indexOf(phrase) >= 0;
	}

	public int indexOf(int[] phrase) {
		return Arrays.binarySearch(phrases, 0, size, phrase, comparator);
	}

	public int size() {
		return size;
	}

	public Iterator<String> getIterator(final Corpus corpus) {
		return new FetchingIterator<String>() {
			int row = size - 1;

			@Override
			protected String fetch() {
				if (row < 0) {
					return null;
				}

				StringBuffer result = new StringBuffer(100);

				int[] indexArray = phrases[row];

				for (int col = 0; col < indexArray.length; col++) {
					if (result.length() > 0) {
						result.append(" ");
					}

					result.append(corpus.get(indexArray[col]));
				}

				row--;

				return result.toString();
			}
		};
	}
}
