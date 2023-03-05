package com.example.stock.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.stock.model.StockModel;
import com.fasterxml.jackson.databind.ObjectMapper;


@Controller
public class MonitoringController {

	private static final Logger logger = LoggerFactory.getLogger(MonitoringController.class);
	
	@RequestMapping("/")
    public String home(Model model) {
		logger.info("==========start MonitoringController home============");
		logger.info("==========end MonitoringController home============");
        return "home";
    }
	
	@RequestMapping("/stock")
    public String stock(Model model) {
		logger.info("==========start MonitoringController stock============");
    	try {
    		StockModel obj = null;
    		ObjectMapper mapper = new ObjectMapper();
			obj = mapper.readValue(new File("C:/check_stock/stock.json"), StockModel.class);
			model.addAttribute("stockObj",obj);
    	}catch(Exception e) {
    		e.printStackTrace();
    		logger.error(e.getMessage());
    	}
    	logger.info("==========end MonitoringController stock============");
        return "stock";
    }
	
	@RequestMapping("/stock-multi")
    public String stockMulti(Model model) {
		logger.info("==========start MonitoringController stockMulti============");
		try {
    		StockModel obj = null;
    		ObjectMapper mapper = new ObjectMapper();
			obj = mapper.readValue(new File("C:/check_stock/stock.json"), StockModel.class);
			model.addAttribute("stockObj",obj);
    	}catch(Exception e) {
    		e.printStackTrace();
    		logger.error(e.getMessage());
    	}
    	logger.info("==========end MonitoringController stockMulti============");
        return "stock-multi";
    }
	
	@RequestMapping("/debtor-sale")
    public String debtorSale(Model model) {
		logger.info("==========start MonitoringController debtor-sale============");
    	try {
    		
    	}catch(Exception e) {
    		e.printStackTrace();
    		logger.error(e.getMessage());
    	}
    	logger.info("==========end MonitoringController debtor-sale============");
        return "debtor-sale";
    }
	
	@RequestMapping(value = "/saveStockData" , method= RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Object> saveStockData(@RequestBody StockModel input){
		logger.info("==========start MonitoringController saveStockData============");
		try {
			
			ObjectMapper Obj = new ObjectMapper(); 
			String jsonStr = Obj.writeValueAsString(input);  
			
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(jsonStr);
			
			//FileWriter file = new FileWriter("C:/check_stock/stock.json");
			Writer file = new PrintWriter("C:/check_stock/stock.json", "UTF-8");
			
	        file.write(jsonObject.toJSONString());
	        file.close();
			return new ResponseEntity<Object>("OK", HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<Object>("ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			logger.info("==========end MonitoringController saveStockData============");
		}
	}
}
