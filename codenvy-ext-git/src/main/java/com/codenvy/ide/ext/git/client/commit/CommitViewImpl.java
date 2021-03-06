/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.git.client.commit;

import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.GitResources;
import com.codenvy.ide.ui.window.Window;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;

/**
 * The implementation of {@link CommitView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class CommitViewImpl extends Window implements CommitView {
    interface CommitViewImplUiBinder extends UiBinder<Widget, CommitViewImpl> {
    }

    private static CommitViewImplUiBinder ourUiBinder = GWT.create(CommitViewImplUiBinder.class);

    @UiField
    CheckBox all;
    @UiField
    CheckBox amend;
    @UiField
    TextArea message;
    Button   btnCommit;
    Button   btnCancel;
    @UiField(provided = true)
    final   GitResources            res;
    @UiField(provided = true)
    final   GitLocalizationConstant locale;
    private ActionDelegate          delegate;

    /**
     * Create view.
     *
     * @param res
     * @param locale
     */
    @Inject
    protected CommitViewImpl(GitResources res, GitLocalizationConstant locale) {
        this.res = res;
        this.locale = locale;
        this.ensureDebugId("git-commit-window");

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setTitle(locale.commitTitle());
        this.setWidget(widget);
        
        btnCancel = createButton(locale.buttonCancel(), "git-commit-cancel", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onCancelClicked();
            }
        });
        btnCommit = createButton(locale.buttonCommit(), "git-commit-commit", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onCommitClicked();
            }
        });
        btnCommit.addStyleName(resources.centerPanelCss().blueButton());

        getFooter().add(btnCommit);
        getFooter().add(btnCancel);
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getMessage() {
        return message.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setMessage(@Nonnull String message) {
        this.message.setText(message);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAllFilesInclued() {
        return all.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setAllFilesInclude(boolean isAllFiles) {
        all.setValue(isAllFiles);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAmend() {
        return amend.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setAmend(boolean isAmend) {
        amend.setValue(isAmend);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableCommitButton(boolean enable) {
        btnCommit.setEnabled(enable);
    }

    /** {@inheritDoc} */
    @Override
    public void focusInMessageField() {
        message.setFocus(true);
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("message")
    public void onMessageChanged(KeyUpEvent event) {
        delegate.onValueChanged();
    }

    /** {@inheritDoc} */
    @Override
    protected void onClose() {
    }
}