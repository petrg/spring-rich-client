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
package org.springframework.richclient.form.builder;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.springframework.richclient.forms.SwingFormModel;
import org.springframework.richclient.util.TablePanelBuilder;
import org.springframework.rules.Constraint;

/**
 * @author oliverh
 */
public class TableFormBuilder extends AbstractFormBuilder {

    private TablePanelBuilder builder;

    private boolean hasSpaceToLeft = true;

    private boolean skipRow;

    public TableFormBuilder(SwingFormModel formModel) {
        super(formModel);
        this.builder = new TablePanelBuilder(new JPanel());
    }

    public void row() {
        hasSpaceToLeft = true;
        builder.row();
    }

    public JComponent[] add(String propertyName) {
        return add(propertyName, "");
    }

    public JComponent[] add(String propertyName, String attributes) {
        return addComponents(getDefaultComponent(propertyName), attributes, propertyName,
                getLabelAttributes());
    }

    public JComponent[] add(String propertyName, JComponent component) {
        return add(propertyName, component, "");
    }

    public JComponent[] add(String propertyName, JComponent component,
            String attributes) {
        return addComponents(component, "", propertyName, getLabelAttributes());
    }

    public JComponent[] addSelector(String propertyName, Constraint filter) {
        return addSelector(propertyName, filter, "");
    }
    
    public JComponent[] addSelector(String propertyName, Constraint filter, String attributes) {
        return addComponents(getSelector(propertyName, filter), attributes, propertyName, getLabelAttributes());
    }
    
    public JComponent[] addPasswordField(String propertyName) {
        return addPasswordField(propertyName, "");
    }

    public JComponent[] addPasswordField(String propertyName, String attributes) {
        return addComponents(getPasswordField(propertyName), attributes, propertyName, getLabelAttributes());
    }

    public JComponent[] addTextArea(String propertyName) {
        return addTextArea(propertyName, "");
    }

    public JComponent[] addTextArea(String propertyName, String attributes) {
        return addComponents(getTextArea(propertyName), attributes, propertyName,
                getLabelAttributes() + " valign=top");
    }

    public void addSeparator(String text) {
        builder.separator(text);
    }

    public void addSeparator(String text, String attributes) {
        builder.separator(text, attributes);
    }

    public JComponent getForm() {
        getFormModel().revert();
        return builder.getPanel();
    }

    protected String getLabelAttributes() {
        return "colGrId=label colSpec=left:pref";
    }

    private JComponent[] addComponents(JComponent component, String attributes,
            String propertyName, String labelAttributes) {
        JLabel label = getLabelFor(propertyName, component);

        if (!hasSpaceToLeft) {
            builder.gapCol();
        }
        hasSpaceToLeft = false;
        builder.cell(label, labelAttributes);
        builder.labelGapCol();
        builder.cell(component, attributes);
        return new JComponent[] {label, component};
    }
}