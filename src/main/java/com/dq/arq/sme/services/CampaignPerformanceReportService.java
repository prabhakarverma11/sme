package com.dq.arq.sme.services;

import java.util.Date;
import java.util.List;

import com.dq.arq.sme.domain.CampaignPerformanceReportDo;

public interface CampaignPerformanceReportService {

	Integer saveCampaignPerformanceReportDo(CampaignPerformanceReportDo campaignPerformanceReportDo);
	
	boolean isYesterdaysRecordSynced();
	
	List<CampaignPerformanceReportDo> getCampaignPerformanceReportDosList();
	
	List<CampaignPerformanceReportDo> getCampaignPerformanceReportDosBetweenDate(Date startDate,Date endDate);
	
	void saveCampaignPerformanceReportDos(List<CampaignPerformanceReportDo> campaignPerformanceReportDos);

	CampaignPerformanceReportDo getCampaignPerformanceReportDoById(Integer id);
	
//	CampaignPerformanceReportDo getCampaignPerformanceReportDoByName(String name);
	
	void updateCampaign(CampaignPerformanceReportDo campaignPerformanceReportDo);
	
	List<CampaignPerformanceReportDo> getCampaignPerformanceReportDosListByDateAndPage(String startDate,String endDate,String criteria,String[] status,String campaignsToFilter,int page,int rows);

	List<CampaignPerformanceReportDo> getCampaignPerformanceReportDosListByDate(String startDate,String endDate,String criteria,String[] status, String campaignsToFilterList);
	
	long countCampaignPerformanceReportDos(String startDate,String endDate,String criteria,String[] status, String campaignsToFilter);
	
	void deleteCampaignPerformanceReportDo(CampaignPerformanceReportDo campaignPerformanceReportDo);
	
	void deleteAllData();
	
	void delete30DaysData();
	
	void deleteTodayData();

	Long getTotalImpressionsByDate(String startDate, String endDate,
			String[] status, String campaignsToFilter);

	Long getTotalClicksByDate(String startDate, String endDate,
			String[] status, String campaignsToFilter);

	
	Double getTotalCostByDate(String startDate, String endDate,
			String[] status, String campaignsToFilter);

	Double getAvgCPCByDate(String startDate, String endDate, 
			String[] status, String campaignsToFilter);

	Double getAvgCTRByDate(String startDate, String endDate, 
			String[] status, String campaignsToFilter);

	
	List<CampaignPerformanceReportDo> getCampaignPerformanceReportDosListByDateAndPageAndSortedByColumn(
			String startDateForQuery, String endDateForQuery, String criteria, String[] status,
			String campaignsToFilter, Integer page, Integer rowsPerPage, String columnName, Integer orderBy);
	
}
