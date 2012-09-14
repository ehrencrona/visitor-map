package com.velik.recommend.corpus;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SparseSymmetricalCorrelationTest {

	private SparseSymmetricalCorrelation sc;
	private Corpus corpus;

	@Before
	public void setUp() {
		corpus = new Corpus();
		corpus.add("fail");
		corpus.add("fast");
		corpus.add("slow");

		sc = new SparseSymmetricalCorrelation(corpus);
	}

	@Test
	public void testAllCorrelate() {
		corpus = new Corpus();

		// has to be even.
		int size = 2 * 14;

		// make order irregular
		for (int i = 0; i < size; i += 2) {
			corpus.add(Integer.toString(i));
		}

		for (int i = size - 1; i >= 0; i -= 2) {
			corpus.add(Integer.toString(i));
		}

		sc = new SparseSymmetricalCorrelation(corpus);

		for (int i = 0; i < size; i++) {
			for (int j = 0; j <= i; j++) {
				sc.encountered(Integer.toString(i), Integer.toString(j));
			}
		}

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < i; j++) {
				Assert.assertEquals(1, sc.get(Integer.toString(j), Integer.toString(i)));
			}
		}
	}

	@Test
	public void testGrowth() {
		for (int i = 0; i < 100; i++) {
			corpus.add(Integer.toString(i));
		}

		sc = new SparseSymmetricalCorrelation(corpus);

		for (int i = 0; i < 100; i++) {
			for (int j = 0; j < i; j++) {
				sc.encountered(Integer.toString(i), Integer.toString(j));
			}
		}

		for (int i = 0; i < 100; i++) {
			for (int j = 0; j < i; j++) {
				Assert.assertEquals(1, sc.get(Integer.toString(j), Integer.toString(i)));
			}
		}
	}

	@Test
	public void testEmpty() {
		Assert.assertFalse(sc.iterator().hasNext());

		sc.encountered("fail", "fast");

		Assert.assertTrue(sc.iterator().hasNext());
	}

	@Test
	public void test() {
		sc.encountered("fail", "fast");

		Assert.assertEquals(1, sc.get("fast", "fail"));
		Assert.assertEquals(1, sc.get("fail", "fast"));
		Assert.assertEquals(0, sc.get("fail", "fail"));

		Iterator<SingleCorrelation> it = sc.iterator();

		Assert.assertEquals("fail", it.next().getFrom());
		Assert.assertFalse(it.hasNext());
	}
}
