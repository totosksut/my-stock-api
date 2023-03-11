package com.example.stock.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.stock.bu.StockFastBu;

@RestController
public class StockController {
	
	private static final Logger logger = LoggerFactory.getLogger(StockController.class);
	
	
	@Autowired
	StockFastBu stkFastBu;

	@RequestMapping(value = "/checkStockSingle" , method= RequestMethod.POST)
	public ResponseEntity<Object> checkStockSingle(){
		logger.info("==========start checkStockSingle Service============");
		try {
			stkFastBu.checkStock();
			stkFastBu.checkStockMulti();
			return new ResponseEntity<Object>("OK", HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<Object>("ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			logger.info("==========end checkStockSingle Service============");
		}
	}
	
//	@RequestMapping(value = "/checkStockMulti" , method= RequestMethod.POST)
//	public ResponseEntity<Object> checkStockMulti(){
//		logger.info("==========start checkStockMulti Service============");
//		try {
//			stkFastBu.checkStockMulti();
//			return new ResponseEntity<Object>("OK", HttpStatus.OK);
//		}catch(Exception e) {
//			return new ResponseEntity<Object>("ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
//		}finally {
//			logger.info("==========end checkStockMulti Service============");
//		}
//	}
	
	@RequestMapping(value = "/createTable" , method= RequestMethod.POST)
	public ResponseEntity<Object> createTable(){
		logger.info("==========start createTable Service ============");
		try {
			stkFastBu.createTable();
			return new ResponseEntity<Object>("OK", HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<Object>("ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			logger.info("==========end createTable Service============");
		}
	}
	
	@RequestMapping(value = "/cleanCache" , method= RequestMethod.POST)
	public ResponseEntity<Object> cleanCache(){
		logger.info("==========start cleanCache Service ============");
		try {
			stkFastBu.cleanCache();
			return new ResponseEntity<Object>("OK", HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<Object>("ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			logger.info("==========end cleanCache Service============");
		}
	}
	
//	@RequestMapping(value = "/testQuery" , method= RequestMethod.POST)
//	public ResponseEntity<Object> testQuery(){
//		logger.info("==========start testQuery Service============");
//		try {
//			stkFastBu.testQuery();
//			return new ResponseEntity<Object>("OK", HttpStatus.OK);
//		}catch(Exception e) {
//			return new ResponseEntity<Object>("ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
//		}finally {
//			logger.info("==========end testQuery Service============");
//		}
//	}
}
