/**
 * Authorization interceptor for adding token to outgoing requests, and handling error responses
*/
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .factory('authInterceptor', authInterceptor);

    authInterceptor.$inject = ['$q', '$location', 'tokenService','config'];
    function authInterceptor($q, $location, tokenService, config) {
      return {
    	  	// IDM SiteMinder Change
			request : function(config) {
				//var aHeaders = JSON.stringify(config.headers);
				//console.log("Inside header Interceptor" + aHeaders);
				return config;
			},
			requestError : function(config) {
				return config;
			},

			response : function(res) {
				return res;
			},
    	  
        responseError: function (response) {
          if (response.status === 401) {
            $location.path('/login');
          }
          return $q.reject(response);
        }
      };
    }
})();