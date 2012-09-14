package com.velik.recommend.factory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.velik.recommend.corpus.Article;
import com.velik.recommend.corpus.ArticleVisitor;
import com.velik.recommend.corpus.NameFindingVisitor;
import com.velik.recommend.corpus.NameFindingVisitor.PotentialName;
import com.velik.recommend.corpus.NoSuchWordException;
import com.velik.recommend.corpus.PersonNames;
import com.velik.recommend.corpus.WordSet;

public class PersonNameCorpusFactory {

	public PersonNames create(final Context context) {
		System.out.println("Computing person names...");
		final PersonNames result = new PersonNames();

		WordSet firsts = result.firsts;
		WordSet middles = result.middles;
		WordSet lasts = result.lasts;
		WordSet titles = result.titles;

		for (String word : new String[] { "al", "auf dem", "auf'm", "aus dem", "aus der", "d", "e", "da", "de", "del",
				"los", "las", "den", "der", "di", "do", "dos", "du", "el", "la", "lo", "ten", "ter", "van", "von",
				"von de", "von", "y", "zur", "zu", "ibn" }) {
			titles.add(word);
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(
					"../spider/names.txt")), "UTF-8"));

			String line;

			while ((line = reader.readLine()) != null) {
				int j = line.indexOf(',');

				if (j > 0) {
					line = line.substring(0, j);
				}

				if (hasSpecialCharacters(line)) {
					continue;
				}

				int i = line.indexOf(" --- ");

				if (i > 0) {
					line = line.substring(0, i) + line.substring(i + 4);
				}

				String firstAndMiddle = null;
				String last = null;

				i = line.lastIndexOf(" ");

				if (i > 0) {
					last = line.substring(i + 1);
					firstAndMiddle = line.substring(0, i);
				}

				if (firstAndMiddle != null && last != null) {
					String[] components = last.split("[ \\.-]");

					for (String lastPart : components) {
						if (lastPart.equals("")) {
							continue;
						}

						if (lastPart.length() > 3 && Character.isUpperCase(lastPart.charAt(0))
								&& !isEntirelyUppercase(lastPart)) {
							lasts.add(lastPart);
						}
					}

					boolean first = true;
					for (String firstOrMiddlePart : firstAndMiddle.split("[ \\.-]")) {
						if (firstOrMiddlePart.length() > 2 && Character.isUpperCase(firstOrMiddlePart.charAt(0))
								&& !isEntirelyUppercase(firstOrMiddlePart)) {

							if (first) {
								firsts.add(firstOrMiddlePart);
								first = false;
							} else {
								middles.add(firstOrMiddlePart);
							}
						}
					}
				}
			}

			reader.close();

			final Map<String, Integer> countByFullName = new HashMap<String, Integer>();

			final NameFindingVisitor delegate = new NameFindingVisitor(context.getNouns(), result);

			context.visit(new ArticleVisitor() {
				private int articleCount = 0;

				@Override
				public void processArticle(Article article) {
					if (articleCount++ % 10000 == 9999) {
						System.out.println(articleCount + " articles...");
					}

					delegate.processArticle(article);

					for (PotentialName name : delegate.getPotentialNames()) {
						String fullName = name.getFullName();
						Integer count = countByFullName.get(fullName);

						if (count == null) {
							count = 1;
						} else {
							count = count + 1;
						}

						countByFullName.put(fullName, count);
					}
				}
			});

			delegate.done();

			for (Entry<String, Integer> entry : countByFullName.entrySet()) {
				System.out.println(entry);

				if (entry.getValue() < 5) {
					continue;
				}

				String fullName = entry.getKey();

				try {
					String[] components = fullName.split(" ");

					List<Integer> indexes = new ArrayList<Integer>();

					for (int i = 0; i < components.length; i++) {
						String component = components[i];

						for (String subComponent : component.split("-")) {
							try {
								indexes.add(result.titles.indexOf(subComponent));
							} catch (NoSuchWordException e) {
								if (i == 0) {
									indexes.add(result.firsts.indexOf(subComponent));
								} else if (i == components.length - 1) {
									indexes.add(result.lasts.indexOf(subComponent));
								} else {
									indexes.add(result.middles.indexOf(subComponent));
								}
							}
						}
					}

					result.full.add(toIntArray(indexes));
				} catch (NoSuchWordException e) {
					// next. don't pollute the name lists
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Done.");

		return result;
	}

	private int[] toIntArray(List<Integer> indexes) {
		int[] result = new int[indexes.size()];

		for (int i = 0; i < indexes.size(); i++) {
			result[i] = indexes.get(i);
		}

		return result;
	}

	private boolean hasSpecialCharacters(String line) {
		for (int i = 0; i < line.length(); i++) {
			char ch = line.charAt(i);

			if (ch != ' ' && ch != '.' && ch != '-' && !Character.isLetter(ch)) {
				return true;
			}
		}

		return false;
	}

	private boolean isEntirelyUppercase(String string) {
		for (int i = 0; i < string.length(); i++) {
			if (!Character.isUpperCase(string.charAt(i))) {
				return false;
			}
		}

		return true;
	}
}
