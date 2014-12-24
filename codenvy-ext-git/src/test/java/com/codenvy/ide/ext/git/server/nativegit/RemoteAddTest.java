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
package com.codenvy.ide.ext.git.server.nativegit;

import com.codenvy.ide.ext.git.server.GitConnection;
import com.codenvy.ide.ext.git.server.GitConnectionFactory;
import com.codenvy.ide.ext.git.server.GitException;
import com.codenvy.ide.ext.git.server.nativegit.commands.RemoteListCommand;
import com.codenvy.ide.ext.git.shared.*;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;

import static com.codenvy.api.core.util.LineConsumerFactory.NULL;
import static com.codenvy.ide.ext.git.shared.BranchListRequest.LIST_REMOTE;
import static org.testng.Assert.assertEquals;

/**
 * @author Eugene Voevodin
 */
public class RemoteAddTest extends BaseTest {

    @Test
    public void testSimpleRemoteAdd() throws GitException {
        NativeGit nativeGit = new NativeGit(getRepository().toFile());
        RemoteListCommand rList = nativeGit.createRemoteListCommand();
        int beforeCount = rList.execute().size();
        getConnection().remoteAdd(newDTO(RemoteAddRequest.class).withName("origin").withUrl("some.url"));
        int afterCount = rList.execute().size();
        assertEquals(afterCount, beforeCount + 1);
    }

    @Test
    public void testAddNotAllBranchesTracked() throws GitException, URISyntaxException {
        //given
        File repository2 = new File(getTarget().toAbsolutePath().toString(), "repository2");
        repository2.mkdir();
        forClean.add(repository2);
        NativeGit nativeGit = new NativeGit(getRepository().toFile());
        nativeGit.createBranchCreateCommand().setBranchName("b1").execute();
        nativeGit.createBranchCreateCommand().setBranchName("b2").execute();
        nativeGit.createBranchCreateCommand().setBranchName("b3").execute();
        final GitConnectionFactory factory = new NativeGitConnectionFactory(null, null, null);
        GitConnection connection = factory.getConnection(repository2,
                newDTO(GitUser.class).withName("test_name")
                        .withEmail("test@email"),
                NULL);
        connection.init(newDTO(InitRequest.class));
        //add remote tracked only to b1 and b3 branches
        RemoteAddRequest remoteAddRequest = newDTO(RemoteAddRequest.class)
                .withName("origin")
                .withUrl(getRepository().toFile().getAbsolutePath());
        remoteAddRequest.setBranches(Arrays.asList("b1", "b3"));
        //when
        connection.remoteAdd(remoteAddRequest);
        //then
        //make pull
        new NativeGit(repository2).createPullCommand().setRemote("origin").execute();
        validateBranchList(connection.branchList(newDTO(BranchListRequest.class).withListMode(LIST_REMOTE)),
                Arrays.asList(
                        newDTO(Branch.class).withName("refs/remotes/origin/b1")
                                .withDisplayName("origin/b1").withActive(false).withRemote(true),
                        newDTO(Branch.class).withName("refs/remotes/origin/b3")
                                .withDisplayName("origin/b3").withActive(false).withRemote(true)
                )
        );
    }
}
