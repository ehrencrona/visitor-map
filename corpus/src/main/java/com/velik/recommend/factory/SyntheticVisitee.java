package com.velik.recommend.factory;

import java.net.URL;

import com.velik.recommend.corpus.ArticleVisitor;
import com.velik.recommend.corpus.EmptyArticle;

public class SyntheticVisitee implements Visitee {
	private String sentence;
	private int repeat;

	public SyntheticVisitee(String sentence, int repeat) {
		this.sentence = sentence;
		this.repeat = repeat;
	}

	@Override
	public void visit(ArticleVisitor articleVisitor) {
		for (int i = 0; i < repeat; i++) {
			articleVisitor.processArticle(new EmptyArticle() {

				@Override
				public String getBody() {
					return sentence;
				}

				@Override
				public URL getUrl() {
					throw new UnsupportedOperationException();
				}

			});
		}
	}

}
