/*
 * Copyright 2002-2004 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.rules.constraint.property;

import org.springframework.binding.PropertyAccessStrategy;
import org.springframework.core.closure.Constraint;
import org.springframework.rules.constraint.Required;
import org.springframework.util.Assert;

/**
 * Validates a property value as 'required' if some other condition is true.
 *
 * @author Seth Ladd
 * @author Keith Donald
 */
public class RequiredIfTrue extends AbstractPropertyConstraint implements
		Constraint {

	private String propertyName;

	private Constraint constraint;

	/**
	 * Tests that the property is present if the provided predicate is
	 * satisified.
	 *
	 * @param predicate
	 *            the condition
	 */
	public RequiredIfTrue(String propertyName, Constraint predicate) {
		super(propertyName);
		setConstraint(predicate);
	}

	protected RequiredIfTrue(String propertyName) {
		super(propertyName);
	}

	public Constraint getConstraint() {
		return constraint;
	}

	protected void setConstraint(Constraint predicate) {
		Assert.notNull(predicate, "predicate is required");
		this.constraint = predicate;
	}

	protected boolean test(PropertyAccessStrategy domainObjectAccessStrategy) {
		if (constraint.test(domainObjectAccessStrategy)) {
			return Required.instance().test(
					domainObjectAccessStrategy
					.getPropertyValue(getPropertyName()));
		}
		else {
			return true;
		}
	}

	public String toString() {
		return "required if (" + constraint + ")";
	}

}