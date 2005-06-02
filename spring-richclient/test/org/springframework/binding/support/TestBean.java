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
package org.springframework.binding.support;

import java.util.List;
import java.util.Map;

/**
 * @author Oliver Hutchison
 */
public class TestBean {

    private String simpleProperty;

    private Map mapProperty;

    private List listProperty;

    private TestBean nestedProperty;

    public Object readOnly;

    public Object writeOnly;

    public String getSimpleProperty() {
        return simpleProperty;
    }

    public void setSimpleProperty(String simpleProperty) {
        this.simpleProperty = simpleProperty;
    }

    public Map getMapProperty() {
        return mapProperty;
    }

    public void setMapProperty(Map mapProperty) {
        this.mapProperty = mapProperty;
    }

    public List getListProperty() {
        return listProperty;
    }

    public void setListProperty(List listProperty) {
        this.listProperty = listProperty;
    }

    public TestBean getNestedProperty() {
        return nestedProperty;
    }

    public void setNestedProperty(TestBean nestedProperty) {
        this.nestedProperty = nestedProperty;
    }

    public Object getReadOnly() {
        return readOnly;
    }

    public void setWriteOnly(Object writeOnly) {
        this.writeOnly = writeOnly;        
    }
}