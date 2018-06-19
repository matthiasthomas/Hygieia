package com.capitalone.dashboard;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.capitalone.dashboard.service.ApplicationService;
import com.capitalone.dashboard.util.ApplicationDBLogger;
import com.capitalone.dashboard.util.HygieiaConstants;

@Component
public class ApplicationDBCleanupJob {
	private static final Log LOG = LogFactory
			.getLog(ApplicationDBCleanupJob.class);
	private final TemplateEngine templateEngine;
	private final JavaMailSender mailSender;
	private final ApplicationService applicationService; 

	@Value(value = "${schedule.mail.recipients}")
	private String receipients;

	@Value(value = "${schedule.mail.from}")
	private String from;
	
	@Value(value = "${schedule.dbcleanup.retentionmonths}")
	private int retentionmonths;

	@Autowired
	public ApplicationDBCleanupJob(TemplateEngine templateEngine,
			JavaMailSender mailSender, ApplicationService applicationService) {
		this.templateEngine = templateEngine;
		this.mailSender = mailSender;
		this.applicationService =applicationService;
	}

	@Scheduled(cron = "${schedule.dbcleanup.cron}")
	private void clearCollections() {
		if(!isLocal())
		{
			LOG.info("Started Schedule for DB Cleanup");
			if(retentionmonths > 1 )
				prepareAndSend(applicationService.cleanData(retentionmonths),"Success");
			else
				prepareAndSend(new ArrayList<Map<String, String>>(),"Failed");
		}
		else			
			LOG.info("Not starting schedule as it is local machine!!!");
	}

	public void prepareAndSend(List<Map<String, String>> data,String status) {
		try {
			MimeMessagePreparator messagePreparator = mimeMessage -> {
				MimeMessageHelper messageHelper = new MimeMessageHelper(
						mimeMessage, true);
				messageHelper.setFrom(from);
				messageHelper.setTo(receipients.split(","));
				messageHelper.setSubject("DB Cleanup Job completed - "
						+ getHostName());
				messageHelper.setText(buildMessage(data,status), true);
			};

			mailSender.send(messagePreparator);
			LOG.info("DB Cleanup Job E-Mail sent successfully");
		} catch (MailException e) {
			LOG.error("DB Cleanup Job , Exception while sending email", e);
			ApplicationDBLogger
					.log(HygieiaConstants.API,
							"ApplicationDBCleanupJob.prepareAndSend",
							"DB Cleanup Job Monitoring , Exception while sending email",
							e);
		}
	}

	public String buildMessage(List<Map<String, String>> data,String status) {
		Context context = new Context();
		context.setVariable("COLLECTION_CLEANUP",data);
		context.setVariable("STATUS",status);

		return templateEngine.process("cleanUpNotificationTemplate", context);
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
					"ApplicationDBCleanupJob.getHostName",
					"Error getting getHostName", e);
		}
		return hostname;
	}
}