package edu.unc.mapseq.module.ic;

import java.io.File;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "FlattenVCF", executable = "$%s_IC_HOME/bin/flatten_vcf.py")
public class FlattenVCF extends Module {

    @NotNull(message = "vcf is required", groups = InputValidations.class)
    @FileIsReadable(message = "vcf does not exist or is not readable", groups = InputValidations.class)
    @InputArgument(flag = "--vcf")
    private File vcf;

    @NotNull(message = "output is required", groups = InputValidations.class)
    @FileIsReadable(message = "output does not exist or is not readable", groups = OutputValidations.class)
    @OutputArgument(flag = "--out")
    private File output;

    @NotNull(message = "intervalList is required", groups = InputValidations.class)
    @FileIsReadable(message = "intervalList does not exist or is not readable", groups = InputValidations.class)
    @InputArgument(flag = "--int")
    private File intervalList;

    @NotNull(message = "sample is required", groups = InputValidations.class)
    @InputArgument(flag = "--sample")
    private String sample;

    @InputArgument(flag = "--header")
    private Boolean header;

    @Override
    public Class<?> getModuleClass() {
        return FlattenVCF.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(), getWorkflowName()
                .toUpperCase());
    }

    public File getVcf() {
        return vcf;
    }

    public void setVcf(File vcf) {
        this.vcf = vcf;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public File getIntervalList() {
        return intervalList;
    }

    public void setIntervalList(File intervalList) {
        this.intervalList = intervalList;
    }

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    public Boolean getHeader() {
        return header;
    }

    public void setHeader(Boolean header) {
        this.header = header;
    }

}
