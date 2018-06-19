package com.capitalone.dashboard.rest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.capitalone.dashboard.auth.access.Admin;
import com.capitalone.dashboard.model.ErrorLog;
import com.capitalone.dashboard.service.ErrorLogService;
import com.capitalone.dashboard.util.ApplicationDBLogger;
import com.capitalone.dashboard.util.HygieiaConstants;

@RestController
@RequestMapping("/misc")
@Admin
public class MiscController {
	private static final Log LOG = LogFactory.getLog(MiscController.class);
	private final ErrorLogService errorLogService;

	@Autowired
	public MiscController(ErrorLogService errorLogService) {
		this.errorLogService = errorLogService;
	}

	@RequestMapping(path = "/showLogs", method = GET)
	public ModelAndView showLogs(
			HttpServletResponse response,
			@RequestParam(value = "timestamp", required = false) String timestamp) {
		long time = 0;
		if (timestamp != null && !timestamp.trim().equals("")) {
			time = Long.parseLong(timestamp);
		} else {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR, -24);
			time = cal.getTimeInMillis();
		}
		exportXls(errorLogService.fetchLogs(time), response);
		return null;
	}

	private void exportXls(List<ErrorLog> lstErrorLog,
			HttpServletResponse response) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		try {
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition",
					"attachment; filename=ErrorLog.xlsx");
			XSSFSheet sheet = workbook.createSheet("ErrorLog");
			int rownum = 0;
			Row row = sheet.createRow(rownum++);
			createHeader(row);
			for (ErrorLog errorLog : lstErrorLog) {
				row = sheet.createRow(rownum++);
				createList(errorLog, row);
			}
			workbook.write(response.getOutputStream());
			workbook.close();
		} catch (Exception e) {
			LOG.error("Error writting to Error Log XLS", e);
			ApplicationDBLogger.log(HygieiaConstants.API,
					"ApplicationDBLogMonitor.exportXls",
					"Error writting to Error Log XLS", e);
		}
	}

	@SuppressWarnings({"CPD-START"})
	private void createList(ErrorLog errorLog, Row row) {
		Cell cell = row.createCell(0);
		cell.setCellValue(errorLog.displayDate());

		cell = row.createCell(1);
		cell.setCellValue(errorLog.getModule());

		cell = row.createCell(2);
		cell.setCellValue(errorLog.getFunctionality());

		cell = row.createCell(3);
		cell.setCellValue(trunk(errorLog.getMessage()));

		cell = row.createCell(4);
		cell.setCellValue(trunk(errorLog.getException()));

		cell = row.createCell(5);
		cell.setCellValue(trunk(errorLog.getData()));
	}
	
	@SuppressWarnings({"CPD-START"})
	private String trunk(String msg)
	{
		if(msg==null) {
			return "";
		} else if(msg.length() >30000 ) {
			return msg.substring(0, 30000);
		} else { 
			return msg;
		}
	}

	private void createHeader(Row row) {
		Cell cell = row.createCell(0);
		cell.setCellValue("Error Timestamp");

		cell = row.createCell(1);
		cell.setCellValue("Module");

		cell = row.createCell(2);
		cell.setCellValue("Functionality");

		cell = row.createCell(3);
		cell.setCellValue("Message");

		cell = row.createCell(4);
		cell.setCellValue("Exception");

		cell = row.createCell(5);
		cell.setCellValue("Data");
	}
}