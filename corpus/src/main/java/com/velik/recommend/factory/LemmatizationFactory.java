package com.velik.recommend.factory;

import java.util.Iterator;

import com.velik.recommend.corpus.Corpus;
import com.velik.recommend.corpus.ManyToOneMapping;
import com.velik.recommend.model.NounNeighborhood;
import com.velik.recommend.model.Nouns;

public class LemmatizationFactory {

	public ManyToOneMapping create(Context context, Stemmer stemmer) {
		NounNeighborhood neighborhood = context.getNounNeighborhood();
		Nouns nouns = context.getNouns();

		System.out.println("Generating lemmatization...");

		Corpus endings = new Corpus();

		ManyToOneMapping result = new ManyToOneMapping(nouns.getCorpus());

		for (String word : nouns.getCorpus()) {
			Iterator<String> it = stemmer.getPotentialStems(word);

			while (it.hasNext()) {
				String potentialStem = it.next();

				if (!nouns.contains(potentialStem)) {
					potentialStem = removeUmlaute(potentialStem);
				}

				if (nouns.contains(potentialStem)) {
					int f1 = nouns.getFrequency(potentialStem);
					int f2 = nouns.getFrequency(word);

					int correlation = neighborhood.get(potentialStem, word);

					int expectedCorrelation = f1 * NounNeighborhoodFactory.NEIGHBORHOOD_LENGTH * f2
							/ nouns.getFrequency().getTotal();
					int correlationLimit = Math.max(2, 3 * expectedCorrelation);

					if (f1 == 0 || f2 == 0 || f2 / f1 > 20 || f1 / f2 > 20 || correlation < correlationLimit) {
						System.out.println("no  " + word + " " + potentialStem + " corr " + correlation + " freq " + f2
								+ " " + f1);

						continue;
					}

					System.out.println("yes " + word + " " + potentialStem + " corr " + correlation + " freq " + f1
							+ " " + f2);

					result.put(word, potentialStem);
				}
			}
		}

		return result;
	}

	private String removeUmlaute(String string) {
		StringBuffer result = new StringBuffer(string.length());

		for (int i = 0; i < string.length(); i++) {
			char ch = string.charAt(i);

			if (ch == 'Š') {
				ch = 'a';
			} else if (ch == 'š') {
				ch = 'o';
			} else if (ch == 'Ÿ') {
				ch = 'u';
			}

			result.append(ch);
		}

		return result.toString();
	}

}
