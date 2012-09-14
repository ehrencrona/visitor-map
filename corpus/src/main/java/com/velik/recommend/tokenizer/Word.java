package com.velik.recommend.tokenizer;

import com.velik.recommend.corpus.NoSuchWordException;

public interface Word {
	String getWord();

	Word getWord(int offset) throws NoSuchWordException;

	char getPrecedingPunctuation();

	int getIndex();
}
