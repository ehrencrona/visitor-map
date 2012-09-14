package com.velik.recommend.corpus;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import com.velik.recommend.factory.CompoundWordUtil;

public class CompoundWordUtilMediumTest {

	@Test
	public void testDass() throws Exception {
		ArrayList<String> components = new CompoundWordUtil(MediumTest.context.getNouns()).getComponents("dass");

		Assert.assertEquals("Was split to " + components, 1, components.size());
	}
}
