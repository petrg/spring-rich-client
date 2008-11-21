/*
 * Copyright 2002-2006 the original author or authors.
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
package org.springframework.richclient.application;

import java.util.Set;

import org.springframework.richclient.factory.ControlFactory;

/**
 * An <code>ApplicationPage</code> is a container for <code>PageComponent</code> s.
 */
public interface ApplicationPage extends ControlFactory {
    String getId();

    ApplicationWindow getWindow();

    void addPageComponentListener(PageComponentListener listener);

    void removePageComponentListener(PageComponentListener listener);

    PageComponent getActiveComponent();

    void setActiveComponent(PageComponent pageComponent);

    void showView(String viewDescriptorId);

    void showView(ViewDescriptor viewDescriptor);

    /**
     * Returns the {@link View} with the given id. Returns <code>null</code> if no {@link View} with the given id is
     * shown.
     * <p>
     * This method is "generified" to avoid extra casts when calling this method:
     * <pre>
     * ApplicationPage page = ...; // get a reference to the ApplicationPage
     * InitialView initialView = page.getView("initialView");
     * </pre>
     * 
     * @param id
     *            the id, cannot be <code>null</code>
     * @return the {@link View}, or <code>null</code>
     */
    <T extends View> T getView(String id);

    void openEditor(Object editorInput);

    boolean closeAllEditors();

    boolean close();

    boolean close(PageComponent pageComponent);

    /**
     * Returns the Set of {@link PageComponent}s on this ApplicationPage
     * 
     * @return the page components
     */
    public Set<PageComponent> getPageComponents();

}