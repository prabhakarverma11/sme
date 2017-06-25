package com.dq.arq.sme.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import au.com.bytecode.opencsv.CSVWriter;

import com.dq.arq.sme.adwordapi.ReportAdwordApi;
import com.dq.arq.sme.domain.AdgroupDo;
import com.dq.arq.sme.domain.CampaignDo;
import com.dq.arq.sme.domain.CampaignPerformanceReportDo;
import com.dq.arq.sme.domain.ReportCriteriaDo;
import com.dq.arq.sme.domain.UserDo;
import com.dq.arq.sme.domain.UserRoleDo;
import com.dq.arq.sme.services.CampaignPerformanceReportService;
import com.dq.arq.sme.services.CampaignService;
import com.dq.arq.sme.services.MailService;
import com.dq.arq.sme.services.UserService;
import com.dq.arq.sme.util.UtilConstants;
import com.dq.arq.sme.util.UtilityMethod;
import com.google.api.ads.adwords.lib.utils.ReportDownloadResponseException;
import com.google.api.ads.adwords.lib.utils.ReportException;

@Controller
public class ReportController {

	//final static Logger logger = LoggerFactory.getLogger(ReportController.class); 
	final static Logger logger = LoggerFactory.getLogger("LogTesting"); 
	
	@Autowired
	CampaignPerformanceReportService campaignPerformanceReportService;
	
	@Autowired
	CampaignService campaignService;
	
	@Autowired
	UserService userService;
	
	/**
	 * 
	 * @param model
	 * @param request
	 * @param reportCriteriaDo
	 * @return
	 * 
	 * reportCriteriaDo object is passed with criteria selected by user
	 * 
	 * Yesterdays records are synced from the adword database to local database if not synced already
	 * 
	 * Report details are fetched from the local database
	 */
	@RequestMapping(value = "/cpreport")
	public String criteriaPerformanceReport(ModelMap model,HttpServletRequest request,
			HttpSession session,
			@ModelAttribute("reportCriteriaDo") ReportCriteriaDo reportCriteriaDo,
			@RequestParam(value="page", required = false) Integer page,
			@RequestParam(value="rows", required = false) Integer rows,
			@RequestParam(value = "col", required = false) String columnName,
			@RequestParam(value = "o", required = false) Integer orderBy)
	{
		logger.debug("\n\n\n*************** Entering criteriaPerformanceReport method of ReportController ***************\n\n\n");
		if(columnName==null || columnName.equals("")) {
			session.removeAttribute("col");
			session.removeAttribute("o");
		}
		Integer rowsPerPage = UtilConstants.CAMPAIGNS_PER_PAGE; 
		if(rows!=null)
			rowsPerPage = rows;
		
		String startDateForQuery="";
		String endDateForQuery = "";
		
		UserDo userDo = (UserDo) request.getSession().getAttribute("sUser");
		UserRoleDo userRoleDo = userService.getUserRoleDoByUserDo(userDo);
		
		List<CampaignDo> campaignDosList = new ArrayList<CampaignDo>();
		
		if(userRoleDo.getRole().equals("ROLE_ADMIN"))
			campaignDosList =campaignService.getCampaignDosListForAdmin();
		else {
			campaignDosList = campaignService.getCampaignDosListByUserDo(userDo);
		}
		model.put("campaignDosList", campaignDosList);
		
		List<CampaignPerformanceReportDo> campaignPerformanceReportDos = new ArrayList<CampaignPerformanceReportDo>();
		if(reportCriteriaDo.getCriteria()==null && page==null)
		{
			session.setAttribute("totalRecords", 0);
			model.put("campaignPerformanceReportDos", campaignPerformanceReportDos);
			reportCriteriaDo.setCriteria("cumulative");
			reportCriteriaDo.setStatus(new String[]{ReportCriteriaDo.Status.Enabled.name()});
			reportCriteriaDo.setDateRangeType("TODAY");
			reportCriteriaDo.setStartDate(UtilityMethod.formatDateTOMM_DD_YYYY(new Date()));
			reportCriteriaDo.setEndDate(UtilityMethod.formatDateTOMM_DD_YYYY(new Date()));
			model.put("reportCriteriaDo",reportCriteriaDo);
			logger.info("\n\n\n+++++++++++++++ SUCCESS:: default reportCriteriaDo with details: +++++++++++++++\n"
					+ "criteria: "+reportCriteriaDo.getCriteria()+"\n"
					+ "status: "+Arrays.toString(reportCriteriaDo.getStatus())+"\n"
					+ "dateRangeType: "+reportCriteriaDo.getDateRangeType()+"\n"
					+ "startDate: "+reportCriteriaDo.getStartDate()+"\n"
					+ "endDate: "+reportCriteriaDo.getEndDate()+" has been set.\n"
					+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
			logger.debug("\n\n\n############### Exiting criteriaPerformanceReport method of ReportController ###############\n\n\n");
			return "cpreport";
		}
		//Sync with yesterday's data starts
		if(!campaignPerformanceReportService.isYesterdaysRecordSynced())
		{
			
			List<CampaignPerformanceReportDo> campaignPerformanceReportDos1 = new ArrayList<CampaignPerformanceReportDo>();

			try {
				List<CampaignDo> campaignDosForSyncingData = campaignDosList; 
				if(!userRoleDo.getRole().equals("ROLE_ADMIN"))
					campaignDosForSyncingData = campaignService.getCampaignDosListForAdmin();
				
				reportCriteriaDo.setStartDate(UtilityMethod.convertYYYY_MM_DDtoYYYYMMDD(UtilityMethod.convertMM_DD_YYYYtoYYYY_MM_DD(reportCriteriaDo.getStartDate())));
				reportCriteriaDo.setEndDate(UtilityMethod.convertYYYY_MM_DDtoYYYYMMDD(UtilityMethod.convertMM_DD_YYYYtoYYYY_MM_DD(reportCriteriaDo.getEndDate())));
				new ReportAdwordApi().syncYesterdayCampaignPerformanceReport(campaignPerformanceReportDos1,campaignDosForSyncingData,campaignService);
				reportCriteriaDo.setStartDate(UtilityMethod.convertYYYYMMDDtoMM_DD_YYYY(reportCriteriaDo.getStartDate()));
				reportCriteriaDo.setEndDate(UtilityMethod.convertYYYYMMDDtoMM_DD_YYYY(reportCriteriaDo.getEndDate()));
				campaignPerformanceReportService.saveCampaignPerformanceReportDos(campaignPerformanceReportDos1);
				logger.info("\n\n\n+++++++++++++++ SUCCESS:: "+campaignPerformanceReportDos1.size()+" yesterdays record inserted into database. +++++++++++++++\n");
			} catch (ReportException | ReportDownloadResponseException
					| IOException | ParseException e) {
				logger.info("\n\n\n??????????????? ERROR:: Could not get Campaign performance Report from Google Adwords, errorMessage:"+e.getMessage()+" ???????????????\n\n\n");
				e.printStackTrace();
				model.put("msg", "Could not get Campaign performance Report from Google Adwords. Check log for errors");
				model.put("errorMsg",0);
				logger.debug("\n\n\n############### Exiting criteriaPerformanceReport method of ReportController ###############\n\n\n");
				return "cpreport";
			}
			
		}
		//Sync with yesterday's data ends
		
		if(page !=null)
			reportCriteriaDo = (ReportCriteriaDo) session.getAttribute("reportCriteriaDo");
		startDateForQuery = UtilityMethod.convertMM_DD_YYYYtoYYYY_MM_DD(reportCriteriaDo.getStartDate());
		endDateForQuery = UtilityMethod.convertMM_DD_YYYYtoYYYY_MM_DD(reportCriteriaDo.getEndDate());
		String campaignsToFilter = reportCriteriaDo.getCampaignsToFilter();
		
		//Get details of overall report
		Long totalImpressions = campaignPerformanceReportService.getTotalImpressionsByDate(startDateForQuery,endDateForQuery,reportCriteriaDo.getStatus(),campaignsToFilter);
		Long totalClicks = campaignPerformanceReportService.getTotalClicksByDate(startDateForQuery,endDateForQuery,reportCriteriaDo.getStatus(),campaignsToFilter);
		Double totalCost = campaignPerformanceReportService.getTotalCostByDate(startDateForQuery,endDateForQuery,reportCriteriaDo.getStatus(),campaignsToFilter);
		Double avgCPC = campaignPerformanceReportService.getAvgCPCByDate(startDateForQuery,endDateForQuery,reportCriteriaDo.getStatus(),campaignsToFilter);
		Double avgCTR = campaignPerformanceReportService.getAvgCTRByDate(startDateForQuery,endDateForQuery,reportCriteriaDo.getStatus(),campaignsToFilter);
		
		model.put("totalImpressions", totalImpressions);
		model.put("totalClicks", totalClicks);
		model.put("totalCost", totalCost);
		model.put("avgCPC", avgCPC);
		model.put("avgCTR", avgCTR);
		
		
		//Get Report from local db starts
		Integer totalRecords = 0;
		if(page==null) {
			page = 1;
			totalRecords = (int) campaignPerformanceReportService.countCampaignPerformanceReportDos(startDateForQuery,endDateForQuery,reportCriteriaDo.getCriteria(),reportCriteriaDo.getStatus(),campaignsToFilter);
			session.setAttribute("reportCriteriaDo", reportCriteriaDo);
			session.setAttribute("totalRecords", totalRecords);
		}else {
			totalRecords = (Integer) session.getAttribute("totalRecords");
			reportCriteriaDo = (ReportCriteriaDo) session.getAttribute("reportCriteriaDo");
		}
		if(totalRecords>0) {
			if(columnName!=null && !columnName.equals("")) {
				session.setAttribute("col", columnName);
				session.setAttribute("o", orderBy);
				campaignPerformanceReportDos = campaignPerformanceReportService.getCampaignPerformanceReportDosListByDateAndPageAndSortedByColumn(startDateForQuery,endDateForQuery,reportCriteriaDo.getCriteria(),reportCriteriaDo.getStatus(),campaignsToFilter,page,rowsPerPage,columnName,orderBy);
			}else {
				campaignPerformanceReportDos = campaignPerformanceReportService.getCampaignPerformanceReportDosListByDateAndPage(startDateForQuery,endDateForQuery,reportCriteriaDo.getCriteria(),reportCriteriaDo.getStatus(),campaignsToFilter,page,rowsPerPage);
			}
		}
		logger.info("\n\n\n+++++++++++++++ INFO:: for totals: +++++++++++++++\n"
				+ "startDateForQuery: "+startDateForQuery+"\n"
				+ "endDateForQuery: "+endDateForQuery+"\n"
				+ "criteria: "+reportCriteriaDo.getCriteria()+"\n"
				+ "status: "+Arrays.toString(reportCriteriaDo.getStatus())+"\n"
				+ "campaignsToFilter: "+campaignsToFilter+"\n"
				+ "totalImpressions: "+totalImpressions+"\n"
				+ "totalClicks: "+totalClicks+"\n"
				+ "totalCost: "+totalCost+"\n"
				+ "avgCPC: "+avgCPC+"\n"
				+ "avgCTR: "+avgCTR+"\n"
				+ "totalRecords: "+totalRecords+"\n"
				+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
		
		//Get Report from local db ends
		
		model.put("campaignPerformanceReportDos", campaignPerformanceReportDos);
		model.put("rows", rows);
		model.put("reportCriteriaDo",reportCriteriaDo);
		model.put("userEmail",userDo.getEmail());
		session.setAttribute("recordsPerPage", rowsPerPage);
		session.setAttribute("pageNumber", page);
		logger.debug("\n\n\n############### Exiting criteriaPerformanceReport method of ReportController ###############\n\n\n");
		return "cpreport";
	}
	
	@RequestMapping(value = "/refresh30dayscpreport")
	public String refresh30DaysCampaignPerformanceReportData(ModelMap model,HttpServletRequest request,
			HttpSession session)
	{
		logger.debug("\n\n\n*************** Entering refresh30DaysCampaignPerformanceReportData method of ReportController ***************\n\n\n");
		try {
			List<CampaignDo> campaignDosForSyncingData= campaignService.getCampaignDosListForAdmin();
			List<CampaignPerformanceReportDo> campaignPerformanceReportDos = new ArrayList<CampaignPerformanceReportDo>();
			new ReportAdwordApi().sync30DaysCPReport(campaignPerformanceReportDos,campaignDosForSyncingData,campaignService);
			campaignPerformanceReportService.delete30DaysData();
			campaignPerformanceReportService.saveCampaignPerformanceReportDos(campaignPerformanceReportDos);
			logger.info("\n\n\n+++++++++++++++ SUCCESS:: "+campaignPerformanceReportDos.size()+" records inserted into database. +++++++++++++++\n");
		} catch (NumberFormatException | ReportException
				| ReportDownloadResponseException | IOException
				| ParseException e) {
			logger.info("\n\n\n??????????????? ERROR:: Could not get Campaign performance Report from Google Adwords, errorMessage:"+e.getMessage()+" ???????????????\n\n\n");
			e.printStackTrace();
		}
		logger.debug("\n\n\n############### Exiting refresh30DaysCampaignPerformanceReportData method of ReportController ###############\n\n\n");
		return "forward:/userHome";
	}
	
	@RequestMapping(value = "/refreshtodaycpreport")
	public String refreshTodayCampaignPerformanceReportData(ModelMap model,HttpServletRequest request,
			HttpSession session)
	{
		logger.debug("\n\n\n*************** Entering refreshTodayCampaignPerformanceReportData method of ReportController ***************\n\n\n");
		try {
			List<CampaignDo> campaignDosForSyncingData= campaignService.getCampaignDosListForAdmin();
			List<CampaignPerformanceReportDo> campaignPerformanceReportDos = new ArrayList<CampaignPerformanceReportDo>();
			new ReportAdwordApi().syncTodayCPReport(campaignPerformanceReportDos,campaignDosForSyncingData,campaignService);
			campaignPerformanceReportService.deleteTodayData();
			campaignPerformanceReportService.saveCampaignPerformanceReportDos(campaignPerformanceReportDos);
		} catch (NumberFormatException | ReportException
				| ReportDownloadResponseException | IOException
				| ParseException e) {
			logger.info("\n\n\n??????????????? ERROR:: Could not get Campaign performance Report from Google Adwords, errorMessage:"+e.getMessage()+" ???????????????\n\n\n");
			e.printStackTrace();
		}
		logger.debug("\n\n\n############### Exiting refreshTodayCampaignPerformanceReportData method of ReportController ###############\n\n\n");
		return "forward:/userHome";
	}

	@RequestMapping(value="/mailReport")
	public String mailReport(ModelMap model,HttpServletRequest request,HttpSession session,HttpServletResponse response) throws IOException, AddressException, MessagingException
	{
		logger.debug("\n\n\n*************** Entering mailReport method of ReportController ***************\n\n\n");
		UserDo userDo = (UserDo) request.getSession().getAttribute("sUser");
		ReportCriteriaDo reportCriteriaDo = (ReportCriteriaDo) session.getAttribute("reportCriteriaDo");
		String fileName="CampaignPerformanceReport_"+System.currentTimeMillis()+".csv";
		File csvFile = getReportCSVFile(fileName,request,session);

		String mailSubject="";
		if(reportCriteriaDo.getDateRangeType().equals("TODAY") || reportCriteriaDo.getDateRangeType().equals("YESTERDAY"))
			mailSubject = "ARQ SME | Campaign Performance Report for "+reportCriteriaDo.getStartDate();
		else 
			mailSubject = "ARQ SME | Campaign Performance Report for date between "+reportCriteriaDo.getStartDate() +" and "+reportCriteriaDo.getEndDate();

		String mailMessage = "Greetings!!<br><br>Please find attached Performance Report for date between "+reportCriteriaDo.getStartDate() +" and "+reportCriteriaDo.getEndDate()
		+"<br><br><br>"
		+ "----------------------<br>"
		+ "Thanks & Regards<br>"
		+ "ARQ Team";
		String toAddress = request.getParameter("toAddresses");
//		String toAddress = userDo.getEmail();
		if(!UtilityMethod.getServerName(request).equals("http://localhost:8080/sme/"))
		{
			try{
				logger.debug("\n\n\n=============== SEND:: mail with details: ===============\n"
					+ "sendTo: "+toAddress+"\n"
					+ "mailSubject: "+mailSubject+"\n"
					+ "mailMessage: "+mailMessage+"\n"
					+ "serverName: "+UtilityMethod.getServerName(request)+"\n"
					+ "=============================================\n\n\n");
				MailService.sendAttachmentEmail(toAddress,mailSubject, mailMessage, csvFile.getAbsolutePath());
				logger.info("\n\n\n+++++++++++++++ SUCCESS:: mail sent to:"+toAddress+" successfully. +++++++++++++++\n\n\n");
			}catch (Exception e) {
				logger.info("\n\n\n??????????????? ERROR:: mail could not send to:"+toAddress+" , errorMessage:"+e.getMessage()+"???????????????\n\n\n");
				e.printStackTrace();
			}
		}
		
		model.put("msg", "Email sent to addresses: "+toAddress+" successfully");
		model.put("errorMsg",1);
		
		logger.debug("\n\n\n############### Exiting mailReport method of ReportController ###############\n\n\n");	
		return "forward:/cpreport";
	}

	@RequestMapping(value="/downloadcsv")
	public void downloadCSV(HttpServletRequest request,HttpSession session,HttpServletResponse response) throws IOException
	{
		logger.debug("\n\n\n*************** Entering downloadCSV method of ReportController ***************\n\n\n");
		String fileName="CampaignPerformanceReport_"+System.currentTimeMillis()+".csv";
		File csvFile = getReportCSVFile(fileName,request,session);

		// Writing data into csv file starts
		try {
			response.setHeader("Content-type","application/csv");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

			OutputStream out = response.getOutputStream();
			FileInputStream in = new FileInputStream(csvFile);

			int readBytes = in.available();
			byte[] content = new byte[readBytes];
			in.read(content);
			out.write(content);

			in.close();
			out.close();
			logger.info("\n\n\n+++++++++++++++ SUCCESS:: CSV is downloaded successfully +++++++++++++++\n\n\n");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("\n\n\n??????????????? ERROR:: CSV could not be downloaded successfully, errorMessage:"+e.getMessage()+" ???????????????\n\n\n");
			logger.debug("\n\n\n############### Exiting downloadCSV method of ReportController ###############\n\n\n");
			return;
		}
		// Writing data into csv file ends
		logger.debug("\n\n\n############### Exiting downloadCSV method of ReportController ###############\n\n\n");
	}

	private File getReportCSVFile(String fileName, HttpServletRequest request,
			HttpSession session) throws IOException {
		logger.debug("\n\n\n*************** Entering getReportCSVFile method of ReportController ***************\n\n\n");
		ReportCriteriaDo reportCriteriaDo = (ReportCriteriaDo) session.getAttribute("reportCriteriaDo");

		Date startDate = UtilityMethod.convertStringMM_DD_YYYYTODateInJava(reportCriteriaDo.getStartDate());
		Date endDate = UtilityMethod.convertStringMM_DD_YYYYTODateInJava(reportCriteriaDo.getEndDate());

		String campaignsToFilter = reportCriteriaDo.getCampaignsToFilter();


		List<CampaignPerformanceReportDo> campaignPerformanceReportDos =  campaignPerformanceReportService.getCampaignPerformanceReportDosListByDate(UtilityMethod.formatDateTOYYYY_MM_DD(startDate),UtilityMethod.formatDateTOYYYY_MM_DD(endDate),reportCriteriaDo.getCriteria(),reportCriteriaDo.getStatus(),campaignsToFilter);
		Long totalImpressions = campaignPerformanceReportService.getTotalImpressionsByDate(UtilityMethod.formatDateTOYYYY_MM_DD(startDate),UtilityMethod.formatDateTOYYYY_MM_DD(endDate),reportCriteriaDo.getStatus(),campaignsToFilter);
		Long totalClicks = campaignPerformanceReportService.getTotalClicksByDate(UtilityMethod.formatDateTOYYYY_MM_DD(startDate),UtilityMethod.formatDateTOYYYY_MM_DD(endDate),reportCriteriaDo.getStatus(),campaignsToFilter);
		Double totalCost = campaignPerformanceReportService.getTotalCostByDate(UtilityMethod.formatDateTOYYYY_MM_DD(startDate),UtilityMethod.formatDateTOYYYY_MM_DD(endDate),reportCriteriaDo.getStatus(),campaignsToFilter);
		Double avgCPC = campaignPerformanceReportService.getAvgCPCByDate(UtilityMethod.formatDateTOYYYY_MM_DD(startDate),UtilityMethod.formatDateTOYYYY_MM_DD(endDate),reportCriteriaDo.getStatus(),campaignsToFilter);
		Double avgCTR = campaignPerformanceReportService.getAvgCTRByDate(UtilityMethod.formatDateTOYYYY_MM_DD(startDate),UtilityMethod.formatDateTOYYYY_MM_DD(endDate),reportCriteriaDo.getStatus(),campaignsToFilter);
		
		File csvFile = new File(fileName);
		csvFile.setReadable(true, false);
		csvFile.setWritable(true, false);
		csvFile.createNewFile();
		CSVWriter writer = new CSVWriter(new FileWriter(csvFile.getAbsolutePath()));
		UserRoleDo userRoleDo = userService.getUserRoleDoByUserDo((UserDo)session.getAttribute("sUser"));
		if(reportCriteriaDo.getCriteria().equals("daywise")) {
			if(userRoleDo.getRole().equals("ROLE_ADMIN"))
				writer.writeNext(new String[]{"S.No","Date","Campaign Name","Campaign Status","Impressions","Clicks","CPC","CTR (in %)","Cost (Rs.)"});
			else
				writer.writeNext(new String[]{"S.No","Date","Campaign Name","Campaign Status","Impressions","Clicks","CTR (in %)"});
				
		}
		else {
			if(userRoleDo.getRole().equals("ROLE_ADMIN"))
				writer.writeNext(new String[]{"S.No","Campaign Name","Campaign Status","Impressions","Clicks","CPC","CTR (in %)","Cost (Rs.)"});
			else
				writer.writeNext(new String[]{"S.No","Campaign Name","Campaign Status","Impressions","Clicks","CTR (in %)"});
				
		}
		int count=1;
		
		logger.info("\n\n\n+++++++++++++++ INFO:: for totals: +++++++++++++++\n"
				+ "startDateForQuery: "+UtilityMethod.formatDateTOYYYY_MM_DD(startDate)+"\n"
				+ "endDateForQuery: "+UtilityMethod.formatDateTOYYYY_MM_DD(endDate)+"\n"
				+ "criteria: "+reportCriteriaDo.getCriteria()+"\n"
				+ "status: "+Arrays.toString(reportCriteriaDo.getStatus())+"\n"
				+ "campaignsToFilter: "+campaignsToFilter+"\n"
				+ "totalImpressions: "+totalImpressions+"\n"
				+ "totalClicks: "+totalClicks+"\n"
				+ "avgCPC: "+avgCPC+"\n"
				+ "avgCTR: "+avgCTR+"\n"
				+ "totalCost: "+totalCost+"\n"
				+ "+++++++++++++++++++++++++++++++++++++++++++++\n\n\n");
		logger.debug("\n\n\n=============== SEND:: ===============\n"
	    		+ "startDate: "+startDate+"\n"
	    		+ "endDate: "+endDate+"\n"
				+ "criteria: "+reportCriteriaDo.getCriteria()+"\n"
				+ "status: "+Arrays.toString(reportCriteriaDo.getStatus())+"\n"
				+ "endDate: "+reportCriteriaDo.getEndDate()+"\n"
				+ "campaignsToFilter: "+campaignsToFilter+"\n"
				+ "=============================================\n\n\n");
		DecimalFormat df = new DecimalFormat("###.##");
		String line ="";
		line+="Total,";
		if(reportCriteriaDo.getCriteria().equals("daywise"))
			line+=" ,";
		line+=" , ,";
		line+=totalImpressions+",";
		line+=totalClicks+",";
		if(userRoleDo.getRole().equals("ROLE_ADMIN"))
			line+=df.format(avgCPC)+",";
		line+=df.format(avgCTR)+",";
		if(userRoleDo.getRole().equals("ROLE_ADMIN"))
			line+=Math.round(totalCost)+",";
		writer.writeNext(line.split(","));
		
		for(CampaignPerformanceReportDo row: campaignPerformanceReportDos)
		{
			line ="";
			line+=(count++)+",";
			if(reportCriteriaDo.getCriteria().equals("daywise"))
				line+=UtilityMethod.formatDateTOYYYY_MM_DD(row.getDate())+",";
			line+=row.getCampaignDo().getName()+",";
			line+=row.getCampaignDo().getStatus()+",";
			line+=row.getImpressions()+",";
			line+=row.getClicks()+",";
			if(userRoleDo.getRole().equals("ROLE_ADMIN")) {
				if(row.getClicks() != 0) {
					line+=df.format(row.getCost()/row.getClicks())+",";
				}else {
					line+="-,";
				}
			}
			if(row.getImpressions() !=0) {
				line+=df.format(100*row.getClicks()/row.getImpressions())+",";
			}else {
				line+="-,";
			}
			if(userRoleDo.getRole().equals("ROLE_ADMIN"))
				line+=Math.round(row.getCost())+",";
			logger.debug("\n\n\n=============== RECEIVED:: row with details: ===============\n"
					+ "campaignName: "+row.getCampaignDo().getName()+"\n"
					+ "status: "+row.getCampaignDo().getStatus()+"\n"
					+ "impressions: "+row.getImpressions()+"\n"
					+ "clicks: "+row.getClicks()+"\n"
					+ "cost: "+row.getCost()+"\n"
					+ "date: "+row.getDate()+"\n"
					+ "=============================================\n\n\n");
			writer.writeNext(line.split(","));
		}
		writer.close();
		logger.debug("\n\n\n############### Exiting getReportCSVFile method of ReportController ###############\n\n\n");
		return csvFile;
	}

}
