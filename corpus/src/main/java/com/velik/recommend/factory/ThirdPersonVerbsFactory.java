package com.velik.recommend.factory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.velik.recommend.corpus.Article;
import com.velik.recommend.corpus.ArticleVisitor;
import com.velik.recommend.corpus.WordSet;
import com.velik.recommend.model.ThirdPersonVerbs;
import com.velik.recommend.tokenizer.TextTokenizer;
import com.velik.recommend.tokenizer.Word;

public class ThirdPersonVerbsFactory {

	public ThirdPersonVerbs create(Context context) {
		final ThirdPersonVerbs result = new ThirdPersonVerbs();

		final WordSet familyNames = context.getPersonNames().lasts;

		context.visit(new ArticleVisitor() {

			@Override
			public void processArticle(Article article) {
				boolean mightBeVerb = false;

				TextTokenizer tokenizer = new TextTokenizer(article.getBody(), false);
				tokenizer.setLengthLimit(2);

				Iterator<Word> it = tokenizer.tokenize().iterator();

				List<String> last = new ArrayList<String>();

				while (it.hasNext()) {
					String word = it.next().getWord();

					if (word.equals("sich") && !last.isEmpty() && isPotentialVerb(last.get(last.size() - 1))) {
						result.getFrequency().encountered(last.get(last.size() - 1));
					}

					if (mightBeVerb && isPotentialVerb(word)) {
						result.getFrequency().encountered(word);
					}

					last.add(word);

					if (last.size() > 5) {
						last.remove(0);
					}

					mightBeVerb = word.equals("Er") || word.equals("Es") || familyNames.contains(word);
				}

			}

			private boolean isPotentialVerb(String word) {
				return Character.isLowerCase(word.charAt(0)) && word.endsWith("t");
			}
		});

		return result;
	}
}
