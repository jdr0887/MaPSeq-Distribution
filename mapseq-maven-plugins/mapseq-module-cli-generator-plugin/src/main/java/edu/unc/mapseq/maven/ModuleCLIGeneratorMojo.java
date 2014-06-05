package edu.unc.mapseq.maven;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.DuplicateRealmException;

import edu.unc.mapseq.generator.ModuleCLIGenerator;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.Ignore;

/**
 * @goal generate
 * @phase generate-sources
 * @requiresProject true
 * @description
 * 
 * @author jdr0887
 * 
 */
public class ModuleCLIGeneratorMojo extends AbstractMojo {

    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        ClassWorld world = new ClassWorld();
        ClassRealm jpaRealm;

        try {
            jpaRealm = world.newRealm("maven.plugin.mapseq", Thread.currentThread().getContextClassLoader());
        } catch (DuplicateRealmException e) {
            throw new MojoExecutionException("Problem while creating new ClassRealm", e);
        }

        List<File> pathList = new ArrayList<File>();
        try {
            Iterator<?> itor = project.getCompileClasspathElements().iterator();
            while (itor.hasNext()) {
                File pathElem = new File((String) itor.next());
                pathList.add(pathElem);
                URL url = pathElem.toURI().toURL();
                jpaRealm.addConstituent(url);
            }

            Set<?> artifacts = project.getDependencyArtifacts();
            for (Iterator<?> artifactIterator = artifacts.iterator(); artifactIterator.hasNext();) {
                Artifact artifact = (Artifact) artifactIterator.next();
                if (!"test".equals(artifact.getScope())) {
                    File file = artifact.getFile();
                    URL url = file.toURI().toURL();
                    jpaRealm.addConstituent(url);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (DependencyResolutionRequiredException e) {
            e.printStackTrace();
        }
        Thread.currentThread().setContextClassLoader(jpaRealm.getClassLoader());

        // set the new ClassLoader as default for this Thread
        List<Class<?>> classList = new ArrayList<Class<?>>();

        for (File path : pathList) {

            Collection<File> files = org.apache.commons.io.FileUtils.listFiles(path,
                    FileFilterUtils.asFileFilter(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            if (name.endsWith(".class") && name.indexOf("$") == -1 && !name.contains("CLI")) {
                                return true;
                            }
                            return false;
                        }
                    }), TrueFileFilter.INSTANCE);
            for (File f : files) {
                int idx = f.getAbsolutePath().indexOf("target/classes/");
                String fullClassName = f.getAbsolutePath().replace(".class", "");
                fullClassName = fullClassName.substring(idx + 15, fullClassName.length());
                fullClassName = fullClassName.replace("/", ".");
                try {
                    classList.add(Class.forName(fullClassName, false, jpaRealm.getClassLoader()));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }

        List<Class<?>> filteredClassList = new ArrayList<Class<?>>();
        for (Class<?> c : classList) {
            if (c.isAnnotationPresent(Ignore.class) || !c.isAnnotationPresent(Application.class)) {
                continue;
            }

            filteredClassList.add(c);
        }

        String pkg = "edu.unc.mapseq.module";
        ModuleCLIGenerator generator = new ModuleCLIGenerator(filteredClassList, pkg, project.getBuild().getDirectory()
                + File.separator + "generated-sources" + File.separator + "modules");
        generator.run();

    }

    public MavenProject getProject() {
        return project;
    }

    public void setProject(MavenProject project) {
        this.project = project;
    }

}
