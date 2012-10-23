package org.openmrs.module.reportingobjectgroup.objectgroup.persister;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.reportingobjectgroup.objectgroup.ObjectGroup;
import org.openmrs.module.reportingobjectgroup.objectgroup.definition.ObjectGroupDefinition;
import org.openmrs.module.reportingobjectgroup.serializer.ReportingObjectGroupSerializedDefinitionService;




/**
 * This class provides access to persisted {@link ObjectGroup}s, 
 * and exposes them as a {@link ObjectGroupDefinition}
 */
@Handler(supports={ObjectGroupDefinition.class},order=100)
public class SerializedObjectGroupDefinitionPersister implements ObjectGroupDefinitionPersister{
	
protected static Log log = LogFactory.getLog(SerializedObjectGroupDefinitionPersister.class);
	
    //****************
    // Constructor
    //****************
	protected SerializedObjectGroupDefinitionPersister() { }
	
    //****************
    // Instance methods
    //****************
	
	/**
	 * Utility method that returns the SerializedDefinitionService
	 */
	public ReportingObjectGroupSerializedDefinitionService getService() {
		return Context.getService(ReportingObjectGroupSerializedDefinitionService.class);
	}

	/**
     * @see ObjectGroupDefinitionPersister#getObjectGroupDefinition(Integer)
     */
    public ObjectGroupDefinition getObjectGroupDefinition(Integer id) {
    	return getService().getDefinition(ObjectGroupDefinition.class, id);
    }
    
	/**
     * @see ObjectGroupDefinitionPersister#getObjectGroupDefinitionByUuid(String)
     */
    public ObjectGroupDefinition getObjectGroupDefinitionByUuid(String uuid) {
     	return getService().getDefinitionByUuid(ObjectGroupDefinition.class, uuid);
    }

	/**
     * @see ObjectGroupDefinitionPersister#getAllObjectGroupDefinitions(boolean)
     */
    public List<ObjectGroupDefinition> getAllObjectGroupDefinitions(boolean includeRetired) {
     	return getService().getAllDefinitions(ObjectGroupDefinition.class, includeRetired);
    }
    
	/**
	 * @see ObjectGroupDefinitionPersister#getNumberOfObjectGroupDefinitions(boolean)
	 */
	public int getNumberOfObjectGroupDefinitions(boolean includeRetired) {
    	return getService().getNumberOfDefinitions(ObjectGroupDefinition.class, includeRetired);
	}

	/**
     * @see ObjectGroupDefinitionPersister#getObjectGroupDefinitionByName(String, boolean)
     */
    public List<ObjectGroupDefinition> getObjectGroupDefinitions(String name, boolean exactMatchOnly) {
    	return getService().getDefinitions(ObjectGroupDefinition.class, name, exactMatchOnly);
    }
    
	/**
     * @see ObjectGroupDefinitionPersister#saveObjectGroupDefinition(ObjectGroupDefinition)
     */
    public ObjectGroupDefinition saveObjectGroupDefinition(ObjectGroupDefinition objectGroupDefinition) {
     	return getService().saveDefinition(objectGroupDefinition);
    }

	/**
     * @see ObjectGroupDefinitionPersister#purgeObjectGroupDefinition(ObjectGroupDefinition)
     */
    public void purgeObjectGroupDefinition(ObjectGroupDefinition objectGroupDefinition) {
    	getService().purgeDefinition(objectGroupDefinition);
    }

	public List<ObjectGroupDefinition> getAllDefinitions(boolean includeVoided) {
		return getService().getAllDefinitions(ObjectGroupDefinition.class, includeVoided);

	}

	public ObjectGroupDefinition getDefinition(Integer id) {
		return getService().getDefinition(ObjectGroupDefinition.class, id);
	}

	public ObjectGroupDefinition getDefinitionByUuid(String uuid) {
		return getService().getDefinitionByUuid(ObjectGroupDefinition.class, uuid);
	}

	public List<ObjectGroupDefinition> getDefinitions(String name, boolean exactMatchOnly)
			throws APIException {
		return getService().getDefinitions(ObjectGroupDefinition.class, name, exactMatchOnly);
	}

	public int getNumberOfDefinitions(boolean includeRetired) {
		return getService().getNumberOfDefinitions(ObjectGroupDefinition.class, includeRetired);
	}

	public void purgeDefinition(ObjectGroupDefinition definition) {
		getService().purgeDefinition(definition);
		
	}

	public ObjectGroupDefinition saveDefinition(ObjectGroupDefinition definition) {
		return getService().saveDefinition(definition);
	}
	
	
}
