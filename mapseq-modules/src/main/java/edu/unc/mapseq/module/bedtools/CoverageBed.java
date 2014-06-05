package edu.unc.mapseq.module.bedtools;

import java.io.File;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.Executable;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsNotEmpty;
import edu.unc.mapseq.module.constraints.FileIsReadable;

/**
 * 
 * @author roachjm
 * 
 */
@Application(name = "CoverageBed")
@Executable(value = "$%s_BEDTOOLS_HOME/bin/bedtools coverage")
public class CoverageBed extends Module {

    @NotNull(message = "input is required", groups = InputValidations.class)
    @FileIsReadable(message = "input does not exist or is not readable", groups = InputValidations.class)
    @InputArgument(flag = "-abam")
    private File input;

    @NotNull(message = "bed file is required", groups = InputValidations.class)
    @FileIsReadable(message = "bed file does not exist or is not readable", groups = InputValidations.class)
    @InputArgument(flag = "-b")
    private File bed;

    @NotNull(message = "output is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "invalid output file", groups = OutputValidations.class)
    @OutputArgument(redirect = true)
    private File output;

    @InputArgument(flag = "-split")
    private Boolean splitBed;

    public CoverageBed() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return CoverageBed.class;
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

    public File getBed() {
        return bed;
    }

    public void setBed(File bed) {
        this.bed = bed;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public Boolean getSplitBed() {
        return splitBed;
    }

    public void setSplitBed(Boolean splitBed) {
        this.splitBed = splitBed;
    }

    public static void main(String[] args) {

        CoverageBed module = new CoverageBed();
        module.setInput(new File("inFile.txt"));
        module.setBed(new File("bed.txt"));
        module.setOutput(new File("output.txt"));
        module.setSplitBed(Boolean.TRUE);
        module.setWorkflowName("TEST");
        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
