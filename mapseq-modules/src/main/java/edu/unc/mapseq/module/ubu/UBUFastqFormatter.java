package edu.unc.mapseq.module.ubu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import edu.unc.bioinf.ubu.fastq.FastqFormatter;
import edu.unc.mapseq.module.DefaultModuleOutput;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "UBUSamFilter")
public class UBUFastqFormatter extends Module {

    @NotNull(message = "input is required", groups = InputValidations.class)
    @FileIsReadable(message = "input does not exist or is not readable", groups = InputValidations.class)
    @InputArgument
    private File input;

    @NotNull(message = "output is required", groups = InputValidations.class)
    @FileIsReadable(message = "output does not exist or is not readable", groups = OutputValidations.class)
    @InputArgument
    private File output;

    @NotNull(message = "suffix is required", groups = InputValidations.class)
    @InputArgument
    private String suffix;

    @InputArgument
    private Boolean phred33to64;

    @InputArgument
    private Boolean strip;

    @Override
    public Class<?> getModuleClass() {
        return UBUFastqFormatter.class;
    }

    @Override
    public ModuleOutput call() throws Exception {

        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();
        moduleOutput.setExitCode(0);
        List<String> argumentList = new ArrayList<String>();
        argumentList.add("--in");
        argumentList.add(this.input.getAbsolutePath());
        argumentList.add("--out");
        argumentList.add(this.output.getAbsolutePath());
        argumentList.add("--suffix");
        argumentList.add(suffix);

        if (phred33to64 != null && phred33to64) {
            argumentList.add("--phred33to64");
        }

        if (strip != null && strip) {
            argumentList.add("--strip");
        }

        try {
            FastqFormatter.run(argumentList.toArray(new String[argumentList.size()]));
        } catch (Exception e) {
            moduleOutput.setExitCode(-1);
            moduleOutput.setError(new StringBuilder(e.getMessage()));
            return moduleOutput;
        }

        return moduleOutput;
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

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public Boolean getPhred33to64() {
        return phred33to64;
    }

    public void setPhred33to64(Boolean phred33to64) {
        this.phred33to64 = phred33to64;
    }

    public Boolean getStrip() {
        return strip;
    }

    public void setStrip(Boolean strip) {
        this.strip = strip;
    }

    @Override
    public String toString() {
        return String.format(
                "UBUFastqFormatter [input=%s, output=%s, suffix=%s, phred33to64=%s, strip=%s, toString()=%s]", input,
                output, suffix, phred33to64, strip, super.toString());
    }

    public static void main(String[] args) {
        UBUFastqFormatter module = new UBUFastqFormatter();

        // module.setInput(new File("/home/jdr0887/data/mapsplice",
        // "130913_UNC12-SN629_0330_AD2E14ACXX_AGTTCC_L006_R1.fastq"));
        // module.setOutput(new File("/home/jdr0887/data/mapsplice",
        // "130913_UNC12-SN629_0330_AD2E14ACXX_AGTTCC_L006_R1.filtered.fastq"));
        // module.setSuffix("/1");

        module.setInput(
                new File("/home/jdr0887/data/mapsplice", "130913_UNC12-SN629_0330_AD2E14ACXX_AGTTCC_L006_R2.fastq"));
        module.setOutput(new File("/home/jdr0887/data/mapsplice",
                "130913_UNC12-SN629_0330_AD2E14ACXX_AGTTCC_L006_R2.filtered.fastq"));
        module.setSuffix("/2");

        module.setStrip(Boolean.TRUE);
        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
