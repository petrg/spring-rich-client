package org.springframework.rules.reporting;

import org.springframework.core.closure.Constraint;

/**
 * @author Keith Donald
 */
public class ValueValidationResults implements ValidationResults {

	private Object argument;

	private Constraint violatedConstraint;

	public ValueValidationResults(Object argument, Constraint violatedConstraint) {
		this.argument = argument;
		this.violatedConstraint = violatedConstraint;
	}

	public ValueValidationResults(Object argument) {
		this.argument = argument;
	}

	/**
	 * @see org.springframework.rules.reporting.ValidationResults#getRejectedValue()
	 */
	public Object getRejectedValue() {
		return argument;
	}

	/**
	 * @see org.springframework.rules.reporting.ValidationResults#getViolatedConstraint()
	 */
	public Constraint getViolatedConstraint() {
		return violatedConstraint;
	}

	/**
	 * @see org.springframework.rules.reporting.ValidationResults#getViolatedCount()
	 */
	public int getViolatedCount() {
		if (violatedConstraint != null) {
			return new SummingVisitor(violatedConstraint).sum();
		}
		else {
			return 0;
		}
	}

	/**
	 * @see org.springframework.rules.reporting.ValidationResults#getSeverity()
	 */
	public Severity getSeverity() {
		return Severity.ERROR;
	}

}
