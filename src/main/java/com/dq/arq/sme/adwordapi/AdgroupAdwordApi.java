package com.dq.arq.sme.adwordapi;

import java.rmi.RemoteException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dq.arq.sme.domain.AdgroupDo;
import com.dq.arq.sme.domain.CampaignDo;
import com.google.api.ads.adwords.axis.factory.AdWordsServices;
import com.google.api.ads.adwords.axis.utils.v201607.SelectorBuilder;
import com.google.api.ads.adwords.axis.v201607.cm.AdGroup;
import com.google.api.ads.adwords.axis.v201607.cm.AdGroupOperation;
import com.google.api.ads.adwords.axis.v201607.cm.AdGroupPage;
import com.google.api.ads.adwords.axis.v201607.cm.AdGroupReturnValue;
import com.google.api.ads.adwords.axis.v201607.cm.AdGroupServiceInterface;
import com.google.api.ads.adwords.axis.v201607.cm.AdGroupStatus;
import com.google.api.ads.adwords.axis.v201607.cm.ApiException;
import com.google.api.ads.adwords.axis.v201607.cm.BiddingStrategyConfiguration;
import com.google.api.ads.adwords.axis.v201607.cm.Bids;
import com.google.api.ads.adwords.axis.v201607.cm.CpcBid;
import com.google.api.ads.adwords.axis.v201607.cm.CriterionTypeGroup;
import com.google.api.ads.adwords.axis.v201607.cm.Money;
import com.google.api.ads.adwords.axis.v201607.cm.Operator;
import com.google.api.ads.adwords.axis.v201607.cm.Selector;
import com.google.api.ads.adwords.axis.v201607.cm.Setting;
import com.google.api.ads.adwords.axis.v201607.cm.TargetingSetting;
import com.google.api.ads.adwords.axis.v201607.cm.TargetingSettingDetail;
import com.google.api.ads.adwords.lib.client.AdWordsSession;
import com.google.api.ads.adwords.lib.selectorfields.v201607.cm.AdGroupField;
import com.google.api.ads.common.lib.auth.OfflineCredentials;
import com.google.api.ads.common.lib.auth.OfflineCredentials.Api;
import com.google.api.ads.common.lib.conf.ConfigurationLoadException;
import com.google.api.ads.common.lib.exception.OAuthException;
import com.google.api.ads.common.lib.exception.ValidationException;
import com.google.api.client.auth.oauth2.Credential;

public class AdgroupAdwordApi {

	 static final Logger logger = LoggerFactory.getLogger("AdwordsLog");
	
	Credential oAuth2Credential;
	AdWordsSession session;
	AdWordsServices adWordsServices;
	
	public AdgroupAdwordApi() {
		logger.info("*************** Entering AdgroupAdwordApi constructor ***************");
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
			
			logger.info("??????????????? ERROR:: Caught exception in AdgroupAdwordApi constructor: "+e.getMessage()+" ???????????????");
			e.printStackTrace();
		}
		logger.info("############### Exiting AdgroupAdwordApi constructor ###############");
	}
	
	public void createAdgroup(AdgroupDo adgroupDo,Long campaignId) throws ApiException, RemoteException
	{
		logger.info("*************** Entering createAdgroup method ***************");
		 // Get the AdGroupService.
	    AdGroupServiceInterface adGroupService =
	        adWordsServices.get(session, AdGroupServiceInterface.class);

	    // Create ad group.
	    AdGroup adGroup = new AdGroup();
	    adGroup.setName(adgroupDo.getProductName());
	    if(adgroupDo.getStatus()==AdgroupDo.Status.Enabled.name())
	    	adGroup.setStatus(AdGroupStatus.ENABLED);
	    else if(adgroupDo.getStatus()==AdgroupDo.Status.Paused.name())
	    	adGroup.setStatus(AdGroupStatus.PAUSED);
	    else if(adgroupDo.getStatus()== AdgroupDo.Status.Removed.name())
	    	adGroup.setStatus(AdGroupStatus.REMOVED);
	    else
	    	adGroup.setStatus(AdGroupStatus.UNKNOWN);
	    adGroup.setCampaignId(campaignId);

	    // Optional settings.

	    // Targeting restriction settings. Depending on the criterionTypeGroup
	    // value, most TargetingSettingDetail only affect Display campaigns.
	    // However, the USER_INTEREST_AND_LIST value works for RLSA campaigns -
	    // Search campaigns targeting using a remarketing list.
	    TargetingSetting targeting = new TargetingSetting();

	    // Restricting to serve ads that match your ad group placements.
	    // This is equivalent to choosing "Target and bid" in the UI.
	    TargetingSettingDetail placements = new TargetingSettingDetail();
	    placements.setCriterionTypeGroup(CriterionTypeGroup.PLACEMENT);
	    placements.setTargetAll(Boolean.FALSE);

	    // Using your ad group verticals only for bidding. This is equivalent
	    // to choosing "Bid only" in the UI.
	    TargetingSettingDetail verticals = new TargetingSettingDetail();
	    verticals.setCriterionTypeGroup(CriterionTypeGroup.VERTICAL);
	    verticals.setTargetAll(Boolean.TRUE);

	    targeting.setDetails(new TargetingSettingDetail[] {placements, verticals});
	    adGroup.setSettings(new Setting[] {targeting});

	    // Create ad group bid.
	    BiddingStrategyConfiguration biddingStrategyConfiguration = new BiddingStrategyConfiguration();
	    CpcBid bid = new CpcBid();
	    bid.setBid(new Money(null, 15*1000000L));
	    biddingStrategyConfiguration.setBids(new Bids[] {bid});
	    adGroup.setBiddingStrategyConfiguration(biddingStrategyConfiguration);

	   /* // Add as many additional ad groups as you need.
	    AdGroup adGroup2 = new AdGroup();
	    adGroup2.setName("Earth to Venus Cruises #" + System.currentTimeMillis());
	    adGroup2.setStatus(AdGroupStatus.ENABLED);
	    adGroup2.setCampaignId(campaignId);

	    BiddingStrategyConfiguration biddingStrategyConfiguration2 = new BiddingStrategyConfiguration();
	    CpcBid bid2 = new CpcBid();
	    bid2.setBid(new Money(null, 10000000L));
	    biddingStrategyConfiguration2.setBids(new Bids[] {bid2});
	    adGroup2.setBiddingStrategyConfiguration(biddingStrategyConfiguration2);*/

	    // Create operations.
	    AdGroupOperation operation = new AdGroupOperation();
	    operation.setOperand(adGroup);
	    operation.setOperator(Operator.ADD);
	    /*AdGroupOperation operation2 = new AdGroupOperation();
	    operation2.setOperand(adGroup2);
	    operation2.setOperator(Operator.ADD);*/

	    //AdGroupOperation[] operations = new AdGroupOperation[] {operation, operation2};
	    AdGroupOperation[] operations = new AdGroupOperation[] {operation};
	    logger.info("\n=============== SEND:: adgroupDo with details =============== \n"
	    		+ "productName: "+adgroupDo.getProductName()+"\n"
				+ "status: "+adgroupDo.getStatus()+"\n"
				+ "campaignID: "+campaignId+"\n"
				+ "=============================================");
	    // Add ad groups.
	    AdGroupReturnValue result  = adGroupService.mutate(operations);
		

	    // Display new ad groups.
	    for (AdGroup adGroupResult : result.getValue()) {

			adgroupDo.setApiId(adGroupResult.getId());
			logger.info("\n=============== RECEIVED:: AdGroupResult with details: ===============\n"
					+ "name: " + adGroupResult.getName() + "\n"
					+ "ID: " + adGroupResult.getId()+"\n"
					+ "=============================================");
			logger.info("\n+++++++++++++++ SUCCESS:: AdGroupResult with details: +++++++++++++++\n"
					+ "name: " + adGroupResult.getName() + "\n"
					+ "ID: " + adGroupResult.getId()+ " was added.\n"
					+ "+++++++++++++++++++++++++++++++++++++++++++++");
	    }
	    logger.info("############### Exiting createAdgroup method ###############");
	}
	
	
	public void updateAdgroup(AdgroupDo adgroupDo,Long campaignId) throws ApiException, RemoteException
	{
		logger.info("*************** Entering updateAdgroup method ***************");
		// Get the AdGroupService.
	    AdGroupServiceInterface adGroupService =
	        adWordsServices.get(session, AdGroupServiceInterface.class);

	    // Create ad group with updated status.
	    AdGroup adGroup = new AdGroup();
	    adGroup.setId(adgroupDo.getApiId());
	    adGroup.setName(adgroupDo.getProductName());
	    if(adgroupDo.getStatus()==AdgroupDo.Status.Enabled.name())
		    adGroup.setStatus(AdGroupStatus.ENABLED);
		    else if(adgroupDo.getStatus()==AdgroupDo.Status.Paused.name())
		    	adGroup.setStatus(AdGroupStatus.PAUSED);
		    else if(adgroupDo.getStatus()==AdgroupDo.Status.Removed.name())
		    	adGroup.setStatus(AdGroupStatus.REMOVED);
		    else
		    	adGroup.setStatus(AdGroupStatus.UNKNOWN);

	    // Create operations.
	    AdGroupOperation operation = new AdGroupOperation();
	    operation.setOperand(adGroup);
	    operation.setOperator(Operator.SET);

	    AdGroupOperation[] operations = new AdGroupOperation[] {operation};
	    logger.info("\n=============== SEND:: adgroupDo with details: ===============\n"
	    		+ "apiID: "+adgroupDo.getApiId()+"\n"
				+ "productName: "+adgroupDo.getProductName()+"\n"
				+ "status: "+adgroupDo.getStatus()+"\n"
				+ "campaignID: "+campaignId+"\n"
				+ "=============================================");
	    // Update ad group.
	    AdGroupReturnValue result = adGroupService.mutate(operations);

	    // Display ad groups.
	    for (AdGroup adGroupResult : result.getValue()) {	    	
	    	logger.info("\n=============== RECEIVED:: AdGroupResult with details: ===============\n"
	    			+ "name: "+adGroupResult.getName()+"\n"
					+ "ID: "+adGroupResult.getId()+"\n"
					+ "status: "+adGroupResult.getStatus()+"\n"
					+ "=============================================");
	    	logger.info("\n+++++++++++++++ SUCCESS:: AdGroupResult with details: +++++++++++++++\n"
	    			+ "name: "+adGroupResult.getName()+"\n"
					+ "ID: "+adGroupResult.getId()+"\n"
					+ "status: "+adGroupResult.getStatus()+" was updated.\n"
					+ "+++++++++++++++++++++++++++++++++++++++++++++");
	    }
	    logger.info("############### Exiting updateAdgroup method ###############");
	}
	
	public boolean syncExistingAdgroupDetails(CampaignDo campaignDo,AdgroupDo adgroupDo) throws ApiException, RemoteException
	{
		logger.info("*************** Entering syncExistingAdgroupDetails method ***************");
		// Get the AdGroupService.
	    AdGroupServiceInterface adGroupService =
	        adWordsServices.get(session, AdGroupServiceInterface.class);

	    if(adgroupDo.getApiId()==null)
	    {
	    	logger.info("\n??????????????? ERROR:: ???????????????\n"
	    			+ "campaignDo with Name: "+campaignDo.getName()+"\n"
					+ "adgroupDo with name: "+adgroupDo.getProductName()+" does not have Google Adgroup API ID.\n"
					+ "?????????????????????????????????????????????");
	    	return false;
	    }
	    
	    // Create selector.
	    SelectorBuilder builder = new SelectorBuilder();
	    Selector selector = builder
	        .fields(AdGroupField.Id, AdGroupField.Name,AdGroupField.Status)
	        .equals(AdGroupField.CampaignId, campaignDo.getApiId().toString())
	        .equals(AdGroupField.Id, adgroupDo.getApiId().toString())
	        .build();

	    logger.info("\n=============== SEND:: ===============\n"
	    		+ "campaignDo with apiID: "+campaignDo.getApiId()+"\n"
				+ "adgroupDo with apiID: "+adgroupDo.getApiId()+"\n"
				+ "=============================================");
	      // Get all ad groups.
	      AdGroupPage page = adGroupService.get(selector);

	      // Display ad groups.
	      if (page.getEntries() != null) {
	        for (AdGroup adGroup : page.getEntries()) {
				adgroupDo.setApiId(adGroup.getId());
	        	adgroupDo.setProductName(adGroup.getName());
	        	if(adGroup.getStatus().toString().equals("ENABLED"))
	        		adgroupDo.setStatus(AdgroupDo.Status.Enabled.name());
	        	else if(adGroup.getStatus().toString().equals("PAUSED"))
	        		adgroupDo.setStatus(AdgroupDo.Status.Paused.name());
	        	else if(adGroup.getStatus().toString().equals("REMOVED"))
	        		adgroupDo.setStatus(AdgroupDo.Status.Removed.name());
	        	else
	        		adgroupDo.setStatus(AdgroupDo.Status.Unknown.name());
	        	logger.info("\n=============== RECEIVED:: adgroupDo with details: ===============\n"
	        			+ "apiID: "+adgroupDo.getApiId()+"\n"
    					+ "productName: "+adgroupDo.getProductName()+"\n"
						+ "status: "+adgroupDo.getStatus()+"\n"
						+ "=============================================");
	        	logger.info("\n+++++++++++++++ SUCCESS:: Adgroup with details: +++++++++++++++\n"
	        			+ "name:"+adGroup.getName()+"\n"
    					+ "ID: "+adGroup.getId()+" was found and synced\n"
						+ "+++++++++++++++++++++++++++++++++++++++++++++");
	        }
	      } else {
	        logger.info("\n+++++++++++++++ SUCCESS:: +++++++++++++++\n"
	        		+ "campaignDo with name: "+campaignDo.getName()+"\n"
    				+ "adgroupDo with name: "+adgroupDo.getProductName()+" was removed from adwords.\n"
					+ "+++++++++++++++++++++++++++++++++++++++++++++");
	        return false;
	      }
	    logger.info("############### Exiting syncExistingAdgroupDetails method ###############");
		return true;
	}
	
}
