package com.capitalone.dashboard;

import java.io.File;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.MutableDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.capitalone.dashboard.model.DateWiseCount;
import com.capitalone.dashboard.model.ErrorLog;
import com.capitalone.dashboard.service.ErrorLogService;
import com.capitalone.dashboard.util.ApplicationDBLogger;
import com.capitalone.dashboard.util.HygieiaConstants;

@Component
public class ApplicationDBLogMonitor {
	private static final Log LOG = LogFactory
			.getLog(ApplicationDBLogMonitor.class);
	private final TemplateEngine templateEngine;
	private final ErrorLogService errorLogService;
	private final JavaMailSender mailSender;

	@Value(value = "${schedule.mail.recipients}")
	private String receipients;

	@Value(value = "${schedule.mail.from}")
	private String from;

	@Autowired
	public ApplicationDBLogMonitor(TemplateEngine templateEngine,
			JavaMailSender mailSender, ErrorLogService errorLogService) {
		this.templateEngine = templateEngine;
		this.errorLogService = errorLogService;
		this.mailSender = mailSender;
	}

	@Scheduled(cron = "${schedule.logmonitoring.cron}")
	private void logMonitoring() {
		if(!isLocal())
		{
			LOG.info("Started Scheduled logMonitoring");
			LOG.info("Error Count : Before Clear Logs : "
					+ errorLogService.groupByModule());
			Date date = new Date();
			cleanLogs(date);
	
			List<DateWiseCount> list = errorLogService.groupByDate();
	
			LOG.info("Error Count : After Clear Logs : "
					+ errorLogService.groupByModule());
	
			MutableDateTime dateTime = new MutableDateTime(date);
			dateTime.addDays(-1);
			List<ErrorLog> lstErrorLog = errorLogService.fetchLogs(dateTime
					.getMillis());
			prepareAndSend(lstErrorLog, list);
		}
		else			
			LOG.info("Not starting schedule as it is local machine!!!");
	}

	public void cleanLogs(Date date) {
		MutableDateTime dateTime = new MutableDateTime(date);
		dateTime.addDays(-7);

		LOG.info("Error Log Records Deleted : "
				+ errorLogService.clearLogs(dateTime.getMillis()));
	}
	
	@SuppressWarnings({"CPD-START"})
	private boolean isLocal() {
		boolean local = true;
		try {
			String hostname = InetAddress.getLocalHost().getHostName();
			hostname = hostname.replaceAll(".spe.sony.com", "").toUpperCase();
			if (hostname == null)
				local = true;
			else if (hostname.indexOf("USPL") >= 0)
				local = false;
			else if (hostname.indexOf("USDL") >= 0)
				local = false;
			else
				local = true;
		} catch (Exception e) {
			local = false;
			LOG.error("Error getting getHostName", e);
			ApplicationDBLogger.log(HygieiaConstants.API,
					"ApplicationDBCleanupJob.getHostName",
					"Error getting getHostName", e);
		} finally {
			return local;
		}
	}

	@SuppressWarnings({"CPD-END"})
	public void prepareAndSend(List<ErrorLog> lstErrorLog,
			List<DateWiseCount> list) {
		File file = exportXls(lstErrorLog);
		try {
			MimeMessagePreparator messagePreparator = mimeMessage -> {
				MimeMessageHelper messageHelper = new MimeMessageHelper(
						mimeMessage, true);
				messageHelper.setFrom(from);
				messageHelper.setTo(receipients.split(","));
				messageHelper
						.setSubject("Hygieia Exceptions for Last 24 Hours - "
								+ getHostName());
				int to = lstErrorLog.size() >= 50 ? 50 : lstErrorLog.size();

				List<ErrorLog> lst = (to == 0 ? lstErrorLog : lstErrorLog
						.subList(0, to - 1));

				messageHelper.setText(buildMessage(lst, list), true);
				if (file != null) {
					FileSystemResource fileResource = new FileSystemResource(
							file);
					messageHelper.addAttachment("ErrorLog.xlsx", fileResource);
				}
			};

			mailSender.send(messagePreparator);
			LOG.info("Error Log Email sent");
		} catch (MailException e) {
			LOG.error(
					"Application Log Monitoring , Exception while sending email",
					e);
			ApplicationDBLogger
					.log(HygieiaConstants.API,
							"ApplicationDBLogMonitor.prepareAndSend",
							"Application Log Monitoring , Exception while sending email",
							e);
			if (file != null && file.exists())
				file.delete();
		}
	}

	public String buildMessage(List<ErrorLog> lstErrorLog,
			List<DateWiseCount> list) {
		Context context = new Context();
		context.setVariable("errorList", lstErrorLog);
		context.setVariable("moduleCountList", list);

		return templateEngine.process("errorLogTemplate", context);
	}

	public File exportXls(List<ErrorLog> lstErrorLog) {
		File file = null;
		XSSFWorkbook workbook = new XSSFWorkbook();
		try {
			XSSFSheet sheet = workbook.createSheet("ErrorLog");

			int rownum = 0;
			Row row = sheet.createRow(rownum++);
			createHeader(row);

			for (ErrorLog errorLog : lstErrorLog) {
				row = sheet.createRow(rownum++);
				createList(errorLog, row);
			}

			file = File.createTempFile("ErrorLog", "xlsx");
			FileOutputStream out = new FileOutputStream(file);
			workbook.write(out);
			out.close();
			workbook.close();
		} catch (Exception e) {
			LOG.error("Error writting to Error Log XLS", e);
			ApplicationDBLogger.log(HygieiaConstants.API,
					"ApplicationDBLogMonitor.exportXls",
					"Error writting to Error Log XLS", e);
		}
		return file;
	}

	@SuppressWarnings({"CPD-START"})
	private void createList(ErrorLog errorLog, Row row)

	{
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
	
	@SuppressWarnings({"CPD-END"})
	private String trunk(String msg)
	{
		if(msg==null)
			return "";
		else if(msg.length() >30000 )
			return msg.substring(0, 30000);
		else 
			return msg;
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

	private String getHostName() {
		String hostname = "UNKNOWN";
		try {
			hostname = InetAddress.getLocalHost().getHostName();
			hostname = hostname.replaceAll(".spe.sony.com", "").toUpperCase();
			if (hostname.indexOf("USPL") >= 0)
				hostname = "PROD-" + hostname;
			else if (hostname.indexOf("USDL") >= 0)
				hostname = "DEV-" + hostname;
		} catch (Exception e) {
			LOG.error("Error getting getHostName", e);
			ApplicationDBLogger.log(HygieiaConstants.API,
					"ApplicationDBLogMonitor.getHostName",
					"Error getting getHostName", e);
		}
		return hostname;
	}
}