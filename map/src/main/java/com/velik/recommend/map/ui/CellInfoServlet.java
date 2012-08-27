package com.velik.recommend.map.ui;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.velik.json.JsonMap;
import com.velik.recommend.map.ArticleInfo;
import com.velik.recommend.map.Context;
import com.velik.recommend.map.StressMap;
import com.velik.recommend.map.StressMatrix;

public class CellInfoServlet extends AbstractHttpServlet {
	private static final Pattern POSITION_PATTERN = Pattern.compile("/([0-9]*)x([0-9]*)");

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();

		if (pathInfo == null) {
			throw new ServletException("No position provided.");
		}

		Matcher matcher = POSITION_PATTERN.matcher(pathInfo);

		if (!matcher.find()) {
			throw new ServletException("Position \"" + pathInfo + "\" did not match <x>x<y> pattern.");
		}

		int col = Integer.parseInt(matcher.group(1));
		int row = Integer.parseInt(matcher.group(2));

		Context context = Context.getContext();

		StressMap map = context.getMap();
		int index = map.getIndex(map.pos(col, row));

		StressMatrix matrix = context.getStressMatrix();

		int minor = matrix.getMinorByIndex(index);

		ArticleInfo articleInfo = null;

		if (minor > 0) {
			articleInfo = context.getArticleInfo().get(minor);
		}

		if (articleInfo != null) {
			respondWithJson(
					new JsonMap().put("col", col).put("row", row).put("title", articleInfo.title)
							.put("type", articleInfo.type.toString().toLowerCase())
							.put("department", articleInfo.department), resp);
		} else {
			respondWithJson(new JsonMap().put("title", "Unknown article 1." + minor), resp);
		}
	}
}
