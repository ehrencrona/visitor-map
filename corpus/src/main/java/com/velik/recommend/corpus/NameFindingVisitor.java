package com.velik.recommend.corpus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.velik.recommend.factory.CompoundWordUtil;
import com.velik.recommend.model.Nouns;
import com.velik.recommend.tokenizer.TextTokenizer;
import com.velik.recommend.tokenizer.TokenizedText;
import com.velik.recommend.tokenizer.Word;

public final class NameFindingVisitor implements ArticleVisitor {
	private static final int POINT_LIMIT = 200;

	private Nouns nouns;

	private Map<String, List<PotentialName>> potentialNameByComponent = new HashMap<String, List<PotentialName>>();
	private Map<String, PotentialName> potentialNameByFullName = new HashMap<String, PotentialName>();

	private CompoundWordUtil compoundWords;
	private PersonNames personNames;

	private Frequency epithets = new Frequency(new Corpus());

	private static boolean explain = false;

	public static class PotentialName {
		protected int points = 0;

		int begin = -1;
		int end = -1;

		public String epithet;

		private TokenizedText text;

		// what parts of the name reoccur later and how often?
		private Map<String, Integer> referredTo = new HashMap<String, Integer>();

		private TokenizedText lastReferenceText;
		private Word beginLastReference;
		private Word endLastReference;

		public PotentialName(TokenizedText text, int begin, int end) {
			this.text = text;
			this.begin = begin;
			this.end = end;
		}

		public String toString() {
			return getFullName() + " (" + points + ")";
		}

		public String getFullName() {
			StringBuffer fullName = new StringBuffer();

			for (int i = begin; i <= end; i++) {
				if (fullName.length() > 0) {
					fullName.append(' ');
				}

				try {
					fullName.append(text.getWord(i).getWord());
				} catch (NoSuchWordException e) {
				}
			}

			return fullName.toString();
		}

		public String getFirstName() {
			return text.getWord(begin).getWord();
		}

		public int getNameCount() {
			return end - begin + 1;
		}

		public String getName(int i) {
			return text.getWord(begin + i).getWord();
		}

		public String getLastName() {
			return getName(getNameCount() - 1);
		}

		public void storeLastReference() {
			if (beginLastReference == null || endLastReference == null) {
				return;
			}

			String referredToAs = lastReferenceText.getWords(beginLastReference.getIndex(),
					endLastReference.getIndex() + 1);

			if (referredToAs.equals(getLastName() + "s")) {
				referredToAs = getLastName();
			}

			Integer oldRefs = referredTo.get(referredToAs);

			if (oldRefs == null) {
				oldRefs = 0;
			}

			referredTo.put(referredToAs, oldRefs + 1);
			beginLastReference = null;
			endLastReference = null;
		}
	}

	public NameFindingVisitor(Nouns nouns, PersonNames personNames) {
		this.nouns = nouns;

		compoundWords = new CompoundWordUtil(nouns);

		this.personNames = personNames;
	}

	@Override
	public void processArticle(Article article) {
		articleStarted();

		encounteredText(article.getTitle());
		encounteredText(article.getSubline());
		encounteredText(article.getBody());

		articleEnded();
	}

	void articleStarted() {
		potentialNameByComponent.clear();
		potentialNameByFullName.clear();
	}

	List<PotentialName> articleEnded() {
		List<PotentialName> result = new ArrayList<PotentialName>();

		if (!potentialNameByComponent.isEmpty()) {
			for (Entry<String, PotentialName> entry : potentialNameByFullName.entrySet()) {
				PotentialName name = entry.getValue();
				name.storeLastReference();

				String fullName = name.getFullName();
				Integer fullNameReferences = name.referredTo.get(fullName);

				if (fullNameReferences != null) {
					if (explain)
						System.out.println(name + " was referred to " + fullNameReferences + " time(s) by full name.");

					name.points += 50 * fullNameReferences;
					name.referredTo.remove(fullName);
				}

				if (name.referredTo.size() == 1) {
					Entry<String, Integer> reference = name.referredTo.entrySet().iterator().next();
					if (reference.getKey().equals(name.getLastName())) {
						name.points += 40 * Math.min(reference.getValue(), 2);

						if (explain)
							System.out.println(name + " was referred to using last name " + reference.getValue()
									+ " time(s).");
					} else {
						name.points -= 25;

						if (explain)
							System.out.println(name + " was referred to using another name than last name ("
									+ reference.getKey() + ").");
					}
				} else if (name.referredTo.size() > 1) {
					name.points -= 50;

					if (explain)
						System.out.println(name + " was referred to using several names " + name.referredTo.keySet()
								+ ".");
				}
			}

			Iterator<Entry<String, PotentialName>> it = potentialNameByFullName.entrySet().iterator();

			nextPotential: while (it.hasNext()) {
				Entry<String, PotentialName> entry = it.next();
				PotentialName pn = entry.getValue();

				if (pn.points < POINT_LIMIT) {
					if (explain)
						System.out.println("dropped " + pn + " due to point limit");

					it.remove();
				} else {
					String lastName = pn.getName(pn.getNameCount() - 1);

					List<PotentialName> otherSimilars = potentialNameByComponent.get(lastName);

					if (otherSimilars == null) {
						continue;
					}

					for (PotentialName otherSimilar : otherSimilars) {
						if (otherSimilar == pn) {
							continue;
						}

						if (otherSimilar.points >= pn.points) {
							if (explain)
								System.out.println("dropped " + pn + " due to " + otherSimilar
										+ " being more promising.");

							it.remove();
							continue nextPotential;
						}
					}
				}
			}

			if (!potentialNameByFullName.isEmpty()) {
				for (PotentialName name : potentialNameByFullName.values()) {
					if (name.epithet != null) {
						epithets.encountered(name.epithet);
					}

					result.add(name);
				}
			}
		}

		return result;
	}

	public void done() {
		Frequency splitEpithets = new Frequency(new Corpus());

		for (String epithet : epithets.getCorpus()) {
			String original = epithet;

			epithet = primitiveStemming(epithet.toLowerCase());

			ArrayList<String> components = compoundWords.getComponents(epithet);

			String lastComponent = components.get(components.size() - 1);

			lastComponent = primitiveStemming(lastComponent);

			if (nouns.contains(lastComponent) && lastComponent.length() > 2) {
				int f = epithets.get(original);
				splitEpithets.add(lastComponent, f);
			}
		}

		Frequency finalEpithets = new Frequency(new Corpus());

		for (String splitEpithet : splitEpithets.getCorpus()) {
			int f = splitEpithets.get(splitEpithet);

			if (f > 20) {
				finalEpithets.add(splitEpithet, f);
			}
		}

		personNames.epithets = finalEpithets.getCorpus();
	}

	String primitiveStemming(String epithet) {
		epithet = potentialEnding(epithet, "en");
		epithet = potentialEnding(epithet, "in");
		epithet = potentialEnding(epithet, "s");

		if (epithet.startsWith("vize")) {
			epithet = epithet.substring(4);
		}

		if (epithet.startsWith("bundes")) {
			epithet = epithet.substring(6);
		}

		return epithet;
	}

	private String potentialEnding(String string, String ending) {
		if (string.endsWith(ending) && nouns.contains(string.substring(0, string.length() - ending.length()))) {
			return string.substring(0, string.length() - ending.length());
		} else {
			return string;
		}
	}

	public void encounteredText(String textString) {
		TextTokenizer tokenizer = new TextTokenizer(textString, false);

		tokenizer.setLengthLimit(0);

		int nameBegin = 0;

		TokenizedText text = tokenizer.tokenize();

		for (Word next : text) {
			if (next.getPrecedingPunctuation() != ' ') {
				nameBegin = next.getIndex();
			}

			String word = next.getWord();

			if (!isCapitalized(word)) {
				if (!personNames.titles.contains(word)) {
					nameBegin = next.getIndex() + 1;
				} else if (nameBegin == next.getIndex()) {
					nameBegin++;
				}
			} else if (nameBegin == next.getIndex() && isCapitalizedArticle(word)) {
				nameBegin++;
			} else if (!isAllUpperCase(word)) {
				for (int tryNameBegin = nameBegin; tryNameBegin <= next.getIndex(); tryNameBegin++) {
					encounteredPotentialName(next, tryNameBegin, text);
				}
			} else {
				nameBegin = next.getIndex() + 1;
			}
		}
	}

	private boolean isCapitalizedArticle(String word) {
		return word.equals("Der") || word.equals("Die") || word.equals("Das");
	}

	private boolean isAllUpperCase(String word) {
		for (int i = 0; i < word.length(); i++) {
			if (Character.isLowerCase(word.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	private void encounteredPotentialName(Word word, int nameBegin, TokenizedText text) {
		PotentialName pn = null;

		if (nameBegin < word.getIndex()) {
			try {
				Word next = word.getWord(1);

				if (getNamePlausibility(next.getWord(), personNames.middles) > 0
						|| getNamePlausibility(next.getWord(), personNames.lasts) > 0) {
					// we'll be back.
					return;
				}
			} catch (NoSuchWordException e) {
				// ok, go on.
			}

			// do we have <first name> <last name>? This should be the first
			// occurrence of a name.

			PotentialName oldPn = potentialNameByFullName.get(new PotentialName(text, nameBegin, word.getIndex())
					.getFullName());

			if (oldPn == null) {
				pn = new PotentialName(text, nameBegin, word.getIndex());

				String explanation = "";

				int points = 0;

				try {
					List<Integer> nameComponentIndexes = new ArrayList<Integer>();

					for (int i = nameBegin; i <= word.getIndex(); i++) {
						nameComponentIndexes.add(personNames.getCorpus().indexOf(text.getWord(i).getWord()));
					}

					if (personNames.full.contains(toIntArray(nameComponentIndexes))) {
						explanation += ", " + pn.getFullName() + " is a known full name";

						pn.points += 200;
					}
				} catch (NoSuchWordException e) {
				}

				String firstName = text.getWord(nameBegin).getWord();

				int firstPlausibility = getNamePlausibility(firstName, personNames.firsts);

				if (firstPlausibility > 0) {
					if (explain) {
						explanation += ", " + firstName + " is a known first name";
					}

					points += 15 * firstPlausibility;
				} else {
					if (explain) {
						explanation += ", " + firstName + " is not a known first name";
					}

					points -= 50;
				}

				if (compoundWords.isCompoundWord(firstName.toLowerCase())) {
					if (explain) {
						explanation += ", " + firstName + " is compound";
					}

					points -= 150;
				}

				int lastPlausibility = getNamePlausibility(word.getWord(), personNames.lasts);

				if (lastPlausibility > 0) {
					if (explain) {
						explanation += ", " + word.getWord() + " is a known last name";
					}

					points += 20 * lastPlausibility;
				} else {
					if (explain) {
						explanation += ", " + word.getWord() + " is not a known last name";
					}

					// last names are hard to recognize but we must penalize
					// longer strings with unknown words at the end in case they
					// are unrelated to the name.
					points -= 10;
				}

				int numberOfNames = word.getIndex() - nameBegin + 1;

				for (int i = nameBegin + 1; i < word.getIndex(); i++) {
					String middleName = text.getWord(i).getWord();

					if (personNames.titles.contains(middleName)) {
						points -= 25;

						if (explain) {
							explanation += ", " + middleName + " is a title and unusual";
						}

						numberOfNames--;
					} else {
						int middlePlausibility = getNamePlausibility(middleName, personNames.middles);

						if (middlePlausibility > 0) {
							points += 10 * middlePlausibility;

							if (explain) {
								explanation += ", " + middleName + " is a known middle name";
							}
						} else {
							points -= 25;

							if (explain) {
								explanation += ", " + middleName + " is not a known middle name";
							}
						}
					}
				}

				try {
					String potentialEpithet = text.getWord(nameBegin - 1).getWord().toLowerCase();

					ArrayList<String> components = compoundWords.getComponents(potentialEpithet);

					String last = components.get(components.size() - 1);

					if (personNames.epithets.contains(last)) {
						points += 50;

						if (explain) {
							explanation += ", the preceding word " + potentialEpithet + " contained the epithet "
									+ last;
						}
					}
				} catch (NoSuchWordException e) {
					// no epithet
				}

				try {
					Word followingWord = text.getWord(word.getIndex() + 1);

					if (followingWord.getPrecedingPunctuation() == ',') {
						String potentialEpithet = followingWord.getWord().toLowerCase();

						ArrayList<String> components = compoundWords.getComponents(potentialEpithet);

						String last = components.get(components.size() - 1);

						if (personNames.epithets.contains(last)) {
							points += 50;

							if (explain) {
								explanation += ", the following word " + potentialEpithet + " contained the epithet "
										+ last;
							}
						}
					}
				} catch (NoSuchWordException e) {
					// no epithet
				}

				if (numberOfNames > 4) {
					points -= 250;
				} else if (numberOfNames > 3) {
					points -= 150;
				} else if (numberOfNames > 2) {
					points -= 50;
				} else if (numberOfNames <= 1) {
					points = 0;
				}

				if (numberOfNames > 2) {
					if (explain) {
						explanation += ", there are " + numberOfNames + " names";
					}
				}

				if (points > 0) {
					try {
						String precedingWord = text.getWord(nameBegin - 1).getWord();

						if (isCapitalized(precedingWord)) {
							pn.epithet = precedingWord;
						}
					} catch (NoSuchWordException e) {
					}

					try {
						Word nextWord = text.getWord(word.getIndex() + 1);

						if (nextWord.getPrecedingPunctuation() == ',' && isCapitalized(nextWord.getWord())) {
							pn.epithet = nextWord.getWord();
						}
					} catch (NoSuchWordException e) {
					}

					pn.points = points;

					for (int i = 0; i < pn.getNameCount(); i++) {
						// skip first and middle names; people are addressed by
						// last
						// name in news texts.
						addToPotential(pn.getName(i), pn);
					}

					potentialNameByFullName.put(pn.getFullName(), pn);

					if (explain) {
						System.out.println("Potential " + pn + explanation);
					}
				} else {
					if (explain)
						System.out.println("Ignored " + pn + explanation);
				}
			}
		}

		// it wasn't a full name; but it might be a family name
		// referring to an earlier full name.
		String wordString = word.getWord();

		revisit(word, wordString, text);

		if (wordString.endsWith("s")) {
			wordString = wordString.substring(0, wordString.length() - 1);

			revisit(word, wordString, text);
		}
	}

	private int[] toIntArray(List<Integer> nameComponentIndexes) {
		int[] result = new int[nameComponentIndexes.size()];

		for (int i = 0; i < result.length; i++) {
			result[i] = nameComponentIndexes.get(i);
		}

		return result;
	}

	private void revisit(Word word, String wordString, TokenizedText text) {
		List<PotentialName> oldPns = potentialNameByComponent.get(wordString);

		if (oldPns != null) {
			for (PotentialName oldPn : oldPns) {
				if (oldPn.lastReferenceText != text || oldPn.end < word.getIndex()) {
					if (oldPn.endLastReference != null) {
						if (oldPn.lastReferenceText == text && oldPn.endLastReference.getIndex() == word.getIndex() - 1) {
							oldPn.endLastReference = word;
						} else {
							oldPn.storeLastReference();
						}
					} else {
						oldPn.lastReferenceText = text;
						oldPn.beginLastReference = word;
						oldPn.endLastReference = word;
					}
				}
			}
		}
	}

	private void addToPotential(String word, PotentialName pn) {
		List<PotentialName> existing = potentialNameByComponent.get(word);

		if (existing == null) {
			existing = new ArrayList<PotentialName>();
			potentialNameByComponent.put(word, existing);
		}

		existing.add(pn);
	}

	/**
	 * @return 0..10
	 */
	private int getNamePlausibility(String string, WordSet names) {
		int parts = 0;

		int points = 0;

		for (String part : string.split("-")) {
			if (names.contains(part)) {
				points += 10;
			} else {
				if (names == personNames.firsts && compoundWords.isCompoundWord(string)) {
					// should be higher than the points gained by having
					// another, known, name, see "Mercedes-Sportchef".
					points -= 15;
				}
			}

			parts++;
		}

		return Math.max(points / parts, 0);
	}

	private boolean isCapitalized(String word) {
		return Character.isUpperCase(word.charAt(0));
	}

	public Collection<PotentialName> getPotentialNames() {
		return potentialNameByFullName.values();
	}
}