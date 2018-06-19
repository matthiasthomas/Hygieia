package com.capitalone.dashboard.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.model.DateWiseCount;
import com.capitalone.dashboard.model.ErrorLog;
import com.capitalone.dashboard.model.ModuleWiseCount;
import com.capitalone.dashboard.repository.ErrorLogRepository;

@Service
public class ErrorLogServiceImpl implements ErrorLogService {
	
	private final ErrorLogRepository errorLogRepository; 
	
    @Autowired
    public ErrorLogServiceImpl(ErrorLogRepository errorLogRepository) {
        this.errorLogRepository = errorLogRepository;
    }

	public List<ErrorLog>  fetchLogs(long timestamp)
	{
		return errorLogRepository.findByTimestampGreaterThan(timestamp);
	}

	@Override
	public int clearLogs(long timestamp) {
		return errorLogRepository.deleteAllCreateDateLessThan(timestamp);
	}
	
	public void save(ErrorLog errorLog)
	{
		errorLogRepository.save(errorLog);
	}

	@Override
	public List<ModuleWiseCount> groupByModule() {		
		return errorLogRepository.groupByModule();
	}
	
	@Override
	public List<DateWiseCount> groupByDate() {		
		return errorLogRepository.groupByDate();
	}
}
