package com.velik.recommend.log;

import java.io.File;
import java.util.Iterator;

import com.velik.util.FetchingIterator;

public class AccessLogDirectoryReader implements Iterable<Access> {
	private File directory;
	private long accessId = 0;

	public AccessLogDirectoryReader(File directory) {
		this.directory = directory;
	}

	@Override
	public Iterator<Access> iterator() {
		final File[] files = directory.listFiles();

		return new FetchingIterator<Access>() {
			int atFile = 0;
			private Iterator<Access> delegate;

			@Override
			protected Access fetch() {
				if (delegate == null || !delegate.hasNext()) {
					while (atFile < files.length) {
						File file = files[atFile++];

						System.out.println(file);

						if (!file.getName().endsWith(".log")) {
							continue;
						}

						delegate = new AccessLogReader(file, accessId).iterator();

						break;
					}

					if (!delegate.hasNext()) {
						return null;
					}
				}

				// TODO not really correct; we are assuming the files are
				// produced on a single front and read chronologically. has to
				// be fixed.
				accessId++;

				return delegate.next();
			}
		};
	}
}
