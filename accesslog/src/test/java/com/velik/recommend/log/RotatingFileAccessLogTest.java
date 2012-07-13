package com.velik.recommend.log;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

public class RotatingFileAccessLogTest {
	private int day = 0;

	@Test
	public void test() {
		RotatingFileAccessLog accessLog = new RotatingFileAccessLog("foo", false) {

			@Override
			protected int getCurrentDay() {
				return day;
			}

		};

		accessLog.log(new DefaultAccess(1, 1, 2));
		File oldFile = accessLog.getFile();

		day = 1;

		accessLog.log(new DefaultAccess(2, 3, 4));

		File newFile = accessLog.getFile();
		Assert.assertFalse(oldFile.getName().equals(newFile.getName()));

		accessLog.close();

		day = 0;

		Assert.assertEquals(1, new AccessLogReader(new File("foo" + accessLog.getFileNameSuffix()))
				.iterator().next().getMinorId());

		day = 1;
		Assert.assertEquals(3, new AccessLogReader(new File("foo" + accessLog.getFileNameSuffix()))
				.iterator().next().getMinorId());
	}
}
