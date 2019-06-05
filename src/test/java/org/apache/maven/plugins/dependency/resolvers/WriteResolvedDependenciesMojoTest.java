package org.apache.maven.plugins.dependency.resolvers;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.dependency.AbstractDependencyMojoTestCase;
import org.apache.maven.plugins.dependency.utils.DependencyStatusSets;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class WriteResolvedDependenciesMojoTest
    extends AbstractDependencyMojoTestCase
{
    protected void setUp()
        throws Exception
    {
        // required for mojo lookups to work
        super.setUp( "dss", true );
    }

    public void testDependencyStatusLog()
            throws IOException, MojoExecutionException {
        Set<Artifact> artifacts = this.stubFactory.getMixedArtifacts();
        doTestDependencyStatusLog( artifacts );
    }

    public void testDependencyStatusLogNullFiles()
            throws IOException, MojoExecutionException {
        this.stubFactory.setCreateFiles( false );
        Set<Artifact> artifacts = this.stubFactory.getMixedArtifacts();
        doTestDependencyStatusLog( artifacts );
    }

    public void testDependencyStatusEmptySet() throws MojoExecutionException {
        doTestDependencyStatusLog( new HashSet<Artifact>() );
    }

    public void doTestDependencyStatusLog( Set<Artifact> artifacts ) throws MojoExecutionException {
        // TODO: implement logger to check correct output
        // this test is just looking for unexpected exceptions.

        WriteListMojo mojo = newMojo( new DependencyStatusSets() );
        System.out.println(mojo.getOutput( false, true, false ));
        mojo.getOutput( true, true, false );

        mojo = newMojo( new DependencyStatusSets( artifacts, null, null ) );
        System.out.println(mojo.getOutput( false, true, false ));
        System.out.println(mojo.getOutput( true, true, false ));

        mojo.writeResolvedDependencies( true, true, false );

        mojo = newMojo( new DependencyStatusSets( null, artifacts, null ) );
        mojo.getOutput( false, true, false );
        mojo.getOutput( true, true, false );

        mojo = newMojo( new DependencyStatusSets( null, null, artifacts ) );
        mojo.getOutput( false, true, false );
        mojo.getOutput( true, true, false );

        mojo = newMojo( new DependencyStatusSets( artifacts, artifacts, null ) );
        mojo.getOutput( false, true, false );
        mojo.getOutput( true, true, false );

        mojo = newMojo( new DependencyStatusSets( null, artifacts, artifacts ) );
        mojo.getOutput( false, true, false );
        mojo.getOutput( true, true, false );

        mojo = newMojo( new DependencyStatusSets( artifacts, null, artifacts ) );
        mojo.getOutput( false, true, false );
        mojo.getOutput( true, true, false );

        mojo = newMojo( new DependencyStatusSets( artifacts, artifacts, artifacts ) );
        mojo.getOutput( false, true, false );
        mojo.getOutput( true, true, false );
        mojo.getOutput( false, false, false );
        mojo.getOutput( true, false, false );
    }

    private WriteListMojo newMojo( final DependencyStatusSets dss )
    {
        WriteListMojo mojo = new WriteListMojo();
        mojo.results = dss;
        return mojo;
    }
}
