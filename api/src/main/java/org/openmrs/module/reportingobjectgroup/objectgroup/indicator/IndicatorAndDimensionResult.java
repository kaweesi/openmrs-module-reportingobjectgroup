package org.openmrs.module.reportingobjectgroup.objectgroup.indicator;

import org.openmrs.Cohort;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.indicator.CohortIndicatorResult;
import org.openmrs.module.reporting.indicator.IndicatorResult;
import org.openmrs.module.reporting.indicator.dimension.CohortIndicatorAndDimensionResult;
import org.openmrs.module.reportingobjectgroup.objectgroup.service.ObjectGroupIndicatorResult;

/**
 * Result class that can hold ChortIndicatorResults or ObjectGroupIndicatorResults, the getValue() method will return the right value based on Results type.
 * 
 *  TODO:  this should extend CohortIndicatorAndDimensionResult from reporting module -- its basically a copy.
 *  TODO:  dimensions aren't implemented for ObjectGroupIndicatorResults
 *  
 * @author dthomas
 *
 */
public class IndicatorAndDimensionResult extends CohortIndicatorAndDimensionResult {
//***** PROPERTIES *****
	
	

	private ObjectGroupIndicatorResult objectGroupIndicatorResult;
	
	
	//***** CONSTRUCTORS *****
	/**
	 * Default constructor
	 */
	public IndicatorAndDimensionResult(IndicatorResult indicatorResult, EvaluationContext context) {
		super(null, context);
		if (indicatorResult instanceof CohortIndicatorResult)
			this.setCohortIndicatorResult((CohortIndicatorResult) indicatorResult);
		 else if (indicatorResult instanceof ObjectGroupIndicatorResult)
			this.objectGroupIndicatorResult = (ObjectGroupIndicatorResult) indicatorResult;
		 else
			 throw new RuntimeException("Found unsupported IndicatorResult type.");
    }
	
	//***** INSTANCE METHODS *****
	

	
	/**
	 * @return the Cohort which results from intersecting the CohortIndicator cohort with the dimension Cohorts
	 */
	@Override
	public Cohort getCohortIndicatorAndDimensionCohort() {
		if (this.getCohortIndicatorResult() != null){
			Cohort ret = this.getCohortIndicatorResult().getCohort();
			if (ret != null && !getDimensionResults().isEmpty()) {
				ret = Cohort.intersect(ret, calculateDimensionCohort());
			}
			return ret;
		} else {
			return this.getObjectGroupIndicatorResult().getObjectGroup().getCohort();
		}
	}
	


	/**
	 * @see IndicatorResult#getValue()
	 */
	@Override
	public Number getValue() {
		if (this.getCohortIndicatorResult() != null)
			return CohortIndicatorResult.getResultValue(this.getCohortIndicatorResult(), calculateDimensionCohort());
		else
			return ObjectGroupIndicatorResult.getResultValue(objectGroupIndicatorResult);
		//TODO:  add args above if you ever want to do object dimensions
    }
	
	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return ObjectUtil.nvlStr(getValue(), "");
	}
	
	//***** PROPERTY ACCESS *****

   
	public ObjectGroupIndicatorResult getObjectGroupIndicatorResult() {
		return objectGroupIndicatorResult;
	}

	public void setObjectGroupIndicatorResult(
			ObjectGroupIndicatorResult objectGroupIndicatorResult) {
		this.objectGroupIndicatorResult = objectGroupIndicatorResult;
	}

	
	
}
