package edu.unc.mapseq.module.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.module.DefaultModuleOutput;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;

/**
 * 
 * @author jdr0887
 */
@Application(name = "RegexCat", isWorkflowRunIdOptional = true)
public class RegexCat extends Module {

    private final Logger logger = LoggerFactory.getLogger(RegexCat.class);

    @NotNull(message = "directory is required", groups = InputValidations.class)
    @InputArgument
    private File directory;

    @NotNull(message = "regex is required", groups = InputValidations.class)
    @InputArgument
    private String regex;

    @FileIsReadable(message = "File does not exist", groups = OutputValidations.class)
    @NotNull(message = "Output file name is required", groups = InputValidations.class)
    @OutputArgument(redirect = true)
    private File output;

    public RegexCat() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return RegexCat.class;
    }

    @Override
    public ModuleOutput call() throws Exception {
        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();
        moduleOutput.setExitCode(0);
        try {
            List<File> foundFiles = FileFilterUtils.filterList(new RegexFileFilter(regex), directory.listFiles());
            Collections.sort(foundFiles);
            BufferedWriter bw = new BufferedWriter(new FileWriter(output));
            for (File foundFile : foundFiles) {
                logger.info("foundFile: {}", foundFile.getAbsolutePath());
                IOUtils.copyLarge(new FileReader(foundFile), bw);
            }
            bw.close();
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

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public static void main(String[] args) {

        RegexCat module = new RegexCat();
        module.setDirectory(new File("/tmp"));
        module.setRegex("^remap_unmapped\\.[0-9]\\.1");
        // module.setRegex("^.*\\.txt");
        module.setOutput(new File("/tmp", "concatenated.txt"));

        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
