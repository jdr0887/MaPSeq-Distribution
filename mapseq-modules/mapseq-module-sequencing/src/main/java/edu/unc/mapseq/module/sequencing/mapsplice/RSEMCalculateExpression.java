package edu.unc.mapseq.module.sequencing.mapsplice;

import java.io.File;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;

@Application(name = "RSEMCalculateExpression", executable = "$%s_RSEM_HOME/rsem-calculate-expression", wallTime = 5L)
public class RSEMCalculateExpression extends Module {

    @InputArgument(flag = "--paired-end")
    private Boolean pairedEnd;

    @InputArgument(flag = "--bam")
    private Boolean bam;

    @InputArgument(flag = "--estimate-rspd")
    private Boolean estimateRSPD;

    @InputArgument(flag = "--p")
    private Integer threads;

    @NotNull(message = "fastqR1 is required", groups = InputValidations.class)
    @InputArgument(order = 1, delimiter = "")
    private File bamFile;

    @NotNull(message = "fastqR1 is required", groups = InputValidations.class)
    @InputArgument(order = 2, delimiter = "")
    private File referenceSequence;

    @NotNull(message = "output is required", groups = InputValidations.class)
    @OutputArgument(delimiter = "")
    private File output;

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(),
                getWorkflowName().toUpperCase());
    }

    @Override
    public Class<?> getModuleClass() {
        return RSEMCalculateExpression.class;
    }

    public Boolean getPairedEnd() {
        return pairedEnd;
    }

    public void setPairedEnd(Boolean pairedEnd) {
        this.pairedEnd = pairedEnd;
    }

    public Boolean getBam() {
        return bam;
    }

    public void setBam(Boolean bam) {
        this.bam = bam;
    }

    public Boolean getEstimateRSPD() {
        return estimateRSPD;
    }

    public void setEstimateRSPD(Boolean estimateRSPD) {
        this.estimateRSPD = estimateRSPD;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public File getBamFile() {
        return bamFile;
    }

    public void setBamFile(File bamFile) {
        this.bamFile = bamFile;
    }

    public File getReferenceSequence() {
        return referenceSequence;
    }

    public void setReferenceSequence(File referenceSequence) {
        this.referenceSequence = referenceSequence;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    @Override
    public String toString() {
        return String.format(
                "RSEMCalculateExpression [pairedEnd=%s, bam=%s, estimateRSPD=%s, threads=%s, bamFile=%s, referenceSequence=%s, output=%s, toString()=%s]",
                pairedEnd, bam, estimateRSPD, threads, bamFile, referenceSequence, output, super.toString());
    }

    public static void main(String[] args) {
        RSEMCalculateExpression module = new RSEMCalculateExpression();
        module.setWorkflowName("TEST");
        module.setBam(Boolean.TRUE);
        module.setBamFile(new File("/tmp/asdf.bam"));
        module.setDryRun(Boolean.TRUE);
        module.setOutput(new File("/tmp/output"));
        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
