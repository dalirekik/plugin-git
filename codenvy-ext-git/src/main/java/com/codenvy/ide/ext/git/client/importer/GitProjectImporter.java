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
package com.codenvy.ide.ext.git.client.importer;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ImportProject;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.api.projectimporter.ProjectImporter;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

/**
 * Provide possibility for importing source from hosted GIT repository URL.
 *
 * @author Roman Nikitenko
 */
@Singleton
public class GitProjectImporter implements ProjectImporter {

    private ProjectServiceClient   projectService;
    private DtoUnmarshallerFactory dtoUnmarshallerFactory;

    @Inject
    public GitProjectImporter(ProjectServiceClient projectService,
                              DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.projectService = projectService;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
    }

    @Nonnull
    @Override
    public String getId() {
        return "git";
    }

    @Override
    public void importSources(@Nonnull String projectName, @Nonnull ImportProject importProject,
                              @Nonnull final AsyncCallback<ProjectDescriptor> callback) {
        projectService
                .importProject(projectName, false, importProject, new AsyncRequestCallback<ProjectDescriptor>(
                        dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class)) {
                    @Override
                    protected void onSuccess(ProjectDescriptor result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        callback.onFailure(exception);
                    }
                });
    }
}
