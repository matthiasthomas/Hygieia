(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('userData', userData);

    function userData($http) {
        var testDetailRoute = 'test-data/signup_detail.json';
        var adminRoute = '/api/admin';
        var userRoute = '/api/users';

        //To get all users based on pagination parameters
        var userSearchWithPaginationRoute ="/api/users/search/";

        //To create a user
        var createUserRoute = "/api/admin/users/createUser";

        //To update a user
        var updateUserRoute = "/api/admin/users/updateUser";

        //To delete an existing user
        var deleteUserRoute = "/api/admin/users/deleteUser";



        return {
            getAllUsers: getAllUsers,
            promoteUserToAdmin: promoteUserToAdmin,
            demoteUserFromAdmin: demoteUserFromAdmin,
            createToken: createToken,
            apitokens: apitokens,
            deleteToken: deleteToken,
            updateToken: updateToken,
            searchWithPagination: searchWithPagination,
            createUser: createUser,
            updateUser: updateUser,
            deleteUser: deleteUser
        };


        // reusable helper
        function getPromise(route) {
            return $http.get(route).then(function (response) {
              console.log("Data="+ JSON.stringify(response.data));
                return response.data;
            });
        }

      function getAllUsers(){

          if(HygieiaConfig.local)
          {
            console.log("In local testing");
            return getPromise(testDetailRoute);
          }
          else
          {
        return $http.get(userRoute);
      }
    }

    function promoteUserToAdmin(user) {
        var route = adminRoute + "/users/addAdmin";
        return $http.post(route, user);
    }

    function demoteUserFromAdmin(user) {
      var route = adminRoute + "/users/removeAdmin";
      return $http.post(route, user);
    }

    function createToken(apitoken) {
        var route = adminRoute + "/createToken";
        return $http.post(route, apitoken);
    }

    function apitokens() {
        var route = adminRoute + "/apitokens";
        return $http.get(route);
    }

    function deleteToken(id) {
        var route = adminRoute + "/deleteToken";
        return $http.delete(route + '/' + id)
            .then(function (response) {
                return response.data;
            });
    }
    function updateToken(apiToken, id) {
        var route = adminRoute + "/updateToken";
        return $http.post(route + '/' + id, apiToken);
    }
    
    function searchWithPagination(page, pagesize, username) {
    	if(!username) {
    		username = "";
    	}
    	//To get all users based on pagination parameter
        // /api/users/search/{page}/{pagesize}/
        //To get users based on searched username and pagination parameter
        // /api/users/search/{page}/{pagesize}/{username}
        var route = userSearchWithPaginationRoute + page + "/" + pagesize + "/" + username;
        return $http.get(route);
    }
    
    function createUser(userInfo) {
    	return $http.put(createUserRoute, userInfo);
    }
    function updateUser(userInfo) {
    	return $http.put(updateUserRoute, userInfo);
    }
    function deleteUser(userId) {
    	return $http.delete(deleteUserRoute + "/" + userId);
    }
  }
})();
