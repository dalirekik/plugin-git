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
package com.codenvy.ide.ext.git.client.push;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ext.git.shared.Remote;

import javax.annotation.Nonnull;

/**
 * The view of {@link PushToRemotePresenter}.
 *
 * @author Andrey Plotnikov
 * @author Sergii Leschenko
 */
public interface PushToRemoteView extends View<PushToRemoteView.ActionDelegate> {
    /** Needs for delegate some function into PushToRemote view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Push button. */
        void onPushClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();

        /** Performs any actions appropriate in response to the local branch value changed. */
        void onLocalBranchChanged();

        /** Performs any actions appropriate in response to the repository value changed. */
        void onRepositoryChanged();
    }

    /**
     * Returns selected repository.
     *
     * @return repository.
     */
    @Nonnull
    String getRepository();

    /**
     * Sets available repositories.
     *
     * @param repositories
     *         available repositories
     */
    void setRepositories(@Nonnull Array<Remote> repositories);

    /** @return local branch */
    @Nonnull
    String getLocalBranch();

    /**
     * Set local branches into view.
     *
     * @param branches
     *         local branches
     */
    void setLocalBranches(@Nonnull Array<String> branches);

    /** @return remote branches */
    @Nonnull
    String getRemoteBranch();

    /**
     * Set remote branches into view.
     *
     * @param branches
     *         remote branches
     */
    void setRemoteBranches(@Nonnull Array<String> branches);

    /**
     * Add remote branch into view.
     *
     * @param branch
     *         remote branch
     * @return {@code true} if branch added and {@code false} if branch already exist
     */
    boolean addRemoteBranch(@Nonnull String branch);

    /**
     * Selects pointed local branch
     *
     * @param branch
     *         local branch to select
     */
    void selectLocalBranch(@Nonnull String branch);

    /**
     * Selects pointed remote branch
     *
     * @param branch
     *         remote branch to select
     */
    void selectRemoteBranch(@Nonnull String branch);

    /**
     * Change the enable state of the push button.
     *
     * @param enabled
     *         {@code true} to enable the button, {@code false} to disable it
     */
    void setEnablePushButton(boolean enabled);

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}