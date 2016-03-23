package edu.unc.mapseq.module.sequencing.mapsplice;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.Properties;

import javax.validation.constraints.NotNull;

import org.renci.common.exec.BashExecutor;
import org.renci.common.exec.CommandInput;
import org.renci.common.exec.CommandOutput;
import org.renci.common.exec.Executor;
import org.renci.common.exec.ExecutorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleException;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.ShellModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;

@Application(name = "AlignmentHandlerMulti", executable = "$%s_MAPSPLICE_HOME/bin/alignment_handler_multi", wallTime = 5L)
public class AlignmentHandlerMulti extends Module {

    private final Logger logger = LoggerFactory.getLogger(AlignmentHandlerMulti.class);

    @InputArgument
    private File junctionFile;

    @InputArgument
    private File chromosomeDirectory;

    @NotNull(message = "maxMateDistance is required", groups = InputValidations.class)
    @InputArgument
    private Integer maximumMateDistance;

    @NotNull(message = "maximumHits is required", groups = InputValidations.class)
    @InputArgument
    private Integer maximumHits;

    @NotNull(message = "filteredAlignmentBase is required", groups = InputValidations.class)
    @InputArgument
    private File filteredAlignmentBase;

    @NotNull(message = "filteredAlignmentAppend is required", groups = InputValidations.class)
    @InputArgument
    private String filteredAlignmentAppend;

    @NotNull(message = "readLengthProperties is required", groups = InputValidations.class)
    @InputArgument
    private File readLengthProperties;

    @NotNull(message = "filterFlag is required", groups = InputValidations.class)
    @InputArgument
    private Integer filterFlag;

    @NotNull(message = "minimumInsertion is required", groups = InputValidations.class)
    @InputArgument
    private Integer minimumInsertion;

    @NotNull(message = "maximumDeletion is required", groups = InputValidations.class)
    @InputArgument
    private Integer maximumDeletion;

    @NotNull(message = "minimumAnchor is required", groups = InputValidations.class)
    @InputArgument
    private Integer minimumAnchor;

    @NotNull(message = "minimumJunctionAnchor is required", groups = InputValidations.class)
    @InputArgument
    private Integer minimumJunctionAnchor;

    @NotNull(message = "minimumMismatch is required", groups = InputValidations.class)
    @InputArgument
    private Integer minimumMismatch;

    @NotNull(message = "addSoftClip is required", groups = InputValidations.class)
    @InputArgument
    private Integer addSoftClip;

    @NotNull(message = "mateDistanceSD is required", groups = InputValidations.class)
    @InputArgument
    private Integer mateDistanceSD;

    @NotNull(message = "maximumAnchorDiff is required", groups = InputValidations.class)
    @InputArgument
    private Integer maximumAnchorDiff;

    @NotNull(message = "intronDistanceSD is required", groups = InputValidations.class)
    @InputArgument
    private Integer intronDistanceSD;

    @NotNull(message = "chromosomeSizeFile is required", groups = InputValidations.class)
    @InputArgument
    private File chromosomeSizeFile;

    @NotNull(message = "encompassingFusionRegionExtension is required", groups = InputValidations.class)
    @InputArgument
    private Integer encompassingFusionRegionExtension;

    @NotNull(message = "input is required", groups = InputValidations.class)
    @InputArgument
    private File input;

    @NotNull(message = "minimumCoverage is required", groups = InputValidations.class)
    @InputArgument
    private Integer minimumCoverage;

    @NotNull(message = "fragmentLength is required", groups = InputValidations.class)
    @InputArgument
    private Integer fragmentLength;

    @NotNull(message = "fragmentLengthSD is required", groups = InputValidations.class)
    @InputArgument
    private Integer fragmentLengthSD;

    @NotNull(message = "averageFragmentLength is required", groups = InputValidations.class)
    @InputArgument
    private Integer averageFragmentLength;

    @NotNull(message = "boundary is required", groups = InputValidations.class)
    @InputArgument
    private Integer boundary;

    @NotNull(message = "minimumEncompassCount is required", groups = InputValidations.class)
    @InputArgument
    private Integer minimumEncompassCount;

    @NotNull(message = "minimumEntropy is required", groups = InputValidations.class)
    @InputArgument
    private Double minimumEntropy;

    @NotNull(message = "threads is required", groups = InputValidations.class)
    @InputArgument
    private Integer threads;

    public AlignmentHandlerMulti() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return AlignmentHandlerMulti.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(),
                getWorkflowName().toUpperCase());
    }

    @Override
    public ModuleOutput call() throws Exception {
        logger.debug("ENTERING call()");

        Properties readLengthProperties = new Properties();
        FileInputStream fis = new FileInputStream(this.readLengthProperties);
        readLengthProperties.loadFromXML(fis);
        fis.close();

        StringBuilder command = new StringBuilder(getExecutable());

        if (junctionFile != null && junctionFile.exists()) {
            command.append(" ").append(junctionFile.getAbsolutePath());
        } else {
            command.append(" \"\"");
        }
        command.append(" 1");
        command.append(" ").append(maximumMateDistance);
        command.append(" ").append(maximumHits);
        command.append(" ").append(filteredAlignmentBase.getAbsolutePath());
        command.append(" ").append(readLengthProperties.getProperty("maxLength"));
        if (chromosomeDirectory != null && chromosomeDirectory.exists()) {
            command.append(" ").append(chromosomeDirectory.getAbsolutePath());
        } else {
            command.append(" \"\"");
        }
        command.append(" ").append(filterFlag);
        command.append(" ").append(minimumInsertion);
        command.append(" ").append(maximumDeletion);
        command.append(" ").append(minimumAnchor);
        command.append(" ").append(minimumJunctionAnchor);
        command.append(" ").append(minimumMismatch);
        command.append(" ").append(addSoftClip);
        command.append(" ").append(mateDistanceSD);
        command.append(" ").append(maximumAnchorDiff);
        command.append(" ").append(chromosomeSizeFile.getAbsolutePath());
        command.append(" ").append(encompassingFusionRegionExtension);
        command.append(" ").append(threads);
        command.append(" ").append(intronDistanceSD);
        command.append(" ").append(minimumCoverage);
        command.append(" ").append(fragmentLength);
        command.append(" ").append(fragmentLengthSD);
        command.append(" ").append(averageFragmentLength);
        command.append(" ").append(boundary);
        Integer maxLength = Integer.valueOf(readLengthProperties.getProperty("maxLength"));
        command.append(" ").append(maxLength / 2);
        if (maxLength > 75) {
            minimumEncompassCount = 0;
        }
        command.append(" ").append(minimumEncompassCount);
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(6);
        command.append(" ").append(df.format(minimumEntropy));
        command.append(" ").append(input.getAbsolutePath());
        command.append(" > ").append(input.getAbsolutePath()).append(".alignment_handler.log 2>&1");
        CommandInput commandInput = new CommandInput();
        logger.info("command.toString(): {}", command.toString());
        commandInput.setCommand(command.toString());
        CommandOutput commandOutput;
        try {
            Executor executor = BashExecutor.getInstance();
            commandOutput = executor.execute(commandInput);
        } catch (ExecutorException e) {
            throw new ModuleException(e);
        }
        return new ShellModuleOutput(commandOutput);
    }

    public File getJunctionFile() {
        return junctionFile;
    }

    public void setJunctionFile(File junctionFile) {
        this.junctionFile = junctionFile;
    }

    public File getChromosomeDirectory() {
        return chromosomeDirectory;
    }

    public void setChromosomeDirectory(File chromosomeDirectory) {
        this.chromosomeDirectory = chromosomeDirectory;
    }

    public Integer getMaximumMateDistance() {
        return maximumMateDistance;
    }

    public void setMaximumMateDistance(Integer maximumMateDistance) {
        this.maximumMateDistance = maximumMateDistance;
    }

    public Integer getMaximumHits() {
        return maximumHits;
    }

    public void setMaximumHits(Integer maximumHits) {
        this.maximumHits = maximumHits;
    }

    public Integer getMaximumDeletion() {
        return maximumDeletion;
    }

    public void setMaximumDeletion(Integer maximumDeletion) {
        this.maximumDeletion = maximumDeletion;
    }

    public Double getMinimumEntropy() {
        return minimumEntropy;
    }

    public void setMinimumEntropy(Double minimumEntropy) {
        this.minimumEntropy = minimumEntropy;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public File getFilteredAlignmentBase() {
        return filteredAlignmentBase;
    }

    public void setFilteredAlignmentBase(File filteredAlignmentBase) {
        this.filteredAlignmentBase = filteredAlignmentBase;
    }

    public String getFilteredAlignmentAppend() {
        return filteredAlignmentAppend;
    }

    public void setFilteredAlignmentAppend(String filteredAlignmentAppend) {
        this.filteredAlignmentAppend = filteredAlignmentAppend;
    }

    public File getReadLengthProperties() {
        return readLengthProperties;
    }

    public void setReadLengthProperties(File readLengthProperties) {
        this.readLengthProperties = readLengthProperties;
    }

    public Integer getFilterFlag() {
        return filterFlag;
    }

    public void setFilterFlag(Integer filterFlag) {
        this.filterFlag = filterFlag;
    }

    public Integer getMinimumInsertion() {
        return minimumInsertion;
    }

    public void setMinimumInsertion(Integer minimumInsertion) {
        this.minimumInsertion = minimumInsertion;
    }

    public Integer getMinimumAnchor() {
        return minimumAnchor;
    }

    public void setMinimumAnchor(Integer minimumAnchor) {
        this.minimumAnchor = minimumAnchor;
    }

    public Integer getMinimumJunctionAnchor() {
        return minimumJunctionAnchor;
    }

    public void setMinimumJunctionAnchor(Integer minimumJunctionAnchor) {
        this.minimumJunctionAnchor = minimumJunctionAnchor;
    }

    public Integer getMinimumMismatch() {
        return minimumMismatch;
    }

    public void setMinimumMismatch(Integer minimumMismatch) {
        this.minimumMismatch = minimumMismatch;
    }

    public Integer getAddSoftClip() {
        return addSoftClip;
    }

    public void setAddSoftClip(Integer addSoftClip) {
        this.addSoftClip = addSoftClip;
    }

    public Integer getMateDistanceSD() {
        return mateDistanceSD;
    }

    public void setMateDistanceSD(Integer mateDistanceSD) {
        this.mateDistanceSD = mateDistanceSD;
    }

    public Integer getMaximumAnchorDiff() {
        return maximumAnchorDiff;
    }

    public void setMaximumAnchorDiff(Integer maximumAnchorDiff) {
        this.maximumAnchorDiff = maximumAnchorDiff;
    }

    public Integer getIntronDistanceSD() {
        return intronDistanceSD;
    }

    public void setIntronDistanceSD(Integer intronDistanceSD) {
        this.intronDistanceSD = intronDistanceSD;
    }

    public File getChromosomeSizeFile() {
        return chromosomeSizeFile;
    }

    public void setChromosomeSizeFile(File chromosomeSizeFile) {
        this.chromosomeSizeFile = chromosomeSizeFile;
    }

    public Integer getEncompassingFusionRegionExtension() {
        return encompassingFusionRegionExtension;
    }

    public void setEncompassingFusionRegionExtension(Integer encompassingFusionRegionExtension) {
        this.encompassingFusionRegionExtension = encompassingFusionRegionExtension;
    }

    public File getInput() {
        return input;
    }

    public void setInput(File input) {
        this.input = input;
    }

    public Integer getMinimumCoverage() {
        return minimumCoverage;
    }

    public void setMinimumCoverage(Integer minimumCoverage) {
        this.minimumCoverage = minimumCoverage;
    }

    public Integer getFragmentLength() {
        return fragmentLength;
    }

    public void setFragmentLength(Integer fragmentLength) {
        this.fragmentLength = fragmentLength;
    }

    public Integer getFragmentLengthSD() {
        return fragmentLengthSD;
    }

    public void setFragmentLengthSD(Integer fragmentLengthSD) {
        this.fragmentLengthSD = fragmentLengthSD;
    }

    public Integer getAverageFragmentLength() {
        return averageFragmentLength;
    }

    public void setAverageFragmentLength(Integer averageFragmentLength) {
        this.averageFragmentLength = averageFragmentLength;
    }

    public Integer getBoundary() {
        return boundary;
    }

    public void setBoundary(Integer boundary) {
        this.boundary = boundary;
    }

    public Integer getMinimumEncompassCount() {
        return minimumEncompassCount;
    }

    public void setMinimumEncompassCount(Integer minimumEncompassCount) {
        this.minimumEncompassCount = minimumEncompassCount;
    }

    @Override
    public String toString() {
        return String.format(
                "AlignmentHandlerMulti [logger=%s, junctionFile=%s, chromosomeDirectory=%s, maximumMateDistance=%s, maximumHits=%s, filteredAlignmentBase=%s, filteredAlignmentAppend=%s, readLengthProperties=%s, filterFlag=%s, minimumInsertion=%s, maximumDeletion=%s, minimumAnchor=%s, minimumJunctionAnchor=%s, minimumMismatch=%s, addSoftClip=%s, mateDistanceSD=%s, maximumAnchorDiff=%s, intronDistanceSD=%s, chromosomeSizeFile=%s, encompassingFusionRegionExtension=%s, input=%s, minimumCoverage=%s, fragmentLength=%s, fragmentLengthSD=%s, averageFragmentLength=%s, boundary=%s, minimumEncompassCount=%s, minimumEntropy=%s, threads=%s, toString()=%s]",
                logger, junctionFile, chromosomeDirectory, maximumMateDistance, maximumHits, filteredAlignmentBase,
                filteredAlignmentAppend, readLengthProperties, filterFlag, minimumInsertion, maximumDeletion,
                minimumAnchor, minimumJunctionAnchor, minimumMismatch, addSoftClip, mateDistanceSD, maximumAnchorDiff,
                intronDistanceSD, chromosomeSizeFile, encompassingFusionRegionExtension, input, minimumCoverage,
                fragmentLength, fragmentLengthSD, averageFragmentLength, boundary, minimumEncompassCount,
                minimumEntropy, threads, super.toString());
    }

    public static void main(String[] args) {

        AlignmentHandlerMulti module = new AlignmentHandlerMulti();
        module.setWorkflowName("TEST");
        module.setAddSoftClip(1);
        module.setAverageFragmentLength(225);
        module.setBoundary(36);
        module.setChromosomeSizeFile(new File(
                "/proj/seq/mapseq/LBG/131104_UNC15-SN850_0337_BC2LWCACXX/RNASeq/C01_P-4-CALGB1066769-1/tmp/chrom_sizes"));
        module.setEncompassingFusionRegionExtension(50000);
        module.setFilteredAlignmentAppend("_filtered_normal_alignments");
        module.setFilteredAlignmentBase(new File(
                "/proj/seq/mapseq/LBG/131104_UNC15-SN850_0337_BC2LWCACXX/RNASeq/C01_P-4-CALGB1066769-1/tmp/remap/_filtered_normal_alignments"));
        module.setFilterFlag(12 + 32 + 256);
        module.setFragmentLength(400);
        module.setFragmentLengthSD(100);
        module.setInput(new File(
                "/proj/seq/mapseq/LBG/131104_UNC15-SN850_0337_BC2LWCACXX/RNASeq/C01_P-4-CALGB1066769-1/tmp/remap/remapped.sam"));
        module.setIntronDistanceSD(500);
        module.setMateDistanceSD(100);
        module.setMaximumAnchorDiff(50);
        module.setMaximumDeletion(6);
        module.setMaximumHits(40);
        module.setMaximumMateDistance(50000);
        module.setReadLengthProperties(new File("/home/jdr0887/tmp/readLengthProperties.xml"));
        module.setMinimumAnchor(0);
        module.setMinimumCoverage(0);
        module.setMinimumEncompassCount(1);
        module.setMinimumEntropy(-0.0001);
        module.setMinimumInsertion(6);
        module.setMinimumJunctionAnchor(10);
        module.setMinimumMismatch(5);
        module.setThreads(8);
        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
