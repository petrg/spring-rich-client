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
package org.springframework.richclient.form.binding.swing;

import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;

import org.springframework.beans.PropertyComparator;
import org.springframework.binding.form.ConfigurableFormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.support.AbstractBindingFactory;
import org.springframework.richclient.forms.BufferedCollectionValueModel;
import org.springframework.richclient.list.BeanPropertyValueListRenderer;
import org.springframework.richclient.list.ObservableList;

/**
 * A convenient implementation of <code>BindingFactory</code>. Provides a set
 * of methods that address the typical binding requirements of Swing based 
 * forms.
 * 
 * @author Oliver Hutchison
 */
public class SwingBindingFactory extends AbstractBindingFactory {

    public SwingBindingFactory(ConfigurableFormModel formModel) {
        super(formModel);
    }

    public Binding createBoundTextField(String formProperty) {
        return createBinding(JTextField.class, formProperty);
    }

    public Binding createBoundTextArea(String formProperty) {
        return createBinding(JTextArea.class, formProperty);
    }

    public Binding createBoundTextArea(String formProperty, int rows, int columns) {
        Map context = createContext(TextAreaBinder.ROWS_KEY, new Integer(rows));
        context.put(TextAreaBinder.COLUMNS_KEY, new Integer(columns));
        return createBinding(JTextArea.class, formProperty, context);
    }

    public Binding createBoundFormattedTextField(String formProperty) {
        return createBinding(JFormattedTextField.class, formProperty);
    }

    public Binding createBoundFormattedTextField(String formProperty, AbstractFormatterFactory formatterFactory) {
        Map context = createContext(FormattedTextFieldBinder.FORMATTER_FACTORY_KEY, formatterFactory);
        return createBinding(JFormattedTextField.class, formProperty, context);
    }

    public Binding createBoundSpinner(String formProperty) {
        return createBinding(JSpinner.class, formProperty);
    }
    
    public Binding createBoundLabel(String formProperty) {
        return createBinding(JLabel.class, formProperty);
    }

    public Binding createBoundCheckBox(String formProperty) {
        return createBinding(JCheckBox.class, formProperty);
    }

    public Binding createBoundComboBox(String formProperty) {
        return createBinding(JComboBox.class, formProperty);
    }

    /**
     * 
     * @param formProperty the property to be bound
     * @param selectableItems a Collection or array containing the list of items 
     * that may be selected
     */
    public Binding createBoundComboBox(String formProperty, Object selectableItems) {
        return createBoundComboBox(formProperty, new ValueHolder(selectableItems));
    }

    public Binding createBoundComboBox(String formProperty, ValueModel selectableItemsHolder) {
        Map context = createContext(ComboBoxBinder.SELECTABLE_ITEMS_HOLDER_KEY, selectableItemsHolder);
        return createBinding(JComboBox.class, formProperty, context);
    }

    public Binding createBoundComboBox(String formProperty, String selectableItemsProperty, String renderedItemProperty) {
        return createBoundComboBox(formProperty, getFormModel().getValueModel(selectableItemsProperty),
                renderedItemProperty);
    }

    public Binding createBoundComboBox(String formProperty, ValueModel selectableItemsHolder, String renderedProperty) {
        Map context = createContext(ComboBoxBinder.SELECTABLE_ITEMS_HOLDER_KEY, selectableItemsHolder);
        context.put(ComboBoxBinder.RENDERER_KEY, new BeanPropertyValueListRenderer(renderedProperty));
        context.put(ComboBoxBinder.COMPARATOR_KEY, new PropertyComparator(renderedProperty));
        return createBinding(JComboBox.class, formProperty, context);
    }

    /**
     * This method will most likely move over to FormModel
     * 
     * @deprecated
     */
    public ObservableList createBoundListModel(String formProperty) {
        final ConfigurableFormModel formModel = ((ConfigurableFormModel)getFormModel());
        ValueModel valueModel = formModel.getValueModel(formProperty);
        if (!(valueModel instanceof BufferedCollectionValueModel)) {
            valueModel = new BufferedCollectionValueModel(formModel.getPropertyAccessStrategy().getPropertyValueModel(
                    formProperty), formModel.getPropertyAccessStrategy().getMetadataAccessStrategy().getPropertyType(
                    formProperty));
            formModel.add(formProperty, valueModel);
        }
        return (ObservableList)valueModel.getValue();
    }

    public Binding createBoundList(String formProperty) {
        Map context = createContext(ListBinder.MODEL_KEY, createBoundListModel(formProperty));
        return createBinding(JList.class, formProperty, context);
    }

    public Binding createBoundList(String selectionFormProperty, Object selectableItems, String renderedProperty) {
        return createBoundList(selectionFormProperty, new ValueHolder(selectableItems), renderedProperty);
    }

    /**
     * Binds the values specified in the collection contained within
     * <code>selectableItemsHolder</code> to a {@link JList}, with any
     * user selection being placed in the form property referred to by
     * <code>selectionFormProperty</code>.  Each item in the list will be
     * rendered by looking up a property on the item by the name contained
     * in <code>renderedProperty</code>, retrieving the value of the property,
     * and rendering that value in the UI.  Note that the selection in the
     * bound list will track any changes to the
     * <code>selectionFormProperty</code>.  This is especially useful to
     * preselect items in the list - if <code>selectionFormProperty</code>
     * is not empty when the list is bound, then its content will be used
     * for the initial selection.  This method uses default behavior to
     * determine the selection mode of the resulting <code>JList</code>:
     * if <code>selectionFormProperty</code> refers to a
     * {@link java.util.Collection} type property, then
     * {@link javax.swing.ListSelectionModel#MULTIPLE_INTERVAL_SELECTION} will
     * be used, otherwise
     * {@link javax.swing.ListSelectionModel#SINGLE_SELECTION} will be used.
     * 
     * @param selectionFormProperty form property to hold user's selection.
     *                              This property must either be compatible
     *                              with the item objects contained in
     *                              <code>selectableItemsHolder</code> (in
     *                              which case only single selection makes
     *                              sense), or must be a
     *                              <code>Collection</code> type, which allows
     *                              for multiple selection.
     * @param selectableItemsHolder <code>ValueModel</code> containing the
     *                              items with which to populate the list. 
     * @param renderedProperty      the property to be queried for each item
     *                              in the list, the result of which will be
     *                              used to render that item in the UI
     *                              
     * @return
     */
    public Binding createBoundList(String selectionFormProperty, ValueModel selectableItemsHolder,
            String renderedProperty) {
      return createBoundList(selectionFormProperty, selectableItemsHolder, renderedProperty, null);
    }

    /**
     * Binds the values specified in the collection contained within
     * <code>selectableItemsHolder</code> to a {@link JList}, with any
     * user selection being placed in the form property referred to by
     * <code>selectionFormProperty</code>.  Each item in the list will be
     * rendered by looking up a property on the item by the name contained
     * in <code>renderedProperty</code>, retrieving the value of the property,
     * and rendering that value in the UI.  Note that the selection in the
     * bound list will track any changes to the
     * <code>selectionFormProperty</code>.  This is especially useful to
     * preselect items in the list - if <code>selectionFormProperty</code>
     * is not empty when the list is bound, then its content will be used
     * for the initial selection.
     * 
     * @param selectionFormProperty form property to hold user's selection.
     *                              This property must either be compatible
     *                              with the item objects contained in
     *                              <code>selectableItemsHolder</code> (in
     *                              which case only single selection makes
     *                              sense), or must be a
     *                              <code>Collection</code> type, which allows
     *                              for multiple selection.
     * @param selectableItemsHolder <code>ValueModel</code> containing the
     *                              items with which to populate the list. 
     * @param renderedProperty      the property to be queried for each item
     *                              in the list, the result of which will be
     *                              used to render that item in the UI
     * @param forceSelectMode       forces the list selection mode.  Must be
     *                              one of the constants defined in
     *                              {@link javax.swing.ListSelectionModel} or
     *                              <code>null</code> for default behavior.
     *                              If <code>null</code>, then
     *                              {@link javax.swing.ListSelectionModel#MULTIPLE_INTERVAL_SELECTION}
     *                              will be used if
     *                              <code>selectionFormProperty</code> refers
     *                              to a {@link java.util.Collection} type
     *                              property, otherwise
     *                              {@link javax.swing.ListSelectionModel#SINGLE_SELECTION}
     *                              will be used.
     *                              
     * @return
     */
    public Binding createBoundList(String selectionFormProperty, ValueModel selectableItemsHolder,
                                   String renderedProperty, Integer forceSelectMode) {
      final ConfigurableFormModel formModel = (ConfigurableFormModel)getFormModel();
        
      final ValueModel selectionValueModel = formModel.getValueModel(selectionFormProperty);
      final Map context = createContext(ListBinder.SELECTED_ITEM_HOLDER_KEY, selectionValueModel);
      final Class selectionPropertyType = formModel.getMetadataAccessStrategy().getPropertyType(selectionFormProperty);
      if(selectionPropertyType != null) {
        context.put(ListBinder.SELECTED_ITEM_TYPE_KEY, selectionPropertyType);
      }
      if(forceSelectMode != null) {
        context.put(ListBinder.SELECTION_MODE_KEY, forceSelectMode);
      }
      context.put(ListBinder.SELECTABLE_ITEMS_HOLDER_KEY, selectableItemsHolder);
      context.put(ListBinder.RENDERER_KEY, new BeanPropertyValueListRenderer(renderedProperty));
      context.put(ListBinder.COMPARATOR_KEY, new PropertyComparator(renderedProperty));
      return createBinding(JList.class, selectionFormProperty, context);
    }
}