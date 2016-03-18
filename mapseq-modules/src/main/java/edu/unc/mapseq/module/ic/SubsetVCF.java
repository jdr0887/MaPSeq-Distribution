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

@Application(name = "SubsetVCF", executable = "$%s_IC_HOME/bin/subset_vcf.py")
public class SubsetVCF extends Module {

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

    @Override
    public Class<?> getModuleClass() {
        return SubsetVCF.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(),
                getWorkflowName().toUpperCase());
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

    @Override
    public String toString() {
        return String.format("SubsetVCF [vcf=%s, output=%s, intervalList=%s, toString()=%s]", vcf, output, intervalList,
                super.toString());
    }

}
