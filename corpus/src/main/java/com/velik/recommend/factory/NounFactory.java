package com.velik.recommend.factory;

import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;

import java.util.Iterator;

import com.velik.recommend.corpus.Article;
import com.velik.recommend.corpus.ArticleVisitor;
import com.velik.recommend.corpus.Corpus;
import com.velik.recommend.corpus.Frequency;
import com.velik.recommend.model.Nouns;
import com.velik.recommend.tokenizer.TextTokenizer;
import com.velik.recommend.tokenizer.Word;

public class NounFactory implements ArticleVisitor {
	private Frequency frequency;
	private Frequency upperCaseFrequency;

	public Nouns create(Context context) {
		System.out.println("Getting nouns...");

		Corpus corpus = new Corpus();
		frequency = new Frequency(corpus);
		upperCaseFrequency = new Frequency(corpus);

		context.visit(this);

		int frequencyLimit = Math.max(frequency.getTotal() / 100000, 2);

		Nouns nouns = new Nouns();

		for (String word : corpus) {
			int total = frequency.get(word);
			int upperCase = upperCaseFrequency.get(word);

			if (total > frequencyLimit && upperCase > 0 && 10 * (total - upperCase) / upperCase == 0) {
				nouns.add(word, total);
			}
		}

		System.out.println("Found " + nouns.getCorpus().size() + " nouns among " + nouns.getFrequency().getTotal()
				+ " words.");

		return nouns;
	}

	@Override
	public void processArticle(Article article) {
		encounteredText(article.getTitle());
		encounteredText(article.getSubline());
		encounteredText(article.getBody());
	}

	public void encounteredText(String text) {
		Iterator<Word> wordIterator = new TextTokenizer(text, false).tokenize().iterator();

		while (wordIterator.hasNext()) {
			String word = wordIterator.next().getWord();

			boolean upperCase = isUpperCase(word.charAt(0)) && isLowerCase(word.charAt(1));
			word = word.toLowerCase();

			frequency.encountered(word);

			if (upperCase) {
				upperCaseFrequency.encountered(word);
			}
		}

	}
}
