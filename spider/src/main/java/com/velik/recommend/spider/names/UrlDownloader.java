package com.velik.recommend.spider.names;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class UrlDownloader {

	StringBuffer load(URL url, String charset) throws IOException, UnsupportedEncodingException {
		URLConnection connection = url.openConnection();

		connection.setConnectTimeout(15000);
		connection.setReadTimeout(15000);

		if (connection instanceof HttpURLConnection) {
			int responseCode = ((HttpURLConnection) connection).getResponseCode();

			if (responseCode >= 400 && responseCode < 600) {
				throw new IOException("Got response code " + responseCode + ".");
			}
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), charset), 100000);

		StringBuffer sb = new StringBuffer(10000);

		String line;

		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		return sb;
	}
}
