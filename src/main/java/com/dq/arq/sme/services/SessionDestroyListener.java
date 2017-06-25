package com.dq.arq.sme.services;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;
import com.dq.arq.sme.domain.UserSessionDo;


public class SessionDestroyListener implements ApplicationListener<HttpSessionDestroyedEvent>{

	private static final Logger logger = LoggerFactory.getLogger("LogTesting");
	
	@Autowired
	UserService userService;
	
	@Autowired UserSessionService userSessionService;

    public void onApplicationEvent(HttpSessionDestroyedEvent httpSessionDestroyedEvent)
    {
    	
    	
//    		System.out.println("----Session Destroyed----");
    		for (SecurityContext securityContext : httpSessionDestroyedEvent.getSecurityContexts())
            {
    			HttpSession session = ((HttpSessionDestroyedEvent) httpSessionDestroyedEvent).getSession();
				UserSessionDo userSessionDo = userSessionService.getUserSessionDoBySessionId(session.getId());
				if(userSessionDo!=null)
				userSessionDo.setLoggedOutTime(new Date());
				userSessionDo.setIsLoggedIn(0);
				userSessionService.updateUserSessionDo(userSessionDo);
				logger.info("\n\n\n+++++++++++++++ SUCCESS:: User Session Destroyed with details: +++++++++++++++\n"
						+ "id: "+userSessionDo.getId()+"\n"
						+ "sessionId: "+userSessionDo.getSessionId()+"\n"
						+ "userId: "+userSessionDo.getUserDo().getId()+"\n"
						+ "isLoggedIn: "+userSessionDo.getIsLoggedIn()+"\n"
						+ "Device Details: "+userSessionDo.getDeviceDetails()+"\n"
						+ "IP: "+userSessionDo.getIp()+"\n"
						+ "LoggedIn Time: "+userSessionDo.getLoggedInTime()+"\n"
						+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
	    		String sessionId = session.getId();
//	    		System.out.println("sessionId: "+sessionId);
            }
    		

    	}


    	
    	//UserService userService = (UserService) appContext.getBean("userServiceImpl");
    	
    	
//    	if (event instanceof HttpSessionDestroyedEvent) {
//        	System.out.println("Session Destroyed"+event);
//        	for (SecurityContext securityContext : event.getSecurityContexts())
//             {
//        	Authentication auth = securityContext.getAuthentication();
//        	User user = (User) auth.getPrincipal();
//        	
//			UserDo userDo=userService.getUserDoByEmail(user.getUsername());
//			System.out.println("inside inside security listener"+userDo.getEmail());
//        	}
//    }
    	
//        for (SecurityContext securityContext : event.getSecurityContexts())
//        {
//            Authentication auth = securityContext.getAuthentication();
//            User user = (User) auth.getPrincipal();
//			UserDo userDo=userService.getUserDoByEmail(user.getUsername());
//			System.out.println("inside inside security listener");
//        }
    

}

