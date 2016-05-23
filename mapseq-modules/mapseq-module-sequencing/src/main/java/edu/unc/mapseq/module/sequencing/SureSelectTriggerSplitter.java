package edu.unc.mapseq.module.sequencing;

import java.io.File;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;

@Application(name = "SureSelectTriggerSplitter", executable = "$PERL_HOME/bin/perl $%s_SURESELECT_TRIGGER_SPLITTER_HOME/bin/SureSelectTargetSpliter_FBPloidyMaker.pl")
public class SureSelectTriggerSplitter extends Module {

    @NotNull(message = "intervalList is required", groups = InputValidations.class)
    @InputArgument(flag = "-i", description = "SureSelect panel interval file in NC_000001.10:762022-762345, for example, format")
    private File intervalList;

    @NotNull(message = "subjectName is required", groups = InputValidations.class)
    @InputArgument(flag = "-s", description = "Sample ID")
    private String subjectName;

    @NotNull(message = "gender is required", groups = InputValidations.class)
    @InputArgument(flag = "-g", description = "Sample gender (F|M)")
    private String gender;

    @NotNull(message = "numberOfSubsets is required", groups = InputValidations.class)
    @InputArgument(flag = "-n", description = "Number of subsets")
    private Integer numberOfSubsets;

    @NotNull(message = "par1Coordinate is required", groups = InputValidations.class)
    @InputArgument(flag = "-1", description = "ChrX PAR1 coordinate in 60001-2699520, for example hg19, format (1-base)")
    private String par1Coordinate;

    @NotNull(message = "par2Coordinate is required", groups = InputValidations.class)
    @InputArgument(flag = "-2", description = "ChrX PAR2 coordinate in 154931044-155260560, for example hg19, format (1-base)")
    private String par2Coordinate;

    @NotNull(message = "output is required", groups = InputValidations.class)
    @OutputArgument(flag = "-o", description = "Output file base name")
    private String outputPrefix;

    @Override
    public Class<?> getModuleClass() {
        return SureSelectTriggerSplitter.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(), getWorkflowName().toUpperCase());
    }

    public File getIntervalList() {
        return intervalList;
    }

    public void setIntervalList(File intervalList) {
        this.intervalList = intervalList;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getNumberOfSubsets() {
        return numberOfSubsets;
    }

    public void setNumberOfSubsets(Integer numberOfSubsets) {
        this.numberOfSubsets = numberOfSubsets;
    }

    public String getPar1Coordinate() {
        return par1Coordinate;
    }

    public void setPar1Coordinate(String par1Coordinate) {
        this.par1Coordinate = par1Coordinate;
    }

    public String getPar2Coordinate() {
        return par2Coordinate;
    }

    public void setPar2Coordinate(String par2Coordinate) {
        this.par2Coordinate = par2Coordinate;
    }

    public String getOutputPrefix() {
        return outputPrefix;
    }

    public void setOutputPrefix(String outputPrefix) {
        this.outputPrefix = outputPrefix;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    @Override
    public String toString() {
        return String.format(
                "SureSelectTriggerSplitter [intervalList=%s, subjectName=%s, gender=%s, numberOfSubsets=%s, par1Coordinate=%s, par2Coordinate=%s, outputPrefix=%s]",
                intervalList, subjectName, gender, numberOfSubsets, par1Coordinate, par2Coordinate, outputPrefix);
    }

    public static void main(String[] args) {
        SureSelectTriggerSplitter module = new SureSelectTriggerSplitter();
        module.setWorkflowName("TEST");
        module.setSubjectName("NCG_1234");
        module.setGender("M");
        module.setIntervalList(new File("/tmp", "input.interval_list"));
        module.setOutputPrefix("output");
        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
