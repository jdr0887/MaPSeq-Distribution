package edu.unc.mapseq.maven;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * @goal mapseq-script
 * @phase generate-sources
 * @requiresProject true
 * @requiresDependencyResolution test
 * @description
 * 
 * @author jdr0887
 * 
 */
public class MaPSeqScriptMojo extends AbstractMojo {

    /**
     * List of Remote Repositories used by the resolver
     * 
     * @parameter property="project.remoteArtifactRepositories"
     * @readonly
     * @required
     */
    protected List<ArtifactRepository> remoteRepos;

    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * Location of the local repository.
     * 
     * @parameter property="localRepository"
     * @readonly
     * @required
     */
    protected ArtifactRepository local;

    /**
     * Used to look up Artifacts in the remote repository.
     * 
     * @component
     */
    protected ArtifactResolver resolver;

    /**
     * @parameter
     * @required
     */
    protected String outputDir;

    /**
     * @parameter
     * @required
     */
    public String scriptName;

    /**
     * @parameter
     */
    public String className = "";

    @SuppressWarnings("unchecked")
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Set<Artifact> artifacts = project.getArtifacts();
        Set<Artifact> resolvedArtifacts = new HashSet<Artifact>();
        for (Artifact artifact : artifacts) {
            try {
                this.resolver.resolve(artifact, this.remoteRepos, this.getLocal());
                resolvedArtifacts.add(artifact);
            } catch (ArtifactResolutionException e) {
                e.printStackTrace();
            } catch (ArtifactNotFoundException e) {
                e.printStackTrace();
            }
        }

        File runScript = new File(outputDir, getScriptName());
        StringBuilder sb = new StringBuilder();
        sb.append("#!/bin/bash\n\n");
        sb.append("DIR=$(dirname $0)\n\n");
        sb.append(". ~/.mapseqrc\n");
        sb.append("CLASSPATH=.\n");
        sb.append("CLASSPATH=$CLASSPATH:$DIR/../lib/").append(project.getArtifactId()).append("-")
                .append(project.getVersion()).append(".jar\n");
        for (Artifact artifact : resolvedArtifacts) {
            sb.append("CLASSPATH=$CLASSPATH:$DIR/../lib/").append(artifact.getArtifactId()).append("-")
                    .append(artifact.getVersion()).append(".jar\n");
        }
        sb.append("if [ ! -d \"$DIR/../tmp\" ]; then mkdir $DIR/../tmp; fi\n");
        sb.append("if [ ! -d \"$DIR/../logs\" ]; then mkdir $DIR/../logs; fi\n");
        sb.append("export CLASSPATH\n");
        sb.append("export JAVA_OPTS=\"-XX:MaxPermSize=512m -XX:-UseSplitVerifier -Xmx4g\"\n");
        sb.append(
                "CMD=\"$JAVA_HOME/bin/java $JAVA_OPTS -Djava.io.tmpdir=$DIR/../tmp -Dmapseq.log.dir=$DIR/../logs -cp $CLASSPATH ")
                .append(getClassName()).append(" $@\"\n");
        // sb.append("echo $CMD\n");
        sb.append("$CMD\n");
        try {
            FileUtils.writeStringToFile(runScript, sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public MavenProject getProject() {
        return project;
    }

    public void setProject(MavenProject project) {
        this.project = project;
    }

    public ArtifactResolver getResolver() {
        return resolver;
    }

    public void setResolver(ArtifactResolver resolver) {
        this.resolver = resolver;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public List<ArtifactRepository> getRemoteRepos() {
        return remoteRepos;
    }

    public void setRemoteRepos(List<ArtifactRepository> remoteRepos) {
        this.remoteRepos = remoteRepos;
    }

    public ArtifactRepository getLocal() {
        return local;
    }

    public void setLocal(ArtifactRepository local) {
        this.local = local;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

}
