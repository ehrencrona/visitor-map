package com.velik.recommend.log;

public interface AccessLog {

	void log(Access access);

	void close();

}
