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
package org.springframework.richclient.wizard;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.core.UIConstants;
import org.springframework.richclient.dialog.DialogPage;
import org.springframework.richclient.dialog.MessageAreaChangeListener;
import org.springframework.richclient.dialog.MessageAreaModel;
import org.springframework.richclient.dialog.TitledApplicationDialog;
import org.springframework.richclient.util.GuiStandardUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Dialog for wizards.
 * 
 * @author Keith Donald
 */
public class WizardDialog extends TitledApplicationDialog implements WizardContainer, MessageAreaChangeListener,
		PropertyChangeListener {
	private static final String NEXT_MESSAGE_CODE = "wizard.next";

	private static final String BACK_MESSAGE_CODE = "wizard.back";

	private Wizard wizard;

	private ActionCommand nextCommand;

	private ActionCommand backCommand;

	private WizardPage currentPage;

	private int largestPageWidth;

	private int largestPageHeight;

	public WizardDialog() {
		this(null);
	}

	public WizardDialog(Wizard wizard) {
		super();
		setWizard(wizard);
		setResizable(true);
	}

	public void setWizard(Wizard wizard) {
		if (this.wizard != wizard) {
			if (this.wizard != null) {
				this.wizard.setContainer(null);
			}
			this.wizard = wizard;
			if (this.wizard != null) {
				this.wizard.setContainer(this);
				this.setTitle(wizard.getTitle());
			}
		}
	}

	protected String getFinishFaceConfigurationKey() {
		return "finishCommand";
	}

	protected JComponent createTitledDialogContentPane() {
		wizard.addPages();
		createPageControls();
		WizardPage startPage = wizard.getStartingPage();
		Assert.notNull(startPage, "No starting page returned; unable to show wizard.");
		JComponent control = startPage.getControl();
		control.setPreferredSize(getLargestPageSize());
		return control;
	}

	private Dimension getLargestPageSize() {
		return new Dimension(largestPageWidth + UIConstants.ONE_SPACE, largestPageHeight + UIConstants.ONE_SPACE);
	}

	protected Object[] getCommandGroupMembers() {
		if (!wizard.needsPreviousAndNextButtons()) {
			return super.getCommandGroupMembers();
		}
		nextCommand = new ActionCommand("nextCommand") {
			public void doExecuteCommand() {
				onNext();
			}
		};
		backCommand = new ActionCommand("backCommand") {
			public void doExecuteCommand() {
				onBack();
			}
		};
		backCommand.setEnabled(false);
		return new AbstractCommand[] { backCommand, nextCommand, getFinishCommand(), getCancelCommand() };
	}

	protected void onAboutToShow() {
		showPage(wizard.getStartingPage());
		super.onAboutToShow();
	}

	/**
	 * Allow the wizard's pages to pre-create their page controls. This allows
	 * the wizard dialog to open to the correct size.
	 */
	private void createPageControls() {
		WizardPage[] pages = wizard.getPages();
		for (int i = 0; i < pages.length; i++) {
			JComponent c = pages[i].getControl();
			GuiStandardUtils.attachDialogBorder(c);
			Dimension size = c.getPreferredSize();
			if (size.width > largestPageWidth) {
				largestPageWidth = size.width;
			}
			if (size.height > largestPageHeight) {
				largestPageHeight = size.height;
			}
		}
	}

	public void showPage(WizardPage page) {
		if (this.currentPage != page) {
			if (this.currentPage != null) {
				this.currentPage.removeMessageAreaChangeListener(this);
				this.currentPage.removePropertyChangeListener(this);
			}
			this.currentPage = page;
			this.currentPage.addMessageAreaChangeListener(this);
			this.currentPage.addPropertyChangeListener(this);
			updateDialog();
			setContentPane(page.getControl());
		}
		this.currentPage.onAboutToShow();
		this.currentPage.setVisible(true);
	}

	public WizardPage getCurrentPage() {
		return currentPage;
	}

	protected void onBack() {
		WizardPage newPage = currentPage.getPreviousPage();
		if (newPage == null || newPage == currentPage) {
			throw new IllegalStateException("No such page.");
		}
		showPage(newPage);
	}

	protected void onNext() {
		WizardPage newPage = currentPage.getNextPage();
		if (newPage == null || newPage == currentPage) {
			throw new IllegalStateException("No such page.");
		}
		showPage(newPage);
	}

	protected boolean onFinish() {
		return wizard.performFinish();
	}

	protected void onCancel() {
		if (wizard.performCancel()) {
			super.onCancel();
		}
	}

	/**
	 * Updates this dialog's controls to reflect the current page.
	 */
	protected void updateDialog() {
		if (!isControlCreated()) {
			throw new IllegalStateException("Container controls not initialized - update not allowed.");
		}

		// Update the title bar
		updateTitleBar();

		// Update the message line
		updateMessage();

		// Update the buttons
		updateButtons();
	}

	/**
	 * Updates the title bar (title, description, and image) to reflect the
	 * state of the currently active page in this container.
	 */
	protected void updateTitleBar() {
		setTitleAreaText(currentPage.getTitle());
		setTitleAreaImage(currentPage.getImage());
		setDescription(currentPage.getDescription());
	}

	/**
	 * Updates the message (or error message) shown in the message line to
	 * reflect the state of the currently active page in this container.
	 */
	protected void updateMessage() {
		String errorMessage = currentPage.getErrorMessage();
		if (StringUtils.hasText(errorMessage)) {
			setErrorMessage(errorMessage);
		}
		else {
			setMessage(currentPage.getMessage());
		}
	}

	private void updateButtons() {
		if (wizard.needsPreviousAndNextButtons()) {
			backCommand.setEnabled(currentPage.getPreviousPage() != null);
			nextCommand.setEnabled(canFlipToNextPage());
		}
		setFinishEnabled(wizard.canFinish());
		if (canFlipToNextPage() && !wizard.canFinish()) {
			registerDefaultCommand(nextCommand);
		}
		else {
			registerDefaultCommand();
		}
	}

	private boolean canFlipToNextPage() {
		return currentPage.canFlipToNextPage();
	}

	public void messageUpdated(MessageAreaModel source) {
		updateMessage();
	}

	public void propertyChange(PropertyChangeEvent e) {
		if (DialogPage.PAGE_COMPLETE_PROPERTY.equals(e.getPropertyName())) {
			updateButtons();
		}
		else if (DialogPage.DESCRIPTION_PROPERTY.equals(e.getPropertyName())) {
			updateTitleBar();
		}
	}
}