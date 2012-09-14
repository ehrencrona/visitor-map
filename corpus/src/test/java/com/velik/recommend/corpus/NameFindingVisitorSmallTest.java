package com.velik.recommend.corpus;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.velik.recommend.model.Nouns;

public class NameFindingVisitorSmallTest extends AbstractNameFindingTest {
	private NameFindingVisitor visitor;

	@Before
	public void setUp() {
		Nouns nouns = new Nouns();

		nouns.add("bundeskanzlerin", 1);
		nouns.add("angela", 1);
		nouns.add("merkel", 1);
		nouns.add("bauer", 11);

		nouns.getFrequency().setTotal(10000);

		ManyToOneMapping lemma = new ManyToOneMapping(nouns.getCorpus());

		PersonNames names = new PersonNames();
		names.firsts.add("Angela");
		names.firsts.add("Otto");
		names.firsts.add("Charles");
		names.lasts.add("Merkel");
		names.lasts.add("Bauer");
		names.titles.add("de");

		visitor = new NameFindingVisitor(nouns, names);
	}

	@Test
	public void testName() {
		visitor.encounteredText("Bundeskanzlerin Angela Merkel sagt: ich bin Angela.");

		assertEquals(visitor.getPotentialNames(), "Angela Merkel", "Bundeskanzlerin Angela Merkel");

		System.out.println(visitor.getPotentialNames());

		visitor.articleEnded();

		assertEquals(visitor.getPotentialNames(), "Angela Merkel");
	}

	@Test
	public void testSingleOccurrence() {
		visitor.encounteredText("Angela Merkel sagt jede Menge Sachen.");

		assertEquals(visitor.getPotentialNames(), "Angela Merkel");

		visitor.articleEnded();

		assertEquals(visitor.getPotentialNames(), "Angela Merkel");
	}

	@Test
	public void testCommonWord() {
		visitor.encounteredText("Das ist Otto Bauer.");

		assertEquals(visitor.getPotentialNames(), "Otto Bauer");

		visitor.articleEnded();

		Assert.assertEquals(0, visitor.getPotentialNames().size());

		visitor.articleStarted();

		visitor.encounteredText("Der Otto Bauer ist kein Bauer; Otto Bauer ist Anwalt.");

		assertEquals(visitor.getPotentialNames(), "Otto Bauer");

		visitor.articleEnded();

		assertEquals(visitor.getPotentialNames(), "Otto Bauer");
	}

	@Test
	public void testLongNames() {
		visitor.encounteredText("Charles de Gaulle hat Frankreich regiert. Jetzt heisst der Flughafen de Gaulle. Niemand nennt ihn Charles.");

		assertEquals(visitor.getPotentialNames(), "Charles de Gaulle");

		visitor.articleEnded();

		assertEquals(visitor.getPotentialNames(), "Charles de Gaulle");
	}
}
