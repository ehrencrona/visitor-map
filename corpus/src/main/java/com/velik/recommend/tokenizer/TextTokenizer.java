package com.velik.recommend.tokenizer;

public class TextTokenizer {
	private int at;
	private boolean toLowerCase;
	private int lengthLimit = 3;

	boolean spaceBefore = true;
	private String string;

	private TokenizedText text = new TokenizedText();

	private char precededBy = ' ';
	private StringBuffer wordBuffer = new StringBuffer(30);

	public TextTokenizer(String string, boolean toLowerCase) {
		at = 0;

		this.toLowerCase = toLowerCase;
		this.string = string;
	}

	public TokenizedText tokenize() {
		while (at < string.length()) {
			try {
				char ch = string.charAt(at);

				if (isWordSeparator(ch)) {
					if (wordBuffer.length() > 0) {
						if (!spaceBefore) {
							wordBuffer.setLength(0);

							precededBy = ch;
						} else {
							pushCurrentWord();
						}

						spaceBefore = ch == ' ' || ch == '\n';

						precededBy = ch;
					} else {
						if (ch != ' ') {
							precededBy = ch;
						}

						spaceBefore = spaceBefore || ch == ' ' || ch == '\n';
					}
				} else if (isWordCharacter(ch)) {
					if (toLowerCase) {
						ch = Character.toLowerCase(ch);
					}

					wordBuffer.append(ch);
				} else {
					wordBuffer.setLength(0);
				}
			} finally {
				at++;
			}
		}

		pushCurrentWord();

		return text;
	}

	private void pushCurrentWord() {
		String wordString = wordBuffer.toString();

		if (sanityCheck(wordString)) {
			text.add(wordString, precededBy);
		}

		wordBuffer.setLength(0);
	}

	private boolean sanityCheck(String wordString) {
		if (wordString.length() < lengthLimit) {
			return false;
		}

		if (wordString.startsWith("-") || wordString.endsWith("-")) {
			return false;
		}

		int minuses = 0;

		boolean isUpper = true;

		for (int i = 0; i < wordString.length(); i++) {
			char ch = wordString.charAt(i);

			if (isUpper) {
				isUpper = Character.isUpperCase(ch);
			} else {
				if (Character.isUpperCase(ch)) {
					return false;
				}
			}

			if (ch == '-') {
				if (++minuses > 2) {
					return false;
				}

				isUpper = true;
			}
		}

		if (wordString.length() - minuses < lengthLimit) {
			return false;
		}

		return true;
	}

	private boolean isWordCharacter(char ch) {
		return Character.isLetter(ch) || ch == '-';
	}

	private boolean isWordSeparator(char ch) {
		return ch == ' ' || ch == '.' || ch == ',' || ch == ';' || ch == ':' || ch == '"' || ch == '\n';
	}

	public void setLengthLimit(int lengthLimit) {
		this.lengthLimit = Math.max(lengthLimit, 1);
	}
}
