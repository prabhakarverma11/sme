package com.dq.arq.sme.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dq.arq.sme.domain.UserDo;
import com.dq.arq.sme.domain.UserRoleDo;
import com.dq.arq.sme.services.MailService;
import com.dq.arq.sme.services.UserService;
import com.dq.arq.sme.util.UtilConstants;
import com.dq.arq.sme.util.UtilityMethod;

@Controller
public class UserController {

	//final static Logger logger = LoggerFactory.getLogger(UserController.class);
	final static Logger logger = LoggerFactory.getLogger("LogTesting");
	
	@Autowired
	UserService userService;
	
	@Autowired
	MailService mailService;
	
	/**
	 * 
	 * @param model
	 * @return
	 * 
	 * Calls the view with form for getting user details
	 */
	@RequestMapping(value="/adduser")
	public String addUser(ModelMap model)
	{
		logger.debug("\n\n\n*************** Entering addUser method of UserController ***************\n\n\n");
		UserDo userDo = new UserDo();
		model.put("userDo",userDo);
		logger.debug("\n\n\n############### Exiting addUser method of UserController ###############\n\n\n");
		return "adduser";
	}
	
	
	/**
	 * 
	 * @param model
	 * @param request
	 * @param userDo
	 * @return
	 * 
	 * Saves the user details and calls the view to show user list
	 */
	@RequestMapping(value="/saveuser",method = RequestMethod.POST)
	public String saveUser(ModelMap model,HttpServletRequest request, @ModelAttribute("userDo") UserDo userDo)
	{
		logger.debug("\n\n\n*************** Entering saveUser method of UserController ***************\n\n\n");
		
		UserDo duplicateUserDo = userService.getUserDoByEmail(userDo.getEmail());
		if(duplicateUserDo !=null) {
			logger.info("\n\n\n??????????????? ERROR:: User: "+userDo.getEmail()+" already exists. ???????????????\n\n\n");
			model.put("msg", "User with name '"+userDo.getEmail()+"' already exists");
			model.put("errorMsg", 0);
			model.put("userDo", userDo);
			logger.debug("\n\n\n############### Exiting saveUser method of UserController ###############\n\n\n");
			return "adduser";
		}
		
		UserDo loggedInUser = (UserDo) request.getSession().getAttribute("sUser");
		userDo.setCreatedBy(loggedInUser.getName());
		userDo.setCreatedOn(new Date());
		userDo.setUpdatedBy(loggedInUser.getName());
		userDo.setUpdatedOn(new Date());
		userDo.setIsVerified(true);
		userDo.setUniqueId(UtilityMethod.getUniqueId());
		Integer userId;
		try{
			userId = userService.saveUserDo(userDo);
			userService.createUserRole(userDo);
			model.put("msg", "User with email '"+userDo.getEmail()+"' saved successfully.");
			model.put("errorMsg",1);
			logger.info("\n\n\n+++++++++++++++ SUCCESS:: User with details: +++++++++++++++\n"
					+ "id: "+userDo.getId()+"\n"
					+ "name: "+userDo.getName()+"\n"
					+ "email: "+userDo.getEmail()+"\n saved successfully.\n"
					+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
		}
		catch(Exception e){
			logger.info("\n\n\n??????????????? ERROR:: Could not save user: "+userDo.getEmail()+", exception: "+e.getMessage()+". ???????????????\n\n\n");
			model.put("msg", "Could not save User. Check log for errors");
			model.put("errorMsg",0);
			e.printStackTrace();
			logger.debug("\n\n\n############### Exiting saveUser method of UserController ###############\n\n\n");
			return "forward:/listuser";
		}
		
		if(!UtilityMethod.getServerName(request).equals("http://localhost:8080/sme/"))
		{
			String mailSubject="Congratulations!! Your account has been created successfully on ARQ SME Platform";
	
			String mailMessage="Greetings!!<br><br>Your account has been created successfully on ARQ SME Platform.<br>"+
					"<br>Use the below credentials to login to ARQ SME platform<br><br>"
					+ "<strong>URL: </strong> <a href=\""+UtilityMethod.getServerName(request)+"\">"
							+UtilityMethod.getServerName(request)+ "</a><br>"
					+ "<strong>Username:</strong> "+userDo.getEmail()+"<br>"
					+ "<strong>Password:</strong> "+userDo.getPassword()+"</strong><br><br>"
					+"<i>Don't forget to update the password once you login to your account<i><br><br><br>"
					+ "----------------------<br>"
					+ "Thanks & Regards<br>"
					+ "ARQ Team";
			
			try {
				logger.debug("\n\n\n=============== SEND:: mail with details: ===============\n"
						+ "sendTo: "+userDo.getEmail()+"\n"
						+ "password: "+userDo.getPassword()+"\n"
						+ "mailSubject: "+mailSubject+"\n"
						+ "mailMessage: "+mailMessage+"\n"
						+ "serverName: "+UtilityMethod.getServerName(request)+"\n"
						+ "=============================================\n\n\n");
				mailService.sendHtmlEmail(mailSubject,mailMessage,userDo.getEmail());
				logger.info("\n\n\n+++++++++++++++ SUCCESS:: mail sent to:"+userDo.getEmail()+" successfully. +++++++++++++++\n\n\n");
			} catch (Exception ex) {
				logger.info("\n\n\n??????????????? ERROR:: failed to send email to: "+userDo.getEmail()+", exception: "+ex.getMessage()+". ???????????????\n\n\n");
				ex.printStackTrace();
			}
		}
		logger.debug("\n\n\n############### Exiting saveUser method of UserController ###############\n\n\n");
		return "forward:/listuser";
	}
	
	
	/**
	 * 
	 * @param model
	 * @param request
	 * @return
	 * 
	 * Displays list of Users
	 */
	@RequestMapping(value="/listuser")
	public String listUser(ModelMap model,HttpServletRequest request,HttpSession session,
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "col", required = false) String columnName,
			@RequestParam(value = "o", required = false) Integer orderBy)
	{
		logger.debug("\n\n\n*************** Entering listUser method of UserController ***************\n\n\n");
		if (page == null)
			page = 1;
		if(columnName==null || columnName.equals("")) {
			session.removeAttribute("col");
			session.removeAttribute("o");
		}
		Integer totalRecords =0;
		List<UserDo> userDos = new ArrayList<UserDo>();
		totalRecords = userService.countUserDos().intValue();
		if(totalRecords>0) {
			if(columnName!=null && !columnName.equals("")) {
				session.setAttribute("col", columnName);
				session.setAttribute("o", orderBy);
				userDos = userService.getUserDosListByPageAndSortedByColumn(page,columnName,orderBy);
			}else {
				userDos = userService.getUserDosListByPage(page);
			}
		}
		
		session.setAttribute("totalRecords", totalRecords);
		session.setAttribute("recordsPerPage", UtilConstants.CAMPAIGNS_PER_PAGE);
		session.setAttribute("pageNumber", page);
		model.put("userDos", userDos);
		logger.debug("\n\n\n############### Exiting listUser method of UserController ###############\n\n\n");
		return "userlist";
	}
	
	/**
	* @param model
	 * @param userId
	 * @return
	 * 
	 * Calls the view with form for updating user details
	 */
	@RequestMapping(value="/edituser")
	public String editUser(ModelMap model,@RequestParam(required=true,value="uid")Integer userId)
	{
		logger.debug("\n\n\n*************** Entering editUser method of UserController ***************\n\n\n");
		UserDo userDo = userService.getUserDoById(userId);
		userDo.setCreatedOnString(UtilityMethod.formatDateTOMM_DD_YYYY(userDo.getCreatedOn()));
		model.put("userDo",userDo);
		model.put("oldEmail", userDo.getEmail());
		logger.debug("\n\n\n############### Exiting editUser method of UserController ###############\n\n\n");
		return "edituser";
	}
	
/**
 * 
 * @param model
 * @param userId
 * @return
 * 
 * User details are updated based on the model received from the model
 */
	@RequestMapping(value="/updateuser")
	public String updateUser(ModelMap model,HttpSession httpSession,HttpServletRequest request,@ModelAttribute("userDo")UserDo userDo)
	{
		logger.debug("\n\n\n*************** Entering updateUser method of UserController ***************\n\n\n");
		if((userService.getUserDoByEmail(userDo.getEmail())!=null) &&  (!userDo.getEmail().equals(request.getParameter("oldEmail"))))
		{
			model.put("errorMsg",0);
			model.put("msg", "User with email id: "+userDo.getEmail()+" already exists");
			logger.info("\n\n\n??????????????? ERROR:: User: "+userDo.getEmail()+" already exists ???????????????\n\n\n");
			model.put("oldEmail",request.getParameter("oldEmail"));
			model.put("userDo",userDo);
			logger.debug("\n\n\n############### Exiting updateUser method of UserController ###############\n\n\n");
			return "edituser";
		}
		UserDo currentUserDo = (UserDo) request.getSession().getAttribute("sUser");
		userDo.setUpdatedBy(currentUserDo.getName());
		userDo.setUpdatedOn(new Date());
		userDo.setCreatedOn(UtilityMethod.convertStringMM_DD_YYYYTODateInJava(userDo.getCreatedOnString()));
		userDo.setIsVerified(true);
		userDo.setUniqueId(UtilityMethod.getUniqueId());
		userService.updateUser(userDo);
		UserRoleDo userRoleDo = userService.getUserRoleDoByUserDo(userDo);
		if(userRoleDo.getRole().equals("ROLE_ADMIN"))	//Updating current user in session
			httpSession.setAttribute("sUser", userDo);
		model.put("msg", "Account Details of user "+ userDo.getName()+ " updated successfully");
		model.put("errorMsg",1);
		logger.info("\n\n\n+++++++++++++++ SUCCESS:: User with details: +++++++++++++++\n"
				+ "id: "+userDo.getId()+"\n"
				+ "name: "+userDo.getName()+"\n"
				+ "email: "+userDo.getEmail()+" updated successfully.\n"
				+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
		logger.debug("\n\n\n############### Exiting updateUser method of UserController ###############\n\n\n");
		return "forward:/listuser";
	}
	
	@ResponseBody
	@RequestMapping(value="/searchuserlist",method=RequestMethod.GET,produces="application/json")
	public void searchCampaignList(@RequestParam(value="name",required=false) String uName, HttpServletResponse response) throws IOException{
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		List<Object[]> rows = userService.getUserDosListBySearch(uName);
		for(Object[] row:rows) {
			out.print((Integer)row[0]+":"+(String)row[1]+",");
		}
	}
}
