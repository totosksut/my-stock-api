package com.example.stock.bu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.stock.config.H2Connection;
import com.example.stock.dao.StockDAO;
import com.example.stock.model.DebtorModel;
import com.example.stock.model.StockModel;
import com.example.stock.util.ConnectionUtils;
import com.example.stock.util.EmailUtils;
import com.example.stock.util.FilesUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;

@Service
public class StockFastBu {

	private static final Logger logger = LoggerFactory.getLogger(StockFastBu.class);

	@Autowired
	StockDAO stkDao;

	public StockModel getObjStock() throws Exception {

		StockModel obj = null;

		logger.info("============Start StockBU.getObjStock============");
		try {

			ObjectMapper mapper = new ObjectMapper();
			obj = mapper.readValue(new File("C:/check_stock/stock.json"), StockModel.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error StockBU.getObjStock : " + e.getMessage());
			throw e;
		} finally {
			logger.info("============End StockBU.getObjStock============");
		}

		return obj;
	}

	public DebtorModel getObjDebSales() throws Exception {

		DebtorModel obj = null;

		logger.info("============Start StockBU.getObjDebSales============");
		try {

			ObjectMapper mapper = new ObjectMapper();
			obj = mapper.readValue(new File("C:/debtor/debtor.json"), DebtorModel.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error StockBU.getObjDebSales : " + e.getMessage());
			throw e;
		} finally {
			logger.info("============End StockBU.getObjDebSales============");
		}

		return obj;
	}

	public void createTable() throws Exception {
		logger.info("============Start StockBU.createTable============");
		try {

			// get obj stock
			StockModel stockObj = getObjStock();

			// get obj debtor sales
			DebtorModel debSalesObj = getObjDebSales();

			String pathSP = stockObj.getDbfPathSP() + "STCRD.dbf";
			String pathMC = stockObj.getDbfPathMC() + "STCRD.dbf";
			String pathSF = stockObj.getDbfPathSF() + "STCRD.dbf";
			String pathAP = stockObj.getDbfPathAP() + "STCRD.dbf";

			String pathARMASSP = debSalesObj.getDbfPathCustSP() + "ARMAS.dbf";
			String pathARMASMC = debSalesObj.getDbfPathCustMC() + "ARMAS.dbf";
			String pathARMASSF = debSalesObj.getDbfPathCustSF() + "ARMAS.dbf";
			String pathARMASAP = debSalesObj.getDbfPathCustAP() + "ARMAS.dbf";

			String pathARTRNSP = debSalesObj.getDbfPathCustSP() + "ARTRN.dbf";
			String pathARTRNMC = debSalesObj.getDbfPathCustMC() + "ARTRN.dbf";
			String pathARTRNSF = debSalesObj.getDbfPathCustSF() + "ARTRN.dbf";
			String pathARTRNAP = debSalesObj.getDbfPathCustAP() + "ARTRN.dbf";

			FilesUtils.copyFileARMAS(pathARMASSP, "SP", debSalesObj.getDbfPathCustSP());
			FilesUtils.copyFileARMAS(pathARMASMC, "MC", debSalesObj.getDbfPathCustMC());
			FilesUtils.copyFileARMAS(pathARMASSF, "SF", debSalesObj.getDbfPathCustSF());
			FilesUtils.copyFileARMAS(pathARMASAP, "AP", debSalesObj.getDbfPathCustAP());

			FilesUtils.copyFileARTRN(pathARTRNSP, "SP", debSalesObj.getDbfPathCustSP());
			FilesUtils.copyFileARTRN(pathARTRNMC, "MC", debSalesObj.getDbfPathCustMC());
			FilesUtils.copyFileARTRN(pathARTRNSF, "SF", debSalesObj.getDbfPathCustSF());
			FilesUtils.copyFileARTRN(pathARTRNAP, "AP", debSalesObj.getDbfPathCustAP());

			FilesUtils.copyFile(pathSP, "SP", stockObj.getDbfPath());
			FilesUtils.copyFile(pathMC, "MC", stockObj.getDbfPath());
			FilesUtils.copyFile(pathSF, "SF", stockObj.getDbfPath());
			FilesUtils.copyFile(pathAP, "AP", stockObj.getDbfPath());

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error StockBU.createTable : " + e.getMessage());
			throw e;
		} finally {
			logger.info("============End StockBU.createTable============");
		}
	}
	
	public void testQuery() throws Exception {
		logger.info("============Start StockBU.testQuery============");
		// get obj stock
		StockModel stockObj = getObjStock();
		String path = stockObj.getDbfPath();

		ConnectionUtils conUtils = new ConnectionUtils();
				
		cleanCache();

		try (Connection conn = conUtils.getConnection(path)){
			Integer count = stkDao.countData(conn);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error StockBU.testQuery : " + e.getMessage());
			throw e;
		} finally {
			
			logger.info("============End StockBU.testQuery============");

		}
	}

	@SuppressWarnings("resource")
	public void checkStock() throws Exception {

		logger.info("============Start StockBU.checkStock============");
		
		// get obj stock
		StockModel stockObj = getObjStock();
		String path = stockObj.getDbfPath();

		ConnectionUtils conUtils = new ConnectionUtils();
		
		cleanCache();

		try (Connection conn = conUtils.getConnection(path)){
			
			stkDao.createIndexTable(conn);

			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			String strDate = sdf.format(cal.getTime());

			String pathSafetyStock = stockObj.getSafetyStockPath() + "code_safety_stock.xlsx";
			String pathResult = stockObj.getExcelPath() + "result.xlsx";


			try (InputStream is = new FileInputStream(pathSafetyStock);
					ReadableWorkbook wb = new ReadableWorkbook(is)) {
				
				OutputStream os = new FileOutputStream(pathResult);
				Workbook wbOut = new Workbook(os, "DemoExcel", "1.0");
				

				wb.getSheets().forEach(sheet -> {
					try (Stream<Row> rows = sheet.openStream()) {

						try{

							logger.info("sheet name : "+sheet.getName());
							Worksheet ws = wbOut.newWorksheet(sheet.getName());

							ws.range(0, 6, 0, 9).merge();
							ws.range(0, 6, 0, 9).style().horizontalAlignment("center").bold().fontSize(18).set();
							ws.value(0, 6, "Check Stock Report");
							
							ws.range(1, 6, 1, 9).merge();
							ws.range(1, 6, 1, 9).style().horizontalAlignment("center").bold().fontSize(11).set();
							ws.value(1, 6, strDate);
							
							ws.range(3, 0, 3, 7).merge();
							ws.range(3, 8, 3, 10).merge();
							ws.range(3, 11, 3, 13).merge();
							
							ws.range(3, 0, 3, 7).style().borderStyle("thin").horizontalAlignment("center").bold().set();
							ws.range(3, 8, 3, 10).style().borderStyle("thin").horizontalAlignment("center").bold().set();
							ws.range(3, 11, 3, 13).style().borderStyle("thin").horizontalAlignment("center").bold().set();
							
							ws.value(3, 0, "Name");
							ws.value(3, 8, "Remain");
							ws.value(3, 11, "Remain (คลังจอง)");
							
							rows.skip(1).forEach(r -> {
								
								int i = r.getRowNum()+2;
								Double remain01 = 0D;
								Double remain03 = 0D;

								String stkCode = r.getCellAsString(0).orElse("");
								String stkName = r.getCellAsString(1).orElse("");
								BigDecimal beg = new BigDecimal(r.getCellRawValue(9).orElse("0.00"));
								Double begDouble = beg.doubleValue();
								
								ws.range(i, 0, i, 7).merge();
								ws.range(i, 8, i, 10).merge();
								ws.range(i, 11, i, 13).merge();
								
								ws.range(i, 0, i, 7).style().borderStyle("thin").set();
								ws.range(i, 8, i, 10).style().borderStyle("thin").set();
								ws.range(i, 11, i, 13).style().borderStyle("thin").set();
								
								//logger.info(stkCode+ " "+ stkName);
								try {
									remain01 = stkDao.checkStockRemain0102(stkCode, conn);
									remain03 = stkDao.checkStockRemain03(stkCode, conn);
								} catch (Exception e) {
									logger.error(e.getMessage());
									throw new RuntimeException(e);
								}
								
								ws.value(i, 0, stkCode+ " "+ stkName);
								if(begDouble>=0) {
									ws.value(i, 8, remain01);
									ws.value(i, 11, remain03-remain01);
								}else {
									ws.value(i, 8, remain01+begDouble);
									ws.value(i, 11, (remain03-remain01)+begDouble);
								}

							});

						} catch (Exception e) {
							logger.error(e.getMessage());
							throw e; 
						}

					} catch (Exception e) {
						logger.error(e.getMessage());
						throw new RuntimeException(e);
					}
				});
				
				wbOut.finish();
				os.close();
			}
			
			
			EmailUtils.sendEmailStock(null);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error StockBU.checkStock : " + e.getMessage());
			throw e;
		} finally {

			logger.info("============End StockBU.checkStock============");

		}
	}
	
	@SuppressWarnings("resource")
	public void checkStockMulti() throws Exception {
		logger.info("============Start StockBU.checkStockMulti============");
		
		//get obj stock
		StockModel stockObj = getObjStock();
		String path = stockObj.getDbfPath();

		ConnectionUtils conUtils = new ConnectionUtils();
		
		cleanCache();
		
		try (Connection conn =  conUtils.getConnection(path)){
			
			stkDao.createIndexTable(conn);
			
			Calendar cal = Calendar.getInstance();
	        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	        String strDate = sdf.format(cal.getTime());
			
			//int k=1;
			

	        if(stockObj.getListFileStock()!=null) {
	        	for(String pathStr : stockObj.getListFileStock()) {
	        		String pathSafetyStock = pathStr;
	    			String pathResult = stockObj.getExcelMultiPath() + getFileName(pathStr);
	    			
	    			try (InputStream is = new FileInputStream(pathSafetyStock);
	    					ReadableWorkbook wb = new ReadableWorkbook(is)) {
	    				
	    				OutputStream os = new FileOutputStream(pathResult);
	    				Workbook wbOut = new Workbook(os, "DemoExcel", "1.0");
	    				

	    				wb.getSheets().forEach(sheet -> {
	    					try (Stream<Row> rows = sheet.openStream()) {

	    						try{

	    							logger.info("sheet name : "+sheet.getName());
	    							Worksheet ws = wbOut.newWorksheet(sheet.getName());

	    							ws.range(0, 6, 0, 9).merge();
	    							ws.range(0, 6, 0, 9).style().horizontalAlignment("center").bold().fontSize(18).set();
	    							ws.value(0, 6, "Check Stock Report");
	    							
	    							ws.range(1, 6, 1, 9).merge();
	    							ws.range(1, 6, 1, 9).style().horizontalAlignment("center").bold().fontSize(11).set();
	    							ws.value(1, 6, strDate);
	    							
	    							ws.range(3, 0, 3, 7).merge();
	    							ws.range(3, 8, 3, 10).merge();
	    							ws.range(3, 11, 3, 13).merge();
	    							
	    							ws.range(3, 0, 3, 7).style().borderStyle("thin").horizontalAlignment("center").bold().set();
	    							ws.range(3, 8, 3, 10).style().borderStyle("thin").horizontalAlignment("center").bold().set();
	    							ws.range(3, 11, 3, 13).style().borderStyle("thin").horizontalAlignment("center").bold().set();
	    							
	    							ws.value(3, 0, "Name");
	    							ws.value(3, 8, "Remain");
	    							ws.value(3, 11, "Remain (คลังจอง)");
	    							
	    							rows.skip(1).forEach(r -> {
	    								
	    								int i = r.getRowNum()+2;
	    								Double remain01 = 0D;
	    								Double remain03 = 0D;

	    								String stkCode = r.getCellAsString(0).orElse("");
	    								String stkName = r.getCellAsString(1).orElse("");
	    								BigDecimal beg = new BigDecimal(r.getCellRawValue(9).orElse("0.00"));
	    								Double begDouble = beg.doubleValue();
	    								
	    								ws.range(i, 0, i, 7).merge();
	    								ws.range(i, 8, i, 10).merge();
	    								ws.range(i, 11, i, 13).merge();
	    								
	    								ws.range(i, 0, i, 7).style().borderStyle("thin").set();
	    								ws.range(i, 8, i, 10).style().borderStyle("thin").set();
	    								ws.range(i, 11, i, 13).style().borderStyle("thin").set();
	    								
	    								//logger.info(stkCode+ " "+ stkName);
	    								try {
	    									remain01 = stkDao.checkStockRemain0102(stkCode, conn);
	    									remain03 = stkDao.checkStockRemain03(stkCode, conn);
	    								} catch (Exception e) {
	    									logger.error(e.getMessage());
	    									throw new RuntimeException(e);
	    								}
	    								
	    								ws.value(i, 0, stkCode+ " "+ stkName);
	    								if(begDouble>=0) {
	    									ws.value(i, 8, remain01);
	    									ws.value(i, 11, remain03-remain01);
	    								}else {
	    									ws.value(i, 8, remain01+begDouble);
	    									ws.value(i, 11, (remain03-remain01)+begDouble);
	    								}

	    							});

	    						} catch (Exception e) {
	    							logger.error(e.getMessage());
	    							throw e; 
	    						}

	    					} catch (Exception e) {
	    						logger.error(e.getMessage());
	    						throw new RuntimeException(e);
	    					}
	    				});
	    				
	    				wbOut.finish();
	    				os.close();
	    			}
	        	}
	        }
	        
	        
	        EmailUtils.sendEmailStockMulti(null);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error StockBU.checkStockMulti : " + e.getMessage());
			throw e;
		} finally {
			
			logger.info("============End StockBU.checkStockMulti============");

		}
		
	}
	
	public void cleanCache() throws Exception {
		
		logger.info("============Start StockBU.cleanCache============");
		try {
			
			StockModel stockObj = getObjStock();
			
			File directory = new File(stockObj.getPathH2());
	        FileUtils.cleanDirectory(directory);
	        H2Connection.isFirst = false;
			
		}catch(Exception e) {
			e.printStackTrace();
			logger.error("Error StockBU.cleanCache : " + e.getMessage());
			throw e;
		}finally {
			logger.info("============End StockBU.cleanCache============");
		}
	}
	
	public String getFileName(String fullPath) throws Exception{
		String result = "";
		try {
			int index = fullPath.lastIndexOf("/");
			result = fullPath.substring(index + 1);
		}catch(Exception e) {
			e.printStackTrace();
			logger.error("Error StockBU.cleanCache : " + e.getMessage());
			throw e;
		}
		return result;
	}
}
