package edu.unc.mapseq.module.samtools;

import java.io.File;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.Executable;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "SortByReferenceAndName")
@Executable(value = "perl $MAPSEQ_HOME/bin/sort_bam_by_reference_and_name.pl --samtools $%s_SAMTOOLS_HOME/bin/samtools --temp-dir $MAPSEQ_HOME/tmp")
public class SortByReferenceAndName extends Module {

    @NotNull(message = "flagstatInput is required", groups = InputValidations.class)
    @FileIsReadable(message = "Invalid flagstatInput file", groups = InputValidations.class)
    @InputArgument(flag = "--input")
    private File input;

    @NotNull(message = "output is required", groups = InputValidations.class)
    @FileIsReadable(message = "Invalid output file", groups = OutputValidations.class)
    @OutputArgument(flag = "--output")
    private File output;

    public SortByReferenceAndName() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return SortByReferenceAndName.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Executable.class).value(), getWorkflowName().toUpperCase());
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

}
