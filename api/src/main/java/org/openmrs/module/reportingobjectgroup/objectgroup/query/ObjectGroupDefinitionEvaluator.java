package org.openmrs.module.reportingobjectgroup.objectgroup.query;

import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reportingobjectgroup.objectgroup.ObjectGroup;
import org.openmrs.module.reportingobjectgroup.objectgroup.definition.ObjectGroupDefinition;


/**
 * The basic interface used for evaluating an ObjectGroupDefinition.
 * Its current implementation is SqlObjectGroupDefinitionEvaluator 
 *
 * @author dthomas
 *
 */
public interface ObjectGroupDefinitionEvaluator {

	
	public ObjectGroup evaluate(ObjectGroupDefinition objectGroupDefinition, EvaluationContext context) throws EvaluationException;
	
	
}
