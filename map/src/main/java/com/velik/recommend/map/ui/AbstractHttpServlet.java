package com.velik.recommend.map.ui;

import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import com.velik.json.JsonObject;

public class AbstractHttpServlet extends HttpServlet {

	protected void respondWithJson(JsonObject json, HttpServletResponse response) throws IOException {
		if (json == null) {
			return;
		}

		OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream(), "UTF-8");

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		json.print(writer);

		writer.close();
	}

}
