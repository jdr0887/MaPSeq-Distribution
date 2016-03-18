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

@Application(name = "CompareExpected", executable = "$%s_IC_HOME/bin/comp_expected.py")
public class CompareExpected extends Module {

    @NotNull(message = "output is required", groups = InputValidations.class)
    @FileIsReadable(message = "output does not exist or is not readable", groups = OutputValidations.class)
    @OutputArgument(flag = "--out")
    private File output;

    @NotNull(message = "maximumLikelihood is required", groups = InputValidations.class)
    @FileIsReadable(message = "maximumLikelihood does not exist or is not readable", groups = InputValidations.class)
    @InputArgument(flag = "--ml")
    private File maximumLikelihood;

    @NotNull(message = "expectedEC2HTSFMap is required", groups = InputValidations.class)
    @FileIsReadable(message = "expectedEC2HTSFMap does not exist or is not readable", groups = InputValidations.class)
    @InputArgument(flag = "--ecvcf")
    private File expectedEC2HTSFMap;

    @Override
    public Class<?> getModuleClass() {
        return CompareExpected.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(),
                getWorkflowName().toUpperCase());
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public File getMaximumLikelihood() {
        return maximumLikelihood;
    }

    public void setMaximumLikelihood(File maximumLikelihood) {
        this.maximumLikelihood = maximumLikelihood;
    }

    public File getExpectedEC2HTSFMap() {
        return expectedEC2HTSFMap;
    }

    public void setExpectedEC2HTSFMap(File expectedEC2HTSFMap) {
        this.expectedEC2HTSFMap = expectedEC2HTSFMap;
    }

    @Override
    public String toString() {
        return String.format("CompareExpected [output=%s, maximumLikelihood=%s, expectedEC2HTSFMap=%s, toString()=%s]",
                output, maximumLikelihood, expectedEC2HTSFMap, super.toString());
    }

    public static void main(String[] args) {
        CompareExpected module = new CompareExpected();
        module.setWorkflowName("TEST");
        module.setOutput(new File("/tmp", "blah.idchk.txt"));
        module.setMaximumLikelihood(new File("/tmp", "blah.ec.tsv"));
        module.setExpectedEC2HTSFMap(new File("/proj/renci/nida/ucsf/identity-check/resources/ucsf_ec_to_htsf.tsv"));
        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
