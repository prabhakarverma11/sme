package com.dq.arq.sme.cron;

public interface CronJobs {
	
	public void syncYesterdaysCampaignData();
	
	public void pauseKeywords();

}
