package com.velik.recommend.corpus;

import java.util.Collection;

import org.junit.Assert;

import com.velik.recommend.corpus.NameFindingVisitor.PotentialName;

public class AbstractNameFindingTest {

	protected void assertEquals(Collection<PotentialName> potentialNames, String... fullNames) {
		for (String fullName : fullNames) {
			assertContains(potentialNames, fullName);
		}

		if (potentialNames.size() > fullNames.length) {
			String superfluous = "";
			for (PotentialName pn : potentialNames) {
				if (!contains(fullNames, pn.getFullName())) {
					superfluous += ", " + pn.getFullName();
				}
			}

			Assert.fail("Found the following unexpected names: " + superfluous.substring(2));
		}
	}

	private boolean contains(String[] array, String lookFor) {
		for (String item : array) {
			if (item.equals(lookFor)) {
				return true;
			}
		}

		return false;
	}

	protected void assertContains(Collection<PotentialName> potentialNames, String fullName) {
		for (PotentialName pn : potentialNames) {
			if (fullName.equals(pn.getFullName())) {
				return;
			}
		}

		Assert.fail(potentialNames + " did not contain " + fullName + ", only " + potentialNames);
	}

}
