/*
 * Copyright 2002-2004 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.richclient.command.config;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import junit.framework.TestCase;

/**
 * @author Peter De Bruycker
 */
public class CommandButtonIconInfoTest extends TestCase {

    private Icon icon;

    private Icon selectedIcon;

    private Icon rolloverIcon;

    private Icon disabledIcon;

    private Icon pressedIcon;

    private CommandButtonIconInfo completeInfo;

    public void testConstructor() {
        CommandButtonIconInfo info = new CommandButtonIconInfo(icon);
        assertEquals(icon, info.getIcon());
        assertNull(info.getSelectedIcon());
        assertNull(info.getRolloverIcon());
        assertNull(info.getDisabledIcon());
        assertNull(info.getPressedIcon());
    }

    public void testConstructor2() {
        CommandButtonIconInfo info = new CommandButtonIconInfo(icon,
                selectedIcon);
        assertEquals(icon, info.getIcon());
        assertEquals(selectedIcon, info.getSelectedIcon());
        assertNull(info.getRolloverIcon());
        assertNull(info.getDisabledIcon());
        assertNull(info.getPressedIcon());
    }

    public void testConstructor3() {
        CommandButtonIconInfo info = new CommandButtonIconInfo(icon,
                selectedIcon, rolloverIcon);
        assertEquals(icon, info.getIcon());
        assertEquals(selectedIcon, info.getSelectedIcon());
        assertEquals(rolloverIcon, info.getRolloverIcon());
        assertNull(info.getDisabledIcon());
        assertNull(info.getPressedIcon());
    }

    public void testConstructor4() {
        CommandButtonIconInfo info = new CommandButtonIconInfo(icon,
                selectedIcon, rolloverIcon, disabledIcon, pressedIcon);
        assertEquals(icon, info.getIcon());
        assertEquals(selectedIcon, info.getSelectedIcon());
        assertEquals(rolloverIcon, info.getRolloverIcon());
        assertEquals(disabledIcon, info.getDisabledIcon());
        assertEquals(pressedIcon, info.getPressedIcon());
    }
    
    public void testConfigureWithNullButton() {
        CommandButtonIconInfo info = new CommandButtonIconInfo(icon);
        try {
            info.configure(null);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            pass();
        }
    }
    
    public void testConfigureWithJButton() {
        JButton button = new JButton("Test");
        JButton result = (JButton) completeInfo.configure(button);
        assertSame(button, result);
        
        assertEquals(icon, button.getIcon());
        assertEquals(selectedIcon, button.getSelectedIcon());
        assertEquals(rolloverIcon, button.getRolloverIcon());
        assertEquals(disabledIcon, button.getDisabledIcon());
        assertEquals(pressedIcon, button.getPressedIcon());
    }
    
    
    public void testConfigureWithJMenuItem() {
        JMenuItem button = new JMenuItem("Test");
        JMenuItem result = (JMenuItem) completeInfo.configure(button);
        assertSame(button, result);
        
        assertEquals(icon, button.getIcon());
        assertNull(button.getSelectedIcon());
        assertNull(button.getRolloverIcon());
        assertEquals(disabledIcon, button.getDisabledIcon());
        assertNull(button.getPressedIcon());
    }
    
    public void testConfigureWithJMenu() {
        JMenu button = new JMenu("Test");
        button.setIcon(icon);
        button.setSelectedIcon(selectedIcon);
        button.setRolloverIcon(rolloverIcon);
        button.setDisabledIcon(disabledIcon);
        button.setPressedIcon(pressedIcon);
        
        JMenuItem result = (JMenuItem) completeInfo.configure(button);
        assertSame(button, result);
        
        assertNull(button.getIcon());
        assertNull(button.getSelectedIcon());
        assertNull(button.getRolloverIcon());
        assertNull(button.getDisabledIcon());
        assertNull(button.getPressedIcon());
    }
    
    private static void pass()
    {
        // test passes
    }
    
    private static class TestIcon implements Icon {

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.Icon#getIconHeight()
         */
        public int getIconHeight() {
            return 16;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.Icon#getIconWidth()
         */
        public int getIconWidth() {
            return 16;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.Icon#paintIcon(java.awt.Component,
         *      java.awt.Graphics, int, int)
         */
        public void paintIcon(Component arg0, Graphics arg1, int arg2, int arg3) {
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        icon = new TestIcon();
        selectedIcon = new TestIcon();
        rolloverIcon = new TestIcon();
        disabledIcon = new TestIcon();
        pressedIcon = new TestIcon();
        
        completeInfo = new CommandButtonIconInfo(icon,
                selectedIcon, rolloverIcon, disabledIcon, pressedIcon);

    }

}