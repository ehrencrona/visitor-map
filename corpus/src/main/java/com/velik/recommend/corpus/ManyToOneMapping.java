package com.velik.recommend.corpus;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ManyToOneMapping extends Frequency {
	private static final Logger LOGGER = Logger.getLogger(ManyToOneMapping.class.getName());

	private static final long serialVersionUID = 0;

	public ManyToOneMapping(Corpus corpus) {
		super(corpus);

		for (int i = 0; i < frequencies.length; i++) {
			frequencies[i] = -1;
		}
	}

	public void put(String from, String to) {
		set(from, indexOf(to));
	}

	protected void expand(int i) {
		int[] newFrequencies = new int[(int) (i * 1.3)];

		System.arraycopy(frequencies, 0, newFrequencies, 0, frequencies.length);

		for (int j = frequencies.length; j < newFrequencies.length; j++) {
			newFrequencies[j] = -1;
		}

		frequencies = newFrequencies;
	}

	public String getMapped(String from) throws NoSuchWordException {
		int map = get(from);

		if (map == -1) {
			throw new NoSuchWordException();
		}

		return corpus.get(map);
	}

	public List<String> getInverseMapping(String to) {
		int index = indexOf(to);

		List<String> result = new ArrayList<String>();

		for (int i = 0; i < frequencies.length; i++) {
			if (frequencies[i] == index) {
				result.add(corpus.get(i));
			}
		}

		return result;
	}
}
