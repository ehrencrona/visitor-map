package com.velik.recommend.map.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.velik.recommend.map.ArticleInfo;
import com.velik.recommend.map.StressMap;
import com.velik.recommend.map.StressMatrix;

public class MappedDepartmentMapPositionValue extends DepartmentMapPositionValue {
	private static final String OTHER = "Sonstiges";

	private static final Logger LOGGER = Logger.getLogger(MappedDepartmentMapPositionValue.class.getName());

	private static Map<String, String> departmentMapping;

	private String zoomInOnDepartment;

	public MappedDepartmentMapPositionValue(StressMap map, Map<Integer, ArticleInfo> articleInfos, StressMatrix matrix) {
		this(map, articleInfos, matrix, null);
	}

	public MappedDepartmentMapPositionValue(StressMap map, Map<Integer, ArticleInfo> articleInfos, StressMatrix matrix,
			String zoomInOnDepartment) {
		super(map, articleInfos, matrix, false);

		this.zoomInOnDepartment = zoomInOnDepartment;
	}

	protected synchronized void initialize() {
		indexByName.put(OTHER, 0);
		super.initialize();
	}

	static {
		readMapping();
	}

	@Override
	protected String getDepartment(ArticleInfo info) {
		String result = super.getDepartment(info);

		String mappedResult = departmentMapping.get(result);

		if (mappedResult == null) {
			LOGGER.log(Level.WARNING, "Found no mapping for " + result + ".");
			mappedResult = result;
		}

		String[] path = mappedResult.split("/");

		if (zoomInOnDepartment != null) {
			if (path[0].equalsIgnoreCase(zoomInOnDepartment)) {
				return mappedResult;
			} else {
				return OTHER;
			}
		} else {
			return path[0];
		}
	}

	private static synchronized void readMapping() {
		if (departmentMapping != null) {
			return;
		}

		departmentMapping = new HashMap<String, String>();

		File file = new File("department-mapping.txt");

		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));

			String line;

			while ((line = reader.readLine()) != null) {
				String[] comp = line.split("->");

				if (comp.length != 2) {
					System.out.println("Could not parse " + line + " in department mapping.");
				} else {
					departmentMapping.put(comp[0], comp[1]);
				}
			}

			reader.close();
		} catch (FileNotFoundException e) {
			LOGGER.log(Level.WARNING, "While reading department mapping from " + file + ": " + e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "While reading department mapping from " + file + ": " + e.getMessage(), e);
		}

	}
}
