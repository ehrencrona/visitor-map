package com.velik.recommend.corpus;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.velik.recommend.factory.Context;
import com.velik.recommend.factory.ParsedSpideredArticlesVisitee;
import com.velik.recommend.factory.UnparsedSpideredArticlesVisitee;

public class CorpusBuilder {

	private static final Logger LOGGER = Logger.getLogger(CorpusBuilder.class.getName());

	static int articles = 0;

	public static void main(String[] args) throws Exception {
		File directory = new File(".");

		Context context = new Context(new ParsedSpideredArticlesVisitee(new UnparsedSpideredArticlesVisitee(directory,
				100)), true);

		PersonNames pn = context.getPersonNames();
		Iterator<String> iterator = pn.full.getIterator(pn.getCorpus());

		while (iterator.hasNext()) {
			System.out.println(iterator.next());
		}

		/*
		 * context.visit(new ArticleVisitor() {
		 * 
		 * @Override public void processArticle(Article article) { articles++;
		 * 
		 * if (articles % 100000 == 0) { System.out.println(articles +
		 * " articles..."); } } });
		 * 
		 * System.out.println(articles + " articles.");
		 */
	}

	private static void checkDeclinationCoverage(Context context) {
		final Set<String> verbs = new HashSet<String>();

		ManyToOneMapping lemm = context.getLemmatization();

		int covered = 0;
		int i = 0;

		for (String word : context.getNouns().getCorpus()) {
			List<String> forms = lemm.getInverseMapping(word);

			if (!forms.isEmpty()) {
				i++;
				covered += forms.size() + 1;
			}
		}

		System.out.println("Found declinations for " + i + " words covering " + covered + " words in total");
	}

	private static void findEndings(ManyToOneMapping lemm) {
		int i = 0;
		int covered = 0;

		Corpus endings = new Corpus();
		Frequency frequencies = new Frequency(endings);
		Map<String, String> dictionaryFormByEnding = new HashMap<String, String>();

		for (String word : lemm.getCorpus()) {
			try {
				String dictionaryForm = lemm.getMapped(word);

				String ending = word.substring(dictionaryForm.length());

				frequencies.encountered(ending);

				dictionaryFormByEnding.put(ending, dictionaryForm);
			} catch (NoSuchWordException e) {
				// on.
			}
		}

		for (String ending : endings) {
			int freq = frequencies.get(ending);

			if (freq > 5) {
				System.out.println("-" + ending + ", e.g. " + dictionaryFormByEnding.get(ending) + " " + freq);
			}
		}
	}

}
