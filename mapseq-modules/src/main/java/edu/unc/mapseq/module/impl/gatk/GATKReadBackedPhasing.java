package edu.unc.mapseq.module.impl.gatk;

import java.io.File;
import java.util.List;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;

@Application(name = "GATKReadBackedPhasing", executable = "$JAVA_HOME/bin/java -Xmx4g -Djava.io.tmpdir=$MAPSEQ_CLIENT_HOME/tmp -jar $%s_GATK_HOME/GenomeAnalysisTK.jar --analysis_type ReadBackedPhasing")
public class GATKReadBackedPhasing extends Module {

    @NotNull(message = "out is required", groups = InputValidations.class)
    @OutputArgument(flag = "--out", persistFileData = true, mimeType = MimeType.TEXT_VCF)
    private File out;

    @NotNull(message = "variant is required", groups = InputValidations.class)
    @InputArgument(flag = "--variant")
    private File variant;

    @NotNull(message = "referenceSequence is required", groups = InputValidations.class)
    @InputArgument(flag = "--reference_sequence")
    private File referenceSequence;

    @NotNull(message = "input is required", groups = InputValidations.class)
    @InputArgument(flag = "--input_file")
    private File input;

    @NotNull(message = "input is required", groups = InputValidations.class)
    @InputArgument(flag = "--intervals")
    private File intervals;

    @InputArgument(flag = "--cacheWindowSize")
    private Integer cacheWindowSize;

    @InputArgument(flag = "-enableMergeToMNP")
    private Boolean enableMergeToMNP;

    @InputArgument(flag = "--maxGenomicDistanceForMNP")
    private Integer maxGenomicDistanceForMNP;

    @InputArgument(flag = "--maxPhaseSites")
    private Integer maxPhaseSites;

    @InputArgument(flag = "--min_base_quality_score")
    private Integer minBaseQualityScore;

    @InputArgument(flag = "--min_mapping_quality_score")
    private Integer minMappingQualityScore;

    @InputArgument(flag = "--phaseQualityThresh")
    private Double phaseQualityThresh;

    @InputArgument(flag = "--respectPhaseInInput")
    private Boolean respectPhaseInInput;

    @InputArgument(flag = "--sampleToPhase")
    private List<String> sampleToPhase;

    @InputArgument(flag = "--phone_home")
    private String phoneHome;

    @InputArgument(flag = "--downsampling_type")
    private String downsamplingType;

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(),
                getWorkflowName().toUpperCase());
    }

    @Override
    public Class<?> getModuleClass() {
        return GATKReadBackedPhasing.class;
    }

    public File getOut() {
        return out;
    }

    public void setOut(File out) {
        this.out = out;
    }

    public File getVariant() {
        return variant;
    }

    public void setVariant(File variant) {
        this.variant = variant;
    }

    public File getReferenceSequence() {
        return referenceSequence;
    }

    public void setReferenceSequence(File referenceSequence) {
        this.referenceSequence = referenceSequence;
    }

    public File getInput() {
        return input;
    }

    public void setInput(File input) {
        this.input = input;
    }

    public File getIntervals() {
        return intervals;
    }

    public void setIntervals(File intervals) {
        this.intervals = intervals;
    }

    public Integer getCacheWindowSize() {
        return cacheWindowSize;
    }

    public void setCacheWindowSize(Integer cacheWindowSize) {
        this.cacheWindowSize = cacheWindowSize;
    }

    public Boolean getEnableMergeToMNP() {
        return enableMergeToMNP;
    }

    public void setEnableMergeToMNP(Boolean enableMergeToMNP) {
        this.enableMergeToMNP = enableMergeToMNP;
    }

    public Integer getMaxGenomicDistanceForMNP() {
        return maxGenomicDistanceForMNP;
    }

    public void setMaxGenomicDistanceForMNP(Integer maxGenomicDistanceForMNP) {
        this.maxGenomicDistanceForMNP = maxGenomicDistanceForMNP;
    }

    public Integer getMaxPhaseSites() {
        return maxPhaseSites;
    }

    public void setMaxPhaseSites(Integer maxPhaseSites) {
        this.maxPhaseSites = maxPhaseSites;
    }

    public Integer getMinBaseQualityScore() {
        return minBaseQualityScore;
    }

    public void setMinBaseQualityScore(Integer minBaseQualityScore) {
        this.minBaseQualityScore = minBaseQualityScore;
    }

    public Integer getMinMappingQualityScore() {
        return minMappingQualityScore;
    }

    public void setMinMappingQualityScore(Integer minMappingQualityScore) {
        this.minMappingQualityScore = minMappingQualityScore;
    }

    public Double getPhaseQualityThresh() {
        return phaseQualityThresh;
    }

    public void setPhaseQualityThresh(Double phaseQualityThresh) {
        this.phaseQualityThresh = phaseQualityThresh;
    }

    public Boolean getRespectPhaseInInput() {
        return respectPhaseInInput;
    }

    public void setRespectPhaseInInput(Boolean respectPhaseInInput) {
        this.respectPhaseInInput = respectPhaseInInput;
    }

    public List<String> getSampleToPhase() {
        return sampleToPhase;
    }

    public void setSampleToPhase(List<String> sampleToPhase) {
        this.sampleToPhase = sampleToPhase;
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

    @Override
    public String toString() {
        return String.format(
                "GATKReadBackedPhasing [out=%s, variant=%s, referenceSequence=%s, input=%s, intervals=%s, cacheWindowSize=%s, enableMergeToMNP=%s, maxGenomicDistanceForMNP=%s, maxPhaseSites=%s, minBaseQualityScore=%s, minMappingQualityScore=%s, phaseQualityThresh=%s, respectPhaseInInput=%s, sampleToPhase=%s, phoneHome=%s, downsamplingType=%s, toString()=%s]",
                out, variant, referenceSequence, input, intervals, cacheWindowSize, enableMergeToMNP,
                maxGenomicDistanceForMNP, maxPhaseSites, minBaseQualityScore, minMappingQualityScore,
                phaseQualityThresh, respectPhaseInInput, sampleToPhase, phoneHome, downsamplingType, super.toString());
    }

}
