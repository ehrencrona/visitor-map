package com.velik.recommend.factory;

import java.util.ArrayList;

import com.velik.recommend.model.Nouns;

public class CompoundWordUtil {
	protected static final int MIN_SUBWORD_LENGTH = 3;
	protected static final int MIN_FREQUENCY = 10;

	private Nouns nouns;

	public CompoundWordUtil(Nouns nouns) {
		this.nouns = nouns;
	}

	public boolean isCompoundWord(String word) {
		ArrayList<String> components = getComponents(word);

		return components.size() > count('-', word) + 1;
	}

	private int count(char c, String word) {
		int result = 0;

		for (int i = 0; i < word.length(); i++) {
			if (word.charAt(i) == c) {
				result++;
			}
		}

		return result;
	}

	public ArrayList<String> getComponents(String word) {
		ArrayList<String> parts = new ArrayList<String>();

		String[] subwords = word.split("-");

		nextSubword: for (String subword : subwords) {
			if (subword.length() > MIN_SUBWORD_LENGTH * 2) {
				for (int i = MIN_SUBWORD_LENGTH; i <= subword.length() - MIN_SUBWORD_LENGTH; i++) {
					String word1 = subword.substring(0, i);
					String word2 = subword.substring(i);

					if (isPotentialComponent(word2)) {
						if (isPotentialComponent(word1)) {
							parts.add(word1);
							parts.add(word2);
							continue nextSubword;
						}

						if (word1.endsWith("s")) {
							if (isPotentialComponent(word1.substring(0, word1.length() - 1))) {
								parts.add(word1.substring(0, word1.length() - 1));
								parts.add(word2);

								continue nextSubword;
							}
						}

						if (word1.endsWith("en")) {
							if (isPotentialComponent(word1.substring(0, word1.length() - 2))) {
								parts.add(word1.substring(0, word1.length() - 2));
								parts.add(word2);

								continue nextSubword;
							}
						}
					}

				}
			}

			parts.add(subword);
		}

		return parts;
	}

	private boolean isPotentialComponent(String string) {
		return nouns.getFrequency(string) > MIN_FREQUENCY;
	}
}
