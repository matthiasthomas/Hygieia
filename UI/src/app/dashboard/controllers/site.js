/**
 * Controller for choosing or creating a new dashboard
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('SiteController', SiteController);

    SiteController.$inject = ['$scope', '$stateParams', '$q', '$uibModal', 'dashboardData', '$location', 'DashboardType', 'userService', 'authService', 'dashboardService','__env'];
    function SiteController($scope, $stateParams, $q, $uibModal, dashboardData, $location, DashboardType, userService, authService, dashboardService, env) {
        var ctrl = this;
        var selectedTabIndex = !isNaN($stateParams.tabIndex) ? (+$stateParams.tabIndex) : undefined; 

        // public variables
        ctrl.search = '';
        ctrl.myadmin = '';

        ctrl.username = userService.getUsername();
        ctrl.authType = userService.getAuthType();
        ctrl.showAuthentication = userService.isAuthenticated();
		ctrl.isViewOnly = true; 
		ctrl.showCreate = false;
		ctrl.isManager = false;

        ctrl.templateUrl = 'app/dashboard/views/navheader.html';
        ctrl.dashboardTypeEnum = DashboardType;

        // public methods
        ctrl.createDashboard = createDashboard;
        ctrl.deleteDashboard = deleteDashboard;
        ctrl.manageTemplates = manageTemplates;
        ctrl.open = open;
        ctrl.login = login;
        ctrl.logout = logout;
        ctrl.admin = admin;
        ctrl.setType = setType;
        ctrl.showType = showType; 
        ctrl.filterNotOwnedList = filterNotOwnedList;
        ctrl.filterDashboards = filterDashboards;
        ctrl.editDashboard = editDashboard;
        ctrl.getInvalidAppOrCompError = getInvalidAppOrCompError;
        /*ctrl.selecedDashboard = {
            "name": "All dashboards"
        };*/
        
        if (userService.isAdmin()) {
            ctrl.myadmin = true;
			ctrl.isViewOnly = false;
			ctrl.showCreate = true;
        } else if (userService.isManager()) {
			ctrl.isViewOnly = false;
			//HYG-96
			ctrl.showCreate = true;
        }
		else
		{
			ctrl.isViewOnly = true;
			ctrl.showCreate = false;
		}

        ctrl.isManager=userService.isManager();
        
        (function() {
            // set up the different types of dashboards with a custom icon
            var types = dashboardData.types();
            ctrl.dashboardTypes = types;
            pullDashboards();
            if(selectedTabIndex){
                setType(ctrl.dashboardTypes[selectedTabIndex - 1].id);
                ctrl.selecedDashboard = ctrl.dashboardTypes[selectedTabIndex - 1];
            }
            _(types).forEach(function (item) {
                /*if(item.id == DashboardType.PRODUCT) {
                    item.icon = 'fa-cubes';
                } else */if(item.id == DashboardType.AGGREGATE) {
                    item.icon = 'fa-snowflake-o';
                }
            });
        })();

        function setType(type) {
            ctrl.dashboardType = type;
        }
        
        function showType() {
        	if(ctrl.selecedDashboard) {
            	_(ctrl.dashboardTypes).forEach(function (object, i) {
                    if(object.id == ctrl.selecedDashboard.id) {
                    	//$location.path("#/tab/" + (i + 1));
                    	window.location.href = "#/tab/" + (i + 1);
                    	return;
                    }
                });
        	} else {
        		//$location.path("#/");
        		window.location.href = "#/";
        	}
        }

        function filterDashboards(item) {
            var matchesSearch = (!ctrl.search || item.name.toLowerCase().indexOf(ctrl.search.toLowerCase()) !== -1);
            /*if (ctrl.dashboardType == DashboardType.PRODUCT) {
                return item.isProduct && !item.isAggregate && matchesSearch;
            }*/

            if (ctrl.dashboardType == DashboardType.TEAM) {
                return /*!item.isProduct &&*/ !item.isAggregate && matchesSearch;
            }

            if (ctrl.dashboardType == DashboardType.AGGREGATE) {
                return item.isAggregate && /*!item.isProduct && */matchesSearch;
            }

            return matchesSearch;
        }

        function admin() {
            console.log('sending to admin page');
            $location.path('/admin');
        }

        function login() {
          $location.path('/login');
        }

        function logout() {
            authService.logout();
            if(ctrl.authType == "IDM")
            {
                var protocol=location.protocol;
                var host=location.host;
                window.location.href = protocol + "//" + host + "/logout.html"
            } else {
            	$location.path('/login');
            }
        }

        // method implementations
        function createDashboard() {
            // open modal for creating a new dashboard
            $uibModal.open({
                templateUrl: 'app/dashboard/views/createDashboard.html',
                controller: 'CreateDashboardController',
                controllerAs: 'ctrl'
            });
        }

        function editDashboard(item,size)
        {
            // open modal for renaming dashboard
            var modalInstance = $uibModal.open({
                templateUrl: 'app/dashboard/views/editDashboard.html',
                controller: 'EditDashboardController',
                controllerAs: 'ctrl',
                size:size,
                resolve: {
                    dashboardItem: function() {
                        return item;
                    }
                }
            });
            modalInstance.result.then(function success() {
                pullDashboards()
            });
        }

        function manageTemplates() {
            $location.path('/templates');
        }

        function open(id, name) {
            logAnalytics('browse', 'Dashboard-' + name, name);       
            $location.path('/dashboard/' + id);
        }

        function processDashboardResponse(data) {
            // add dashboards to list
            ctrl.dashboards = [];
            var dashboards = [];
            for (var x = 0; x < data.length; x++) {
                var board = {
                    id: data[x].id,
                    name: dashboardService.getDashboardTitle(data[x]),
                    /*isProduct: data[x].type && data[x].type.toLowerCase() === DashboardType.PRODUCT.toLowerCase(),*/
                    isAggregate: data[x].type && data[x].type.toLowerCase() === DashboardType.AGGREGATE.toLowerCase()
                };

                /*if(board.isProduct) {
                    //console.log(board);
                }*/
                dashboards.push(board);
            }

            ctrl.dashboards = dashboards;
        }

        function processDashboardError(data) {
            ctrl.dashboards = [];
        }

        function processMyDashboardResponse(mydata) {

            // add dashboards to list
            ctrl.mydash = [];
            var dashboards = [];
            for (var x = 0; x < mydata.length; x++) {

                dashboards.push({
                    id: mydata[x].id,
                    name: dashboardService.getDashboardTitle(mydata[x]),
                    type: mydata[x].type,
                    /*isProduct: mydata[x].type && mydata[x].type.toLowerCase() === DashboardType.PRODUCT.toLowerCase(),*/
                    isAggregate: mydata[x].type && mydata[x].type.toLowerCase() === DashboardType.AGGREGATE.toLowerCase(),
                    validServiceName:  mydata[x].validServiceName,
                    validAppName: mydata[x].validAppName,
                    configurationItemBusServName:  mydata[x].configurationItemBusServName,
                    configurationItemBusAppName:  mydata[x].configurationItemBusAppName,
                    configurationItemBusServId:  mydata[x].configurationItemBusServObjectId,
                    configurationItemBusAppId:  mydata[x].configurationItemBusAppObjectId,
                    showError: ctrl.getInvalidAppOrCompError(mydata[x])
                });
            }

            ctrl.mydash = dashboards;
        }

        function processMyDashboardError(data) {
            ctrl.mydash = [];
        }




        function deleteDashboard(item) {
            var id = item.id;
            dashboardData.delete(id).then(function () {
                _.remove(ctrl.dashboards, {id: id});
                _.remove(ctrl.mydash, {id: id});
            }, function(response) {
                var msg = 'An error occurred while deleting the dashboard';

                if(response.status > 204 && response.status < 500) {
                    msg = 'The Team Dashboard is currently being used by a Product Dashboard/s. You cannot delete at this time.';
                }

                swal(msg);
            });
        }

        function filterNotOwnedList(db1, db2) {

            console.log("size before is:" + db1.length);

            var jointArray = db1.concat(db2);

            console.log("size after is:" + jointArray.length);

            var uniqueArray = jointArray.filter(function (elem, pos) {
                return jointArray.indexOf(elem) == pos;
            });

            console.log("size after reduction  is:" + uniqueArray.length);
            ctrl.dashboards = uniqueArray;
        }
        function getInvalidAppOrCompError(data){
            var showError = false;

            if((data.configurationItemBusServName != undefined && !data.validServiceName) || (data.configurationItemBusAppName != undefined && !data.validAppName)){
                showError = true;
            }
            return showError;
        }
        
    	function gtag(){dataLayer.push(arguments);}
    	
        function pullDashboards(){
            // request dashboards
            dashboardData.search(ctrl.username,ctrl.authType).then(processDashboardResponse, processDashboardError);
            
            logAnalytics('browse', 'landing_page', 'landing_page');            
            // request my dashboards
            dashboardData.mydashboard(ctrl.username).then(processMyDashboardResponse, processMyDashboardError);
        }
        
        function logAnalytics(action, category, eventLabel) {
        	if(env.analyticsTagID != '') {
	            gtag('config', env.analyticsTagID, {'user_id': ctrl.username});
	    	    //gtag('set', {'userid': ctrl.username});
	            gtag('event', action, {
	            	'event_category': category,
	                'event_label': eventLabel
	            });
        	}
        }
    }

})();
