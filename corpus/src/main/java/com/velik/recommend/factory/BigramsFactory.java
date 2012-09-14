package com.velik.recommend.factory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.velik.recommend.corpus.Article;
import com.velik.recommend.corpus.ArticleVisitor;
import com.velik.recommend.corpus.ManyToOneMapping;
import com.velik.recommend.corpus.NoSuchWordException;
import com.velik.recommend.corpus.SingleCorrelation;
import com.velik.recommend.corpus.SparseCorrelation;
import com.velik.recommend.model.Bigrams;
import com.velik.recommend.model.Nouns;
import com.velik.recommend.tokenizer.TextTokenizer;
import com.velik.recommend.tokenizer.Word;

public class BigramsFactory {
	private static Set<String> inNames = new HashSet<String>(Arrays.asList("von", "van", "de"));
	private int frequencyLimit = 10;

	public Bigrams create(Context context) {
		final Nouns nouns = context.getNouns();

		final SparseCorrelation previousToNext = new SparseCorrelation(nouns.getCorpus());

		final ManyToOneMapping lemma = context.getLemmatization();

		final CompoundWordUtil compoundWords = new CompoundWordUtil(nouns);

		context.visit(new ArticleVisitor() {

			@Override
			public void processArticle(Article article) {
				encounteredText(article.getTitle());
				encounteredText(article.getSubline());
				encounteredText(article.getBody());
			}

			public void encounteredText(String text) {
				String last = null;
				int at = 0;

				Iterator<Word> wordIterator = new TextTokenizer(text, true).tokenize().iterator();

				while (wordIterator.hasNext()) {
					String word = wordIterator.next().getWord();

					if (!nouns.contains(word)) {
						if (!inNames.contains(word)) {
							last = null;
						}

						continue;
					}

					if (last != null) {
						try {
							word = lemma.getMapped(word);
						} catch (NoSuchWordException e) {
							// fine
						}

						previousToNext.encountered(last, word);
					}

					last = word;
				}
			}

		});

		int found = 0;

		Bigrams result = new Bigrams(nouns.getCorpus());

		for (SingleCorrelation sc : previousToNext) {
			if (sc.getFrequency() > frequencyLimit) {
				result.add(sc.getFrom(), sc.getTo());
				found++;
			}
		}

		System.out.println("Found " + found + " bigrams.");

		return result;
	}
}
