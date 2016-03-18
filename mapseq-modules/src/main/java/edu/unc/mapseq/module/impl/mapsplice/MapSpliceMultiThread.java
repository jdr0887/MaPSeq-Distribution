package edu.unc.mapseq.module.impl.mapsplice;

import java.io.File;

import javax.validation.constraints.NotNull;

import org.renci.common.exec.BashExecutor;
import org.renci.common.exec.CommandInput;
import org.renci.common.exec.CommandOutput;
import org.renci.common.exec.Executor;
import org.renci.common.exec.ExecutorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleException;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.ShellModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "MapSpliceMultiThread", executable = "$%s_MAPSPLICE_HOME/bin/mapsplice_multi_thread", wallTime = 5L)
public class MapSpliceMultiThread extends Module {

    private final Logger logger = LoggerFactory.getLogger(MapSpliceMultiThread.class);

    @NotNull(message = "fastq1 is required", groups = InputValidations.class)
    @FileIsReadable(message = "fastq2 does not exist or is not readable", groups = InputValidations.class)
    @InputArgument
    private File fastq1;

    @NotNull(message = "fastq2 is required", groups = InputValidations.class)
    @FileIsReadable(message = "fastq2 does not exist or is not readable", groups = InputValidations.class)
    @InputArgument
    private File fastq2;

    @NotNull(message = "index is required", groups = InputValidations.class)
    @InputArgument
    private File index;

    @NotNull(message = "mapspliceOut is required", groups = InputValidations.class)
    @InputArgument
    private File mapspliceOut;

    @NotNull(message = "output is required", groups = InputValidations.class)
    @OutputArgument(mimeType = MimeType.BOWTIE_HITS, persistFileData = true, delimiter = "")
    private File output;

    @InputArgument
    private QualityScaleType qualityScale;

    @InputArgument
    private Integer minimumLength;

    @InputArgument
    private Integer segmentLength;

    @InputArgument
    private Integer minimumIntron;

    @InputArgument
    private Integer suppressAlignmentsOver;

    @InputArgument
    private Integer maximumIntronSingle;

    @InputArgument
    private Integer maximumIntronDouble;

    @InputArgument
    private Integer spliceMismatches;

    @InputArgument
    private Integer maximumAppendMismatches;

    @InputArgument
    private Integer maximumInsertions;

    @InputArgument
    private Integer maximumDeletions;

    @InputArgument
    private Integer reportUpToNAlignmentsPerRead;

    @InputArgument
    private Integer threads = 1;

    @InputArgument
    private File chromosomeSize;

    @InputArgument
    private File referenceSequenceDirectory;

    @InputArgument
    private Boolean spliceOnly;

    @InputArgument
    private Boolean optimizeRepeats;

    @InputArgument
    private File outputUnMapped;

    @InputArgument
    private Boolean doubleAnchorNonCanonical;

    @InputArgument
    private Integer minimumMapLength;

    @InputArgument
    private Boolean fastqReadFormat;

    @InputArgument
    private File clusterRegion;

    @InputArgument
    private File fusion;

    @InputArgument
    private File junctionIndex;

    @InputArgument
    private File fusionIndex;

    @Override
    public Class<?> getModuleClass() {
        return MapSpliceMultiThread.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(),
                getWorkflowName().toUpperCase());
    }

    @Override
    public ModuleOutput call() throws Exception {
        logger.debug("ENTERING call()");

        StringBuilder command = new StringBuilder(getExecutable());

        if (this.qualityScale != null) {
            command.append(" --qual-scale ").append(this.qualityScale.name());
        }

        if (this.minimumLength != null) {
            command.append(" --min_len ").append(this.minimumLength.toString());
        }

        if (this.segmentLength != null) {
            command.append(" --seg_len ").append(this.segmentLength.toString());
        }

        if (this.minimumIntron != null) {
            command.append(" --min_intron ").append(this.minimumIntron.toString());
        }

        if (this.suppressAlignmentsOver != null) {
            command.append(" -m ").append(this.suppressAlignmentsOver.toString());
        }

        if (this.maximumIntronSingle != null) {
            command.append(" --max_intron_single ").append(this.maximumIntronSingle.toString());
        }

        if (this.maximumIntronDouble != null) {
            command.append(" --max_intron_double ").append(this.maximumIntronDouble.toString());
        }

        if (this.spliceMismatches != null) {
            command.append(" -v ").append(this.spliceMismatches.toString());
        }

        if (this.maximumAppendMismatches != null) {
            command.append(" --max_append_mis ").append(this.maximumAppendMismatches.toString());
        }

        if (this.doubleAnchorNonCanonical != null && this.doubleAnchorNonCanonical) {
            command.append(" --double_anchor_noncanon");
        }

        if (this.maximumInsertions != null) {
            command.append(" --max_ins ").append(this.maximumInsertions.toString());
        }

        if (this.maximumDeletions != null) {
            command.append(" --max_del ").append(this.maximumDeletions.toString());
        }

        if (this.reportUpToNAlignmentsPerRead != null) {
            command.append(" -k ").append(this.reportUpToNAlignmentsPerRead.toString());
        }

        if (this.threads != null) {
            command.append(" --threads ").append(this.threads.toString());
        }

        if (this.chromosomeSize != null) {
            command.append(" --chrom_tab ").append(this.chromosomeSize.getAbsolutePath());
        }

        if (this.referenceSequenceDirectory != null) {
            command.append(" --ref_seq_path ").append(this.referenceSequenceDirectory.getAbsolutePath());
            if (this.referenceSequenceDirectory.isDirectory()
                    && !this.referenceSequenceDirectory.getAbsolutePath().endsWith("/")) {
                command.append("/");
            }
        }

        if (this.junctionIndex != null) {
            command.append(" --juncdb_index ").append(this.junctionIndex.getAbsolutePath());
        }

        if (this.fusionIndex != null) {
            command.append(" --fusiondb_index ").append(this.fusionIndex.getAbsolutePath());
        }

        if (this.optimizeRepeats != null && this.optimizeRepeats) {
            command.append(" --optimize_repeats ");
        }

        if (this.spliceOnly != null && this.spliceOnly) {
            command.append(" --splice_only ");
        }

        if (this.outputUnMapped != null) {
            command.append(" --output_unmapped_pe ").append(this.outputUnMapped.getAbsolutePath());
        }

        if (this.minimumMapLength != null) {
            command.append(" --min_map_len ").append(this.minimumMapLength.toString());
        }

        if (this.fastqReadFormat != null && this.fastqReadFormat) {
            command.append(" -q ");
        }

        command.append(" --mapsplice_out ").append(this.mapspliceOut.getAbsolutePath());

        if (this.clusterRegion != null) {
            command.append(" --cluster ").append(this.clusterRegion.getAbsolutePath());
        }

        if (this.fusion != null) {
            command.append(" --fusion ").append(this.fusion.getAbsolutePath());
        }

        command.append(" ").append(this.index.getAbsolutePath());
        command.append(" -1 ").append(this.fastq1.getAbsolutePath());
        command.append(" -2 ").append(this.fastq2.getAbsolutePath());
        command.append(" ").append(this.output.getAbsolutePath());

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

    public File getJunctionIndex() {
        return junctionIndex;
    }

    public void setJunctionIndex(File junctionIndex) {
        this.junctionIndex = junctionIndex;
    }

    public File getClusterRegion() {
        return clusterRegion;
    }

    public void setClusterRegion(File clusterRegion) {
        this.clusterRegion = clusterRegion;
    }

    public File getFusion() {
        return fusion;
    }

    public void setFusion(File fusion) {
        this.fusion = fusion;
    }

    public Boolean getFastqReadFormat() {
        return fastqReadFormat;
    }

    public void setFastqReadFormat(Boolean fastqReadFormat) {
        this.fastqReadFormat = fastqReadFormat;
    }

    public File getFastq1() {
        return fastq1;
    }

    public void setFastq1(File fastq1) {
        this.fastq1 = fastq1;
    }

    public File getFastq2() {
        return fastq2;
    }

    public void setFastq2(File fastq2) {
        this.fastq2 = fastq2;
    }

    public File getIndex() {
        return index;
    }

    public void setIndex(File index) {
        this.index = index;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public QualityScaleType getQualityScale() {
        return qualityScale;
    }

    public void setQualityScale(QualityScaleType qualityScale) {
        this.qualityScale = qualityScale;
    }

    public Integer getMinimumLength() {
        return minimumLength;
    }

    public void setMinimumLength(Integer minimumLength) {
        this.minimumLength = minimumLength;
    }

    public Integer getSegmentLength() {
        return segmentLength;
    }

    public void setSegmentLength(Integer segmentLength) {
        this.segmentLength = segmentLength;
    }

    public Integer getMinimumIntron() {
        return minimumIntron;
    }

    public void setMinimumIntron(Integer minimumIntron) {
        this.minimumIntron = minimumIntron;
    }

    public Integer getSuppressAlignmentsOver() {
        return suppressAlignmentsOver;
    }

    public void setSuppressAlignmentsOver(Integer suppressAlignmentsOver) {
        this.suppressAlignmentsOver = suppressAlignmentsOver;
    }

    public Integer getMaximumIntronSingle() {
        return maximumIntronSingle;
    }

    public void setMaximumIntronSingle(Integer maximumIntronSingle) {
        this.maximumIntronSingle = maximumIntronSingle;
    }

    public Integer getMaximumIntronDouble() {
        return maximumIntronDouble;
    }

    public void setMaximumIntronDouble(Integer maximumIntronDouble) {
        this.maximumIntronDouble = maximumIntronDouble;
    }

    public Integer getSpliceMismatches() {
        return spliceMismatches;
    }

    public void setSpliceMismatches(Integer spliceMismatches) {
        this.spliceMismatches = spliceMismatches;
    }

    public Integer getMaximumAppendMismatches() {
        return maximumAppendMismatches;
    }

    public void setMaximumAppendMismatches(Integer maximumAppendMismatches) {
        this.maximumAppendMismatches = maximumAppendMismatches;
    }

    public Integer getMaximumInsertions() {
        return maximumInsertions;
    }

    public void setMaximumInsertions(Integer maximumInsertions) {
        this.maximumInsertions = maximumInsertions;
    }

    public Integer getMaximumDeletions() {
        return maximumDeletions;
    }

    public void setMaximumDeletions(Integer maximumDeletions) {
        this.maximumDeletions = maximumDeletions;
    }

    public Integer getReportUpToNAlignmentsPerRead() {
        return reportUpToNAlignmentsPerRead;
    }

    public void setReportUpToNAlignmentsPerRead(Integer reportUpToNAlignmentsPerRead) {
        this.reportUpToNAlignmentsPerRead = reportUpToNAlignmentsPerRead;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public File getFusionIndex() {
        return fusionIndex;
    }

    public void setFusionIndex(File fusionIndex) {
        this.fusionIndex = fusionIndex;
    }

    public File getChromosomeSize() {
        return chromosomeSize;
    }

    public void setChromosomeSize(File chromosomeSize) {
        this.chromosomeSize = chromosomeSize;
    }

    public File getReferenceSequenceDirectory() {
        return referenceSequenceDirectory;
    }

    public void setReferenceSequenceDirectory(File referenceSequenceDirectory) {
        this.referenceSequenceDirectory = referenceSequenceDirectory;
    }

    public File getMapspliceOut() {
        return mapspliceOut;
    }

    public void setMapspliceOut(File mapspliceOut) {
        this.mapspliceOut = mapspliceOut;
    }

    public Boolean getSpliceOnly() {
        return spliceOnly;
    }

    public void setSpliceOnly(Boolean spliceOnly) {
        this.spliceOnly = spliceOnly;
    }

    public Boolean getOptimizeRepeats() {
        return optimizeRepeats;
    }

    public void setOptimizeRepeats(Boolean optimizeRepeats) {
        this.optimizeRepeats = optimizeRepeats;
    }

    public File getOutputUnMapped() {
        return outputUnMapped;
    }

    public void setOutputUnMapped(File outputUnMapped) {
        this.outputUnMapped = outputUnMapped;
    }

    public Boolean getDoubleAnchorNonCanonical() {
        return doubleAnchorNonCanonical;
    }

    public void setDoubleAnchorNonCanonical(Boolean doubleAnchorNonCanonical) {
        this.doubleAnchorNonCanonical = doubleAnchorNonCanonical;
    }

    public Integer getMinimumMapLength() {
        return minimumMapLength;
    }

    public void setMinimumMapLength(Integer minimumMapLength) {
        this.minimumMapLength = minimumMapLength;
    }

    @Override
    public String toString() {
        return String.format(
                "MapSpliceMultiThread [logger=%s, fastq1=%s, fastq2=%s, index=%s, mapspliceOut=%s, output=%s, qualityScale=%s, minimumLength=%s, segmentLength=%s, minimumIntron=%s, suppressAlignmentsOver=%s, maximumIntronSingle=%s, maximumIntronDouble=%s, spliceMismatches=%s, maximumAppendMismatches=%s, maximumInsertions=%s, maximumDeletions=%s, reportUpToNAlignmentsPerRead=%s, threads=%s, chromosomeSize=%s, referenceSequenceDirectory=%s, spliceOnly=%s, optimizeRepeats=%s, outputUnMapped=%s, doubleAnchorNonCanonical=%s, minimumMapLength=%s, fastqReadFormat=%s, clusterRegion=%s, fusion=%s, junctionIndex=%s, fusionIndex=%s, toString()=%s]",
                logger, fastq1, fastq2, index, mapspliceOut, output, qualityScale, minimumLength, segmentLength,
                minimumIntron, suppressAlignmentsOver, maximumIntronSingle, maximumIntronDouble, spliceMismatches,
                maximumAppendMismatches, maximumInsertions, maximumDeletions, reportUpToNAlignmentsPerRead, threads,
                chromosomeSize, referenceSequenceDirectory, spliceOnly, optimizeRepeats, outputUnMapped,
                doubleAnchorNonCanonical, minimumMapLength, fastqReadFormat, clusterRegion, fusion, junctionIndex,
                fusionIndex, super.toString());
    }

    public static void main(String[] args) {

        MapSpliceMultiThread module = new MapSpliceMultiThread();
        module.setWorkflowName("TEST");

        File fastqR1 = new File("/proj/seq/mapseq/LBG/dev/130913_UNC12-SN629_0330_AD2E14ACXX/RNASeq/SL355-2R0-CP-671",
                "130913_UNC12-SN629_0330_AD2E14ACXX_CTTGTA_L005_R1.filtered.fastq");
        File fastqR2 = new File("/proj/seq/mapseq/LBG/dev/130913_UNC12-SN629_0330_AD2E14ACXX/RNASeq/SL355-2R0-CP-671",
                "130913_UNC12-SN629_0330_AD2E14ACXX_CTTGTA_L005_R2.filtered.fastq");

        module.setFastq1(fastqR1);
        module.setFastq2(fastqR2);
        module.setQualityScale(QualityScaleType.phred33);
        module.setThreads(2);
        module.setOutput(new File("/home/jdr0887/data/mapsplice/tmp/original", "bowtie.output"));
        module.setMapspliceOut(new File("/home/jdr0887/data/mapsplice/tmp/original", "original.sam"));
        module.setOutputUnMapped(new File("/home/jdr0887/data/mapsplice/tmp/original", "original_unmapped"));
        module.setReferenceSequenceDirectory(new File("/home/jdr0887/data/mapsplice/hg19_M_rCRS/chromosomes"));
        module.setIndex(new File("/home/jdr0887/data/mapsplice/hg19_M_rCRS/ebwt/humanchridx_M_rCRS"));
        module.setMinimumIntron(50);
        module.setMaximumIntronSingle(200000);
        module.setSuppressAlignmentsOver(40);
        module.setMaximumIntronDouble(200000);
        module.setReportUpToNAlignmentsPerRead(40);
        module.setMinimumLength(25);
        module.setSpliceMismatches(1);
        module.setSpliceOnly(Boolean.TRUE);
        module.setFastqReadFormat(Boolean.TRUE);
        module.setMaximumInsertions(6);
        module.setMaximumDeletions(6);
        module.setMinimumMapLength(1);
        module.setMaximumAppendMismatches(3);
        module.setSegmentLength(25);
        module.setChromosomeSize(new File(
                "/proj/seq/mapseq/LBG/dev/130913_UNC12-SN629_0330_AD2E14ACXX/RNASeq/SL355-2R0-CP-671/tmp/chrom_sizes"));

        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
