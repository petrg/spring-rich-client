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
package org.springframework.binding.form.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.binding.MutablePropertyAccessStrategy;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.MutableFormModel;
import org.springframework.binding.form.NestableFormModel;
import org.springframework.binding.form.NestingFormModel;
import org.springframework.binding.form.ValidationListener;
import org.springframework.binding.support.BeanPropertyAccessStrategy;
import org.springframework.binding.value.ValueChangeListener;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.BufferedValueModel;
import org.springframework.binding.value.support.PropertyAdapter;
import org.springframework.rules.Constraint;
import org.springframework.rules.support.Algorithms;
import org.springframework.rules.support.ClosureWithoutResult;
import org.springframework.util.Assert;

/**
 * @author Keith Donald
 */
public class CompoundFormModel extends AbstractFormModel implements
        NestingFormModel {

    private List childFormObjectBuffers = new ArrayList(9);

    private Map childFormModels = new LinkedHashMap(9);

    public CompoundFormModel() {
    }

    public CompoundFormModel(Object domainObject) {
        this(new BeanPropertyAccessStrategy(domainObject));
    }

    public CompoundFormModel(ValueModel domainObjectHolder) {
        this(new BeanPropertyAccessStrategy(domainObjectHolder));
    }

    public CompoundFormModel(
            MutablePropertyAccessStrategy domainObjectAccessStrategy) {
        this(domainObjectAccessStrategy, true);
    }

    public CompoundFormModel(
            MutablePropertyAccessStrategy domainObjectAccessStrategy,
            boolean bufferChanges) {
        super(domainObjectAccessStrategy);
        setBufferChangesDefault(bufferChanges);
    }

    public MutableFormModel createChild(String childFormModelName) {
        ValidatingFormModel childModel = new ValidatingFormModel(
                getPropertyAccessStrategy(), getBufferChangesDefault());
        childModel.setRulesSource(getRulesSource());
        addChildModel(childFormModelName, childModel);
        return childModel;
    }

    public MutableFormModel createChild(String childFormModelName,
            String childFormObjectPath) {
        return (MutableFormModel)createChildInternal(new ValidatingFormModel(),
                childFormModelName, childFormObjectPath);
    }

    public NestingFormModel createCompoundChild(String childFormModelName,
            String childFormObjectPath) {
        return (NestingFormModel)createChildInternal(new CompoundFormModel(),
                childFormModelName, childFormObjectPath);
    }

    private FormModel createChildInternal(AbstractFormModel childFormModel,
            String childFormModelName, String childFormObjectPath) {
        ValueModel valueHolder = new PropertyAdapter(
                getPropertyAccessStrategy(), childFormObjectPath);
        if (getBufferChangesDefault()) {
            valueHolder = new BufferedValueModel(valueHolder);
            childFormObjectBuffers.add(valueHolder);
        }
        boolean enabledDefault = valueHolder.getValue() != null;
        Class valueClass = getMetadataAccessStrategy().getPropertyType(
                childFormObjectPath);
        new ChildFormObjectSetter(valueHolder, valueClass);
        return createChildInternal(childFormModel, childFormModelName,
                valueHolder, enabledDefault);
    }

    private static class ChildFormObjectSetter implements ValueChangeListener {
        private ValueModel formObjectHolder;

        private Class formObjectClass;

        public ChildFormObjectSetter(ValueModel formObjectHolder,
                Class formObjectClass) {
            this.formObjectHolder = formObjectHolder;
            this.formObjectClass = formObjectClass;
            this.formObjectHolder.addValueChangeListener(this);
            setIfNull();
        }

        public void valueChanged() {
            setIfNull();
        }

        public void setIfNull() {
            if (formObjectHolder.getValue() == null) {
                formObjectHolder.setValue(BeanUtils
                        .instantiateClass(formObjectClass));
            }
        }
    }

    public MutableFormModel createChild(String childFormModelName,
            ValueModel childFormObjectHolder) {
        return createChild(childFormModelName, childFormObjectHolder, true);
    }

    public NestingFormModel createCompoundChild(String childFormModelName,
            ValueModel childFormObjectHolder) {
        return createCompoundChild(childFormModelName, childFormObjectHolder,
                true);
    }

    public MutableFormModel createChild(String childFormModelName,
            ValueModel childFormObjectHolder, boolean enabled) {
        return (MutableFormModel)createChildInternal(new ValidatingFormModel(),
                childFormModelName, childFormObjectHolder, enabled);
    }

    public NestingFormModel createCompoundChild(String childFormModelName,
            ValueModel childFormObjectHolder, boolean enabled) {
        return (NestingFormModel)createChildInternal(new CompoundFormModel(),
                childFormModelName, childFormObjectHolder, enabled);
    }

    private FormModel createChildInternal(AbstractFormModel childModel,
            String childFormModelName, ValueModel childFormObjectHolder,
            boolean enabled) {
        MutablePropertyAccessStrategy childObjectAccessStrategy = getPropertyAccessStrategy()
                .newPropertyAccessStrategy(childFormObjectHolder);
        childModel.setPropertyAccessStrategy(childObjectAccessStrategy);
        childModel.setEnabled(enabled);
        childModel.setBufferChangesDefault(getBufferChangesDefault());
        childModel.setRulesSource(getRulesSource());
        addChildModel(childFormModelName, childModel);
        return childModel;
    }

    public NestableFormModel addChildModel(String childFormModelName,
            NestableFormModel childModel) {
        Assert.isTrue(getChildFormModel(childFormModelName) == null,
                "Child model by name '" + childFormModelName
                        + "' already exists");
        childModel.setParent(this);
        if (logger.isDebugEnabled()) {
            logger.debug("Adding new nested form model '" + childFormModelName
                    + "', value=" + childModel);
        }
        childFormModels.put(childFormModelName, childModel);
        return childModel;
    }

    public void addValidationListener(final ValidationListener listener) {
        Algorithms.instance().forEach(childFormModels.values(),
                new ClosureWithoutResult() {
                    public void doCall(Object formModel) {
                        ((FormModel)formModel).addValidationListener(listener);
                    }
                });
    }

    public void addValidationListener(ValidationListener listener,
            String childFormModelName) {
        FormModel model = getChildFormModel(childFormModelName);
        Assert.notNull(model, "No child model by name " + childFormModelName
                + "exists; unable to add listener");
        model.addValidationListener(listener);
    }

    public void removeValidationListener(ValidationListener listener,
            String childFormModelName) {
        FormModel model = getChildFormModel(childFormModelName);
        Assert.notNull(model, "No child model by name " + childFormModelName
                + "exists; unable to remove listener");
        model.removeValidationListener(listener);
    }

    public FormModel getChildFormModel(String childFormModelName) {
        return (FormModel)childFormModels.get(childFormModelName);
    }

    public void removeValidationListener(final ValidationListener listener) {
        Algorithms.instance().forEach(childFormModels.values(),
                new ClosureWithoutResult() {
                    public void doCall(Object childFormModel) {
                        ((FormModel)childFormModel)
                                .removeValidationListener(listener);
                    }
                });
    }

    public ValueModel getDisplayValueModel(String formProperty) {
        // todo
        return null;
    }

    public ValueModel getValueModel(String formPropertyPath) {
        return getValueModel(formPropertyPath, true);
    }

    public ValueModel findValueModelFor(FormModel delegatingChild,
            String formPropertyPath) {
        Iterator it = childFormModels.values().iterator();
        while (it.hasNext()) {
            NestableFormModel formModel = (NestableFormModel)it.next();
            if (delegatingChild != null && formModel == delegatingChild) {
                continue;
            }
            ValueModel valueModel = formModel.getValueModel(formPropertyPath,
                    false);
            if (valueModel != null) { return valueModel; }
        }
        if (logger.isInfoEnabled()) {
            logger.info("No value model by name '" + formPropertyPath
                    + "' found on any nested form models... returning [null]");
        }
        return null;
    }

    public ValueModel getValueModel(String formPropertyPath, boolean queryParent) {
        ValueModel valueModel = findValueModelFor(null, formPropertyPath);
        if (valueModel == null) {
            if (getParent() != null && queryParent) {
                valueModel = getParent().findValueModelFor(this,
                        formPropertyPath);
            }
        }
        return valueModel;
    }

    protected void handleEnabledChange() {
        Algorithms.instance().forEach(childFormModels.values(),
                new ClosureWithoutResult() {
                    public void doCall(Object childFormModel) {
                        ((FormModel)childFormModel).setEnabled(isEnabled());
                    }
                });
    }

    public boolean getHasErrors() {
        return Algorithms.instance().areAnyTrue(childFormModels.values(),
                new Constraint() {
                    public boolean test(Object childFormModel) {
                        return ((FormModel)childFormModel).getHasErrors();
                    }
                });
    }

    public Map getErrors() {
        final Map allErrors = new HashMap();
        Algorithms.instance().forEach(childFormModels.values(),
                new ClosureWithoutResult() {
                    public void doCall(Object childFormModel) {
                        allErrors.putAll(((FormModel)childFormModel)
                                .getErrors());
                    }
                });
        return allErrors;
    }

    public boolean isDirty() {
        return Algorithms.instance().areAnyTrue(childFormModels.values(),
                new Constraint() {
                    public boolean test(Object childFormModel) {
                        return ((FormModel)childFormModel).isDirty();
                    }
                });
    }

    public boolean hasErrors(String childModelName) {
        FormModel model = getChildFormModel(childModelName);
        Assert.notNull(model, "No child model by name " + childModelName
                + "exists.");
        return model.getHasErrors();
    }

    public void commit() {
        if (preEditCommit()) {
            Algorithms.instance().forEach(childFormModels.values(),
                    new ClosureWithoutResult() {
                        public void doCall(Object childFormModel) {
                            ((FormModel)childFormModel).commit();
                        }
                    });
            if (getBufferChangesDefault()) {
                Algorithms.instance().forEach(childFormObjectBuffers,
                        new ClosureWithoutResult() {
                            public void doCall(Object bufferedValueModel) {
                                ((BufferedValueModel)bufferedValueModel)
                                        .commit();
                            }
                        });
            }
            postEditCommit();
        }
    }

    public void revert() {
        Algorithms.instance().forEach(childFormModels.values(),
                new ClosureWithoutResult() {
                    public void doCall(Object childFormModel) {
                        ((FormModel)childFormModel).revert();
                    }
                });
    }
}