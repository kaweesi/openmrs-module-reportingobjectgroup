package org.openmrs.module.reportingobjectgroup.objectgroup.definition;

import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.BaseDefinition;
import org.openmrs.module.reporting.evaluation.caching.Caching;



/**
 * The base definition in this module.  Emulates BaseCohortDefinition.
 * 
 * 
 * @author dthomas
 *
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public abstract class ObjectGroupDefinition extends BaseDefinition {
	
	//***** PROPERTIES *****
	//id property is necessary because BaseDefinition extends BaseOpenmrsMetadata
	
	@ConfigurationProperty
    private Integer id;

    //***** CONSTRUCTORS *****
    
    /**
     * Default Constructor
     */
    public ObjectGroupDefinition() {
    	super();
    }
    
    //***** INSTANCE METHODS *****
	
	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return ObjectUtil.nvlStr(getName(), getClass().getSimpleName());
	}
	
    //***** Property Access *****
	
    /**
     * @return the id
     */
    public Integer getId() {
    	return id;
    }

	/**
     * @param id the id to set
     */
    public void setId(Integer id) {
    	this.id = id;
    }
	
	
}
