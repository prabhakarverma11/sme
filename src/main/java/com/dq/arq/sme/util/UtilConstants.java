package com.dq.arq.sme.util;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtilConstants {

	private static final Logger logger = LoggerFactory.getLogger(UtilConstants.class);


	
	
	public static String hostname;
	public static String port;
	public static String username;
	public static String password;
	public static String auth;

	public static String fromAddress;
	public static String toAddresses;
	public static String ccAddresses;
	public static String bccAddresses;
	
	public static int CAMPAIGNS_PER_PAGE;
	public static int ADGROUPS_PER_PAGE;
	public static int NO_OF_KEYWORDS;
	
	
	static {
		Properties systemProp = null;
		URL url = null;
		try {
			systemProp = new Properties();
			url = UtilConstants.class.getResource("/mail.properties");
			if (url == null) {
				logger.debug("Inside UtilConstants: mail.properties file not found !!");
			} else {
				logger.debug("Inside UtilConstants: mail.proerties found.");
			}
			systemProp.load(url.openStream());
			hostname = systemProp.getProperty("hostname");
			port = systemProp.getProperty("port");
			username = systemProp.getProperty("username");
			password = systemProp.getProperty("password");
			auth= systemProp.getProperty("auth");

			fromAddress = systemProp.getProperty("fromAddress");
			toAddresses = systemProp.getProperty("toAddresses");
			ccAddresses = systemProp.getProperty("ccAddresses");
			bccAddresses = systemProp.getProperty("bccAddresses");
			systemProp.clear();
			
			url=UtilConstants.class.getResource("/sme.properties");
			if (url == null) {
				logger.debug("Inside UtilConstants: sme.properties file not found !!");
			} else {
				logger.debug("Inside UtilConstants: sme.proerties found.");
			}
			systemProp.load(url.openStream());
			CAMPAIGNS_PER_PAGE = Integer.parseInt(systemProp.getProperty("CAMPAIGNS_PER_PAGE"));
			ADGROUPS_PER_PAGE = Integer.parseInt(systemProp.getProperty("ADGROUPS_PER_PAGE"));
			NO_OF_KEYWORDS = Integer.parseInt(systemProp.getProperty("NO_OF_KEYWORDS"));
		} catch (Exception e) {
			logger.debug("Inside UtilConstants: ERROR : " + e.toString());

		}

	}
	
}
