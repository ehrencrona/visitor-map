package com.velik.recommend.factory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.velik.recommend.corpus.ArticleVisitor;
import com.velik.recommend.corpus.Corpus;
import com.velik.recommend.corpus.ManyToOneMapping;
import com.velik.recommend.corpus.PersonNames;
import com.velik.recommend.model.Bigrams;
import com.velik.recommend.model.NounNeighborhood;
import com.velik.recommend.model.Nouns;
import com.velik.recommend.model.ThirdPersonVerbs;
import com.velik.recommend.model.TransientCorpusHolder;
import com.velik.recommend.util.AbstractContext;

public class Context extends AbstractContext {
	private static final Logger LOGGER = Logger.getLogger(Context.class.getName());

	private Visitee articleVisitee;

	public Context(Visitee articleVisitee, boolean persist) {
		super(persist);

		this.articleVisitee = articleVisitee;
	}

	public PersonNames getPersonNames() {
		try {
			PersonNames result = (PersonNames) read("person-names");
			result.deserialized();
			return result;
		} catch (IOException e) {
			return store(new PersonNameCorpusFactory().create(this), "person-names");
		}
	}

	public Bigrams getBigrams() {
		try {
			return setCorpus((Bigrams) read("bigrams"), getNouns().getCorpus());
		} catch (IOException e) {
			return store(new BigramsFactory().create(this), "bigrams");
		}
	}

	public ThirdPersonVerbs getThirdPersonVerbs() {
		try {
			return (ThirdPersonVerbs) read("third-person-verbs");
		} catch (IOException e) {
			return store(new ThirdPersonVerbsFactory().create(this), "third-person-verbs");
		}
	}

	public ManyToOneMapping getLemmatization() {
		try {
			return setCorpus((ManyToOneMapping) read("lemmatization"), getNouns().getCorpus());
		} catch (IOException e) {
			return store(new LemmatizationFactory().create(this, new EndingListStemmer()), "lemmatization");
		}
	}

	public Nouns getNouns() {
		try {
			return (Nouns) read("nouns");
		} catch (IOException e) {
			if (!(e instanceof FileNotFoundException)) {
				LOGGER.log(Level.WARNING, "Reading nouns: " + e.getMessage(), e);
			}

			return store(new NounFactory().create(this), "nouns");
		}
	}

	public NounNeighborhood getNounNeighborhood() {
		try {
			return setCorpus((NounNeighborhood) read("noun-neighborhood"), getNouns().getCorpus());
		} catch (IOException e) {
			if (!(e instanceof FileNotFoundException)) {
				LOGGER.log(Level.WARNING, "Reading noun-neighborhood: " + e.getMessage(), e);
			}

			return store(new NounNeighborhoodFactory().create(this), "noun-neighborhood");
		}
	}

	public void visit(ArticleVisitor articleVisitor) {
		articleVisitee.visit(articleVisitor);
	}

	private <T extends TransientCorpusHolder> T setCorpus(T object, Corpus corpus) {
		object.setCorpus(corpus);

		return object;
	}

}
