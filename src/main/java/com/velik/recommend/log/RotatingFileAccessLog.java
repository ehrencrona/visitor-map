package com.velik.recommend.log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A version of the {@link FileAccessLog} that switches files every day. Map
 * this filter over the logging pixel to log accesses in the access log.
 * 
 * @author ehrencrona
 */
public class RotatingFileAccessLog extends FileAccessLog {
	private int lastDay;

	private String fileNamePrefix;

	public RotatingFileAccessLog(String fileNamePrefix, boolean append) {
		super();

		this.append = append;
		this.fileNamePrefix = fileNamePrefix;

		switchDay();
	}

	protected int getCurrentDay() {
		return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}

	@Override
	protected void unsynchronizedLog(Access access) {
		if (getCurrentDay() != lastDay) {
			switchDay();
		}

		super.unsynchronizedLog(access);
	}

	private void switchDay() {
		lastDay = getCurrentDay();
		setFileName(fileNamePrefix + getFileNameSuffix());
	}

	String getFileNameSuffix() {
		String hostName;

		try {
			hostName = "." + InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			// ok, drop it.
			hostName = "";
		}

		String date = "." + new SimpleDateFormat("yy-MM-").format(new Date()) + twoDigits(getCurrentDay());

		return hostName + date + ".log";
	}

	private String twoDigits(int i) {
		return (i < 10 ? "0" : "") + i;
	}
}
