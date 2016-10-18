package edu.unc.mapseq.module.core;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;
import edu.unc.mapseq.module.constraints.FileListIsReadable;

/**
 * 
 * @author jdr0887
 * 
 */
@Application(name = "Zip", executable = "/usr/bin/zip", isWorkflowRunIdOptional = true)
public class Zip extends Module {

    @NotNull(message = "Zip is required", groups = InputValidations.class)
    @FileIsReadable(message = "Zip file does is not readable", groups = OutputValidations.class)
    @InputArgument(flag = "-j")
    private File output;

    @NotNull(message = "Entry is required", groups = InputValidations.class)
    @FileListIsReadable(message = "One or more entries is not readable", groups = InputValidations.class)
    @InputArgument(order = 99, description = "Files to zip", delimiter = "")
    private List<File> entry;

    public Zip() {
        super();
    }

    @Override
    public String getExecutable() {
        return getModuleClass().getAnnotation(Application.class).executable();
    }

    @Override
    public Class<?> getModuleClass() {
        return Zip.class;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public List<File> getEntry() {
        return entry;
    }

    public void setEntry(List<File> entry) {
        this.entry = entry;
    }

    @Override
    public String toString() {
        return String.format("Zip [output=%s, entry=%s, toString()=%s]", output, entry, super.toString());
    }

    public static void main(String[] args) {
        try {
            Zip module = new Zip();
            module.setWorkflowName("TEST");
            module.setEntry(Arrays.asList(new File("/tmp", "asdf.txt"), new File("/tmp", "zxcv.txt")));
            module.setOutput(new File("/tmp", "asdf.zip"));
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
