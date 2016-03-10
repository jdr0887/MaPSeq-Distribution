package edu.unc.mapseq.maven;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import edu.unc.mapseq.generator.ModuleCLIGenerator;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.Ignore;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresProject = true, requiresDependencyResolution = ResolutionScope.COMPILE)
public class ModuleCLIGeneratorMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        try {

            List<URL> urls = new ArrayList<URL>();
            for (String fileName : project.getCompileClasspathElements()) {
                File pathElem = new File(fileName);
                urls.add(pathElem.toURI().toURL());
            }

            ClassLoader cl = new URLClassLoader(urls.toArray(new URL[urls.size()]), getClass().getClassLoader());

            Thread.currentThread().setContextClassLoader(cl);

            List<Class<?>> classList = new ArrayList<Class<?>>();

            String targetClassesPath = null;
            for (URL url : urls) {
                if (url.getFile().contains("target/classes")) {
                    targetClassesPath = url.getFile();
                    break;
                }
            }

            if (StringUtils.isNotEmpty(targetClassesPath)) {
                Collection<File> files = FileUtils.listFiles(new File(targetClassesPath),
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
                    try {
                        int idx = f.getAbsolutePath().indexOf("target/classes/");
                        String fullClassName = f.getAbsolutePath().replace(".class", "");
                        fullClassName = fullClassName.substring(idx + 15, fullClassName.length());
                        fullClassName = fullClassName.replace("/", ".");
                        classList.add(Class.forName(fullClassName, false, cl));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }

            List<Class<?>> filteredClassList = new ArrayList<Class<?>>();
            classList.forEach(a -> {
                if (!a.isAnnotationPresent(Ignore.class) && a.isAnnotationPresent(Application.class)) {
                    filteredClassList.add(a);
                }
            });

            // filteredClassList.forEach(a -> getLog().info(a.getName()));

            String pkg = "edu.unc.mapseq.module";
            ModuleCLIGenerator generator = new ModuleCLIGenerator(filteredClassList, pkg,
                    String.format("%2$s%1$s%3$s%1$s%4$s", File.separator, project.getBuild().getDirectory(),
                            "generated-sources", "modules"));
            generator.run();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (DependencyResolutionRequiredException e) {
            e.printStackTrace();
        }

    }

    public MavenProject getProject() {
        return project;
    }

    public void setProject(MavenProject project) {
        this.project = project;
    }

}
