package com.github.excel.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.github.excel.easyexcel.model.EasyExcelFillModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;

public class EasyExcelDemo {


	public static void main(String[] args) {
// 获取当前类加载器对象
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		String url = null;
		// 通过classloader获取指定路径下的文件流
		try (InputStream inputStream = classLoader.getResourceAsStream("template/easyexcel-template.xlsx");
		     FileOutputStream outputStream = new FileOutputStream(new File("D:/easyexcel.xlsx"));
		     ExcelWriter excelWriter = EasyExcel.write(outputStream).withTemplate(inputStream).build()) {

			WriteSheet writeSheet = EasyExcel.writerSheet("实时数据").build();

			Map<String, Object> map = new HashMap<String, Object>(){
				{
					put("shit", "屎");
					put("bullshit", "牛屎");
					put("holyshit", "神圣的屎");
				}
			};
			excelWriter.fill(map, writeSheet);
			excelWriter.fill(data(), writeSheet);



		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	public static List<EasyExcelFillModel> data(){
		List<EasyExcelFillModel> data = new ArrayList<>();
		for (int i = 0; i < 10; i++){
			data.add(new EasyExcelFillModel("张三", "三年二班", new EasyExcelFillModel.Result("语文","98")));
		}
		return data;
	}

}
