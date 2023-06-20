package com.codinggyd.excel.example;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

import com.codinggyd.excel.constant.ExcelConst;
import com.codinggyd.excel.core.ExcelExporterUtils;
import com.codinggyd.excel.core.exportexcel.bean.SheetData;

import junit.framework.TestCase;
/**
 * 
 * <pre>
 * 类名:  TestXlsExcelExporter.java
 * 包名:  com.codinggyd.excel.example
 * 描述:  XLS格式的Excel生成方法测试
 * 
 * 作者:  guoyd
 * 日期:  2017年12月3日
 *
 * Copyright @ 2017 Corpration Name
 * </pre>
 */
public class TestXlsExcelExporter extends TestCase  {
	
//	测试ExcelExporterUtils#export(String format,Class<?> clazz,List<T> data) 
	public void testExporter1() throws Exception {
		long start = System.currentTimeMillis();

		String file = "D:/test.xls";
		String format = ExcelConst.EXCEL_FORMAT_XLS;
		List<User2> data = new ArrayList<User2>();
		for (int i=0;i<1000;i++) {
			User2 t = new User2();
			t.setAge(i);
			t.setName("测试"+i);
			t.setMoney(1d*i);
			t.setCreateTime(new Date());
			t.setTtt(Arrays.asList("a","b"+i));
			data.add(t);
		}
		//一行代码调用生成
 		Workbook wb = ExcelExporterUtils.export(User2.class, data, format); 
		
		FileOutputStream fos = new FileOutputStream(new File(file));
		wb.write(fos);
		fos.close();
		wb.close();
		
		System.out.println("导出数据量"+data.size()+"条,耗时"+(System.currentTimeMillis()-start)+"ms");

	}
	
//	测试ExcelExporterUtils#export(String format,Class<?> clazz,List<T> data,OutputStream outputStream) 
	public void testExporter2() throws Exception {
		long start = System.currentTimeMillis();

		String file = "D:/new.xls";
		FileOutputStream fos = new FileOutputStream(new File(file));
		String format = ExcelConst.EXCEL_FORMAT_XLS;
		List<User2> data = new ArrayList<User2>();
		for (int i=0;i<100;i++) {
			User2 t = new User2();
			t.setAge(i);
			t.setName("测试"+i);
			t.setMoney(1d*i);
			t.setCreateTime(new Date());
			data.add(t);
		}
		//一行代码调用生成
 		ExcelExporterUtils.export(User2.class, data, format,fos); 
		 
		System.out.println("导出数据量"+data.size()/10000+"万条,耗时"+(System.currentTimeMillis()-start)+"ms");

	}
	
	
//	测试ExcelExporterUtils#exportBatch(List<SheetData<T>> sheetDatas)
	@SuppressWarnings("rawtypes")
	public void testExporter3() throws Exception {
		long start = System.currentTimeMillis();


		String format = ExcelConst.EXCEL_FORMAT_XLS;
		
		List<User2> userData = new ArrayList<User2>();
		for (int i=0;i<10000;i++) {
			User2 t = new User2();
			t.setAge(i);
			t.setName("测试"+i);
			t.setMoney(1d*i);
			t.setCreateTime(new Date());
			userData.add(t);
		}
		
		List<Man2> manData = new ArrayList<Man2>();
		for (int i=0;i<10000;i++) {
			Man2 t = new Man2();
			t.setName("测试"+i);
			t.setValue(i+"");
			manData.add(t);
		}
		
		List<Position2> posData = new ArrayList<Position2>();
		for (int i=0;i<10000;i++) {
			Position2 t = new Position2();
			t.setName("1000"+i);
			t.setValue(i+"");
			posData.add(t);
		}
		
		
		SheetData<User2> userSheet = new SheetData<User2>(User2.class, userData, format);
		SheetData<Man2> manSheet = new SheetData<Man2>(Man2.class, manData, format);
		SheetData<Position2> posSheet = new SheetData<Position2>(Position2.class, posData, format);

		List<SheetData> multiSheets = new ArrayList<>(); 
		multiSheets.add(userSheet);
		multiSheets.add(manSheet);
		multiSheets.add(posSheet);

		//一行代码调用生成
		for (int i=0;i<1;i++) {
			String file = "D:/newbatch"+i+".xls";
			Workbook wb = ExcelExporterUtils.exportBatch(multiSheets,format); 
			FileOutputStream fos = new FileOutputStream(new File(file));
			wb.write(fos);
			fos.close();
			wb.close();
		}
		
		System.out.println("导出耗时"+(System.currentTimeMillis()-start)+"ms");

	}
	 
}

