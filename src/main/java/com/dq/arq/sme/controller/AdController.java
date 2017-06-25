package com.dq.arq.sme.controller;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import com.dq.arq.sme.adwordapi.CampaignAdwordApi;
import com.dq.arq.sme.domain.AdDo;
import com.dq.arq.sme.domain.AdgroupDo;
import com.dq.arq.sme.domain.CampaignDo;
import com.dq.arq.sme.domain.UserDo;
import com.dq.arq.sme.services.AdService;
import com.dq.arq.sme.services.AdgroupService;
import com.dq.arq.sme.services.CampaignService;
import com.dq.arq.sme.util.UtilConstants;
import com.dq.arq.sme.util.UtilityMethod;
import com.google.api.ads.adwords.axis.v201607.cm.ApiException;

@Controller
public class AdController {

	final static Logger logger = LoggerFactory.getLogger(AdController.class);
	
	@Autowired
	AdService adService;
	
	@Autowired
	AdgroupService adgroupService;
	
	@Autowired
	CampaignService campaignService;
	
	/**
	 * @param model
	 * @param request
	 * @param adgroupDo
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value="/savead")
	public String saveAd(ModelMap model,HttpServletRequest request,@ModelAttribute("adDo") AdDo adDo,
			@RequestParam(value="adgid") Integer adgroupId,
			@RequestParam(value="showall",required = false) Integer showAll,
			@RequestParam(value="rows",required =false) Integer rows,
			@RequestParam(value="adgdpage",required= false) Integer adgroupDetailsPage) throws IOException
	{
		logger.debug("\n\n\n*************** Entering saveAd method of AdController ***************\n\n\n");
		if(rows==null)
			rows=UtilConstants.ADGROUPS_PER_PAGE;
		if(adgroupDetailsPage==null)
			adgroupDetailsPage = 1;
		AdgroupDo adgroupDo = adgroupService.getAdgroupDoById(adgroupId);
		CampaignDo campaignDo = campaignService.getCampaignDoById(adgroupDo.getCampaignDo().getId());
		adgroupDo.setCampaignDo(campaignDo);
		
		UserDo userDo = (UserDo) request.getSession().getAttribute("sUser");
		adDo.setAdgroupDo(adgroupDo);
		adDo.setAdgroupApiId(adgroupDo.getApiId());
		adDo.setCreatedBy(userDo.getName());
		adDo.setCreatedOn(new Date());
		adDo.setUpdatedBy(userDo.getName());
		adDo.setUpdatedOn(new Date());
		adDo.setStatus(AdDo.Status.Enabled.name());
		

		try{
			//Saving ad in the local database
			Integer adId = adService.saveAdDo(adDo);
			model.put("msg", "Ad added successfully");
			model.put("errorMsg",1);
			logger.info("\n\n\n+++++++++++++++ SUCCESS:: Ad with id: "+adDo.getId()+" is saved successfully. +++++++++++++++\n\n\n");
		}catch(Exception e)
		{
			model.put("msg", "Could not save Ad. Check log for errors");
			model.put("errorMsg",0);
			e.printStackTrace();
			logger.info("\n\n\n??????????????? ERROR:: ad with id: "+adDo.getId()+" could not be saved , errorMessage:"+e.getMessage()+"???????????????\n\n\n");
			logger.debug("\n\n\n############### Exiting saveAd method of AdController ###############\n\n\n");
			if(showAll==null)
				return "forward:/adgroupdetails?adgid="+ adgroupId+"&rows="+rows+"&page=1";
			else
				return "forward:/adgroupdetails?rows="+rows+"&page="+adgroupDetailsPage;
		}
		
		
		try{
			//Creating ad in google adwordAPI
			AdAdwordApi adAdwordApi=new AdAdwordApi();
			adAdwordApi.createAd(adDo);

			//Updating adgroup with list of keywords and adgroup api key

			adService.updateAdDo(adDo);
	
		}catch ( RemoteException e1) {
			
			adDo.setStatus(AdDo.Status.Disabled.name());
			adService.updateAdDo(adDo);
			
			if(e1 instanceof ApiException)
			{
				ApiException apiException = (ApiException) e1;
				model.put("msg", "Ad not saved. Adword Error: "+UtilityMethod.getErrorMessageFromProperties(apiException.getFaultReason()));
				model.put("errorMsg", 0);
				logger.info("\n\n\n??????????????? ERROR:: ad with id: "+adDo.getId()+" could not be saved due to adwordError , errorMessage:"+apiException.getFaultReason()+"???????????????\n\n\n");
			}
			else
			{
				model.put("msg", "Ad not saved on adword. Please contact admin");
				logger.info("\n\n\n??????????????? ERROR:: ad with id: "+adDo.getId()+" could not be saved, errorMessage:"+e1.getMessage()+"???????????????\n\n\n");
			}
			
			e1.printStackTrace();

			logger.debug("\n\n\n############### Exiting saveAdgroup method of AdgroupController ###############\n\n\n");
			if(showAll==null)
				return "forward:/adgroupdetails?adgid="+ adgroupId+"&rows="+rows+"&page=1";
			else
				return "forward:/adgroupdetails?rows="+rows+"&page="+adgroupDetailsPage;
		} 
		
		
		logger.debug("\n\n\n############### Exiting saveAd method of AdController ###############\n\n\n");
		if(showAll==null)
			return "forward:/adgroupdetails?adgid="+ adgroupId+"&rows="+rows+"&page=1";
		else
			return "forward:/adgroupdetails?rows="+rows+"&page="+adgroupDetailsPage;
	}
	
	/**
	 * @param model
	 * @param request
	 * @param adgroupDo
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value="/updatead", method=RequestMethod.POST)
	public String updateAd(ModelMap model,HttpServletRequest request,@ModelAttribute("adDo") AdDo adDo,
			@RequestParam(value="adgid",required=false) Integer adgroupId,
			@RequestParam(value="showall",required = false) Integer showAll,
			@RequestParam(value="rows",required =false) Integer rows,
			@RequestParam(value="adgdpage",required= false) Integer adgroupDetailsPage
			) throws IOException
	{
		logger.debug("\n\n\n*************** Entering updateAd method of AdController ***************\n\n\n");
		
		AdgroupDo adgroupDo = adgroupService.getAdgroupDoById(adgroupId);
		CampaignDo campaignDo = campaignService.getCampaignDoById(adgroupDo.getCampaignDo().getId());
		adgroupDo.setCampaignDo(campaignDo);
		
		UserDo userDo = (UserDo) request.getSession().getAttribute("sUser");
		
		AdDo oldAdDo = adService.getAdDoById(adDo.getId());
		
		adDo.setStatus(request.getParameter("editAdStatus"));
		adDo.setApiId(oldAdDo.getApiId());
		adDo.setCreatedOn(oldAdDo.getCreatedOn());
		adDo.setCreatedBy(oldAdDo.getCreatedBy());
		adDo.setAdgroupDo(adgroupDo);
		adDo.setAdgroupApiId(adgroupDo.getApiId());
		adDo.setUpdatedBy(userDo.getName());
		adDo.setUpdatedOn(new Date());
		
		
		try{
			//Saving ad in the local database
			adService.updateAdDo(adDo);
			if((adDo.getStatus() == AdDo.Status.Enabled.name()) || (adDo.getStatus() == AdDo.Status.Paused.name()))
				model.put("msg", "Ad updated successfully");
			else
				model.put("msg", "Ad removed successfully");
			model.put("errorMsg",1);
			logger.info("\n\n\n+++++++++++++++ SUCCESS:: Ad with id: "+adDo.getId()+" is updated successfully. +++++++++++++++\n\n\n");
		}catch(Exception e)
		{
			model.put("msg", "Could not save Ad. Check log for errors");
			model.put("errorMsg",0);
			e.printStackTrace();
			logger.info("\n\n\n??????????????? ERROR:: ad with id: "+adDo.getId()+" could not be updated , errorMessage:"+e.getMessage()+"???????????????\n\n\n");
			logger.debug("\n\n\n############### Exiting updateAd method of AdController ###############\n\n\n");
			if(showAll==null)
				return "forward:/adgroupdetails?adgid="+ adgroupId+"&rows="+rows+"&page=1";
			else
				return "forward:/adgroupdetails?rows="+rows+"&page="+adgroupDetailsPage;
		}
		
		
		try{
			//Creating ad in google adwordAPI
			AdAdwordApi adAdwordApi=new AdAdwordApi();
			if((adDo.getStatus() == AdDo.Status.Enabled.name()) || (adDo.getStatus() == AdDo.Status.Paused.name()))
				adAdwordApi.updateAd(adgroupDo, adDo);	
			else
				adAdwordApi.removeAd(adDo);
			adService.updateAdDo(adDo);
			
			
		}catch ( RemoteException e1) {
			
			adDo.setStatus(AdDo.Status.Disabled.name());
			adService.updateAdDo(adDo);
			if(e1 instanceof ApiException)
			{
				
				ApiException apiException = (ApiException) e1;
				model.put("msg", "Ad not saved. Adword Error: "+UtilityMethod.getErrorMessageFromProperties(apiException.getFaultReason()));
				model.put("errorMsg", 0);
				logger.info("\n\n\n??????????????? ERROR:: ad with id: "+adDo.getId()+" could not be updated due to adwordError , errorMessage:"+apiException.getFaultReason()+"???????????????\n\n\n");
			}
			else
			{
				model.put("msg", "Ad not saved on adword. Please contact admin");
				logger.info("\n\n\n??????????????? ERROR:: ad with id: "+adDo.getId()+" could not be updated, errorMessage:"+e1.getMessage()+"???????????????\n\n\n");
			}
			
			e1.printStackTrace();
			
			logger.debug("\n\n\n############### Exiting updateAdgroup method of AdgroupController ###############\n\n\n");
			if(showAll==null)
				return "forward:/adgroupdetails?adgid="+ adgroupId+"&rows="+rows+"&page=1";
			else
				return "forward:/adgroupdetails?rows="+rows+"&page="+adgroupDetailsPage;
		} 
		
		
		logger.debug("\n\n\n############### Exiting updateAd method of AdController ###############\n\n\n");
		if(showAll==null)
			return "forward:/adgroupdetails?adgid="+ adgroupId+"&rows="+rows+"&page=1";
		else
			return "forward:/adgroupdetails?rows="+rows+"&page="+adgroupDetailsPage;
	}
	
	@RequestMapping(value="/changeadstatus")
	public String changeAdStatus(
			ModelMap model,
			@RequestParam(value="adid") String adIds,
			@RequestParam(value="st") Integer status,
			@RequestParam(value="rows",required=false) Integer rows,
			@RequestParam(value="adgdpage",required=false) Integer adgdPage,
			@RequestParam(value = "adgid", required = false) Integer adgId
			) {
		logger.debug("\n\n\n*************** Entering changeAdStatus method of AdController ***************\n\n\n");
		if(rows==null)
			rows=UtilConstants.ADGROUPS_PER_PAGE;
		String[] adIdList = adIds.split("\\|");
		Integer adCount = 0;
		for(String adid:adIdList) {
			if(adid.equals(""))
				continue;
			AdDo adDo = adService.getAdDoById(Integer.parseInt(adid));
			AdgroupDo adgroupDo = adService.getAdgroupDoByAdId(Integer.parseInt(adid));
			adCount+=1;
			AdAdwordApi adAdwordApi=new AdAdwordApi();
			switch(status) {
			case 1:
				adDo.setStatus(AdDo.Status.Enabled.name());
				adService.updateAdDo(adDo);
				try {
					adAdwordApi.updateAd(adgroupDo, adDo);
					model.put("msg", "Status of "+adCount+" ads of '" + adgroupDo.getProductName() + "' set to <strong>Enabled</strong>.");
					model.put("errorMsg", 1);
					logger.info("\n\n\n+++++++++++++++ Status of "+adCount+" ads of '" + adgroupDo.getProductName() + "' set to 'Enabled'. +++++++++++++++\n\n\n");
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 2:
				adDo.setStatus(AdDo.Status.Paused.name());
				adService.updateAdDo(adDo);
				try {
					adAdwordApi.updateAd(adgroupDo, adDo);
					model.put("msg", "Status of "+adCount+" ads of '" + adgroupDo.getProductName() + "' set to <code><strong>Paused</strong></code>.");
					model.put("errorMsg", 1);
					logger.info("\n\n\n+++++++++++++++ Status of "+adCount+" ads of '" + adgroupDo.getProductName() + "' set to 'Paused'. +++++++++++++++\n\n\n");
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 3:
				adDo.setStatus(AdDo.Status.Disabled.name());
				adService.updateAdDo(adDo);
				try {
					adAdwordApi.updateAd(adgroupDo, adDo);
					model.put("msg", "Status of "+adCount+" ads of '" + adgroupDo.getProductName() + "' set to <strong>Disabled</strong>.");
					model.put("errorMsg", 1);
					logger.info("\n\n\n+++++++++++++++ Status of "+adCount+" ads of '" + adgroupDo.getProductName() + "' set to 'Disabled'. +++++++++++++++\n\n\n");
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		}
		
		logger.debug("\n\n\n############### Exiting changeAdStatus method of AdController ###############\n\n\n");
		if(adgId!=null)
			return "forward:/adgroupdetails?adgid="+adgId;
		else
			return "forward:/adgroupdetails?page="+adgdPage+"&rows="+rows;
	}
	
}
