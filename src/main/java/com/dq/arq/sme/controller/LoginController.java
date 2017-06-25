package com.dq.arq.sme.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.dq.arq.sme.domain.CampaignDo;
import com.dq.arq.sme.domain.UserDo;
import com.dq.arq.sme.domain.UserRoleDo;
import com.dq.arq.sme.domain.UserSessionDo;
import com.dq.arq.sme.services.CampaignService;
import com.dq.arq.sme.services.MailService;
import com.dq.arq.sme.services.SessionDestroyListener;
import com.dq.arq.sme.services.UserService;
import com.dq.arq.sme.services.UserSessionService;
import com.dq.arq.sme.util.UtilityMethod;

@Controller
public class LoginController {


	//private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	private static final Logger logger = LoggerFactory.getLogger("LogTesting");

	@Autowired
	UserService userService;
	@Autowired
	MailService mailService;
	@Autowired
	CampaignService campaignService;
	@Autowired
	UserSessionService userSessionService;
	
	@Autowired
	SessionDestroyListener applicationSecurityListener;
	
	@RequestMapping(value = "/userHome")
	public String getUserHome(Map<String, Object> map, HttpServletRequest request, HttpSession httpSession) throws IOException {
		logger.debug("\n\n\n*************** Entering getUserHome method of LoginController ***************\n\n\n");
		AuthenticationTrustResolver authTR = new AuthenticationTrustResolverImpl();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(authTR.isAnonymous(auth)) {
			logger.debug("\n\n\n############### Exiting getUserHome method of LoginController: 'User neither found in session nor user pkey is supplied' ###############\n\n\n");
			return "redirect:Welcome";
		}
		
		if (auth.isAuthenticated() && httpSession.getAttribute("sUser") == null) {
			User user = (User) auth.getPrincipal();
			UserDo userDo=userService.getUserDoByEmail(user.getUsername());
			
			if(!userDo.getIsVerified()) {
				logger.debug("\n\n\n############### Exiting getUserHome method of LoginController: 'User is not verified' ###############\n\n\n");
				SecurityContextHolder.getContext().setAuthentication(null);
				return "redirect:Welcome?msg=usernotverified&emailId="+userDo.getEmail();
			}
			
//Saving userSessionDo starts
			
			String userAgent = request.getHeader("User-Agent");
			String os = UtilityMethod.getOsFromUserAgent(userAgent);
			String browser = UtilityMethod.getBrowserFromUserAgent(userAgent);
			String ipAddress = request.getHeader("X-FORWARDED-FOR");
			if (ipAddress == null) {
				   ipAddress = request.getRemoteAddr();
			}
			
			
			UserSessionDo userSessionDo = new UserSessionDo();
			userSessionDo.setUserDo(userDo);
			userSessionDo.setIsLoggedIn(1);
			userSessionDo.setDeviceDetails(browser+" ("+os+")");
			userSessionDo.setIp(ipAddress);
			userSessionDo.setLoggedInTime(new Date(httpSession.getCreationTime()));
			userSessionDo.setSessionId(httpSession.getId());
			userSessionService.saveUserSessionDo(userSessionDo);
			logger.info("\n\n\n+++++++++++++++ SUCCESS:: User Session Created with details: +++++++++++++++\n"
					+ "id: "+userSessionDo.getId()+"\n"
					+ "sessionId: "+userSessionDo.getSessionId()+"\n"
					+ "userId: "+userSessionDo.getUserDo().getId()+"\n"
					+ "isLoggedIn: "+userSessionDo.getIsLoggedIn()+"\n"
					+ "Device Details: "+userSessionDo.getDeviceDetails()+"\n"
					+ "IP: "+userSessionDo.getIp()+"\n"
					+ "LoggedIn Time: "+userSessionDo.getLoggedInTime()+"\n"
					+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
			
			//Saving userSessionDo ends
			
			/*System.out.println(new Date(httpSession.getCreationTime()));
			System.out.println("Inside LoginController: Session created of user: "+user.getUsername());
			System.out.println("Login Controller: sessionId: "+httpSession.getId());*/

			UserRoleDo userRoleDo = userService.getUserRoleDoByUserDo(userDo);
			userDo.setName(UtilityMethod.capitalizeString(userDo.getName()));
			map.put("user", userDo);
			httpSession.setAttribute("sUser", userDo);
			httpSession.setAttribute("userRoleDo", userRoleDo);
			//  breadCrumbTrailUpdator.updateTrail("userhome", request);
			logger.info("\n\n\n+++++++++++++++ SUCCESS:: User with details: +++++++++++++++\n"
					+ "id: "+userDo.getId()+"\n"
					+ "name: "+userDo.getName()+"\n"
					+ "email: "+userDo.getEmail()+"\n"
					+ "role: "+userRoleDo.getRole()+" loggedin successfully.\n"
					+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
			
			int count=0;
			Map<UserDo,HttpSession> userSessions  = UserDo.getUsersSessions();
			
					for(Map.Entry<UserDo, HttpSession> entry:userSessions.entrySet())
					{
						if(userDo.getEmail().equals(entry.getKey().getEmail()))
						{
							count++;
							
						}
					}
			if(count>1)
				return "forward:/selectSession";
				
			return "forward:/userHome";
			//return "forward:/listcampaign";

		} else if (httpSession.getAttribute("sUser") != null) {

			UserDo userDo=(UserDo) httpSession.getAttribute("sUser");
			
			List<CampaignDo> campaignDos = campaignService.getCampaignDosListCreatedToday();
			map.put("newCampaignDos", campaignDos);
			
			campaignDos = campaignService.getCampaignDosListExpireToday();
			map.put("campaignDosExpireToday", campaignDos);
			
			
			map.put("user", userDo);
			// breadCrumbTrailUpdator.updateTrail("userhome", request);
			logger.debug("\n\n\n############### Exiting getUserHome method of LoginController: 'User found in session' ###############\n\n\n");
			if(userService.getUserRoleDoByUserDo(userDo).getRole().equals("ROLE_ADMIN"))
				return "homepage";
			else
				return "forward:/listcampaign";
		} else {
			logger.info("\n\n\n??????????????? ERROR:: User: "+((User)auth.getPrincipal()).getUsername()+" could not log in ???????????????\n\n\n");
			logger.debug("\n\n\n############### Exiting getUserHome method of LoginController: 'User neither found in session nor user pkey is supplied' ###############\n\n\n");
			return "redirect:Welcome";
		}
	}

	@RequestMapping(value="destroySession", method = RequestMethod.POST)
	public String destroySession(Map<String,Object> map,HttpServletRequest request,HttpServletResponse response,HttpSession httpSession)
	{
		logger.debug("\n\n\n*************** Entering destroySession method of LoginController ***************\n\n\n");
		httpSession.setAttribute("inSelectSession", 0);
		String sessionId = request.getParameter("sessionRadio");
		
		HttpSession session = null;
		Map<UserDo,HttpSession> userSessions  = UserDo.getUsersSessions();
		for(Map.Entry<UserDo,HttpSession> entry:userSessions.entrySet())
		{
			if(sessionId.equals(entry.getValue().getId()))
			{
				session = entry.getValue();
			}
		}
		session.invalidate();		
		logger.debug("\n\n\n*************** Exiting destroySession method of LoginController ***************\n\n\n");
		return "redirect:userHome";
	}
	
	
	@RequestMapping(value="selectSession")
	public String selectSession(Map<String,Object> map,HttpServletRequest request,HttpServletResponse response,HttpSession session)
	{
		logger.debug("\n\n\n*************** Entering selectSession method of LoginController ***************\n\n\n");
		
		UserDo userDo = (UserDo) request.getSession().getAttribute("sUser");
		Map<UserDo,HttpSession> userSessions  = UserDo.getUsersSessions();
		List<UserDo> userDos  = new ArrayList<UserDo>(); 
		List<String> httpSessions  = new ArrayList<String>();
		//List<HttpSession> httpSessionList = new ArrayList<HttpSession>();
		for(Map.Entry<UserDo,HttpSession> entry:userSessions.entrySet())
		{
			if(userDo.getEmail().equals(entry.getKey().getEmail()))
			{
				session.setAttribute("inSelectSession", 1);
				//HttpSession session = entry.getValue();
				userDos.add(entry.getKey());
				//httpSessionList.add(entry.getValue());
				httpSessions.add(entry.getValue().getId());
			}
		}
//		for(HttpSession httpSession: httpSessionList)
//		{
//			httpSession.invalidate();
//		}
		List<UserSessionDo> userSessionDos = userSessionService.getUserSessionDosByUserDosandSessionIds(userDos,httpSessions);
		map.put("userSessionDos", userSessionDos);
		
		
		logger.debug("\n\n\n*************** Exiting selectSession method of LoginController ***************\n\n\n");
		return "multilogin";
	}

	@RequestMapping(value =  { "/", "/Welcome" })
	public String renderWelcomePage(Map<String, Object> map,
			@RequestParam(value = "msg", required = false) String msg,
			@RequestParam(value = "emailId", required = false) String emailId,
			HttpServletRequest request,HttpServletResponse response,HttpSession session) {
		logger.debug("\n\n\n*************** Entering renderWelcomePage method of LoginController ***************\n\n\n");
		
		
		if((SecurityContextHolder.getContext().getAuthentication()!=null)&&(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User)
				  && !(msg!=null && msg.equals("usernotverified")))
			return "forward:/userHome";
		
		Integer error_code = (Integer) request.getAttribute("javax.servlet.error.status_code");
		if(error_code != null)
		{
			if(error_code == 404)
				map.put("error", "You tried to access a page that does not exist!!");
			else if(error_code == 403)
				map.put("error", "You tried to access a forbidden page!!");
			else if(error_code == 500)
				map.put("error", "Internal error caught on the page you accessed!!");
		}

		if(msg != null)
		{
			if (msg.equals("invalidup"))
				map.put("error", "Invalid username or password!");
			else if(msg.equals("loggedout"))
					{
						map.put("error", "You have logged out successfully!");
						logger.info("\n\n\n+++++++++++++++ SUCCESS:: You have logged out successfully +++++++++++++++\n\n\n");
					}
			else if( msg.equals("usernotverified"))
			{
				map.put("error", "Please verify your account to login!");
				map.put("emailId", emailId);
				map.put("password","");
			}
			else if(msg.equals("couldnotverify"))
				map.put("error", "Sorry !! Could not verify your account.");
			else if(msg.equals("ssession"))
				map.put("error", "You are already logged on from another browser");
		}
		logger.debug("\n\n\n############### Exiting renderWelcomePage method of LoginController: 'msg="+msg+",email="+emailId+"' ###############\n\n\n");
		return "login";
	}
	@RequestMapping(value = "/register")
	public String registerUser(ModelMap model,HttpServletRequest request) {
		logger.debug("\n\n\n*************** Entering registerUser method of LoginController ***************\n\n\n");
		UserDo userDo = new UserDo();
		model.put("userDo",userDo);
		logger.debug("\n\n\n############### Exiting registerUser method of LoginController ###############\n\n\n");
		return "register";
	}

	@RequestMapping(value = "/registernewuser", method = RequestMethod.POST)
	public String registerNewUser(ModelMap model, HttpServletRequest request, @ModelAttribute("userDo") UserDo userDo) {
		logger.debug("\n\n\n*************** Entering registerNewUser method of LoginController ***************\n\n\n");
		UserDo duplicateUserDo = userService.getUserDoByEmail(userDo.getEmail());
		if (duplicateUserDo != null) {
			logger.info("\n\n\n??????????????? ERROR:: User with email: "+userDo.getEmail()+" already exists ???????????????\n\n\n");
			model.put("msg", "User with email: '" + userDo.getEmail() + "' already exists");
			model.put("errorMsg", 0);
			model.put("userDo", userDo);
			logger.debug("\n\n\n############### Exiting registerNewUser method of LoginController ###############\n\n\n");
			return "register";
		}


		userDo.setCreatedBy("Self");
		userDo.setCreatedOn(new Date());
		userDo.setIsVerified(false);		
		Integer userId;
		try {
			userId = userService.saveUserDo(userDo);
			userService.createUserRole(userDo);
			logger.info("\n\n\n+++++++++++++++ SUCCESS:: User with details: +++++++++++++++\n"
					+ "id: "+userId+"\n"
					+ "name: "+userDo.getName()+"\n"
					+ "email: "+userDo.getEmail()+" saved successfully.\n"
					+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
			logger.debug("\n\n\n############### Exiting registerNewUser method of LoginController ###############\n\n\n");
			return "forward:/accountverification?emailId="+userDo.getEmail();
		} catch (Exception e) {
			logger.info("\n\n\n??????????????? ERROR:: User with details: ???????????????\n"
					+ "id: "+userDo.getId()+"\n"
					+ "name: "+userDo.getName()+"\n"
					+ "email: "+userDo.getEmail()+" could not save.\n"
					+ "exception: "+e.getMessage()+"\n"
					+"?????????????????????????????????????????????\n\n\n");
			model.put("msg", "Could not save User. Check log for errors");
			model.put("errorMsg", 0);
			e.printStackTrace();
			logger.debug("\n\n\n############### Exiting registerNewUser method of LoginController ###############\n\n\n");
			return "forward:/register";
		}
	}
	@RequestMapping(value="/verifyaccount")
	public String verifyAccount(@RequestParam(value = "user_id", required = true) int id,@RequestParam(value = "unique_id", required = true) String uniqueId) {
		logger.debug("\n\n\n*************** Entering verifyAccount method of LoginController ***************\n\n\n");
		UserDo userDo = userService.getUserDoById(id);
		if(userDo.getUniqueId().equals(uniqueId)) {			
			userDo.setIsVerified(true);
			userService.updateUser(userDo);
			logger.info("\n\n\n+++++++++++++++ SUCCESS:: User with details: +++++++++++++++\n"
					+ "id: "+id+"\n"
					+ "uniqueId: "+uniqueId+"\n"
					+ "name: "+userDo.getName()+"\n"
					+ "email: "+userDo.getEmail()+" is verified successfully.\n"
					+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
			logger.debug("\n\n\n############### Exiting verifyAccount method of LoginController ###############\n\n\n");
			return "verificationsuccessfull";
		}else {
			logger.info("\n\n\n??????????????? ERROR:: User with details: ???????????????\n"
					+ "id: "+id+"\n"
					+ "uniqueId: "+uniqueId+"\n"
					+ "name: "+userDo.getName()+"\n"
					+ "email: "+userDo.getEmail()+" could not verify.\n"
					+"?????????????????????????????????????????????\n\n\n");
			logger.debug("\n\n\n############### Exiting verifyAccount method of LoginController ###############\n\n\n");
			return "redirect:Welcome?msg=couldnotverify";
		}
	}

	@RequestMapping(value="/forgotpassword")
	public String forgotPassword(ModelMap model, HttpServletRequest request )
	{
		logger.debug("\n\n\n*************** Entering forgotPassword method of LoginController ***************\n\n\n");
		String userEmail = request.getParameter("userEmail");
		if(userEmail == null) {
			logger.info("\n\n\n??????????????? ERROR:: Emailid of user is null: ???????????????\n\n\n");
			logger.debug("\n\n\n############### Exiting forgotPassword method of LoginController ###############\n\n\n");
			return "forgotpassword";
		}
		else
		{
			UserDo userDo = userService.getUserDoByEmail(userEmail);
			if(userDo==null)
			{
				model.put("errorMsg", 0);
				model.put("msg", "This E-mail Address is not registered with us!");
				logger.info("\n\n\n??????????????? ERROR:: User:"+userEmail+" is not registered with us: ???????????????\n\n\n");
			}
			else
			{
				String newPassword = UtilityMethod.getUniqueId();
				userDo.setPassword(newPassword);
				userService.updateUser(userDo);

				//Send mail to user with new password starts
				if (!UtilityMethod.getServerName(request).equals("http://localhost:8080/sme/")) {
					String mailSubject = "ARQ SME | Password Reset Requested";

					String mailMessage = "Dear "+userDo.getName()+",<br><br>You have requested for password reset. "
							+ "Below are your new credentials to login to ARQ SME Platfom <br><br><strong>Username: </strong>"
							+userDo.getEmail()+ "<br><strong>Password: </strong>" +userDo.getPassword() + "</strong><br><br>"
							+"Access ARQ SME Platform by clicking on following URL (or copy paste the link in your browser):<br> <a href=\"" + UtilityMethod.getServerName(request) + "\">"
									+ UtilityMethod.getServerName(request)+"</a><br><br>"
							+ "<i>You may update the password once you login to your account<i><br><br><br>"
							+ "----------------------<br>" + "Thanks & Regards<br>" + "ARQ Team";

					try {
						logger.debug("\n\n\n=============== SEND:: mail with details: ===============\n"
								+ "sendTo: "+userDo.getEmail()+"\n"
								+ "password: "+userDo.getPassword()+"\n"
								+ "mailSubject: "+mailSubject+"\n"
								+ "mailMessage: "+mailMessage+"\n"
								+ "serverName: "+UtilityMethod.getServerName(request)+"\n"
								+ "=============================================\n\n\n");
						mailService.sendHtmlEmail(mailSubject, mailMessage, userDo.getEmail());
						logger.info("\n\n\n+++++++++++++++ SUCCESS:: mail sent to:"+userDo.getEmail()+" with new password: "+userDo.getPassword()+"+++++++++++++++\n\n\n");
					} catch (Exception ex) {
						logger.info("\n\n\n??????????????? ERROR:: mail could not send to:"+userEmail+" , errorMessage:"+ex.getMessage()+"???????????????\n\n\n");
						model.put("errorMsg", 0);
						model.put("msg", "Sorry mail could not be sent. Please contact ARQ SME Team!");
						ex.printStackTrace();
						logger.debug("\n\n\n############### Exiting forgotPassword method of LoginController ###############\n\n\n");
						return "forgotpassword";
					}
				}

				//Send mail to user with new password ends

				model.put("msg", "Email sent to '"+userEmail+"' with new password");
				model.put("errorMsg", 1);
			}
			logger.debug("\n\n\n############### Exiting forgotPassword method of LoginController ###############\n\n\n");
			return "forgotpassword";
		}
		
	}
	
	@RequestMapping(value="/accountverification")
	public String accountVerification(ModelMap model,HttpServletRequest request,@ModelAttribute("emailId") String emailId)
	{
		logger.debug("\n\n\n*************** Entering accountVerification method of LoginController ***************\n\n\n");
		UserDo userDo = userService.getUserDoByEmail(emailId);
		try{
			userDo.setUniqueId(UtilityMethod.getUniqueId());
			userService.updateUser(userDo);
		}catch(Exception e){
			logger.info("\n\n\n??????????????? ERROR:: Details of user:"+userDo.getEmail()+" could not update , errorMessage:"+e.getMessage()+"???????????????\n\n\n");
			model.put("errorMsg", 0);
			model.put("msg", "Could not send verification mail to your email address '" + userDo.getEmail() + "'");
			logger.debug("\n\n\n############### Exiting accountVerification method of LoginController ###############\n\n\n");
			return "accountverification";
			
		}
		if (!UtilityMethod.getServerName(request).equals("http://localhost:8080/sme/")) 
		{
			String mailSubject = "Email verification request for new account on ARQ SME Platform";

			String mailMessage = "Dear "+userDo.getName()+",<br><br> Thank you for registering your account on ARQ SME Platform.<br><br>"
					+ "To complete your sign up, please verify your account by clicking the link below (or copy paste the link in your browser)<br>"
					+"<strong>URL: </strong> <a href=\"" + UtilityMethod.getServerName(request) + "verifyaccount?user_id="
					+userDo.getId()+ "&unique_id="
					+userDo.getUniqueId()+ "\">"
							+ UtilityMethod.getServerName(request)+ "verifyaccount?user_id="
									+userDo.getId()+ "&unique_id="
									+userDo.getUniqueId()+"</a><br><br><br>"
					+ "----------------------<br>" + "Thanks & Regards<br>" + "ARQ Team";

			try {
				logger.debug("\n\n\n=============== SEND:: mail with details: ===============\n"
						+ "sendTo: "+userDo.getEmail()+"\n"
						+ "password: "+userDo.getPassword()+"\n"
						+ "mailSubject: "+mailSubject+"\n"
						+ "mailMessage: "+mailMessage+"\n"
						+ "serverName: "+UtilityMethod.getServerName(request)+"\n"
						+ "=============================================\n\n\n");
				mailService.sendHtmlEmail(mailSubject, mailMessage, userDo.getEmail());
				logger.info("\n\n\n+++++++++++++++ SUCCESS:: mail sent to:"+userDo.getEmail()+" successfully. +++++++++++++++\n\n\n");
			} catch (Exception ex) {
				logger.info("\n\n\n??????????????? ERROR:: mail could not send to:"+userDo.getEmail()+" , errorMessage:"+ex.getMessage()+"???????????????\n\n\n");
				ex.printStackTrace();
			}
		}
		model.put("errorMsg", 1);
		model.put("msg", "Please verify your account by clicking on the link sent to your email address '" + userDo.getEmail() + "'");
		logger.debug("\n\n\n############### Exiting accountVerification method of LoginController ###############\n\n\n");
		return "accountverification";
	}
	
	//for 403 access denied page
	@RequestMapping(value = "/403")
	public String accesssDenied(Map<String, Object> map, HttpSession session, ModelMap model,
			HttpServletRequest request) {

		//check if user is logged in
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			UserDetails userDetail = (UserDetails) auth.getPrincipal();
			model.put("username", userDetail.getUsername());
			
			session.invalidate();
		}
		return "403";
	}
	
}
