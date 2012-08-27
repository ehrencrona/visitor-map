package com.velik.recommend.map.ui;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.velik.recommend.map.Context;

public class MapPositionValueServlet extends AbstractHttpServlet {
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Context context = Context.getContext();

		MapPositionValue positionValue = new StressMapPositionValue(context.getMap());
		positionValue = new DepartmentMapPositionValue(context.getMap(), context.getArticleInfo(),
				context.getStressMatrix(), true);

		respondWithJson(new MapPositionValueJson(positionValue, context.getMap()), response);
	}

}
