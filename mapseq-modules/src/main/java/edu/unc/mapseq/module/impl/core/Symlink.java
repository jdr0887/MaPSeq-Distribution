package edu.unc.mapseq.module.impl.core;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang.StringUtils;

import edu.unc.mapseq.module.DefaultModuleOutput;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleException;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;

/**
 * 
 * @author jdr0887
 */
@Application(name = "Symlink")
public class Symlink extends Module {

    @NotNull(message = "link is required", groups = InputValidations.class)
    @InputArgument
    private File link;

    @NotNull(message = "target is required", groups = InputValidations.class)
    @InputArgument
    private File target;

    @InputArgument
    private Integer sleepDuration;

    @InputArgument
    private String prefix;

    public Symlink() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return Symlink.class;
    }

    @Override
    public ModuleOutput call() throws ModuleException {
        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();
        int exitCode = 0;
        try {

            if (target.isFile() && StringUtils.isEmpty(prefix)) {
                if (!link.exists()) {
                    Files.createSymbolicLink(link.toPath(), target.toPath());
                }
            }

            if (target.isDirectory() && StringUtils.isNotEmpty(prefix)) {
                List<File> foundFiles = FileFilterUtils.filterList(FileFilterUtils.prefixFileFilter(prefix),
                        target.listFiles());
                if (foundFiles != null) {
                    for (File foundFile : foundFiles) {
                        File tmpLink = new File(link, foundFile.getName());
                        File tmpTarget = new File(target, foundFile.getName());
                        if (!tmpLink.exists() && !tmpTarget.isDirectory()) {
                            Files.createSymbolicLink(tmpLink.toPath(), tmpTarget.toPath());
                        }
                    }
                }
            }

            if (sleepDuration != null && sleepDuration > 0) {
                // b/c condor is not as HTC as we once thought
                Thread.sleep(sleepDuration * 1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
            moduleOutput.setError(new StringBuilder(e.getMessage()));
            moduleOutput.setExitCode(exitCode);
        }
        moduleOutput.setExitCode(exitCode);
        return moduleOutput;
    }

    public File getLink() {
        return link;
    }

    public void setLink(File link) {
        this.link = link;
    }

    public File getTarget() {
        return target;
    }

    public void setTarget(File target) {
        this.target = target;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Integer getSleepDuration() {
        return sleepDuration;
    }

    public void setSleepDuration(Integer sleepDuration) {
        this.sleepDuration = sleepDuration;
    }

    @Override
    public String toString() {
        return String.format("Symlink [link=%s, target=%s, sleepDuration=%s, prefix=%s, toString()=%s]", link, target,
                sleepDuration, prefix, super.toString());
    }

}
