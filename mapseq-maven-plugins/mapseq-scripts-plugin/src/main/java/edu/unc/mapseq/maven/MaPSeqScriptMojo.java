package edu.unc.mapseq.maven;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.impl.ArtifactResolver;

@Mojo(name = "mapseq-script", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresProject = true, requiresDependencyResolution = ResolutionScope.TEST)
public class MaPSeqScriptMojo extends AbstractMojo {

    @Component
    private MavenProject project;

    @Component
    protected ArtifactResolver resolver;

    @Parameter(required = true)
    protected String outputDir;

    @Parameter(required = true)
    public String scriptName;

    @Parameter
    public String className = "";

    @SuppressWarnings("unchecked")
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Set<Artifact> artifacts = project.getArtifacts();

        File runScript = new File(outputDir, getScriptName());
        StringBuilder sb = new StringBuilder();
        sb.append("#!/bin/bash\n\n");
        sb.append("DIR=$(dirname $0)\n\n");
        sb.append(". ~/.mapseqrc\n");
        sb.append("CLASSPATH=.\n");
        sb.append("CLASSPATH=$CLASSPATH:$DIR/../lib/").append(project.getArtifactId()).append("-")
                .append(project.getVersion()).append(".jar\n");
        for (Artifact artifact : artifacts) {
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
