package com.dq.arq.sme.util;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;

public class AuthenticationFailureHandlerImpl  implements AuthenticationFailureHandler{

	final static Logger logger = LoggerFactory.getLogger(AuthenticationFailureHandlerImpl.class);
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest arg0,
			HttpServletResponse response, AuthenticationException authenticationException)
			throws IOException, ServletException {
		
		logger.info("\n\n\n*************** Inside onAuthenticationFailure method of AuthenticationFailureHandlerImpl ***************\n\n\n");
			if(authenticationException instanceof BadCredentialsException)
			response.sendRedirect("Welcome?msg=invalidup");
		else if(authenticationException instanceof  SessionAuthenticationException)
		{
			response.sendRedirect("Welcome?msg=ssession");
		}
		
		logger.info("\n\n\n*************** Exiting from onAuthenticationFailure method of AuthenticationFailureHandlerImpl ***************\n\n\n");
		
		
	}
	 

}
