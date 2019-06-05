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
 * http://www.apache.org/licenses/LICENSE-2.0
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
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.plugins.dependency.utils.DependencyUtil;
import org.apache.maven.shared.utils.logging.MessageBuilder;
import org.apache.maven.shared.utils.logging.MessageUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;


/**
 * Displays the list of dependencies for this project.
 *
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @since 2.0-alpha-5
 */
@Mojo(name = "write-list", requiresDependencyResolution = ResolutionScope.TEST, threadSafe = true)
public class WriteListMojo
        extends ResolveDependenciesMojo {

    @Parameter(defaultValue = "ResolvedDependenciesFile.json", property = "mdep.resolvedDependenciesFile")
    private String resolvedDependenciesFile = "ResolvedDependenciesFile.json";

    @Parameter(defaultValue = "${project.basedir}", property = "mdep.resolvedDependenciesDirectory")
    private String resolvedDependenciesDirectory = "./target/";

    @Parameter(property = "project.build.directory", required = true)
    private File targetDirectory;


    @Override
    protected void doExecute() throws MojoExecutionException {
        // get sets of dependencies
        results = this.getDependencySets(false, includeParents);
        writeResolvedDependencies(outputAbsoluteArtifactFilename, outputScope, sort);
    }

    public void writeResolvedDependencies(boolean outputAbsoluteArtifactFilename, boolean theOutputScope, boolean theSort) throws MojoExecutionException {
        StringBuilder sb = new StringBuilder();
        sb.append( System.lineSeparator() );
        sb.append( "{" );
        sb.append( System.lineSeparator() );
        sb.append( "   \"type\": \"resolvedDependencies\"," );
        sb.append( System.lineSeparator() );

        sb.append( "   \"artifacts\": [" );
        sb.append( System.lineSeparator() );
        if (results.getResolvedDependencies() == null || results.getResolvedDependencies().isEmpty()) {
            sb.append( System.lineSeparator() );
        } else {
            sb.append(buildArtifactListOutput(results.getResolvedDependencies(), outputAbsoluteArtifactFilename,
                    theOutputScope, theSort));
            int lastIdx = sb.lastIndexOf(",");
            sb.deleteCharAt(lastIdx);
        }
        sb.append( "]" );

        sb.append( System.lineSeparator() );
        sb.append( "}" );

        final File writeFile = new File(resolvedDependenciesDirectory, resolvedDependenciesFile);
        writeToFile( sb, writeFile, "Resolved dependencies file written to: " );
    }


    private void writeToFile(StringBuilder sb, File writeFile, String msg) throws MojoExecutionException {
        try (Writer w = new FileWriter(writeFile, false)) {
            w.write(sb.toString());
            w.flush();
            DependencyUtil.log(msg + writeFile.getCanonicalPath(), getLog());
        } catch (IOException e) {
            throw new MojoExecutionException(e.toString(), e);
        }
    }


    @Override
    protected MessageBuilder formatArtifact(boolean outputAbsoluteArtifactFilename, boolean theOutputScope, Artifact artifact) {
        MessageBuilder messageBuilder = MessageUtils.buffer();

        messageBuilder.a("    {");
        messageBuilder.a(" \"groupId\":").a(quote(artifact.getGroupId())).a(", ");
        messageBuilder.a(" \"artifactId\":").a(quote(artifact.getArtifactId())).a(", ");
        messageBuilder.a(" \"version\":").a(quote(artifact.getVersion()));

        final boolean isFilename = outputAbsoluteArtifactFilename && (artifact.getFile() != null);
        if (theOutputScope)
        {
            messageBuilder.a(", ");
            messageBuilder.a(" \"scope\":").a(quote(artifact.getScope())).a(", ");
        } else {
            messageBuilder.a(" ");
        }
        messageBuilder.a(" \"type\":").a(quote(artifact.getType()));
        if ((artifact.getClassifier() != null) && !artifact.getClassifier().isEmpty())
        {
            messageBuilder.a(", ");
            messageBuilder.a(" \"classifier\":").a(quote(artifact.getClassifier()));
        } else {
            messageBuilder.a(" ");
        }
        if (isFilename)
        {
            messageBuilder.a(", ");
            try {
                messageBuilder.a(" \"file\":").a(quote(artifact.getFile().getCanonicalPath())).a(" ");
            } catch (IOException e) {
                messageBuilder.a(" \"file\":").a(quote(e.toString())).a(" ");
            }
        } else {
            messageBuilder.a(" ");
        }
        messageBuilder.a("}, ");

        return messageBuilder;
    }

    private String quote(String value) {
        return "\"" + value + "\"";
    }


}
