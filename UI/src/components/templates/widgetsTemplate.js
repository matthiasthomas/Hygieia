/**
 * Controller for the Widget Managed template.
 *
 */
(function () {
    'use strict';
    angular
        .module(HygieiaConfig.module)
        .controller('WidgetTemplateController', WidgetTemplateController);

    WidgetTemplateController.$inject = ['$scope', '$window', '$uibModal', 'DashboardType', 'userService', 'dashboardData', 'dashboardService'];
    function WidgetTemplateController($scope, $window, $uibModal, DashboardType, userService, dashboardData, dashboardService) {
        var ctrl = this;
        ctrl.tabs = [
            {name: "Widget"},
            {name: "Pipeline"},
            {name: "Cloud"}
        ];
        ctrl.pipelineInd = false;
        ctrl.cloudInd = false;
        ctrl.widgetView = ctrl.tabs[0].name;
        ctrl.toggleView = function (index) {
            ctrl.widgetView = typeof ctrl.tabs[index] === 'undefined' ? ctrl.tabs[0].name : ctrl.tabs[index].name;
        };

        $scope.init = function (dashboard) {
            ctrl.sortOrder = [];
            var widgetObj = {};
            ctrl.widgets = dashboard.activeWidgets;
            _(ctrl.widgets).forEach(function (widget) {
                if (widget == 'pipeline') {
                    ctrl.pipelineInd = true;
                } else if (widget == 'cloud') {
                    ctrl.cloudInd = true;
                } else {
                    if (widget == 'codeanalysis') {
                        widgetObj[widget] = 'Code Quality - SonarQube';
                    } else if (widget === 'performance') {
                        widgetObj[widget] = 'Performance Analysis';
                    } else if (widget === 'functionaltest') {
                        widgetObj[widget] = 'Functional Testing';
                    } else if (widget === 'unittest') {
                        widgetObj[widget] = 'Unit Testing';
                    } else if (widget === 'integrationtest') {
                        widgetObj[widget] = 'Integration Testing';
                    }else {
                        widgetObj[widget] = getDisplayName(widget);
                    }
                }

            });
            ctrl.widgetDisplay = widgetObj;
            _.each(ctrl.widgetDisplay, function (val, key) {
                ctrl.sortOrder.push(key);
            });
            ctrl.widgetsOrder = chunk(ctrl.sortOrder, 3);
            if (ctrl.pipelineInd === false) {
                for (var i = 0; i < ctrl.tabs.length; i++)
                    if (ctrl.tabs[i].name === "Pipeline") {
                        ctrl.tabs.splice(i, 1);
                        break;
                    }
            }
            if (ctrl.cloudInd === false) {
                for (var i = 0; i < ctrl.tabs.length; i++)
                    if (ctrl.tabs[i].name === "Cloud") {
                        ctrl.tabs.splice(i, 1);
                        break;
                    }
            }
            ctrl.CurrentDashboardId = dashboard.id;
        };

        // break array into chunk of 3
        function chunk(arr, chunkSize) {
            var returnArray = [];
            for (var i = 0, len = arr.length; i < len; i += chunkSize)
                returnArray.push(arr.slice(i, i + chunkSize));
            return returnArray;
        }

        //get display name in camel case
        function getDisplayName(title) {
            return title.charAt(0).toUpperCase() + title.slice(1);
        }

        ctrl.isOwner = false;
        ctrl.username = userService.getUsername();
        ctrl.isAdmin = userService.isAdmin();
        ctrl.isManager = userService.isManager();
        ctrl.getInvalidAppOrCompError = getInvalidAppOrCompError;
        ctrl.editDashboard = editDashboard;
        dashboardData.mydashboard(ctrl.username).then(processMyDashboardResponse, processMyDashboardError);
       

        function processMyDashboardResponse(mydata) {
        	//ctrl.isOwner
            // add dashboards to list
            //ctrl.mydashboards = [];
        	ctrl.mydashboard = null;
            var dashboards = [];
			
            for (var x = 0; x < mydata.length; x++) {
            	
            	if(mydata[x].id === ctrl.CurrentDashboardId) {
            		ctrl.isOwner =  true;
            		setData(mydata[x]);
            		return;
            	}
            	
            }
            if (ctrl.isAdmin && ctrl.isOwner==false) {
				dashboardData.detail(ctrl.CurrentDashboardId).then(setData);
				return;
			}

        }

        function processMyDashboardError(data) {
            ctrl.mydashboards = [];
        }

        function getInvalidAppOrCompError(data){
            return false;
        }
        
        function editDashboard()
        {
            // open modal for renaming dashboard
            var modalInstance = $uibModal.open({
                templateUrl: 'app/dashboard/views/editDashboard.html',
                controller: 'EditDashboardController',
                controllerAs: 'ctrl',
                size: 'lg',
                resolve: {
                    dashboardItem: function() {
                    	return ctrl.mydashboard;
                    }
                }
            });
            modalInstance.result.then(function success(result) {
                if(result === 'success')
                $window.location.reload();
            });
        }
        function setData(mydata) {
        	ctrl.mydashboard = {
                    id: mydata.id,
                    name: dashboardService.getDashboardTitle(mydata),
                    type: mydata.type,
                    /*isProduct: mydata.type && mydata.type.toLowerCase() === DashboardType.PRODUCT.toLowerCase(),*/
                    validServiceName:  mydata.validServiceName,
                    validAppName: mydata.validAppName,
                    configurationItemBusServName:  mydata.configurationItemBusServName,
                    configurationItemBusAppName:  mydata.configurationItemBusAppName,
                    configurationItemBusServId:  mydata.configurationItemBusServObjectId,
                    configurationItemBusAppId:  mydata.configurationItemBusAppObjectId,
                    showError: ctrl.getInvalidAppOrCompError(mydata)
                };
        }
    }
})();



