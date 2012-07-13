package com.velik.recommend.util;

import org.junit.Assert;
import org.junit.Test;

public class Utf8StringTest {
	@Test
	public void testToString() {
		Assert.assertEquals("foobar", new Utf8String("foobar").toString());
	}

	@Test
	public void testEquality() {
		Assert.assertEquals(new Utf8String("foobar"), new Utf8String("foobar"));
		Assert.assertFalse(new Utf8String("foobar2").equals(new Utf8String("foobar")));
	}
}
