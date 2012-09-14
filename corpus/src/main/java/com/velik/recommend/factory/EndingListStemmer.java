package com.velik.recommend.factory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EndingListStemmer implements Stemmer {

	public static class Replacement {
		public String replace;
		public String with;
	}

	private static String[] ENDINGS = new String[] { "s", "er", "en", "n", "in", "e", "r", "es", "ern", "a", "ner",
			"ist", "innen", "ung", "se", "ler", "ses" };

	private static Replacement[] REPLACEMENTS = new Replacement[] {
	/* italian words */
	replacement("o", "i"), replacement("a", "e"),
	/* lating words */
	replacement("us", "i"), replacement("um", "a") };

	private static Replacement replacement(String replace, String with) {
		Replacement result = new Replacement();
		result.replace = replace;
		result.with = with;

		return result;
	}

	@Override
	public Iterator<String> getPotentialStems(String word) {
		List<String> result = new ArrayList<String>();

		for (String ending : ENDINGS) {
			if (word.endsWith(ending)) {
				result.add(word.substring(0, word.length() - ending.length()));
			}
		}

		for (Replacement replace : REPLACEMENTS) {
			if (word.endsWith(replace.with)) {
				result.add(word.substring(0, word.length() - replace.with.length()) + replace.replace);
			}
		}

		return result.iterator();
	}

}
