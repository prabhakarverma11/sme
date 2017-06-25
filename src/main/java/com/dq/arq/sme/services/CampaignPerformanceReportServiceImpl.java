package com.dq.arq.sme.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dq.arq.sme.dao.CampaignPerformanceReportDao;
import com.dq.arq.sme.domain.CampaignPerformanceReportDo;

@Service
@Transactional
public class CampaignPerformanceReportServiceImpl implements CampaignPerformanceReportService{

	@Autowired
	CampaignPerformanceReportDao campaignPerformanceReportDao;
	
	@Override
	public Integer saveCampaignPerformanceReportDo(CampaignPerformanceReportDo campaignPerformanceReportDo) {
		
		Integer id = campaignPerformanceReportDao.saveCampaignPerformanceReportDo(campaignPerformanceReportDo);
		return id;
	}
	
	
	@Override
	public void saveCampaignPerformanceReportDos(List<CampaignPerformanceReportDo> campaignPerformanceReportDos) {
		
		for(CampaignPerformanceReportDo campaignPerformanceReportDo : campaignPerformanceReportDos)
		{
		campaignPerformanceReportDao.saveCampaignPerformanceReportDo(campaignPerformanceReportDo);
		}
	}
	
	@Override
	public List<CampaignPerformanceReportDo> getCampaignPerformanceReportDosList()
	{
		return campaignPerformanceReportDao.getCampaignPerformanceReportDosList();
	}
	
	@Override
	public List<CampaignPerformanceReportDo> getCampaignPerformanceReportDosBetweenDate(Date startDate,Date endDate){
		
		return  campaignPerformanceReportDao.getCampaignPerformanceReportDosBetweenDate(startDate,endDate);
	}
	

	@Override
	public CampaignPerformanceReportDo getCampaignPerformanceReportDoById(Integer id) {
		
		return campaignPerformanceReportDao.getCampaignPerformanceReportDoById(id);
	}

	/*@Override
	public CampaignPerformanceReportDo getCampaignPerformanceReportDoByName(String name) {
		return campaignPerformanceReportDao.getCampaignPerformanceReportDoByName(name);
	}*/

	@Override
	public void updateCampaign(CampaignPerformanceReportDo campaignPerformanceReportDo) {
		campaignPerformanceReportDao.updateCampaign(campaignPerformanceReportDo);
	}


	@Override
	public List<CampaignPerformanceReportDo> getCampaignPerformanceReportDosListByDateAndPage(String startDate,String endDate,String criteria,String[] status,String campaignsToFilter,int page,int rows) {

		return campaignPerformanceReportDao.getCampaignPerformanceReportDosListByDateAndPage(startDate,endDate,criteria,status,campaignsToFilter,page,rows);
	}
	
	@Override
	public List<CampaignPerformanceReportDo> getCampaignPerformanceReportDosListByDate(String startDate,String endDate,String criteria,String[] status, String campaignsToFilter) {
		
		return campaignPerformanceReportDao.getCampaignPerformanceReportDosListByDate(startDate,endDate,criteria,status,campaignsToFilter);
	}
	
	public boolean isYesterdaysRecordSynced(){
	
		return campaignPerformanceReportDao.isYesterdaysRecordSynced();
	}


	@Override
	public long countCampaignPerformanceReportDos(String startDate,String endDate,String criteria,String[] status, String campaignsToFilter) {
		return campaignPerformanceReportDao.countCampaignPerformanceReportDos(startDate,endDate,criteria,status,campaignsToFilter);
	}
	
	@Override
	public void deleteCampaignPerformanceReportDo(CampaignPerformanceReportDo campaignPerformanceReportDo)
	{
		campaignPerformanceReportDao.deleteCampaignPerformanceReportDo(campaignPerformanceReportDo);
	}
	
	@Override
	public void deleteAllData()
	{
		List<CampaignPerformanceReportDo> campaignPerformanceReportDos = getCampaignPerformanceReportDosList();
		
		for(CampaignPerformanceReportDo campaignPerformanceReportDo:campaignPerformanceReportDos)
		{
		campaignPerformanceReportDao.deleteCampaignPerformanceReportDo(campaignPerformanceReportDo);
		}
	}
	
	@Override
	public void delete30DaysData()
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -30);
		Date _30DaysBackDate = cal.getTime();
		Date todaysDate = new Date();
		
		List<CampaignPerformanceReportDo> campaignPerformanceReportDos = getCampaignPerformanceReportDosBetweenDate(_30DaysBackDate,todaysDate);
		
		for(CampaignPerformanceReportDo campaignPerformanceReportDo:campaignPerformanceReportDos)
		{
		campaignPerformanceReportDao.deleteCampaignPerformanceReportDo(campaignPerformanceReportDo);
		}
	}
	
	@Override
	public void deleteTodayData()
	{
		Date todaysDate = new Date();
		
		List<CampaignPerformanceReportDo> campaignPerformanceReportDos = getCampaignPerformanceReportDosBetweenDate(todaysDate,todaysDate);
		
		for(CampaignPerformanceReportDo campaignPerformanceReportDo:campaignPerformanceReportDos)
		{
		campaignPerformanceReportDao.deleteCampaignPerformanceReportDo(campaignPerformanceReportDo);
		}
	}


	@Override
	public Long getTotalImpressionsByDate(String startDate, String endDate,
			String[] status, String campaignsToFilter) {
		return campaignPerformanceReportDao.getTotalImpressionsByDate(startDate,endDate,status,campaignsToFilter);
	}
	


	@Override
	public Long getTotalClicksByDate(String startDate, String endDate,  String[] status,
			String campaignsToFilter) {
		return campaignPerformanceReportDao.getTotalClicksByDate(startDate,endDate,status,campaignsToFilter);
	}


	@Override
	
	public Double getTotalCostByDate(String startDate, String endDate,  String[] status,
			String campaignsToFilter) {
		return campaignPerformanceReportDao.getTotalCostByDate(startDate,endDate,status,campaignsToFilter);
	}


	@Override
	public Double getAvgCPCByDate(String startDate, String endDate,  String[] status,
			String campaignsToFilter) {
		return campaignPerformanceReportDao.getAvgCPCByDate(startDate,endDate,status,campaignsToFilter);
	}


	@Override
	public Double getAvgCTRByDate(String startDate, String endDate,  String[] status,
			String campaignsToFilter) {
		return campaignPerformanceReportDao.getAvgCTRByDate(startDate,endDate,status,campaignsToFilter);
	}


	@Override
	public List<CampaignPerformanceReportDo> getCampaignPerformanceReportDosListByDateAndPageAndSortedByColumn(
			String startDateForQuery, String endDateForQuery, String criteria, String[] status,
			String campaignsToFilter, Integer page, Integer rowsPerPage, String columnName, Integer orderBy) {
		return campaignPerformanceReportDao.getCampaignPerformanceReportDosListByDateAndPageAndSortedByColumn(startDateForQuery,endDateForQuery,criteria,status,campaignsToFilter,page,rowsPerPage,columnName,orderBy);
	}
}
