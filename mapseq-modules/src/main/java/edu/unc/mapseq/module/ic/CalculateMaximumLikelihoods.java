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

@Application(name = "CalculateMaximumLikelihoods", executable = "$%s_IC_HOME/bin/calc_ml.py")
public class CalculateMaximumLikelihoods extends Module {

    @NotNull(message = "flatVCF is required", groups = InputValidations.class)
    @FileIsReadable(message = "flatVCF does not exist or is not readable", groups = InputValidations.class)
    @InputArgument(flag = "--fvcf")
    private File flatVCF;

    @NotNull(message = "output is required", groups = InputValidations.class)
    @FileIsReadable(message = "output does not exist or is not readable", groups = OutputValidations.class)
    @OutputArgument(flag = "--out")
    private File output;

    @NotNull(message = "affy is required", groups = InputValidations.class)
    @FileIsReadable(message = "affy does not exist or is not readable", groups = InputValidations.class)
    @InputArgument(flag = "--affy")
    private File ecData;

    @Override
    public Class<?> getModuleClass() {
        return CalculateMaximumLikelihoods.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(),
                getWorkflowName().toUpperCase());
    }

    public File getFlatVCF() {
        return flatVCF;
    }

    public void setFlatVCF(File flatVCF) {
        this.flatVCF = flatVCF;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public File getEcData() {
        return ecData;
    }

    public void setEcData(File ecData) {
        this.ecData = ecData;
    }

    @Override
    public String toString() {
        return String.format("CalculateMaximumLikelihoods [flatVCF=%s, output=%s, ecData=%s, toString()=%s]", flatVCF,
                output, ecData, super.toString());
    }

}
