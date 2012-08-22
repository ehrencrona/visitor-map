package com.velik.recommend.log;

import java.io.File;

public class Read {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for (Access a : new AccessLogReader(new File("logger.S19LPOL01.rbpd.de.12-08-01.log"))) {
			System.out.println(a);
		}
	}
}
