package com.velik.recommend.map;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Foo {

	public static void main(String[] args) {
		MapContext ctx = new MapContext(new File("/projects/recommend/accesslog/data"), true);

		Set<String> departments = new HashSet<String>();

		for (ArticleInfo value : ctx.getArticleInfo().values()) {
			departments.add(value.department);
		}

		ArrayList<String> departmentList = new ArrayList<String>(departments);

		Collections.sort(departmentList);

		for (String dept : departmentList) {
			String[] path = dept.split("/");

			if ((dept.startsWith("region-") && path.length > 1) || path[0].equals("bergisches-land")
					|| path[0].startsWith("niederrhein")) {
				System.out.println(dept + "->" + "regio/" + path[1]);
			} else if (path[path.length - 1].equals("nachrichten")) {
				System.out.println(dept + "->" + "regio/" + path[path.length - 2]);
			} else if (dept.startsWith("sport/fussball/")) {
				System.out.println(dept + "->" + "/sport/" + path[2]);
			} else if (dept.startsWith("sport") && path.length > 1) {
				System.out.println(dept + "->" + "/sport/" + path[1]);
			} else {
				System.out.println(dept + "->" + "/allgemein/" + path[0]);
			}
		}

		// new DistributionFactory(ctx.getAccessIterator()).print();

		/*
		 * StressMap map = ctx.getMap(); DepartmentMapPositionValue value = new
		 * DepartmentMapPositionValue(map, ctx.getArticleInfo(),
		 * ctx.getStressMatrix(), true);
		 * 
		 * ctx.getMap();
		 */
	}
}
