/**
 * Chartist.js plugin to display a data label on top of the points in a line chart.
 *
 */
/* global Chartist */
(function (window, document, Chartist) {
    'use strict';

    var defaultOptions = {
    	axisX: {
    			textAnchor: 'middle'
    	}
    };

    Chartist.plugins = Chartist.plugins || {};
    Chartist.plugins.axisLabelIE = function (options) {

        options = Chartist.extend({}, defaultOptions, options);

        return function axisLabelIE(chart) {
            chart.on('draw', function(event) { 
            	// If the draw event is for labels on the x-axis
            	if(event.type === 'label' && event.axis.units.pos === 'x') {
            	// If foreign object is NOT supported, we need to fallback to text-anchor and event.width / 2 offset.
            		if(!chart.supportsForeignObject) {
            			event.element.attr({
            				x: event.x + event.width / 2,
            		        'text-anchor': 'middle',
            		        'text-align': 'center'
            			});
            		}
            	}});
        	};
    };

}(window, document, Chartist));