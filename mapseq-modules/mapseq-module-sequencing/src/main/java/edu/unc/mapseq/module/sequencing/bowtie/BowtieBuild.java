package edu.unc.mapseq.module.sequencing.bowtie;

import java.io.File;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "BowtieBuild", executable = "$%s_BOWTIE_HOME/bin/bowtie-build")
public class BowtieBuild extends Module {

    @NotNull(message = "input is required", groups = InputValidations.class)
    @FileIsReadable(message = "input does not exist or is not readable", groups = InputValidations.class)
    @InputArgument(order = 0, delimiter = "")
    private File input;

    @NotNull(message = "prefix is required", groups = InputValidations.class)
    @InputArgument(order = 1, delimiter = "")
    private String prefix;

    @Override
    public Class<?> getModuleClass() {
        return BowtieBuild.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(),
                getWorkflowName().toUpperCase());
    }

    public File getInput() {
        return input;
    }

    public void setInput(File input) {
        this.input = input;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String toString() {
        return String.format("BowtieBuild [input=%s, prefix=%s, toString()=%s]", input, prefix, super.toString());
    }

}
