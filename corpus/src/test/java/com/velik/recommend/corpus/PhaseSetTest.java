package com.velik.recommend.corpus;

import org.junit.Assert;
import org.junit.Test;

public class PhaseSetTest {
	@Test
	public void test() {
		PhraseSet ps = new PhraseSet();

		Assert.assertEquals(0, ps.add(new int[] { 1, 2 }));
		Assert.assertEquals(1, ps.add(new int[] { 1, 3 }));
		Assert.assertEquals(2, ps.add(new int[] { 1, 2, 3 }));
		Assert.assertEquals(0, ps.add(new int[] { 1 }));

		Assert.assertEquals(0, ps.indexOf(new int[] { 1 }));
		Assert.assertEquals(2, ps.indexOf(new int[] { 1, 3 }));
		Assert.assertEquals(-1, ps.indexOf(new int[] {}));
	}
}
