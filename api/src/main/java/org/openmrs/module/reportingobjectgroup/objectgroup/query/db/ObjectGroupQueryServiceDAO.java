package org.openmrs.module.reportingobjectgroup.objectgroup.query.db;

import java.util.Map;

import org.openmrs.module.reportingobjectgroup.objectgroup.ObjectGroup;

public interface ObjectGroupQueryServiceDAO {

	
	 public ObjectGroup executeSqlQuery(String sqlQuery, Map<String,Object> paramMap);
	 
	 
}
