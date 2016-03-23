package edu.unc.mapseq.module.sequencing.mapsplice;

import java.io.File;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "Filter1Hits", executable = "$%s_MAPSPLICE_HOME/bin/filter_1hits")
public class Filter1Hits extends Module {

    @NotNull(message = "junctionFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "junctionFile does not exist or is not readable", groups = InputValidations.class)
    @InputArgument(order = 0)
    private File junctionFile;

    @NotNull(message = "minimumMismatch is required", groups = InputValidations.class)
    @InputArgument(order = 1)
    private Integer minimumMismatch;

    @NotNull(message = "minimumLPQ is required", groups = InputValidations.class)
    @InputArgument(order = 2)
    private Double minimumLPQ;

    @NotNull(message = "remained is required", groups = InputValidations.class)
    @FileIsReadable(message = "canonicalJunctionOutput does not exist or is not readable", groups = OutputValidations.class)
    @InputArgument(order = 8)
    private File canonicalJunctionOutput;

    @NotNull(message = "nonCanonicalJunctionOutput is required", groups = InputValidations.class)
    @FileIsReadable(message = "nonCanonicalJunctionOutput does not exist or is not readable", groups = OutputValidations.class)
    @InputArgument(order = 9)
    private File nonCanonicalJunctionOutput;

    @Override
    public Class<?> getModuleClass() {
        return Filter1Hits.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(),
                getWorkflowName().toUpperCase());
    }

    public File getJunctionFile() {
        return junctionFile;
    }

    public void setJunctionFile(File junctionFile) {
        this.junctionFile = junctionFile;
    }

    public Integer getMinimumMismatch() {
        return minimumMismatch;
    }

    public void setMinimumMismatch(Integer minimumMismatch) {
        this.minimumMismatch = minimumMismatch;
    }

    public Double getMinimumLPQ() {
        return minimumLPQ;
    }

    public void setMinimumLPQ(Double minimumLPQ) {
        this.minimumLPQ = minimumLPQ;
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

    @Override
    public String toString() {
        return String.format(
                "Filter1Hits [junctionFile=%s, minimumMismatch=%s, minimumLPQ=%s, canonicalJunctionOutput=%s, nonCanonicalJunctionOutput=%s, toString()=%s]",
                junctionFile, minimumMismatch, minimumLPQ, canonicalJunctionOutput, nonCanonicalJunctionOutput,
                super.toString());
    }

}
