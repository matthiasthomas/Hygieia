package com.capitalone.dashboard.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.capitalone.dashboard.model.GitKpiCollectorItem;

public interface GitKpiRepository extends  PagingAndSortingRepository<GitKpiCollectorItem, ObjectId>{
    @Query(value="{repoCollectorItemId: ?0}")
    GitKpiCollectorItem findByRepoCollectorItemId(ObjectId repoCollectorItemId);
}
