package com.velik.recommend.corpus;

import java.io.File;

import com.velik.recommend.factory.Context;
import com.velik.recommend.factory.ParsedSpideredArticlesVisitee;
import com.velik.recommend.factory.UnparsedSpideredArticlesVisitee;

public class MediumTest {

	public static Context context = new Context(new ParsedSpideredArticlesVisitee(new UnparsedSpideredArticlesVisitee(
			new File("/projects/spider"), 30)), true);

}
