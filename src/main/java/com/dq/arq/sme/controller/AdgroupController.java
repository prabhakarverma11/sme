package com.dq.arq.sme.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
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

import com.dq.arq.sme.adwordapi.AdAdwordApi;
import com.dq.arq.sme.adwordapi.AdgroupAdwordApi;
import com.dq.arq.sme.adwordapi.KeywordAdwordApi;
import com.dq.arq.sme.domain.AdDo;
import com.dq.arq.sme.domain.AdgroupDo;
import com.dq.arq.sme.domain.CampaignDo;
import com.dq.arq.sme.domain.KeywordDo;
import com.dq.arq.sme.domain.ProductCategoryDo;
import com.dq.arq.sme.domain.UserDo;
import com.dq.arq.sme.domain.UserRoleDo;
import com.dq.arq.sme.services.AdService;
import com.dq.arq.sme.services.AdgroupService;
import com.dq.arq.sme.services.CampaignService;
import com.dq.arq.sme.services.CategoryService;
import com.dq.arq.sme.services.KeywordService;
import com.dq.arq.sme.services.MailService;
import com.dq.arq.sme.services.ProductCategoryService;
import com.dq.arq.sme.services.UserService;
import com.dq.arq.sme.util.UtilConstants;
import com.dq.arq.sme.util.UtilityMethod;
import com.google.api.ads.adwords.axis.v201607.cm.ApiException;

@Controller
public class AdgroupController {

	//final static Logger logger = LoggerFactory.getLogger(AdgroupController.class);
	final static Logger logger = LoggerFactory.getLogger("LogTesting");

	@Autowired
	AdgroupService adgroupService;

	@Autowired
	CampaignService campaignService;

	@Autowired
	CategoryService categoryService; 

	@Autowired
	MailService mailService;

	@Autowired 
	KeywordService keywordService;

	@Autowired 
	ProductCategoryService productCategoryService;

	@Autowired
	AdService adService;

	@Autowired
	UserService userService;

	/**
	 * 
	 * @param model
	 * @param campaignId
	 * @return
	 * 
	 * Sets the campaignId and category list in the campaignDo object and passes it to the view
	 * In the view user is asked to fill the form with ad group values
	 */
	@RequestMapping(value = "/addadgroup")
	public String addAdgroup(ModelMap model,@RequestParam(required = true, value = "cid") Integer campaignId) {
		logger.debug("\n\n\n*************** Entering addAdgroup method of AdgroupController ***************\n\n\n");
		AdgroupDo adgroupDo = new AdgroupDo();
		adgroupDo.setCampaignDo(campaignService.getCampaignDoById(campaignId));


		//adgroupDo.setCategoryList(categoryService.getPrimeCategories());

		model.put("ProductCategoryDos", productCategoryService.getProductCategoryDosList());
		AdDo adDo = new AdDo();
		List<AdDo> adDos = new ArrayList<AdDo>();
		adDos.add(adDo);
		adgroupDo.setAdDos(adDos);
		model.put("adgroupDo",adgroupDo);

		logger.debug("\n\n\n+++++++++++++++ INFO:: +++++++++++++++\n"
				+ "campaignId: "+campaignId+"\n"
				+ "productCategorySize: "+productCategoryService.getProductCategoryDosList().size()+"\n"
				+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
		logger.debug("\n\n\n############### Exiting addAdgroup method of AdgroupController ###############\n\n\n");
		return "addadgroup";
	}

	/**
	 * 
	 * @param model
	 * @param request
	 * @param adgroupDo
	 * @return
	 * 
	 * Retrieved the form values containing the adgroup details
	 * Validates for duplicate adgroup name
	 * Sets the adword in the google api to the existing campaign
	 * Sets the keyword suggested by api to the existing campaign
	 * Saves the adgroup details in the local db
	 * Sends the email to internal team notifying them about the new campaign creation
	 * @throws IOException 
	 */
	@RequestMapping(value = "/saveadgroup", method = RequestMethod.POST)
	public String saveAdgroup(ModelMap model,HttpServletRequest request, @ModelAttribute("adgroupDo") AdgroupDo adgroupDo) throws IOException {
		logger.debug("\n\n\n*************** Entering saveAdgroup method of AdgroupController ***************\n\n\n");

		CampaignDo campaignDo = campaignService.getCampaignDoById(adgroupDo.getCampaignDo().getId());
		adgroupDo.setCampaignDo(campaignDo);



		List<AdgroupDo> adgroupDos= adgroupService.getAdgroupDosListByCampaignDo(campaignDo);
		for(AdgroupDo adgroupDoByCampaign:adgroupDos)
		{
			if(adgroupDoByCampaign.getProductName().equals(adgroupDo.getProductName()))
			{
				
				logger.info("\n\n\n??????????????? ERROR:: adgroup with name: "+adgroupDo.getProductName()+" already exists. ???????????????\n\n\n");
				model.put("msg", "Product with name "+adgroupDo.getProductName()+" already exists!!");
				model.put("errorMsg",0);
				adgroupDo.setCampaignDo(campaignService.getCampaignDoById(adgroupDo.getCampaignDo().getId()));
				model.put("ProductCategoryDos", productCategoryService.getProductCategoryDosList());
				model.put("adgroupDo",adgroupDo);
				logger.debug("\n\n\n############### Exiting saveAdgroup method of AdgroupController ###############\n\n\n");
				return "addadgroup";
			}
		}
		
		
		UserDo userDo = (UserDo) request.getSession().getAttribute("sUser");
		adgroupDo.setCreatedBy(userDo.getName());
		adgroupDo.setCreatedOn(new Date());
		adgroupDo.setUpdatedBy(userDo.getName());
		adgroupDo.setUpdatedOn(new Date());

		adgroupDo.setStatus(AdgroupDo.Status.Enabled.name());  //Setting status to enabled
		adgroupDo.setThresholdKeywordAvgCpc(60d);

		ProductCategoryDo productCategoryDo =  productCategoryService.getProductCategoryDoByName(adgroupDo.getCategoryName());
		
		
		//Retrieve keyword Ideas starts
		KeywordAdwordApi keywordAdwordApi = new KeywordAdwordApi();
				List<String> locationApiIds = new ArrayList<String>();
				if(campaignDo.getLocationInclude()!=null && !campaignDo.getLocationInclude().equals(""))
				{
					for(String locationId : campaignDo.getLocationInclude().split(","))
					{
						if(!locationId.equals(""))
						{
							if(campaignDo.getLocationIncludeCriteria()==4)
							{
								locationApiIds.add(locationId.split(":")[0]);
							}
							else	
								locationApiIds.add(locationId);
						}

					}
				}
				List<KeywordDo> newkeywordDos = new ArrayList<KeywordDo>();
				String mailMessage="";
				String mailSubject="";
				try{
				 newkeywordDos = keywordAdwordApi.getKeywordIdeas(adgroupDo.getProductName(),productCategoryDo.getId(),UtilConstants.NO_OF_KEYWORDS,locationApiIds);
				 
				 if(newkeywordDos.size() == 0)
					{
						logger.info("\n\n\n??????????????? ERROR:: No Keyword Ideas found. Category and product name does not match. ???????????????\n\n\n");
						model.put("msg", "No keyword ideas found. Category and Product Name does not match");
						model.put("errorMsg",0);
						adgroupDo.setCampaignDo(campaignService.getCampaignDoById(adgroupDo.getCampaignDo().getId()));
						//adgroupDo.setCategoryList(categoryService.getPrimeCategories());
						model.put("ProductCategoryDos", productCategoryService.getProductCategoryDosList());
						model.put("adgroupDo",adgroupDo);
						logger.debug("\n\n\n############### Exiting saveAdgroup method of AdgroupController ###############\n\n\n");
						return "addadgroup";
					}
				 
				}catch(RemoteException e1)
				{

					if(e1 instanceof ApiException)
					{
						ApiException apiException = (ApiException) e1;
						model.put("msg", "Product not saved. Adword Error: "+UtilityMethod.getErrorMessageFromProperties(apiException.getFaultReason()));
						model.put("errorMsg", 0);
						logger.info("\n\n\n??????????????? ERROR:: Keyword Ideas with adgroup name :"+adgroupDo.getProductName()+" could not be fetched due to adwordError , errorMessage:"+UtilityMethod.printStackTrace(e1)+"???????????????\n\n\n");
					}
					else
					{
						model.put("msg", "Product not saved. Please contact admin");
						logger.info("\n\n\n??????????????? ERROR::  Keyword Ideas with adgroup name:"+adgroupDo.getProductName()+" could not be fetched, errorMessage:"+e1.getMessage()+"???????????????\n\n\n");
					}
					
					mailSubject="Problem with campaign "+adgroupDo.getCampaignDo().getName()+" and product "+adgroupDo.getProductName()+" !!";

					mailMessage="Greetings!!<br><br>Campaign with name <strong>"+adgroupDo.getCampaignDo().getName() +"</strong> and"
							+ " product <strong>"+adgroupDo.getProductName()+"</strong> encountered an error during fetching keyword ideas. Please review.<br>"+
							"<br>Use the below credentials to login to ARQ SME platform<br><br>"
							+ "<strong>URL: </strong> <a href=\""+UtilityMethod.getServerName(request)+"\">"+UtilityMethod.getServerName(request)+"</a><br>"
							+ "<strong>Username:</strong> admin@arq.co.in<br>"
							+ "<strong>Password:</strong> Admin</strong><br><br><br>"
							+"Exception:<br><br>"
							+UtilityMethod.printStackTrace(e1)
							+ "<br><br><br>----------------------<br>"
							+ "Thanks & Regards<br>"
							+ "ARQ Team";

					e1.printStackTrace();

					logger.debug("\n\n\n############### Exiting saveAdgroup method of AdgroupController ###############\n\n\n");
					return "forward:/listadgroup?cid="+ adgroupDo.getCampaignDo().getId();
				}
				//Retrieve keyword Ideas ends

		try{
			//Saving adGroup in the local database
			Integer adgroupId = adgroupService.saveAdgroupDo(adgroupDo);
			model.put("msg", "Product with name '"+adgroupDo.getProductName()+"' added successfully");
			model.put("errorMsg",1);
			logger.info("\n\n\n+++++++++++++++ SUCCESS:: adgroup with name: "+adgroupDo.getProductName()+" is saved successfully. +++++++++++++++\n\n\n");
		}catch(Exception e)
		{
			model.put("msg", "Could not save product. Check log for errors");
			model.put("errorMsg",0);
			e.printStackTrace();
			logger.info("\n\n\n??????????????? ERROR:: adgroup with name:"+adgroupDo.getProductName()+" could not save , errorMessage:"+e.getMessage()+"???????????????\n\n\n");
			logger.debug("\n\n\n############### Exiting saveAdgroup method of AdgroupController ###############\n\n\n");
			return "forward:/listadgroup?cid="+ adgroupDo.getCampaignDo().getId();
		}

		AdDo adDo = new AdDo();
		adDo = adgroupDo.getAdDos().get(0);
		adDo.setCreatedBy(userDo.getName());
		adDo.setCreatedOn(new Date());
		adDo.setUpdatedBy(userDo.getName());
		adDo.setUpdatedOn(new Date());
		adDo.setStatus(AdDo.Status.Enabled.name());
		adDo.setAdgroupDo(adgroupDo);
		try{
			//Saving adGroup in the local database
			Integer adId = adService.saveAdDo(adDo);
			logger.info("\n\n\n+++++++++++++++ SUCCESS:: ad with id: "+adDo.getId()+" is saved successfully. +++++++++++++++\n\n\n");
		}catch(Exception e)
		{
			e.printStackTrace();
			logger.info("\n\n\n??????????????? ERROR:: Ad with id '"+adDo.getId()+"' could not be saved , errorMessage:"+e.getMessage()+"???????????????\n\n\n");
			logger.debug("\n\n\n############### Exiting saveAdgroup method of AdgroupController ###############\n\n\n");
			return "forward:/listadgroup?cid="+ adgroupDo.getCampaignDo().getId();
		}

		
		try {
			try{
				//Creating adGroup in google adwordAPI
				AdgroupAdwordApi adgroupAdwordApi = new AdgroupAdwordApi();
				adgroupAdwordApi.createAdgroup(adgroupDo, adgroupDo.getCampaignDo().getApiId());
				adgroupService.updateAdgroup(adgroupDo);

			}catch(RemoteException e1)
			{

				adgroupDo.setStatus(AdgroupDo.Status.Removed.name());
				adgroupService.updateAdgroup(adgroupDo);

				if(e1 instanceof ApiException)
				{
					ApiException apiException = (ApiException) e1;
					model.put("msg", "Product not saved. Error: "+UtilityMethod.getErrorMessageFromProperties(apiException.getFaultReason()));
					model.put("errorMsg", 0);
					logger.info("\n\n\n??????????????? ERROR:: adgroup with name :"+adgroupDo.getProductName()+" could not be saved due to adwordError , errorMessage:"+UtilityMethod.printStackTrace(e1)+"???????????????\n\n\n");
				}
				else
				{
					model.put("msg", "Product not saved on adword. Please contact admin");
					logger.info("\n\n\n??????????????? ERROR::  adgroup with name  :"+adgroupDo.getProductName()+" could not be saved, errorMessage:"+e1.getMessage()+"???????????????\n\n\n");
				}

				e1.printStackTrace();

				mailSubject="Problem with campaign "+adgroupDo.getCampaignDo().getName()+" and product "+adgroupDo.getProductName()+" !!";

				mailMessage="Greetings!!<br><br>Campaign with name <strong>"+adgroupDo.getCampaignDo().getName() +"</strong> and"
						+ " product <strong>"+adgroupDo.getProductName()+"</strong> encountered an error during adgroup creation. Please review.<br>"+
						"<br>Use the below credentials to login to ARQ SME platform<br><br>"
						+ "<strong>URL: </strong> <a href=\""+UtilityMethod.getServerName(request)+"\">"+UtilityMethod.getServerName(request)+"</a><br>"
						+ "<strong>Username:</strong> admin@arq.co.in<br>"
						+ "<strong>Password:</strong> Admin</strong><br><br><br>"
						+"Exception:<br><br>"
						+UtilityMethod.printStackTrace(e1)
						+ "<br><br><br>----------------------<br>"
						+ "Thanks & Regards<br>"
						+ "ARQ Team";

				logger.debug("\n\n\n############### Exiting saveAdgroup method of AdgroupController ###############\n\n\n");
				return "forward:/listadgroup?cid="+ adgroupDo.getCampaignDo().getId();
			}

			try {
				//Creating keywords in google adwordAPI

				for(KeywordDo keywordDo : newkeywordDos)
				{
					keywordDo.setMatchType("PHRASE");
					keywordDo.setStatus(KeywordDo.Status.Enabled.name());
				}
				keywordAdwordApi.refreshKeywordList(adgroupDo,newkeywordDos);
				for(KeywordDo keywordDo : newkeywordDos)
				{
					keywordDo.setAdgroupDo(adgroupDo);
					keywordDo.setAdgroupApiId(adgroupDo.getApiId());
					keywordDo.setCreatedBy(userDo.getName());
					keywordDo.setUpdatedBy(userDo.getName());
					keywordDo.setCreatedOn(new Date());

				}

				keywordService.saveKeywordDos(newkeywordDos);
			}catch(RemoteException |UnsupportedEncodingException e1)
			{

				if(e1 instanceof ApiException)
				{
					ApiException apiException = (ApiException) e1;
					model.put("msg", "Product not saved. Adword Error: "+UtilityMethod.getErrorMessageFromProperties(apiException.getFaultReason()));
					model.put("errorMsg", 0);
					logger.info("\n\n\n??????????????? ERROR:: keywords with adgroup name :"+adgroupDo.getProductName()+" could not be saved due to adwordError , errorMessage:"+UtilityMethod.printStackTrace(e1)+"???????????????\n\n\n");
				}
				else
				{
					model.put("msg", "Product not saved. Please contact admin");
					logger.info("\n\n\n??????????????? ERROR::  keywords with adgroup name:"+adgroupDo.getProductName()+" could not be saved, errorMessage:"+e1.getMessage()+"???????????????\n\n\n");
				}

				e1.printStackTrace();

				mailSubject="Problem with campaign "+adgroupDo.getCampaignDo().getName()+" and product "+adgroupDo.getProductName()+" !!";

				mailMessage="Greetings!!<br><br>Campaign with name <strong>"+adgroupDo.getCampaignDo().getName() +"</strong> and"
						+ " product <strong>"+adgroupDo.getProductName()+"</strong> encountered an error during keywords creation. Please review.<br>"+
						"<br>Use the below credentials to login to ARQ SME platform<br><br>"
						+ "<strong>URL: </strong> <a href=\""+UtilityMethod.getServerName(request)+"\">"+UtilityMethod.getServerName(request)+"</a><br>"
						+ "<strong>Username:</strong> admin@arq.co.in<br>"
						+ "<strong>Password:</strong> Admin</strong><br><br><br>"
						+"Exception:<br><br>"
						+UtilityMethod.printStackTrace(e1)
						+ "<br><br><br>----------------------<br>"
						+ "Thanks & Regards<br>"
						+ "ARQ Team";

				logger.debug("\n\n\n############### Exiting saveAdgroup method of AdgroupController ###############\n\n\n");
				return "forward:/listadgroup?cid="+ adgroupDo.getCampaignDo().getId();
			}

			try {
				//Creating ad in google adwordAPI
				AdAdwordApi adAdwordApi=new AdAdwordApi();
				adDo.setAdgroupApiId(adgroupDo.getApiId());
				adAdwordApi.createAd(adDo);
				adService.updateAdDo(adDo);


				mailSubject="New Product "+adgroupDo.getProductName()+" with campaign "+adgroupDo.getCampaignDo().getName()+" created | Please review";

				mailMessage="Greetings!!<br><br>New Product with name <strong>"+adgroupDo.getProductName() +"</strong> has been created in the "
						+ "campaign "+adgroupDo.getCampaignDo().getName()+". Please review.<br>"+
						"<br>Use the below credentials to login to ARQ SME platform<br><br>"
						+ "<strong>URL: </strong> <a href=\""+UtilityMethod.getServerName(request)+"\">"+UtilityMethod.getServerName(request)+"</a><br>"
						+ "<strong>Username:</strong> admin@arq.co.in<br>"
						+ "<strong>Password:</strong> Admin</strong><br><br><br>"
						+ "----------------------<br>"
						+ "Thanks & Regards<br>"
						+ "ARQ Team";

				logger.info("\n\n\n+++++++++++++++ SUCCESS:: adgroup with name: "+adgroupDo.getProductName()+" is saved successfully on Google adword and updated local database accordingly. +++++++++++++++\n\n\n");

			}catch(RemoteException e1)
			{

				adDo.setStatus(AdDo.Status.Disabled.name());
				adService.updateAdDo(adDo);

				if(e1 instanceof ApiException)
				{
					ApiException apiException = (ApiException) e1;
					model.put("msg", "Product not saved. Error: "+UtilityMethod.getErrorMessageFromProperties(apiException.getFaultReason()));
					model.put("errorMsg", 0);
					logger.info("\n\n\n??????????????? ERROR:: Ads with adgroup name :"+adgroupDo.getProductName()+" could not be saved due to adwordError , errorMessage:"+UtilityMethod.printStackTrace(apiException)+UtilityMethod.printStackTrace(e1)+"???????????????\n\n\n");
				}
				else
				{
					model.put("msg", "Product not saved on adword. Please contact admin");
					logger.info("\n\n\n??????????????? ERROR::  Ads with adgroup name:"+adgroupDo.getProductName()+" could not be saved, errorMessage:"+e1.getMessage()+"???????????????\n\n\n");
				}

				//e1.printStackTrace();



				mailSubject="Problem with campaign "+adgroupDo.getCampaignDo().getName()+" and product "+adgroupDo.getProductName()+" !!";

				mailMessage="Greetings!!<br><br>Campaign with name <strong>"+adgroupDo.getCampaignDo().getName() +"</strong> and"
						+ " product <strong>"+adgroupDo.getProductName()+"</strong> encountered an error during ad creation. Please review.<br>"+
						"<br>Use the below credentials to login to ARQ SME platform<br><br>"
						+ "<strong>URL: </strong> <a href=\""+UtilityMethod.getServerName(request)+"\">"+UtilityMethod.getServerName(request)+"</a><br>"
						+ "<strong>Username:</strong> admin@arq.co.in<br>"
						+ "<strong>Password:</strong> Admin</strong><br><br><br>"
						+"Exception:<br><br>"
						+UtilityMethod.printStackTrace(e1)
						+ "<br><br><br>----------------------<br>"
						+ "Thanks & Regards<br>"
						+ "ARQ Team";

				logger.debug("\n\n\n############### Exiting saveAdgroup method of AdgroupController ###############\n\n\n");
				return "forward:/listadgroup?cid="+ adgroupDo.getCampaignDo().getId();
			}
		} 
		finally{
			if(!UtilityMethod.getServerName(request).equals("http://localhost:8080/sme/"))
			{
				try {
					logger.debug("\n\n\n=============== SEND:: mail with details: ===============\n"
							+ "sendTo: "+userDo.getEmail()+"\n"
							+ "mailSubject: "+mailSubject+"\n"
							+ "mailMessage: "+mailMessage+"\n"
							+ "serverName: "+UtilityMethod.getServerName(request)+"\n"
							+ "=============================================\n\n\n");
					mailService.sendHtmlEmail(mailSubject,mailMessage);
					logger.info("\n\n\n+++++++++++++++ SUCCESS:: mail sent to:"+userDo.getEmail()+" successfully. +++++++++++++++\n\n\n");
				} catch (Exception ex) {
					logger.info("\n\n\n??????????????? ERROR:: mail could not send to:"+userDo.getEmail()+" , errorMessage:"+ex.getMessage()+"???????????????\n\n\n");
					ex.printStackTrace();
				}
			}
		}
		//Mail Sending code ends
		logger.debug("\n\n\n############### Exiting saveAdgroup method of AdgroupController ###############\n\n\n");
		return "forward:/listadgroup?cid="+ adgroupDo.getCampaignDo().getId();
	}


	
	/**
	 * 
	 * @param campaignId
	 * @param model
	 * @param request
	 * @param session
	 * @return
	 * 
	 * Displays the list of adgroup based on the campaignId
	 */
	@RequestMapping(value = "/listadgroup")
	public String adgroupListForCampaign(
			ModelMap model, HttpServletRequest request,HttpSession session,
			@RequestParam(required = false, value = "cid") Integer campaignId,
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "rows", required = false) Integer rows,
			@RequestParam(value = "col", required = false) String columnName,
			@RequestParam(value = "o", required = false) Integer orderBy)  {

 
		logger.debug("\n\n\n*************** Entering adgroupList method of AdgroupController ***************\n\n\n");
		if(page==null)
			page = 1;
		if(rows==null)
			rows = UtilConstants.CAMPAIGNS_PER_PAGE;
		Integer totalRecords = 0;
		Integer adgroupPerPage = rows; 
		
		if(columnName==null || columnName.equals("")) {
			session.removeAttribute("col");
			session.removeAttribute("o");
		}
		
		List<AdgroupDo> adgroupDos = new ArrayList<AdgroupDo>();

		
		if(campaignId==null) {// case for listing all the adgroups
			UserDo usrDo = (UserDo)session.getAttribute("sUser");
			UserRoleDo usrRoleDo = userService.getUserRoleDoByUserDo(usrDo);
			
			if(usrRoleDo.getRole().equals("ROLE_ADMIN")) {
				totalRecords = adgroupService.countAdGroupDosForAdmin().intValue();
				if(totalRecords>0) {
					if(columnName!=null && !columnName.equals("")) {
						session.setAttribute("col", columnName);
						session.setAttribute("o", orderBy);
						adgroupDos =adgroupService.getAdgroupDosListForAdminByPageAndSortedByColumn(page,adgroupPerPage,columnName,orderBy);
					}else {
						adgroupDos =adgroupService.getAdgroupDosListForAdminByPage(page,adgroupPerPage);
					}
				}
			}else {
				totalRecords = adgroupService.countAdGroupDosByUserDo(usrDo).intValue();
				if(totalRecords>0) {
					if(columnName!=null && !columnName.equals("")) {
						session.setAttribute("col", columnName);
						session.setAttribute("o", orderBy);
						adgroupDos =adgroupService.getAdgroupDosListByUserDoAndPageAndSortedByColumn(usrDo,page,adgroupPerPage,columnName,orderBy);
					}else {						
						adgroupDos =adgroupService.getAdgroupDosListByUserDoAndPage(usrDo,page,adgroupPerPage);
					}
				}
			}
			
			model.put("showAllAdgroups", true);
			logger.debug("\n\n\n+++++++++++++++ INFO:: +++++++++++++++\n"
					+ "page: "+page+"\n"
					+ "totalRecords: "+totalRecords+"\n"
					+ "adgroupDosSize: "+adgroupDos.size()+"\n"
					+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
		}
		else { //case otherwise
			CampaignDo campaignDo = campaignService.getCampaignDoById(campaignId);

			totalRecords = adgroupService.countAdGroupDosByCampaign(campaignDo).intValue();
			if(totalRecords>0) {
				if(columnName!=null && !columnName.equals("")) {
					session.setAttribute("col", columnName);
					session.setAttribute("o", orderBy);
					adgroupDos =adgroupService.getAdgroupDosListByCampaignDoAndPageAndSortedByColumn(campaignDo,page,adgroupPerPage,columnName,orderBy);
				}else {
					adgroupDos =adgroupService.getAdgroupDosListByCampaignDoAndPage(campaignDo,page,adgroupPerPage);
				}
			}
			model.put("campaignId", campaignId);
			model.put("campaignName", campaignDo.getName());
			model.put("accountManager", campaignDo.getUserDo().getName());
			model.put("showAllAdgroups", false);
			logger.debug("\n\n\n+++++++++++++++ INFO:: +++++++++++++++\n"
					+ "campaignId: "+campaignId+"\n"
					+ "campaignName: "+campaignDo.getName()+"\n"
					+ "page: "+page+"\n"
					+ "totalRecords: "+totalRecords+"\n"
					+ "adgroupDosSize: "+adgroupDos.size()+"\n"
					+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
		}
		int totalPages = 0;
		if(totalRecords != 0) {
			if((totalRecords % adgroupPerPage) == 0) {
				totalPages = (totalRecords / adgroupPerPage);
			}else {
				totalPages = (totalRecords / adgroupPerPage)+1;
			}
			if(page>totalPages) {
				page=totalPages;
				logger.debug("\n\n\n############### Exiting adgroupListForCampaign method of AdgroupController ###############\n\n\n");
				if(campaignId!=null)
					return "forward:/listadgroup"+"?cid="+campaignId+"&page="+page+"&rows="+rows;
				else
					return "forward:/listadgroup"+"?page="+page+"&rows="+rows;
			}
		}
		for(AdgroupDo adgroupDo: adgroupDos)
		{
			CampaignDo campaignDo = adgroupService.getCampaignDoByAdgroupId(adgroupDo.getId());
			adgroupDo.setCampaignDo(campaignDo);
			List<KeywordDo> keywordDos = keywordService.getKeywordDosListByAdgroupDo(adgroupDo);
			String keywordTextList = "";
			for(KeywordDo keywordDo : keywordDos)
			{
				keywordTextList+=keywordDo.getText()+",";
			}
			adgroupDo.setKeywordNames(keywordTextList);
			adgroupDo.setAdDos(adService.getAdDosListByAdgroupDo(adgroupDo));
			for(AdDo adDo : adgroupDo.getAdDos())
			{
				adDo.setCreatedOnString(UtilityMethod.formatDateTOYYYY_MM_DD(adDo.getCreatedOn()));
			}
		}
		model.put("adDo",new AdDo());
		model.put("rows", rows);
		model.put("adgroupDos", adgroupDos);
		session.setAttribute("totalRecords", totalRecords);
		session.setAttribute("recordsPerPage", adgroupPerPage);
		session.setAttribute("pageNumber", page);
		logger.debug("\n\n\n############### Exiting adgroupListForCampaign method of AdgroupController ###############\n\n\n");
		return "adgrouplist";
	}


	@RequestMapping(value="/editadgroup")
	public String editAdgroup(ModelMap model,HttpServletRequest request,
			@RequestParam("adgid") Integer adgroupId,
			@RequestParam(value="cid",required=false) Integer campaignId,
			@RequestParam(value="adgpage",required=false) Integer adgPage,
			@RequestParam(value="rows",required=false) Integer rows
			)
	{
		logger.debug("\n\n\n*************** Entering editAdgroup method of AdgroupController ***************\n\n\n");
		if(adgPage==null)
			adgPage=1;
		if(rows==null)
			rows = UtilConstants.CAMPAIGNS_PER_PAGE;
		AdgroupDo adgroupDo = adgroupService.getAdgroupDoById(adgroupId);
		adgroupDo.setCampaignDo(campaignService.getCampaignDoById(adgroupDo.getCampaignDo().getId()));
		//adgroupDo.setCategoryList(categoryService.getPrimeCategories());
		model.put("ProductCategoryDos", productCategoryService.getProductCategoryDosList());
		adgroupDo.setCreatedOnString(UtilityMethod.formatDateTOYYYY_MM_DD(adgroupDo.getCreatedOn()));
		model.put("adgroupDo", adgroupDo);
		model.put("adgPage", adgPage);
		model.put("rows", rows);
		model.put("cid",campaignId);
		logger.debug("\n\n\n+++++++++++++++ INFO:: +++++++++++++++\n"
				+ "adgroupId: "+adgroupId+"\n"
				+ "adgroupName: "+adgroupDo.getProductName()+"\n"
				+ "campaignName: "+adgroupDo.getCampaignDo().getName()+"\n"
				+ "productCategorySize: "+productCategoryService.getProductCategoryDosList().size()+"\n"
				+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
		logger.debug("\n\n\n############### Exiting editAdgroup method of AdgroupController ###############\n\n\n");
		return "editadgroup";
	}

	@RequestMapping(value="/updateadgroup",method=RequestMethod.POST)
	public String updateAdgroup(ModelMap model,HttpServletRequest request,@ModelAttribute("adgroupDo") AdgroupDo adgroupDo,
			@RequestParam(value="cid",required=false)Integer campaignId,
			@RequestParam(value="adgpage",required=false)Integer adgPage,
			@RequestParam(value="rows",required=false)Integer rows) throws IOException
	{
		logger.debug("\n\n\n*************** Entering updateAdgroup method of AdgroupController ***************\n\n\n");
		boolean isProductNameChanged = true;
		boolean isStatusChanged=false;

		UserDo userDo = (UserDo) request.getSession().getAttribute("sUser");
		adgroupDo.setUpdatedBy(userDo.getName());
		adgroupDo.setUpdatedOn(new Date());
		CampaignDo campaignDo = campaignService.getCampaignDoById(adgroupDo.getCampaignDo().getId());
		adgroupDo.setCampaignDo(campaignDo);

		adgroupDo.setCreatedOn(UtilityMethod.convertStringYYYY_MM_DDTODateInJava(adgroupDo.getCreatedOnString()));
		AdgroupDo oldAdgroupDo = adgroupService.getAdgroupDoById(adgroupDo.getId());

		List<AdgroupDo> adgroupDos= adgroupService.getAdgroupDosListByCampaignDo(campaignDo);
		for(AdgroupDo adgroupDoByCampaign:adgroupDos)
		{
			if(adgroupDoByCampaign.getProductName().equals(adgroupDo.getProductName()) && adgroupDoByCampaign.getId() != adgroupDo.getId())
			{
				logger.info("\n\n\n??????????????? ERROR:: adgroup with name: "+adgroupDo.getProductName()+" already exists. ???????????????\n\n\n");
				model.put("msg", "Product with name "+adgroupDo.getProductName()+" already exists!!");
				model.put("errorMsg",0);
				adgroupDo.setCampaignDo(campaignService.getCampaignDoById(adgroupDo.getCampaignDo().getId()));
				//adgroupDo.setCategoryList(categoryService.getPrimeCategories());
				model.put("ProductCategoryDos", productCategoryService.getProductCategoryDosList());
				adgroupDo.setCreatedOnString(UtilityMethod.formatDateTOYYYY_MM_DD(adgroupDo.getCreatedOn()));
				model.put("adgroupDo",adgroupDo);
				model.put("adService", adService);
				model.put("adgPage", adgPage);
				model.put("rows", rows);
				logger.debug("\n\n\n############### Exiting updateAdgroup method of AdgroupController ###############\n\n\n");
				return "editadgroup";
			}
		}

		if(!adgroupDo.getStatus().equals(oldAdgroupDo.getStatus()))
			isStatusChanged=true;

		ProductCategoryDo productCategoryDo =  productCategoryService.getProductCategoryDoByName(adgroupDo.getCategoryName());

		List<KeywordDo> newkeywordDos = new ArrayList<KeywordDo>();
		String mailMessage="";
		String mailSubject="";
		KeywordAdwordApi keywordAdwordApi = new KeywordAdwordApi();
		try{
			List<String> locationApiIds = new ArrayList<String>();
			if(campaignDo.getLocationInclude()!=null && !campaignDo.getLocationInclude().equals(""))
			{
				for(String locationId : campaignDo.getLocationInclude().split(","))
				{
					if(!locationId.equals(""))
					{
						if(campaignDo.getLocationIncludeCriteria()==4)
						{
							locationApiIds.add(locationId.split(":")[0]);
						}
						else	
							locationApiIds.add(locationId);
					}

				}
			}

		 newkeywordDos = keywordAdwordApi.getKeywordIdeas(adgroupDo.getProductName(),productCategoryDo.getId(),UtilConstants.NO_OF_KEYWORDS,locationApiIds);
		 
		 if(newkeywordDos.size() == 0)
			{
				logger.info("\n\n\n??????????????? ERROR:: No Keyword Ideas found. Category and product name does not match. ???????????????\n\n\n");
				model.put("msg", "No keyword ideas found. Category and Product Name does not match");
				model.put("errorMsg",0);
				adgroupDo.setCampaignDo(campaignService.getCampaignDoById(adgroupDo.getCampaignDo().getId()));
				//adgroupDo.setCategoryList(categoryService.getPrimeCategories());
				model.put("ProductCategoryDos", productCategoryService.getProductCategoryDosList());
				adgroupDo.setCreatedOnString(UtilityMethod.formatDateTOYYYY_MM_DD(adgroupDo.getCreatedOn()));
				model.put("adgroupDo",adgroupDo);
				model.put("adgPage", adgPage);
				model.put("rows", rows);
				logger.debug("\n\n\n############### Exiting saveAdgroup method of AdgroupController ###############\n\n\n");
				return "editadgroup";
			}
		 
		}catch(RemoteException e1)
		{

			if(e1 instanceof ApiException)
			{
				ApiException apiException = (ApiException) e1;
				model.put("msg", "Product not saved. Adword Error: "+UtilityMethod.getErrorMessageFromProperties(apiException.getFaultReason()));
				model.put("errorMsg", 0);
				logger.info("\n\n\n??????????????? ERROR:: Keyword Ideas with adgroup name :"+adgroupDo.getProductName()+" could not be fetched due to adwordError , errorMessage:"+UtilityMethod.printStackTrace(e1)+"???????????????\n\n\n");
			}
			else
			{
				model.put("msg", "Product not saved. Please contact admin");
				logger.info("\n\n\n??????????????? ERROR::  Keyword Ideas with adgroup name:"+adgroupDo.getProductName()+" could not be fetched, errorMessage:"+e1.getMessage()+"???????????????\n\n\n");
			}
			
			mailSubject="Problem with campaign "+adgroupDo.getCampaignDo().getName()+" and product "+adgroupDo.getProductName()+" !!";

			mailMessage="Greetings!!<br><br>Campaign with name <strong>"+adgroupDo.getCampaignDo().getName() +"</strong> and"
					+ " product <strong>"+adgroupDo.getProductName()+"</strong> encountered an error during fetching keyword ideas. Please review.<br>"+
					"<br>Use the below credentials to login to ARQ SME platform<br><br>"
					+ "<strong>URL: </strong> <a href=\""+UtilityMethod.getServerName(request)+"\">"+UtilityMethod.getServerName(request)+"</a><br>"
					+ "<strong>Username:</strong> admin@arq.co.in<br>"
					+ "<strong>Password:</strong> Admin</strong><br><br><br>"
					+"Exception:<br><br>"
					+UtilityMethod.printStackTrace(e1)
					+ "<br><br><br>----------------------<br>"
					+ "Thanks & Regards<br>"
					+ "ARQ Team";

			e1.printStackTrace();

			logger.debug("\n\n\n############### Exiting saveAdgroup method of AdgroupController ###############\n\n\n");
			return "forward:/listadgroup?cid="+ adgroupDo.getCampaignDo().getId();
		}
		//Retrieve keyword Ideas ends
		
		
		
		
		try{
			//Saving adGroup in the local database
			adgroupService.updateAdgroup(adgroupDo);
			if(adgroupDo.getStatus()==AdgroupDo.Status.Removed.name()) {
				model.put("msg", "Product with name '"+adgroupDo.getProductName()+"' removed successfully");
				logger.info("\n\n\n+++++++++++++++ SUCCESS:: adgroup with name: "+adgroupDo.getProductName()+" removed successfully. +++++++++++++++\n\n\n");
			}
			else {
				model.put("msg", "Product with name '"+adgroupDo.getProductName()+"' updated successfully");
				logger.info("\n\n\n+++++++++++++++ SUCCESS:: adgroup with name: "+adgroupDo.getProductName()+" updated successfully. +++++++++++++++\n\n\n");
			}
			model.put("errorMsg",1);
		}catch(Exception e)
		{
			model.put("msg", "Could not update Product. Check log for errors");
			model.put("errorMsg",0);
			e.printStackTrace();
			logger.info("\n\n\n??????????????? ERROR:: adgroup with name:"+adgroupDo.getProductName()+" could not save , errorMessage:"+e.getMessage()+"???????????????\n\n\n");
			logger.debug("\n\n\n############### Exiting updateAdgroup method of AdgroupController ###############\n\n\n");
			return "forward:/listadgroup?cid="+ adgroupDo.getCampaignDo().getId()+"&page="+adgPage+"&rows="+rows;
		}

		int noOfKeywordsAdded=-1;
		try{
			//Creating adGroup in google adwordAPI
			if(isProductNameChanged||isStatusChanged)
			{
				AdgroupAdwordApi adgroupAdwordApi = new AdgroupAdwordApi();
				adgroupAdwordApi.updateAdgroup(adgroupDo, adgroupDo.getCampaignDo().getApiId());
				if((adgroupDo.getStatus() == AdgroupDo.Status.Removed.name()) || (adgroupDo.getStatus() == AdgroupDo.Status.Unknown.name()))
				{
					logger.debug("\n\n\n############### Exiting updateAdgroup method of AdgroupController(status>2) ###############\n\n\n");
					return "forward:/listadgroup?cid="+adgroupDo.getCampaignDo().getId()+"&page="+adgPage+"&rows="+rows;
				}

				//Creating keywords in google adwordAPI

				
				List<KeywordDo> existingkeywordDos = keywordService.getKeywordDosListByAdgroupDo(adgroupDo);
				keywordService.deleteKeywordDos(existingkeywordDos);
				for(KeywordDo keywordDo : newkeywordDos)
				{
					keywordDo.setMatchType("PHRASE");
					keywordDo.setStatus(KeywordDo.Status.Enabled.name());
				}
				keywordAdwordApi.refreshKeywordList(adgroupDo,newkeywordDos);
				for(KeywordDo keywordDo : newkeywordDos)
				{
					keywordDo.setAdgroupDo(adgroupDo);
					keywordDo.setAdgroupApiId(adgroupDo.getApiId());
					keywordDo.setCreatedBy(userDo.getName());
					keywordDo.setUpdatedBy(userDo.getName());
					keywordDo.setCreatedOn(new Date());

				}

				keywordService.saveKeywordDos(newkeywordDos);
				noOfKeywordsAdded=newkeywordDos.size();
			}



			//Updating adgroup with list of keywords and adgroup api key

			adgroupService.updateAdgroup(adgroupDo);

			if(noOfKeywordsAdded == 0)
			{
				logger.info("\n\n\n??????????????? ERROR:: No Keyword Ideas found. Category and product name does not match. ???????????????\n\n\n");
				model.put("msg", "No keyword ideas found. Category and Product Name does not match");
				model.put("errorMsg",0);
				adgroupDo.setCampaignDo(campaignService.getCampaignDoById(adgroupDo.getCampaignDo().getId()));
				//adgroupDo.setCategoryList(categoryService.getPrimeCategories());
				model.put("ProductCategoryDos", productCategoryService.getProductCategoryDosList());
				adgroupDo.setCreatedOnString(UtilityMethod.formatDateTOYYYY_MM_DD(adgroupDo.getCreatedOn()));
				model.put("adgroupDo",adgroupDo);
				model.put("adService", adService);
				model.put("adgPage", adgPage);
				model.put("rows", rows);
				logger.debug("\n\n\n############### Exiting updateAdgroup method of AdgroupController ###############\n\n\n");
				return "editadgroup";
			}


			logger.info("\n\n\n+++++++++++++++ SUCCESS:: adgroup with name: "+adgroupDo.getProductName()+" is updated successfully on Google adword and updated local database accordingly. +++++++++++++++\n\n\n");
		} catch ( RemoteException | UnsupportedEncodingException e1) {
			if(e1 instanceof ApiException)
			{
				ApiException apiException = (ApiException) e1;
				model.put("msg", "Adword Error: "+UtilityMethod.getErrorMessageFromProperties(apiException.getFaultReason()));
				logger.info("\n\n\n??????????????? ERROR:: adgroup with name:"+adgroupDo.getProductName()+" could not update due to adwordError , errorMessage:"+apiException.getFaultReason()+"???????????????\n\n\n");
			}
			else
			{
				model.put("msg", "Product not saved on adword. Please contact admin");
				logger.info("\n\n\n??????????????? ERROR:: adgroup with name:"+adgroupDo.getProductName()+" could not update, errorMessage:"+e1.getMessage()+"???????????????\n\n\n");
			}

			model.put("errorMsg",0);
			e1.printStackTrace();
			logger.debug("\n\n\n############### Exiting updateAdgroup method of AdgroupController ###############\n\n\n");
			return "forward:/listadgroup?cid="+ adgroupDo.getCampaignDo().getId()+"&page="+adgPage+"&rows="+rows;
		}
		logger.debug("\n\n\n############### Exiting updateAdgroup method of AdgroupController ###############\n\n\n");
		if(campaignId!=null)
			return "forward:/listadgroup?cid="+campaignId+"&page="+adgPage+"&rows="+rows;
		else
			return "forward:/listadgroup?page="+adgPage+"&rows="+rows;
	}

	@RequestMapping(value="/syncadgroup")
	public String syncAdgroup(ModelMap model,HttpServletRequest request,
			@RequestParam(value="adgid",required = false) Integer adgroupId,
			@RequestParam(value="cid",required = false) Integer campaignId,
			@RequestParam(value="rows",required = false) Integer rows,
			@RequestParam(value="adgpage",required = false) Integer adgPage) throws IOException
	{
		logger.debug("\n\n\n*************** Entering syncAdgroup method of AdgroupController ***************\n\n\n");
		if(adgPage==null)
			adgPage=1;
		if(rows ==null) {
			rows = UtilConstants.CAMPAIGNS_PER_PAGE;
		}
			
		UserDo userDo = (UserDo) request.getSession().getAttribute("sUser");

		boolean multipleAdgroups= false;
		List<AdgroupDo> adgroupDos = new ArrayList<AdgroupDo>();
		CampaignDo campaignDo = new CampaignDo();
		if(adgroupId==null)
		{
			adgroupDos = adgroupService.getAdgroupDosList();
			multipleAdgroups = true;
		}
		else
		{
			AdgroupDo adgroupDo = adgroupService.getAdgroupDoById(adgroupId);
			adgroupDos.add(adgroupDo);
		}
		logger.debug("\n\n\n+++++++++++++++ INFO:: +++++++++++++++\n"
				+ "page: "+adgPage+"\n"
				+ "user: "+userDo.getName()+"\n"
				+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
		for(AdgroupDo adgroupDo : adgroupDos)
		{
			campaignDo = adgroupService.getCampaignDoByAdgroupId(adgroupDo.getId());
			try{
				adgroupService.syncAdgroupDetails(campaignDo, adgroupDo,userDo);
				adgroupService.updateAdgroup(adgroupDo);
				logger.info("\n\n\n+++++++++++++++ SUCCESS:: adgroup with id: "+adgroupId+" is synced +++++++++++++++\n\n\n");
			}catch(NumberFormatException|IOException e)
			{
				if(e instanceof ApiException)
				{
					ApiException apiException = (ApiException) e;
					model.put("msg", "Not Synced. Adword Error: "+UtilityMethod.getErrorMessageFromProperties(apiException.getFaultReason()));
					logger.info("\n\n\n??????????????? ERROR:: adgroup with name:"+adgroupDo.getProductName()+" could not sync due to adwordError , errorMessage:"+apiException.getFaultReason()+"???????????????\n\n\n");
				}
				else
				{
					model.put("msg", "Product not synced with adword. Please contact admin");
					logger.info("\n\n\n??????????????? ERROR:: adgroup with name:"+adgroupDo.getProductName()+" could not sync, errorMessage:"+e.getMessage()+"???????????????\n\n\n");
				}
				e.printStackTrace();

				logger.debug("\n\n\n############### Exiting syncAdgroup method of AdgroupController ###############\n\n\n");
					return "forward:listadgroup?page="+adgPage+"&rows="+rows;
			} 
		}

		model.put("errorMsg", 1);
		if(multipleAdgroups==true)
		{
			model.put("msg","All adgroups for campaign with name '"+campaignDo.getName()+"' synced with Google Adwords account");
			logger.info("\n\n\n+++++++++++++++ SUCCESS:: all adgroups for campaign with name: "+campaignDo.getName()+" synced with Google Adwords account +++++++++++++++\n\n\n");
		}

		else
		{
			if((adgroupDos.get(0).getStatus() == AdgroupDo.Status.Removed.name())||(adgroupDos.get(0).getStatus() == AdgroupDo.Status.Unknown.name()))
			{
				model.put("msg","Product with name '"+adgroupDos.get(0).getProductName()+"' is not present/removed in Google Adwords account. Removed from here.");
				logger.info("\n\n\n??????????????? ERROR:: adgroup with name:"+adgroupDos.get(0).getProductName()+" is not present/removed in Google Adwords account. Removed from here. ???????????????\n\n\n");
			}
			else
			{
				model.put("msg","Product with name '"+adgroupDos.get(0).getProductName()+"' synced with Google Adwords account");
				logger.info("\n\n\n+++++++++++++++ SUCCESS:: adgroup with name:"+adgroupDos.get(0).getProductName()+" is synced with Google Adwords account. +++++++++++++++\n\n\n");
			}
		}
		logger.debug("\n\n\n############### Exiting syncAdgroup method of AdgroupController ###############\n\n\n");
		if(adgroupId==null)
			return "forward:listadgroup?page=1&rows="+rows;
		else if(campaignId == null)
			return "forward:listadgroup?page="+adgPage+"&rows="+rows;
		else
			return "forward:listadgroup?cid="+campaignId+"&page="+adgPage+"&rows="+rows;
	}
	
	@RequestMapping(value="/adgroupdetails")
	public String adgroupDetails(HttpSession session, ModelMap model,
			@RequestParam(value="page",required=false) Integer page,
			@RequestParam(value = "rows", required = false) Integer rows,
			@RequestParam(value="adgid",required=false) Integer adgroupId,
			@RequestParam(value="showall",required=false) Integer showAll,
			@RequestParam(value = "col", required = false) String columnName,
			@RequestParam(value = "o", required = false) Integer orderBy) {
		logger.debug("\n\n\n*************** Entering adgroupDetails method of AdgroupController ***************\n\n\n");
		Integer totalRecords = 0;
		if(page==null) {
			page=1;
		}
		if(rows == null) {
			rows = UtilConstants.ADGROUPS_PER_PAGE;
		}
		if(columnName==null || columnName.equals("")) {
			session.removeAttribute("col");
			session.removeAttribute("o");
		}
		Integer adgroupPerPage = rows;
		
		List<AdgroupDo> adgroupDos = new ArrayList<AdgroupDo>();
		Boolean showAllAdgroups = false;
		if(adgroupId==null || showAll != null) {
			showAllAdgroups = true;
			UserDo userDo= (UserDo) session.getAttribute("sUser");
			UserRoleDo userRoleDo = userService.getUserRoleDoByUserDo(userDo);
			if(userRoleDo.getRole().equals("ROLE_ADMIN")) {
				totalRecords = adgroupService.countAdGroupDosForAdmin().intValue();
				if(totalRecords>0) {
					if(columnName!=null && !columnName.equals("")) {
						session.setAttribute("col", columnName);
						session.setAttribute("o", orderBy);
						adgroupDos = adgroupService.getAdgroupDosListForAdminByPageAndSortedByColumn(page, adgroupPerPage, columnName, orderBy);
					}else {
						adgroupDos = adgroupService.getAdgroupDosListForAdminByPage(page, adgroupPerPage);
					}
				}
			}else {
				totalRecords = adgroupService.countAdGroupDosByUserDo(userDo).intValue();
				if(totalRecords>0) {
					if(columnName!=null && !columnName.equals("")) {
						session.setAttribute("col", columnName);
						session.setAttribute("o", orderBy);
						adgroupDos = adgroupService.getAdgroupDosListByUserDoAndPageAndSortedByColumn(userDo, page, adgroupPerPage, columnName, orderBy);
					}else {
						adgroupDos = adgroupService.getAdgroupDosListByUserDoAndPage(userDo, page, adgroupPerPage);
					}
				}
			}
		}else {
			totalRecords = 1;
			AdgroupDo adgroupDo = adgroupService.getAdgroupDoById(adgroupId);
			adgroupDos.add(adgroupDo);
			model.put("adgroupId", adgroupId);
			model.put("productName", adgroupDo.getProductName());
			model.put("campaignId", adgroupService.getCampaignDoByAdgroupId(adgroupDo.getId()).getId());
			model.put("campaignName", adgroupService.getCampaignDoByAdgroupId(adgroupDo.getId()).getName());
		}
		for(AdgroupDo adgroupDo: adgroupDos) {
			List<KeywordDo> keywordDos = keywordService.getKeywordDosListByAdgroupDo(adgroupDo);
			String keywordTextList = "";
			for(KeywordDo keywordDo : keywordDos)
			{
				keywordTextList+=keywordDo.getText()+",";
			}
			adgroupDo.setKeywordNames(keywordTextList);
			adgroupDo.setAdDos(adService.getAdDosListByAdgroupDo(adgroupDo));
			for(AdDo adDo : adgroupDo.getAdDos())
			{
				adDo.setCreatedOnString(UtilityMethod.formatDateTOYYYY_MM_DD(adDo.getCreatedOn()));
			}
		}
 		session.setAttribute("totalRecords", totalRecords);
		session.setAttribute("recordsPerPage", adgroupPerPage);
		session.setAttribute("pageNumber", page);
		model.put("adDo",new AdDo());
		model.put("rows", rows);
		model.put("showAllAdgroups", showAllAdgroups);
		model.put("adgroupDos", adgroupDos);
		logger.debug("\n\n\n############### Exiting adgroupDetails method of AdgroupController ###############\n\n\n");
		return "adgroupdetails";
	}
	
	@RequestMapping(value="/changeadgroupstatus")
	public String changeAdgroupStatus(
			ModelMap model,
			@RequestParam(value="adgid") String adgroupIds,
			@RequestParam(value="st") Integer status,
			@RequestParam(value="rows",required=false) Integer rows,
			@RequestParam(value="cid",required=false) Integer cid,
			@RequestParam(value="adgpage",required=false) Integer adgPage
			) {
		logger.debug("\n\n\n*************** Entering changeAdgroupStatus method of AdgroupController ***************\n\n\n");
		if(rows==null)
			rows=UtilConstants.CAMPAIGNS_PER_PAGE;
		String[] adgroupIdList = adgroupIds.split("\\|");
		Integer adgroupCount = 0;
		for(String adgid:adgroupIdList) {
			AdgroupDo adgroupDo = adgroupService.getAdgroupDoById(Integer.parseInt(adgid));
			adgroupCount+=1;
			AdgroupAdwordApi adgroupAdwordApi = new AdgroupAdwordApi();
			switch(status) {
			case 1:
				adgroupDo.setStatus(AdgroupDo.Status.Enabled.name());
				adgroupService.updateAdgroup(adgroupDo);
				try {
					adgroupAdwordApi.updateAdgroup(adgroupDo, adgroupService.getCampaignDoByAdgroupId(adgroupDo.getId()).getId().longValue());
					model.put("msg", "Status of product '" + adgroupDo.getProductName() + "' is set to <strong>Enabled</strong>.");
					model.put("errorMsg", 1);
					logger.info("\n\n\n+++++++++++++++ Status of advertiser '" + adgroupDo.getProductName() + "' is set to 'Enabled'. +++++++++++++++\n\n\n");
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 2:
				adgroupDo.setStatus(AdgroupDo.Status.Paused.name());
				adgroupService.updateAdgroup(adgroupDo);
				try {
					adgroupAdwordApi.updateAdgroup(adgroupDo, adgroupService.getCampaignDoByAdgroupId(adgroupDo.getId()).getId().longValue());
					model.put("msg", "Status of product '" + adgroupDo.getProductName()+ "' is set to <code><strong>Paused</strong></code>.");
					model.put("errorMsg", 1);
					logger.info("\n\n\n+++++++++++++++ Status of product '" + adgroupDo.getProductName() + "' is set to 'Paused'. +++++++++++++++\n\n\n");
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 3:
				List<AdDo> adDos = adService.getAdDosListByAdgroupDo(adgroupDo);
				for(AdDo adDo: adDos) {
					adDo.setStatus(AdDo.Status.Disabled.name());
					adService.updateAdDo(adDo);
				}
				adgroupDo.setStatus(AdgroupDo.Status.Removed.name());
				adgroupService.updateAdgroup(adgroupDo);
				try {
					adgroupAdwordApi.updateAdgroup(adgroupDo, adgroupService.getCampaignDoByAdgroupId(adgroupDo.getId()).getId().longValue());
					model.put("msg", "Product '" +adgroupDo.getProductName() + "' is removed successfully");
					model.put("errorMsg", 1);
					logger.info("\n\n\n+++++++++++++++ SUCCESS:: Product '" + adgroupDo.getProductName()
					+ "' removed successfully. +++++++++++++++\n\n\n");
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		}
		if(adgroupIdList.length >1) {
			if(status ==1)
				model.put("msg", "Status of <strong>" + adgroupCount + "</strong> products set to <strong>Enabled</strong>");
			else if (status==2)
				model.put("msg", "Status of <strong>" + adgroupCount + "</strong> products set to <code><strong>Paused</strong></code>");
			else
				model.put("msg", "<strong>" + adgroupCount + "</strong> products hase been <strong>Removed</strong> successfully");
			model.put("errorMsg", 1);
		}
		logger.debug("\n\n\n############### Exiting changeadgroupstatus method of AdgroupController ###############\n\n\n");
		if(cid!=null)
			return "forward:/listadgroup?page="+adgPage+"&rows="+rows+"&cid="+cid;
		else
			return "forward:/listadgroup?page="+adgPage+"&rows="+rows;
	}

}