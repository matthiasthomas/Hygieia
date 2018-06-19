package com.capitalone.dashboard.service;

import java.util.List;
import java.util.Map;

public interface ApplicationService {
	List<Map<String ,String>> cleanData(int dataRetentionPeriodMonths);
}
