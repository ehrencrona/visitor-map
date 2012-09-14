package com.velik.recommend.corpus;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

public class SymmetricalCorrelationTest {

	@Test
	public void testEmpty() {
		SymmetricalCorrelation sc = new SymmetricalCorrelation(new Corpus(), 0);

		Assert.assertFalse(sc.iterator().hasNext());

		sc.encountered("fail", "fast");

		Assert.assertTrue(sc.iterator().hasNext());
	}

	@Test
	public void test() {
		SymmetricalCorrelation sc = new SymmetricalCorrelation(new Corpus(), 0);

		sc.encountered("fail", "fast");

		Assert.assertEquals(1, sc.get("fast", "fail"));
		Assert.assertEquals(1, sc.get("fail", "fast"));
		Assert.assertEquals(0, sc.get("fail", "fail"));

		Iterator<SingleCorrelation> it = sc.iterator();

		Assert.assertEquals("fast", it.next().getFrom());
		Assert.assertFalse(it.hasNext());
	}
}
