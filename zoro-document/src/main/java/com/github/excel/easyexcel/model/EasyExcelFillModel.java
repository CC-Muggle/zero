package com.github.excel.easyexcel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EasyExcelFillModel {

	private String name;

	private String className;

	private Result result;


	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Result {

		private String discipline;

		private String score;


	}

}
