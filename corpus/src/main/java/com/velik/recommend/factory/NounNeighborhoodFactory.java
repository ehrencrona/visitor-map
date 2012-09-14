package com.velik.recommend.factory;

import java.util.Iterator;

import com.velik.recommend.corpus.Article;
import com.velik.recommend.corpus.ArticleVisitor;
import com.velik.recommend.model.NounNeighborhood;
import com.velik.recommend.model.Nouns;
import com.velik.recommend.tokenizer.TextTokenizer;
import com.velik.recommend.tokenizer.Word;

public class NounNeighborhoodFactory implements ArticleVisitor {
	public static final int NEIGHBORHOOD_LENGTH = 30;

	private NounNeighborhood neighborhood;

	private Nouns nouns;

	public NounNeighborhood create(Context context) {
		nouns = context.getNouns();

		System.out.println("Getting neighborhood...");

		neighborhood = new NounNeighborhood(nouns.getCorpus());

		context.visit(this);

		return neighborhood;
	}

	@Override
	public void processArticle(Article article) {
		encounteredText(article.getTitle());
		encounteredText(article.getSubline());
		encounteredText(article.getBody());
	}

	public void encounteredText(String text) {
		String[] recent = new String[NEIGHBORHOOD_LENGTH];
		int at = 0;

		Iterator<Word> wordIterator = new TextTokenizer(text, true).tokenize().iterator();

		while (wordIterator.hasNext()) {
			String word = wordIterator.next().getWord();

			if (!nouns.contains(word)) {
				continue;
			}

			for (String foundWord : recent) {
				if (foundWord != null) {
					neighborhood.encountered(foundWord, word);
				}
			}

			recent[at++] = word;

			if (at == NEIGHBORHOOD_LENGTH) {
				at = 0;
			}
		}
	}
}
