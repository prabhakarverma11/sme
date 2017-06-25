package com.dq.arq.sme.adwordapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dq.arq.sme.domain.CampaignDo;
import com.dq.arq.sme.domain.LocationDo;
import com.dq.arq.sme.util.UtilityMethod;
import com.google.api.ads.adwords.axis.factory.AdWordsServices;
import com.google.api.ads.adwords.axis.v201607.cm.AdSchedule;
import com.google.api.ads.adwords.axis.v201607.cm.AdServingOptimizationStatus;
import com.google.api.ads.adwords.axis.v201607.cm.Address;
import com.google.api.ads.adwords.axis.v201607.cm.AdvertisingChannelType;
import com.google.api.ads.adwords.axis.v201607.cm.ApiException;
import com.google.api.ads.adwords.axis.v201607.cm.BiddingStrategyConfiguration;
import com.google.api.ads.adwords.axis.v201607.cm.BiddingStrategyType;
import com.google.api.ads.adwords.axis.v201607.cm.Budget;
import com.google.api.ads.adwords.axis.v201607.cm.BudgetBudgetDeliveryMethod;
import com.google.api.ads.adwords.axis.v201607.cm.BudgetOperation;
import com.google.api.ads.adwords.axis.v201607.cm.BudgetServiceInterface;
import com.google.api.ads.adwords.axis.v201607.cm.Campaign;
import com.google.api.ads.adwords.axis.v201607.cm.CampaignCriterion;
import com.google.api.ads.adwords.axis.v201607.cm.CampaignCriterionOperation;
import com.google.api.ads.adwords.axis.v201607.cm.CampaignCriterionPage;
import com.google.api.ads.adwords.axis.v201607.cm.CampaignCriterionReturnValue;
import com.google.api.ads.adwords.axis.v201607.cm.CampaignCriterionServiceInterface;
import com.google.api.ads.adwords.axis.v201607.cm.CampaignOperation;
import com.google.api.ads.adwords.axis.v201607.cm.CampaignReturnValue;
import com.google.api.ads.adwords.axis.v201607.cm.CampaignServiceInterface;
import com.google.api.ads.adwords.axis.v201607.cm.CampaignStatus;
import com.google.api.ads.adwords.axis.v201607.cm.Criterion;
import com.google.api.ads.adwords.axis.v201607.cm.DayOfWeek;
import com.google.api.ads.adwords.axis.v201607.cm.FrequencyCap;
import com.google.api.ads.adwords.axis.v201607.cm.GeoTargetTypeSetting;
import com.google.api.ads.adwords.axis.v201607.cm.GeoTargetTypeSettingPositiveGeoTargetType;
import com.google.api.ads.adwords.axis.v201607.cm.Level;
import com.google.api.ads.adwords.axis.v201607.cm.Location;
import com.google.api.ads.adwords.axis.v201607.cm.ManualCpcBiddingScheme;
import com.google.api.ads.adwords.axis.v201607.cm.MinuteOfHour;
import com.google.api.ads.adwords.axis.v201607.cm.Money;
import com.google.api.ads.adwords.axis.v201607.cm.NegativeCampaignCriterion;
import com.google.api.ads.adwords.axis.v201607.cm.NetworkSetting;
import com.google.api.ads.adwords.axis.v201607.cm.Operator;
import com.google.api.ads.adwords.axis.v201607.cm.Platform;
import com.google.api.ads.adwords.axis.v201607.cm.Predicate;
import com.google.api.ads.adwords.axis.v201607.cm.PredicateOperator;
import com.google.api.ads.adwords.axis.v201607.cm.Proximity;
import com.google.api.ads.adwords.axis.v201607.cm.ProximityDistanceUnits;
import com.google.api.ads.adwords.axis.v201607.cm.Selector;
import com.google.api.ads.adwords.axis.v201607.cm.Setting;
import com.google.api.ads.adwords.axis.v201607.cm.TimeUnit;
import com.google.api.ads.adwords.lib.client.AdWordsSession;
import com.google.api.ads.adwords.lib.client.reporting.ReportingConfiguration;
import com.google.api.ads.adwords.lib.jaxb.v201607.DownloadFormat;
import com.google.api.ads.adwords.lib.utils.ReportDownloadResponse;
import com.google.api.ads.adwords.lib.utils.ReportDownloadResponseException;
import com.google.api.ads.adwords.lib.utils.ReportException;
import com.google.api.ads.adwords.lib.utils.v201607.ReportDownloader;
import com.google.api.ads.common.lib.auth.OfflineCredentials;
import com.google.api.ads.common.lib.auth.OfflineCredentials.Api;
import com.google.api.ads.common.lib.conf.ConfigurationLoadException;
import com.google.api.ads.common.lib.exception.OAuthException;
import com.google.api.ads.common.lib.exception.ValidationException;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.util.Charsets;
import com.google.common.base.Splitter;

public class CampaignAdwordApi {


	static final Logger logger = LoggerFactory.getLogger("AdwordsLog");

	Credential oAuth2Credential;
	AdWordsSession session;
	AdWordsServices adWordsServices;


	public CampaignAdwordApi() {
		logger.info("*************** Entering CampaignAdwordApi constructor ***************");
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
		logger.info("############### Exiting CampaignAdwordApi constructor ###############");
	}

	public void createCampaign(CampaignDo campaignDo,LocationDo locationDo) throws ApiException, RemoteException
	{
		logger.info("*************** Entering createCampaign method ***************");
		// Get the BudgetService.
		BudgetServiceInterface budgetService =
				adWordsServices.get(session, BudgetServiceInterface.class);

		CampaignCriterionServiceInterface campaignCriterionService =
				adWordsServices.get(session, CampaignCriterionServiceInterface.class);

		// Create a budget, which can be shared by multiple campaigns.
		Budget sharedBudget = new Budget();
		sharedBudget.setName(campaignDo.getName()+"_budget #" + System.currentTimeMillis());
		Money budgetAmount = new Money();
		budgetAmount.setMicroAmount(campaignDo.getBudgetAmount().longValue()*1000000);
		sharedBudget.setAmount(budgetAmount);
		sharedBudget.setDeliveryMethod(BudgetBudgetDeliveryMethod.STANDARD);

		BudgetOperation budgetOperation = new BudgetOperation();
		budgetOperation.setOperand(sharedBudget);
		budgetOperation.setOperator(Operator.ADD);

		logger.info("\n=============== SEND:: campaignDo with details: ===============\n"
				+ "Id: "+campaignDo.getId()+"\n"
				+ "name: "+campaignDo.getName()+"\n"
				+ "budgetAmount: "+campaignDo.getBudgetAmount()+"\n"
				+ "=============================================");
		// Add the budget
		Long budgetId =  budgetService.mutate(new BudgetOperation[] {budgetOperation}).getValue(0).getBudgetId();
		logger.info("=============== RECEIVED:: budgetID: "+budgetId+" ===============");
		campaignDo.setBudgetId(budgetId);
		// Get the CampaignService.
		CampaignServiceInterface campaignService =
				adWordsServices.get(session, CampaignServiceInterface.class);

		// Create campaign.
		Campaign campaign = new Campaign();
		campaign.setName(campaignDo.getName());
		if(campaignDo.getStatus()==CampaignDo.Status.Enabled.name())
			campaign.setStatus(CampaignStatus.ENABLED);
		else if(campaignDo.getStatus()==CampaignDo.Status.Paused.name())
			campaign.setStatus(CampaignStatus.PAUSED);
		else if(campaignDo.getStatus()==CampaignDo.Status.Removed.name()) 
			campaign.setStatus(CampaignStatus.REMOVED);
		else
			campaign.setStatus(CampaignStatus.UNKNOWN);
		BiddingStrategyConfiguration biddingStrategyConfiguration = new BiddingStrategyConfiguration();
		biddingStrategyConfiguration.setBiddingStrategyType(BiddingStrategyType.MANUAL_CPC);

		// You can optionally provide a bidding scheme in place of the type.
		ManualCpcBiddingScheme cpcBiddingScheme = new ManualCpcBiddingScheme();
		cpcBiddingScheme.setEnhancedCpcEnabled(false);
		biddingStrategyConfiguration.setBiddingScheme(cpcBiddingScheme);

		campaign.setBiddingStrategyConfiguration(biddingStrategyConfiguration);


		// You can optionally provide these field(s).
		//campaign.setStartDate(new DateTime().plusDays(1).toString("yyyyMMdd"));
		campaign.setStartDate(UtilityMethod.formatDateTOYYYYMMDD(campaignDo.getStartDate()));
		//campaign.setEndDate(new DateTime().plusDays(30).toString("yyyyMMdd"));
		campaign.setEndDate(UtilityMethod.formatDateTOYYYYMMDD(campaignDo.getEndDate()));
		campaign.setAdServingOptimizationStatus(AdServingOptimizationStatus.OPTIMIZE);
		campaign.setFrequencyCap(new FrequencyCap(5L, TimeUnit.DAY, Level.ADGROUP));

		// Only the budgetId should be sent, all other fields will be ignored by CampaignService.
		Budget budget = new Budget();
		budget.setBudgetId(budgetId);
		campaign.setBudget(budget);

		campaign.setAdvertisingChannelType(AdvertisingChannelType.SEARCH);

		// Set the campaign network options to Search and Search Network.
		NetworkSetting networkSetting = new NetworkSetting();
		networkSetting.setTargetGoogleSearch(true);
		networkSetting.setTargetSearchNetwork(true);
		networkSetting.setTargetContentNetwork(false);
		networkSetting.setTargetPartnerSearchNetwork(false);
		campaign.setNetworkSetting(networkSetting);

		// Set options that are not required.
		GeoTargetTypeSetting geoTarget = new GeoTargetTypeSetting();
		geoTarget.setPositiveGeoTargetType(GeoTargetTypeSettingPositiveGeoTargetType.DONT_CARE);
		campaign.setSettings(new Setting[] {geoTarget});





		// Create operations.
		CampaignOperation operation = new CampaignOperation();
		operation.setOperand(campaign);
		operation.setOperator(Operator.ADD);
		CampaignOperation[] operations = new CampaignOperation[] {operation};

		logger.info("\n=============== SEND:: campaign with details: ===============\n"
				+ "Id: "+campaignDo.getId()+"\n"
				+ "apiID: "+campaignDo.getApiId()+"\n"
				+ "name: "+campaign.getName()+"\n"
				+ "status: "+campaign.getStatus()+"\n"
				+ "biddingStrategyType: "+campaign.getBiddingStrategyConfiguration().getBiddingStrategyType()+"\n"
				+ "startDate: "+campaign.getStartDate()+"\n"
				+ "endDate: "+campaign.getEndDate()+"\n"
				+ "adServingOptimizationStatus: "+campaign.getAdServingOptimizationStatus()+"\n"
				+ "frequencyCapLevel: "+campaign.getFrequencyCap().getLevel()+"\n"
				+ "budgetId: "+campaign.getBudget().getBudgetId()+"\n"
				+ "advertisingChannelType: "+campaign.getAdvertisingChannelType()+"\n"
				+ "locationIncludeCriteria :" +campaignDo.getLocationIncludeCriteria()+"\n"
				+ "locationInclude :" +campaignDo.getLocationInclude()+"\n"
				+ "locationExcludeCriteria :" +campaignDo.getLocationExcludeCriteria()+"\n"
				+ "locationExclude :" +campaignDo.getLocationExclude()+"\n"
				+ "=============================================");
		// Add campaigns.
		CampaignReturnValue result = campaignService.mutate(operations);

		// Display campaigns.
		for (Campaign campaignResult : result.getValue()) {
			campaignDo.setApiId(campaignResult.getId());
			logger.info("\n=============== RECEIVED:: CampaignResult with details: ===============\n"
					+ "name: "+campaignResult.getName()+"\n"
					+ "ID: "+campaignResult.getId()+"\n"
					+ "=============================================");
			logger.info("\n+++++++++++++++ SUCCESS:: CampaignResult with details: +++++++++++++++\n"
					+ "name: "+campaignResult.getName()+"\n"
					+ "ID: "+campaignResult.getId()+" was added.\n"
					+ "+++++++++++++++++++++++++++++++++++++++++++++");
			//campaignDo.setBiddingStrategyId(campaignResult.getBiddingStrategyConfiguration().getBiddingStrategyId().intValue());
			//campaignDo.setAdvertisingChannelType(campaignResult.getAdvertisingChannelType().toString());
		}
		
		//Ad Scheduling starts
		List<CampaignCriterionOperation> campaignCriterionOperations = new ArrayList<CampaignCriterionOperation>();

		DayOfWeek[] days = new DayOfWeek[] {DayOfWeek.MONDAY,
				DayOfWeek.TUESDAY,
				DayOfWeek.WEDNESDAY,
				DayOfWeek.THURSDAY,
				DayOfWeek.FRIDAY,
				DayOfWeek.SATURDAY,
				DayOfWeek.SUNDAY};

		for (int i = 0; i < 7; i++) {
			AdSchedule schedule = new AdSchedule();
			schedule.setDayOfWeek(days[i]);
			// Start at 9:00 am...
			schedule.setStartHour(9);
			schedule.setStartMinute(MinuteOfHour.ZERO);
			// ... and end at 9:00 pm
			schedule.setEndHour(21);
			schedule.setEndMinute(MinuteOfHour.ZERO);
			CampaignCriterionOperation operationForAdSchedule = new CampaignCriterionOperation();
			CampaignCriterion campaignCriterionForAdSchedule = new CampaignCriterion();

			campaignCriterionForAdSchedule.setCampaignId(campaignDo.getApiId());
			campaignCriterionForAdSchedule.setCriterion(schedule);
			// Run at normal bid rates
			campaignCriterionForAdSchedule.setBidModifier(1.0);
			operationForAdSchedule.setOperand(campaignCriterionForAdSchedule);
			operationForAdSchedule.setOperator(Operator.ADD);
			campaignCriterionOperations.add(operationForAdSchedule);
		}


		//Ad Scdehuling ends
		//Setting desktop adjustable bid to -100 starts
		Platform desktop = new Platform();
		desktop.setId(30000L);

		CampaignCriterion campaignCriterion = new CampaignCriterion();
		campaignCriterion.setCampaignId(campaignDo.getApiId());
		campaignCriterion.setCriterion(desktop);
		campaignCriterion.setBidModifier(0.0);
		CampaignCriterionOperation platformOperation = new CampaignCriterionOperation();
		platformOperation.setOperand(campaignCriterion);
		platformOperation.setOperator(Operator.SET);
		campaignCriterionOperations.add(platformOperation);
		//Setting desktop adjustable bid to -100 ends


		CampaignCriterionReturnValue creterionResult =
				campaignCriterionService.mutate(campaignCriterionOperations
						.toArray(new CampaignCriterionOperation[campaignCriterionOperations.size()]));
				
		//Targeting based on Location list provided by user starts
		campaignCriterionOperations = new ArrayList<CampaignCriterionOperation>();
		List<Criterion> includeLocationCriteria = new ArrayList<Criterion>();
		if(campaignDo.getLocationIncludeCriteria()==2||campaignDo.getLocationIncludeCriteria()==3)
		{
			if(!"".equals(campaignDo.getLocationInclude()))
			{
				for(String locationId: campaignDo.getLocationInclude().split(","))
				{
					Location location = new Location();
					location.setId(Long.parseLong(locationId));
					includeLocationCriteria.add(location);

				}


			}
		}
		else if(campaignDo.getLocationIncludeCriteria()==4)
		{
			String locArray []= campaignDo.getLocationInclude().split(":");
			Address address = new Address();
			Proximity proximity = new Proximity();
			if(locArray.length>4)
			{

				if(!"".equals(locArray[0]))
					address.setCountryCode(locArray[0]);


				if(!"".equals(locArray[1]))
					address.setStreetAddress(locArray[1]);


				if(!"".equals(locArray[2]))
					address.setCityName(locArray[2]);

				if(!"".equals(locArray[3]))
					address.setProvinceName(locArray[3]);


				if(!"".equals(locArray[4]))
					address.setPostalCode(locArray[4]);


				proximity.setAddress(address);

				if(locArray[6].equals("miles"))
					proximity.setRadiusDistanceUnits(ProximityDistanceUnits.MILES);
				else
					proximity.setRadiusDistanceUnits(ProximityDistanceUnits.KILOMETERS);
				proximity.setRadiusInUnits((double) Double.parseDouble(locArray[5]));
			}
			else{
				String locName[] = locationDo.getCanonicalName().split(",");
				if(locationDo.getTargetType().equals("Postal Code"))
				{
					address.setPostalCode(locName[0]);
					address.setCityName(locName[1]);
					address.setCountryCode(locationDo.getCountryCode());
				}
				else if(locName.length==3)
				{
					address.setCityName(locName[0]);
					address.setProvinceName(locName[1]);
					address.setCountryCode(locationDo.getCountryCode());
				}
				else if(locName.length==2)
				{
					address.setProvinceName(locName[0]);
					address.setCountryCode(locationDo.getCountryCode());
				}
				else if(locName.length==1)
				{
					address.setCountryCode(locationDo.getCountryCode());
				}

				proximity.setAddress(address);

				if(locArray[2].equals("miles"))
					proximity.setRadiusDistanceUnits(ProximityDistanceUnits.MILES);
				else
					proximity.setRadiusDistanceUnits(ProximityDistanceUnits.KILOMETERS);
				proximity.setRadiusInUnits((double) Double.parseDouble(locArray[1]));
				logger.info("\n=============== SEND:: locationDo with details: ===============\n"
						+ "canonicalName: "+locationDo.getCanonicalName()+"\n"
						+ "targetType: "+locationDo.getTargetType()+"\n"
						+ "countryCode: "+locationDo.getCountryCode()+"\n"
						+ "=============================================");
			}
			includeLocationCriteria.add(proximity);

		}

		for (Criterion criterion : includeLocationCriteria) {
			CampaignCriterionOperation locationIncludeOperation = new CampaignCriterionOperation();
			CampaignCriterion campCriterion = new CampaignCriterion();
			campCriterion.setCampaignId(campaignDo.getApiId());
			campCriterion.setCriterion(criterion);
			locationIncludeOperation.setOperand(campCriterion);
			locationIncludeOperation.setOperator(Operator.ADD);
			campaignCriterionOperations.add(locationIncludeOperation);
		}

		List<Criterion> excludeLocationCriteria = new ArrayList<Criterion>();
		if(!"".equals(campaignDo.getLocationExclude()))
		{
			for(String locationId: campaignDo.getLocationExclude().split(","))
			{
				Location location = new Location();
				location.setId(Long.parseLong(locationId));
				excludeLocationCriteria.add(location);
			}

			for (Criterion criterion : excludeLocationCriteria) {
				CampaignCriterionOperation locationExcludeOperation = new CampaignCriterionOperation();
				CampaignCriterion campCriterion = new NegativeCampaignCriterion();
				campCriterion.setCampaignId(campaignDo.getApiId());
				campCriterion.setCriterion(criterion);
				locationExcludeOperation.setOperand(campCriterion);
				locationExcludeOperation.setOperator(Operator.ADD);
				campaignCriterionOperations.add(locationExcludeOperation);
			}
		}



		if(campaignCriterionOperations.size()>0)
		{

			CampaignCriterionReturnValue locationCreterionResult =
					campaignCriterionService.mutate(campaignCriterionOperations
							.toArray(new CampaignCriterionOperation[campaignCriterionOperations.size()]));

			// Display campaigns.
			for (CampaignCriterion campaignCriterionResult : locationCreterionResult.getValue()) {

				logger.info("\n=============== RECEIVED:: CampaignCriterionResult with details: ===============\n"
						+ "campaignId: "+campaignCriterionResult.getCampaignId()+"\n"
						+ "criterionId "+campaignCriterionResult.getCriterion().getId()+"\n"
						+ "type: "+campaignCriterionResult.getCriterion().getCriterionType()+"\n"
						+ "=============================================");
				logger.info("\n+++++++++++++++ SUCCESS:: CampaignCriterionResult with details: +++++++++++++++\n"
						+ "campaignId: "+campaignCriterionResult.getCampaignId()+"\n"
						+ "criterionId "+campaignCriterionResult.getCriterion().getId()+"\n"
						+ "type: "+campaignCriterionResult.getCriterion().getCriterionType()+" was added\n"
						+ "+++++++++++++++++++++++++++++++++++++++++++++");

				if(campaignCriterionResult.getCriterion().getCriterionType().equals("Proximity"))
				{
					String locArray []= campaignDo.getLocationInclude().split(":");
					if(locArray.length>4)
					{
						campaignDo.setLocationInclude(campaignCriterionResult.getCriterion().getId()+":"+campaignDo.getLocationInclude());
					}
				}
			}
		}



		//Targeting based on Location list provided by user ends
		logger.info("############### Exiting createCampaign method ###############");
	}

	public void updateCampaign(CampaignDo campaignDo,LocationDo locationDo,boolean isLocationChanged,boolean isBudgetChanged) throws ApiException, RemoteException
	{
		logger.info("*************** Entering updateCampaign method ***************");
		// Get the CampaignService.
		CampaignServiceInterface campaignService =
				adWordsServices.get(session, CampaignServiceInterface.class);
		// Get the BudgetService.
		BudgetServiceInterface budgetService =
				adWordsServices.get(session, BudgetServiceInterface.class);
		CampaignCriterionServiceInterface campaignCriterionService =
				adWordsServices.get(session, CampaignCriterionServiceInterface.class);

		// Create campaign with updated status.
		Campaign campaign = new Campaign();
		campaign.setId(campaignDo.getApiId());
		campaign.setName(campaignDo.getName());
		//campaign.setStartDate(UtilityMethod.formatDateTOYYYYMMDD(campaignDo.getStartDate()));
		campaign.setEndDate(UtilityMethod.formatDateTOYYYYMMDD(campaignDo.getEndDate()));
		if(campaignDo.getStatus()==CampaignDo.Status.Enabled.name())
			campaign.setStatus(CampaignStatus.ENABLED);
		else if(campaignDo.getStatus()==CampaignDo.Status.Paused.name())
			campaign.setStatus(CampaignStatus.PAUSED);
		else if(campaignDo.getStatus()==CampaignDo.Status.Removed.name()) 
			campaign.setStatus(CampaignStatus.REMOVED);
		else
			campaign.setStatus(CampaignStatus.UNKNOWN);

		if(isBudgetChanged)
		{
			//Update budget starts
			// Create a budget, which can be shared by multiple campaigns.
			Budget sharedBudget = new Budget();
			sharedBudget.setName(campaignDo.getName()+"_budget #" + System.currentTimeMillis());
			Money budgetAmount = new Money();
			budgetAmount.setMicroAmount(campaignDo.getBudgetAmount().longValue()*1000000);
			sharedBudget.setAmount(budgetAmount);
			sharedBudget.setDeliveryMethod(BudgetBudgetDeliveryMethod.STANDARD);

			BudgetOperation budgetOperation = new BudgetOperation();
			budgetOperation.setOperand(sharedBudget);
			budgetOperation.setOperator(Operator.ADD);
			logger.info("\n=============== SEND:: campaignDo with details: ===============\n"
					+ "Id: "+campaignDo.getId()+"\n"
					+ "name: "+campaignDo.getName()+"\n"
					+ "budgetAmount: "+campaignDo.getBudgetAmount()+"\n"
					+ "endDate: "+campaignDo.getEndDate()+"\n"
					+ "status: "+campaignDo.getStatus()+"\n"
					+ "=============================================");
			// Add the budget
			Long budgetId =  budgetService.mutate(new BudgetOperation[] {budgetOperation}).getValue(0).getBudgetId();
			logger.info("\n=============== RECEIVED:: budgetId: "+budgetId+" ===============");
			campaignDo.setBudgetId(budgetId);
			Budget budget = new Budget();
			budget.setBudgetId(budgetId);
			campaign.setBudget(budget);
			//Update budget ends
		}


		// Create operations.
		CampaignOperation operation = new CampaignOperation();
		operation.setOperand(campaign);
		operation.setOperator(Operator.SET);

		CampaignOperation[] operations = new CampaignOperation[] {operation};

		logger.info("\n=============== SEND:: campaignDo with details: ===============\n"
				+ "Id: "+campaignDo.getId()+"\n"
				+ "apiId: "+campaignDo.getApiId()+"\n"
				+ "name: "+campaignDo.getName()+"\n"
				+ "budgetAmount: "+campaignDo.getBudgetAmount()+"\n"
				+ "endDate: "+campaignDo.getEndDate()+"\n"
				+ "status: "+campaignDo.getStatus()+"\n"
				+ "budgetId: "+campaignDo.getBudgetId()+"\n"
				+ "locationIncludeCriteria :" +campaignDo.getLocationIncludeCriteria()+"\n"
				+ "locationInclude :" +campaignDo.getLocationInclude()+"\n"
				+ "locationExcludeCriteria :" +campaignDo.getLocationExcludeCriteria()+"\n"
				+ "locationExclude :" +campaignDo.getLocationExclude()+"\n"
				+ "=============================================");
		// Update campaign.
		CampaignReturnValue result = campaignService.mutate(operations);

		// Display campaigns.
		for (Campaign campaignResult : result.getValue()) {

			logger.info("\n===============RECEIVED:: campaignResult with details: ===============\n"
					+ "name: "+campaignResult.getName()+"\n"
					+ "ID: "+campaignResult.getId()+"\n"
					+ "deliveryMethod: "+campaignResult.getBudget().getDeliveryMethod()+"\n"
					+ "=============================================");

			logger.info("\n+++++++++++++++ SUCCESS:: CampaignResult with details: +++++++++++++++\n"
					+ "name: "+campaignResult.getName()+"\n"
					+ "ID: "+campaignResult.getId()+"\n"
					+ "deliveryMethod: "+campaignResult.getBudget().getDeliveryMethod()+" was updated\n"
					+ "+++++++++++++++++++++++++++++++++++++++++++++");
		}

		if(isLocationChanged)
		{
			//Targeting based on Location list provided by user starts

			//Fetch list of existing locations starts
			String existingLocationsToExclude=getExistingLocationsToExclude(campaignDo.getApiId());
			String existingLocationsToInclude=getExistingLocationsToInclude(campaignDo.getApiId());
			String existingLocations=existingLocationsToInclude+existingLocationsToExclude;
			String existingProximities=getExistingProximitiesIds(campaignDo.getApiId());

			//Fetch list of existing locations ends

			//Removal of existing locations starts

			List<CampaignCriterionOperation> locationRemoveCriterionOperations = new ArrayList<CampaignCriterionOperation>();

			List<Criterion> locationCriteria = new ArrayList<Criterion>();

			if(!"".equals(existingLocations))
			{
				for(String locationId: existingLocations.split(","))
				{
					Location location = new Location();
					location.setId(Long.parseLong(locationId));
					locationCriteria.add(location);
				}
			}



			if(!"".equals(existingProximities))
			{
				for(String proximityId: existingProximities.split(","))
				{
					Proximity proximity = new Proximity();
					proximity.setId(Long.parseLong(proximityId));
					locationCriteria.add(proximity);
				}
			}


			for (Criterion criterion : locationCriteria) {
				CampaignCriterionOperation locationOperation = new CampaignCriterionOperation();
				CampaignCriterion campaignCriterion = new CampaignCriterion();
				campaignCriterion.setCampaignId(campaignDo.getApiId());
				campaignCriterion.setCriterion(criterion);
				locationOperation.setOperand(campaignCriterion);
				locationOperation.setOperator(Operator.REMOVE);
				locationRemoveCriterionOperations.add(locationOperation);
			}


			if(locationRemoveCriterionOperations.size()>0)
			{	
				CampaignCriterionReturnValue locationRemoveCriterionResult =
						campaignCriterionService.mutate(locationRemoveCriterionOperations
								.toArray(new CampaignCriterionOperation[locationRemoveCriterionOperations.size()]));

				// Display campaigns.
				for (CampaignCriterion campaignCriterion : locationRemoveCriterionResult.getValue()) {

					logger.info("\n=============== RECEIVED:: campaignCriterion with details: ===============\n"
							+ "campaignId: "+campaignCriterion.getCampaignId()+"\n"
							+ "criterionId: "+campaignCriterion.getCriterion().getId()+"\n"
							+ "type: "+campaignCriterion.getCriterion().getCriterionType()+"\n"
							+ "=============================================");
					logger.info("\n+++++++++++++++ SUCCESS:: campaignCriterion with details:+++++++++++++++\n"
							+ "campaignId: "+campaignCriterion.getCampaignId()+"\n"
							+ "criterionId: "+campaignCriterion.getCriterion().getId()+"\n"
							+ "type: "+campaignCriterion.getCriterion().getCriterionType()+" was removed.\n"
							+ "+++++++++++++++++++++++++++++++++++++++++++++");


				}
			}


			//Removal of existing locations ends

			List<CampaignCriterionOperation> locationCriterionOperations = new ArrayList<CampaignCriterionOperation>();

			List<Criterion> includeLocationCriteria = new ArrayList<Criterion>();
			if(campaignDo.getLocationIncludeCriteria()==2||campaignDo.getLocationIncludeCriteria()==3)
			{
				if(!"".equals(campaignDo.getLocationInclude()))
				{
					for(String locationId: campaignDo.getLocationInclude().split(","))
					{
						Location location = new Location();
						location.setId(Long.parseLong(locationId));
						includeLocationCriteria.add(location);
					}


				}
			}
			else if(campaignDo.getLocationIncludeCriteria()==4)
			{
				String locArray []= campaignDo.getLocationInclude().split(":");
				Address address = new Address();
				Proximity proximity = new Proximity();
				if(locArray.length>4)
				{

					if(!"".equals(locArray[0]))
						address.setCountryCode(locArray[0]);


					if(!"".equals(locArray[1]))
						address.setStreetAddress(locArray[1]);


					if(!"".equals(locArray[2]))
						address.setCityName(locArray[2]);

					if(!"".equals(locArray[3]))
						address.setProvinceName(locArray[3]);


					if(!"".equals(locArray[4]))
						address.setPostalCode(locArray[4]);


					proximity.setAddress(address);

					if(locArray[6].equals("miles"))
						proximity.setRadiusDistanceUnits(ProximityDistanceUnits.MILES);
					else

						proximity.setRadiusDistanceUnits(ProximityDistanceUnits.KILOMETERS);
					proximity.setRadiusInUnits((double) Double.parseDouble(locArray[5]));
				}
				else{
					String locName[] = locationDo.getCanonicalName().split(",");
					if(locationDo.getTargetType().equals("Postal Code"))
					{
						address.setPostalCode(locName[0]);
						address.setCityName(locName[1]);
						address.setCountryCode(locationDo.getCountryCode());
					}
					else if(locName.length==3)
					{
						address.setCityName(locName[0]);
						address.setProvinceName(locName[1]);
						address.setCountryCode(locationDo.getCountryCode());
					}
					else if(locName.length==2)
					{
						address.setProvinceName(locName[0]);
						address.setCountryCode(locationDo.getCountryCode());
					}
					else if(locName.length==1)
					{
						address.setCountryCode(locationDo.getCountryCode());
					}

					proximity.setAddress(address);

					if(locArray[2].equals("miles"))
						proximity.setRadiusDistanceUnits(ProximityDistanceUnits.MILES);
					else

						proximity.setRadiusDistanceUnits(ProximityDistanceUnits.KILOMETERS);
					proximity.setRadiusInUnits((double) Double.parseDouble(locArray[1]));
					logger.info("\n=============== SEND:: locationDo with details: ===============\n"
							+ "canonicalName: "+locationDo.getCanonicalName()+"\n"
							+ "targetType: "+locationDo.getTargetType()+"\n"
							+ "countryCode: "+locationDo.getCountryCode()+"\n"
							+ "=============================================");
				}
				includeLocationCriteria.add(proximity);

			}

			for (Criterion criterion : includeLocationCriteria) {
				CampaignCriterionOperation locationIncludeOperation = new CampaignCriterionOperation();
				CampaignCriterion campaignCriterion = new CampaignCriterion();
				campaignCriterion.setCampaignId(campaignDo.getApiId());
				campaignCriterion.setCriterion(criterion);
				locationIncludeOperation.setOperand(campaignCriterion);
				locationIncludeOperation.setOperator(Operator.ADD);
				locationCriterionOperations.add(locationIncludeOperation);
			}



			List<Criterion> excludeLocationCriteria = new ArrayList<Criterion>();
			if(!"".equals(campaignDo.getLocationExclude()))
			{
				for(String locationId: campaignDo.getLocationExclude().split(","))
				{
					Location location = new Location();
					location.setId(Long.parseLong(locationId));
					excludeLocationCriteria.add(location);
				}

				for (Criterion criterion : excludeLocationCriteria) {
					CampaignCriterionOperation locationExcludeOperation = new CampaignCriterionOperation();
					CampaignCriterion campaignCriterion = new NegativeCampaignCriterion();
					campaignCriterion.setCampaignId(campaignDo.getApiId());
					campaignCriterion.setCriterion(criterion);
					locationExcludeOperation.setOperand(campaignCriterion);
					locationExcludeOperation.setOperator(Operator.ADD);
					locationCriterionOperations.add(locationExcludeOperation);
				}
			}

			if(locationCriterionOperations.size()>0)
			{
				CampaignCriterionReturnValue locationCriterionResult =
						campaignCriterionService.mutate(locationCriterionOperations
								.toArray(new CampaignCriterionOperation[locationCriterionOperations.size()]));

				// Display campaigns.
				for (CampaignCriterion campaignCriterion : locationCriterionResult.getValue()) {
					logger.info("\n=============== RECEIVED:: campaignCriterion with details: ===============\n"
							+ "campaignId: "+campaignCriterion.getCampaignId()+"\n"
							+ "criterionId: "+campaignCriterion.getCriterion().getId()+"\n"
							+ "type: "+campaignCriterion.getCriterion().getCriterionType()+"\n"
							+ "=============================================");
					logger.info("\n+++++++++++++++ SUCCESS:: campaignCriterion with details: +++++++++++++++\n"
							+ "campaignId: "+campaignCriterion.getCampaignId()+"\n"
							+ "criterionId: "+campaignCriterion.getCriterion().getId()+"\n"
							+ "type: "+campaignCriterion.getCriterion().getCriterionType()+" was added.\n"
							+ "+++++++++++++++++++++++++++++++++++++++++++++");

					if(campaignCriterion.getCriterion().getCriterionType().equals("Proximity"))
					{
						String locArray []= campaignDo.getLocationInclude().split(":");
						if(locArray.length>4)
						{
							campaignDo.setLocationInclude(campaignCriterion.getCriterion().getId()+":"+campaignDo.getLocationInclude());
						}
					}
				}

			}
		}
		logger.info("############### Exiting updateCampaign method ###############");
	}

	public String getExistingProximitiesIds(Long campaignApiId) throws ApiException, RemoteException {
		logger.info("*************** Entering getExistingProximitiesIds method ***************");
		CampaignCriterionServiceInterface campaignCriterionService =
				adWordsServices.get(session, CampaignCriterionServiceInterface.class);

		String existingProximities="";
		//Fetching Proximity criteria starts

		Selector selector = new Selector();
		selector.setFields(new String[] {"CampaignId", "Id", "CriteriaType"});

		selector.setPredicates(new Predicate[] {new Predicate("CriteriaType",
				PredicateOperator.EQUALS, new String[] {"PROXIMITY"}),new Predicate("CampaignId",
						PredicateOperator.EQUALS, new String[] {campaignApiId+""})});


		//selector.setPaging(new Paging(offset, PAGE_SIZE));
		CampaignCriterionPage page = null;
		page = null;
		// do {
		logger.info("\n=============== SEND:: campaignApiId: "+campaignApiId+" ===============\n");
		page = campaignCriterionService.get(selector);

		if (page.getEntries() != null) {
			// Display campaigns.
			for (CampaignCriterion campaignCriterion : page.getEntries()) {

				logger.info("\n=============== RECEIVED:: campaignCriterion with details: ===============\n"
						+ "campaignId: "+campaignCriterion.getCampaignId()+"\n"
						+ "criterionId: "+campaignCriterion.getCriterion().getId()+"\n"
						+ "type: "+campaignCriterion.getCriterion().getCriterionType()+"\n"
						+ "=============================================");
				logger.info("\n+++++++++++++++ SUCCESS:: campaignCriterion with details: +++++++++++++++\n"
						+ "campaignId: "+campaignCriterion.getCampaignId()+"\n"
						+ "criterionId: "+campaignCriterion.getCriterion().getId()+"\n"
						+ "type: "+campaignCriterion.getCriterion().getCriterionType()+" was found\n"
						+ "+++++++++++++++++++++++++++++++++++++++++++++");
				existingProximities+=campaignCriterion.getCriterion().getId()+",";
			}
		} else {
			logger.info("\n??????????????? ERROR:: No campaign proximity Criteria were found.???????????????\n");
		}
		//Fetching Proximity criteria ends
		logger.info("############### Exiting getExistingProximitiesIds method ###############");
		return existingProximities;
	}

	public String getExistingLocationsToInclude(Long campaignApiId) throws ApiException, RemoteException {
		logger.info("*************** Entering getExistingLocationsToInclude method ***************");
		CampaignCriterionServiceInterface campaignCriterionService =
				adWordsServices.get(session, CampaignCriterionServiceInterface.class);
		String existingLocationsToInclude="";
		// Create selector.
		Selector selector = new Selector();
		selector.setFields(new String[] {"CampaignId", "Id", "CriteriaType"});


		selector.setPredicates(new Predicate[] {new Predicate("CriteriaType",
				PredicateOperator.EQUALS, new String[] {"LOCATION"}),new Predicate("CampaignId",
						PredicateOperator.EQUALS, new String[] {campaignApiId+""}),new Predicate("IsNegative",
								PredicateOperator.EQUALS, new String[] {"False"})});


		//selector.setPaging(new Paging(offset, PAGE_SIZE));
		CampaignCriterionPage page = null;
		logger.info("\n=============== SEND:: campaignApiId: "+campaignApiId+" ===============\n");
		page = campaignCriterionService.get(selector);
		if (page.getEntries() != null) {
			// Display campaigns.
			for (CampaignCriterion campaignCriterion : page.getEntries()) {
				logger.info("\n=============== RECEIVED:: campaignCriterion with details: ===============\n"
						+ "campaignId: "+campaignCriterion.getCampaignId()+"\n"
						+ "criterionId: "+campaignCriterion.getCriterion().getId()+"\n"
						+ "type: "+campaignCriterion.getCriterion().getCriterionType()+"\n"
						+ "=============================================");
				logger.info("\n+++++++++++++++ SUCCESS:: campaignCriterion with details: +++++++++++++++\n"
						+ "campaignId: "+campaignCriterion.getCampaignId()+"\n"
						+ "criterionId: "+campaignCriterion.getCriterion().getId()+"\n"
						+ "type: "+campaignCriterion.getCriterion().getCriterionType()+" was found\n"
						+ "+++++++++++++++++++++++++++++++++++++++++++++");
				existingLocationsToInclude+=campaignCriterion.getCriterion().getId()+",";
			}
		} else {
			logger.info("\n??????????????? ERROR:: No campaign Location criteria with given campaign Id were found.???????????????\n");
		}
		logger.info("############### Exiting getExistingLocationsToInclude method ###############");
		return existingLocationsToInclude;
	}


	public String getExistingLocationsToExclude(Long campaignApiId) throws ApiException, RemoteException {
		logger.info("*************** Entering getExistingLocationsToExclude method ***************");
		CampaignCriterionServiceInterface campaignCriterionService =
				adWordsServices.get(session, CampaignCriterionServiceInterface.class);
		String existingLocationsToExclude="";
		// Create selector.
		Selector selector = new Selector();
		selector.setFields(new String[] {"CampaignId", "Id", "CriteriaType"});


		selector.setPredicates(new Predicate[] {new Predicate("CriteriaType",
				PredicateOperator.EQUALS, new String[] {"LOCATION"}),new Predicate("CampaignId",
						PredicateOperator.EQUALS, new String[] {campaignApiId+""}),new Predicate("IsNegative",
								PredicateOperator.EQUALS, new String[] {"True"})});


		//selector.setPaging(new Paging(offset, PAGE_SIZE));
		CampaignCriterionPage page = null;
		logger.info("\n=============== SEND:: campaignApiId: "+campaignApiId+" ===============\n");
		page = campaignCriterionService.get(selector);

		if (page.getEntries() != null) {
			// Display campaigns.
			for (CampaignCriterion campaignCriterion : page.getEntries()) {
				logger.info("\n=============== RECEIVED:: campaignCriterion with details: ===============\n"
						+ "campaignId: "+campaignCriterion.getCampaignId()+"\n"
						+ "criterionId: "+campaignCriterion.getCriterion().getId()+"\n"
						+ "type: "+campaignCriterion.getCriterion().getCriterionType()+"\n"
						+ "=============================================");
				logger.info("\n+++++++++++++++ SUCCESS:: campaignCriterion with details: +++++++++++++++\n"
						+ "campaignId: "+campaignCriterion.getCampaignId()+"\n"
						+ "criterionId: "+campaignCriterion.getCriterion().getId()+"\n"
						+ "type: "+campaignCriterion.getCriterion().getCriterionType()+" was found\n"
						+ "+++++++++++++++++++++++++++++++++++++++++++++");
				existingLocationsToExclude+=campaignCriterion.getCriterion().getId()+",";
			}
		} else {
			logger.info("\n??????????????? ERROR:: No campaign Location criteria with given campaign Id were found.???????????????\n");
		}
		logger.info("############### Exiting getExistingLocationsToExclude method ###############");
		return existingLocationsToExclude;

	}


	public void updateBasicCampaignDetails(CampaignDo campaignDo) throws NumberFormatException, IOException, ReportException, ReportDownloadResponseException, ParseException
	{
		logger.info("*************** Entering updateBasicCampaignDetails method ***************");
		String query="SELECT CampaignName,BudgetId,Amount,CampaignStatus, StartDate, EndDate FROM CAMPAIGN_PERFORMANCE_REPORT "
				+ "WHERE CampaignId = "+campaignDo.getApiId();

		ReportingConfiguration reportingConfiguration =
				new ReportingConfiguration.Builder()
				// Skip all header and summary lines since the loop below expects
				// every field to be present in each line.
				.skipReportHeader(true)
				.skipColumnHeader(true)
				.skipReportSummary(true)
				// Enable to include rows with zero impressions.
				.includeZeroImpressions(true)
				.build();
		session.setReportingConfiguration(reportingConfiguration);

		BufferedReader reader = null;
		try {
			// Set the property api.adwords.reportDownloadTimeout or call
			// ReportDownloader.setReportDownloadTimeout to set a timeout (in milliseconds)
			// for CONNECT and READ in report downloads.
			final ReportDownloadResponse response =
					new ReportDownloader(session).downloadReport(query, DownloadFormat.CSV);

			// Read the response as a BufferedReader.
			reader = new BufferedReader(new InputStreamReader(response.getInputStream(), Charsets.UTF_8));

			String line;
			Splitter splitter = Splitter.on(',');
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			int count=0;
			while ((line = reader.readLine()) != null) {
				count++;
				logger.info(line);
				List<String> values = splitter.splitToList(line);
				campaignDo.setName(values.get(0));
				campaignDo.setBudgetId(Long.parseLong(values.get(1)));
				campaignDo.setBudgetAmount(Double.parseDouble(values.get(2))/1000000);

				if(values.get(3).equals("enabled"))
					campaignDo.setStatus(CampaignDo.Status.Enabled.name());
				else if(values.get(3).equals("paused"))
					campaignDo.setStatus(CampaignDo.Status.Paused.name());
				else if(values.get(3).equals("removed"))
					campaignDo.setStatus(CampaignDo.Status.Removed.name());
				else
					campaignDo.setStatus(CampaignDo.Status.Unknown.name());
				campaignDo.setStartDate(dateFormat.parse(values.get(4)));
				campaignDo.setEndDate(dateFormat.parse(values.get(5)));
			}
			if(count==0)	//If no campaign with given campaign API Id exist
			{
				campaignDo.setApiId(null);
				campaignDo.setStatus(CampaignDo.Status.Unknown.name());
			}

		}finally{
			if (reader != null) {
				reader.close();
			}
		}
		logger.info("\n+++++++++++++++ SUCCESS:: campaign with details: +++++++++++++++\n"
				+ "id: "+campaignDo.getId()+"\n"
				+ "name: "+campaignDo.getName()+"\n"
				+ "budgetId: "+campaignDo.getBudgetId()+"\n"
				+ "budgetAmount: "+campaignDo.getBudgetAmount()+"\n"
				+ "status: "+campaignDo.getStatus()+"\n"
				+ "startDate: "+campaignDo.getStartDate()+"\n"
				+ "endDate: "+campaignDo.getEndDate()+"\n"
				+ "apiId: "+campaignDo.getApiId()+" was updated\n"
				+ "+++++++++++++++++++++++++++++++++++++++++++++");
		logger.info("############### Exiting updateBasicCampaignDetails method ###############");
	}
}
