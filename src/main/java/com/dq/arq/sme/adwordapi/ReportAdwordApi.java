package com.dq.arq.sme.adwordapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dq.arq.sme.domain.AdgroupDo;
import com.dq.arq.sme.domain.CampaignDo;
import com.dq.arq.sme.domain.CampaignPerformanceReportDo;
import com.dq.arq.sme.domain.KeywordDo;
import com.dq.arq.sme.domain.ReportCriteriaDo;
import com.dq.arq.sme.services.CampaignService;
import com.google.api.ads.adwords.lib.client.AdWordsSession;
import com.google.api.ads.adwords.lib.client.reporting.ReportingConfiguration;
import com.google.api.ads.adwords.lib.jaxb.v201607.DownloadFormat;
import com.google.api.ads.adwords.lib.jaxb.v201607.Predicate;
import com.google.api.ads.adwords.lib.jaxb.v201607.PredicateOperator;
import com.google.api.ads.adwords.lib.jaxb.v201607.ReportDefinition;
import com.google.api.ads.adwords.lib.jaxb.v201607.ReportDefinitionDateRangeType;
import com.google.api.ads.adwords.lib.jaxb.v201607.ReportDefinitionReportType;
import com.google.api.ads.adwords.lib.jaxb.v201607.Selector;
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
import com.google.common.collect.Lists;

public class ReportAdwordApi {

	Logger logger = LoggerFactory.getLogger("AdwordsLog");

	Credential oAuth2Credential;
	AdWordsSession session;

	public ReportAdwordApi(){
		logger.info("*************** Entering ReportAdwordApi constructor ***************");
		// Generate a refreshable OAuth2 credential.
		try {
			oAuth2Credential = new OfflineCredentials.Builder()
			.forApi(Api.ADWORDS)
			.fromFile()
			.build()
			.generateCredential();
		} catch (OAuthException | ValidationException
				| ConfigurationLoadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Construct an AdWordsSession.
		try {
			session = new AdWordsSession.Builder()
			.fromFile()
			.withOAuth2Credential(oAuth2Credential)
			.build();
		} catch (ValidationException | ConfigurationLoadException e) {
			logger.info("??????????????? ERROR:: Caught exception in ReportAdwordApi constructor: "+e.getMessage()+" ???????????????");
			e.printStackTrace();
		}
		logger.info("############### Exiting ReportAdwordApi constructor ###############");
	}


	public void getCampaignPerformanceReport(List<CampaignPerformanceReportDo> campaignPerformanceReportDos,ReportCriteriaDo reportCriteriaDo,CampaignService campaignService) throws ReportException, ReportDownloadResponseException, IOException, ParseException
	{
		logger.info("*************** Entering getCampaignPerformanceReport method ***************");
		// Create the query.
		String query="";
		if(reportCriteriaDo.getCriteria().equals("daywise"))
		{

			if(reportCriteriaDo.getDateRangeType().equals("CUSTOM_DATE"))
			{
				query="SELECT CampaignId, "
						+ "Impressions, Clicks, Cost,  Date FROM CAMPAIGN_PERFORMANCE_REPORT "
						+ "WHERE CampaignStatus IN [ENABLED, PAUSED] AND Date >= StartDate AND Date <= EndDate "
						+ "DURING "+reportCriteriaDo.getStartDate()+","+reportCriteriaDo.getEndDate();
			}
			else
			{
				query="SELECT CampaignId, "
						+ "Impressions, Clicks, Cost,  Date FROM CAMPAIGN_PERFORMANCE_REPORT "
						+ "WHERE CampaignStatus IN [ENABLED, PAUSED] AND Date >= StartDate AND Date <= EndDate "
						+ "DURING "+reportCriteriaDo.getDateRangeType();
			}
		}
		else
		{

			if(reportCriteriaDo.getDateRangeType().equals("CUSTOM_DATE"))
			{
				query = "SELECT CampaignId,"
						+ "Impressions, Clicks, Cost FROM CAMPAIGN_PERFORMANCE_REPORT "
						+ "WHERE CampaignStatus IN [ENABLED, PAUSED] "
						+ "DURING "+reportCriteriaDo.getStartDate()+","+reportCriteriaDo.getEndDate();

			}
			else
			{
				query = "SELECT CampaignId,"
						+ "Impressions, Clicks, Cost FROM CAMPAIGN_PERFORMANCE_REPORT "
						+ "WHERE CampaignStatus IN [ENABLED, PAUSED] "
						+ "DURING "+reportCriteriaDo.getDateRangeType();
			}

		}

		// Optional: Set the reporting configuration of the session to suppress header, column name, or
		// summary rows in the report output. You can also configure this via your ads.properties
		// configuration file. See AdWordsSession.Builder.from(Configuration) for details.
		// In addition, you can set whether you want to explicitly include or exclude zero impression
		// rows.
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
			logger.info("\n=============== SEND:: reportCriteriaDo with details: ===============\n"
					+ "criteria: "+reportCriteriaDo.getCriteria()+"\n"
					+ "dateRangeType: "+reportCriteriaDo.getDateRangeType()+"\n"
					+ "startDate: "+reportCriteriaDo.getStartDate()+"\n"
					+ "endDate: "+reportCriteriaDo.getEndDate()+"\n"
					+ "query: "+query+"\n"
					+ "=============================================");
			final ReportDownloadResponse response =
					new ReportDownloader(session).downloadReport(query, DownloadFormat.CSV);

			// Read the response as a BufferedReader.
			reader = new BufferedReader(new InputStreamReader(response.getInputStream(), Charsets.UTF_8));

			String line;
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Splitter splitter = Splitter.on(',');
			while ((line = reader.readLine()) != null) {
				CampaignPerformanceReportDo campaignPerformanceReportDo = new CampaignPerformanceReportDo();
				List<String> values = splitter.splitToList(line);
				
				CampaignDo campaignDo = campaignService.getCampaignDoByApiId(Long.parseLong(values.get(0)));
				
				campaignPerformanceReportDo.setCampaignDo(campaignDo);
				
				campaignPerformanceReportDo.setImpressions(Long.parseLong(values.get(1)));
				campaignPerformanceReportDo.setClicks(Long.parseLong(values.get(2)));
				campaignPerformanceReportDo.setCost(Double.parseDouble(values.get(3)));
				if(reportCriteriaDo.getCriteria().equals("daywise"))
					campaignPerformanceReportDo.setDate(dateFormat.parse(values.get(4)));


				campaignPerformanceReportDos.add(campaignPerformanceReportDo);
				logger.info("\n=============== RECEIVED:: CampaignPerformanceReportDo with details: ===============\n"
						+ "campaignId: "+campaignPerformanceReportDo.getCampaignDo().getId()+"\n"
						+ "impressions: "+campaignPerformanceReportDo.getImpressions()+"\n"
						+ "clicks: "+campaignPerformanceReportDo.getClicks()+"\n"
						+ "cost: "+campaignPerformanceReportDo.getCost()+"\n"
						+ "date: "+campaignPerformanceReportDo.getDate()+"\n"
						+ "=============================================");
			}

		}finally{
			if (reader != null) {
				reader.close();
			}
		}
		logger.info("############### Exiting getCampaignPerformanceReport method ###############");
	}



	public void syncYesterdayCampaignPerformanceReport(List<CampaignPerformanceReportDo> campaignPerformanceReportDos,List<CampaignDo> campaignDosForSyncingData,CampaignService campaignService) throws ReportException, ReportDownloadResponseException, IOException, ParseException
	{
		logger.info("*************** Entering syncYesterdayCampaignPerformanceReport method ***************");
		String campaignList="";
		if(campaignDosForSyncingData.size()>0)
		{
			for(CampaignDo campaignDo: campaignDosForSyncingData)
			{
				if(campaignDo.getApiId()!=null)
					campaignList+="'"+campaignDo.getApiId()+"',";
			}
		}
		campaignList = campaignList.substring(0,campaignList.length()-1);

		// Create the query.
		String query="SELECT CampaignId, "
				+ "Impressions, Clicks, Cost, Date FROM CAMPAIGN_PERFORMANCE_REPORT WHERE CampaignId IN ["+campaignList
				+ "] DURING YESTERDAY";

		logger.info("\n=============== SEND:: Query to retrieve campaign performance report data from adwords: "+query+" ===============\n");

		// Optional: Set the reporting configuration of the session to suppress header, column name, or
		// summary rows in the report output. You can also configure this via your ads.properties
		// configuration file. See AdWordsSession.Builder.from(Configuration) for details.
		// In addition, you can set whether you want to explicitly include or exclude zero impression
		// rows.
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
			while ((line = reader.readLine()) != null) {
				CampaignPerformanceReportDo campaignPerformanceReportDo = new CampaignPerformanceReportDo();
				List<String> values = splitter.splitToList(line);
				
				CampaignDo campaignDo = campaignService.getCampaignDoByApiId(Long.parseLong(values.get(0)));
				
				campaignPerformanceReportDo.setCampaignDo(campaignDo);
				campaignPerformanceReportDo.setImpressions(Long.parseLong(values.get(1)));
				campaignPerformanceReportDo.setClicks(Long.parseLong(values.get(2)));
				campaignPerformanceReportDo.setCost(Double.parseDouble(values.get(3)));
				campaignPerformanceReportDo.setDate(dateFormat.parse(values.get(4)));
				campaignPerformanceReportDos.add(campaignPerformanceReportDo);
				logger.info("\n=============== RECEIVED:: CampaignPerformanceReportDo with details: ===============\n"
						+ "campaignId: "+campaignPerformanceReportDo.getCampaignDo().getId()+"\n"
						+ "impressions: "+campaignPerformanceReportDo.getImpressions()+"\n"
						+ "clicks: "+campaignPerformanceReportDo.getClicks()+"\n"
						+ "cost: "+campaignPerformanceReportDo.getCost()+"\n"
						+ "date: "+campaignPerformanceReportDo.getDate()+"\n"
						+ "=============================================");
			}


		}finally{
			if (reader != null) {
				reader.close();
			}
		}
		logger.info("############### Exiting syncYesterdayCampaignPerformanceReport method ###############");
	}


	public void sync30DaysCPReport(List<CampaignPerformanceReportDo> campaignPerformanceReportDos,List<CampaignDo> campaignDosForSyncingData,CampaignService campaignService) throws ReportException, ReportDownloadResponseException, IOException, ParseException
	{
		logger.info("*************** Entering sync30DaysCPReport method ***************");
		String campaignList="";
		if(campaignDosForSyncingData.size()>0)
		{
			for(CampaignDo campaignDo: campaignDosForSyncingData)
			{
				if(campaignDo.getApiId()!=null)
					campaignList+="'"+campaignDo.getApiId()+"',";

			}
		}
		campaignList = campaignList.substring(0,campaignList.length()-1);

		// Create the query.
		String query="SELECT CampaignId, "
				+ "Impressions, Clicks, Cost, Date FROM CAMPAIGN_PERFORMANCE_REPORT WHERE CampaignId IN ["+campaignList
				+ "] DURING LAST_30_DAYS";

		logger.info("\n=============== SEND:: Query to retrieve campaign performance report data from adwords: "+query+" ===============\n");
		// Optional: Set the reporting configuration of the session to suppress header, column name, or
		// summary rows in the report output. You can also configure this via your ads.properties
		// configuration file. See AdWordsSession.Builder.from(Configuration) for details.
		// In addition, you can set whether you want to explicitly include or exclude zero impression
		// rows.
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
			ReportDownloadResponse response =
					new ReportDownloader(session).downloadReport(query, DownloadFormat.CSV);

			// Read the response as a BufferedReader.
			reader = new BufferedReader(new InputStreamReader(response.getInputStream(), Charsets.UTF_8));

			String line;
			Splitter splitter = Splitter.on(',');
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			while ((line = reader.readLine()) != null) {

				CampaignPerformanceReportDo campaignPerformanceReportDo = new CampaignPerformanceReportDo();
				List<String> values = splitter.splitToList(line);
				CampaignDo campaignDo = campaignService.getCampaignDoByApiId(Long.parseLong(values.get(0)));
				if(dateFormat.parse(values.get(4)).compareTo(campaignDo.getStartDate())>=0)
				{
					campaignPerformanceReportDo.setCampaignDo(campaignDo);
					campaignPerformanceReportDo.setImpressions(Long.parseLong(values.get(1)));
					campaignPerformanceReportDo.setClicks(Long.parseLong(values.get(2)));
					campaignPerformanceReportDo.setCost(Double.parseDouble(values.get(3)));
					campaignPerformanceReportDo.setDate(dateFormat.parse(values.get(4)));
					campaignPerformanceReportDos.add(campaignPerformanceReportDo);
					logger.info("\n=============== RECEIVED:: CampaignPerformanceReportDo with details: ===============\n"
							+ "campaignId: "+campaignPerformanceReportDo.getCampaignDo().getId()+"\n"
							+ "impressions: "+campaignPerformanceReportDo.getImpressions()+"\n"
							+ "clicks: "+campaignPerformanceReportDo.getClicks()+"\n"
							+ "cost: "+campaignPerformanceReportDo.getCost()+"\n"
							+ "date: "+campaignPerformanceReportDo.getDate()+"\n"
							+ "=============================================");
				}
			}

		}finally{
			if (reader != null) {
				reader.close();
			}
		}

		logger.info("############### Exiting sync30DaysCPReport method ###############");
	}

	public void syncTodayCPReport(List<CampaignPerformanceReportDo> campaignPerformanceReportDos,List<CampaignDo> campaignDosForSyncingData,CampaignService campaignService) throws ReportException, ReportDownloadResponseException, IOException, ParseException
	{
		logger.info("*************** Entering syncTodayCPReport method ***************");
		String campaignList="";
		if(campaignDosForSyncingData.size()>0)
		{
			for(CampaignDo campaignDo: campaignDosForSyncingData)
			{
				if(campaignDo.getApiId()!=null)
					campaignList+="'"+campaignDo.getApiId()+"',";

			}
		}
		campaignList = campaignList.substring(0,campaignList.length()-1);

		// Create the query.
		String query="SELECT CampaignId, "
				+ "Impressions, Clicks, Cost, Date FROM CAMPAIGN_PERFORMANCE_REPORT WHERE CampaignId IN ["+campaignList
				+ "] DURING TODAY";

		logger.info("\n=============== SEND:: Query to retrieve campaign performance report data from adwords: "+query+" ===============\n");
		// Optional: Set the reporting configuration of the session to suppress header, column name, or
		// summary rows in the report output. You can also configure this via your ads.properties
		// configuration file. See AdWordsSession.Builder.from(Configuration) for details.
		// In addition, you can set whether you want to explicitly include or exclude zero impression
		// rows.
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
			ReportDownloadResponse response =
					new ReportDownloader(session).downloadReport(query, DownloadFormat.CSV);

			// Read the response as a BufferedReader.
			reader = new BufferedReader(new InputStreamReader(response.getInputStream(), Charsets.UTF_8));

			String line;
			Splitter splitter = Splitter.on(',');
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			while ((line = reader.readLine()) != null) {

				CampaignPerformanceReportDo campaignPerformanceReportDo = new CampaignPerformanceReportDo();
				List<String> values = splitter.splitToList(line);
				
				CampaignDo campaignDo = campaignService.getCampaignDoByApiId(Long.parseLong(values.get(0)));
				
					campaignPerformanceReportDo.setCampaignDo(campaignDo);
				
					campaignPerformanceReportDo.setImpressions(Long.parseLong(values.get(1)));
					campaignPerformanceReportDo.setClicks(Long.parseLong(values.get(2)));
					campaignPerformanceReportDo.setCost(Double.parseDouble(values.get(3)));
					campaignPerformanceReportDo.setDate(dateFormat.parse(values.get(4)));
					campaignPerformanceReportDos.add(campaignPerformanceReportDo);
					logger.info("\n=============== RECEIVED:: CampaignPerformanceReportDo with details: ===============\n"
							+ "campaignId: "+campaignPerformanceReportDo.getCampaignDo().getId()+"\n"
							+ "impressions: "+campaignPerformanceReportDo.getImpressions()+"\n"
							+ "clicks: "+campaignPerformanceReportDo.getClicks()+"\n"
							+ "cost: "+campaignPerformanceReportDo.getCost()+"\n"
							+ "date: "+campaignPerformanceReportDo.getDate()+"\n"
							+ "=============================================");
				}

		}finally{
			if (reader != null) {
				reader.close();
			}
		}

		logger.info("############### Exiting syncTodayCPReport method ###############");
	} 

	public List<CampaignPerformanceReportDo> refreshCampaignPerformanceReportData(List<CampaignDo> campaignDosForSyncingData,CampaignService campaignService) throws ReportException, ReportDownloadResponseException, NumberFormatException, IOException, ParseException
	{
		logger.info("*************** Entering refreshCampaignPerformanceReportData method ***************");
		List<CampaignPerformanceReportDo> campaignPerformanceReportDos = new ArrayList<CampaignPerformanceReportDo>();

		//String campaignList="";
		String campaignListForLogger="";
		List<String> campaignList = new ArrayList<String>();
		if(campaignDosForSyncingData.size()>0)
		{
			for(CampaignDo campaignDo: campaignDosForSyncingData)
			{
				if(campaignDo.getApiId()!=null) {
					//campaignList+="'"+campaignDo.getApiId()+"',";
					campaignListForLogger+="'"+campaignDo.getApiId()+"',";
					campaignList.add(campaignDo.getApiId().toString());
				}

			}
		}
		//campaignList = campaignList.substring(0,campaignList.length()-1);

		// Create selector.
		Selector selector = new Selector();
		selector.getFields().addAll(Lists.newArrayList(

				"CampaignId",
				"Impressions","Clicks", "Cost", "Date"
				));
		Predicate p=new Predicate();
		p.setField("CampaignId");
		p.setOperator(PredicateOperator.IN);
		p.getValues().addAll(campaignList);
		selector.getPredicates().add(p);
		logger.info("\n=============== SEND:: campaignList to refresh campaign performance reprot data: "+campaignListForLogger+" ===============\n");
		// Create report definition.
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setReportName("Campaign performance report #" + System.currentTimeMillis());
		reportDefinition.setDateRangeType(ReportDefinitionDateRangeType.ALL_TIME);
		reportDefinition.setReportType(ReportDefinitionReportType.CAMPAIGN_PERFORMANCE_REPORT);
		reportDefinition.setDownloadFormat(DownloadFormat.CSV);


		// Optional: Set the reporting configuration of the session to suppress header, column name, or
		// summary rows in the report output. You can also configure this via your ads.properties
		// configuration file. See AdWordsSession.Builder.from(Configuration) for details.
		// In addition, you can set whether you want to explicitly include or exclude zero impression
		// rows.
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

		reportDefinition.setSelector(selector);

		BufferedReader reader = null;
		try {
			// Set the property api.adwords.reportDownloadTimeout or call
			// ReportDownloader.setReportDownloadTimeout to set a timeout (in milliseconds)
			// for CONNECT and READ in report downloads.
			final ReportDownloadResponse response =
					new ReportDownloader(session).downloadReport(reportDefinition);

			// Read the response as a BufferedReader.
			reader = new BufferedReader(new InputStreamReader(response.getInputStream(), Charsets.UTF_8));

			String line;
			Splitter splitter = Splitter.on(',');
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			while ((line = reader.readLine()) != null) {
				CampaignPerformanceReportDo campaignPerformanceReportDo = new CampaignPerformanceReportDo();
				List<String> values = splitter.splitToList(line);
				
				CampaignDo campaignDo = campaignService.getCampaignDoByApiId(Long.parseLong(values.get(0)));
				campaignPerformanceReportDo.setCampaignDo(campaignDo);
				
				campaignPerformanceReportDo.setImpressions(Long.parseLong(values.get(1)));
				campaignPerformanceReportDo.setClicks(Long.parseLong(values.get(2)));
				campaignPerformanceReportDo.setCost(Double.parseDouble(values.get(3)));
				campaignPerformanceReportDo.setDate(dateFormat.parse(values.get(4)));
				campaignPerformanceReportDos.add(campaignPerformanceReportDo);
				logger.info("\n=============== RECEIVED:: CampaignPerformanceReportDo with details: ===============\n"
						+ "campaignId: "+campaignPerformanceReportDo.getCampaignDo().getId()+"\n"
						+ "impressions: "+campaignPerformanceReportDo.getImpressions()+"\n"
						+ "clicks: "+campaignPerformanceReportDo.getClicks()+"\n"
						+ "cost: "+campaignPerformanceReportDo.getCost()+"\n"
						+ "date: "+campaignPerformanceReportDo.getDate()+"\n"
						+ "=============================================");
			}


		}finally{
			if (reader != null) {
				reader.close();
			}
		}
		logger.info("############### Exiting refreshCampaignPerformanceReportData method ###############");
		return campaignPerformanceReportDos;
	}

	public void updateKeywordsWithFirstPageCpc(AdgroupDo adgroupDo,String keywordApiIds,List<KeywordDo> existingKeywordDos) throws ReportException, ReportDownloadResponseException, IOException
	{
		logger.info("*************** Entering updateKeywordsWithFirstPageCpc method ***************");
		

		// Create the query.
		String query="SELECT Id,AverageCpc,TopOfPageCpc,FirstPageCpc FROM KEYWORDS_PERFORMANCE_REPORT WHERE "
				+ "AdGroupId = "+adgroupDo.getApiId()+" AND Id IN ["+keywordApiIds+"]";

		logger.debug("\n=============== SEND:: Query to retrieve keywords with their AverageCpc,TopOfPageCpc and FirstPageCpc from adwords: "+query+" ===============\n");
		// Optional: Set the reporting configuration of the session to suppress header, column name, or
		// summary rows in the report output. You can also configure this via your ads.properties
		// configuration file. See AdWordsSession.Builder.from(Configuration) for details.
		// In addition, you can set whether you want to explicitly include or exclude zero impression
		// rows.
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
			ReportDownloadResponse response =
					new ReportDownloader(session).downloadReport(query, DownloadFormat.CSV);

			// Read the response as a BufferedReader.
			reader = new BufferedReader(new InputStreamReader(response.getInputStream(), Charsets.UTF_8));

			String line;
			Splitter splitter = Splitter.on(',');

			while ((line = reader.readLine()) != null) {

				List<String> values = splitter.splitToList(line);
				
				for(KeywordDo keywordDo:existingKeywordDos)
				{
					logger.info("\n=============== RECEIVED:: Keyword with details: ===============\n"
							+ "KeywordAPIId: "+values.get(0)+"\n"
							+ "AdgroupApiId: "+adgroupDo.getApiId()+"\n"
							+ "avgCPC: "+Double.parseDouble(values.get(1))/1000000+"\n"
							+ "TopOfPageCpc: "+Double.parseDouble(values.get(2))/1000000+"\n"
							+ "FirstPageCpc: "+Double.parseDouble(values.get(3))/1000000+"\n"
							+ "=============================================");
					
					if(keywordDo.getApiId().toString().equals(values.get(0)))
					{
						if(Double.parseDouble(values.get(3))/1000000 > 0)
						{
							keywordDo.setBid(Double.parseDouble(values.get(3))/1000000);
							logger.info("Bid of Keyword with text: "+keywordDo.getText()+" set to its FirstPageCpc: "+Double.parseDouble(values.get(3))/1000000);
						}
						
						
					}
				}
				

			}

		}finally{
			if (reader != null) {
				reader.close();
			}
		}

		logger.info("############### Exiting updateKeywordsWithFirstPageCpc method ###############");

	}





}


