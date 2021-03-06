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
package com.codenvy.ide.ext.git.client.remove;

import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorInput;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.api.projecttree.generic.FolderNode;
import com.codenvy.ide.api.projecttree.generic.ProjectNode;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;
import java.util.List;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link RemoveFromIndexPresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class RemoveFromIndexPresenterTest extends BaseTest {
    public static final boolean  REMOVED = true;
    public static final String   MESSAGE = "message";
    public static final SafeHtml SAFE_HTML = mock(SafeHtml.class);
    @Mock
    private RemoveFromIndexView      view;
    private RemoveFromIndexPresenter presenter;
    @Mock
    private EditorAgent              editorAgent;
    @Mock
    private EditorPartPresenter      partPresenter;
    @Mock
    private EditorInput              editorInput;
    @Mock
    private FileNode               file;

    @Override
    public void disarm() {
        super.disarm();

        presenter = new RemoveFromIndexPresenter(view,
                                                 eventBus,
                                                 service,
                                                 constant,
                                                 appContext,
                                                 selectionAgent,
                                                 notificationManager,
                                                 editorAgent);
        StringMap<EditorPartPresenter> partPresenterMap = Collections.createStringMap();
        partPresenterMap.put("partPresenter", partPresenter);

        when(editorAgent.getOpenedEditors()).thenReturn(partPresenterMap);
        when(partPresenter.getEditorInput()).thenReturn(editorInput);
        when(editorInput.getFile()).thenReturn(file);
    }

    @Test
    public void testShowDialogWhenSomeFileIsSelected() throws Exception {
        String filePath = PROJECT_PATH + PROJECT_NAME;
        Selection selection = mock(Selection.class);
        FileNode file = mock(FileNode.class);
        when(file.getPath()).thenReturn(filePath);
        when(selection.getFirstElement()).thenReturn(file);
        when(selectionAgent.getSelection()).thenReturn(selection);
        when(constant.removeFromIndexFile(anyString())).thenReturn(SAFE_HTML);
        when(SAFE_HTML.asString()).thenReturn(MESSAGE);

        presenter.showDialog();

        verify(view).setMessage(eq(MESSAGE));
        verify(view).setRemoved(eq(!REMOVED));
        verify(view).showDialog();
        verify(constant).removeFromIndexFile(eq(PROJECT_NAME));
    }

    @Test
    public void testShowDialogWhenSomeFolderIsSelected() throws Exception {
        String folderPath = PROJECT_PATH + PROJECT_NAME;
        Selection selection = mock(Selection.class);
        FolderNode folder = mock(FolderNode.class);
        when(folder.getPath()).thenReturn(folderPath);
        when(selection.getFirstElement()).thenReturn(folder);
        when(selectionAgent.getSelection()).thenReturn(selection);
        when(constant.removeFromIndexFolder(anyString())).thenReturn(SAFE_HTML);
        when(SAFE_HTML.asString()).thenReturn(MESSAGE);

        presenter.showDialog();

        verify(view).setMessage(eq(MESSAGE));
        verify(view).setRemoved(eq(!REMOVED));
        verify(view).showDialog();
        verify(constant).removeFromIndexFolder(eq(PROJECT_NAME));
    }

    @Test
    public void testShowDialogWhenRootFolderIsSelected() throws Exception {
        Selection selection = mock(Selection.class);
        ProjectNode project = mock(ProjectNode.class);
        when(project.getPath()).thenReturn(PROJECT_PATH);
        when(selection.getFirstElement()).thenReturn(project);
        when(selectionAgent.getSelection()).thenReturn(selection);
        when(constant.removeFromIndexAll()).thenReturn(MESSAGE);

        presenter.showDialog();

        verify(view).setMessage(eq(MESSAGE));
        verify(view).setRemoved(eq(!REMOVED));
        verify(view).showDialog();
        verify(constant).removeFromIndexAll();
    }

    @Test
    public void testOnRemoveClickedWhenRemoveRequestIsSuccessful() throws Exception {
        when(view.isRemoved()).thenReturn(REMOVED);
        when(selectionAgent.getSelection()).thenReturn(null);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, EMPTY_TEXT);
                return callback;
            }
        }).when(service).remove((ProjectDescriptor)anyObject(), (List<String>)anyObject(), anyBoolean(),
                                (AsyncRequestCallback<String>)anyObject());

        presenter.showDialog();
        presenter.onRemoveClicked();

        verify(service).remove(eq(rootProjectDescriptor), (List<String>)anyObject(), eq(REMOVED),
                               (AsyncRequestCallback<String>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(constant).removeFilesSuccessfull();
        verify(view).close();
    }

    @Test
    public void testOnRemoveClickedWhenRemoveRequestIsFailed() throws Exception {
        when(view.isRemoved()).thenReturn(REMOVED);
        when(selectionAgent.getSelection()).thenReturn(null);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[3];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).remove((ProjectDescriptor)anyObject(), (List<String>)anyObject(), anyBoolean(),
                                (AsyncRequestCallback<String>)anyObject());

        presenter.showDialog();
        presenter.onRemoveClicked();

        verify(service).remove(eq(rootProjectDescriptor), (List<String>)anyObject(), eq(REMOVED),
                               (AsyncRequestCallback<String>)anyObject());
        verify(constant).removeFilesFailed();
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(view).close();
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();

        verify(view).close();
    }
}