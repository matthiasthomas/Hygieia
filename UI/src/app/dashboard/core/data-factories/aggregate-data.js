/**
 * Gets feature related data
 */
(function() {
	'use strict';

	angular.module(HygieiaConfig.module + '.core').factory('aggregateData', aggregateData);

	function aggregateData($http) {
		var testGetDashboardAggregateDataURL = "test-data/aggregate-projects.json";
		var getDashboardAggregateDataURL = "/api/aggregate/aggregatewidgets";

		return {
			getDashboardAggregateData : getDashboardAggregateData
		};

		/**
		 * Get dashboard aggregate data
		 */
		function getDashboardAggregateData(dashboardId,offset) {
			/*
			 * return $http.get(testGetDashboardAggregateDataURL +
			 * "-dashboardId=" + dashboardId).success(
			 */
			return $http.get(
					HygieiaConfig.local ? testGetDashboardAggregateDataURL : getDashboardAggregateDataURL
							+ "?dashboardId=" + dashboardId + "&offset=" +offset ).success(function(response) {
				return response;
			}).error(function(response) {
				return null;
			});
		}
	}
})();
