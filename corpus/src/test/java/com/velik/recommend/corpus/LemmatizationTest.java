package com.velik.recommend.corpus;

import org.junit.Assert;
import org.junit.Test;

import com.velik.recommend.factory.Context;
import com.velik.recommend.factory.SyntheticVisitee;

public class LemmatizationTest {

	@Test
	public void test() throws Exception {
		Context ctx = new Context(new SyntheticVisitee("Dies ist ein deutsches Wort und dies sind deutsche Wšrter.",
				100), false);

		Corpus nouns = ctx.getNouns().getCorpus();

		Assert.assertTrue(nouns.contains("wort"));
		Assert.assertTrue(nouns.contains("wšrter"));

		ManyToOneMapping lemma = ctx.getLemmatization();

		Assert.assertEquals("wort", lemma.getMapped("wšrter"));
	}

}
