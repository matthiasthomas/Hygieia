/**
 * Chartist.js plugin to display a data label on top of the points in a line chart.
 *
 */
/* global Chartist */
(function (window, document, Chartist) {
    'use strict';

    var defaultOptions = {
        onClick: false
    };

    Chartist.plugins = Chartist.plugins || {};
    Chartist.plugins.ctBarClick = function (options) {

        options = Chartist.extend({}, defaultOptions, options);

        return function ctBarClick(chart) {
            if (chart instanceof Chartist.Bar) {
                chart.on('draw', function(data) {
                	if (data.type === 'bar' && options.onClick) {
                        var node = data.element._node;
                        //node.style.cursor = 'pointer';
                        //console.log(data);
                        node.setAttribute('ct:series-index', data.axisX.ticks[data.index]);
                        node.setAttribute('ct:point-index', data.index);
                        node.addEventListener('click', options.onClick);
                    }
                });
            }
        };
    };

}(window, document, Chartist));