/**
 * Build widget configuration
 */
(function() {
	'use strict';

	angular.module(HygieiaConfig.module).controller('castConfigController',
			castConfigController);

	castConfigController.$inject = [ 'modalData', '$uibModalInstance',
			'collectorData', '$http', '$scope' ];
	function castConfigController(modalData, $uibModalInstance, collectorData,
			$http, $scope) {
		var ctrl = this;
		var widgetConfig = modalData.widgetConfig;

		ctrl.collectors = [];
		ctrl.collectorItems = [];
		ctrl.serviceNowOption = "";
		ctrl.unittestOptions = [];
		ctrl.submitted = false;
		ctrl.submit = submitForm;
		ctrl.submitAdd = submitFormAdd;
		ctrl.submitRemoveItem = submitRemoveItem;
		ctrl.submitProduct = submitProductConfig;

		ctrl.tabs = [ {
			name : "Team"
		}
		/* Disabled Product tab for now , {
			name : "Product"
		} */
		];
		ctrl.tabView = ctrl.tabs[0].name;
		ctrl.tabToggleView = tabToggleView;

		ctrl.clearWidget = clearWidget;
		ctrl.isWidgetConfigured = isWidgetConfigured;

		function isWidgetConfigured() {
			if (widgetConfig.id)
				return true;
			return false;
		}

		function clearWidget() {
			var postObj = {
				clear : true,
				collectorType : [ "CAST" ],
				name : "CAST",
				options : {},
				componentId : modalData.dashboard.application.components[0].id,
				collectorItemIds : null
			};

			// pass this new config to the modal closing so it's saved
			$uibModalInstance.close(postObj);
		}

		function tabToggleView(index) {
			ctrl.dupErroMessage = "";
			ctrl.tabView = typeof ctrl.tabs[index] === 'undefined' ? ctrl.tabs[0].name
					: ctrl.tabs[index].name;
		}
		;
		$scope.getCodeQualityJobs = function(filter) {
			//console.log(filter)
			return collectorData.itemsByTypeAndName('CAST', 'CAST', {
				"search" : filter,
				"sort" : "description",
				"size" : 20
			}).then(function(response) {
				return response;
			});
		}

		//START: Support for Product Tab
		$scope.getCollectionItemsByType = function() {
			return collectorData.getCollectorItem(
					modalData.dashboard.application.components[0].id,
					'ProductCAST').then(function(response) {
				return response;
			});
		}

		$scope.updateProductItems = function(component) {
			return collectorData.updateCollectorItems(component).then(
					function(response) {
						return response;
					});
		}
		//END: Support for Product Tab

		loadSavedCodeAnalysisJob();

		// method implementations
		function loadSavedCodeAnalysisJob() {
			ctrl.jobName = "";
			var collector = modalData.dashboard.application.components[0].collectorItems.CAST, savedCollector = collector ? collector[0].description
					: null;
			//console.log(savedCollector)
			if (savedCollector) {
				ctrl.jobName = collector[0].id;
				$scope.getCodeQualityJobs(savedCollector).then(getCallback)
			}
		}

		function getCallback(data) {
			_(data).forEach(function(item) {
				if (item.id == ctrl.jobName) {
					ctrl.collectorItemId = item;
				}
			});
		}

		// Request collectors
		collectorData.collectorsByTypeAndName('CAST', 'CAST').then(
				processCollectorsResponse);

		function processCollectorsResponse(data) {
			ctrl.collectors = data;
		}

		function submitForm(collectoritem) {
			ctrl.submitted = true;
			var postObj = {
				name : 'CAST',
				options : {
					id : widgetConfig.options.id,
					name : collectoritem.description
				},
				componentId : modalData.dashboard.application.components[0].id,
				collectorItemId : collectoritem.id
			};

			// pass this new config to the modal closing so it's saved
			$uibModalInstance.close(postObj);
		}

		function createCollectorItem() {
			var item = {};
			var collectorId;

			collectorId = _.find(ctrl.collectors, {
				name : 'CAST'
			}).id
			item = createItemFromSelect(collectorId)

			return collectorData.createCollectorItem(item);
		}

		function createItemFromSelect(collectorId) {
			return {
				collectorId : collectorId,
				description : ctrl.collectorId.description,
				options : {
					projectUrl : ctrl.collectorId.projectUrl,
					projectName : ctrl.collectorId.projectName
				}
			}
		}

		//START: Support for Product Tab
		loadCollectionItemsByType("ProductCAST");
		function loadCollectionItemsByType(cType) {
			$scope.getCollectionItemsByType(cType).then(
					getCollectionItemsCallback);
		}

		function getCollectionItemsCallback(data) {
			ctrl.itemList = [];
			for (var x = 0; x < data.length; x++) {
				var obj = data[x];
				var item = {
					id : obj.id,
					description : obj.description,
					collectorId : obj.collectorId
				};
				ctrl.itemList.push(item);

			}
		}

		function submitFormAdd(collectorItem) {
			if (collectorItem && collectorItem.id) {
				if (!ctrl.itemList)
					ctrl.itemList = [];

				ctrl.itemList.push(collectorItem);
				ctrl.addCollectorItemId = "";
			}
		}

		function submitRemoveItem(collectorItem) {
			if (collectorItem && collectorItem.id) {
				var index = ctrl.itemList.indexOf(collectorItem);
				ctrl.itemList.splice(index, 1);
			}
		}

		function submitProductConfig(collectoritem) {
			var componentId = modalData.dashboard.application.components[0].id;

			var postItems = [];
			for ( var i in ctrl.itemList) {
				var item = ctrl.itemList[i];
				var obj = {
					"id" : item.id,
					collectorId : item.collectorId,
					description : item.description,
					options : {
						projectUrl : item.projectUrl,
						projectName : item.projectName
					}
				}

				postItems.push(obj);
			}

			var postObj = {
				componentId : componentId,
				collectorType : "ProductCAST",
				collectorItems : postItems
			}
			$scope.updateProductItems(postObj);
			$uibModalInstance.close();
		}
		//END: Support for Product Tab

	}
})();