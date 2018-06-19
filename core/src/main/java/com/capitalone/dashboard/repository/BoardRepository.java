package com.capitalone.dashboard.repository;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.ProjectBoard;

/**
 * Repository for {@link Board}.
 */
public interface BoardRepository
		extends CrudRepository<ProjectBoard, ObjectId>, QueryDslPredicateExecutor<ProjectBoard> {

	@Query(value = "{ 'source' : ?0, 'projectId' : ?1}")
	ProjectBoard findByProject(String source, long projectId);

	@Query(value = "{ 'projectId' : ?0 }")
	ProjectBoard findByProjectId(long projectId);

	@Query(value = "{ 'projectId' : ?0, 'boards.boardId' : ?1  }")
	ProjectBoard findByProjectIdAndBoardId(long projectId, long boardId);

	@Query(value = "{ 'projectId' : ?0, 'boards.boardId' : ?1 }")
	Page<ProjectBoard> findByProjectIdAndBoardIdLimited(long projectId, long boardId, Pageable page);
}
