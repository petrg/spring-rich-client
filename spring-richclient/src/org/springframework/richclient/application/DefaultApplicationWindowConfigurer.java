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
package org.springframework.richclient.application;

import java.awt.Dimension;
import java.awt.Image;

import org.springframework.richclient.application.config.ApplicationWindowConfigurer;
import org.springframework.util.Assert;
import org.springframework.util.ToStringCreator;

/**
 * @author Keith Donald
 */
public class DefaultApplicationWindowConfigurer implements
        ApplicationWindowConfigurer {

    String title = "New Application Window";

    Image image;

    boolean showMenuBar = true;

    boolean showToolBar = true;

    boolean showStatusBar = true;

    Dimension initialSize = new Dimension(800, 600);

    private ApplicationWindow window;

    public DefaultApplicationWindowConfigurer(ApplicationWindow window) {
        Assert.notNull(window, "Application window is required");
        this.window = window;
    }

    public ApplicationWindow getWindow() {
        return window;
    }

    public String getTitle() {
        return title;
    }

    public Image getImage() {
        return image;
    }

    public Dimension getInitialSize() {
        return initialSize;
    }

    public boolean getShowMenuBar() {
        return showMenuBar;
    }

    public boolean getShowToolBar() {
        return showToolBar;
    }

    public boolean getShowStatusBar() {
        return showStatusBar;
    }

    public void setTitle(String title) {
        this.title = title;
        if (window.isControlCreated()) {
            window.getControl().setTitle(title);
        }
    }

    public void setImage(Image image) {
        this.image = image;
        if (window.isControlCreated()) {
            window.getControl().setIconImage(image);
        }
    }

    public void setInitialSize(Dimension initialSize) {
        if (initialSize != null) {
            this.initialSize = initialSize;
        }
    }

    public void setShowMenuBar(boolean showMenuBar) {
        this.showMenuBar = showMenuBar;
        if (window.isControlCreated()) {
            window.getMenuBar().setVisible(showMenuBar);
        }
    }

    public void setShowToolBar(boolean showToolBar) {
        this.showToolBar = showToolBar;
        if (window.isControlCreated()) {
            window.getToolBar().setVisible(showToolBar);
        }
    }

    public void setShowStatusBar(boolean showStatusBar) {
        this.showStatusBar = showStatusBar;
        if (window.isControlCreated()) {
            window.getStatusBar().setVisible(showStatusBar);
        }
    }

    public String toString() {
        return new ToStringCreator(this).append("title", title).append("image",
            image).append("showMenuBar", showMenuBar).append("showToolBar",
            showToolBar).append("showStatusBar", showStatusBar).append(
            "initialSize", initialSize).append("window", window).toString();
    }

}