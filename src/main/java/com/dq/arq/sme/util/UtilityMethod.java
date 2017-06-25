package com.dq.arq.sme.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.ads.adwords.axis.v201607.cm.ApiException;



public class UtilityMethod {
	final static Logger logger = LoggerFactory.getLogger(UtilityMethod.class);
	public static String getServerPath(HttpServletRequest request) {
		logger.debug("\n\n\n*************** Entering getServerPath method of UtilityMethod ***************\n\n\n");
		String serverName = request.getServerName();
		String contextPath = request.getContextPath();
		logger.debug("\n\n\n+++++++++++++++ INFO:: Server details +++++++++++++++\n"
				+ "serverName: "+serverName+"\n"
				+ "contextPath: "+contextPath+"\n"
				+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
		if (serverName.equalsIgnoreCase("localhost")||serverName.equalsIgnoreCase("127.0.0.1")||serverName.equalsIgnoreCase("0:0:0:0:0:0:0:1")) {
			serverName = "http://localhost:8080" + contextPath;
		}
		else if(serverName.equalsIgnoreCase("144.76.127.52"))
		{
			serverName = "http://144.76.127.52:8081" + contextPath;	
		}
		else if(serverName.equalsIgnoreCase("sme.arq.co.in"))
		{
			serverName = "http://" + serverName +contextPath;
		}
		logger.debug("\n\n\n############### Exiting getServerPath method of UtilityMethod ###############\n\n\n");
		return serverName;
	}
	
	public static String getServerName(HttpServletRequest request) {
		logger.debug("\n\n\n*************** Entering getServerName method of UtilityMethod ***************\n\n\n");
		String serverName = request.getServerName();
		logger.debug("\n\n\n+++++++++++++++ INFO:: Server details +++++++++++++++\n"
				+ "serverName: "+serverName+"\n"
				+ "contextPath: "+request.getContextPath()+"\n"
				+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
		if (serverName.equalsIgnoreCase("localhost")||serverName.equalsIgnoreCase("127.0.0.1")||serverName.equalsIgnoreCase("0:0:0:0:0:0:0:1")) {
			serverName = "http://localhost:8080";
		}
		else if(serverName.equalsIgnoreCase("144.76.127.52"))
		{
			serverName = "http://144.76.127.52:8081";	
		}
		else if(serverName.equalsIgnoreCase("sme.arq.co.in"))
		{
			serverName = "http://" + serverName;
		}
		logger.debug("\n\n\n############### Exiting getServerName method of UtilityMethod ###############\n\n\n");
		return serverName+"/sme/";
	}
	

	
	public static Date convertStringYYYY_MM_DDTODateInJava(String date) {
		logger.debug("\n\n\n*************** Entering convertStringYYYY_MM_DDTODateInJava method of UtilityMethod ***************\n\n\n");
		Date datetoReturn = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			datetoReturn = formatter.parse(date);
			logger.debug("\n\n\n+++++++++++++++ INFO:: +++++++++++++++\n"
					+ "dateFrom: "+date+"\n"
					+ "dateTo: "+datetoReturn.toString()+"\n"
					+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		logger.debug("\n\n\n############### Exiting convertStringYYYY_MM_DDTODateInJava method of UtilityMethod ###############\n\n\n");
		return datetoReturn;
	}
	
	public static Date convertStringMM_DD_YYYYTODateInJava(String date) {
		logger.debug("\n\n\n*************** Entering convertStringMM_DD_YYYYTODateInJava method of UtilityMethod ***************\n\n\n");
		Date datetoReturn = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		try {
			
			datetoReturn = formatter.parse(date);
			logger.debug("\n\n\n+++++++++++++++ INFO:: +++++++++++++++\n"
					+ "dateFrom: "+date+"\n"
					+ "dateTo: "+datetoReturn.toString()+"\n"
					+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		logger.debug("\n\n\n############### Exiting convertStringMM_DD_YYYYTODateInJava method of UtilityMethod ###############\n\n\n");
		return datetoReturn;
	}
	
	public static String formatDateTOYYYY_MM_DD(Date date) {
		logger.debug("\n\n\n*************** Entering formatDateTOYYYY_MM_DD method of UtilityMethod ***************\n\n\n");
		String newDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
		logger.debug("\n\n\n+++++++++++++++ INFO:: +++++++++++++++\n"
				+ "dateFrom: "+date+"\n"
				+ "dateTo: "+newDate+"\n"
				+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
		logger.debug("\n\n\n############### Exiting formatDateTOYYYY_MM_DD method of UtilityMethod ###############\n\n\n");
		return newDate;
	}
	
	public static String formatDateTOMM_DD_YYYY(Date date) {
		logger.debug("\n\n\n*************** Entering formatDateTOMM_DD_YYYY method of UtilityMethod ***************\n\n\n");
		String newDate = new SimpleDateFormat("MM/dd/yyyy").format(date);
		logger.debug("\n\n\n+++++++++++++++ INFO:: +++++++++++++++\n"
				+ "dateFrom: "+date+"\n"
				+ "dateTo: "+newDate+"\n"
				+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
		logger.debug("\n\n\n############### Exiting formatDateTOMM_DD_YYYY method of UtilityMethod ###############\n\n\n");
		return newDate;
	}
	
	public static String formatDateTOYYYYMMDD(Date date) {
		logger.debug("\n\n\n*************** Entering formatDateTOYYYYMMDD method of UtilityMethod ***************\n\n\n");
		String newDate = new SimpleDateFormat("yyyyMMdd").format(date);
		logger.debug("\n\n\n+++++++++++++++ INFO:: +++++++++++++++\n"
				+ "dateFrom: "+date+"\n"
				+ "dateTo: "+newDate+"\n"
				+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
		logger.debug("\n\n\n############### Exiting formatDateTOYYYYMMDD method of UtilityMethod ###############\n\n\n");
		return newDate;
	}
	
	public static String convertYYYY_MM_DDtoYYYYMMDD(String date)
	{
		logger.debug("\n\n\n*************** Entering formatDateTOYYYYMMDD method of UtilityMethod ***************\n\n\n");
		SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date tempDate = new Date();
		SimpleDateFormat toFormat = new SimpleDateFormat("yyyyMMdd");
		try {

			tempDate = fromFormat.parse(date);
			logger.debug("\n\n\n+++++++++++++++ INFO:: +++++++++++++++\n"
					+ "dateFrom: "+date+"\n"
					+ "dateTo: "+tempDate.toString()+"\n"
					+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		logger.debug("\n\n\n############### Exiting convertYYYY_MM_DDtoYYYYMMDD method of UtilityMethod ###############\n\n\n");
		return toFormat.format(tempDate);
	}
	
	public static String convertYYYYMMDDtoYYYY_MM_DD(String date)
	{
		logger.debug("\n\n\n*************** Entering convertYYYYMMDDtoYYYY_MM_DD method of UtilityMethod ***************\n\n\n");
		SimpleDateFormat fromFormat = new SimpleDateFormat("yyyyMMdd");
		Date tempDate = new Date();
		SimpleDateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			tempDate = fromFormat.parse(date);
			logger.debug("\n\n\n+++++++++++++++ INFO:: +++++++++++++++\n"
					+ "dateFrom: "+date+"\n"
					+ "dateTo: "+tempDate.toString()+"\n"
					+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		logger.debug("\n\n\n############### Exiting convertYYYYMMDDtoYYYY_MM_DD method of UtilityMethod ###############\n\n\n");
		return toFormat.format(tempDate);
	}


	public static String convertMM_DD_YYYYtoYYYY_MM_DD(String date) {
		logger.debug("\n\n\n*************** Entering convertMM_DD_YYYYtoYYYY_MM_DD method of UtilityMethod ***************\n\n\n");
		SimpleDateFormat fromFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date tempDate = new Date();
		SimpleDateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			tempDate = fromFormat.parse(date);
			logger.debug("\n\n\n+++++++++++++++ INFO:: +++++++++++++++\n"
					+ "dateFrom: "+date+"\n"
					+ "dateTo: "+tempDate.toString()+"\n"
					+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		logger.debug("\n\n\n############### Exiting convertMM_DD_YYYYtoYYYY_MM_DD method of UtilityMethod ###############\n\n\n");
		return toFormat.format(tempDate);
	}

	public static String convertYYYYMMDDtoMM_DD_YYYY(String date) {
		logger.debug("\n\n\n*************** Entering convertYYYYMMDDtoMM_DD_YYYY method of UtilityMethod ***************\n\n\n");
		SimpleDateFormat fromFormat = new SimpleDateFormat("yyyyMMdd");
		Date tempDate = new Date();
		SimpleDateFormat toFormat = new SimpleDateFormat("MM/dd/yyyy");
		try {
			
			tempDate = fromFormat.parse(date);
			logger.debug("\n\n\n+++++++++++++++ INFO:: +++++++++++++++\n"
					+ "dateFrom: "+date+"\n"
					+ "dateTo: "+tempDate.toString()+"\n"
					+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		logger.debug("\n\n\n############### Exiting convertYYYYMMDDtoMM_DD_YYYY method of UtilityMethod ###############\n\n\n");
		return toFormat.format(tempDate);
	}
	
	public static String getUniqueId() {
		logger.debug("\n\n\n*************** Entering getUniqueId method of UtilityMethod ***************\n\n\n");
		String UUIDUserToken = UUID.randomUUID().toString();
		UUIDUserToken = UUIDUserToken.replaceAll("-", "");
		UUIDUserToken = UUIDUserToken.substring(0, 15);
		logger.debug("\n\n\n+++++++++++++++ INFO:: +++++++++++++++\n"
				+ "uniqueId: "+UUIDUserToken+"\n"
				+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
		logger.debug("\n\n\n############### Exiting getUniqueId method of UtilityMethod ###############\n\n\n");
		return UUIDUserToken;
	}
	
	public static String capitalizeString(String str)
	{
		logger.debug("\n\n\n*************** Entering capitalizeString method of UtilityMethod ***************\n\n\n");
		String result="";
		if((!str.equalsIgnoreCase("")&&(str!=null)))
		{
			String strArray[] = str.split(" ");
			for(String s:strArray)
			{
				result+=s.substring(0,1).toUpperCase() +s.substring(1);
				result+=" ";
			}
			logger.debug("\n\n\n+++++++++++++++ INFO:: +++++++++++++++\n"
					+ "inputString: "+str+"\n"
					+ "outputString: "+result+"\n"
					+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
			logger.debug("\n\n\n############### Exiting capitalizeString method of UtilityMethod ###############\n\n\n");
			return result;
		}
		logger.debug("\n\n\n############### Exiting capitalizeString method of UtilityMethod ###############\n\n\n");
		return "";
	}
	
	public static String printStackTrace(Exception e) {
		String out = "";
		Object[] obj = e.getStackTrace();
		if(e instanceof ApiException)
		{
			ApiException apiException = (ApiException) e;
			out+=apiException.getFaultDetails()+"\n\n";
		}
		out+=e+"\n";
		for(Object o:obj) {
			out+=o.toString()+"\n";
		}
		return out;
	}
	
	public static String getErrorMessageFromProperties(String faultReason) throws IOException {
		URL url = UtilityMethod.class.getResource("/arqsme.properties");
		Properties systemProp = new Properties();
		systemProp.load(url.openStream());
		Set<Object> keys = systemProp.keySet();
		for(Object key: keys) {
			String k = (String)key;
			System.out.println(k+" ****** "+faultReason);
			if(faultReason.contains(k))
				return systemProp.getProperty((String) key);
		}
		return "Encountered a network error. Please contact admin";
}
	public static String getOsFromUserAgent(String userAgent)
	{
		userAgent =  userAgent.toLowerCase(); 
		String os = "";
			if (userAgent.indexOf("windows") >= 0 )
         {
             os = "Windows";
         } else if(userAgent.indexOf("mac") >= 0)
         {
             os = "Mac";
         } else if(userAgent.indexOf("x11") >= 0)
         {
             os = "Unix";
         } else if(userAgent.indexOf("android") >= 0)
         {
             os = "Android";
         } else if(userAgent.indexOf("iphone") >= 0)
         {
             os = "IPhone";
         }else{
             os = "UnKnown, More-Info: "+userAgent;
         }
			return os;
	}
	
	public static String getBrowserFromUserAgent(String userAgent)
	{
		userAgent =  userAgent.toLowerCase(); 
		String browser = "";
		 if (userAgent.contains("msie"))
	        {
	            browser="Internet Explorer";
	        } else if (userAgent.contains("safari") && userAgent.contains("version"))
	        {
	            browser="Safari";
	        } else if ( userAgent.contains("opr") || userAgent.contains("opera"))
	        {
	            if(userAgent.contains("opera"))
	                browser="Opera";
	            else if(userAgent.contains("opr"))
	                browser="Opera";
	        } else if (userAgent.contains("chrome"))
	        {
	            browser="Chrome";
	        } else if ((userAgent.indexOf("mozilla/7.0") > -1) || (userAgent.indexOf("netscape6") != -1)  || (userAgent.indexOf("mozilla/4.7") != -1) || (userAgent.indexOf("mozilla/4.78") != -1) || (userAgent.indexOf("mozilla/4.08") != -1) || (userAgent.indexOf("mozilla/3") != -1) )
	        {
	            //browser=(userAgent.substring(userAgent.indexOf("MSIE")).split(" ")[0]).replace("/", "-");
	            browser = "Netscape";

	        } else if (userAgent.contains("firefox"))
	        {
	            browser="Firefox";
	        } else if(userAgent.contains("rv"))
	        {
	            browser="Internet Explorer";
	        } else
	        {
	            browser = "Generic Browser";
	        }
		 return browser;
	}
	public static boolean securedURL(String url) {
		logger.debug("\n\n\n************* Entering securedURL method of UtilityMethod *************\n\n\n");
		String str="";
		BufferedReader br = null;
		URL URLFile = UtilityMethod.class.getResource("/allowedURLs.txt");
		logger.debug("\n\n\n+++++++++++++++ INFO:: +++++++++++++++\n"
				+ "url: "+url+"\n"
				+ "fileName: "+"allowedURLs.txt"+"\n"
				+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
		try {
			br = new BufferedReader(new FileReader(URLFile.getFile()));
			
			while((str = br.readLine())!=null)
			{
				if(url.startsWith(str)) {
					logger.debug("\n\n\n############### Exiting getSecuredURL method of UtilityMethod ###############\n\n\n");
					return false;
				}
		}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(br!=null)
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		logger.debug("\n\n\n############### Exiting getSecuredURL method of UtilityMethod ###############\n\n\n");
		return true;
	}

}
