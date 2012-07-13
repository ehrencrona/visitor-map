package com.velik.recommend.spider;

import java.io.File;
import java.net.URL;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

public class UrlFileTest {

	@Test
	public void test() throws Exception {
		File file = new File("test.url");

		try {
			UrlFileWriter appender = new UrlFileWriter(file, false);

			URL url1 = new URL("http://www.a.com/b?c=d");
			URL url2 = new URL("http://www.foobar.com/hello/hej");

			appender.write(url1);
			appender.write(url2);

			appender.close();

			Iterator<URL> it = new UrlFileReader(file).iterator();

			Assert.assertEquals(url1, it.next());
			Assert.assertEquals(url2, it.next());
		} finally {
			file.delete();
		}
	}
}
