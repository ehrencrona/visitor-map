package com.velik.recommend.tokenizer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.velik.recommend.corpus.NoSuchWordException;

public class TokenizedText implements Iterable<Word> {
	public class TokenizedWord implements Word {

		private int index;
		private String word;
		private char precededBy;

		public TokenizedWord(int index, String word, char precededBy) {
			this.index = index;
			this.word = word;
			this.precededBy = precededBy;
		}

		@Override
		public String getWord() {
			return word;
		}

		@Override
		public Word getWord(int offset) throws NoSuchWordException {
			try {
				return words.get(index + offset);
			} catch (IndexOutOfBoundsException e) {
				throw new NoSuchWordException(e);
			}
		}

		@Override
		public char getPrecedingPunctuation() {
			return precededBy;
		}

		public String toString() {
			return word;
		}

		@Override
		public int getIndex() {
			return index;
		}
	}

	private List<Word> words = new ArrayList<Word>(200);

	void add(String wordString, char precededBy) {
		words.add(new TokenizedWord(words.size(), wordString, precededBy));
	}

	@Override
	public Iterator<Word> iterator() {
		return words.iterator();
	}

	public Word getWord(int i) throws NoSuchWordException {
		try {
			return words.get(i);
		} catch (IndexOutOfBoundsException e) {
			throw new NoSuchWordException(e);
		}
	}

	public int size() {
		return words.size();
	}

	public String getWords(int begin, int end) {
		StringBuffer result = new StringBuffer(50);

		for (int i = begin; i < end; i++) {
			if (result.length() > 0) {
				result.append(' ');
			}

			result.append(getWord(i));
		}

		return result.toString();
	}

	@Override
	public String toString() {
		return getWords(0, size());
	}
}
