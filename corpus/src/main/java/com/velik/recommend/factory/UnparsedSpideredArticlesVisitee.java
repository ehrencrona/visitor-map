package com.velik.recommend.factory;

import java.io.File;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

import com.velik.recommend.corpus.ArticleVisitor;
import com.velik.recommend.parser.N24ArticleParser;
import com.velik.recommend.parser.Parser;
import com.velik.recommend.parser.RpArticleParser;
import com.velik.recommend.parser.SpideredDocument;
import com.velik.recommend.parser.SpideredHtmlZipFileReader;
import com.velik.recommend.parser.SueddeutscheArticleParser;
import com.velik.recommend.parser.ZeitArticleParser;

public class UnparsedSpideredArticlesVisitee implements Visitee {
	private static final Logger LOGGER = Logger.getLogger(UnparsedSpideredArticlesVisitee.class.getName());
	private int fileLimit;
	private File directory;

	public UnparsedSpideredArticlesVisitee(File directory, int fileLimit) {
		this.fileLimit = fileLimit;
		this.directory = directory;
	}

	public void visit(ArticleVisitor callback) {
		if (directory.isDirectory()) {
			for (int i = 1; i <= fileLimit; i++) {
				System.out.println(i);

				String istr = Integer.toString(i);

				File file = new File(directory, "spidered-html.0" + (istr.length() == 1 ? "0" : "") + istr + ".zip");

				if (!file.exists()) {
					continue;
				}

				Pattern sueddeutscheIdPattern = visitSingleFile(file, i, callback);
			}
		} else {
			visitSingleFile(directory, 0, callback);
		}
	}

	private Pattern visitSingleFile(File file, int i, ArticleVisitor callback) {
		Pattern minusIdPattern = Pattern.compile("[-/]1\\.[0-9]?");

		try {
			for (SpideredDocument document : new SpideredHtmlZipFileReader(new ZipFile(file))) {
				Parser parser = null;

				String html = document.getHtml();
				URL url = document.getUrl();

				if (url.getHost().equals("www.n24.de")) {
					if (url.getPath().endsWith(".html")) {
						parser = new N24ArticleParser(html, url);
					}
				} else if (url.getHost().equals("www.zeit.de")) {
					if (!url.getPath().startsWith("/schlagworte") && !url.getPath().endsWith("/index")
							&& !url.getPath().contains("www.zeit.de")) {
						parser = new ZeitArticleParser(html, url);
					}
				} else if (url.getHost().equals("www.sueddeutsche.de")) {
					if (minusIdPattern.matcher(url.getPath()).find()) {
						parser = new SueddeutscheArticleParser(html, url);
					}
				} else if (url.getHost().equals("www.rp-online.de")) {
					if (minusIdPattern.matcher(url.getPath()).find()) {
						parser = new RpArticleParser(html, url);
					}
				}

				if (parser != null) {
					callback.processArticle(parser.getArticle());
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "In " + i + ": " + e, e);
		}
		return minusIdPattern;
	}

}
