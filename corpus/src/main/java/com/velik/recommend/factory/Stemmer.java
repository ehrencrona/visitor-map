package com.velik.recommend.factory;

import java.util.Iterator;

public interface Stemmer {
	Iterator<String> getPotentialStems(String word);
}
