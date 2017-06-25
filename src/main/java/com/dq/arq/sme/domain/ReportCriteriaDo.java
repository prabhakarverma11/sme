package com.dq.arq.sme.domain;

public class ReportCriteriaDo {
	
	public enum Status{
		Enabled,Paused,Removed,Unknown
	}
	String campaignsToFilter;
	Integer filterCampaign; 
	String criteria;
	String dateRangeType;
	String startDate;
	String endDate;
	Status status[];
	
	public Integer getFilterCampaign() {
		return filterCampaign;
	}
	public void setFilterCampaign(Integer filterCampaign) {
		this.filterCampaign = filterCampaign;
	}
	public String getCampaignsToFilter() {
		return campaignsToFilter;
	}
	public void setCampaignsToFilter(String campaignsToFilter) {
		this.campaignsToFilter = campaignsToFilter;
	}
	public String getCriteria() {
		return criteria;
	}
	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getDateRangeType() {
		return dateRangeType;
	}
	public void setDateRangeType(String dateRangeType) {
		this.dateRangeType = dateRangeType;
	}
	public String[] getStatus() {
		String[] statusList = new String[this.status.length];
		for(int i=0;i<this.status.length;i++) {
			statusList[i] = this.status[i].name();
		}
		return statusList;
	}
	public void setStatus(String[] status) {
		Status[] newStatus = new Status[status.length];
		for(int i = 0;i<status.length;i++) {
			newStatus[i] = Status.valueOf(status[i]);
		}
			
		this.status = newStatus;
	}
	
}
