package com.velik.recommend.corpus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.velik.recommend.corpus.NameFindingVisitor.PotentialName;
import com.velik.recommend.factory.Context;

public class NameFindingVisitorMediumTest extends AbstractNameFindingTest {
	private static Map<String, String> texts = new HashMap<String, String>();
	private static Context context = MediumTest.context;
	private static NameFindingVisitor visitor;
	private List<String> foundNames;
	private static int totalOptional;
	private static int foundOptional;

	@BeforeClass
	public static void setUp() throws Exception {
		Class<NameFindingVisitorMediumTest> klass = NameFindingVisitorMediumTest.class;

		BufferedReader reader = new BufferedReader(new InputStreamReader(klass.getResourceAsStream("/"
				+ klass.getSimpleName() + ".txt"), "UTF-8"));

		String line;

		while ((line = reader.readLine()) != null) {
			int i = line.indexOf(':');

			texts.put(line.substring(0, i), line.substring(i + 1));
		}

		visitor = new NameFindingVisitor(context.getNouns(), MediumTest.context.getPersonNames());
	}

	@AfterClass
	public static void summarize() {
		System.out.println("Found " + foundOptional + " out of " + totalOptional + " names.");
	}

	private void text(String textId) {
		visitor.articleStarted();
		visitor.encounteredText(texts.get(textId));
		visitor.articleEnded();

		Collection<PotentialName> pns = visitor.getPotentialNames();

		foundNames = new ArrayList<String>(pns.size());

		for (PotentialName pn : pns) {
			foundNames.add(pn.getFullName());
		}

	}

	private void mandatoryNames(String... names) {
		List<String> missing = new ArrayList<String>(names.length);

		for (String name : names) {
			if (!foundNames.remove(name)) {
				missing.add(name);
			}
		}

		if (!missing.isEmpty()) {
			Assert.fail("The following names were not found: " + missing + ". Instead found " + foundNames + ".");
		}
	}

	private void optionalNames(String... names) {
		totalOptional += names.length;

		for (String name : names) {
			if (foundNames.remove(name)) {
				foundOptional++;
			}
		}

		if (!foundNames.isEmpty()) {
			Assert.fail("Found the following unexpected names: " + foundNames);
		}
	}

	@Test
	public void testRomania() {
		text("ROMANIA");
		mandatoryNames("Steffen Seibert", "Victor Ponta", "José Manuel Barroso", "Herman van Rompuy");
		optionalNames("Traian Basescu");
	}

	@Test
	public void testKarabo() {
		text("KARABO");

		mandatoryNames("Lee Berger");
		optionalNames("Justin Mukanku");
	}

	@Test
	public void testMauer() {
		text("MAUER");

		mandatoryNames("Michail Gorbatschow", "Hans-Dietrich Genscher");
		optionalNames();
	}

	@Test
	public void testJuventus() {
		text("JUVENTUS");

		mandatoryNames("Luca Toni", "Massimo Ambrosini", "Alessandro Del Piero", "John Arne Riise");
		optionalNames("Stefano Okaka Chuka", "John Arne Riise", "Simone Vergassola", "Massimo Ambrosini",
				"Cristiano Lucarelli", "Alberto Zaccheroni", "Thomas Hitzlsperger");
	}

	@Test
	public void testAstronomie() {
		text("ASTRONOMIE");

		mandatoryNames("Simon Lilly");

		optionalNames("Sebastiano Cantalup");
	}

	@Test
	public void testFormel1() {
		text("FORMEL1");

		mandatoryNames("Pedro de la Rosa", "Norbert Haug", "Nico Rosberg", "Felipe Massa", "Robert Kubica",
				"Rubens Barrichello", "Michael Schumacher");
		optionalNames("Massimo Rivola", "Gary Paffett");
	}

	@Test
	public void testPicasso() {
		text("PICASSO");

		mandatoryNames("Anselm Kiefer", "Gerhard Richter", "Gustav Klimt", "Max Beckmann", "Willem de Kooning");
		optionalNames("Neo Rauch", "Ronald Lauder");
	}

	@Test
	public void testSki() {
		text("SKI");

		mandatoryNames("Christoph Ebert", "Tassilo Weinzierl", "Andreas König");
		optionalNames();
	}

	@Test
	public void testTour() {
		text("TOUR");

		mandatoryNames("Andreas Klöden", "Bradley Wiggins", "Thibaut Pinot", "Christopher Froome", "Pierre Rolland");

		optionalNames("Vincenzo Nibali", "Cadel Evans", "Christopher Froome", "John Lelangue");
	}

	@Test
	public void testBeiDenFrauen() {
		text("BEIDENFRAUEN");

		mandatoryNames("Johan Blake", "Usain Bolt", "Veronica Campbell-Brown", "Warren Weir", "Asafa Powell",
				"Shelly-Ann Fraser-Pryce", "Walker Meter");

		optionalNames("Kerron Stewart", "Sherone Simpson", "Ricky Simms", "Shericka Williams");
	}

	@Test
	public void testOvrebo() {
		text("OVREBO");

		mandatoryNames("Guus Hiddink", "Juan Mata", "Tom Henning Övrebo", "Tom Henning", "Frank Lampard");
		optionalNames();
	}

	@Test
	public void testNihat() {
		text("NIHAT");

		mandatoryNames("Fatih Terim", "Nihat Kahveci");
		optionalNames();
	}

	@Test
	public void testKrawinkel() {
		text("KRAWINKEL");

		mandatoryNames("Michael Krawinkel", "Antje Gahl");

		optionalNames("Ursula Summ", "Howard Hay");
	}
}