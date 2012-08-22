package com.velik.recommend.spider;

import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;

public class TopLevelUrlFilterTest {

	@Test
	public void test() throws Exception {
		TopLevelUrlFilter filter = new TopLevelUrlFilter("www.a.com", "hej", "foo");

		Assert.assertTrue(filter.isFollow(new URL("http://www.a.com/hej/da"), null));
		Assert.assertTrue(filter.isFollow(new URL("http://www.a.com/foo"), null));
		Assert.assertTrue(filter.isFollow(new URL("http://www.b.com/"), null));
		Assert.assertFalse(filter.isFollow(new URL("http://www.a.com/"), null));
		Assert.assertFalse(filter.isFollow(new URL("http://www.a.com/do/hej"), null));
	}
}
