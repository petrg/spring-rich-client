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
package org.springframework.richclient.list;

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.rules.values.ValueHolder;
import org.springframework.rules.values.ValueListener;
import org.springframework.rules.values.ValueModel;

/**
 * A combobox whose contents are dynamically refreshable.
 * 
 * @author Keith Donald
 */
public class DynamicComboBoxListModel extends ComboBoxListModel implements
        ValueListener {
    private static final Log logger = LogFactory
            .getLog(DynamicComboBoxListModel.class);

    private ValueModel selectedItemHolder;

    private ValueModel selectableItemsHolder;

    public DynamicComboBoxListModel(ValueModel selectedItemHolder) {
        this(selectedItemHolder, (ValueModel)null);
    }

    public DynamicComboBoxListModel(ValueModel selectedItemHolder, List items) {
        this(selectedItemHolder, new ValueHolder(items));
    }

    public DynamicComboBoxListModel(final ValueModel selectedItemHolder,
            ValueModel selectableItemsHolder) {
        super();
        this.selectedItemHolder = selectedItemHolder;
        if (selectedItemHolder != null) {
            selectedItemHolder.addValueListener(new ValueListener() {
                public void valueChanged() {
                    if (logger.isDebugEnabled()) {
                        logger
                                .debug("Notifying combo box view selected value changed; new value is '"
                                        + selectedItemHolder.get() + "'");
                    }
                    if (selectedItemHolder.get() == null) {
                        if (size() > 0 && get(0) != null) {
                            setSelectedItem(get(0));
                        }
                    }
                    else {
                        fireContentsChanged(this, -1, -1);
                    }
                }
            });
        }
        setSelectableItemsHolder(selectableItemsHolder);
    }

    public void setSelectableItemsHolder(ValueModel holder) {
        if (this.selectableItemsHolder == holder) { return; }
        if (this.selectableItemsHolder != null) {
            holder.removeValueListener(this);
        }
        this.selectableItemsHolder = holder;
        if (this.selectableItemsHolder != null) {
            doAdd(holder.get());
            this.selectableItemsHolder.addValueListener(this);
        }
    }

    public Object getSelectedItem() {
        if (selectedItemHolder != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Returning selected item "
                        + selectedItemHolder.get());
            }
            Object item = selectedItemHolder.get();
            return item;
        }
        else {
            return super.getSelectedItem();
        }
    }

    public void setSelectedItem(Object selectedItem) {
        if (selectedItemHolder != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Setting newly selected item on value holder to "
                        + selectedItem);
            }
            //if (selectedItem == null && size() > 0) {
           //     selectedItemHolder.set(get(0));
            //} else {
                selectedItemHolder.set(selectedItem);
            //}
        }
        else {
            super.setSelectedItem(selectedItem);
        }
    }

    public void valueChanged() {
        if (logger.isDebugEnabled()) {
            logger.debug("Backing collection of selectable items changed; "
                    + "refreshing combo box with contents of new collection.");
        }
        doAdd(selectableItemsHolder.get());
    }

    private void doAdd(Object items) {
        clear();
        if (items != null) {
            if (items instanceof Collection) {
                addAll((Collection) items); 
            } else if (items instanceof Object[]) {
                Object[] itemsArray = (Object[]) items;
                for (int i = 0; i < itemsArray.length; i++) {
                    add(itemsArray[i]);
                }
            } else {
                throw new IllegalArgumentException(
                        "selectableItemsHolder must hold a Collection or array");
            }        
        }
        sort();
    }

}