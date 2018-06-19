(function () {
	'use strict';

	angular.module(HygieiaConfig.module).controller('castViewController',
		castViewController);

	castViewController.$inject = ['$scope', 'codeAnalysisData',
		'testSuiteData', '$q', '$filter', '$uibModal'];
	function castViewController($scope, codeAnalysisData, testSuiteData, $q,
		$filter, $uibModal) {

		var ctrl = this;

		ctrl.threshold = {
			'0': { color: '#FF5733' },
			'2': { color: '#DE8F07' },
			'3': { color: '#B5F247' }
		};


		ctrl.load = function () {
			var caRequest = {
				componentId: $scope.widgetConfig.componentId,
				type: 'CAST',
				max: 1
			};

			return $q.all([codeAnalysisData.staticDetails(caRequest).then(
				processCaResponse)]);
		};

		function coveragePieChart(lineCoverage) {
			lineCoverage.value = lineCoverage.value || 0;

			ctrl.unitTestCoverageData = {
				series: [lineCoverage.value, (100 - lineCoverage.value)]
			};
		}

		function processCaResponse(response) {
			var deferred = $q.defer();
			var caData = _.isEmpty(response.result) ? {} : response.result[0];

			ctrl.url = caData.url;
			ctrl.projName = caData.name;

			ctrl.qualities = {
				risk: getMetric(caData.metrics, 'risk', 'Risk Index'),
				maintainability: getMetric(caData.metrics, 'maintainability', 'Maintainability Index'),
				critical_total: getMetric(caData.metrics, 'critical_total', 'Critical Violations Count'),
				critical_added: getMetric(caData.metrics, 'critical_added', 'Added '),
				critical_removed: getMetric(caData.metrics, 'critical_removed', 'Removed')
			};

			deferred.resolve(response.lastUpdated);
			return deferred.promise;
		}

		function getMetric(metrics, metricName, title) {
			title = title || metricName;
			return angular.extend((_.find(metrics, {
				name: metricName
			}) || {
					name: title
				}), {
					name: title
				});
		}

	}
})();
