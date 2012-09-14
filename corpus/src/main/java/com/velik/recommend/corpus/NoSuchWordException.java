package com.velik.recommend.corpus;

public class NoSuchWordException extends RuntimeException {

	public NoSuchWordException() {
		super();
	}

	public NoSuchWordException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public NoSuchWordException(String arg0) {
		super(arg0);
	}

	public NoSuchWordException(Throwable arg0) {
		super(arg0);
	}

}
