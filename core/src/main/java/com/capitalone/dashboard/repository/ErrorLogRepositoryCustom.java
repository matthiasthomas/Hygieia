package com.capitalone.dashboard.repository;

import java.util.List;

import com.capitalone.dashboard.model.DateWiseCount;
import com.capitalone.dashboard.model.ModuleWiseCount;

public interface ErrorLogRepositoryCustom {

	int deleteAllCreateDateLessThan(long createDate);

	List<ModuleWiseCount> groupByModule();

	List<DateWiseCount> groupByDate();

}
