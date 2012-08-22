package com.velik.recommend.stats;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.velik.recommend.log.Access;
import com.velik.recommend.log.AccessLogReader;

public class MassageLog {

	public static class ArticleCounter implements Comparable<ArticleCounter> {
		private int minor;

		public ArticleCounter(int minor) {
			this.minor = minor;
		}

		int count;

		@Override
		public int compareTo(ArticleCounter o) {
			return count - o.count;
		}

		public String toString() {
			return "minor " + minor + ", " + count;
		}
	}

	public static class UserCounter implements Comparable<UserCounter> {
		private long id;

		public UserCounter(long id) {
			this.id = id;
		}

		int count;

		@Override
		public int compareTo(UserCounter o) {
			return count - o.count;
		}

		public String toString() {
			return "user " + id + ", " + count;
		}
	}

	public static void main(String[] args) throws IOException {
		Map<Integer, List<Long>> usersByMinor = new HashMap<Integer, List<Long>>();

		HashSet<Integer> articles = (HashSet<Integer>) load("articles.set");

		for (File file : new File("data").listFiles()) {
			System.out.println(file);
			if (!file.getName().endsWith(".log")) {
				continue;
			}

			for (Access access : new AccessLogReader(file)) {
				if (access.getMajorId() != 1 || !articles.contains(access.getMinorId())) {
					continue;
				}

				List<Long> users = usersByMinor.get(access.getMinorId());

				if (users == null) {
					users = new ArrayList<Long>();

					usersByMinor.put(access.getMinorId(), users);
				}

				int i = Collections.binarySearch(users, access.getUserId());

				if (i < 0) {
					users.add(-1 - i, access.getUserId());
				}
			}
		}

		store(toEnergyMatrix(usersByMinor), "energy-matrix.ser");
	}

	private static Stresses toEnergyMatrix(Map<Integer, List<Long>> usersByMinor) {
		return null;
	}

	public static void getDistributions(String[] args) throws IOException {
		Map<Integer, ArticleCounter> accessByMinor = new HashMap<Integer, ArticleCounter>(10000);
		Map<Long, UserCounter> accessByUser = new HashMap<Long, UserCounter>(10000);

		Set<Long> users = new HashSet<Long>(10000);
		long total = 0;

		for (File file : new File("data").listFiles()) {
			System.out.println(file);
			if (!file.getName().endsWith(".log")) {
				continue;
			}

			for (Access access : new AccessLogReader(file)) {
				if (access.getMajorId() != 1) {
					continue;
				}

				total++;
				users.add(access.getUserId());

				{
					ArticleCounter counter = accessByMinor.get(access.getMinorId());

					if (counter == null) {
						counter = new ArticleCounter(access.getMinorId());

						accessByMinor.put(access.getMinorId(), counter);
					}

					counter.count++;
				}
				/*
				 * UserCounter counter = accessByUser.get(access.getUserId());
				 * 
				 * if (counter == null) { counter = new
				 * UserCounter(access.getUserId());
				 * 
				 * accessByUser.put(access.getUserId(), counter); }
				 * 
				 * counter.count++;
				 */
			}
		}

		System.out.println("Unique users:" + users.size());
		System.out.println("total counts: " + total);

		{
			Distribution<ArticleCounter> distribution = new Distribution<ArticleCounter>();

			for (ArticleCounter counter : accessByMinor.values()) {
				distribution.add(counter);
			}

			distribution.print(99, 100, 50);

			HashSet<Integer> articles = new HashSet<Integer>(2048);

			for (int i = distribution.getValues().size() - 2048; i < distribution.getValues().size(); i++) {
				articles.add(distribution.getValues().get(i).minor);
			}

			System.out.println(articles.size());

			store(articles, "articles.ser");
		}
		/*
		 * { Distribution<UserCounter> distribution = new
		 * Distribution<UserCounter>();
		 * 
		 * for (UserCounter counter : accessByUser.values()) {
		 * distribution.add(counter); }
		 * 
		 * distribution.print(95, 100, 300); }
		 */
	}

	private static void store(Object articles, String fileName) throws IOException {
		ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File(fileName)));

		os.writeObject(articles);

		os.close();
	}

	private static Object load(String fileName) throws IOException {
		ObjectInputStream is = new ObjectInputStream(new FileInputStream(fileName));

		try {
			return is.readObject();
		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		} finally {
			is.close();
		}
	}
}
