package com.dq.arq.sme.services;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.dq.arq.sme.util.UtilityMethod;

@ControllerAdvice
@EnableWebMvc
public class ExceptionControllerAdvice {

	private static final Logger logger = LoggerFactory
			.getLogger("LogTesting");
	
	@ExceptionHandler(Exception.class)
	public String exception(HttpServletRequest request,Exception e) {
		
		   //customize error message
    	logger.info("\n\n\n*************** Entering preHandle method of ExceptionControllerAdvice ***************\n\n\n");
        Throwable throwable = (Throwable) request
                .getAttribute("javax.servlet.error.exception");
        Integer statusCode = (Integer) request
                .getAttribute("javax.servlet.error.status_code");
        String servletName = (String) request
                .getAttribute("javax.servlet.error.servlet_name");
        if (servletName == null) {
            servletName = "Unknown";
        }
        String requestUri = (String) request
                .getAttribute("javax.servlet.error.request_uri");
        if (requestUri == null) {
            requestUri = "Unknown";
        }   
        if(statusCode!=null&&statusCode==404)
        	return "forward:/Welcome";
        
        logger.info("\n\n\n??????????????? ERROR:: Caught exception starts ???????????????\n\n");
        e.printStackTrace();
        logger.error("ex: ",e);
        logger.info("\n\n\n??????????????? ERROR:: Caught exception ends ???????????????\n\n");
        
        logger.info("\n\n\n*************** Exiting preHandle method of ExceptionControllerAdvice ***************\n\n\n");
        return "forward:/listcampaign?error=1";
	}
}
