package com.velik.recommend.spider.names;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;

import com.velik.json.JsonArray;
import com.velik.json.JsonMap;
import com.velik.json.JsonParser;
import com.velik.json.ParseException;

public class MarathonNameSpider {

	// "http://www.bmw-berlin-marathon.com/files/addons/scc_events_data/ajax.teilnehmer.php?ident=MAH,MAL,MAR,MAW,MAI&_search=true&nd=1344103585322&rows=200&sidx=teilnehmer_name&sord=asc&teilnehmer_name=&page=";

	private static final String BASE_URL = "http://www.bmw-berlin-marathon.com/files/addons/scc_events_data/ajax.results.php?t=BM_2008&ci=MAL&s=default&l=de_de_utf8&_search=false&nd=1344107988305&rows=200&sidx=platz&sord=asc&page=";

	public static void main(String[] args) throws Exception {
		Writer writer = new OutputStreamWriter(new FileOutputStream(new File("names.txt"), true), "UTF-8");

		for (int page = 1; page < 176; page++) {
			Thread.sleep(1000);

			System.out.println(page);

			URL url = new URL(BASE_URL + page);

			StringBuffer sb = new UrlDownloader().load(url, "ISO-8859-1");

			try {
				Object json = new JsonParser(sb.toString()).parse();

				for (Object row : ((JsonArray) ((JsonMap) json).get("rows"))) {
					JsonArray cell = (JsonArray) ((JsonMap) row).get("cell");

					writer.write(decode(cell.get(4) + " --- " + cell.get(3)));
					writer.write('\n');
				}
			} catch (ParseException e) {
				System.out.println(e.toString());
			}

		}

		writer.close();
	}

	private static String decode(String string) {
		int i;

		while ((i = string.indexOf("\\u")) > 0) {
			string = string.substring(0, i) + unicode(string.substring(i + 2, i + 6)) + string.substring(i + 6);
		}

		return string;
	}

	private static char unicode(String hex) {
		return (char) Integer.parseInt(hex, 16);
	}
}
