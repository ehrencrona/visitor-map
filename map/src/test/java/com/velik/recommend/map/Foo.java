package com.velik.recommend.map;

import java.io.File;

public class Foo {

	public static void main(String[] args) {
		Context ctx = new Context(new File("/projects/recommend/accesslog/data"), true);

		StressMap map = ctx.getMap();

		ctx.getMap();
	}
}
