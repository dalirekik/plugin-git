/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.git.client.action;

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.GitResources;
import com.codenvy.ide.ext.git.client.history.HistoryPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
@Singleton
public class HistoryAction extends GitAction {
    private final HistoryPresenter     presenter;
    private final AnalyticsEventLogger eventLogger;

    @Inject
    public HistoryAction(HistoryPresenter presenter,
                         ResourceProvider resourceProvider,
                         GitResources resources,
                         GitLocalizationConstant constant,
                         AnalyticsEventLogger eventLogger) {
        super(constant.historyControlTitle(), constant.historyControlPrompt(), null, resources.showHistory(), resourceProvider);
        this.presenter = presenter;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: Git show history");
        presenter.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setVisible(getActiveProject() != null);
        e.getPresentation().setEnabled(isGitRepository());
    }
}