package com.example.stock.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConstantUtils {
			
	public static String getPropertiesStock(String key) {
		String res = null;
		try (InputStream input = new FileInputStream("C:/check_stock/appconfig.properties")) {

            Properties prop = new Properties();

            prop.load(input);

            // get the property value and print it out
            res = prop.getProperty(key);

        } catch (IOException io) {
            io.printStackTrace();
        } catch (Exception e) {
        	e.printStackTrace();
        }
		
		return res;
	}
	
	public static String getPropertiesDebtor(String key) {
		String res = null;
		try (InputStream input = new FileInputStream("C:/debtor/appconfig.properties")) {

            Properties prop = new Properties();

            prop.load(input);

            // get the property value and print it out
            res = prop.getProperty(key);

        } catch (IOException io) {
            io.printStackTrace();
        } catch (Exception e) {
        	e.printStackTrace();
        }
		
		return res;
	}
	
}
