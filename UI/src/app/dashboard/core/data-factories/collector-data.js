/**
 * Collector and collector item data
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('collectorData', collectorData);

    function collectorData($http, $q) {
        var itemRoute = '/api/collector/item';
        var itemByComponentRoute = '/api/collector/item/component/';
        var itemsByTypeRoute = '/api/collector/item/type/';
        var itemsByTypeAndNameRoute = '/api/collector/item/type/name/';
        var collectorsByTypeRoute = '/api/collector/type/';
        var collectorsByTypeAndNameRoute = '/api/collector/type/name/';
        var encryptRoute = "/api/encrypt/";
        var collectorItemsUpdateRoute = '/api/collector/items/update';
        var collectorItemByOptionsRoute = '/api/collector/itembyoptions';
        var createUpdateCollectorItemRoute = '/api/collector/items/createUpdate';
        var componentItemsByComponentRoute = '/api/collector/item/component/repo/';
        

        return {
            itemsByType: itemsByType,
            itemsByTypeAndName: itemsByTypeAndName,
            createCollectorItem: createCollectorItem,
            getCollectorItem : getCollectorItem,
            collectorsByType: collectorsByType,
            collectorsByTypeAndName: collectorsByTypeAndName,
            encrypt: encrypt,
            getCollectorItemById:getCollectorItemById,
            updateCollectorItems: updateCollectorItems,
            getCollectorItemByOptions: getCollectorItemByOptions,
            createUpdateCollectorItems:createUpdateCollectorItems,
            getComponentCollectorItem:getComponentCollectorItem
        };
        
        function getCollectorItemByOptions(componentRequest) {
            return $http.post(collectorItemByOptionsRoute,componentRequest).then(function (response) {
                return response.data;
            });
        }

        function createUpdateCollectorItems(componentRequest) {
            return $http.post(createUpdateCollectorItemRoute,componentRequest).then(function (response) {
                return response.data;
            });
        }

        function updateCollectorItems(componentRequest) {
            return $http.post(collectorItemsUpdateRoute,componentRequest).then(function (response) {
                return response.data;
            });
        }
        
        function getCollectorItemById(id) {
            return $http.get(itemRoute + '/'+id).then(function (response) {
                return response.data;
            });
        }

        function itemsByType(type, params) {
            return $http.get(itemsByTypeRoute + type, {params: params}).then(function (response) {
                return response.data;
            });
        }
        
        function itemsByTypeAndName(type,name, params) {
            return $http.get(itemsByTypeAndNameRoute + type + '?name=' + name, {params: params}).then(function (response) {
                return response.data;
            });
        }

        function createCollectorItem(collectorItem) {
            return $http.post(itemRoute, collectorItem);
        }


        function getCollectorItem(item, type) {
            return $http.get(itemByComponentRoute + item + '?type=' + type).then(function (response) {
                return response.data;
            });
        }
        
        function getComponentCollectorItem(item, type) {
            return $http.get(componentItemsByComponentRoute + item + '?type=' + type).then(function (response) {
                return response.data;
            });
        }

        function collectorsByType(type) {
            return $http.get(collectorsByTypeRoute + type).then(function (response) {
                return response.data;
            });
        }
        
        function collectorsByTypeAndName(type, name) {
            return $http.get(collectorsByTypeAndNameRoute + type + '?name=' + name).then(function (response) {
                return response.data;
            });
        }

        function encrypt(message) {
            var submitData = {
                message : message
            }
            return $http.post(encryptRoute ,submitData).then(function (response) {
                return response.data;
            });
        }
    }
})();