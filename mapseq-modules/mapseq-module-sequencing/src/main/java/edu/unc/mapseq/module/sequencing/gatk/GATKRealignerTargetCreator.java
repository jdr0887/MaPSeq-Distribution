package edu.unc.mapseq.module.sequencing.gatk;

import java.io.File;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsNotEmpty;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "GATKRealignerTargetCreator", executable = "$JAVA7_HOME/bin/java -Xmx4g -Djava.io.tmpdir=$MAPSEQ_CLIENT_HOME/tmp -jar $%s_GATK_HOME/GenomeAnalysisTK.jar --analysis_type RealignerTargetCreator")
public class GATKRealignerTargetCreator extends Module {

    @NotNull(message = "inputFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "inputFile is not readable", groups = InputValidations.class)
    @InputArgument(flag = "--input_file")
    private File inputFile;

    @NotNull(message = "referenceSequence is required", groups = InputValidations.class)
    @FileIsReadable(message = "referenceSequence is not readable", groups = InputValidations.class)
    @InputArgument(flag = "--reference_sequence")
    private File referenceSequence;

    @InputArgument(flag = "--num_threads")
    private Integer numThreads;

    @NotNull(message = "out is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "invalid output file", groups = OutputValidations.class)
    @OutputArgument(flag = "--out", persistFileData = true, mimeType = MimeType.TEXT_INTERVALS)
    private File out;

    @NotNull(message = "known is required", groups = InputValidations.class)
    @FileIsReadable(message = "known is not readable", groups = InputValidations.class)
    @InputArgument(flag = "--known")
    private File known;

    @InputArgument(flag = "--phone_home")
    private String phoneHome;

    @InputArgument(flag = "--downsampling_type")
    private String downsamplingType;

    @InputArgument(flag = "--windowSize")
    private Integer windowSize;

    @InputArgument(flag = "--mismatchFraction")
    private Double mismatchFraction;

    @InputArgument(flag = "--minReadsAtLocus")
    private Integer minReadsAtLocus;

    @InputArgument(flag = "--maxIntervalSize")
    private Integer maxIntervalSize;

    @Override
    public Class<?> getModuleClass() {
        return GATKRealignerTargetCreator.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(),
                getWorkflowName().toUpperCase());
    }

    public String getPhoneHome() {
        return phoneHome;
    }

    public void setPhoneHome(String phoneHome) {
        this.phoneHome = phoneHome;
    }

    public String getDownsamplingType() {
        return downsamplingType;
    }

    public void setDownsamplingType(String downsamplingType) {
        this.downsamplingType = downsamplingType;
    }

    public File getInputFile() {
        return inputFile;
    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }

    public File getReferenceSequence() {
        return referenceSequence;
    }

    public void setReferenceSequence(File referenceSequence) {
        this.referenceSequence = referenceSequence;
    }

    public Integer getNumThreads() {
        return numThreads;
    }

    public void setNumThreads(Integer numThreads) {
        this.numThreads = numThreads;
    }

    public File getOut() {
        return out;
    }

    public void setOut(File out) {
        this.out = out;
    }

    public File getKnown() {
        return known;
    }

    public void setKnown(File known) {
        this.known = known;
    }

    public Integer getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(Integer windowSize) {
        this.windowSize = windowSize;
    }

    public Double getMismatchFraction() {
        return mismatchFraction;
    }

    public void setMismatchFraction(Double mismatchFraction) {
        this.mismatchFraction = mismatchFraction;
    }

    public Integer getMinReadsAtLocus() {
        return minReadsAtLocus;
    }

    public void setMinReadsAtLocus(Integer minReadsAtLocus) {
        this.minReadsAtLocus = minReadsAtLocus;
    }

    public Integer getMaxIntervalSize() {
        return maxIntervalSize;
    }

    public void setMaxIntervalSize(Integer maxIntervalSize) {
        this.maxIntervalSize = maxIntervalSize;
    }

    @Override
    public String toString() {
        return String.format(
                "GATKRealignerTargetCreator [inputFile=%s, referenceSequence=%s, numThreads=%s, out=%s, known=%s, phoneHome=%s, downsamplingType=%s, windowSize=%s, mismatchFraction=%s, minReadsAtLocus=%s, maxIntervalSize=%s, toString()=%s]",
                inputFile, referenceSequence, numThreads, out, known, phoneHome, downsamplingType, windowSize,
                mismatchFraction, minReadsAtLocus, maxIntervalSize, super.toString());
    }

}
