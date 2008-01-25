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
package org.springframework.binding.form;

import org.springframework.binding.value.ValueModel;

/**
 * Sub-interface implemented by form models that allow for configuration of the
 * form's value models, id etc..
 *
 * @author Keith Donald
 * @author Oliver Hutchison
 */
public interface ConfigurableFormModel extends FormModel {

	/**
	 * An id to identify this formModel.
	 *
	 * @param id
	 */
	public void setId(String id);

	/**
	 * <p>
	 * Set the enabled state of this formModel. All fieldMetaData should take
	 * the enabled state of the formModel into account but should not alter
	 * their own enclosed enabled state.
	 * </p>
	 *
	 * <p>
	 * A disabled formModel can be compared to a visual component which doesn't
	 * respond to any user interaction (grey-out).
	 * </p>
	 *
	 * @param enabled set to <code>true</code> if the formModel should be
	 * enabled. Set to <code>false</code> if all fields should be disabled.
	 */
	public void setEnabled(boolean enabled);

	/**
	 * <p>
	 * Set the readOnly state of this formModel. All fieldMetaData should take
	 * the readOnly state of the formModel into account but should not alter
	 * their own enclosed readOnly state.
	 * </p>
	 *
	 * <p>
	 * A formModel in readOnly state can be seen as visual component in which
	 * the user can navigate but not alter any values. (Eg editable TextFields)
	 * </p>
	 *
	 * @param readOnly set to <code>true</code> if all fields should be set
	 * readOnly.
	 */
	public void setReadOnly(boolean readOnly);

	/**
	 * Add a valueModel for the given property.
	 *
	 * @param propertyName the property to create a valueModel for.
	 * @return a ValueModel that wraps the property.
	 */
	public ValueModel add(String propertyName);

	/**
	 * Add the given valueModel as wrapper for the given property. Note that the
	 * given valueModel should be used to access the property, but may be
	 * wrapped(chained) a number of times in other valueModels.
	 *
	 * @param propertyName the property.
	 * @param valueModel the valueModel to access the property.
	 * @return a valueModel that wraps the given valueModel.
	 */
	public ValueModel add(String propertyName, ValueModel valueModel);

	/**
	 * Add a valueModel that holds a derived value computed by invoking the
	 * given method with the given property as argument on the formModel.
	 *
	 * @param propertyMethodName method to invoke.
	 * @param derivedFromProperty property to use as argument.
	 * @return a valueModel holding the derived value.
	 */
	public ValueModel addMethod(String propertyMethodName, String derivedFromProperty);

	/**
	 * Add a valueModel that holds a derived value computed by invoking the
	 * given method with a number of other properties as arguments on the
	 * formModel.
	 *
	 * @param propertyMethodName method to invoke.
	 * @param derivedFromProperties a number of properties to use as arguments on the method.
	 * @return
	 */
	public ValueModel addMethod(String propertyMethodName, String[] derivedFromProperties);
}