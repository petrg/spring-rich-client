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
package org.springframework.richclient.forms;

import javax.swing.DefaultButtonModel;

import org.springframework.binding.value.ValueChangeListener;
import org.springframework.binding.value.ValueModel;

public class SelectableButtonValueModel extends DefaultButtonModel implements ValueChangeListener {
    private ValueModel valueModel;

    public SelectableButtonValueModel(ValueModel valueModel) {
        this.valueModel = valueModel;
        this.valueModel.addValueChangeListener(this);
        valueChanged();
    }

    public void valueChanged() {
        Boolean selected = (Boolean)valueModel.getValue();      
        setSelected(selected == null ? false : selected.booleanValue());
    }

    public void setPressed(boolean pressed) {
        if ((isPressed() == pressed) || !isEnabled()) {
            return;
        } else if (! pressed && isArmed()) {
            setSelected(!this.isSelected());
        }
        super.setPressed(pressed);
    }

    public void setSelected(boolean selected) {
        if (isSelected() == selected) {
            return;
        }
        super.setSelected(selected);
        valueModel.setValue(Boolean.valueOf(isSelected()));
    }
}