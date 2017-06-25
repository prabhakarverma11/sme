package com.dq.arq.sme.cron;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import com.dq.arq.sme.adwordapi.KeywordAdwordApi;
import com.dq.arq.sme.adwordapi.ReportAdwordApi;
import com.dq.arq.sme.domain.AdgroupDo;
import com.dq.arq.sme.domain.CampaignDo;
import com.dq.arq.sme.domain.CampaignPerformanceReportDo;
import com.dq.arq.sme.domain.KeywordDo;
import com.dq.arq.sme.domain.ProductCategoryDo;
import com.dq.arq.sme.domain.UserDo;
import com.dq.arq.sme.services.AdgroupService;
import com.dq.arq.sme.services.CampaignPerformanceReportService;
import com.dq.arq.sme.services.CampaignService;
import com.dq.arq.sme.services.KeywordService;
import com.dq.arq.sme.services.ProductCategoryService;
import com.google.api.ads.adwords.lib.utils.ReportDownloadResponseException;
import com.google.api.ads.adwords.lib.utils.ReportException;

public class CronJobsImpl implements CronJobs{


	final static Logger logger = LoggerFactory.getLogger(CronJobsImpl.class);

	@Autowired
	CampaignService campaignService;

	@Autowired
	CampaignPerformanceReportService campaignPerformanceReportService;

	@Autowired
	TaskExecutor taskExecutor;

	@Autowired
	AdgroupService adgroupService;

	@Autowired
	KeywordService keywordService;
	
	@Autowired
	ProductCategoryService productCategoryService;

	@Override
	public void syncYesterdaysCampaignData()
	{
		logger.info("\n\n\n*************** Entering syncCampaignPerformanceReportData method of CronJobs class ***************\n\n\n");
		syncYesterdaysCampaignDataExecutor();
		logger.info("\n\n\n############### Exiting syncCampaignPerformanceReportData method of CronJobs class ###############\n\n\n");
	}

	@Transactional
	@Async
	public void syncYesterdaysCampaignDataExecutor()
	{
		logger.info("\n\n\n*************** Entering syncYesterdaysData method of FetchAdwordReport cron ***************\n\n\n");
		if (this.taskExecutor != null) {
			this.taskExecutor.execute(new Runnable() {
				public void run() {
					logger.debug("\n\n\n*************** Entering run method within syncYesterdaysData method of FetchAdwordReport cron ***************\n\n\n");
					if(!campaignPerformanceReportService.isYesterdaysRecordSynced())
					{
						List<CampaignPerformanceReportDo> campaignPerformanceReportDos = new ArrayList<CampaignPerformanceReportDo>();
						try {
							new ReportAdwordApi().syncYesterdayCampaignPerformanceReport(campaignPerformanceReportDos,campaignService.getCampaignDosListForAdmin(),campaignService);
							campaignPerformanceReportService.saveCampaignPerformanceReportDos(campaignPerformanceReportDos);
							logger.info("\n\n\n+++++++++++++++ SUCCESS:: "+campaignPerformanceReportDos.size()+" entries added to campaignPerformanceReportDo table.  +++++++++++++++\n\n\n");
						} catch (ReportException | ReportDownloadResponseException
								| IOException | ParseException e) {
							logger.info("\n\n\n??????????????? ERROR:: Could not fetch campaignPerformanceReportDos from Google Adword , errorMessage:"+e.getMessage()+"???????????????\n\n\n");
							e.printStackTrace();
							return;
						}
					}
					else
					{
						logger.info("\n\n\n+++++++++++++++ SUCCESS:: Yesterday's records are already in the database+++++++++++++++\n\n\n");
					}
					logger.info("\n\n\n############### Exiting run method within syncYesterdaysData method of FetchAdwordReport cron ###############\n\n\n");
				}
			});
		}
		logger.info("\n\n\n############### Exiting syncYesterdaysData method of FetchAdwordReport cron ###############\n\n\n");
	}

	@Override
	public void pauseKeywords()
	{
		logger.info("\n\n\n*************** Entering pauseKeywords method of CronJobs class ***************\n\n\n");
		pauseKeywordsExecutor();
		logger.info("\n\n\n############### Exiting pauseKeywords method of CronJobs class ###############\n\n\n");
	}



	@Transactional
	@Async
	public void pauseKeywordsExecutor()
	{
		logger.info("\n\n\n*************** Entering pauseKeywordsExecutor method of CronJobs class ***************\n\n\n");
		if (this.taskExecutor != null) {
			this.taskExecutor.execute(new Runnable() {
				public void run() {
					logger.info("\n\n\n*************** Entering run method within pauseKeywordsExecutor method of CronJobs class ***************\n\n\n");
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
												keywordDo.setStatus(KeywordDo.Status.Paused.name());
												keywordDosToPause.add(keywordDo);
												count++;
											}
										}
										new KeywordAdwordApi().pauseKeywords(keywordDosToPause);
										
										keywordService.updateKeywordDos(keywordDosToPause);
										
										
										
										//Code added to add extra keywords because some keywords were paused
										UserDo userDo = new UserDo();
										userDo.setName("CronJob");
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
			});
		}
		logger.info("\n\n\n############### Exiting pauseKeywordsExecutor method of CronJobs class ###############\n\n\n");
	}

}