package com.capitalone.dashboard.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.GitKpiCollectorItem;
import com.capitalone.dashboard.model.QCommit;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.GitKpiRepository;
import com.capitalone.dashboard.request.GitRequestRequest;
import com.mysema.query.BooleanBuilder;

@Service
public class GitKPIServiceImpl implements GitKPIService {

    private final GitKpiRepository gitKpiRepository;
    private final ComponentRepository componentRepository;
    private final CommitRepository commitRepository;
    
    @Autowired
    public GitKPIServiceImpl(GitKpiRepository gitKpiRepository,
                           ComponentRepository componentRepository,CommitRepository commitRepository) {
        this.gitKpiRepository = gitKpiRepository;
        this.componentRepository = componentRepository;
        this.commitRepository = commitRepository;
    }

    @Override
    public DataResponse<Map<String,Object>> fetchKpi(GitRequestRequest request) {
    	Map<String,Object> results = new HashMap<String,Object>();
    	results.put("contributionPct", 0);
    	results.put("teamSize", 0);
        
        Component component = componentRepository.findOne(request.getComponentId());
        
		// START HYG-152 : Amended to find details based on branch selection, if
		// available.
		CollectorItem item = null;

		if (request.getCollectorItemId() != null) {
			item = component.getCollectorItemForTypeAndId(
					CollectorType.SCM, request.getCollectorItemId());
		} else {
			item = component.getFirstCollectorItemForType(CollectorType.SCM);
		}
		// END HYG-152 : Amended to find details based on branch selection, if
		// available.

        if (item == null) {
            return new DataResponse<>(results, new Date().getTime());
        }
        
        GitKpiCollectorItem gitKpiCollectorItem= gitKpiRepository.findByRepoCollectorItemId(item.getId());
        if(gitKpiCollectorItem!=null)
        {
        	results.put("teamSize", gitKpiCollectorItem.getTeamSize());
        	results.put("team", gitKpiCollectorItem.getTeam());
        	
        	//Get realtime so that no difference in KPI and GRAPH
        	Set distinctAuthors = fetchCommitCount(gitKpiCollectorItem,14);
        	double pct = ((double)distinctAuthors.size() / gitKpiCollectorItem.getTeamSize()) * 100.00;
        	results.put("activeContributors", distinctAuthors);
        	results.put("contributionPct", pct>100?100:pct);
        }
        return new DataResponse<>(results, new Date().getTime());
    }
    
    private Set fetchCommitCount(GitKpiCollectorItem repo, int noOfDays) {
    	Set unique = new TreeSet();    
        QCommit commit = new QCommit("search");        
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(commit.collectorItemId.eq(repo.getRepoCollectorItemId()));
        long endTimeTarget = new LocalDate().minusDays(noOfDays).toDate().getTime();
        builder.and(commit.scmCommitTimestamp.goe(endTimeTarget));
        
        List<Commit> lst = (List<Commit>)commitRepository.findAll(builder.getValue());
        if(lst!=null)
        {        	
        	for(Commit cmt : lst)
        		unique.add(cmt.getScmAuthorLoginFinal());
        }
        return unique;
    }    
  }
