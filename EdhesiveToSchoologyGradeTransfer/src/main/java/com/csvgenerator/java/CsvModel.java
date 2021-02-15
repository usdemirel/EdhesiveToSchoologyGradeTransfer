package com.csvgenerator.java;

import java.util.ArrayList;
import java.util.List;

public class CsvModel {
	private List<List<String>> result;

	public CsvModel() {
		this.result = new ArrayList<List<String>>();
	}
	
	public int getNumberOfAssignments() {
		return this.result.get(0).size();
	}
	
	public int getNumberOfStudents() {
		return this.result.size()-1;
	}

	public List<List<String>> getResult() {
		return result;
	}

	public void setResult(List<List<String>> result) {
		this.result = result;
	}
	
}
