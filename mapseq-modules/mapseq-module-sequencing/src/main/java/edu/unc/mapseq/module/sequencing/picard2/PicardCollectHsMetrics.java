package edu.unc.mapseq.module.sequencing.picard2;

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

@Application(name = "CollectHsMetrics", executable = "$JAVA8_HOME/bin/java -Xmx4g -jar $%s_PICARD2_HOME/picard.jar CollectHsMetrics TMP_DIR=$MAPSEQ_CLIENT_HOME/tmp")
public class PicardCollectHsMetrics extends Module {

    @NotNull(message = "Input is required", groups = InputValidations.class)
    @FileIsReadable(message = "input file is not readable", groups = InputValidations.class)
    @FileIsNotEmpty(message = "input file is empty", groups = InputValidations.class)
    @InputArgument(flag = "INPUT", delimiter = "=")
    private File input;

    @NotNull(message = "Output is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "output file is empty", groups = OutputValidations.class)
    @OutputArgument(flag = "OUTPUT", delimiter = "=", persistFileData = true, mimeType = MimeType.APPLICATION_BAM)
    private File output;

    @NotNull(message = "Bait Intervals file is required", groups = InputValidations.class)
    @FileIsReadable(message = "baitIntervals file is not readable", groups = InputValidations.class)
    @FileIsNotEmpty(message = "baitIntervals file is empty", groups = InputValidations.class)
    @InputArgument(flag = "BAIT_INTERVALS", delimiter = "=")
    private File baitIntervals;

    @NotNull(message = "Ttarget Intervals file is required", groups = InputValidations.class)
    @FileIsReadable(message = "targetIntervals file is not readable", groups = InputValidations.class)
    @FileIsNotEmpty(message = "targetIntervals file is empty", groups = InputValidations.class)
    @InputArgument(flag = "TARGET_INTERVALS", delimiter = "=")
    private File targetIntervals;

    @InputArgument(flag = "BAIT_SET_NAME", delimiter = "=")
    private String baitSetName;

    @InputArgument(flag = "MINIMUM_MAPPING_QUALITY", delimiter = "=")
    private Integer minimumMappingQuality;

    @InputArgument(flag = "MINIMUM_BASE_QUALITY", delimiter = "=")
    private Integer minimumBaseQuality;

    @InputArgument(flag = "CLIP_OVERLAPPING_READS", delimiter = "=")
    private Boolean clipOverlappingReads = Boolean.TRUE;

    @InputArgument(flag = "METRIC_ACCUMULATION_LEVEL", delimiter = "=")
    private String metricAccumulationLevel = PicardCollectHsMetricsAccumulationLevelType.ALL_READS.toString();

    @InputArgument(flag = "PER_TARGET_COVERAGE", delimiter = "=")
    private File perTargetCoverage;

    @InputArgument(flag = "PER_BASE_COVERAGE", delimiter = "=")
    private File perBaseCoverage;

    @InputArgument(flag = "NEAR_DISTANCE", delimiter = "=")
    private Integer nearDistance = 250;

    @InputArgument(flag = "COVERAGE_CAP", delimiter = "=")
    private Integer coverageGap = 200;

    @InputArgument(flag = "SAMPLE_SIZE", delimiter = "=")
    private Integer sampleSize = 10000;

    public PicardCollectHsMetrics() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return PicardCollectHsMetrics.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(), getWorkflowName().toUpperCase());
    }

    public File getInput() {
        return input;
    }

    public void setInput(File input) {
        this.input = input;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public File getBaitIntervals() {
        return baitIntervals;
    }

    public void setBaitIntervals(File baitIntervals) {
        this.baitIntervals = baitIntervals;
    }

    public File getTargetIntervals() {
        return targetIntervals;
    }

    public void setTargetIntervals(File targetIntervals) {
        this.targetIntervals = targetIntervals;
    }

    public String getBaitSetName() {
        return baitSetName;
    }

    public void setBaitSetName(String baitSetName) {
        this.baitSetName = baitSetName;
    }

    public Integer getMinimumMappingQuality() {
        return minimumMappingQuality;
    }

    public void setMinimumMappingQuality(Integer minimumMappingQuality) {
        this.minimumMappingQuality = minimumMappingQuality;
    }

    public Integer getMinimumBaseQuality() {
        return minimumBaseQuality;
    }

    public void setMinimumBaseQuality(Integer minimumBaseQuality) {
        this.minimumBaseQuality = minimumBaseQuality;
    }

    public Boolean getClipOverlappingReads() {
        return clipOverlappingReads;
    }

    public void setClipOverlappingReads(Boolean clipOverlappingReads) {
        this.clipOverlappingReads = clipOverlappingReads;
    }

    public String getMetricAccumulationLevel() {
        return metricAccumulationLevel;
    }

    public void setMetricAccumulationLevel(String metricAccumulationLevel) {
        this.metricAccumulationLevel = metricAccumulationLevel;
    }

    public File getPerTargetCoverage() {
        return perTargetCoverage;
    }

    public void setPerTargetCoverage(File perTargetCoverage) {
        this.perTargetCoverage = perTargetCoverage;
    }

    public File getPerBaseCoverage() {
        return perBaseCoverage;
    }

    public void setPerBaseCoverage(File perBaseCoverage) {
        this.perBaseCoverage = perBaseCoverage;
    }

    public Integer getNearDistance() {
        return nearDistance;
    }

    public void setNearDistance(Integer nearDistance) {
        this.nearDistance = nearDistance;
    }

    public Integer getCoverageGap() {
        return coverageGap;
    }

    public void setCoverageGap(Integer coverageGap) {
        this.coverageGap = coverageGap;
    }

    public Integer getSampleSize() {
        return sampleSize;
    }

    public void setSampleSize(Integer sampleSize) {
        this.sampleSize = sampleSize;
    }

    @Override
    public String toString() {
        return String.format(
                "PicardCollectHsMetrics [input=%s, output=%s, baitIntervals=%s, targetIntervals=%s, baitSetName=%s, minimumMappingQuality=%s, minimumBaseQuality=%s, clipOverlappingReads=%s, metricAccumulationLevel=%s, perTargetCoverage=%s, perBaseCoverage=%s, nearDistance=%s, coverageGap=%s, sampleSize=%s]",
                input, output, baitIntervals, targetIntervals, baitSetName, minimumMappingQuality, minimumBaseQuality, clipOverlappingReads,
                metricAccumulationLevel, perTargetCoverage, perBaseCoverage, nearDistance, coverageGap, sampleSize);
    }

    public static void main(String[] args) {
        PicardCollectHsMetrics module = new PicardCollectHsMetrics();
        module.setWorkflowName("TEST");
        module.setInput(new File("/tmp", "input.sam"));
        module.setOutput(new File("/tmp", "output.bam"));
        module.setBaitIntervals(new File("/tmp", "baitIntervals.interval_list"));
        module.setTargetIntervals(new File("/tmp", "targetIntervals.interval_list"));
        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
