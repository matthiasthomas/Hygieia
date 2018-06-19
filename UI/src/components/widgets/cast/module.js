(function() {
	'use strict';

	var widget_state, config = {
		view : {
			defaults : {
				title : 'CAST Code Analysis' // widget title
			},
			controller : 'castViewController',
			controllerAs : 'castView',
			templateUrl : 'components/widgets/cast/view.html'
		},
		config : {
			controller : 'castConfigController',
			controllerAs : 'castConfig',
			templateUrl : 'components/widgets/cast/config.html'
		},
		getState : getState,
		collectors : [ 'CAST' ]
	};

	angular.module(HygieiaConfig.module).config(register);

	register.$inject = [ 'widgetManagerProvider', 'WidgetState' ];
	function register(widgetManagerProvider, WidgetState) {
		widget_state = WidgetState;
		widgetManagerProvider.register('cast', config);
	}

	function getState(widgetConfig) {
		//return widget_state.READY;
		return HygieiaConfig.local || widgetConfig.id ? widget_state.READY
				: widget_state.CONFIGURE;
	}
})();