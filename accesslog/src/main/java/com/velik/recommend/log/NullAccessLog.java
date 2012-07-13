package com.velik.recommend.log;

public class NullAccessLog implements AccessLog {

	@Override
	public void log(Access access) {
		// swallow.
	}

	@Override
	public void close() {
		// don't do anything.
	}

}
