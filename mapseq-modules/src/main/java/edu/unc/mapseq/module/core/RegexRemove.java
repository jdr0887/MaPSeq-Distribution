package edu.unc.mapseq.module.core;

import java.io.File;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
@Application(name = "RegexRemove", isWorkflowRunIdOptional = true)
public class RegexRemove extends Module {

    private final Logger logger = LoggerFactory.getLogger(RegexCat.class);

    @NotNull(message = "directory is required", groups = InputValidations.class)
    @InputArgument
    private File directory;

    @NotNull(message = "regex is required", groups = InputValidations.class)
    @InputArgument
    private String regex;

    public RegexRemove() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return RegexRemove.class;
    }

    @Override
    public ModuleOutput call() throws ModuleException {
        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();
        moduleOutput.setExitCode(0);
        try {
            List<File> foundFiles = FileFilterUtils.filterList(new RegexFileFilter(regex), directory.listFiles());
            for (File f : foundFiles) {
                logger.info("removing file: {}", f.getAbsolutePath());
                FileUtils.forceDelete(f);
            }
        } catch (Exception e) {
            e.printStackTrace();
            moduleOutput.setError(new StringBuilder(e.getMessage()));
            moduleOutput.setExitCode(-1);
        }
        return moduleOutput;
    }

    public File getDirectory() {
        return directory;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public static void main(String[] args) {
        RegexRemove module = new RegexRemove();
        module.setDirectory(new File("/tmp"));
        //module.setRegex("^.*\\.txt");
        //module.setRegex("^.*\\.sam\\.[0-9]");
        module.setRegex("^.*\\.sam\\.");
        try {
            module.call();
        } catch (ModuleException e) {
            e.printStackTrace();
        }

    }

}
