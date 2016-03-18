package edu.unc.mapseq.module.samtools;

import java.io.File;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsNotEmpty;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "SAMToolsFlagstat", executable = "$%s_SAMTOOLS_HOME/bin/samtools flagstat")
public class SAMToolsFlagstat extends Module {

    @NotNull(message = "Input is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "input is empty", groups = InputValidations.class)
    @FileIsReadable(message = "Invalid input file", groups = InputValidations.class)
    @InputArgument
    private File input;

    @NotNull(message = "Output is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "output is empty", groups = OutputValidations.class)
    @FileIsReadable(message = "Invalid output file", groups = OutputValidations.class)
    @OutputArgument(redirect = true, persistFileData = true, mimeType = MimeType.TEXT_STAT_SUMMARY)
    private File output;

    public SAMToolsFlagstat() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return SAMToolsFlagstat.class;
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

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    @Override
    public String toString() {
        return String.format("SAMToolsFlagstat [input=%s, output=%s, toString()=%s]", input, output, super.toString());
    }

}
