package com.velik.recommend.factory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;

import com.velik.recommend.corpus.Article;
import com.velik.recommend.corpus.ArticleVisitor;

public class ParsedSpideredArticlesVisitee implements Visitee {
	private static final String BODY_PREFIX = "body:";
	private static final String AUTHOR_PREFIX = "author:";
	private static final String SUBLINE_PREFIX = "subline:";
	private static final String TITLE_PREFIX = "title:";
	private static final String URL_PREFIX = "url:";
	private Visitee unparsedVisitee;

	public ParsedSpideredArticlesVisitee(Visitee unparsedVisitee) {
		this.unparsedVisitee = unparsedVisitee;
	}

	@Override
	public void visit(ArticleVisitor visitor) {
		File parsedArticlesFile = new File("parsed-articles.txt");

		if (!parsedArticlesFile.exists()) {
			System.out.println("Parsed articles file didn't exist. Rebuilding...");

			rebuildParsedArticles(parsedArticlesFile, visitor);
		} else {
			visitParsedArticles(parsedArticlesFile, visitor);
		}
	}

	private void visitParsedArticles(File parsedArticlesFile, ArticleVisitor visitor) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(parsedArticlesFile),
					"UTF-8"));

			String line;

			PojoArticle article = null;

			int stop = 1000000;

			while ((line = reader.readLine()) != null && stop-- > 0) {
				if (line.startsWith(URL_PREFIX)) {
					if (article != null) {
						visitor.processArticle(article);
					}

					article = new PojoArticle();

					String urlString = line.substring(URL_PREFIX.length());

					try {
						article.setUrl(new URL(urlString));
					} catch (MalformedURLException e) {
						System.err.println("URL " + urlString + " was malformed.");
					}
				} else if (line.startsWith(TITLE_PREFIX)) {
					article.setTitle(line.substring(TITLE_PREFIX.length()));
				} else if (line.startsWith(SUBLINE_PREFIX)) {
					article.setSubline(line.substring(SUBLINE_PREFIX.length()));
				} else if (line.startsWith(AUTHOR_PREFIX)) {
					article.setAuthor(line.substring(AUTHOR_PREFIX.length()));
				} else if (line.startsWith(BODY_PREFIX)) {
					article.setBody(line.substring(BODY_PREFIX.length()).replace('|', '\n'));
				} else {
					System.err.println("Unknown line starting with " + line.substring(0, Math.min(100, line.length())));
				}
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void rebuildParsedArticles(File parsedArticlesFile, final ArticleVisitor visitor) {
		try {
			final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					parsedArticlesFile, true), "UTF-8"));

			unparsedVisitee.visit(new ArticleVisitor() {

				@Override
				public void processArticle(Article article) {
					visitor.processArticle(article);

					if (!article.getBody().trim().equals("")) {
						try {
							writer.write(URL_PREFIX + article.getUrl().toString() + "\n");
							writer.write(TITLE_PREFIX + article.getTitle() + "\n");
							writer.write(SUBLINE_PREFIX + article.getSubline() + "\n");
							writer.write(AUTHOR_PREFIX + article.getAuthor() + "\n");
							writer.write(BODY_PREFIX);
							writer.write(article.getBody().replace('\n', '|'));
							writer.write("\n");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			});

			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
