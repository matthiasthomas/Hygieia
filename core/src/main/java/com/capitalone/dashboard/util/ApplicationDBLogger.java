package com.capitalone.dashboard.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capitalone.dashboard.model.ErrorLog;
import com.capitalone.dashboard.service.ErrorLogService;
import com.google.common.base.Throwables;

/**
 * To log exceptions in DB.
 *
 * @author dpatel4
 *
 */
public class ApplicationDBLogger {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ApplicationDBLogger.class);

	public static void log(String module, String functionality,
			String exceptionMessage, Throwable throwable) {
		try {
			ErrorLogService repo = ApplicationContextProvider
					.getApplicationContext().getBean(ErrorLogService.class);
			ErrorLog log = new ErrorLog();
			log.setException(Throwables.getStackTraceAsString(throwable));
			log.setMessage(exceptionMessage);
			log.setModule(module);
			log.setFunctionality(functionality);
			log.setTimestamp(System.currentTimeMillis());
			repo.save(log);
		} catch (Exception e) {
			LOGGER.error("ApplicationDBLogger:ERROR: Error in DB logger : " + e);
		}
	}

	public static void log(String module, String functionality,
			String exceptionMessage, Throwable throwable, String data) {
		try {
			ErrorLogService repo = ApplicationContextProvider
					.getApplicationContext().getBean(ErrorLogService.class);
			ErrorLog log = new ErrorLog();
			log.setException(Throwables.getStackTraceAsString(throwable));
			log.setModule(module);
			log.setData(data);
			log.setMessage(exceptionMessage);
			log.setFunctionality(functionality);
			log.setTimestamp(System.currentTimeMillis());
			repo.save(log);
		} catch (Exception e) {
			LOGGER.error("ApplicationDBLogger:ERROR: Error in DB logger : " + e);
		}
	}
}