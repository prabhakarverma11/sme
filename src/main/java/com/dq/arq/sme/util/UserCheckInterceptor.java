package com.dq.arq.sme.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class UserCheckInterceptor implements HandlerInterceptor{

	private static final Logger logger = LoggerFactory
			.getLogger(UserCheckInterceptor.class);
	
	
	public boolean preHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler)
	    throws Exception {
		
		logger.debug("\n\n\n************* Entering preHandle method of UserCheckInterceptor *************\n\n\n");
		logger.debug("Requested Path: "+request.getRequestURI().substring(request.getContextPath().toString().length()));
		
		HttpSession session = request.getSession();
		Integer inSelectSession = (Integer)session.getAttribute("inSelectSession");
		if(inSelectSession!=null)
		{
			if(inSelectSession==1&&(!request.getRequestURI().equals("/sme/destroySession")&&!request.getRequestURI().equals("/sme/selectSession")))
			{
				response.sendRedirect(UtilityMethod.getServerPath(request)+"/selectSession");
			}
		}
		
		boolean isSecuredURL=UtilityMethod.securedURL(request.getRequestURI().substring(request.getContextPath().toString().length()));
		if(!isSecuredURL) {
			logger.debug("\n\n\n############### Exiting preHandle method of UserCheckInterceptor(url is not secured) ###############\n\n\n");			
			return true;
		}
		
		if(!(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User))
        {
        	response.sendRedirect(UtilityMethod.getServerPath(request)+"/Welcome");
        	logger.debug("\n\n\n############### Exiting preHandle method of UserCheckInterceptor(anonymous user found) ###############\n\n\n");
        	return false;
        }
	 	logger.debug("\n\n\n############### Exiting preHandle method of UserCheckInterceptor ###############\n\n\n");
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2, ModelAndView arg3) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	
}