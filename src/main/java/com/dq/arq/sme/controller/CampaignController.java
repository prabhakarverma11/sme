package com.dq.arq.sme.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

import com.dq.arq.sme.adwordapi.CampaignAdwordApi;
import com.dq.arq.sme.domain.AdgroupDo;
import com.dq.arq.sme.domain.CampaignDo;
import com.dq.arq.sme.domain.LocationDo;
import com.dq.arq.sme.domain.UserDo;
import com.dq.arq.sme.domain.UserRoleDo;
import com.dq.arq.sme.services.AdgroupService;
import com.dq.arq.sme.services.CampaignService;
import com.dq.arq.sme.services.LocationService;
import com.dq.arq.sme.services.UserRoleMapService;
import com.dq.arq.sme.services.UserRoleService;
import com.dq.arq.sme.services.UserService;
import com.dq.arq.sme.util.UtilConstants;
import com.dq.arq.sme.util.UtilityMethod;
import com.google.api.ads.adwords.axis.v201607.cm.ApiException;
import com.google.api.ads.adwords.lib.utils.ReportDownloadResponseException;
import com.google.api.ads.adwords.lib.utils.ReportException;
import com.google.api.ads.common.lib.conf.ConfigurationLoadException;
import com.google.api.ads.common.lib.exception.OAuthException;
import com.google.api.ads.common.lib.exception.ValidationException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * @author manumishra
 * 
 */
@Controller
public class CampaignController {
	//final static Logger logger = LoggerFactory.getLogger(CampaignController.class);
	final static Logger logger = LoggerFactory.getLogger("LogTesting");

	@Autowired
	UserService userService;

	@Autowired
	CampaignService campaignService;

	@Autowired
	LocationService locationService;

	@Autowired
	UserRoleMapService userRoleMapService;
	
	@Autowired
	UserRoleService userRoleService;

	@Autowired
	AdgroupService adgroupService;

	/**
	 * 
	 * @param model
	 * @return
	 * 
	 *         Sets the India locations for locationInclude and locationExclude
	 *         option in the campaignDo object Calls the view with form for
	 *         getting campaign details
	 */
	@RequestMapping(value = "/addcampaign")
	public String addCampaign(ModelMap model) {
		logger.debug("\n\n\n*************** Entering addCampaign method of CampaignController ***************\n\n\n");
		CampaignDo campaignDo = new CampaignDo();

		List<LocationDo> locationDos = locationService.getLocationsInIndia();

		Map<Integer, String> indiaLocationList = new LinkedHashMap<Integer, String>();
		for (LocationDo locationDo : locationDos) {
			indiaLocationList.put(locationDo.getCriteriaId(), locationDo.getCanonicalName());
		}

		List<LocationDo> countryDos = locationService.getCountryList();

		Map<String, String> countryList = new LinkedHashMap<String, String>();
		for (LocationDo locationDo : countryDos) {
			countryList.put(locationDo.getCountryCode(), locationDo.getName());
		}

		UserRoleDo userRoleDo = userRoleService.getUserRoleDoByRole(UserRoleDo.Role.ROLE_USER);
		List<UserDo> userDos = userRoleMapService.getUserDosListByUserRoleDo(userRoleDo);
		
		model.put("userDos", userDos);
		model.put("countryList", countryList);
		model.put("indiaLocationList", indiaLocationList);
		model.put("campaignDo", campaignDo);
		logger.info("\n\n\n+++++++++++++++ INFO:: +++++++++++++++\n" + "userDos.size(): " + userDos.size() + "\n"
				+ "countryList.size(): " + countryList.size() + "\n" + "indiaLocationList.size(): "
				+ indiaLocationList.size() + "\n" + "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
		logger.debug("\n\n\n############### Exiting addCampaign method of CampaignController ###############\n\n\n");
		return "addcampaign";
	}

	/**
	 * 
	 * @param model
	 * @param request
	 * @param campaignDo
	 * @return
	 * 
	 * 		Retrieves the campaignDo object from view containing details of
	 *         the campaign Validates for duplicate campaign name Campaign is
	 *         saved in the Google API Campaign is then saved in the local DB
	 *         alongwith apikey returned from google API
	 * @throws IOException 
	 */
	@RequestMapping(value = "/savecampaign", method = RequestMethod.POST)
	public String saveAdvertiser(ModelMap model, HttpServletRequest request,
			@ModelAttribute("campaignDo") CampaignDo campaignDo) throws IOException {
		logger.debug(
				"\n\n\n*************** Entering saveAdvertiser method of CampaignController ***************\n\n\n");
		if (campaignService.getCampaignDoByName(campaignDo.getName()) != null) {
			logger.info("\n\n\n??????????????? ERROR:: Advertiser with name: " + campaignDo.getName()
					+ " already exists. ???????????????\n\n\n");
			model.put("msg", "Advertiser with name " + campaignDo.getName() + " already exists!!");
			model.put("errorMsg", 0);

			List<LocationDo> locationDos = locationService.getLocationsInIndia();
			Map<Integer, String> indiaLocationList = new LinkedHashMap<Integer, String>();
			for (LocationDo locationDo : locationDos) {
				indiaLocationList.put(locationDo.getCriteriaId(), locationDo.getCanonicalName());
			}
			List<LocationDo> countryDos = locationService.getCountryList();

			Map<String, String> countryList = new LinkedHashMap<String, String>();
			for (LocationDo locationDo : countryDos) {
				countryList.put(locationDo.getCountryCode(), locationDo.getName());
			}

			UserRoleDo userRoleDo = userRoleService.getUserRoleDoByRole(UserRoleDo.Role.ROLE_USER);
			List<UserDo> userDos = userRoleMapService.getUserDosListByUserRoleDo(userRoleDo);
			
			model.put("userDos", userDos);
			model.put("countryList", countryList);
			model.put("indiaLocationList", indiaLocationList);
			model.put("campaignDo", campaignDo);
			model.put("clientId", request.getParameter("clientId"));
			logger.info("\n\n\n+++++++++++++++ SUCCESS:: return to addcampaign.jsp with details: +++++++++++++++\n"
					+ "userDos.size(): " + userDos.size() + "\n" + "countryList.size(): " + countryList.size() + "\n"
					+ "indiaLocationList.size(): " + indiaLocationList.size() + "\n"
					+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
			logger.debug(
					"\n\n\n############### Exiting addCampaign method of CampaignController ###############\n\n\n");
			return "addcampaign";
		}

		UserDo userDo = (UserDo) request.getSession().getAttribute("sUser");
		UserRoleDo userRoleDo = (UserRoleDo) request.getSession().getAttribute("userRoleDo");
		if (userRoleDo.getRole().equals(UserRoleDo.Role.ROLE_ADMIN.name()))
			userDo = userService.getUserDoById(Integer.parseInt(request.getParameter("clientId")));

		campaignDo.setCreatedBy(userDo.getName());
		campaignDo.setCreatedOn(new Date());
		campaignDo.setUpdatedBy(userDo.getName());
		campaignDo.setUpdatedOn(new Date());
		campaignDo.setUserDo(userDo);

		campaignDo.setStartDate(UtilityMethod.convertStringMM_DD_YYYYTODateInJava(campaignDo.getStartDateString()));
		campaignDo.setEndDate(UtilityMethod.convertStringMM_DD_YYYYTODateInJava(campaignDo.getEndDateString()));
		campaignDo.setStatus(CampaignDo.Status.Paused.name()); // Setting default
		// status to Paused
		Integer campaignId = 0;

		if (campaignDo.getLocationIncludeCriteria() == 2) {
			LocationDo locationDo = locationService.getLocationDoByName("India");
			campaignDo.setLocationInclude(locationDo.getCriteriaId() + ",");
		}
		if (campaignDo.getLocationExcludeCriteria() == 2) {
			LocationDo locationDo = locationService.getLocationDoByName("India");
			campaignDo.setLocationExclude(locationDo.getCriteriaId() + ",");
		}
		CampaignAdwordApi campaignAdwordApi;
		try {
			campaignAdwordApi = new CampaignAdwordApi();
		} catch (OAuthException | ValidationException | ConfigurationLoadException e) {
			model.put("msg", "Could not add Advertiser. Check log for errors");
			model.put("errorMsg", 0);
			e.printStackTrace();
			logger.info("??????????????? ERROR:: Caught exception in CampaignAdwordApi constructor: "+e.getMessage()+" ???????????????");
			logger.debug(
					"\n\n\n############### Exiting addCampaign method of CampaignController ###############\n\n\n");
			return "forward:/listcampaign";
		}

		try {
			// Saving campaign in the local database
			campaignId = campaignService.saveCampaignDo(campaignDo);
			model.put("msg", "Details of advertiser with name " + campaignDo.getName() + " added successfully");
			model.put("errorMsg", 1);
			logger.info("\n\n\n+++++++++++++++ SUCCESS:: Advertiser with details: +++++++++++++++\n" + "id: "
					+ campaignId + "\n" + "name: " + campaignDo.getName() + " is saved successfully.\n"
					+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
		} catch (Exception e) {
			model.put("msg", "Could not add Advertiser. Check log for errors");
			model.put("errorMsg", 0);
			e.printStackTrace();
			logger.info("\n\n\n??????????????? ERROR:: Advertiser with name:" + campaignDo.getName()
					+ " could not save , errorMessage:" + e.getMessage() + "???????????????\n\n\n");
			logger.debug(
					"\n\n\n############### Exiting addCampaign method of CampaignController ###############\n\n\n");
			return "forward:/listcampaign";
		}

		try {
			String locArray[] = campaignDo.getLocationInclude().split(":");
			LocationDo locationDo = null;
			if (locArray.length < 4 && campaignDo.getLocationIncludeCriteria() == 4)
				locationDo = locationService.getLocationDoById(Integer.parseInt(locArray[0]));
			// Saving campaign in the Google Adword API
			campaignAdwordApi.createCampaign(campaignDo, locationDo);

			// Updating the campaign with Google Adword API Key
			campaignService.updateCampaign(campaignDo);
			logger.info("\n\n\n+++++++++++++++ SUCCESS:: campaign with details: +++++++++++++++\n" + "id: " + campaignId
					+ "\n" + "apiId: " + campaignDo.getApiId() + "\n" + "name: " + campaignDo.getName()
					+ " is updated successfully.\n" + "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
		} catch (RemoteException e1) {

			campaignDo.setStatus(CampaignDo.Status.Removed.name());
			campaignService.updateCampaign(campaignDo);

			if (e1 instanceof ApiException) {
				ApiException apiException = (ApiException) e1;
				model.put("msg", "Advertiser not saved. Adword Error: " + UtilityMethod.getErrorMessageFromProperties(apiException.getFaultReason()));
				logger.info("\n\n\n??????????????? ERROR:: Advertiser with name:" + campaignDo.getName()
						+ " could not save due to adwordError , errorMessage:" + apiException.getFaultReason()
						+ "???????????????\n\n\n");
			} else {
				model.put("msg", "Advertiser not saved with adword. Please contact admin");
				logger.info("\n\n\n??????????????? ERROR:: Advertiser with name:" + campaignDo.getName()
						+ " could not save to adword , errorMessage:" + e1.getMessage() + "???????????????\n\n\n");
			}
			model.put("errorMsg", 0);
			e1.printStackTrace();
			logger.debug(
					"\n\n\n############### Exiting addCampaign method of CampaignController ###############\n\n\n");
			return "forward:/listcampaign";
		}
		logger.debug("\n\n\n############### Exiting addCampaign method of CampaignController ###############\n\n\n");
		return "forward:/addadgroup?cid=" + campaignId;
	}

	/**
	 * 
	 * @param model
	 * @param request
	 * @param session
	 * @return
	 * 
	 * 		Displays the list of all campaigns
	 */
	@RequestMapping(value = "/listcampaign")
	public String adList(ModelMap model, HttpServletRequest request, HttpSession session,
			@RequestParam(value = "error", required = false) Integer error,
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "rows", required = false) Integer rows,
			@RequestParam(value = "campid", required = false) Integer cid,
			@RequestParam(value = "userId", required = false) Integer usrId,
			@RequestParam(value = "col", required = false) String columnName,
			@RequestParam(value = "o", required = false) Integer orderBy
			) {

		logger.debug("\n\n\n*************** Entering adList method of CampaignController ***************\n\n\n");
		if (page == null)
			page = 1;
		if(rows==null)
			rows =UtilConstants.CAMPAIGNS_PER_PAGE;
		model.put("rows", rows);
		if(columnName==null || columnName.equals("")) {
			session.removeAttribute("col");
			session.removeAttribute("o");
		}
		Integer totalRecords = 0;
		Integer campaignsPerPage = rows; 
		
		UserDo userDo = (UserDo) session.getAttribute("sUser");
		UserRoleDo userRoleDo = (UserRoleDo) session.getAttribute("userRoleDo");
		List<CampaignDo> campaignDos = new ArrayList<CampaignDo>();
	
		//For ordering by column name
		
		
		if (userRoleDo.getRole().equals(UserRoleDo.Role.ROLE_ADMIN.name())) {
			if(cid!=null) {
				CampaignDo campaignDo = campaignService.getCampaignDoById(cid);
				campaignDos.add(campaignDo);
				model.put("showAllCampaigns", false);
				totalRecords = 1;
			}else if(usrId!=null) {
				UserDo userDoForFilter = userService.getUserDoById(usrId);
				totalRecords = campaignService.countCampaignDosByUserDo(userDoForFilter).intValue();
				if (totalRecords > 0) {
					if(columnName!=null && !columnName.equals("")) {
						session.setAttribute("col", columnName);
						session.setAttribute("o", orderBy);
						campaignDos = campaignService.getCampaignDosListByUserDoAndPageAndSortedByColumn(userDoForFilter, page, campaignsPerPage, columnName, orderBy);
					}else
						campaignDos = campaignService.getCampaignDosListByUserDoAndPage(userDoForFilter, page,campaignsPerPage);
				}
				model.put("showAllCampaigns", false);
				model.put("accountManager", userDoForFilter.getName());
				session.setAttribute("userId", usrId);
			}else {
				model.put("showAllCampaigns", true);
				session.removeAttribute("userId");
				totalRecords = campaignService.countCampaignDosByAdmin().intValue();
				if (totalRecords > 0) {
					if(columnName!=null && !columnName.equals("")) {
						session.setAttribute("col", columnName);
						session.setAttribute("o", orderBy);
						campaignDos = campaignService.getCampaignDosListForAdminByPageAndSortedByColumn(page,campaignsPerPage,columnName,orderBy);
					}else {
						campaignDos = campaignService.getCampaignDosListForAdminByPage(page,campaignsPerPage);
					}
				}
			}
		} else {
			model.put("showAllCampaigns", true);
			totalRecords = campaignService.countCampaignDosByUserDo(userDo).intValue();
			if (totalRecords > 0) {
				if(columnName!=null && !columnName.equals("")) {
					session.setAttribute("col", columnName);
					session.setAttribute("o", orderBy);
					campaignDos = campaignService.getCampaignDosListByUserDoAndPageAndSortedByColumn(userDo, page,campaignsPerPage,columnName,orderBy);
				}else {
					campaignDos = campaignService.getCampaignDosListByUserDoAndPage(userDo, page,campaignsPerPage);
				}
			}
		}

		for (CampaignDo campaignDo : campaignDos) {
			campaignDo.setStartDateString(UtilityMethod.formatDateTOMM_DD_YYYY(campaignDo.getStartDate()));
			campaignDo.setEndDateString(UtilityMethod.formatDateTOMM_DD_YYYY(campaignDo.getEndDate()));
		}
		
		model.put("campaignDos", campaignDos);

		int totalPages = 0;
		if(totalRecords != 0) {
			if ((totalRecords % campaignsPerPage) == 0) {
				totalPages = (totalRecords / campaignsPerPage);
			} else {
				totalPages = (totalRecords / campaignsPerPage) + 1;
			}
			if (totalRecords !=0 && page > totalPages) {
				page = totalPages;
				logger.debug(
						"\n\n\n############### Exiting adList method of CampaignController (forworded to adList method again) ###############\n\n\n");
				return "forward:/listcampaign" + "?page=" + page+"&col="+columnName+"&o="+orderBy+"&rows="+rows;
			}
		}
		session.setAttribute("totalRecords", totalRecords);
		session.setAttribute("recordsPerPage", campaignsPerPage);
		session.setAttribute("pageNumber", page);
		if(error!=null)
		{
			model.put("errorMsg", 0);
			model.put("msg", "Encountered a network error. Please contact admin.");
		}
		logger.info("\n\n\n+++++++++++++++ INFO:: +++++++++++++++\n" + "user: " + userDo.getName() + "\n" + "userRole: "
				+ userRoleDo.getRole() + "\n" + "page: " + page + "\n" + "totalRecords: " + totalRecords + "\n"
				+ "itemsInCampaignDos: " + campaignDos.size() + "\n"
				+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
		logger.debug("\n\n\n############### Exiting adList method of CampaignController ###############\n\n\n");
		return "campaignlist";
	}

	/**
	 * @param model
	 * @param request
	 * @param campaignId
	 * @return
	 * 
	 * 		Calls the view for updating campaign detail
	 */
	@RequestMapping(value = "/editcampaign")
	public String editCampaign(ModelMap model, HttpServletRequest request,
			@RequestParam(required = true, value = "cid") Integer campaignId,
			@RequestParam(required = true, value = "cpage") Integer cPage,
			@RequestParam(required = true, value = "rows") Integer rows
			) {
		logger.debug("\n\n\n*************** Entering editCampaign method of CampaignController ***************\n\n\n");
		if(rows == null)
			rows=UtilConstants.CAMPAIGNS_PER_PAGE;
		if(cPage == null)
			cPage=1;
		CampaignDo campaignDo = campaignService.getCampaignDoById(campaignId);
		List<LocationDo> locationDos = locationService.getLocationsInIndia();
		Map<Integer, String> indiaLocationList = new LinkedHashMap<Integer, String>();
		for (LocationDo locationDo : locationDos) {
			indiaLocationList.put(locationDo.getCriteriaId(), locationDo.getCanonicalName());
		}

		campaignDo.setStartDateString(UtilityMethod.formatDateTOMM_DD_YYYY(campaignDo.getStartDate()));
		campaignDo.setEndDateString(UtilityMethod.formatDateTOMM_DD_YYYY(campaignDo.getEndDate()));
		campaignDo.setCreatedOnString(UtilityMethod.formatDateTOMM_DD_YYYY(campaignDo.getCreatedOn()));

		List<LocationDo> countryDos = locationService.getCountryList();

		Map<String, String> countryList = new LinkedHashMap<String, String>();
		for (LocationDo locationDo : countryDos) {
			countryList.put(locationDo.getCountryCode(), locationDo.getName());
		}

		model.put("countryList", countryList);
		model.put("oldCampaignName", campaignDo.getName());
		model.put("indiaLocationList", indiaLocationList);
		model.put("campaignDo", campaignDo);
		model.put("cpage", cPage);
		model.put("rows", rows);
		logger.info("\n\n\n+++++++++++++++ INFO:: +++++++++++++++\n" + "oldCampaignName: " + campaignDo.getName() + "\n"
				+ "countryList.size(): " + countryList.size() + "\n" + "indiaLocationList.size(): "
				+ indiaLocationList.size() + "\n" + "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
		logger.debug("\n\n\n############### Exiting editCampaign method of CampaignController ###############\n\n\n");
		return "editcampaign";
	}

	/**
	 * 
	 * @param model
	 * @param request
	 * @param campaignDo
	 * @return
	 * 
	 * 		Updated the campaign in local db Locations are updated in adword
	 *         only if locations were changed New budget is created in adword
	 *         only if budget amount was changed
	 * @throws IOException 
	 * 
	 */
	@RequestMapping(value = "/updatecampaign", method = RequestMethod.POST)
	public String updateCampaign(ModelMap model, HttpServletRequest request,
			@ModelAttribute("campaignDo") CampaignDo campaignDo,
			@RequestParam(value = "cpage") Integer cPage,
			@RequestParam(value = "rows") Integer rows
			) throws IOException {
		logger.debug(
				"\n\n\n*************** Entering updateCampaign method of CampaignController ***************\n\n\n");
		if (campaignService.getCampaignDoByName(campaignDo.getName()) != null
				&& !campaignDo.getName().equals(request.getParameter("oldCampaignName"))) {
			String newCampaignDoName = campaignDo.getName().toString();
			logger.info("\n\n\n??????????????? ERROR:: Advertiser with name: " + newCampaignDoName
					+ " already exists. ???????????????\n\n\n");
			model.put("msg", "Advertiser with name '" + newCampaignDoName + "' already exists!!");
			model.put("errorMsg", 0);

			List<LocationDo> locationDos = locationService.getLocationsInIndia();
			Map<Integer, String> indiaLocationList = new LinkedHashMap<Integer, String>();
			for (LocationDo locationDo : locationDos) {
				indiaLocationList.put(locationDo.getCriteriaId(), locationDo.getCanonicalName());
			}
			List<LocationDo> countryDos = locationService.getCountryList();

			Map<String, String> countryList = new LinkedHashMap<String, String>();
			for (LocationDo locationDo : countryDos) {
				countryList.put(locationDo.getCountryCode(), locationDo.getName());
			}

			model.put("countryList", countryList);
			model.put("campaignDo", campaignDo);
			model.put("indiaLocationList", indiaLocationList);
			logger.info("\n\n\n+++++++++++++++ SUCCESS:: called editcampaign.jsp with details: +++++++++++++++\n"
					+ "newCampaignName: " + campaignDo.getName() + "\n" + "countryList.size(): " + countryList.size()
					+ "\n" + "indiaLocationList.size(): " + indiaLocationList.size() + "\n"
					+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
			logger.debug(
					"\n\n\n############### Exiting updateCampaign method of CampaignController ###############\n\n\n");
			return "editcampaign";
		}

		boolean isLocationChanged = false;
		boolean isBudgetChanged = false;

		UserDo userDo = (UserDo) request.getSession().getAttribute("sUser");
		campaignDo.setUpdatedBy(userDo.getName());
		campaignDo.setUpdatedOn(new Date());

		campaignDo.setStartDate(UtilityMethod.convertStringMM_DD_YYYYTODateInJava(campaignDo.getStartDateString()));
		campaignDo.setEndDate(UtilityMethod.convertStringMM_DD_YYYYTODateInJava(campaignDo.getEndDateString()));
		campaignDo.setCreatedOn(UtilityMethod.convertStringMM_DD_YYYYTODateInJava(campaignDo.getCreatedOnString()));

		if (campaignDo.getLocationIncludeCriteria() == 2) {
			LocationDo locationDo = locationService.getLocationDoByName("India");
			campaignDo.setLocationInclude(locationDo.getCriteriaId() + ",");
		}
		if (campaignDo.getLocationExcludeCriteria() == 2) {
			LocationDo locationDo = locationService.getLocationDoByName("India");
			campaignDo.setLocationExclude(locationDo.getCriteriaId() + ",");
		}

		CampaignDo oldCampaignDo = campaignService.getCampaignDoById(campaignDo.getId());
		if (campaignDo.getLocationExcludeCriteria() == oldCampaignDo.getLocationExcludeCriteria()
				&& campaignDo.getLocationIncludeCriteria() == oldCampaignDo.getLocationIncludeCriteria()
				&& campaignDo.getLocationInclude().equals(oldCampaignDo.getLocationInclude())
				&& campaignDo.getLocationExclude().equals(oldCampaignDo.getLocationExclude())) {
			isLocationChanged = false;
		} else {
			isLocationChanged = true;
		}

		if (!campaignDo.getBudgetAmount().equals(oldCampaignDo.getBudgetAmount()))
			isBudgetChanged = true;

		CampaignAdwordApi campaignAdwordApi;
		try {
			campaignAdwordApi = new CampaignAdwordApi();
		} catch (OAuthException | ValidationException | ConfigurationLoadException e) {
			model.put("msg", "Could not update Advertiser. Check log for errors");
			model.put("errorMsg", 0);
			e.printStackTrace();
			logger.info("??????????????? ERROR:: Caught exception in CampaignAdwordApi constructor: "+e.getMessage()+" ???????????????");
			logger.debug(
					"\n\n\n############### Exiting updateCampaign method of CampaignController ###############\n\n\n");
			return "forward:/listcampaign";
		}

		try {
			// Saving campaign in the local database
			campaignService.updateCampaign(campaignDo);
		} catch (Exception e) {
			model.put("msg", "Could not save Campaign. Check log for errors");
			model.put("errorMsg", 0);
			e.printStackTrace();
			logger.info("\n\n\n??????????????? ERROR:: Advertiser with name:" + campaignDo.getName()
					+ " could not save , errorMessage:" + e.getMessage() + "???????????????\n\n\n");
			logger.debug(
					"\n\n\n############### Exiting updateCampaign method of CampaignController ###############\n\n\n");
			return "forward:/listcampaign?page=" + cPage+"&rows="+rows;
		}

		try {
			// Saving campaign in the Google Adword API
			String locArray[] = campaignDo.getLocationInclude().split(":");
			LocationDo locationDo = null;
			if (locArray.length < 4 && campaignDo.getLocationIncludeCriteria() == 4)
				locationDo = locationService.getLocationDoById(Integer.parseInt(locArray[0]));

			campaignAdwordApi.updateCampaign(campaignDo, locationDo, isLocationChanged, isBudgetChanged);

			// Updating the campaign with Google Adword API Key
			campaignService.updateCampaign(campaignDo);
			model.put("msg", "advertiser '"+campaignDo.getName()+"' is updated successfully.");
			model.put("errorMsg", 1);
			logger.info("\n\n\n+++++++++++++++ SUCCESS:: Advertiser with details: +++++++++++++++\n" + "id: "
					+ campaignDo.getId() + "\n" + "apiId: " + campaignDo.getApiId() + "\n" + "name: "
					+ campaignDo.getName() + " is updated successfully.\n"
					+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
		} catch (RemoteException e1) {

			campaignDo.setStatus(CampaignDo.Status.Removed.name());
			campaignService.updateCampaign(campaignDo);

			if (e1 instanceof ApiException) {
				ApiException apiException = (ApiException) e1;
				model.put("msg", "Advertiser not updated. Adword Error: " + UtilityMethod.getErrorMessageFromProperties(apiException.getFaultReason()));
				logger.info("\n\n\n??????????????? ERROR:: Advertiser with name:" + campaignDo.getName()
						+ " could not save due to adwordError , errorMessage:" + apiException.getFaultReason()
						+ "???????????????\n\n\n");
			} else {
				model.put("msg", "Advertiser not updated with adword. Please contact admin");
				logger.info("\n\n\n??????????????? ERROR:: Advertiser with name:" + campaignDo.getName()
						+ " could not update, errorMessage:" + e1.getMessage() + "???????????????\n\n\n");
			}
			model.put("errorMsg", 0);
			e1.printStackTrace();
			logger.debug(
					"\n\n\n############### Exiting updateCampaign method of CampaignController ###############\n\n\n");
			return "forward:/listcampaign?page=" + cPage+"&rows="+rows;
		}
		logger.debug("\n\n\n############### Exiting updateCampaign method of CampaignController ###############\n\n\n");
		return "forward:/listcampaign?page=" + cPage+"&rows="+rows;
	}

	
	/**
	 * Sync the campaign selected as well as could be used to sync all the campaigns in one go.
	 * @param model
	 * @param request
	 * @param campaignId
	 * @param cPage
	 * @param rows
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/synccampaign")
	public String syncCampaign(ModelMap model, HttpServletRequest request,
			@RequestParam(value = "cid", required = false) Integer campaignId,
			@RequestParam(value = "cpage", required = false) Integer cPage,
			@RequestParam(value = "rows", required = false) Integer rows) throws IOException {
		logger.debug("\n\n\n*************** Entering syncCampaign method of CampaignController ***************\n\n\n");
		if(cPage==null)
			cPage=1;
		if(rows==null)
			rows=UtilConstants.CAMPAIGNS_PER_PAGE;
		UserDo userDo = (UserDo) request.getSession().getAttribute("sUser");
		CampaignAdwordApi campaignAdwordApi;
		try {
			campaignAdwordApi = new CampaignAdwordApi();
		} catch (OAuthException | ValidationException | ConfigurationLoadException e) {
			model.put("msg", "Could not sync Advertiser(s). Check log for errors");
			model.put("errorMsg", 0);
			e.printStackTrace();
			logger.info("??????????????? ERROR:: Caught exception in CampaignAdwordApi constructor: "+e.getMessage()+" ???????????????");
			logger.debug(
					"\n\n\n############### Exiting syncCampaign method of CampaignController ###############\n\n\n");
			return "forward:/listcampaign";
		}

		List<CampaignDo> campaignDos = new ArrayList<CampaignDo>();
		boolean multipleCampaign = false;
		if (campaignId == null) {
			campaignDos = campaignService.getCampaignDosListForAdmin();
			multipleCampaign = true;
		}else {
			CampaignDo singleCampaignDo = campaignService.getCampaignDoById(campaignId);
			campaignDos.add(singleCampaignDo);
		}
		for (CampaignDo campaignDo : campaignDos) {
			try {
				// update basic campaign details
				if (campaignDo.getApiId() == null) {
					campaignDo.setStatus(CampaignDo.Status.Unknown.name());
				} else {
					campaignAdwordApi.updateBasicCampaignDetails(campaignDo);
				}

				if ((campaignDo.getStatus() == CampaignDo.Status.Removed.name())
						|| (campaignDo.getStatus() == CampaignDo.Status.Unknown.name())) {
					List<AdgroupDo> adgroupDos = adgroupService.getAdgroupDosListByCampaignDo(campaignDo);
					for (AdgroupDo adgroupDo : adgroupDos) {
						if (campaignDo.getStatus() == CampaignDo.Status.Removed.name()) // Campaign
							// was
							// removed
							// using adwords
							// account
							adgroupDo.setStatus(AdgroupDo.Status.Removed.name());
						else if (campaignDo.getStatus() == CampaignDo.Status.Unknown.name()) // Campaign
							// was
							// never saved
							// in adwords
							// account,
							// adword ID not
							// found there
							adgroupDo.setStatus(AdgroupDo.Status.Unknown.name());
					}
					campaignService.updateCampaign(campaignDo);

				} else {
					// update campaign location details
					String existingLocationsToExclude = campaignAdwordApi
							.getExistingLocationsToExclude(campaignDo.getApiId());
					String existingLocationsToInclude = campaignAdwordApi
							.getExistingLocationsToInclude(campaignDo.getApiId());
					String existingProximities = campaignAdwordApi.getExistingProximitiesIds(campaignDo.getApiId());

					// Include locations starts
					if (!existingProximities.equals("")) {
						campaignDo.setLocationIncludeCriteria(4);
						campaignDo.setLocationInclude(existingProximities);
					} else {

						if (existingLocationsToInclude.equals(""))
							campaignDo.setLocationIncludeCriteria(1);
						else {
							if (existingLocationsToInclude.equals("2356,"))
								campaignDo.setLocationIncludeCriteria(2);
							else
								campaignDo.setLocationIncludeCriteria(3);

							campaignDo.setLocationInclude(existingLocationsToInclude);
						}
					}
					// Include locations ends

					// Exclude locations starts

					if (existingLocationsToExclude.equals(""))
						campaignDo.setLocationExcludeCriteria(1);
					else {
						if (existingLocationsToExclude.equals("2356,"))
							campaignDo.setLocationExcludeCriteria(2);
						else
							campaignDo.setLocationExcludeCriteria(3);

						campaignDo.setLocationExclude(existingLocationsToExclude);
					}

					// Exclude locations ends

					campaignService.updateCampaign(campaignDo);
					campaignDo = campaignService.getCampaignDoById(campaignDo.getId());
					// sync campaign adgroup details
					List<AdgroupDo> adgroupDos = adgroupService.getAdgroupDosListByCampaignDo(campaignDo);

					for (AdgroupDo adgroupDo : adgroupDos) {
						adgroupService.syncAdgroupDetails(campaignDo, adgroupDo, userDo);
						adgroupService.updateAdgroup(adgroupDo);
					}
				}
				logger.debug("\n\n\n+++++++++++++++ INFO:: +++++++++++++++\n" + "campaignId: " + campaignId + "\n"
						+ "cPage: " + cPage + "\n" + "user: " + userDo.getName() + "\n" + "status: "
						+ campaignDo.getStatus() + "\n" + "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
				logger.info("\n\n\n+++++++++++++++ SUCCESS:: Advertiser with id: " + campaignId
						+ " is synced +++++++++++++++\n\n\n");
			} catch (NumberFormatException | IOException | ReportException | ReportDownloadResponseException
					| ParseException | OAuthException | ValidationException | ConfigurationLoadException e) {
				if (e instanceof ApiException) {
					ApiException apiException = (ApiException) e;
					model.put("msg", "Advertiser not synced. Adword Error: " + UtilityMethod.getErrorMessageFromProperties(apiException.getFaultReason()));
					logger.info("\n\n\n??????????????? ERROR:: Advertiser with name:" + campaignDo.getName()
							+ " could not sync due to adwordError , errorMessage:" + apiException.getFaultReason()
							+ "???????????????\n\n\n");
				} else {
					model.put("msg", "Advertiser not synced with adword. Please contact admin");
					logger.info("\n\n\n??????????????? ERROR:: Advertiser with name:" + campaignDo.getName()
							+ " could not sync , errorMessage:" + e.getMessage() + "???????????????\n\n\n");
				}
				model.put("errorMsg", 0);
				e.printStackTrace();
				logger.debug(
						"\n\n\n############### Exiting syncCampaign method of CampaignController ###############\n\n\n");
				return "forward:/listcampaign" + "?page=" + cPage+"&rows="+rows;
			}

		}
		model.put("errorMsg", 1);
		if (multipleCampaign) {
			model.put("msg", "All advertisers synced with Google Adwords account");
			logger.info(
					"\n\n\n+++++++++++++++ SUCCESS:: all advertisers synced with Google Adwords account +++++++++++++++\n\n\n");
		} else {
			model.put("errorMsg", 1);
			if ((campaignDos.get(0).getStatus() == CampaignDo.Status.Removed.name())
					|| (campaignDos.get(0).getStatus() == CampaignDo.Status.Unknown.name())) {
				model.put("msg", "Advertiser with name '" + campaignDos.get(0).getName()
						+ "' is not present/removed in Google Adwords account. Removed from here.");
				logger.info("\n\n\n??????????????? ERROR:: Advertiser with name:" + campaignDos.get(0).getName()
						+ " is not present/removed in Google Adwords account. Removed from here. ???????????????\n\n\n");
			}

			else {
				model.put("msg", "Advertiser with name '" + campaignDos.get(0).getName()
						+ "' synced with Google Adwords account");
				logger.info("\n\n\n+++++++++++++++ SUCCESS:: Advertiser with name:" + campaignDos.get(0).getName()
						+ " is synced with Google Adwords account. +++++++++++++++\n\n\n");
			}
		}
		logger.debug("\n\n\n############### Exiting syncCampaign method of CampaignController ###############\n\n\n");
		return "forward:/listcampaign" + "?page=" + cPage+"&rows="+rows;
	}
	
	/**
	 * Search for campaign from the campaign list
	 * @param cName
	 * @param response
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value="/searchcampaignlist",method=RequestMethod.GET,produces="application/json")
	public void searchCampaignList(@RequestParam(value="name",required=false) String cName, HttpServletResponse response) throws IOException{
		logger.debug("\n\n\n*************** Entering searchCampaignList method of CampaignController ***************\n\n\n");
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS, HEAD");
		response.addHeader("Access-Control-Allow-Credentials", "false");
		response.addHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With");
		PrintWriter out = response.getWriter();
		List<CampaignDo> rows = campaignService.getCampaignDosListForAdminBySearchKey(cName);
		Gson gson = new Gson();
		
		JsonObject obj = new JsonObject();
		
		obj.addProperty("name", "prabhakar");
		obj.addProperty("value", "verma");
		
		out.write(obj.toString());
		
		logger.debug("\n\n\n############### Exiting searchCampaignList method of CampaignController ###############\n\n\n");
	}
	
	/**
	 * Method to change the status of campaign
	 * 
	 * @param model
	 * @param campIds
	 * @param status
	 * @param rows
	 * @param usrId
	 * @return
	 */
	@RequestMapping(value="/changecampaignstatus")
	public String changeCampaignStatus(
			ModelMap model,
			@RequestParam(value="cid") String campIds,
			@RequestParam(value="st") Integer status,
			@RequestParam(value="rows",required=false) Integer rows,
			@RequestParam(value = "userId", required = false) Integer usrId
			) {
		logger.debug("\n\n\n*************** Entering changecampaignstatus method of CampaignController ***************\n\n\n");
		if(rows==null)
			rows=UtilConstants.CAMPAIGNS_PER_PAGE;
		String[] campaignIdList = campIds.split("\\|");
		Integer campaignCount = 0;
		for(String id:campaignIdList) {
			if(id.equals(""))
				continue;
			CampaignDo campaignDo = campaignService.getCampaignDoById(Integer.parseInt(id));
			campaignCount+=1;
			CampaignAdwordApi campaignAdwordApi;
			try {
				campaignAdwordApi = new CampaignAdwordApi();
			} catch (OAuthException | ValidationException | ConfigurationLoadException e) {
				model.put("msg", "Could not change status of Advertiser. Check log for errors");
				model.put("errorMsg", 0);
				e.printStackTrace();
				logger.info("??????????????? ERROR:: Caught exception in CampaignAdwordApi constructor: "+e.getMessage()+" ???????????????");
				logger.debug(
						"\n\n\n############### Exiting changecampaignstatus method of CampaignController ###############\n\n\n");
				return "forward:/listcampaign";
			}
			switch(status) {
			case 1:
				campaignDo.setStatus(CampaignDo.Status.Enabled.name());
				campaignService.updateCampaign(campaignDo);
				try {
					campaignAdwordApi.updateCampaign(campaignDo, null, false, false);
					model.put("msg", "Status of advertiser '" + campaignDo.getName() + "' is set to <strong>Enabled</strong>.");
					model.put("errorMsg", 1);
					logger.info("\n\n\n+++++++++++++++ Status of advertiser '" + campaignDo.getName() + "' is set to 'Enabled'. +++++++++++++++\n\n\n");
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				break;
			case 2:
				campaignDo.setStatus(CampaignDo.Status.Paused.name());
				campaignService.updateCampaign(campaignDo);
				try {
					campaignAdwordApi.updateCampaign(campaignDo, null, false, false);
					model.put("msg", "Status of advertiser '" + campaignDo.getName() + "' is set to <code><strong>Paused</strong></code>.");
					model.put("errorMsg", 1);
					logger.info("\n\n\n+++++++++++++++ Status of advertiser '" + campaignDo.getName() + "' is set to 'Paused'. +++++++++++++++\n\n\n");
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				break;
			case 3:
				List<AdgroupDo> adgroupDos = adgroupService.getAdgroupDosListByCampaignDo(campaignDo);
				for(AdgroupDo adgroupDo: adgroupDos) {
					adgroupDo.setStatus(AdgroupDo.Status.Removed.name());
					adgroupService.updateAdgroup(adgroupDo);
				}
				campaignDo.setStatus(CampaignDo.Status.Removed.name());
				campaignService.updateCampaign(campaignDo);
				try {
					campaignAdwordApi.updateCampaign(campaignDo, null, false, false);
					model.put("msg", "Advertiser '" + campaignDo.getName() + "' is removed successfully");
					model.put("errorMsg", 1);
					logger.info("\n\n\n+++++++++++++++ SUCCESS:: Advertiser '" + campaignDo.getName()
					+ "' removed successfully. +++++++++++++++\n\n\n");
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		}
		if(campaignIdList.length >1) {
			if(status ==1)
				model.put("msg", "Status of <strong>" + campaignCount + "</strong> advertisers set to <strong>Enabled</strong>");
			else if (status==2)
				model.put("msg", "Status of <strong>" + campaignCount + "</strong> advertisers set to <code><strong>Paused</strong></code>");
			else
				model.put("msg", "<strong>" + campaignCount + "</strong> advertisers has been <strong>Removed</strong> successfully.");
			model.put("errorMsg", 1);
		}
		logger.debug("\n\n\n############### Exiting changecampaignstatus method of CampaignController ###############\n\n\n");
		if(usrId !=null)
			return "forward:/listcampaign?page=1&rows="+rows+"&userId="+usrId;
		else
			return "forward:/listcampaign?page=1&rows="+rows;
			
	}

}
