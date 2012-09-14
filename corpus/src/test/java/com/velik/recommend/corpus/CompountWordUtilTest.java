package com.velik.recommend.corpus;

import org.junit.Assert;
import org.junit.Test;

import com.velik.recommend.factory.CompoundWordUtil;
import com.velik.recommend.model.Nouns;

public class CompountWordUtilTest {

	@Test
	public void test() {
		Nouns corpus = new Nouns();

		corpus.getFrequency().encountered("lkw");
		corpus.getFrequency().encountered("f�hrer");
		corpus.getFrequency().encountered("schein");

		Assert.assertTrue(new CompoundWordUtil(corpus).isCompoundWord("lkw-f�hrer"));
		Assert.assertTrue(new CompoundWordUtil(corpus).isCompoundWord("lkw-f�hrerschein"));
		Assert.assertTrue(new CompoundWordUtil(corpus).isCompoundWord("scheinf�hrer"));
		Assert.assertTrue(new CompoundWordUtil(corpus).isCompoundWord("f�hrerschein"));
		Assert.assertFalse(new CompoundWordUtil(corpus).isCompoundWord("lkw-"));
		Assert.assertFalse(new CompoundWordUtil(corpus).isCompoundWord("lkwscheiner"));
	}
}
