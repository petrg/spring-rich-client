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
package org.springframework.richclient.dialog;

import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JComponent;

import org.springframework.richclient.core.Message;
import org.springframework.util.Assert;

/**
 * Dialog for showing an message to the user. The severity of the message
 * is used to determine the icon.
 * <p>
 * If the messagetext contains linefeeds ('\n'), the message is split into different
 * parts, and the first part is rendered in bold. This is to mimic the guidelines in
 * http://developer.apple.com/documentation/UserExperience/Conceptual/OSXHIGuidelines/XHIGWindows/chapter_17_section_6.html#//apple_ref/doc/uid/20000957-20000961-BACFBACB
 */
public class MessageDialog extends ApplicationDialog {

    private static final String OK_FACE_DESCRIPTOR_ID = "okCommand";
    private AlertMessageAreaPane messageAreaPane;
    private Message message;

    /**
     * Constructs a new dialog.
     * @param title the title
     * @param message the message
     */
    public MessageDialog(String title, Message message) {
        this(title, null, message);
    }

    /**
     * Constructs a new dialog.
     * @param title the title
     * @param parent the parent window
     * @param message the mesage
     */
    public MessageDialog(String title, Window parent, Message message) {
        super(title, parent);
        setMessage(message);
    }

    /**
     * Constructs a new dialog, the message is converted to an information
     * message
     * @param title the title
     * @param message the message text
     */
    public MessageDialog(String title, String message) {
        this(title, null, message);
    }

    /**
     * Constructs a new dialog, the message is converted to an information
     * message
     * @param title the title
     * @param parent the parent window
     * @param message the message text
     */
    public MessageDialog(String title, Window parent, String message) {
        super(title, parent);
        setMessage(message);
    }

    /**
     * Set the message text, severity is info.
     * @param text the message text
     */
    public void setMessage(String text) {
        setMessage(new Message(text));
    }

    /**
     * Set the message.
     * @param message the message
     */
    public void setMessage(Message message) {
        Assert.notNull(message, "The message is required");
        Assert.hasText(message.getText(), "The message text is required");
        this.message = message;
    }

    /**
     * @see org.springframework.richclient.dialog.ApplicationDialog#getCancelCommandId()
     */
    protected String getCancelCommandId() {
        return OK_FACE_DESCRIPTOR_ID;
    }

    /**
     * @see org.springframework.richclient.dialog.ApplicationDialog#registerDefaultCommand()
     */
    protected void registerDefaultCommand() {
        registerCancelCommandAsDefault();
    }

    /**
     * @see org.springframework.richclient.dialog.ApplicationDialog#getCommandGroupMembers()
     */
    protected Object[] getCommandGroupMembers() {
        return new Object[] { getCancelCommand()};
    }

    /**
     * @see org.springframework.richclient.dialog.ApplicationDialog#createDialogContentPane()
     */
    protected JComponent createDialogContentPane() {
        this.messageAreaPane = new AlertMessageAreaPane();
        this.messageAreaPane.setMessage(message);
        return messageAreaPane.getControl();
    }

    /**
     * @see org.springframework.richclient.dialog.ApplicationDialog#onFinish()
     */
    protected final boolean onFinish() {
        // final because it can never get called
        return true;
    }

    /**
     * @see org.springframework.richclient.dialog.ApplicationDialog#onAboutToShow()
     */
    protected void onAboutToShow() {
        int width = getDialog().getWidth();
        float scale=getDialogScaleFactor();
        int parentWidth = getDialog().getParent().getWidth();
        if (width > parentWidth * scale) {
            final int messageAreaPaneHeight = messageAreaPane.getPreferredHeight();
            // adjust the width
            getDialog().setSize((int) (parentWidth * scale), getDialog().getHeight());

            // dirty hack, because messageAreaPane.getPreferredHeight() doesn't respond
            // immediately to dialog resize when dialog is not visible
            getDialog().addComponentListener(new ComponentAdapter() {
                public void componentShown(ComponentEvent e) {
                    // we must also change the height
                    int newHeight =
                        getDialog().getHeight() + messageAreaPane.getPreferredHeight() - messageAreaPaneHeight;
                    getDialog().setSize(getDialog().getWidth(), newHeight);
                }
            });
        }
        getDialog().setResizable(false);
    }
    
    /**
     * Get the scale factor for the dialog size (as compared to the parent window). The
     * default returned here is 55% (.55). Subclasses can override this to change the size
     * as desired.
     * @return scale factor
     */
    protected float getDialogScaleFactor() {
        return 0.55f;
    }
}