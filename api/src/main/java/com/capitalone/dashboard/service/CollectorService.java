package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface CollectorService {

    /**
     * Finds all Collectors of a given type.
     *
     * @param collectorType collector type
     * @return Collectors matching the specified type
     */
    List<Collector>  collectorsByType(CollectorType collectorType);
    
    /**
     * Finds all Collectors of a given type and Name.
     *
     * @param collectorType collector type
     * @param name Collector name
     * @return Collectors matching the specified type
     */
    List<Collector>  collectorsByTypeAndName(CollectorType collectorType, String name);
    
    /**
     * Finds all CollectorItems of a given type.
     *
     * @param collectorType collector type
     * @return CollectorItems matching the specified type
     */
    List<CollectorItem> collectorItemsByType(CollectorType collectorType);

    /**
     * Finds paged results of CollectorItems of a given type.
     *
     * @param collectorType collector type
     * @param {@link org.springframework.data.domain.Pageable} object to determine which page to return
     * @return CollectorItems matching the specified type
     */
    Page<CollectorItem> collectorItemsByTypeWithFilter(CollectorType collectorType, String descriptionFilter, Pageable pageable);
    
    /**
     * Find a CollectorItem by it's id.
     *
     * @param id id
     * @return CollectorItem
     */
    CollectorItem getCollectorItem(ObjectId id);

    /**
     * Creates a new CollectorItem. If a CollectorItem already exists with the
     * same collector id and options, that CollectorItem will be returned instead
     * of creating a new CollectorItem.
     *
     * @param item CollectorItem to create
     * @return created CollectorItem
     */
    CollectorItem createCollectorItem(CollectorItem item);




    /**
     * Creates a new CollectorItem. If a CollectorItem already exists with the
     * same collector id and niceName, that CollectorItem will be returned instead
     * of creating a new CollectorItem.
     *
     * @param item CollectorItem to create
     * @return created CollectorItem
     */
    CollectorItem createCollectorItemByNiceNameAndJobName(CollectorItem item, String jobName) throws HygieiaException;


    // This is to handle scenarios where the option contains user credentials etc. We do not want to create a new collector item -
    // just update the new credentials.
    CollectorItem createCollectorItemSelectOptions(CollectorItem item, Map<String, Object> allOptions, Map<String, Object> selecOptions);


    /**
     * Creates a new CollectorItem. If a CollectorItem already exists with the
     * same collector id and niceName, that CollectorItem will be returned instead
     * of creating a new CollectorItem.
     *
     * @param item CollectorItem to create
     * @return created CollectorItem
     */
    CollectorItem createCollectorItemByNiceNameAndProjectId(CollectorItem item, String projectId) throws HygieiaException;

    /**
     * Creates a new Collector.
     * @param collector Collector to create
     * @return created Collector
     */
    Collector createCollector(Collector collector);

    /**
     * Finds all CollectorItems of a given class.
     *
     * @param collectorClass collector class
     * @return CollectorItems matching the specified class
     */
    List<CollectorItem> collectorItemsClass(String collectorClass);
    
    /**
     * Finds all CollectorItems of a given class and application name.
     *
     * @param collectorType collector type
     * @param appName Application name
     * @return CollectorItems matching the specified type and application name
     */
    List<CollectorItem> getCollectorItemForComponent(String collectorClass,String appName);
    
    /**
     * Provide all CollectorItems from within Component that matches given component type and component id.
     *
     * @param collectorType collector type
     * @param appName Application name
     * @return CollectorItems of a component with given component type and component id.
     */
    List<CollectorItem> getCollectorItemFromComponent(String id, String type);
	
	 /**
     * Finds all CollectorItems of a given class and application name.
     *
     * @param collectorType collector type
     * @param appName Application name
     * @return CollectorItems matching the specified type and application name
     */
    List<CollectorItem> collectorItemsClassAndAppName(String collectorClass,String appName);

    Page<CollectorItem> collectorItemsByTypeAndNameWithFilter(CollectorType collectorType, String name, String descriptionFilter, Pageable pageable);
    
    List<CollectorItem> updateComponentCollectionItems(ObjectId componentIdObj,CollectorType type,
			List<CollectorItem> collectionItems);
    
    List<CollectorItem> createUpdateComponentCollectionItems(ObjectId componentIdObj,CollectorType type,
			List<CollectorItem> collectionItems);

	CollectorItem getCollectorItemByOptions(CollectorItem item);
}
