package com.velik.recommend.spider;

import java.io.File;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

public class SpideredHtmlFileTest {

	@Test
	public void test() throws Exception {
		File file = new File("foo.log");

		File zipInTheWay = new File("foo.001.zip");
		File firstLog = new File("foo.002.log");
		File rotatedLog = new File("foo.003.log");

		try {
			zipInTheWay.createNewFile();

			SpideredHtmlFile htmlFile = new SpideredHtmlFile(file);

			htmlFile.setMaxSize(180);
			htmlFile.setBufferSize(0);

			// these are about 300 bytes
			for (int i = 0; i < 11; i++) {
				htmlFile.write(new URL("http://www.a.com"), "<html></html>");
			}

			htmlFile.close();

			Assert.assertTrue(firstLog.exists());
			Assert.assertTrue(rotatedLog.exists());
			Assert.assertFalse(new File("foo.004.log").exists());
		} finally {
			firstLog.delete();
			rotatedLog.delete();
			zipInTheWay.delete();
		}
	}
}
