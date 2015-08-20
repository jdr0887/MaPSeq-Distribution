package edu.unc.mapseq.module.mapsplice;

import java.io.File;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "FilterJunctionByROCarguNonCanonical", executable = "$%s_MAPSPLICE_HOME/bin/filterjuncbyROCarguNonCanonical")
public class FilterJunctionByROCarguNonCanonical extends Module {

    @NotNull(message = "junctionFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "junctionFile does not exist or is not readable", groups = InputValidations.class)
    @InputArgument(order = 0)
    private File junctionFile;

    @NotNull(message = "entropyWeight is required", groups = InputValidations.class)
    @InputArgument(order = 1)
    private Double entropyWeight;

    @NotNull(message = "lpqWeight is required", groups = InputValidations.class)
    @InputArgument(order = 2)
    private Double lpqWeight;

    @NotNull(message = "aveMismatchWeight is required", groups = InputValidations.class)
    @InputArgument(order = 3)
    private Double aveMismatchWeight;

    @NotNull(message = "intronWeight is required", groups = InputValidations.class)
    @InputArgument(order = 4)
    private Double intronWeight;

    @NotNull(message = "sumLengthWeight is required", groups = InputValidations.class)
    @InputArgument(order = 5)
    private Double sumLengthWeight;

    @NotNull(message = "minScore is required", groups = InputValidations.class)
    @InputArgument(order = 6)
    private Double minScore;

    @NotNull(message = "minFlankCase is required", groups = InputValidations.class)
    @InputArgument(order = 7)
    private Integer minFlankCase;

    @NotNull(message = "canonicalJunctionOutput is required", groups = InputValidations.class)
    @FileIsReadable(message = "canonicalJunctionOutput does not exist or is not readable", groups = OutputValidations.class)
    @InputArgument(order = 8)
    private File canonicalJunctionOutput;

    @NotNull(message = "nonCanonicalJunctionOutput is required", groups = InputValidations.class)
    @FileIsReadable(message = "nonCanonicalJunctionOutput does not exist or is not readable", groups = OutputValidations.class)
    @InputArgument(order = 9)
    private File nonCanonicalJunctionOutput;

    @Override
    public Class<?> getModuleClass() {
        return FilterJunctionByROCarguNonCanonical.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(), getWorkflowName()
                .toUpperCase());
    }

    public File getJunctionFile() {
        return junctionFile;
    }

    public void setJunctionFile(File junctionFile) {
        this.junctionFile = junctionFile;
    }

    public Double getEntropyWeight() {
        return entropyWeight;
    }

    public void setEntropyWeight(Double entropyWeight) {
        this.entropyWeight = entropyWeight;
    }

    public Double getLpqWeight() {
        return lpqWeight;
    }

    public void setLpqWeight(Double lpqWeight) {
        this.lpqWeight = lpqWeight;
    }

    public Double getAveMismatchWeight() {
        return aveMismatchWeight;
    }

    public void setAveMismatchWeight(Double aveMismatchWeight) {
        this.aveMismatchWeight = aveMismatchWeight;
    }

    public Double getIntronWeight() {
        return intronWeight;
    }

    public void setIntronWeight(Double intronWeight) {
        this.intronWeight = intronWeight;
    }

    public Double getSumLengthWeight() {
        return sumLengthWeight;
    }

    public void setSumLengthWeight(Double sumLengthWeight) {
        this.sumLengthWeight = sumLengthWeight;
    }

    public Double getMinScore() {
        return minScore;
    }

    public void setMinScore(Double minScore) {
        this.minScore = minScore;
    }

    public Integer getMinFlankCase() {
        return minFlankCase;
    }

    public void setMinFlankCase(Integer minFlankCase) {
        this.minFlankCase = minFlankCase;
    }

    public File getCanonicalJunctionOutput() {
        return canonicalJunctionOutput;
    }

    public void setCanonicalJunctionOutput(File canonicalJunctionOutput) {
        this.canonicalJunctionOutput = canonicalJunctionOutput;
    }

    public File getNonCanonicalJunctionOutput() {
        return nonCanonicalJunctionOutput;
    }

    public void setNonCanonicalJunctionOutput(File nonCanonicalJunctionOutput) {
        this.nonCanonicalJunctionOutput = nonCanonicalJunctionOutput;
    }

}
