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
package org.springframework.richclient.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.event.EventListenerList;

import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.config.CommandButtonConfigurer;
import org.springframework.richclient.command.config.CommandConfigurer;
import org.springframework.richclient.command.config.CommandFaceDescriptor;
import org.springframework.richclient.command.support.ButtonBarGroupContainerPopulator;
import org.springframework.richclient.command.support.SimpleGroupContainerPopulator;
import org.springframework.richclient.command.support.ToggleButtonPopupListener;
import org.springframework.richclient.core.UIConstants;
import org.springframework.richclient.factory.ButtonFactory;
import org.springframework.richclient.factory.MenuFactory;
import org.springframework.richclient.util.GuiStandardUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.jgoodies.forms.layout.Size;
import com.jgoodies.plaf.HeaderStyle;
import com.jgoodies.plaf.Options;

/**
 * @author Keith Donald
 */
public class CommandGroup extends AbstractCommand {

    private EventListenerList listenerList;

    private GroupMemberList memberList = new GroupMemberList();

    private CommandRegistry commandRegistry;

    public CommandGroup() {
        super();
    }

    public CommandGroup(String groupId) {
        super(groupId);
    }

    public CommandGroup(String groupId, CommandFaceDescriptor face) {
        super(groupId, face);
    }

    public CommandGroup(String groupId, CommandRegistry commandRegistry) {
        super(groupId);
        setCommandRegistry(commandRegistry);
    }

    public CommandGroup(String id, String encodedLabel) {
        super(id, encodedLabel);
    }

    public CommandGroup(String id, String encodedLabel, Icon icon,
            String caption) {
        super(id, encodedLabel, icon, caption);
    }

    protected void addInternal(AbstractCommand command) {
        this.memberList.add(new SimpleGroupMember(this, command));
    }

    protected void addInlinedInternal(CommandGroup group) {
        this.memberList.add(new InlinedGroupMember(this, group));
    }

    protected void addLazyInternal(String commandId, boolean inlinedGroup) {
        this.memberList.add(new LazyGroupMember(this, commandId, inlinedGroup));
    }

    protected void addSeparatorInternal() {
        this.memberList.add(new SeparatorGroupMember());
    }

    protected void addGlueInternal() {
        this.memberList.add(new GlueGroupMember());
    }

    public void setCommandRegistry(CommandRegistry registry) {
        if (!ObjectUtils.nullSafeEquals(this.commandRegistry, registry)) {

            //@TODO should groups listen to command registration events if
            // they've
            //got lazy members that haven't been instantiated? Or are
            // targetable
            //commands lightweight enough?
            if (logger.isDebugEnabled()) {
                logger.debug("Setting registry " + registry
                        + " for command group '" + getId() + "'");
            }
            this.commandRegistry = registry;
        }
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        getMemberList().setContainersVisible(visible);
    }

    /**
     * Creates a command group with a single command member.
     * 
     * @param groupId
     * @param members
     * @return
     */
    public static CommandGroup createCommandGroup(AbstractCommand member) {
        return createCommandGroup(null, new Object[] { member });
    }

    /**
     * Creates a command group, configuring the group using the ObjectConfigurer
     * service (pulling visual configuration properties from an external
     * source). This method will also auto-configure contained Command members
     * that have not yet been configured.
     * 
     * @param members
     */
    public static CommandGroup createCommandGroup(Object[] members) {
        return createCommandGroup(null, members, null);
    }

    /**
     * Creates a command group, configuring the group using the ObjectConfigurer
     * service (pulling visual configuration properties from an external
     * source). This method will also auto-configure contained Command members
     * that have not yet been configured.
     * 
     * @param groupId
     * @param members
     * @return
     */
    public static CommandGroup createCommandGroup(String groupId,
            Object[] members) {
        return createCommandGroup(groupId, members, null);
    }

    /**
     * Creates a command group, configuring the group using the ObjectConfigurer
     * service (pulling visual configuration properties from an external
     * source). This method will also auto-configure contained Command members
     * that have not yet been configured.
     * 
     * @param groupId
     * @param members
     * @return
     */
    public static CommandGroup createCommandGroup(String groupId,
            Object[] members, CommandConfigurer configurer) {
        if (configurer == null) {
            configurer = Application.services();
        }
        CommandGroupFactoryBean groupFactory = new CommandGroupFactoryBean(
                groupId, null, configurer, members);
        return groupFactory.getCommandGroup();
    }

    protected CommandRegistry getCommandRegistry() {
        return commandRegistry;
    }

    public void add(AbstractCommand command) {
        add(command, true);
    }

    public void add(AbstractCommand command, boolean rebuild) {
        if (command == null) { return; }
        if (containsDirectly(command)) { return; }
        getMemberList().append(new SimpleGroupMember(this, command));
        rebuildIfNecessary(rebuild);
    }

    public void add(String groupMemberPath, AbstractCommand command) {
        AbstractCommand c = find(groupMemberPath);
        assertIsGroup(groupMemberPath, c);
        ((CommandGroup)c).add(command);
    }

    private void assertIsGroup(String groupMemberPath, AbstractCommand c) {
        Assert.notNull(c, "Command at path '" + groupMemberPath
                + "' does not exist.");
        Assert.isTrue((c instanceof CommandGroup), "Command at path '"
                + groupMemberPath + "' is not a group.");
    }

    public void add(String groupMemberPath, AbstractCommand command,
            boolean rebuild) {
        AbstractCommand c = find(groupMemberPath);
        assertIsGroup(groupMemberPath, c);
        ((CommandGroup)c).add(command, rebuild);
    }

    public void remove(AbstractCommand command) {
        remove(command, true);
    }

    public void remove(AbstractCommand command, boolean rebuild) {
        if (command == null) { return; }

        ExpansionPointGroupMember expansionPoint = getMemberList()
                .getExpansionPoint();
        GroupMember member = expansionPoint.getMemberFor(command.getId());

        if (member != null) {
            expansionPoint.remove(member);
            rebuildIfNecessary(rebuild);
        }
    }

    public void remove(String groupMemberPath, AbstractCommand command) {
        AbstractCommand c = find(groupMemberPath);
        assertIsGroup(groupMemberPath, c);
        ((CommandGroup)c).remove(command);
    }

    public void remove(String groupMemberPath, AbstractCommand command,
            boolean rebuild) {
        AbstractCommand c = find(groupMemberPath);
        assertIsGroup(groupMemberPath, c);
        ((CommandGroup)c).remove(command, rebuild);
    }

    public void addSeparator() {
        addSeparator(true);
    }

    public void addSeparator(boolean rebuild) {
        getMemberList().append(new SeparatorGroupMember());
        rebuildIfNecessary(rebuild);
    }

    public void addGlue() {
        addGlue(true);
    }

    public void addGlue(boolean rebuild) {
        getMemberList().append(new GlueGroupMember());
        rebuildIfNecessary(rebuild);
    }

    private void rebuildIfNecessary(boolean rebuild) {
        if (rebuild) {
            rebuildAllControls();
            fireMembersChanged();
        }
    }

    protected void rebuildAllControls() {
        if (logger.isDebugEnabled()) {
            logger.debug("Rebuilding all GUI controls for command group '"
                    + getId() + "'");
        }
        getMemberList().rebuildControls();
    }

    protected GroupMemberList getMemberList() {
        return memberList;
    }

    protected Iterator memberIterator() {
        return getMemberList().iterator();
    }

    public int size() {
        return getMemberCount();
    }

    public boolean isAllowedMember(AbstractCommand proposedMember) {
        return true;
    }

    /**
     * Search for and return the command contained by this group with the
     * specified path. Nested paths should be deliniated by the "/" character;
     * for example, "fileGroup/newGroup/simpleFileCommand". The returned command
     * may be a group or an action command.
     * 
     * @param memberPath
     *            the path of the command, with nested levels deliniated by the
     *            "/" path separator.
     * @return the command at the specified member path, or <code>null</code>
     *         if no was command found.
     */
    public AbstractCommand find(String memberPath) {
        if (logger.isDebugEnabled()) {
            logger.debug("Searching for command with nested path '"
                    + memberPath + "'");
        }
        String[] paths = StringUtils
                .delimitedListToStringArray(memberPath, "/");
        CommandGroup currentGroup = this;
        // fileGroup/newGroup/newJavaProject
        for (int i = 0; i < paths.length; i++) {
            String memberId = paths[i];
            if (i < paths.length - 1) {
                // must be a nested group
                currentGroup = currentGroup.findCommandGroupMember(memberId);
            }
            else {
                // is last path element; can be a group or action
                return currentGroup.findCommandMember(memberId);
            }
        }
        return null;
    }

    private CommandGroup findCommandGroupMember(String groupId) {
        AbstractCommand c = findCommandMember(groupId);
        Assert.isTrue((c instanceof CommandGroup), "Command with id '"
                + groupId + "' is not a group.");
        return (CommandGroup)c;
    }

    private AbstractCommand findCommandMember(String commandId) {
        Iterator it = memberList.iterator();
        while (it.hasNext()) {
            GroupMember member = (GroupMember)it.next();
            if (member.managesCommand(commandId)) { return member.getCommand(); }
        }
        logger.warn("No command with id '" + commandId
                + "' is nested within this group (" + getId()
                + "); returning null");
        return null;
    }

    public int getMemberCount() {
        return getMemberList().size();
    }

    public boolean contains(AbstractCommand c) {
        return contains(c.getId());
    }

    boolean contains(String commandId) {
        return getMemberList().contains(commandId);
    }

    public boolean containsDirectly(AbstractCommand c) {
        return containsDirectly(c.getId());
    }

    boolean containsDirectly(String commandId) {
        return getMemberList().containsDirectly(commandId);
    }

    public void reset() {
        ExpansionPointGroupMember expansionPoint = getMemberList()
                .getExpansionPoint();
        if (!expansionPoint.isEmpty()) {
            expansionPoint.clear();
            rebuildIfNecessary(true);
        }
    }

    /**
     * Creates a pull down button that, when clicked, displays a popup menu that
     * displays this group's members.
     * 
     * @see org.springframework.richclient.command.AbstractCommand#createButton()
     */
    public AbstractButton createButton() {
        return createButton(getButtonFactory(), getMenuFactory());
    }

    public AbstractButton createButton(ButtonFactory factory) {
        return createButton(factory, getMenuFactory());
    }

    public AbstractButton createButton(ButtonFactory factory,
            CommandButtonConfigurer configurer) {
        return createButton(factory, getMenuFactory(), configurer);
    }

    public AbstractButton createButton(ButtonFactory buttonFactory,
            MenuFactory menuFactory) {
        return createButton(buttonFactory, menuFactory,
                getPullDownMenuButtonConfigurer());
    }

    protected CommandButtonConfigurer getPullDownMenuButtonConfigurer() {
        return getCommandServices().getPullDownMenuButtonConfigurer();
    }

    public AbstractButton createButton(ButtonFactory buttonFactory,
            MenuFactory menuFactory, CommandButtonConfigurer buttonConfigurer) {
        JToggleButton button = buttonFactory.createToggleButton();
        attach(button, buttonConfigurer);
        JPopupMenu popup = menuFactory.createPopupMenu();
        bindMembers(button, popup, menuFactory, getMenuItemButtonConfigurer());
        ToggleButtonPopupListener.bind(button, popup);
        return button;
    }

    public JMenuItem createMenuItem() {
        return createMenuItem(getMenuFactory());
    }

    public JMenuItem createMenuItem(MenuFactory factory) {
        JMenu menu = factory.createMenu();
        attach(menu);
        bindMembers(menu, menu, factory, getMenuItemButtonConfigurer());
        return menu;
    }

    public JPopupMenu createPopupMenu() {
        return createPopupMenu(getMenuFactory());
    }

    public JPopupMenu createPopupMenu(MenuFactory factory) {
        JPopupMenu popup = factory.createPopupMenu();
        bindMembers(popup, popup, factory, getMenuItemButtonConfigurer());
        return popup;
    }

    public JToolBar createToolBar() {
        return createToolBar(getButtonFactory());
    }

    public JToolBar createToolBar(ButtonFactory factory) {
        JToolBar toolbar = createNewToolBar(getText());
        bindMembers(toolbar, toolbar, factory, getToolBarButtonConfigurer());
        toolbar.setEnabled(false);
        toolbar.setVisible(true);
        return toolbar;
    }

    public JMenuBar createMenuBar() {
        return createMenuBar(getMenuFactory());
    }

    public JMenuBar createMenuBar(MenuFactory factory) {
        JMenuBar menubar = newMenuBar();
        bindMembers(menubar, menubar, factory, getMenuItemButtonConfigurer());
        return menubar;
    }

    protected JToolBar createNewToolBar(String text) {
        JToolBar toolBar = new JToolBar(text);
        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);
        return toolBar;

    }

    protected JMenuBar newMenuBar() {
        return new JMenuBar();
    }

    public JComponent createButtonBar() {
        return createButtonBar(null);
    }

    public JComponent createButtonBar(Size minimumButtonSize) {
        Iterator members = getMemberList().iterator();
        List buttons = new ArrayList();

        ButtonBarGroupContainerPopulator container = new ButtonBarGroupContainerPopulator();
        container.setMinimumButtonSize(minimumButtonSize);

        while (members.hasNext()) {
            GroupMember member = (GroupMember)members.next();
            if (member.getCommand() instanceof CommandGroup) {
                member.fill(container, getButtonFactory(),
                        getPullDownMenuButtonConfigurer(),
                        Collections.EMPTY_LIST);
            }
            else {
                member.fill(container, getButtonFactory(),
                        getDefaultButtonConfigurer(), Collections.EMPTY_LIST);
            }
        }
        container.onPopulated();
        return GuiStandardUtils.attachBorder(container.getButtonBar(),
                GuiStandardUtils
                        .createTopAndBottomBorder(UIConstants.TWO_SPACES));
    }

    private void bindMembers(Object owner, JComponent memberContainer,
            Object controlFactory, CommandButtonConfigurer configurer) {
        getMemberList().bindMembers(owner,
                new SimpleGroupContainerPopulator(memberContainer),
                controlFactory, configurer);
    }

    public void addGroupListener(CommandGroupListener l) {
        if (listenerList == null) {
            listenerList = new EventListenerList();
        }
        listenerList.add(CommandGroupListener.class, l);
    }

    public void removeGroupListener(CommandGroupListener l) {
        Assert.notNull(listenerList,
                "Listener list has not yet been instantiated!");
        listenerList.remove(CommandGroupListener.class, l);
    }

    protected void fireMembersChanged() {
        if (listenerList == null) { return; }
        CommandGroupEvent event = null;
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == CommandGroupListener.class) {
                if (event == null) {
                    event = new CommandGroupEvent(this);
                }
                ((CommandGroupListener)listeners[i + 1]).membersChanged(event);
            }
        }
    }
}