package com.velik.recommend.corpus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Corpus implements Serializable, Iterable<String>, WordSet {
	private static final long serialVersionUID = -4695092449077455555L;

	private long id = new Random().nextLong();
	private List<String> words = new ArrayList<String>(100000);
	private Map<String, Integer> wordIdByWord = new HashMap<String, Integer>(100000);

	public Corpus() {
	}

	public Set<String> getAll() {
		return wordIdByWord.keySet();
	}

	public int add(String word) {
		Integer result = wordIdByWord.get(word);

		if (result != null) {
			return result;
		}

		int i = words.size();

		words.add(word);
		wordIdByWord.put(word, i);

		return i;
	}

	public int size() {
		return words.size();
	}

	public int indexOf(String word) throws NoSuchWordException {
		Integer result = wordIdByWord.get(word);

		if (result == null) {
			throw new NoSuchWordException();
		}

		return result;
	}

	@Override
	public Iterator<String> iterator() {
		return words.iterator();
	}

	public boolean contains(String word) {
		return wordIdByWord.containsKey(word);
	}

	public String get(int i) {
		return words.get(i);
	}

	public long getId() {
		return id;
	}

	public void print() {
		System.out.println(size() + " words");

		for (String word : this) {
			System.out.println(word);
		}
	}

	public void printAlphabetically() {
		System.out.println(size() + " words");

		ArrayList<String> words = new ArrayList<String>();

		for (String word : this) {
			words.add(word);
		}

		Collections.sort(words);

		for (String word : words) {
			System.out.println(word);
		}
	}

	public void addAll(String[] words) {
		for (String word : words) {
			add(word);
		}
	}
}
