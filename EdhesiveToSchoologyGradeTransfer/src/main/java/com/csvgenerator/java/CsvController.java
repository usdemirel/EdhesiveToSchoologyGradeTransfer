package com.csvgenerator.java;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CsvController {

	private CsvModel model;
	private Map<String, String[]> asgMap;
	private Map<Integer, Double> asgFactorMap;
	private Map<String, String> userMap;
	private List<Integer> cols;

	public CsvController(LocalDate start, LocalDate end, String fullPathToExportedEdhesiveFile, String classSelected) {
		System.out.println("constructor2 is called ################");
		this.model = new CsvModel();
		this.asgMap = new HashMap<String, String[]>();
		this.asgFactorMap = new HashMap<Integer, Double>();
		this.userMap = new HashMap<String, String>();
		this.cols = new ArrayList<Integer>();
		loadAssignmentMap(classSelected, start, end);
		loadUserMap(classSelected);
		loadEdhesiveGradebook(fullPathToExportedEdhesiveFile);
		writeCsvFile(classSelected);
	}

	private String getJarPath() {
		String rosterPath = System.getProperty("java.class.path").substring(0,
				System.getProperty("java.class.path").lastIndexOf("\\") + 1);
		String[] rosterPaths = rosterPath.split(";");
		rosterPath = rosterPaths[0].substring(0, rosterPaths[0].lastIndexOf("\\") + 1);
		System.out.println("--------------- path: " + rosterPath);
		return rosterPath;
	}

	private boolean isEndDateAfterOrEqualToStartDate(LocalDate start, LocalDate end) {
		return start.equals(end) || end.isAfter(start);
	}

	private void loadAssignmentMap(String classSelected, LocalDate startDate, LocalDate endDate) {
		String fullPath = getJarPath() + "edhesive-schoology-csv-files\\assignment-mapping-" + classSelected + ".csv";
		try {
			BufferedReader asgReader = new BufferedReader(new FileReader(fullPath));
			asgReader.readLine();
			String line = "";
			while ((line = asgReader.readLine()) != null) {

				List<String> values = new ArrayList<String>();
				Pattern pattern = Pattern.compile("(?:^|,)(?:\\s*\"([^\"]*)\"\\s*|([^,]*))(?=,|$)");
				Matcher matcher = pattern.matcher(line);
				while (matcher.find()) {
					String value = matcher.group(1);
					if (value == null)
						value = matcher.group(2).trim();
//					System.out.println(value);
					if (!value.equals(""))
						values.add(value);
				}
				if (values.size() >= 10 && !values.get(6).equalsIgnoreCase("ng")
						&& startDate.isBefore(LocalDate.parse(values.get(10)))
						&& endDate.isAfter(LocalDate.parse(values.get(10)))) {
					asgMap.put(values.get(1), new String[] { values.get(4), values.get(2) });
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadUserMap(String classSelected) {
		String fullPath = fullPath = getJarPath() + "edhesive-schoology-csv-files\\schoology-user-ids-" + classSelected
				+ ".csv";
		try {
			BufferedReader usrReader = new BufferedReader(new FileReader(fullPath));
			usrReader.readLine();
			String line = "";
			while ((line = usrReader.readLine()) != null) {
				String[] asgArr = line.split(",");
				userMap.put(asgArr[3], asgArr[0]);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadEdhesiveGradebook(String fullPath) {
		try {
			BufferedReader gradebookReader = new BufferedReader(new FileReader(fullPath));
			String line = gradebookReader.readLine();

			List<String> values = new ArrayList<String>();
			Pattern pattern = Pattern.compile("(?:^|,)(?:\\s*\"([^\"]*)\"\\s*|([^,]*))(?=,|$)");
			Matcher matcher = pattern.matcher(line);
			while (matcher.find()) {
				String value = matcher.group(1);
				if (value == null)
					value = matcher.group(2).trim();
				if (!value.equals(""))
					values.add(value);
			}
			cols.add(3);
			model.getResult().add(new ArrayList<String>());
			model.getResult().get(model.getResult().size() - 1).add("SIS Login ID");

			for (int i = 4; i < values.size(); i++) {

				for (int col : cols) {
//					System.out.print(col + ", ");
				}

				if (asgMap.containsKey(values.get(i))) {
					cols.add(i);
					model.getResult().get(model.getResult().size() - 1).add(asgMap.get(values.get(i))[0]);
					asgFactorMap.put(i, Double.parseDouble(asgMap.get(values.get(i))[1]));
				}
			}

			String[] firstArr;
			while ((line = gradebookReader.readLine()) != null) {
				String[] asgArr = line.split(",");
				firstArr = line.split(",");

				if (userMap.containsKey(firstArr[3])) {
					model.getResult().add(new ArrayList<String>());
					for (int col : cols) {
						if (firstArr[col].length() != 0 && Character.isDigit(firstArr[col].charAt(0)))
							model.getResult().get(model.getResult().size() - 1).add(String
									.valueOf(Math.round(asgFactorMap.get(col) * Double.parseDouble(firstArr[col]))));
						else
							model.getResult().get(model.getResult().size() - 1)
									.add(userMap.getOrDefault(firstArr[col], ""));
					}
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeCsvFile(String classSelected) {
		String fullPath = getJarPath() + classSelected + LocalTime.now().toSecondOfDay() + " at "
				+ LocalTime.now().getHour() + "-" + LocalTime.now().getMinute() + ".csv";
		System.out.println("Full Path: " + fullPath);
		FileWriter writer = null;
		try {
			writer = new FileWriter(fullPath);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < model.getResult().size(); i++) {
			String collect = model.getResult().get(i).stream().collect(Collectors.joining(","));
			try {
				writer.append(collect);
				writer.append("\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
