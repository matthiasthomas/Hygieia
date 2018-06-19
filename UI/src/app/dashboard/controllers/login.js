/**
 * Controller for performing authentication or signingup a new user */
(function () {
    'use strict';
    var app = angular.module(HygieiaConfig.module)
    var inject = ['$location', '$scope', 'authService', 'userService', 'loginRedirectService']
    function LoginController($location, $scope, authService, userService, loginRedirectService) {
        if (userService.isAuthenticated()) {
            $location.path('/');
            return;
        }
        var login = this;
        login.templateUrl = 'app/dashboard/views/navheader.html';
        login.invalidUsernamePassword = false;
		var protocol = location.protocol;
		var host = location.host;
		var isIE = false || !!document.documentMode;
		var isEdge = !isIE && !!window.StyleMedia;

		authService.getAuthenticationProviders().then(function(response) {
			if (response.data[0] == "IDM") {
				return authService.loginIDM();
			} else {
				$scope.authenticationProviders = response.data;
				$scope.activeTab = response.data[0];
			}
		}).then(function(response2) {
			if(response2 != null && response2 != undefined) {
				if (response2.status == 200) {
					if (isIE || isEdge) {
						window.location = protocol + "//" + host + "/dashboard";
					} else {
						location.reload(true);
					}
				} else if (response2.status == 500) {
					alert("API is not responding...");
				} else if (response2.status == 401) {
					authService.logout();
					location.reload(true);
				} else {
					alert("Error occured");
				}
			}
		});


        $scope.isStandardLogin = function () {
          return $scope.activeTab === "STANDARD";
        }

        $scope.isLdapLogin = function () {
          return $scope.activeTab === "LDAP";
        }

        $scope.showStandard = function () {
          $scope.activeTab = "STANDARD";
        }

        $scope.showLdap = function () {
          $scope.activeTab = "LDAP";
        }

        var signup = function () {
            $location.path('/signup');
        };

        $scope.standardLogin = { name: 'Standard Login', login: authService.login, signup: signup };
        $scope.ldapLogin = { name: 'Ldap Login', login: authService.loginLdap };

    }
    app.controller('LoginController', inject.concat([LoginController]));
})();
