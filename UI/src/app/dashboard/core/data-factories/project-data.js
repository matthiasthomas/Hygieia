/**
 * Gets feature related data
 */
(function() {
	'use strict';

	angular.module(HygieiaConfig.module + '.core').factory('projectData', projectData);

	function projectData($http) {
		var param = '?component=';
		var projectParam = '&projectId=';
		
		var testProjectsRoute = 'test-data/projects.json';
        var buildProjectsRoute = '/api/scope';
		
		var testProjectsByCollectorId = 'test-data/teams.json';
		var buildProjectsByCollectorId = '/api/scopecollector/';
		var buildProjectsByCollectorIdPage = '/api/scopecollector/page/';
		
		var buildProjectVelocityByProjectId = "/api/feature/velocity/project/";

		return {
			projects : projects,
			projectsByCollectorIdPaginated:projectsByCollectorIdPaginated,
			projectVelocityByProjectId : projectVelocityByProjectId
		};
		
		/**
		 * Retrieves projects by  collector ID
		 *
		 * @param collectorId
		 */
		function projectsByCollectorId(collectorId) {
			return $http.get(HygieiaConfig.local ? testProjectsByCollectorId : buildProjectsByCollectorId + collectorId)
				.then(function(response) {
					return response.data;
				});
		}

		/**
		 * Retrieves projects by  collector ID
		 *
		 * @param collectorId
		 */
		function projectsByCollectorIdPaginated(collectorId,params) {
			return $http.get(HygieiaConfig.local ? testProjectsByCollectorId : buildProjectsByCollectorIdPage + collectorId,{params: params})
				.then(function(response) {
					return response.data;
				});
		}


		/**
         * Retrieves all projects
         */      
        function projects() {
            return $http.get(HygieiaConfig.local ? testProjectsRoute : (buildProjectsRoute))
                .then(function (response) {
                    return response.data;
                });
        }

		
		/**
		 * Retrieves all boards of a project
		 */
		function boardsByProjectId (collectorId, filterProjectId) {
			return $http.get(HygieiaConfig.local ? testBoardsRoute : (buildBoardsRoute))
				.then(function (response) {
					return response.data;
				});
		}

		
		/**
		 * Retrieves Project Velocity
		 */
		function projectVelocityByProjectId(filterProjectId) {
			return $http.get(buildProjectVelocityByProjectId + filterProjectId)
				.then(function (response) {
					return response.data;
				});
		}
		
		/**
		 * Retrieves Board Velocity
		 */
		function boardVelocityByBoardId(filterProjectId, viewId) {
			return $http.get(buildBoardVelocityByBoardId + filterProjectId + '/'+ viewId)
				.then(function (response) {
					return response.data;
				});
		}
	}
})();
