package edu.unc.mapseq.module.gatk;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.renci.common.exec.BashExecutor;
import org.renci.common.exec.CommandInput;
import org.renci.common.exec.CommandOutput;
import org.renci.common.exec.Executor;
import org.renci.common.exec.ExecutorException;

import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleException;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.ShellModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.Executable;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "GATKDepthOfCoverage")
@Executable(value = "$JAVA_HOME/bin/java -Xmx4g -Djava.io.tmpdir=$MAPSEQ_HOME/tmp -jar $%s_GATK_HOME/GenomeAnalysisTK.jar --analysis_type DepthOfCoverage")
public class GATKDepthOfCoverage extends Module {

    @NotNull(message = "inputFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "inputFile is not readable", groups = InputValidations.class)
    @InputArgument
    private File inputFile;

    @NotNull(message = "referenceSequence is required", groups = InputValidations.class)
    @FileIsReadable(message = "referenceSequence is not readable", groups = InputValidations.class)
    @InputArgument
    private File referenceSequence;

    @InputArgument
    private Integer numThreads;

    @NotNull(message = "out is required", groups = InputValidations.class)
    @InputArgument
    private String outputPrefix;

    @InputArgument
    private File workDirectory;

    @InputArgument
    private String intervalMerging;

    @InputArgument
    private File calculateCoverageOverGenes;

    @InputArgument
    private Byte maxBaseQuality;

    @InputArgument
    private Integer maxMappingQuality;

    @InputArgument
    private Byte minBaseQuality;

    @InputArgument
    private Integer minMappingQuality;

    @InputArgument
    private Boolean omitDepthOutputAtEachBase = Boolean.FALSE;

    @InputArgument
    private Boolean omitIntervalStatistics = Boolean.FALSE;

    @InputArgument
    private Boolean omitLocusTable = Boolean.FALSE;

    @InputArgument
    private Boolean omitPerSampleStats = Boolean.FALSE;

    @InputArgument
    private String outputFormat;

    @InputArgument
    private Boolean printBaseCounts = Boolean.FALSE;

    @InputArgument
    private String validationStrictness;

    @InputArgument
    private File intervals;

    @InputArgument
    private List<Integer> summaryCoverageThreshold;

    @InputArgument
    private String phoneHome;

    @InputArgument
    private String downsamplingType;

    @Override
    public Class<?> getModuleClass() {
        return GATKDepthOfCoverage.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Executable.class).value(), getWorkflowName().toUpperCase());
    }

    @Override
    public ModuleOutput call() throws Exception {
        CommandInput commandInput = new CommandInput();
        StringBuilder command = new StringBuilder();
        command.append(getExecutable());
        command.append(" --input_file ").append(inputFile.getAbsolutePath());
        command.append(" --reference_sequence ").append(referenceSequence.getAbsolutePath());

        if (StringUtils.isNotEmpty(phoneHome)) {
            command.append(" --phone_home ").append(phoneHome);
        }

        if (StringUtils.isNotEmpty(downsamplingType)) {
            command.append(" --downsampling_type ").append(downsamplingType);
        }

        if (numThreads != null && numThreads > 1) {
            command.append(" --num_threads " + numThreads);
        }

        if (StringUtils.isNotEmpty(validationStrictness)) {
            command.append(" --validation_strictness ").append(validationStrictness);
        }

        if (StringUtils.isNotEmpty(intervalMerging)) {
            command.append(" --interval_merging ").append(intervalMerging);
        }

        if (intervals != null) {
            command.append(" --intervals ").append(intervals.getAbsolutePath());
        }

        if (calculateCoverageOverGenes != null) {
            command.append(" --calculateCoverageOverGenes ").append(calculateCoverageOverGenes.getAbsolutePath());
        }

        if (maxBaseQuality != null) {
            command.append(" --maxBaseQuality ").append(maxBaseQuality.longValue());
        }

        if (maxMappingQuality != null) {
            command.append(" --maxMappingQuality ").append(maxMappingQuality);
        }

        if (minBaseQuality != null) {
            command.append(" --minBaseQuality ").append(minBaseQuality.longValue());
        }

        if (minMappingQuality != null) {
            command.append(" --minMappingQuality ").append(minMappingQuality);
        }

        if (omitDepthOutputAtEachBase) {
            command.append(" --omitDepthOutputAtEachBase ");
        }

        if (omitIntervalStatistics) {
            command.append(" --omitIntervalStatistics ");
        }

        if (omitLocusTable) {
            command.append(" --omitLocusTable ");
        }

        if (omitPerSampleStats) {
            command.append(" --omitPerSampleStats ");
        }

        if (StringUtils.isNotEmpty(outputFormat)) {
            command.append(" --outputFormat ").append(outputFormat);
        }

        if (printBaseCounts) {
            command.append(" --printBaseCounts ");
        }

        if (summaryCoverageThreshold != null && summaryCoverageThreshold.size() > 0) {
            for (Integer sct : summaryCoverageThreshold) {
                command.append(" --summaryCoverageThreshold ").append(sct);
            }
        }

        if (workDirectory != null) {
            command.append(" --out ").append(workDirectory.getAbsolutePath()).append("/").append(outputPrefix);
        } else {
            command.append(" --out ").append(System.getProperty("user.dir")).append("/").append(outputPrefix);
        }

        commandInput.setCommand(command.toString());
        CommandOutput commandOutput;
        try {
            Executor executor = BashExecutor.getInstance();
            commandOutput = executor.execute(commandInput);
        } catch (ExecutorException e) {
            throw new ModuleException(e);
        }

        FileData fileData = new FileData();
        fileData.setName(String.format("%s.sample_summary", outputPrefix));
        fileData.setMimeType(MimeType.TEXT_DEPTH_OF_COVERAGE_SUMMARY);
        getFileDatas().add(fileData);

        return new ShellModuleOutput(commandOutput);
    }

    public File getInputFile() {
        return inputFile;
    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }

    public String getIntervalMerging() {
        return intervalMerging;
    }

    public void setIntervalMerging(String intervalMerging) {
        this.intervalMerging = intervalMerging;
    }

    public File getReferenceSequence() {
        return referenceSequence;
    }

    public void setReferenceSequence(File referenceSequence) {
        this.referenceSequence = referenceSequence;
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

    public Integer getNumThreads() {
        return numThreads;
    }

    public void setNumThreads(Integer numThreads) {
        this.numThreads = numThreads;
    }

    public List<Integer> getSummaryCoverageThreshold() {
        return summaryCoverageThreshold;
    }

    public void setSummaryCoverageThreshold(List<Integer> summaryCoverageThreshold) {
        this.summaryCoverageThreshold = summaryCoverageThreshold;
    }

    public String getOutputPrefix() {
        return outputPrefix;
    }

    public void setOutputPrefix(String outputPrefix) {
        this.outputPrefix = outputPrefix;
    }

    public File getCalculateCoverageOverGenes() {
        return calculateCoverageOverGenes;
    }

    public void setCalculateCoverageOverGenes(File calculateCoverageOverGenes) {
        this.calculateCoverageOverGenes = calculateCoverageOverGenes;
    }

    public Byte getMaxBaseQuality() {
        return maxBaseQuality;
    }

    public void setMaxBaseQuality(Byte maxBaseQuality) {
        this.maxBaseQuality = maxBaseQuality;
    }

    public Integer getMaxMappingQuality() {
        return maxMappingQuality;
    }

    public void setMaxMappingQuality(Integer maxMappingQuality) {
        this.maxMappingQuality = maxMappingQuality;
    }

    public Byte getMinBaseQuality() {
        return minBaseQuality;
    }

    public void setMinBaseQuality(Byte minBaseQuality) {
        this.minBaseQuality = minBaseQuality;
    }

    public Integer getMinMappingQuality() {
        return minMappingQuality;
    }

    public void setMinMappingQuality(Integer minMappingQuality) {
        this.minMappingQuality = minMappingQuality;
    }

    public Boolean getOmitDepthOutputAtEachBase() {
        return omitDepthOutputAtEachBase;
    }

    public void setOmitDepthOutputAtEachBase(Boolean omitDepthOutputAtEachBase) {
        this.omitDepthOutputAtEachBase = omitDepthOutputAtEachBase;
    }

    public Boolean getOmitIntervalStatistics() {
        return omitIntervalStatistics;
    }

    public void setOmitIntervalStatistics(Boolean omitIntervalStatistics) {
        this.omitIntervalStatistics = omitIntervalStatistics;
    }

    public Boolean getOmitLocusTable() {
        return omitLocusTable;
    }

    public void setOmitLocusTable(Boolean omitLocusTable) {
        this.omitLocusTable = omitLocusTable;
    }

    public Boolean getOmitPerSampleStats() {
        return omitPerSampleStats;
    }

    public void setOmitPerSampleStats(Boolean omitPerSampleStats) {
        this.omitPerSampleStats = omitPerSampleStats;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public Boolean getPrintBaseCounts() {
        return printBaseCounts;
    }

    public void setPrintBaseCounts(Boolean printBaseCounts) {
        this.printBaseCounts = printBaseCounts;
    }

    public String getValidationStrictness() {
        return validationStrictness;
    }

    public void setValidationStrictness(String validationStrictness) {
        this.validationStrictness = validationStrictness;
    }

    public File getIntervals() {
        return intervals;
    }

    public void setIntervals(File intervals) {
        this.intervals = intervals;
    }

    public File getWorkDirectory() {
        return workDirectory;
    }

    public void setWorkDirectory(File workDirectory) {
        this.workDirectory = workDirectory;
    }

    public static void main(String[] args) {
        GATKDepthOfCoverage module = new GATKDepthOfCoverage();
        module.setWorkflowName("TEST");
        module.setReferenceSequence(new File("/tmp", "reference"));
        module.setInputFile(new File("/tmp", "input"));
        module.setOutputPrefix("asdfasdf");
        module.setIntervals(new File("/tmp", "intervals"));
        module.setDownsamplingType(GATKDownsamplingType.NONE.toString());
        module.setPhoneHome(GATKPhoneHomeType.NO_ET.toString());
        List<Integer> sctList = new ArrayList<Integer>();
        sctList.add(1);
        sctList.add(2);
        sctList.add(3);
        sctList.add(4);
        sctList.add(5);
        module.setSummaryCoverageThreshold(sctList);
        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
