package edu.unc.mapseq.module.bedtools;

import java.io.File;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.annotations.Application;
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
@Application(name = "CoverageBed", executable = "$%s_BEDTOOLS_HOME/bin/bedtools coverage")
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
        return String.format(getModuleClass().getAnnotation(Application.class).executable(),
                getWorkflowName().toUpperCase());
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

    @Override
    public String toString() {
        return String.format("CoverageBed [input=%s, bed=%s, output=%s, splitBed=%s, toString()=%s]", input, bed,
                output, splitBed, super.toString());
    }

    public static void main(String[] args) {

        CoverageBed module = new CoverageBed();
        module.setBed(
                new File("/proj/seq/LBG/tier1data/nextgenseq/seqware-analysis/mapsplice_rsem", "composite_exons.bed"));

        // module.setInput(new
        // File("/proj/seq/mapseq/LBG/CMPSeq/150514_UNC11-SN627_0400_AC5J46ACXX/L005_GTCCGC/RNASeq/",
        // "150514_UNC11-SN627_0400_AC5J46ACXX_GTCCGC_L005.fixed-rg.sorted.bam"));
        // module.setOutput(new
        // File("/proj/seq/mapseq/LBG/CMPSeq/150514_UNC11-SN627_0400_AC5J46ACXX/L005_GTCCGC/RNASeq/",
        // "150514_UNC11-SN627_0400_AC5J46ACXX_GTCCGC_L005.fixed-rg.sorted.coverageBedOut.test.txt"));

        module.setInput(new File("/proj/seq/mapseq/LBG/CMPSeq/150501_UNC15-SN850_0405_BC5HCYACXX/L002_ACTGAT/RNASeq",
                "150501_UNC15-SN850_0405_BC5HCYACXX_ACTGAT_L002.fixed-rg.sorted.bam"));
        module.setOutput(new File("/proj/seq/mapseq/LBG/CMPSeq/150501_UNC15-SN850_0405_BC5HCYACXX/L002_ACTGAT/RNASeq",
                "150501_UNC15-SN850_0405_BC5HCYACXX_ACTGAT_L002.fixed-rg.sorted.coverageBedOut.test.txt"));

        module.setSplitBed(Boolean.TRUE);
        module.setWorkflowName("TEST");
        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
