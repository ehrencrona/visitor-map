package com.velik.recommend.factory;

import com.velik.recommend.corpus.ArticleVisitor;

public interface Visitee {

	void visit(ArticleVisitor articleVisitor);

}
