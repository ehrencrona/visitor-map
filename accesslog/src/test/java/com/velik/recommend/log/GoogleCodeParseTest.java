package com.velik.recommend.log;

import org.junit.Assert;
import org.junit.Test;

public class GoogleCodeParseTest {

	@Test
	public void testParse() throws InvalidRequestException {
		Assert.assertEquals(1314646606,
				AccessLoggingFilter.getUserId("143822771.1314646606.1343884569.1344153862.1344156770.4"));

		// 143822771.779723946.1345184064.1345230574.1345272210.4

		try {
			AccessLoggingFilter.getUserId("");
			Assert.fail();
		} catch (InvalidRequestException e) {
		}
	}
}
