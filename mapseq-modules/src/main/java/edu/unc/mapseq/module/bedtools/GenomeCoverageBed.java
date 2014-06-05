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
 * @author jdr0887
 * 
 */
@Application(name = "GenomeCoverageBed")
@Executable(value = "$%s_BEDTOOLS_HOME/bin/genomeCoverageBed")
public class GenomeCoverageBed extends Module {

    @NotNull(message = "input is required", groups = InputValidations.class)
    @FileIsReadable(message = "input does not exist or is not readable", groups = InputValidations.class)
    @InputArgument(flag = "-ibam")
    private File input;

    @NotNull(message = "genome is required", groups = InputValidations.class)
    @FileIsReadable(message = "genome does not exist or is not readable", groups = InputValidations.class)
    @InputArgument(flag = "-g")
    private File genome;

    @NotNull(message = "outFile is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "invalid output file", groups = OutputValidations.class)
    @OutputArgument(redirect = true)
    private File outFile;

    @InputArgument(flag = "-bg")
    private Boolean reportDepthInBedGraphFormat;

    public GenomeCoverageBed() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return GenomeCoverageBed.class;
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

    public File getGenome() {
        return genome;
    }

    public void setGenome(File genome) {
        this.genome = genome;
    }

    public File getOutFile() {
        return outFile;
    }

    public void setOutFile(File outFile) {
        this.outFile = outFile;
    }

    public Boolean getReportDepthInBedGraphFormat() {
        return reportDepthInBedGraphFormat;
    }

    public void setReportDepthInBedGraphFormat(Boolean reportDepthInBedGraphFormat) {
        this.reportDepthInBedGraphFormat = reportDepthInBedGraphFormat;
    }

    public static void main(String[] args) {

        GenomeCoverageBed module = new GenomeCoverageBed();
        module.setInput(new File("inFile.txt"));
        module.setGenome(new File("choromosome.txt"));
        module.setOutFile(new File("output.txt"));
        module.setReportDepthInBedGraphFormat(Boolean.TRUE);
        module.setWorkflowName("TEST");
        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
