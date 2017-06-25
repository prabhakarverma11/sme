package com.dq.arq.sme.adwordapi;

import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dq.arq.sme.domain.AdgroupDo;
import com.dq.arq.sme.domain.KeywordDo;
import com.dq.arq.sme.domain.KeywordDo.Status;
import com.dq.arq.sme.util.UtilConstants;
import com.google.api.ads.adwords.axis.factory.AdWordsServices;
import com.google.api.ads.adwords.axis.utils.v201607.SelectorBuilder;
import com.google.api.ads.adwords.axis.v201607.cm.AdGroupCriterion;
import com.google.api.ads.adwords.axis.v201607.cm.AdGroupCriterionOperation;
import com.google.api.ads.adwords.axis.v201607.cm.AdGroupCriterionPage;
import com.google.api.ads.adwords.axis.v201607.cm.AdGroupCriterionReturnValue;
import com.google.api.ads.adwords.axis.v201607.cm.AdGroupCriterionServiceInterface;
import com.google.api.ads.adwords.axis.v201607.cm.ApiException;
import com.google.api.ads.adwords.axis.v201607.cm.BiddableAdGroupCriterion;
import com.google.api.ads.adwords.axis.v201607.cm.BiddingStrategyConfiguration;
import com.google.api.ads.adwords.axis.v201607.cm.Bids;
import com.google.api.ads.adwords.axis.v201607.cm.CpcBid;
import com.google.api.ads.adwords.axis.v201607.cm.Criterion;
import com.google.api.ads.adwords.axis.v201607.cm.Keyword;
import com.google.api.ads.adwords.axis.v201607.cm.KeywordMatchType;
import com.google.api.ads.adwords.axis.v201607.cm.Language;
import com.google.api.ads.adwords.axis.v201607.cm.Location;
import com.google.api.ads.adwords.axis.v201607.cm.Money;
import com.google.api.ads.adwords.axis.v201607.cm.NetworkSetting;
import com.google.api.ads.adwords.axis.v201607.cm.Operator;
import com.google.api.ads.adwords.axis.v201607.cm.Paging;
import com.google.api.ads.adwords.axis.v201607.cm.Selector;
import com.google.api.ads.adwords.axis.v201607.cm.UserStatus;
import com.google.api.ads.adwords.axis.v201607.o.Attribute;
import com.google.api.ads.adwords.axis.v201607.o.AttributeType;
import com.google.api.ads.adwords.axis.v201607.o.CategoryProductsAndServicesSearchParameter;
import com.google.api.ads.adwords.axis.v201607.o.DoubleAttribute;
import com.google.api.ads.adwords.axis.v201607.o.IdeaType;
import com.google.api.ads.adwords.axis.v201607.o.IntegerSetAttribute;
import com.google.api.ads.adwords.axis.v201607.o.LanguageSearchParameter;
import com.google.api.ads.adwords.axis.v201607.o.LocationSearchParameter;
import com.google.api.ads.adwords.axis.v201607.o.LongAttribute;
import com.google.api.ads.adwords.axis.v201607.o.MoneyAttribute;
import com.google.api.ads.adwords.axis.v201607.o.NetworkSearchParameter;
import com.google.api.ads.adwords.axis.v201607.o.RelatedToQuerySearchParameter;
import com.google.api.ads.adwords.axis.v201607.o.RequestType;
import com.google.api.ads.adwords.axis.v201607.o.SearchParameter;
import com.google.api.ads.adwords.axis.v201607.o.StringAttribute;
import com.google.api.ads.adwords.axis.v201607.o.TargetingIdea;
import com.google.api.ads.adwords.axis.v201607.o.TargetingIdeaPage;
import com.google.api.ads.adwords.axis.v201607.o.TargetingIdeaSelector;
import com.google.api.ads.adwords.axis.v201607.o.TargetingIdeaServiceInterface;
import com.google.api.ads.adwords.lib.client.AdWordsSession;
import com.google.api.ads.adwords.lib.selectorfields.v201607.cm.AdGroupCriterionField;
import com.google.api.ads.common.lib.auth.OfflineCredentials;
import com.google.api.ads.common.lib.auth.OfflineCredentials.Api;
import com.google.api.ads.common.lib.conf.ConfigurationLoadException;
import com.google.api.ads.common.lib.exception.OAuthException;
import com.google.api.ads.common.lib.exception.ValidationException;
import com.google.api.ads.common.lib.utils.Maps;
import com.google.api.client.auth.oauth2.Credential;
import com.google.common.base.Joiner;
import com.google.common.primitives.Ints;

public class KeywordAdwordApi {

	static final Logger logger = LoggerFactory.getLogger("AdwordsLog");

	Credential oAuth2Credential;
	AdWordsSession session;
	AdWordsServices adWordsServices;

	public  KeywordAdwordApi() {
		logger.info("*************** Entering KeywordAdwordApi constructor ***************");
		try
		{ 
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
		logger.info("############### Exiting KeywordAdwordApi constructor ###############");
	}


	public void pauseKeywords(List<KeywordDo> keywordDos) throws ApiException, RemoteException
	{
		logger.info("*************** Entering pauseKeywords method ***************");
		// Get the AdGroupCriterionService.
		AdGroupCriterionServiceInterface adGroupCriterionService =
				adWordsServices.get(session, AdGroupCriterionServiceInterface.class);
		for(KeywordDo keywordDo: keywordDos)
		{
			logger.info("\n=============== SEND:: keywordDo: ===============\n"
					+ "apiId: "+keywordDo.getApiId()+"\n"
					+ "adgroupApiId: "+keywordDo.getAdgroupApiId() + "\n"
					+ "=============================================");
			// Create ad group criterion with updated bid.
			Criterion criterion = new Criterion();
			criterion.setId(keywordDo.getApiId());

			BiddableAdGroupCriterion biddableAdGroupCriterion = new BiddableAdGroupCriterion();
			biddableAdGroupCriterion.setAdGroupId(keywordDo.getAdgroupApiId());
			biddableAdGroupCriterion.setCriterion(criterion);

			biddableAdGroupCriterion.setUserStatus(UserStatus.PAUSED);

	    keywordDo.setStatus(KeywordDo.Status.Paused.name());


			// Create operations.
			AdGroupCriterionOperation operation = new AdGroupCriterionOperation();
			operation.setOperand(biddableAdGroupCriterion);
			operation.setOperator(Operator.SET);

			AdGroupCriterionOperation[] operations = new AdGroupCriterionOperation[] {operation};

			// Update ad group criteria.
			AdGroupCriterionReturnValue result = adGroupCriterionService.mutate(operations);
			logger.info("*************** Exiting pauseKeywords method ***************");
		}
	}

	public void addKeywords(AdgroupDo adgroupDo,List<KeywordDo> keywordDos) throws UnsupportedEncodingException, ApiException, RemoteException
	{
		logger.info("*************** Entering addKeywords method ***************");
		AdGroupCriterionServiceInterface adGroupCriterionService =
				adWordsServices.get(session, AdGroupCriterionServiceInterface.class);

		logger.info("\n=============== SEND:: adgroupDo with details: ===============\n"
				+ "id: "+adgroupDo.getId()+"\n"
				+ "name: "+adgroupDo.getProductName()+"\n"
				+ "apiId: "+adgroupDo.getApiId()+"\n"
				+ "=============================================");
		for(KeywordDo keywordDo: keywordDos)
		{
			// Create keywords.
			Keyword keyword1 = new Keyword();
			keyword1.setText(keywordDo.getText());
			if(keywordDo.getMatchType().equals("BROAD"))
				keyword1.setMatchType(KeywordMatchType.BROAD);
			else if(keywordDo.getMatchType().equals("PHRASE"))
				keyword1.setMatchType(KeywordMatchType.PHRASE);
			else if(keywordDo.getMatchType().equals("EXACT"))
				keyword1.setMatchType(KeywordMatchType.EXACT);


			// Create biddable ad group criterion.
			BiddableAdGroupCriterion keywordBiddableAdGroupCriterion1 = new BiddableAdGroupCriterion();
			keywordBiddableAdGroupCriterion1.setAdGroupId(adgroupDo.getApiId());
			keywordBiddableAdGroupCriterion1.setCriterion(keyword1);

			// You can optionally provide these field(s).

			
			if(keywordDo.getStatus()== KeywordDo.Status.Enabled.name())
				keywordBiddableAdGroupCriterion1.setUserStatus(UserStatus.ENABLED);
		    else if(keywordDo.getStatus()==KeywordDo.Status.Paused.name())
		    	keywordBiddableAdGroupCriterion1.setUserStatus(UserStatus.PAUSED);
		    else if(keywordDo.getStatus()==Status.Removed.name())
		    	keywordBiddableAdGroupCriterion1.setUserStatus(UserStatus.REMOVED);
			


			/*String encodedFinalUrl  = String.format(adgroupDo.getDisplayUrl(),
					URLEncoder.encode(keyword1.getText(), Charsets.UTF_8.name()));

			keywordBiddableAdGroupCriterion1.setFinalUrls(new UrlList(new String[] {encodedFinalUrl}));*/

			BiddingStrategyConfiguration biddingStrategyConfiguration = new BiddingStrategyConfiguration();
			CpcBid bid = new CpcBid();
			DecimalFormat df = new DecimalFormat("###.##");

			if(keywordDo.getBid()==null)
				keywordDo.setBid(0.01);
			keywordDo.setBid(new Double(df.format(keywordDo.getBid())));
			Double microAmount = 1000000*keywordDo.getBid();
			String microAmountString = df.format(microAmount);
			bid.setBid(new Money(null, new Long(microAmountString)));

			biddingStrategyConfiguration.setBids(new Bids[] {bid});
			keywordBiddableAdGroupCriterion1.setBiddingStrategyConfiguration(biddingStrategyConfiguration);

			// Create operations.
			AdGroupCriterionOperation keywordAdGroupCriterionOperation1 = new AdGroupCriterionOperation();
			keywordAdGroupCriterionOperation1.setOperand(keywordBiddableAdGroupCriterion1);
			keywordAdGroupCriterionOperation1.setOperator(Operator.ADD);


			AdGroupCriterionOperation[] operations =
					new AdGroupCriterionOperation[] {keywordAdGroupCriterionOperation1};

			// Add keywords.
			AdGroupCriterionReturnValue result = null;
			try {
				logger.info("\n=============== SEND:: keywordDo with details: ===============\n"
						+ "id: "+keywordDo.getId()+"\n"
						+ "text: "+keywordDo.getText()+"\n"
						+ "bid: "+keywordDo.getBid()+"\n"
						+ "=============================================");
				result = adGroupCriterionService.mutate(operations);
			} catch ( RemoteException e) {
				logger.info("??????????????? ERROR:: Caught exception in addKeywords method: "+e.getStackTrace()+" ???????????????");
				e.printStackTrace();
			}

			// Display results.
			for (AdGroupCriterion adGroupCriterionResult : result.getValue()) {
				logger.info("\n=============== RECEIVED::adGroupCriterionResult with details: ===============\n"
						+ "adGroupId: "+adGroupCriterionResult.getAdGroupId()+"\n"
						+ "keywordApiId: "+adGroupCriterionResult.getCriterion().getId()+"\n"
						+ "text: "+((Keyword) adGroupCriterionResult.getCriterion()).getText()+"\n"
						+ "matchType: "+((Keyword) adGroupCriterionResult.getCriterion()).getMatchType()+"\n"
						+ "=============================================");
				logger.info("\n+++++++++++++++ SUCCESS::adGroupCriterionResult with details: +++++++++++++++\n"
						+ "adGroupId: "+adGroupCriterionResult.getAdGroupId()+"\n"
						+ "keywordApiId: "+adGroupCriterionResult.getCriterion().getId()+"\n"
						+ "text: "+((Keyword) adGroupCriterionResult.getCriterion()).getText()+"\n"
						+ "matchType: "+((Keyword) adGroupCriterionResult.getCriterion()).getMatchType()+" was added.\n"
						+ "+++++++++++++++++++++++++++++++++++++++++++++");
				keywordDo.setAdgroupDo(adgroupDo);
				keywordDo.setAdgroupApiId(adgroupDo.getApiId());
				keywordDo.setApiId(adGroupCriterionResult.getCriterion().getId());
				if(keywordDo.getMatchType()==null)
					keywordDo.setMatchType(((Keyword) adGroupCriterionResult.getCriterion()).getMatchType()+"");
				if(keywordDo.getStatus()==null)
					keywordDo.setStatus(KeywordDo.Status.Enabled.name());

			}
		}

		logger.info("############### Exiting addKeywords method ###############");
	}

	public void refreshKeywordList(AdgroupDo adgroupDo,List<KeywordDo> newKeywordDos) throws ApiException, RemoteException, UnsupportedEncodingException
	{
		logger.info("*************** Entering refreshKeywordList method ***************");
		List<KeywordDo> existingKeywordDosInAdwords = getKeywords(adgroupDo); //Commented because assuming that keywords would not be updated from adwords account
		if(existingKeywordDosInAdwords.size()>0)
			removeKeywords(adgroupDo,existingKeywordDosInAdwords);

		addKeywords(adgroupDo,newKeywordDos);
		logger.info("*************** Exiting refreshKeywordList method ***************");
	}

	public void updateKeyword(AdgroupDo adgroupDo,KeywordDo keywordDo) throws UnsupportedEncodingException, ApiException, RemoteException
	{
		logger.info("*************** Entering updateKeywords method ***************");
		/*List<KeywordDo> existingKeywordDosInAdwords = getKeywords(adgroupDo); //Commented because assuming that keywords would not be updated from adwords account
		if(existingKeywordDosInAdwords.size()>0)
			removeKeywords(adgroupDo,existingKeywordDosInAdwords);

		addKeywords(adgroupDo,newKeywordDos);*/

		AdGroupCriterionServiceInterface adGroupCriterionService =
		        adWordsServices.get(session, AdGroupCriterionServiceInterface.class);
		    
		    	logger.info("\n=============== SEND:: keywordDo: ===============\n"
						+ "apiId: "+keywordDo.getApiId()+"\n"
						+ "matchType: "+keywordDo.getMatchType()+"\n"
						+ "bid: "+keywordDo.getBid()+"\n"
						+ "status: "+keywordDo.getStatus()+"\n"
						+ "adgroupApiId: "+keywordDo.getAdgroupApiId() + "\n"
						+ "=============================================");
		    // Create ad group criterion with updated bid.
		    //Criterion criterion = new Criterion();
		    Keyword keyword = new Keyword();
		    keyword.setId(keywordDo.getApiId());
		    
		    if(keywordDo.getMatchType().equals("BROAD"))
		    keyword.setMatchType(KeywordMatchType.BROAD);
		    else if(keywordDo.getMatchType().equals("PHRASE"))
		    	keyword.setMatchType(KeywordMatchType.PHRASE);
		    else if(keywordDo.getMatchType().equals("EXACT"))
		    	keyword.setMatchType(KeywordMatchType.EXACT);
		    
		    BiddableAdGroupCriterion biddableAdGroupCriterion = new BiddableAdGroupCriterion();
		    biddableAdGroupCriterion.setAdGroupId(keywordDo.getAdgroupApiId());
		    biddableAdGroupCriterion.setCriterion(keyword);
		    
		    BiddingStrategyConfiguration biddingStrategyConfiguration = new BiddingStrategyConfiguration();
		    
			CpcBid bid = new CpcBid();
			
			DecimalFormat df = new DecimalFormat("###.##");
			
			if(keywordDo.getBid()==null)
				keywordDo.setBid(0.01);
			keywordDo.setBid(new Double(df.format(keywordDo.getBid())));
			Double microAmount = 1000000*keywordDo.getBid();
			String microAmountString = df.format(microAmount);
			bid.setBid(new Money(null, new Long(microAmountString)));
			biddingStrategyConfiguration.setBids(new Bids[] {bid});
			biddableAdGroupCriterion.setBiddingStrategyConfiguration(biddingStrategyConfiguration);

		    

		    if(keywordDo.getStatus()==KeywordDo.Status.Enabled.name())
		    biddableAdGroupCriterion.setUserStatus(UserStatus.ENABLED);
		    else if(keywordDo.getStatus()==KeywordDo.Status.Paused.name())
		    	biddableAdGroupCriterion.setUserStatus(UserStatus.PAUSED);
		    else if(keywordDo.getStatus()==KeywordDo.Status.Removed.name())
		    	biddableAdGroupCriterion.setUserStatus(UserStatus.REMOVED);
		    

		    // Create operations.
		    AdGroupCriterionOperation operation = new AdGroupCriterionOperation();
		    operation.setOperand(biddableAdGroupCriterion);
		    operation.setOperator(Operator.SET);

		    AdGroupCriterionOperation[] operations = new AdGroupCriterionOperation[] {operation};

		    // Update ad group criteria.
		    AdGroupCriterionReturnValue result = adGroupCriterionService.mutate(operations);
		
		 // Display results.
		 			for (AdGroupCriterion adGroupCriterionResult : result.getValue()) {
		 				logger.info("\n=============== RECEIVED::adGroupCriterionResult with details: ===============\n"
		 						+ "adGroupId: "+adGroupCriterionResult.getAdGroupId()+"\n"
		 						+ "keywordApiId: "+adGroupCriterionResult.getCriterion().getId()+"\n"
		 						+ "text: "+((Keyword) adGroupCriterionResult.getCriterion()).getText()+"\n"
		 						+ "matchType: "+((Keyword) adGroupCriterionResult.getCriterion()).getMatchType()+"\n"
		 						+ "=============================================");
		 				logger.info("\n+++++++++++++++ SUCCESS::adGroupCriterionResult with details: +++++++++++++++\n"
		 						+ "adGroupId: "+adGroupCriterionResult.getAdGroupId()+"\n"
		 						+ "keywordApiId: "+adGroupCriterionResult.getCriterion().getId()+"\n"
		 						+ "text: "+((Keyword) adGroupCriterionResult.getCriterion()).getText()+"\n"
		 						+ "matchType: "+((Keyword) adGroupCriterionResult.getCriterion()).getMatchType()+" was updated.\n"
		 						+ "+++++++++++++++++++++++++++++++++++++++++++++");
		 			}
		
		logger.info("############### Exiting updateKeywords method ###############");
	}


	public void removeKeywords(AdgroupDo adgroupDo,List<KeywordDo> keywordDos) throws ApiException, RemoteException
	{
		logger.info("*************** Entering removeKeywords method ***************");
		logger.info("\n=============== SEND:: adgroupDo with details: ===============\n"
				+ "id: "+adgroupDo.getId()+"\n"
				+ "name: "+adgroupDo.getProductName()+"\n"
				+ "apiId: "+adgroupDo.getApiId()+"\n"
				+ "=============================================");
		for(KeywordDo keywordDo: keywordDos)
		{
			// Get the AdGroupCriterionService.
			AdGroupCriterionServiceInterface adGroupCriterionService =
					adWordsServices.get(session, AdGroupCriterionServiceInterface.class);

			// Create base class criterion to avoid setting keyword specific fields.
			Criterion criterion = new Criterion();
			criterion.setId(keywordDo.getApiId());

			// Create ad group criterion.
			AdGroupCriterion adGroupCriterion = new AdGroupCriterion();
			adGroupCriterion.setAdGroupId(adgroupDo.getApiId());
			adGroupCriterion.setCriterion(criterion);

			// Create operations.
			AdGroupCriterionOperation operation = new AdGroupCriterionOperation();
			operation.setOperand(adGroupCriterion);
			operation.setOperator(Operator.REMOVE);

			AdGroupCriterionOperation[] operations = new AdGroupCriterionOperation[] {operation};

			// Remove ad group criteria.
			AdGroupCriterionReturnValue result = adGroupCriterionService.mutate(operations);

			// Display ad group criteria.
			for (AdGroupCriterion adGroupCriterionResult : result.getValue()) {
				logger.info("\n=============== RECEIVED::adGroupCriterionResult with details: ===============\n"
						+ "adGroupId: "+adGroupCriterionResult.getAdGroupId()+"\n"
						+ "criterionId: "+adGroupCriterionResult.getCriterion().getId()+"\n"
						+ "text: "+((Keyword) adGroupCriterionResult.getCriterion()).getText()+"\n"
						+ "matchType: "+((Keyword) adGroupCriterionResult.getCriterion()).getMatchType()+"\n"
						+ "=============================================");
				logger.info("\n+++++++++++++++ SUCCESS::adGroupCriterionResult with details: +++++++++++++++\n"
						+ "adGroupId: "+adGroupCriterionResult.getAdGroupId()+"\n"
						+ "criterionId: "+adGroupCriterionResult.getCriterion().getId()+"\n"
						+ "text: "+((Keyword) adGroupCriterionResult.getCriterion()).getText()+"\n"
						+ "matchType: "+((Keyword) adGroupCriterionResult.getCriterion()).getMatchType()+" was removed.\n"
						+ "+++++++++++++++++++++++++++++++++++++++++++++");
			}
		}
		logger.info("############### Exiting removeKeywords method ###############");
	}


	public List<KeywordDo> getKeywordIdeas(String sourceKeyword,Integer productCategoryId,int count,List<String> includeLocationApiIds) throws ApiException, RemoteException
	{
		logger.info("*************** Entering getKeywordIdeas method ***************");
		// Get the TargetingIdeaService.
		TargetingIdeaServiceInterface targetingIdeaService =
				adWordsServices.get(session, TargetingIdeaServiceInterface.class);

		List<KeywordDo> keywordDos = new ArrayList<KeywordDo>();

		// Create selector.

		TargetingIdeaSelector selector = new TargetingIdeaSelector();
		selector.setRequestType(RequestType.IDEAS);
		selector.setIdeaType(IdeaType.KEYWORD);
		selector.setRequestedAttributeTypes(new AttributeType[] {
				AttributeType.KEYWORD_TEXT,
				AttributeType.SEARCH_VOLUME,
				AttributeType.COMPETITION,
				AttributeType.AVERAGE_CPC,
				AttributeType.TARGETED_MONTHLY_SEARCHES,
				AttributeType.CATEGORY_PRODUCTS_AND_SERVICES});

		//Location based keyword starts
		if(includeLocationApiIds!=null && includeLocationApiIds.size()>0)
		{
			LocationSearchParameter locationSearchParameter = new LocationSearchParameter();
			Location[] locations = new Location[includeLocationApiIds.size()];
			int i=0;
			for(String locationApiId : includeLocationApiIds)
			{
				Location location = new Location();
				location.setId(Long.parseLong(locationApiId));
				locations[i++] = location;
			}
			locationSearchParameter.setLocations(locations);
			selector.setSearchParameters(new SearchParameter[]{locationSearchParameter});
		}
		//Location based keyword ends

		// Set selector paging (required for targeting idea service).
		Paging paging = new Paging();
		paging.setStartIndex(0);
		paging.setNumberResults(2000);
		selector.setPaging(paging);

		// Create related to query search parameter.
		RelatedToQuerySearchParameter relatedToQuerySearchParameter =
				new RelatedToQuerySearchParameter();
		relatedToQuerySearchParameter.setQueries(new String[] {sourceKeyword});

		// Language setting (optional).
		// The ID can be found in the documentation:
		//   https://developers.google.com/adwords/api/docs/appendix/languagecodes
		// See the documentation for limits on the number of allowed language parameters:
		//   https://developers.google.com/adwords/api/docs/reference/latest/TargetingIdeaService.LanguageSearchParameter
		LanguageSearchParameter languageParameter = new LanguageSearchParameter();
		Language english = new Language();
		english.setId(1000L);
		languageParameter.setLanguages(new Language[] {english});


		CategoryProductsAndServicesSearchParameter categoryProductsAndServicesSearchParameter = new CategoryProductsAndServicesSearchParameter();
		categoryProductsAndServicesSearchParameter.setCategoryId(productCategoryId);

		// Create network search parameter (optional).
		NetworkSetting networkSetting = new NetworkSetting();
		networkSetting.setTargetGoogleSearch(true);
		networkSetting.setTargetSearchNetwork(false);
		networkSetting.setTargetContentNetwork(false);
		networkSetting.setTargetPartnerSearchNetwork(false);

		NetworkSearchParameter networkSearchParameter = new NetworkSearchParameter();
		networkSearchParameter.setNetworkSetting(networkSetting);

		selector.setSearchParameters(
				new SearchParameter[] {relatedToQuerySearchParameter, languageParameter,
						networkSearchParameter,categoryProductsAndServicesSearchParameter});
		logger.info("\n=============== SEND:: ===============\n"
				+ "requestType: "+selector.getRequestType()+"\n"
				+ "ideaType: "+selector.getIdeaType()+"\n"
				+ "sourceKeyword: "+sourceKeyword+"\n"
				+ "=============================================");
		// Get related keywords.
		TargetingIdeaPage page = targetingIdeaService.get(selector);

		// Display related keywords.
		if (page.getEntries() != null && page.getEntries().length > 0) {
			for (TargetingIdea targetingIdea : page.getEntries()) {
				Map<AttributeType, Attribute> data = Maps.toMap(targetingIdea.getData());
				StringAttribute keyword = (StringAttribute) data.get(AttributeType.KEYWORD_TEXT);

				IntegerSetAttribute categories =
						(IntegerSetAttribute) data.get(AttributeType.CATEGORY_PRODUCTS_AND_SERVICES);
				String categoriesString = "(none)";
				if (categories != null && categories.getValue() != null) {
					categoriesString = Joiner.on(", ").join(Ints.asList(categories.getValue()));
				}
				Long averageMonthlySearches =
						((LongAttribute) data.get(AttributeType.SEARCH_VOLUME))
						.getValue();

				MoneyAttribute avgCPC = ((MoneyAttribute) data.get(AttributeType.AVERAGE_CPC));
				Money avgCPCAmount = avgCPC.getValue();

				Double competition = ((DoubleAttribute) data.get(AttributeType.COMPETITION)).getValue();

				if(avgCPCAmount != null)
				{
					logger.info("\n=============== RECEIVED:: Keyword with details: ===============\n"
							+ "text: "+keyword.getValue()+"\n"
							+ "averageMonthlySearches: "+averageMonthlySearches+"\n"
							+ "categories: "+categoriesString+"\n"
							+ "competition: "+competition+"\n"
							+ "avgCPCAmount: "+(avgCPCAmount.getMicroAmount()/1000000d)+"\n"

						+ "=============================================");
					logger.info("\n+++++++++++++++ SUCCESS:: Keyword with details: +++++++++++++++\n"
							+ "text: "+keyword.getValue()+"\n"
							+ "averageMonthlySearches: "+averageMonthlySearches+"\n"
							+ "categories: "+categoriesString+" was found.\n"
							+ "competition: "+competition+"\n"
							+ "avgCPCAmount: "+(avgCPCAmount.getMicroAmount()/1000000d)+"\n"
							+ "+++++++++++++++++++++++++++++++++++++++++++++");
					KeywordDo keywordDo = new KeywordDo();
					keywordDo.setText(keyword.getValue());
					keywordDo.setAvgMonthlySearch(averageMonthlySearches);
					keywordDo.setCompetition(competition);
					keywordDo.setAvgCpc(avgCPCAmount.getMicroAmount()/1000000d);
					keywordDo.setBid(avgCPCAmount.getMicroAmount()/1000000d);
					keywordDos.add(keywordDo);
				}
			}
		} else {
			logger.info("\n??????????????? ERROR:: No related keywords were found. ???????????????");
		}

		keywordDos = sortAndTruncateKeywordDos(keywordDos,count);

		logger.info("############### Exiting getKeywordIdeas method ###############");
		return keywordDos;
	}


	private List<KeywordDo> sortAndTruncateKeywordDos(List<KeywordDo> keywordDos,int size) {

		Collections.sort(keywordDos);
		List<KeywordDo> newKeywordDos = new ArrayList<KeywordDo>();
		int count = 0;
		if(keywordDos.size()>size&&size>0)
		{
			for(KeywordDo keywordDo : keywordDos)
			{
				if(keywordDo.getAvgCpc()<60&&keywordDo.getAvgCpc()>0)
				{
					newKeywordDos.add(keywordDo);
					if(++count>=size)
						break;
				}

			}
		}
		return newKeywordDos;
	}


	public void syncExistingKeywordsList(AdgroupDo adgroupDo) throws ApiException, RemoteException
	{
		logger.info("*************** Entering syncExistingKeywordsList method ***************");
		AdGroupCriterionServiceInterface adGroupCriterionService =
				adWordsServices.get(session, AdGroupCriterionServiceInterface.class);

		String keywordIdList="";
		String keywordNameList="";
		// Create selector.
		SelectorBuilder builder = new SelectorBuilder();
		Selector selector = builder
				.fields(
						AdGroupCriterionField.Id,
						AdGroupCriterionField.CriteriaType,
						AdGroupCriterionField.KeywordMatchType,
						AdGroupCriterionField.KeywordText)
						.orderAscBy(AdGroupCriterionField.KeywordText)
						.in(AdGroupCriterionField.AdGroupId, adgroupDo.getApiId().toString())
						.in(AdGroupCriterionField.CriteriaType, "KEYWORD")
						.build();
		logger.info("\n=============== SEND:: adwordGroupDo with apiId: "+adgroupDo.getApiId()+"===============\n");
		// Get all ad group criteria.
		AdGroupCriterionPage page = adGroupCriterionService.get(selector);

		// Display ad group criteria.
		if (page.getEntries() != null && page.getEntries().length > 0) {
			// Display results.
			for (AdGroupCriterion adGroupCriterionResult : page.getEntries()) {
				Keyword keyword = (Keyword) adGroupCriterionResult.getCriterion();
				keywordIdList+=keyword.getId()+",";
				keywordNameList+=keyword.getText()+",";
			}

			logger.debug("\n=============== RECEIVED:: KeywordsList with details: ===============\n"
					+ "keywordIdList: "+keywordIdList+"\n"
					+ "keywordNameList: "+keywordNameList+"\n"
					+ "=============================================");
			logger.info("\n+++++++++++++++ SUCCESS:: KeywordsList with details: +++++++++++++++\n"
					+ "keywordIdList: "+keywordIdList+"\n"
					+ "keywordNameList: "+keywordNameList+" was synced successfully\n"
					+ "+++++++++++++++++++++++++++++++++++++++++++++");
		} else {
			logger.info("\n??????????????? ERROR:: No ad group criteria were found. ???????????????");
		}
		logger.info("############### Exiting syncExistingKeywordsList method ###############");
	}

	public List<KeywordDo> getKeywords(AdgroupDo adgroupDo) throws ApiException, RemoteException
	{
		logger.info("*************** Entering getKeywords method ***************");
		// Get the AdGroupCriterionService.
		AdGroupCriterionServiceInterface adGroupCriterionService =
				adWordsServices.get(session, AdGroupCriterionServiceInterface.class);

		List<KeywordDo> keywordDos = new ArrayList<KeywordDo>();
		// Create selector.
		SelectorBuilder builder = new SelectorBuilder();
		Selector selector = builder
				.fields(
						AdGroupCriterionField.Id,
						AdGroupCriterionField.KeywordText,
						AdGroupCriterionField.KeywordMatchType,
						AdGroupCriterionField.Status,
						AdGroupCriterionField.CpcBid)
						.orderAscBy(AdGroupCriterionField.KeywordText)
						.in(AdGroupCriterionField.AdGroupId, adgroupDo.getApiId().toString())
						.in(AdGroupCriterionField.CriteriaType, "KEYWORD")
						.build();

		logger.info("\n=============== SEND:: adwordGroupDo with apiId: "+adgroupDo.getApiId()+"===============\n");
		// Get all ad group criteria.
		AdGroupCriterionPage page = adGroupCriterionService.get(selector);

		// Display ad group criteria.
		if (page.getEntries() != null && page.getEntries().length > 0) {
			// Display results.
			for (AdGroupCriterion adGroupCriterionResult : page.getEntries()) {
				Keyword keyword = (Keyword) adGroupCriterionResult.getCriterion();
				BiddableAdGroupCriterion biddableAdGroupCriterion = (BiddableAdGroupCriterion) adGroupCriterionResult;
				Bids[] bids = biddableAdGroupCriterion.getBiddingStrategyConfiguration().getBids();

				CpcBid bid =new CpcBid();
				if((bids!=null)&&(bids.length>0))
					bid = (CpcBid)bids[0];
				else
					bid.setBid(new Money(null, 15*1000000L));

				KeywordDo keywordDo = new KeywordDo();
				keywordDo.setApiId(keyword.getId());

				if(biddableAdGroupCriterion.getUserStatus().toString().equals("ENABLED"))
					keywordDo.setStatus(KeywordDo.Status.Enabled.name());
				else if(biddableAdGroupCriterion.getUserStatus().toString().equals("PAUSED"))
					keywordDo.setStatus(KeywordDo.Status.Paused.name());
				else if(biddableAdGroupCriterion.getUserStatus().toString().equals("REMOVED"))
					keywordDo.setStatus(KeywordDo.Status.Removed.name());
				else
					keywordDo.setStatus(KeywordDo.Status.Unknown.name());

				keywordDo.setMatchType(keyword.getMatchType().getValue());
				keywordDo.setText(keyword.getText());
				keywordDo.setBid(new Double(bid.getBid().getMicroAmount()/1000000d));
				keywordDos.add(keywordDo);
				logger.info("\n=============== RECEIVED:: KeywordDo with details: ===============\n"
						+ "apiId: "+keywordDo.getApiId()+"\n"
						+ "status: "+keywordDo.getStatus()+"\n"
						+ "matchType: "+keywordDo.getMatchType()+"\n"
						+ "text: "+keywordDo.getText()+"\n"
						+ "bid: "+keywordDo.getBid()+"\n"
						+ "=============================================");
			}
		} else {
			logger.info("\n??????????????? ERROR:: No ad group criteria were found. ???????????????");
		}

		logger.info("############### Exiting getKeywords method ###############");
		return keywordDos;
	}
}