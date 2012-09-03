package com.velik.recommend.map;

import java.io.File;

import com.velik.recommend.stats.DistributionFactory;

public class Foo {

	public static void main(String[] args) {
		Context ctx = new Context(new File("/projects/recommend/accesslog/data"), true);

		new DistributionFactory(ctx.getAccessIterator()).print();

		/*
		 * StressMap map = ctx.getMap(); DepartmentMapPositionValue value = new
		 * DepartmentMapPositionValue(map, ctx.getArticleInfo(),
		 * ctx.getStressMatrix(), true);
		 * 
		 * ctx.getMap();
		 */
	}
}
