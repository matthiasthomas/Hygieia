package com.capitalone.dashboard.service;

import java.util.List;

import com.capitalone.dashboard.model.DateWiseCount;
import com.capitalone.dashboard.model.ErrorLog;
import com.capitalone.dashboard.model.ModuleWiseCount;

public interface ErrorLogService {
	List<ErrorLog>  fetchLogs(long timestamp);
	int clearLogs(long timestamp);
	void save(ErrorLog errorLog);
	List<ModuleWiseCount> groupByModule();
	List<DateWiseCount> groupByDate();
}
