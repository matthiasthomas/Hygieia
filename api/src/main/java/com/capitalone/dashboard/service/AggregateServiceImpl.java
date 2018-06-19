package com.capitalone.dashboard.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.capitalone.dashboard.model.AggregateWidget;
import com.capitalone.dashboard.model.AggregateWidgetType;
import com.capitalone.dashboard.model.BuildAggregateData;
import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CodeQualityAggregateData;
import com.capitalone.dashboard.model.CodeQualityMeasures;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.GitKpiCollectorItem;
import com.capitalone.dashboard.model.GitStats;
import com.capitalone.dashboard.model.ProjectBoard;
import com.capitalone.dashboard.model.ProjectVelocityAggregateData;
import com.capitalone.dashboard.model.QCommit;
import com.capitalone.dashboard.model.RepoAggregateData;
import com.capitalone.dashboard.model.SNAggregatedData;
import com.capitalone.dashboard.model.SNAssignmentGroup;
import com.capitalone.dashboard.model.ServiceNowAggregateData;
import com.capitalone.dashboard.model.VelocityData;
import com.capitalone.dashboard.model.Widget;
import com.capitalone.dashboard.model.jira.RapidViewBoard;
import com.capitalone.dashboard.model.jira.Sprint;
import com.capitalone.dashboard.repository.BoardRepository;
import com.capitalone.dashboard.repository.CodeQualityHistoryRepository;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.repository.GitKpiRepository;
import com.capitalone.dashboard.util.ApplicationDBLogger;
import com.capitalone.dashboard.util.HygieiaConstants;
import com.mysema.query.BooleanBuilder;

@Service
public class AggregateServiceImpl implements AggregateService {
	private static final Log LOG = LogFactory
			.getLog(AggregateServiceImpl.class);
	private final DashboardRepository dashboardRepository;
	private final ComponentRepository componentRepository;
	private final CollectorService collectorService;
	private final BoardRepository boardRepository;
	private final CodeQualityHistoryRepository codeQualityHistoryRepository;
	private final CodeQualityRepository codeQualityRepository;
	private final GitKpiRepository gitKpiRepository;
	private final CommitRepository commitRepository;

	@Autowired
	public AggregateServiceImpl(DashboardRepository dashboardRepository,
			ComponentRepository componentRepository,
			CollectorService collectorService, BoardRepository boardRepository,
			CodeQualityHistoryRepository codeQualityHistoryRepository,
			CodeQualityRepository codeQualityRepository,
			GitKpiRepository gitKpiRepository, CommitRepository commitRepository) {
		this.componentRepository = componentRepository;
		this.collectorService = collectorService;
		this.dashboardRepository = dashboardRepository;
		this.boardRepository = boardRepository;
		this.codeQualityHistoryRepository = codeQualityHistoryRepository;
		this.codeQualityRepository = codeQualityRepository;
		this.gitKpiRepository = gitKpiRepository;
		this.commitRepository = commitRepository;
	}

	private static final Logger LOGGER = Logger
			.getLogger(AggregateServiceImpl.class);

	@Override
	public AggregateWidget getAggregateWidgetByDashboardId(
			ObjectId dashboardId, int offset) {
		// LOGGER.info("In getAggregateWidgetByDashboardId: " + dashboardId);
		AggregateWidget agregateWidgetObj = new AggregateWidget();
		agregateWidgetObj.setDashboardId(dashboardId);
		Map<String, Object> aggregateWidgets = new HashMap<>();

		aggregateWidgets.put(AggregateWidgetType.ProjectVelocity.toString(),
				getBoardVelocityDetail(dashboardId));
		aggregateWidgets.put(AggregateWidgetType.JenkinsBuild.toString(),
				this.getBuildDataForAggregateDashboard(dashboardId));
		aggregateWidgets.put(AggregateWidgetType.CodeQuality.toString(),
				this.getCodeQualityDataForAggregateDashboard(dashboardId));
		aggregateWidgets.put(AggregateWidgetType.Repo.toString(),
				this.getSCMDataForAggregateDashboard(dashboardId, offset));
		aggregateWidgets.put(AggregateWidgetType.ServiceNow.toString(),
				this.getServiceNowDataForAggregateDashboard(dashboardId));
		agregateWidgetObj.setAggregateWidgets(aggregateWidgets);
		return agregateWidgetObj;
	}

	public BuildAggregateData getBuildDataForAggregateDashboard(
			ObjectId dashboardId) {
		BuildAggregateData data = new BuildAggregateData(dashboardId);
		try {
      @SuppressWarnings({"CPD-START"})
			Dashboard dashboard = dashboardRepository.findOne(dashboardId);
			if (!ObjectUtils.isEmpty(dashboard)) {
				List<Widget> wList = dashboard.getWidgets();
				if (!CollectionUtils.isEmpty(wList)) {
					List<ObjectId> componentList = wList
							.stream()
							.filter(widget1 -> "build".equals(widget1.getName()))
							.map(x -> x.getComponentId())
							.collect(Collectors.toList());
					if (!CollectionUtils.isEmpty(componentList)) {
						Component component = componentRepository
								.findOne(componentList.get(0));
						if (component != null) {
              @SuppressWarnings({"CPD-END"})
							List<CollectorItem> collectorItemList = collectorService
									.getCollectorItemForComponent(component
											.getId().toString(),
											CollectorType.Build.toString());
							if (collectorItemList != null) {
								collectorItemList.forEach(item -> data
										.addJob(item));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			ApplicationDBLogger.log(HygieiaConstants.API,
					"AggregateServiceImpl.getBuildDataForAggregateDashboard",
					e.getMessage(), e);
			LOG.error("getBuildDataForAggregateDashboard", e);
		}
		return data;
	}

	public RepoAggregateData getSCMDataForAggregateDashboard(
			ObjectId dashboardId, int offset) {
		RepoAggregateData data = new RepoAggregateData(dashboardId);
		try {
			Dashboard dashboard = dashboardRepository.findOne(dashboardId);
			if (!ObjectUtils.isEmpty(dashboard)) {
				List<Widget> wList = dashboard.getWidgets();
				if (!CollectionUtils.isEmpty(wList)) {
					List<ObjectId> componentList = wList
							.stream()
							.filter(widget1 -> "repo".equals(widget1.getName()))
							.map(x -> x.getComponentId())
							.collect(Collectors.toList());
					if (!CollectionUtils.isEmpty(componentList)) {
						Component component = componentRepository
								.findOne(componentList.get(0));
						if (component != null) {
							// Component SCM will contain all entries for
							// aggregate
							// and ProductSCM is no more used.
							List<CollectorItem> collectorItemList = component
									.getCollectorItems(CollectorType.SCM);
							if (collectorItemList != null) {
								collectorItemList.forEach(item -> data
										.addRepo(getGitKpiItem(item.getId(),
												offset)));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			ApplicationDBLogger.log(HygieiaConstants.API,
					"AggregateServiceImpl.getSCMDataForAggregateDashboard",
					e.getMessage(), e);
			LOG.error("getSCMDataForAggregateDashboard", e);
		} finally {
			if (data.getMetrics().size() > 0) {
				data.setCurrentMonthIfNotPresent();
			}
		}
		return data;
	}

  @SuppressWarnings({"unused", "CPD-START"})
	public CodeQualityAggregateData getCodeQualityHistoryDataForAggregateDashboard(
			ObjectId dashboardId) {
		CodeQualityAggregateData data = null;
		Dashboard dashboard = dashboardRepository.findOne(dashboardId);
		if (!ObjectUtils.isEmpty(dashboard)) {
			data = new CodeQualityAggregateData(dashboardId);
			List<Widget> wList = dashboard.getWidgets();
			if (!CollectionUtils.isEmpty(wList)) {
				List<ObjectId> componentList = wList
						.stream()
						.filter(widget1 -> "codeanalysis".equals(widget1
								.getName())).map(x -> x.getComponentId())
						.collect(Collectors.toList());
				if (!CollectionUtils.isEmpty(componentList)) {
					Component component = componentRepository
							.findOne(componentList.get(0));
					if (component != null) {
						List<CollectorItem> collectorItemList = collectorService
								.getCollectorItemForComponent(component.getId()
										.toString(), CollectorType.ProductSonar
										.toString());
						if (collectorItemList != null) {
							for (CollectorItem collectorItem : collectorItemList) {
								CodeQualityMeasures codeQualityMeasures = codeQualityHistoryRepository
										.findByCollectorItemId(collectorItem
												.getId());
								if (codeQualityMeasures != null) {
									// data.addMetrics(codeQualityMeasures);
								}
							}
						}
					}
				}
			}
		}
		return data;
	}

	public CodeQualityAggregateData getCodeQualityDataForAggregateDashboard(
			ObjectId dashboardId) {
		CodeQualityAggregateData data = new CodeQualityAggregateData(
				dashboardId);
		try {
			Dashboard dashboard = dashboardRepository.findOne(dashboardId);
			if (!ObjectUtils.isEmpty(dashboard)) {
				List<Widget> wList = dashboard.getWidgets();
				if (!CollectionUtils.isEmpty(wList)) {
					List<ObjectId> componentList = wList
							.stream()
							.filter(widget1 -> "codeanalysis".equals(widget1
									.getName())).map(x -> x.getComponentId())
							.collect(Collectors.toList());
					if (!CollectionUtils.isEmpty(componentList)) {
						Component component = componentRepository
								.findOne(componentList.get(0));
						if (component != null) {
							List<CollectorItem> collectorItemList = collectorService
									.getCollectorItemForComponent(component
											.getId().toString(),
											CollectorType.ProductSonar
													.toString());
							if (collectorItemList != null) {
								for (CollectorItem collectorItem : collectorItemList) {
									CodeQuality codeQuality = codeQualityRepository
											.findByCollectorItemId(collectorItem
													.getId());
									if (codeQuality != null) {
										data.addInfoData(codeQuality);
										data.addMetrics(codeQuality);
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			ApplicationDBLogger
					.log(HygieiaConstants.API,
							"AggregateServiceImpl.getCodeQualityDataForAggregateDashboard",
							e.getMessage(), e);
			LOG.error("getCodeQualityDataForAggregateDashboard", e);
		}
		return data;
	}

  @SuppressWarnings({"CPD-END"})
	private GitKpiCollectorItem getGitKpiItem(ObjectId id, int offset) {
		GitKpiCollectorItem gitKpiCollectorItem = gitKpiRepository
				.findByRepoCollectorItemId(id);
		Map<String, GitStats> map = fetchCommitCount(id, 3, offset);
		if (map.size() > 0) {
			gitKpiCollectorItem.setDaywiseCommits(map);
		}
		return gitKpiCollectorItem;
	}

	private Map<String, GitStats> fetchCommitCount(ObjectId id, int months,
			int offsetinMinutes) {
		Map<String, GitStats> map = new HashMap<String, GitStats>();
		QCommit commit = new QCommit("search");
		DateTimeFormatter format = DateTimeFormat.forPattern("yyyyMM")
				.withZone(
						DateTimeZone
								.forOffsetMillis(offsetinMinutes * 60 * 1000));
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(commit.collectorItemId.eq(id));
		long endTimeTarget = new LocalDate().minusMonths(months).toDate()
				.getTime();
		builder.and(commit.scmCommitTimestamp.goe(endTimeTarget));
		List<Commit> lst = (List<Commit>) commitRepository.findAll(builder
				.getValue());
		if (lst != null) {
			String date = "";
			for (Commit cmt : lst) {
				date = format.print(cmt.getScmCommitTimestamp());
				if (map.containsKey(date))
					map.put(date, map.get(date).addCommits());
				else
					map.put(date, GitStats.initiateCommit());
			}
		}
		return map;
	}

	public ProjectVelocityAggregateData getBoardVelocityDetail(
			ObjectId dashboardId) {
		ProjectVelocityAggregateData data = new ProjectVelocityAggregateData(
				dashboardId);
		Map<String, VelocityData> velocityData = new HashMap<String, VelocityData>();
		try {
			Dashboard dashboard = dashboardRepository.findOne(dashboardId);
			if (!ObjectUtils.isEmpty(dashboard)) {
				List<Widget> wList = dashboard.getWidgets();
				String projectId = "";
				String projectName = "";
				String source = "";
				if (!CollectionUtils.isEmpty(wList)) {
					projectId = wList
							.stream()
							.filter(widget1 -> "feature".equals(widget1
									.getName()))
							.map(x -> x.getOptions().get("projectId")
									.toString()).collect(Collectors.joining());
					projectName = wList
							.stream()
							.filter(widget1 -> "feature".equals(widget1
									.getName()))
							.map(x -> x.getOptions().get("projectName")
									.toString()).collect(Collectors.joining());
					source = wList
							.stream()
							.filter(widget1 -> "feature".equals(widget1
									.getName()))
							.filter(x -> x.getOptions().get("source") != null)
							.map(x -> x.getOptions().get("source").toString())
							.collect(Collectors.joining());
				}
				ProjectBoard board = new ProjectBoard();

				// Avoid unnessary exception
				if (projectId != null && !"".equals(projectId)) {
					board = boardRepository.findByProject(source,
							Long.valueOf(projectId));
					if (board != null) {
						SimpleDateFormat formatter = new SimpleDateFormat(
								"yyyyMM");
						List<RapidViewBoard> viewBoards = board.getBoards();
						for (RapidViewBoard rapidViewBoard : viewBoards) {
							List<Sprint> sprints = rapidViewBoard.getSprints();
							for (Sprint sprint : sprints) {
								long estimated = sprint.getEstimated();
								long completed = sprint.getCompleted();
								long completeDate = sprint.getCompleteDate();
								if (completeDate > 0) {
									Date dt = new Date(completeDate);
									String dtStr = formatter.format(dt);
									manipulateData(dtStr, estimated, completed,
											velocityData);
								}
							}
						}
					}
					data.addInfoData(projectName);
					data.addMetrics(velocityData);
				}
			}
		} catch (Exception e) {
			ApplicationDBLogger.log(HygieiaConstants.API,
					"AggregateServiceImpl.getBoardVelocityDetail",
					e.getMessage(), e);
			LOG.error("getBoardVelocityDetail", e);
		} finally {
			if (data.getMetrics().size() > 0) {
				data.setCurrentMonthIfNotPresent();
			}
		}
		return data;
	}

	private void manipulateData(String month, long estimated, long completed,
			Map<String, VelocityData> map) {
		// Accumulate actual, estimate
		if (map.containsKey(month)) {
			VelocityData data = map.get(month);
			data.setMonth(month);
			data.setActual(completed + data.getActual());
			data.setEstimated(estimated + data.getEstimated());
		} else {
			map.put(month, new VelocityData(month, estimated, completed));
		}
	}

	public ServiceNowAggregateData getServiceNowDataForAggregateDashboard(
			ObjectId dashboardId) {
		ServiceNowAggregateData data = new ServiceNowAggregateData(dashboardId);
		try {
			Dashboard dashboard = dashboardRepository.findOne(dashboardId);
			// Map<String, Object> data = new HashMap<String,Object>();
			if (!ObjectUtils.isEmpty(dashboard)) {
				List<Widget> wList = dashboard.getWidgets();
				if (!CollectionUtils.isEmpty(wList)) {
					List<ObjectId> componentList = wList
							.stream()
							.filter(widget1 -> "servicenow".equals(widget1
									.getName())).map(x -> x.getComponentId())
							.collect(Collectors.toList());
					if (!CollectionUtils.isEmpty(componentList)) {
						Component component = componentRepository
								.findOne(componentList.get(0));
						if (component != null) {
							List<CollectorItem> collectorItemList = collectorService
									.getCollectorItemForComponent(component
											.getId().toString(),
											CollectorType.ServiceNow
													.toString());
							if (collectorItemList != null) {
								for (CollectorItem group : collectorItemList) {
									SNAssignmentGroup assignmentGroup = (SNAssignmentGroup) group;
									data.addInfoData(assignmentGroup);
									data.addMetrics(assignmentGroup);
								}
								
							}
						}
					}
				}
			}
		} catch (Exception e) {
			ApplicationDBLogger
					.log(HygieiaConstants.API,
							"AggregateServiceImpl.getServiceNowDataForAggregateDashboard",
							e.getMessage(), e);
			LOG.error("getServiceNowDataForAggregateDashboard", e);
		}
		return data;
	}

}
