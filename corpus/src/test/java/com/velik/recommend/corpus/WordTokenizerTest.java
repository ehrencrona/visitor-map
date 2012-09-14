package com.velik.recommend.corpus;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import com.velik.recommend.tokenizer.TextTokenizer;
import com.velik.recommend.tokenizer.TokenizedText;
import com.velik.recommend.tokenizer.Word;

public class WordTokenizerTest {

	@Test
	public void testNewline() {
		TextTokenizer t = new TextTokenizer("weiter.\nDer neue deutsche Malerstar", false);

		TokenizedText text = t.tokenize();

		Assert.assertEquals("weiter", text.getWord(0).getWord());
		Assert.assertEquals("Der", text.getWord(1).getWord());

	}

	@Test
	public void test() {
		TextTokenizer t = new TextTokenizer("Sie sagte: \"Ich, so ob-dachte ich, war alt.\" Nicht.", false);

		t.setLengthLimit(0);

		TokenizedText text = t.tokenize();

		Iterator<Word> it = text.iterator();

		assertNext(' ', "Sie", it);
		assertNext(' ', "sagte", it);
		assertNext('"', "Ich", it);
		assertNext(',', "so", it);
		assertNext(' ', "ob-dachte", it);
		assertNext(' ', "ich", it);
		assertNext(',', "war", it);
		assertNext(' ', "alt", it);
		assertNext('"', "Nicht", it);

		Assert.assertFalse(it.hasNext());
	}

	private void assertNext(char c, String string, Iterator<Word> it) {
		Assert.assertTrue(it.hasNext());

		Word next = it.next();

		Assert.assertEquals(string, next.getWord());
		Assert.assertEquals(
				"Was " + (char) +next.getPrecedingPunctuation() + " rather than expected " + (char) c + ".", c,
				next.getPrecedingPunctuation());
	}
}
