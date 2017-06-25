package com.dq.arq.sme.adwordapi;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dq.arq.sme.domain.AdDo;
import com.dq.arq.sme.domain.AdgroupDo;
import com.google.api.ads.adwords.axis.factory.AdWordsServices;
import com.google.api.ads.adwords.axis.utils.v201607.SelectorBuilder;
import com.google.api.ads.adwords.axis.v201607.cm.Ad;
import com.google.api.ads.adwords.axis.v201607.cm.AdGroupAd;
import com.google.api.ads.adwords.axis.v201607.cm.AdGroupAdOperation;
import com.google.api.ads.adwords.axis.v201607.cm.AdGroupAdPage;
import com.google.api.ads.adwords.axis.v201607.cm.AdGroupAdReturnValue;
import com.google.api.ads.adwords.axis.v201607.cm.AdGroupAdServiceInterface;
import com.google.api.ads.adwords.axis.v201607.cm.AdGroupAdStatus;
import com.google.api.ads.adwords.axis.v201607.cm.ApiException;
import com.google.api.ads.adwords.axis.v201607.cm.CallOnlyAd;
import com.google.api.ads.adwords.axis.v201607.cm.Operator;
import com.google.api.ads.adwords.axis.v201607.cm.Selector;
import com.google.api.ads.adwords.lib.client.AdWordsSession;
import com.google.api.ads.adwords.lib.selectorfields.v201607.cm.AdGroupAdField;
import com.google.api.ads.common.lib.auth.OfflineCredentials;
import com.google.api.ads.common.lib.auth.OfflineCredentials.Api;
import com.google.api.ads.common.lib.conf.ConfigurationLoadException;
import com.google.api.ads.common.lib.exception.OAuthException;
import com.google.api.ads.common.lib.exception.ValidationException;
import com.google.api.client.auth.oauth2.Credential;

public class AdAdwordApi {
	static final Logger logger = LoggerFactory.getLogger("AdwordsLog");

	Credential oAuth2Credential;
	AdWordsSession session;
	AdWordsServices adWordsServices;


	public AdAdwordApi() {
		logger.info("*************** Entering AdAdwordApi constructor ***************");
		try{ 
			oAuth2Credential = new OfflineCredentials.Builder()
			.forApi(Api.ADWORDS)
			.fromFile()
			.build()
			.generateCredential();

			// Construct an AdWordsSession.
			session = new AdWordsSession.Builder()
			.fromFile()
			.withOAuth2Credential(oAuth2Credential)
			.build();

			adWordsServices = new AdWordsServices();
		}catch(OAuthException | ValidationException | ConfigurationLoadException e)
		{

			logger.info("??????????????? ERROR:: Caught exception in CampaignAdwordApi constructor: "+e.getMessage()+" ???????????????");
			e.printStackTrace();
		}
		logger.debug("############### Exiting AdAdwordApi constructor ###############\n");
	}

	public void createAd(AdDo adDo) throws ApiException, RemoteException
	{

		logger.info("*************** Entering createAd method ***************");
		// Get the AdGroupAdService.
		AdGroupAdServiceInterface adGroupAdService =
				adWordsServices.get(session, AdGroupAdServiceInterface.class);

		// Create text ads.
		CallOnlyAd callOnlyAd=new CallOnlyAd();
		callOnlyAd.setDescription1(adDo.getDescriptionLine1());
		callOnlyAd.setDescription2(adDo.getDescriptionLine2());
		callOnlyAd.setBusinessName(adDo.getBusinessName());
		callOnlyAd.setCountryCode("IN");
		if(adDo.getPhoneNumber().startsWith("0"))
			adDo.setPhoneNumber(adDo.getPhoneNumber().substring(1, adDo.getPhoneNumber().length()));
		callOnlyAd.setPhoneNumber(adDo.getPhoneNumber());
		callOnlyAd.setDisplayUrl(adDo.getDisplayUrl());
		//   callOnlyAd.setFinalUrls(new String[] {adgroupDo.getDisplayUrl()});



		// Create ad group ad.
		AdGroupAd callAdGroupAd1 = new AdGroupAd();
		callAdGroupAd1.setAdGroupId(adDo.getAdgroupApiId());
		callAdGroupAd1.setAd(callOnlyAd);

		// You can optionally provide these field(s).
		if(adDo.getStatus()==AdDo.Status.Enabled.name())
			callAdGroupAd1.setStatus(AdGroupAdStatus.ENABLED);
		else if(adDo.getStatus()==AdDo.Status.Paused.name())
			callAdGroupAd1.setStatus(AdGroupAdStatus.PAUSED);
		else if(adDo.getStatus()==AdDo.Status.Disabled.name())
			callAdGroupAd1.setStatus(AdGroupAdStatus.DISABLED);



		// Create operations.
		AdGroupAdOperation callAdGroupAdOperation1 = new AdGroupAdOperation();
		callAdGroupAdOperation1.setOperand(callAdGroupAd1);
		callAdGroupAdOperation1.setOperator(Operator.ADD);


		AdGroupAdOperation[] operations =
				new AdGroupAdOperation[] {callAdGroupAdOperation1};

		// Add ads.

		logger.info("\n=============== SEND:: adDo with details: ===============\n"
				+ "descriptionline1: "+adDo.getDescriptionLine1()+"\n"
				+ "descriptionline2: "+adDo.getDescriptionLine2()+"\n"
				+ "businessName: "+adDo.getBusinessName()+"\n"
				+ "phone No: "+adDo.getPhoneNumber()+"\n"
				+ "displayURL:"+adDo.getDisplayUrl()+"\n"

				+"=============================================\n");
		AdGroupAdReturnValue result = adGroupAdService.mutate(operations);

		// Display ads.
		for (AdGroupAd adGroupAdResult : result.getValue()) {

			adDo.setApiId(adGroupAdResult.getAd().getId());
			logger.info("\n=============== RECEIVED:: adGroupAdResult with details: ===============\n"
					+ "ID: "+adGroupAdResult.getAd().getId()+"\n"
					+ "type: "+adGroupAdResult.getAd().getAdType()+"\n"
					+"=============================================");
			logger.info("\n+++++++++++++++ SUCCESS:: adGroupAdResult with details: +++++++++++++++\n"
					+ "ID: "+adGroupAdResult.getAd().getId()+"\n"
					+ "type: "+adGroupAdResult.getAd().getAdType()+" added successfully\n"
					+"+++++++++++++++++++++++++++++++++++++++++++++");
		}
		logger.info("############### Exiting createAd method ###############\n");
	}


	public void updateAd(AdgroupDo adgroupDo,AdDo adDo) throws ApiException, RemoteException
	{

		logger.debug("*************** Entering updateAd method ***************");
		if(adDo.getApiId()!=null)  //If Ad was not previously created
			removeAd(adDo);
		createAd(adDo);
		logger.debug("############### Exiting updateAd method ###############\n");
	}

	public void removeAd(AdDo adDo) throws  ApiException, RemoteException
	{
		logger.info("*************** Entering removeAd method ***************");
		// Get the AdGroupAdService.
		AdGroupAdServiceInterface adGroupAdService =
				adWordsServices.get(session, AdGroupAdServiceInterface.class);

		// Create base class ad to avoid setting type specific fields.
		Ad ad = new Ad();
		ad.setId(adDo.getApiId());

		// Create ad group ad.
		AdGroupAd adGroupAd = new AdGroupAd();
		adGroupAd.setAdGroupId(adDo.getAdgroupApiId());
		adGroupAd.setAd(ad);

		// Create operations.
		AdGroupAdOperation operation = new AdGroupAdOperation();
		operation.setOperand(adGroupAd);
		operation.setOperator(Operator.REMOVE);

		AdGroupAdOperation[] operations = new AdGroupAdOperation[] {operation};

		// Remove ad.
		logger.debug("\n=============== SEND:: adDo with apiID: "+adDo.getApiId()+" ===============\n");

		AdGroupAdReturnValue result = adGroupAdService.mutate(operations);

		// Display ads.
		for (AdGroupAd adGroupAdResult : result.getValue()) {
			adDo.setApiId(adGroupAdResult.getAd().getId());
			logger.debug("\n=============== RECEIVED:: Ad with ID: "+adGroupAdResult.getAd().getId()+" and type: "+adGroupAdResult.getAd().getAdType()+" ===============");
			logger.info("\n+++++++++++++++ SUCCESS:: Ad with ID: "+adGroupAdResult.getAd().getId()+" and type: "+adGroupAdResult.getAd().getAdType()+" removed successfully. +++++++++++++++");
		}
		logger.info("############### Exiting removeAd method ###############\n");
	}

	public List<AdDo> getExistingAdDetails(AdgroupDo adgroupDo) throws ApiException, RemoteException
	{
		logger.info("*************** Entering syncExistingAdDetails method ***************");
		AdGroupAdServiceInterface adGroupAdService =
				adWordsServices.get(session, AdGroupAdServiceInterface.class);

		// Create selector.
		SelectorBuilder builder = new SelectorBuilder();
		Selector selector = builder
				.fields(AdGroupAdField.Id, AdGroupAdField.Status,  AdGroupAdField.CallOnlyAdDescription1,AdGroupAdField.CallOnlyAdDescription2,
						AdGroupAdField.CallOnlyAdBusinessName,AdGroupAdField.CallOnlyAdPhoneNumber,
						AdGroupAdField.DisplayUrl)
						.equals(AdGroupAdField.AdGroupId, adgroupDo.getApiId().toString())
						.build();
		logger.info("\n=============== SEND:: adgroupDo with apiID: "+adgroupDo.getApiId()+" ===============\n");
		// Get all ad groups.
		AdGroupAdPage page = adGroupAdService.get(selector);

		List<AdDo> adDos = new ArrayList<AdDo>();
		// Display ad groups.
		if (page.getEntries() != null) {
			for (AdGroupAd adGroupAd : page.getEntries()) {

				CallOnlyAd callOnlyAd=(CallOnlyAd) adGroupAd.getAd();

				AdDo adDo = new AdDo();
					if(adGroupAd.getStatus().toString().equals("ENABLED"))
						adDo.setStatus(AdDo.Status.Enabled.name());
					else if(adGroupAd.getStatus().toString().equals("PAUSED"))
						adDo.setStatus(AdDo.Status.Paused.name());
					else if(adGroupAd.getStatus().toString().equals("DISABLED"))
						adDo.setStatus(AdDo.Status.Disabled.name());

				adDo.setDescriptionLine1(callOnlyAd.getDescription1());
				adDo.setDescriptionLine2(callOnlyAd.getDescription2());
				adDo.setBusinessName(callOnlyAd.getBusinessName());
				adDo.setPhoneNumber(callOnlyAd.getPhoneNumber());
				if(!callOnlyAd.getDisplayUrl().contains("http:"))
					adDo.setDisplayUrl("http://"+callOnlyAd.getDisplayUrl());
				else
					adDo.setDisplayUrl(callOnlyAd.getDisplayUrl());
				adDo.setApiId(callOnlyAd.getId());

				adDos.add(adDo);
				logger.debug("\n=============== RECEIVED:: adgroupDo with details: ===============\n"
						+ "descriptionline1: "+adDo.getDescriptionLine1()+"\n"
						+ "descriptionline2: "+adDo.getDescriptionLine2()+"\n"
						+ "businessName: "+adDo.getBusinessName()+"\n"
						+ "phone No: "+adDo.getPhoneNumber()+"\n"
						+ "displayURL:"+adDo.getDisplayUrl()+"\n"
						+ "setAdApiId: "+adDo.getApiId()+"\n"
						+ "=============================================");
				logger.info("\n+++++++++++++++ SUCCESS:: Ad with id : "+adDo.getApiId()+" was found and synced +++++++++++++++");
			}
		} else {
			logger.info("\n+++++++++++++++ SUCCESS:: No Ad found for adgroup with product name : "+adgroupDo.getProductName()+" +++++++++++++++");
		}

		logger.info("############### Exiting syncExistingAdDetails method ###############\n");
		return adDos;
	}
}
