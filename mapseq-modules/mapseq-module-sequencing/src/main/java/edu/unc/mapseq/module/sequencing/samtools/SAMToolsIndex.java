package edu.unc.mapseq.module.sequencing.samtools;

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

@Application(name = "SAMToolsIndex", executable = "$%s_SAMTOOLS_HOME/bin/samtools index")
public class SAMToolsIndex extends Module {

    @NotNull(message = "Input is required", groups = InputValidations.class)
    @FileIsReadable(message = "Invalid input file", groups = InputValidations.class)
    @FileIsNotEmpty(message = "input is empty", groups = InputValidations.class)
    @InputArgument(delimiter = "")
    private File input;

    @NotNull(message = "Output is required", groups = InputValidations.class)
    @FileIsReadable(message = "Invalid output file", groups = OutputValidations.class)
    @OutputArgument(delimiter = "", persistFileData = true, mimeType = MimeType.APPLICATION_BAM_INDEX)
    private File output;

    public SAMToolsIndex() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return SAMToolsIndex.class;
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
        return String.format("SAMToolsIndex [input=%s, output=%s, toString()=%s]", input, output, super.toString());
    }

}
