package com.dq.arq.sme.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dq.arq.sme.adwordapi.AdgroupAdwordApi;
import com.dq.arq.sme.adwordapi.KeywordAdwordApi;
import com.dq.arq.sme.adwordapi.ReportAdwordApi;
import com.dq.arq.sme.cron.CronJobs;
import com.dq.arq.sme.cron.CronJobsImpl;
import com.dq.arq.sme.domain.AdDo;
import com.dq.arq.sme.domain.AdgroupDo;
import com.dq.arq.sme.domain.CampaignDo;
import com.dq.arq.sme.domain.KeywordDo;
import com.dq.arq.sme.domain.ProductCategoryDo;
import com.dq.arq.sme.domain.UserDo;
import com.dq.arq.sme.services.AdgroupService;
import com.dq.arq.sme.services.CampaignService;
import com.dq.arq.sme.services.KeywordService;
import com.dq.arq.sme.services.ProductCategoryService;
import com.dq.arq.sme.util.UtilConstants;
import com.dq.arq.sme.util.UtilityMethod;
import com.google.api.ads.adwords.axis.v201607.cm.ApiException;
import com.google.api.ads.adwords.lib.utils.ReportDownloadResponseException;
import com.google.api.ads.adwords.lib.utils.ReportException;

@Controller
public class KeywordController {
	// final static Logger logger =
	// LoggerFactory.getLogger(KeywordController.class);
	final static Logger logger = LoggerFactory.getLogger("LogTesting");

	@Autowired
	CampaignService campaignService;

	@Autowired
	AdgroupService adgroupService;

	@Autowired
	KeywordService keywordService;
	
	@Autowired
	ProductCategoryService productCategoryService;


	@RequestMapping(value = "/editkeywords")
	public String editKeywords(ModelMap model, HttpServletRequest request,
			@RequestParam("adgid") Integer adgroupId,
			@RequestParam(value = "adgPage", required = false) Integer adgPage,
			HttpSession session) {
		logger.debug("\n\n\n*************** Entering editKeywords method of KeywordController ***************\n\n\n");
		AdgroupDo adgroupDo = adgroupService.getAdgroupDoById(adgroupId);

		List<KeywordDo> keywordDos = keywordService.getKeywordDosListByAdgroupDo(adgroupDo);
		model.put("campaignId", adgroupService.getCampaignDoByAdgroupId(adgroupDo.getId()).getId());
		model.put("campaignName", adgroupService.getCampaignDoByAdgroupId(adgroupDo.getId()).getName());
		model.put("adgPage",adgPage);
		model.put("adgroupId", adgroupId);
		model.put("productName", adgroupDo.getProductName());
		model.put("keywordDos", keywordDos);
		logger.debug("\n\n\n############### Exiting editKeywords method of KeywordController ###############\n\n\n");
		return "editkeywords";
	}

	@RequestMapping(value = "/updatekeyword")
	public String updateSingleKeyword(ModelMap model,
			HttpServletRequest request,
			@RequestParam("adgid") Integer adgroupId,
			@RequestParam(value = "adgPage", required = false) Integer adgPage) throws IOException {
		logger.debug("\n\n\n*************** Entering updateSingleKeyword method of KeywordController ***************\n\n\n");

		AdgroupDo adgroupDo = adgroupService.getAdgroupDoById(adgroupId);

		CampaignDo campaignDo = campaignService.getCampaignDoById(adgroupDo.getCampaignDo().getId());
		model.put("campaignName", campaignDo.getName());
		UserDo userDo = (UserDo) request.getSession().getAttribute("sUser");
		adgroupDo.setCampaignDo(campaignDo);
		try {
			String keywordDetails = request.getParameter("keywordDetails");
			String[] keyword = keywordDetails.split("\\|");
			KeywordAdwordApi keywordAdwordApi = new KeywordAdwordApi();
			boolean matchTypeChanged = false;

			if (!(keyword[0] == "" || keyword[0] == " " || ""
					.equals(keyword[0]))) {
				int keyId = Integer.parseInt(keyword[0]);
				KeywordDo existingKeywordDo = keywordService
						.getKeywordDoById(keyId);
				existingKeywordDo.setBid(Double.parseDouble(keyword[2]));
				if (!existingKeywordDo.getMatchType().equals(keyword[3]))
					matchTypeChanged = true;
				existingKeywordDo.setMatchType(keyword[3]);
				// Creating keywords in google adwordAPI
				if (matchTypeChanged) {
					keywordAdwordApi.removeKeywords(
							adgroupDo,
							new ArrayList<KeywordDo>((Arrays
									.asList(existingKeywordDo))));
					keywordAdwordApi.addKeywords(
							adgroupDo,
							new ArrayList<KeywordDo>((Arrays
									.asList(existingKeywordDo))));
				} else {
					keywordAdwordApi
							.updateKeyword(adgroupDo, existingKeywordDo);
				}
				keywordService.updateKeywordDo(existingKeywordDo);
				model.put("msg", "keyword with name: '" + keyword[1]
						+ "' updated successfully.");
				model.put("errorMsg", 1);
			} else {
				if (keywordService.getKeywordDoByName(keyword[1]) != null) {
					model.put("msg", "keyword with name: '" + keyword[1]
							+ "' already exists");
					model.put("errorMsg", 0);
					List<KeywordDo> keywordDos = keywordService
							.getKeywordDosListByAdgroupDo(adgroupDo);
					model.put("adgPage", adgPage);
					model.put("adgroupId", adgroupId);
					model.put("productName", adgroupDo.getProductName());
					model.put("keywordDos", keywordDos);
					logger.debug("\n\n\n############### Exiting updateSingleKeyword method of KeywordController ###############\n\n\n");
					return "editkeywords";
				}
				KeywordDo keywordDo = new KeywordDo();
				keywordDo.setText(keyword[1]);
				keywordDo.setBid(Double.parseDouble(keyword[2]));
				keywordDo.setMatchType(keyword[3]);
				keywordDo.setAdgroupDo(adgroupDo);
				keywordDo.setAdgroupApiId(adgroupDo.getApiId());
				keywordDo.setCreatedBy(userDo.getName());
				keywordDo.setUpdatedBy(userDo.getName());
				keywordDo.setCreatedOn(new Date());

				// Creating keywords in google adwordAPI
				List<KeywordDo> keywordDos = new ArrayList<KeywordDo>();
				keywordDos.add(keywordDo);
				keywordAdwordApi.addKeywords(adgroupDo, keywordDos);
				keywordService.saveKeywordDo(keywordDo);
				model.put("msg", "New keyword with name: '" + keyword[1]
						+ "' added successfully.");
				model.put("errorMsg", 1);
			}

		} catch (RemoteException | UnsupportedEncodingException e) {
			if (e instanceof ApiException) {
				ApiException apiException = (ApiException) e;
				logger.info("\n\n\n??????????????? ERROR:: Keywords not updated. Adword ErrorMessage:"
						+ apiException.getFaultReason()
						+ " ???????????????\n\n\n");
				model.put("msg", "Keywords not updated. Adword Error: "
						+ UtilityMethod.getErrorMessageFromProperties(apiException.getFaultReason()));
			} else {
				logger.info("\n\n\n??????????????? ERROR:: Keywords not updated. Please contact admin, errorMessage:"
						+ e.getMessage() + " ???????????????\n\n\n");
				model.put("msg", "Keywords not updated. Please contact admin");
			}
			e.printStackTrace();
		}

		List<KeywordDo> keywordDos = keywordService
				.getKeywordDosListByAdgroupDo(adgroupDo);
		model.put("adgPage", adgPage);
		model.put("adgroupId", adgroupId);
		model.put("productName", adgroupDo.getProductName());
		model.put("keywordDos", keywordDos);
		logger.debug("\n\n\n############### Exiting updateSingleKeyword method of KeywordController ###############\n\n\n");
		return "editkeywords";
	}

	@RequestMapping(value="/changekeywordstatus")
	public String changeKeywordStatus(
			ModelMap model,
			@RequestParam("kid") String keywordIds,
			@RequestParam(value="st") Integer status,
			@RequestParam(value= "adgid",required=false) Integer adgId,
			@RequestParam(value = "adgPage", required = false) Integer adgPage,
			HttpSession session) {
		logger.debug("\n\n\n*************** Entering changeKeywordStatus method of KeywordController ***************\n\n\n");
		AdgroupDo adgroupDo = adgroupService.getAdgroupDoById(adgId);
		String[] keywordIdList = keywordIds.split("\\|");
		Integer keywordCount =0;
		for(String keyId:keywordIdList) {
			KeywordDo keywordDo = keywordService.getKeywordDoById(Integer.parseInt(keyId));
			keywordCount+=1;
			KeywordAdwordApi keywordAdwordApi = new KeywordAdwordApi();
			switch(status) {
			case 1:
				keywordDo.setStatus(KeywordDo.Status.Enabled.name());
				keywordService.updateKeywordDo(keywordDo);
				try {
					keywordAdwordApi.updateKeyword(adgroupDo, keywordDo);
					model.put("msg", "Status of keyword '" + keywordDo.getText()+ "' is set to <strong>Active</strong>.");
					model.put("errorMsg", 1);
					logger.info("\n\n\n+++++++++++++++ Status of keyword '" +  keywordDo.getText() + "' is set to 'Active'. +++++++++++++++\n\n\n");
				} catch (RemoteException | UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 2:
				keywordDo.setStatus(KeywordDo.Status.Paused.name());
				keywordService.updateKeywordDo(keywordDo);
				try {
					keywordAdwordApi.updateKeyword(adgroupDo, keywordDo);
					model.put("msg", "Status of keyword '" +  keywordDo.getText()+ "' is set to <code><strong>Paused</strong></code>.");
					model.put("errorMsg", 1);
					logger.info("\n\n\n+++++++++++++++ Status of keyword '" +  keywordDo.getText() + "' is set to 'Paused'. +++++++++++++++\n\n\n");
				} catch (RemoteException | UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		}
		if(keywordIdList.length >1) {
			if(status ==1)
				model.put("msg", "Status of <strong>" + keywordCount + "</strong> keywords set to <strong>Active</strong>");
			else if (status==2)
				model.put("msg", "Status of <strong>" + keywordCount + "</strong> keywords set to <code><strong>Paused</strong></code>");
			model.put("errorMsg", 1);
		}
		logger.debug("\n\n\n############### Exiting changeKeywordStatus method of KeywordController ###############\n\n\n");
		return "forward:/editkeywords?adgid="+adgId+"&adgPage="+adgPage;
	}
	@RequestMapping(value = "/pausekeyword")
	public void pauseKeywordCron(ModelMap model, HttpServletRequest request,
			HttpSession session) {
		
		
		
		
		try {
			List<CampaignDo> campaignDos= campaignService.getCampaignDosListForAdmin();
			for(CampaignDo campaignDo:campaignDos)
			{
				// Code added to run the cron only if campaign is created after 23-Nov-2016 
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				String dateString = "2016-11-23";
				if(campaignDo.getCreatedOn().compareTo(df.parse(dateString))>=0)
				{
				List<AdgroupDo> adgroupDos = adgroupService.getAdgroupDosListByCampaignDo(campaignDo);
				for(AdgroupDo adgroupDo: adgroupDos)
				{
					List<KeywordDo> keywordDos = keywordService.getKeywordDosListByAdgroupDo(adgroupDo);

					String keywordApiIds = "";
					for(KeywordDo keywordDo: keywordDos)
					{
						if(keywordDo.getApiId()!=null)
							keywordApiIds +="'"+keywordDo.getApiId()+"',";
					}

					if(keywordApiIds.length()>0)
					{
							keywordApiIds = keywordApiIds.substring(0,keywordApiIds.length()-1);

							new ReportAdwordApi().updateKeywordsWithFirstPageCpc(adgroupDo,keywordApiIds,keywordDos);

							List<KeywordDo> keywordDosToPause = new ArrayList<KeywordDo>();
							int count=0;
							
							for(KeywordDo keywordDo : keywordDos)
							{
								if(keywordDo.getBid()>adgroupDo.getThresholdKeywordAvgCpc())
								{
									keywordDosToPause.add(keywordDo);
									count++;
								}
							}
							KeywordAdwordApi keywordAdwordApi = new KeywordAdwordApi();
							new KeywordAdwordApi().pauseKeywords(keywordDosToPause);
							
							keywordService.updateKeywordDos(keywordDosToPause);
							
							
							
							//Code added to add extra keywords because some keywords were paused
							UserDo userDo = new UserDo();
							userDo.setName("Admin");
							ProductCategoryDo productCategoryDo =  productCategoryService.getProductCategoryDoByName(adgroupDo.getCategoryName());
							keywordService.addNumberOfKeywords(adgroupDo,userDo,productCategoryDo,count);
							
							
						}
					}
			}	
			}

		} catch (ReportException | ReportDownloadResponseException
				| IOException e) {
			logger.info("\n\n\n??????????????? ERROR:: Could not pause KeywordsWithHighCpc on Google Adword , errorMessage:"+e.getMessage()+"???????????????\n\n\n");
			e.printStackTrace();
			return;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}




}