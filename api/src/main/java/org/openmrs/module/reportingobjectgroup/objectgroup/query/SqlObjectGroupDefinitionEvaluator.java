package org.openmrs.module.reportingobjectgroup.objectgroup.query;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reportingobjectgroup.objectgroup.ObjectGroup;
import org.openmrs.module.reportingobjectgroup.objectgroup.definition.ObjectGroupDefinition;
import org.openmrs.module.reportingobjectgroup.objectgroup.definition.SqlObjectGroupDefinition;
import org.openmrs.module.reportingobjectgroup.objectgroup.query.service.ObjectGroupQueryService;



/**
 * The main evaluator for SqlObjectGroupDefinitions
 * 
 * It is somewhat unfortunate that we need to use our own service here...  
 * executeSqlQuery is quite generic, and if the reporting service had a generic strategy for this, we'd use it.
 * 
 * TODO:  question:  can we use a core OpenMRS service to run this query??
 * If so, we could drop ObjectGroupQueryService, ObjectGroupQueryServiceImpl, and the DAO classes.
 * 
 * Another alternative is to use AdministrationService.executeSQL and do all of the parameter binding here... 
 * 
 * Or, convert to dataSetDefinition?
 * 
 * @author dthomas
 *
 */
@Handler(supports={SqlObjectGroupDefinition.class})
public class SqlObjectGroupDefinitionEvaluator implements ObjectGroupDefinitionEvaluator {


	/**
	 * Default Constructor
	 */
	public SqlObjectGroupDefinitionEvaluator() {}
	
	
	
	  public ObjectGroup evaluate(ObjectGroupDefinition objectGroupDefinition, EvaluationContext context) {
		
		SqlObjectGroupDefinition sqlObjectGroupDefinition = (SqlObjectGroupDefinition) objectGroupDefinition;
		ObjectGroupQueryService egs = Context.getService(ObjectGroupQueryService.class);
		ObjectGroup c = egs.executeSqlQuery(sqlObjectGroupDefinition.getQuery(), context.getParameterValues());
		if (context.getBaseCohort() != null) {
			c = ObjectGroup.intersect(c, context.getBaseCohort());
		}
		
		return c;
	  }	
	  
	  
}
