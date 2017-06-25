package com.dq.arq.sme.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dq.arq.sme.domain.CampaignDo;
import com.dq.arq.sme.domain.CampaignPerformanceReportDo;
import com.dq.arq.sme.services.CampaignService;
import com.dq.arq.sme.util.UtilConstants;
import com.dq.arq.sme.util.UtilityMethod;

@Repository
public class CampaignPerformanceReportDaoImpl implements CampaignPerformanceReportDao {

	@Autowired
	SessionFactory sessionFactory;
	
	@Autowired
	CampaignService campaignService;

	@Override
	public Integer saveCampaignPerformanceReportDo(CampaignPerformanceReportDo campaignPerformanceReportDo) {

		sessionFactory.getCurrentSession().save(campaignPerformanceReportDo);
		return campaignPerformanceReportDo.getId();

	}

	@Override
	public List<CampaignPerformanceReportDo> getCampaignPerformanceReportDosList() {

		
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CampaignPerformanceReportDo.class)
				.addOrder(Order.desc("date"));
		List<CampaignPerformanceReportDo> campaignPerformanceReportDos = cr.list();
		return campaignPerformanceReportDos;
	}
	
	
	@Override
	public List<CampaignPerformanceReportDo> getCampaignPerformanceReportDosBetweenDate(Date startDate,Date endDate)
	{
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CampaignPerformanceReportDo.class)
				.add(Restrictions.between("date", startDate, endDate));
		List<CampaignPerformanceReportDo> campaignPerformanceReportDos = cr.list();
		return campaignPerformanceReportDos;
	}

	@Override
	public CampaignPerformanceReportDo getCampaignPerformanceReportDoById(Integer id) {
		CampaignPerformanceReportDo campaignPerformanceReportDo = (CampaignPerformanceReportDo) sessionFactory.getCurrentSession().get(CampaignPerformanceReportDo.class, id);
		return campaignPerformanceReportDo;
	}

	/*@Override
	public CampaignPerformanceReportDo getCampaignPerformanceReportDoByName(String name) {
		String hql = "FROM CampaignPerformanceReportDo where name = '"+name+"'";
		List<CampaignPerformanceReportDo> campaignPerformanceReportDos = sessionFactory.getCurrentSession().createQuery(hql).list();
		if(campaignPerformanceReportDos.size()>0)
			return campaignPerformanceReportDos.get(0);
		else
			return null;
	}*/

	@Override
	public void updateCampaign(CampaignPerformanceReportDo campaignPerformanceReportDo) {
		sessionFactory.getCurrentSession().update(campaignPerformanceReportDo);

	}


	public boolean isYesterdaysRecordSynced()
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		Date yesterdaysDate = cal.getTime();

		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CampaignPerformanceReportDo.class)
				.add(Restrictions.eq("date", yesterdaysDate));
		
		Long count = new Long(cr.list().size());
		
		if(count>0)
			return true;
		else
			return false;

	}

	@Override
	public List<CampaignPerformanceReportDo> getCampaignPerformanceReportDosListByDateAndPage(String startDate,String endDate,String criteria,String[] statusList,String campaignsToFilterList,int page,int rowsPerPage)
	{	
		String[] campaignList = campaignsToFilterList.split(",");
		Integer[] campaignIds = new Integer[campaignList.length];
		int i=0;
		for(String str: campaignList )
		{
			campaignIds[i++] = Integer.parseInt(str);
		}
		
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CampaignPerformanceReportDo.class,"campaignPerformanceReportDo")
				.add(Restrictions.ge("date", UtilityMethod.convertStringYYYY_MM_DDTODateInJava(startDate)))
				.add(Restrictions.le("date", UtilityMethod.convertStringYYYY_MM_DDTODateInJava(endDate)))
				.createCriteria("campaignDo","campaignDo")
				.add(Restrictions.in("id", campaignIds))
				.add(Restrictions.in("status",statusList));
		
		if(criteria.equals("daywise")) {
			cr.setProjection(Projections.projectionList()
					.add(Projections.property("campaignDo.id"))
					.add(Projections.property("campaignPerformanceReportDo.clicks").as("clicks"))
					.add(Projections.property("campaignPerformanceReportDo.impressions").as("impressions"))
					.add(Projections.property("campaignPerformanceReportDo.cost").as("cost"))
					.add(Projections.property("campaignPerformanceReportDo.date"))
					).addOrder(Order.asc("campaignDo.status")).addOrder(Order.desc("campaignPerformanceReportDo.date"));
		}
		else {
			cr.setProjection(Projections.distinct(Projections.projectionList()
					.add(Projections.groupProperty("campaignDo.id"))
					.add(Projections.sum("campaignPerformanceReportDo.clicks").as("clicks"))
					.add(Projections.sum("campaignPerformanceReportDo.impressions").as("impressions"))
					.add(Projections.sum("campaignPerformanceReportDo.cost").as("cost"))
					)).addOrder(Order.asc("campaignDo.status")).addOrder(Order.asc("campaignDo.name"));
		}
		cr.setFirstResult((page-1)*rowsPerPage)
    			.setMaxResults(rowsPerPage);
		List<Object[]> rows=cr.list();
		List<CampaignPerformanceReportDo> campaignPerformanceReportDos = new ArrayList<CampaignPerformanceReportDo>();
		for(Object[] row: rows) {
			

			CampaignDo campaignDo = campaignService.getCampaignDoById(Integer.parseInt(row[0].toString()));
			
			CampaignPerformanceReportDo campaignPerformanceReportDo = new CampaignPerformanceReportDo();
			campaignPerformanceReportDo.setCampaignDo(campaignDo);;
			campaignPerformanceReportDo.setClicks((Long)row[1]);
			campaignPerformanceReportDo.setImpressions((Long)row[2]);
			campaignPerformanceReportDo.setCost((Double)row[3]/1000000);
			if(criteria.equals("daywise"))
				campaignPerformanceReportDo.setDate((Date)row[4]);
			campaignPerformanceReportDos.add(campaignPerformanceReportDo);

		}
		return campaignPerformanceReportDos;
	}
	
	@Override
	public List<CampaignPerformanceReportDo> getCampaignPerformanceReportDosListByDate(String startDate,String endDate,String criteria,String[] statusList, String campaignsToFilterList)
	{	
		String[] campaignList = campaignsToFilterList.split(",");
		Integer[] campaignIds = new Integer[campaignList.length];
		int i=0;
		for(String str: campaignList )
		{
			campaignIds[i++] = Integer.parseInt(str);
		}
		
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CampaignPerformanceReportDo.class,"campaignPerformanceReportDo")
				.add(Restrictions.ge("date", UtilityMethod.convertStringYYYY_MM_DDTODateInJava(startDate)))
				.add(Restrictions.le("date", UtilityMethod.convertStringYYYY_MM_DDTODateInJava(endDate)))
				.createCriteria("campaignDo","campaignDo")
				.add(Restrictions.in("id", campaignIds))
				.add(Restrictions.in("status",statusList));
		
		if(criteria.equals("daywise")) {
			cr.setProjection(Projections.projectionList()
					.add(Projections.property("campaignDo.id"))
					.add(Projections.property("campaignPerformanceReportDo.clicks").as("clicks"))
					.add(Projections.property("campaignPerformanceReportDo.impressions").as("impressions"))
					.add(Projections.property("campaignPerformanceReportDo.cost").as("cost"))
					.add(Projections.property("campaignPerformanceReportDo.date"))
					).addOrder(Order.asc("campaignDo.status")).addOrder(Order.desc("campaignPerformanceReportDo.date"));
		}
		else {
			cr.setProjection(Projections.distinct(Projections.projectionList()
					.add(Projections.groupProperty("campaignDo.id"))
					.add(Projections.sum("campaignPerformanceReportDo.clicks").as("clicks"))
					.add(Projections.sum("campaignPerformanceReportDo.impressions").as("impressions"))
					.add(Projections.sum("campaignPerformanceReportDo.cost").as("cost"))
					)).addOrder(Order.asc("campaignDo.status")).addOrder(Order.asc("campaignDo.name"));
		}
		List<Object[]> rows=cr.list();
		List<CampaignPerformanceReportDo> campaignPerformanceReportDos = new ArrayList<CampaignPerformanceReportDo>();
		for(Object[] row: rows) {
			

			CampaignDo campaignDo = campaignService.getCampaignDoById(Integer.parseInt(row[0].toString()));
			
			CampaignPerformanceReportDo campaignPerformanceReportDo = new CampaignPerformanceReportDo();
			campaignPerformanceReportDo.setCampaignDo(campaignDo);;
			campaignPerformanceReportDo.setClicks((Long)row[1]);
			campaignPerformanceReportDo.setImpressions((Long)row[2]);
			campaignPerformanceReportDo.setCost((Double)row[3]/1000000);
			if(criteria.equals("daywise"))
				campaignPerformanceReportDo.setDate((Date)row[4]);
			campaignPerformanceReportDos.add(campaignPerformanceReportDo);

		}
		return campaignPerformanceReportDos;
	}
	
	@Override
	public List<CampaignPerformanceReportDo> getCampaignPerformanceReportDosListByDateAndPageAndSortedByColumn(String startDate,String endDate,String criteria,String[] statusList,String campaignsToFilterList,Integer page,Integer rowsPerPage, String columnName, Integer orderBy) {
		String[] campaignList = campaignsToFilterList.split(",");
		Integer[] campaignIds = new Integer[campaignList.length];
		int i=0;
		for(String str: campaignList )
		{
			campaignIds[i++] = Integer.parseInt(str);
		}
		
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CampaignPerformanceReportDo.class,"campaignPerformanceReportDo")
				.add(Restrictions.ge("date", UtilityMethod.convertStringYYYY_MM_DDTODateInJava(startDate)))
				.add(Restrictions.le("date", UtilityMethod.convertStringYYYY_MM_DDTODateInJava(endDate)))
				.createCriteria("campaignDo","campaignDo")
				.add(Restrictions.in("id", campaignIds))
				.add(Restrictions.in("status",statusList));
		
		if(criteria.equals("daywise")) {
			cr.setProjection(Projections.projectionList()
					.add(Projections.property("campaignDo.id"))
					.add(Projections.property("campaignPerformanceReportDo.clicks").as("clicks"))
					.add(Projections.property("campaignPerformanceReportDo.impressions").as("impressions"))
					.add(Projections.property("campaignPerformanceReportDo.cost").as("cost"))
					.add(Projections.property("campaignPerformanceReportDo.date"))
					);
		}
		else {
			cr.setProjection(Projections.distinct(Projections.projectionList()
					.add(Projections.groupProperty("campaignDo.id"))
					.add(Projections.sum("campaignPerformanceReportDo.clicks").as("clicks"))
					.add(Projections.sum("campaignPerformanceReportDo.impressions").as("impressions"))
					.add(Projections.sum("campaignPerformanceReportDo.cost").as("cost"))
					));
		}
		switch (columnName) {
			case "da":
				if(orderBy==0)
					cr.addOrder(Order.asc("campaignPerformanceReportDo.date"));
				else
					cr.addOrder(Order.desc("campaignPerformanceReportDo.date"));
				break;
			case "ad":
				if(orderBy==0)
					cr.addOrder(Order.asc("campaignDo.name"));
				else
					cr.addOrder(Order.desc("campaignDo.name"));
				break;
			case "st":
				if(orderBy==0)
					cr.addOrder(Order.asc("campaignDo.status"));
				else
					cr.addOrder(Order.desc("campaignDo.status"));
				break;
			case "im":
				if(orderBy==0)
					cr.addOrder(Order.asc("impressions"));
				else
					cr.addOrder(Order.desc("impressions"));
				break;
			case "cl":
				if(orderBy==0)
					cr.addOrder(Order.asc("clicks"));
				else
					cr.addOrder(Order.desc("clicks"));
				break;
			case "co":
				if(orderBy==0)
					cr.addOrder(Order.asc("cost"));
				else
					cr.addOrder(Order.desc("cost"));
				break;
			default:
				break;
		}
		cr.setFirstResult((page-1)*rowsPerPage)
		.setMaxResults(rowsPerPage);
		List<Object[]> rows=cr.list();
		List<CampaignPerformanceReportDo> campaignPerformanceReportDos = new ArrayList<CampaignPerformanceReportDo>();
		for(Object[] row: rows) {
			

			CampaignDo campaignDo = campaignService.getCampaignDoById(Integer.parseInt(row[0].toString()));
			
			CampaignPerformanceReportDo campaignPerformanceReportDo = new CampaignPerformanceReportDo();
			campaignPerformanceReportDo.setCampaignDo(campaignDo);;
			campaignPerformanceReportDo.setClicks((Long)row[1]);
			campaignPerformanceReportDo.setImpressions((Long)row[2]);
			campaignPerformanceReportDo.setCost((Double)row[3]/1000000);
			if(criteria.equals("daywise"))
				campaignPerformanceReportDo.setDate((Date)row[4]);
			campaignPerformanceReportDos.add(campaignPerformanceReportDo);

		}
		return campaignPerformanceReportDos;
	}

	@Override
	public long countCampaignPerformanceReportDos(String startDate,String endDate,String criteria,String[] statusList,String campaignsToFilterList) {
		
		String[] campaignList = campaignsToFilterList.split(",");
		Integer[] campaignIds = new Integer[campaignList.length];
		int i=0;
		for(String str: campaignList )
		{
			campaignIds[i++] = Integer.parseInt(str);
		}
		
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CampaignPerformanceReportDo.class,"campaignPerformanceReportDo")
				.add(Restrictions.ge("date", UtilityMethod.convertStringYYYY_MM_DDTODateInJava(startDate)))
		.add(Restrictions.le("date", UtilityMethod.convertStringYYYY_MM_DDTODateInJava(endDate)))
		.createCriteria("campaignDo","campaignDo")
		.add(Restrictions.in("id", campaignIds))
		.add(Restrictions.in("status",statusList));
		if(criteria.equals("daywise")) {
			
			cr.setProjection(Projections.projectionList()
					.add(Projections.property("campaignDo.name"))
					.add(Projections.property("campaignPerformanceReportDo.date"))
					).addOrder(Order.desc("campaignPerformanceReportDo.date"));
		}
		else {
			cr.setProjection(Projections.distinct(Projections.projectionList()
					.add(Projections.groupProperty("campaignDo.name"))
					)).addOrder(Order.asc("campaignDo.name"));
		}
		List<Object[]> rows =cr.list();		
		return rows.size();
	}
	
	@Override
	public void deleteCampaignPerformanceReportDo(CampaignPerformanceReportDo campaignPerformanceReportDo)
	{
		sessionFactory.getCurrentSession().delete(campaignPerformanceReportDo);
	}
	
	

	@Override
	public Long getTotalImpressionsByDate(String startDate, String endDate, String[] statusList,
			String campaignsToFilter) {
		String[] campaignList = campaignsToFilter.split(",");
		Integer[] campaignIds = new Integer[campaignList.length];
		int i=0;
		for(String str: campaignList )
		{
			campaignIds[i++] = Integer.parseInt(str);
		}
		
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CampaignPerformanceReportDo.class)
				.add(Restrictions.ge("date", UtilityMethod.convertStringYYYY_MM_DDTODateInJava(startDate)))
		.add(Restrictions.le("date", UtilityMethod.convertStringYYYY_MM_DDTODateInJava(endDate)))
		.setProjection(Projections.projectionList().add(Projections.sum("impressions")))
		.createCriteria("campaignDo")
		.add(Restrictions.in("id", campaignIds))
		.add(Restrictions.in("status",statusList));
		List<Long> rows=cr.list();
		if(rows !=null && rows.size() !=0) {
			return rows.get(0);
		}else {
			return null;
		}
	}

	@Override
	public Long getTotalClicksByDate(String startDate, String endDate, String[] statusList,
			String campaignsToFilter) {
		String[] campaignList = campaignsToFilter.split(",");
		Integer[] campaignIds = new Integer[campaignList.length];
		int i=0;
		for(String str: campaignList )
		{
			campaignIds[i++] = Integer.parseInt(str);
		}
		
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CampaignPerformanceReportDo.class)
				.add(Restrictions.ge("date", UtilityMethod.convertStringYYYY_MM_DDTODateInJava(startDate)))
		.add(Restrictions.le("date", UtilityMethod.convertStringYYYY_MM_DDTODateInJava(endDate)))
		.setProjection(Projections.projectionList().add(Projections.sum("clicks")))
		.createCriteria("campaignDo")
		.add(Restrictions.in("id", campaignIds))
		.add(Restrictions.in("status",statusList));
		List<Long> rows=cr.list();
		if(rows !=null && rows.size() !=0) {
			return rows.get(0);
		}else {
			return null;
		}
	}

	@Override
	public Double getTotalCostByDate(String startDate, String endDate, String[] statusList,
			String campaignsToFilter) {
		
		String[] campaignList = campaignsToFilter.split(",");
		Integer[] campaignIds = new Integer[campaignList.length];
		int i=0;
		for(String str: campaignList )
		{
			campaignIds[i++] = Integer.parseInt(str);
		}
		
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CampaignPerformanceReportDo.class)
				.add(Restrictions.ge("date", UtilityMethod.convertStringYYYY_MM_DDTODateInJava(startDate)))
		.add(Restrictions.le("date", UtilityMethod.convertStringYYYY_MM_DDTODateInJava(endDate)))
		.setProjection(Projections.projectionList().add(Projections.sum("cost")))
		.createCriteria("campaignDo")
		.add(Restrictions.in("id", campaignIds))
		.add(Restrictions.in("status",statusList));
		List<Double> rows=cr.list();
		if(rows !=null && rows.size() !=0) {
			return rows.get(0) != null ? (rows.get(0)/1000000) :null;
		}else {
			return null;
		}
	}

	@Override
	public Double getAvgCPCByDate(String startDate, String endDate, String[] statusList,
			String campaignsToFilter) {
		

		String[] campaignList = campaignsToFilter.split(",");
		Integer[] campaignIds = new Integer[campaignList.length];
		int i=0;
		for(String str: campaignList )
		{
			campaignIds[i++] = Integer.parseInt(str);
		}
		
		
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CampaignPerformanceReportDo.class)
		.add(Restrictions.ge("date", UtilityMethod.convertStringYYYY_MM_DDTODateInJava(startDate)))
		.add(Restrictions.le("date", UtilityMethod.convertStringYYYY_MM_DDTODateInJava(endDate)))
		.setProjection(Projections.projectionList().add(Projections.property("cost")).add(Projections.property("clicks")))
		.createCriteria("campaignDo")
		.add(Restrictions.in("id", campaignIds))
		.add(Restrictions.in("status",statusList));
		
		
		List<Object[]> rows=cr.list();
		Double sumCPC = 0.0;
		for(Object[] row: rows) {
			Double cost = ((Double) row[0])/1000000;
			Long clicks = (Long) row[1];
			Double CPC;
			if(clicks !=0)
				CPC = cost/clicks;
			else
				CPC = 0.0;
			sumCPC+=CPC;
		}
		if(rows != null && rows.size() >0) {
			return sumCPC/rows.size();
		}
		return null;
	}

	@Override
	public Double getAvgCTRByDate(String startDate, String endDate, String[] statusList,
			String campaignsToFilter) {
		
		

		String[] campaignList = campaignsToFilter.split(",");
		Integer[] campaignIds = new Integer[campaignList.length];
		int i=0;
		for(String str: campaignList )
		{
			campaignIds[i++] = Integer.parseInt(str);
		}
		
		
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CampaignPerformanceReportDo.class)
				.add(Restrictions.ge("date", UtilityMethod.convertStringYYYY_MM_DDTODateInJava(startDate)))
				.add(Restrictions.le("date", UtilityMethod.convertStringYYYY_MM_DDTODateInJava(endDate)))
				.setProjection(Projections.projectionList().add(Projections.property("clicks")).add(Projections.property("impressions")))
				.createCriteria("campaignDo")
				.add(Restrictions.in("id", campaignIds))
				.add(Restrictions.in("status",statusList));
		
		List<Object[]> rows=cr.list();
		Double sumCTR = 0.0;
		for(Object[] row: rows) {
			Long clicks = (Long) row[0];
			Long impressions = (Long) row[1];
			Double CTR;
			if(impressions != 0)
				CTR = clicks.doubleValue()/impressions.doubleValue();
			else
				CTR = 0.0;
			sumCTR+=CTR;
		}
		if(rows != null && rows.size() >0) {
			return 100.0*sumCTR/rows.size();
		}
		return null;
	}

	

}
