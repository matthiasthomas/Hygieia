/**
 * Service to handle all token operations
 */
(function() {
	'use strict';

	angular.module(HygieiaConfig.module).service('browserMetaDataService',
			browserMetaDataService);

	function browserMetaDataService() {
		var ua = window.navigator.userAgent;
		var msie = ua.indexOf('MSIE ') > 0;
		var ie11 = ua.indexOf('Trident/') > 0;
		var ie12 = ua.indexOf('Edge/') > 0;
		var isIe = msie || ie11 || ie12;
		
		this.isIE = function() {
			return isIe;
		}

		this.getBrowserInfo = function() {
			var browserInfo = navigator.userAgent;
			var browserFlags = {};

			browserFlags.ISFF = browserInfo.indexOf('Firefox') != -1;
			browserFlags.ISOPERA = browserInfo.indexOf('Opera') != -1;
			browserFlags.ISCHROME = browserInfo.indexOf('Chrome') != -1;
			browserFlags.ISSAFARI = browserInfo.indexOf('Safari') != -1
					&& !browserFlags.ISCHROME;
			browserFlags.ISWEBKIT = browserInfo.indexOf('WebKit') != -1;

			browserFlags.ISIE = browserInfo.indexOf('Trident') > 0
					|| navigator.userAgent.indexOf('MSIE') > 0;
			browserFlags.ISIE6 = browserInfo.indexOf('MSIE 6') > 0;
			browserFlags.ISIE7 = browserInfo.indexOf('MSIE 7') > 0;
			browserFlags.ISIE8 = browserInfo.indexOf('MSIE 8') > 0;
			browserFlags.ISIE9 = browserInfo.indexOf('MSIE 9') > 0;
			browserFlags.ISIE10 = browserInfo.indexOf('MSIE 10') > 0;
			browserFlags.ISOLD = browserFlags.ISIE6 || browserFlags.ISIE7
					|| browserFlags.ISIE8 || browserFlags.ISIE9; // MUST be here

			browserFlags.ISIE11UP = browserInfo.indexOf('MSIE') == -1
					&& browserInfo.indexOf('Trident') > 0;
			browserFlags.ISIE10UP = browserFlags.ISIE10
					|| browserFlags.ISIE11UP;
			browserFlags.ISIE9UP = browserFlags.ISIE9 || browserFlags.ISIE10UP;
			return browserFlags;
		}
	}
})();
