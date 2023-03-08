package com.example.stock.util;

import java.sql.Connection;
import java.sql.DriverManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionUtils {

	
	private static final Logger logger = LoggerFactory.getLogger(ConnectionUtils.class);

	public static Connection getConnection(String path) throws Exception {
		
		Connection conn = null;
		
		try {

			conn = DriverManager.getConnection("jdbc:dbschemaTNP:dbf:" + path);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error ConnectionUtils.getConnection --> "+e.getMessage());
			throw e;
		}
		return conn;
	}

	
}
