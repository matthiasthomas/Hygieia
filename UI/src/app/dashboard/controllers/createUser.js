/**
 * Controller for the modal popup when creating a new user on the admin page
 */
(function() {
	'use strict';

	angular.module(HygieiaConfig.module).controller('createUserController',
			createUserController);

	createUserController.$inject = [ '$scope', '$location',
			'$uibModalInstance', '$uibModal', 'userInfo', 'userData' ];
	function createUserController($scope, $location, $uibModalInstance,
			$uibModal, userInfo, userData) {
		//console.log("userInfo is ", userInfo);
		var ctrl = this;
		ctrl.isEditMode = false;
		ctrl.userInfo = {};
		ctrl.ROLE_TYPES = [ {
			name : "User",
			type : "ROLE_USER"
		}, {
			name : "Admin",
			type : "ROLE_ADMIN"
		}, {
			name : "Manager",
			type : "ROLE_MANAGER"
		} ];
		
		function setUserRole() {
			ctrl.userInfo.role = ctrl.role.type;
		}
		
		function setUpdateUserInfoObject() {
			if (userInfo) {
				ctrl.isEditMode = true;
				ctrl.widgetTitle = "Update User Details";
				ctrl.userInfo = {
					id : userInfo.id,
					username : userInfo.username,
					authType : userInfo.authType,
					firstName : userInfo.firstName,
					lastName : userInfo.lastName,
					emailAddress : userInfo.emailAddress
				}
				if (userInfo.middleName) {
					ctrl.userInfo.middleName = userInfo.middleName;
				}

				if (userInfo.authorities[0] == "ROLE_USER") {
					ctrl.role = {
						name : "User",
						type : "ROLE_USER"
					};
				} else if (userInfo.authorities[0] == "ROLE_MANAGER") {
					ctrl.role = {
						name : "Manager",
						type : "ROLE_MANAGER"
					};
				} else {
					ctrl.role = {
						name : "Admin",
						type : "ROLE_ADMIN"
					};
				}
				setUserRole();
			} else {
				ctrl.widgetTitle = "Create User";
				ctrl.userInfo["authType"] = "IDM";
			}
		}

		setUpdateUserInfoObject();

		function createUser() {
			if (ctrl.cuf.$invalid) {
				ctrl.cuf.$setSubmitted(true)
				return;
			}
			userData.createUser(ctrl.userInfo).then(function(response) {
				$uibModalInstance.close("success");
			}, function(response) {
				$uibModalInstance.dismiss(response);
			});
		}

		function updateUser() {
			if (ctrl.cuf.$invalid) {
				ctrl.cuf.$setSubmitted(true)
				return;
			}
			userData.updateUser(ctrl.userInfo).then(function(response) {
				$uibModalInstance.close("success");
			}, function(response) {
				$uibModalInstance.dismiss(response);
			});
		}

		ctrl.setUserRole = setUserRole;
		ctrl.createUser = createUser;
		ctrl.updateUser = updateUser;
	}
})();