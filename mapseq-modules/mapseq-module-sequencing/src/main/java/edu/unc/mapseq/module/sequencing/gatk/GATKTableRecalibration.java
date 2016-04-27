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

@Application(name = "GATKTableRecalibration", executable = "$JAVA7_HOME/bin/java -Xmx4g -Djava.io.tmpdir=$MAPSEQ_CLIENT_HOME/tmp -jar $%s_GATK_HOME/GenomeAnalysisTK.jar --analysis_type TableRecalibration")
public class GATKTableRecalibration extends Module {

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

    @NotNull(message = "recalFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "recalFile is not readable", groups = InputValidations.class)
    @InputArgument(flag = "--recal_file")
    private File recalFile;

    @NotNull(message = "out is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "invalid output file", groups = OutputValidations.class)
    @OutputArgument(flag = "--out", persistFileData = true, mimeType = MimeType.APPLICATION_BAM)
    private File out;

    @InputArgument(flag = "--context_size")
    private Integer contextSize;

    @InputArgument(flag = "--homopolymer_nback")
    private Integer homopolymerNBack;

    @InputArgument(flag = "--doNotWriteOriginalQuals")
    private Boolean doNotWriteOriginalQuals = Boolean.FALSE;

    @InputArgument(flag = "--max_quality_score")
    private Integer maxQualityScore;

    @InputArgument(flag = "--preserve_qscores_less_than")
    private Integer preserveQScoreLessThan;

    @InputArgument(flag = "--smoothing")
    private Integer smoothing;

    @InputArgument(flag = "--phone_home")
    private String phoneHome;

    @InputArgument(flag = "--downsampling_type")
    private String downsamplingType;

    @Override
    public Class<?> getModuleClass() {
        return GATKTableRecalibration.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(),
                getWorkflowName().toUpperCase());
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

    public File getRecalFile() {
        return recalFile;
    }

    public void setRecalFile(File recalFile) {
        this.recalFile = recalFile;
    }

    public File getOut() {
        return out;
    }

    public void setOut(File out) {
        this.out = out;
    }

    public Integer getContextSize() {
        return contextSize;
    }

    public void setContextSize(Integer contextSize) {
        this.contextSize = contextSize;
    }

    public Integer getHomopolymerNBack() {
        return homopolymerNBack;
    }

    public void setHomopolymerNBack(Integer homopolymerNBack) {
        this.homopolymerNBack = homopolymerNBack;
    }

    public Boolean getDoNotWriteOriginalQuals() {
        return doNotWriteOriginalQuals;
    }

    public void setDoNotWriteOriginalQuals(Boolean doNotWriteOriginalQuals) {
        this.doNotWriteOriginalQuals = doNotWriteOriginalQuals;
    }

    public Integer getMaxQualityScore() {
        return maxQualityScore;
    }

    public void setMaxQualityScore(Integer maxQualityScore) {
        this.maxQualityScore = maxQualityScore;
    }

    public Integer getPreserveQScoreLessThan() {
        return preserveQScoreLessThan;
    }

    public void setPreserveQScoreLessThan(Integer preserveQScoreLessThan) {
        this.preserveQScoreLessThan = preserveQScoreLessThan;
    }

    public Integer getSmoothing() {
        return smoothing;
    }

    public void setSmoothing(Integer smoothing) {
        this.smoothing = smoothing;
    }

    @Override
    public String toString() {
        return String.format(
                "GATKTableRecalibration [inputFile=%s, referenceSequence=%s, numThreads=%s, recalFile=%s, out=%s, contextSize=%s, homopolymerNBack=%s, doNotWriteOriginalQuals=%s, maxQualityScore=%s, preserveQScoreLessThan=%s, smoothing=%s, phoneHome=%s, downsamplingType=%s, toString()=%s]",
                inputFile, referenceSequence, numThreads, recalFile, out, contextSize, homopolymerNBack,
                doNotWriteOriginalQuals, maxQualityScore, preserveQScoreLessThan, smoothing, phoneHome,
                downsamplingType, super.toString());
    }

}
