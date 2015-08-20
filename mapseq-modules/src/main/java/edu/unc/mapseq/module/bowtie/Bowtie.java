package edu.unc.mapseq.module.bowtie;

import java.io.File;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "Bowtie", executable = "$%s_BOWTIE_HOME/bowtie")
public class Bowtie extends Module {

    @NotNull(message = "fastq1 is required", groups = InputValidations.class)
    @FileIsReadable(message = "fastq2 does not exist or is not readable", groups = InputValidations.class)
    @InputArgument(order = 1, delimiter = "")
    private File fastq1;

    @InputArgument(order = 2, delimiter = "")
    private File fastq2;

    @NotNull(message = "index is required", groups = InputValidations.class)
    @InputArgument(order = 0, delimiter = "")
    private File index;

    @NotNull(message = "hits is required", groups = InputValidations.class)
    @FileIsReadable(message = "hits does not exist or is not readable", groups = OutputValidations.class)
    @OutputArgument(mimeType = MimeType.BOWTIE_HITS, persistFileData = true, delimiter = "")
    private File hits;

    @InputArgument(flag = "-q")
    private Boolean queryInputFiles;

    @InputArgument(flag = "-l")
    private Integer seedLength = 28;

    @InputArgument(flag = "--nomaqround")
    private Boolean noMAQRound;

    @InputArgument(flag = "--best")
    private Boolean best;

    @InputArgument(flag = "--quiet")
    private Boolean quiet;

    @InputArgument(flag = "--threads")
    private Integer threads = 1;

    @InputArgument(flag = "--seed")
    private Integer seed;

    @InputArgument(flag = "--sam")
    private Boolean samFormat;

    @InputArgument(flag = "--phred33-quals")
    private Boolean phred33QualityScore;

    @InputArgument(flag = "--phred64-quals")
    private Boolean phred64QualityScore;

    @InputArgument(flag = "--solexa-quals")
    private Boolean solexaQualityScore;

    @InputArgument(flag = "--solexa1.3-quals")
    private Boolean solexa13QualityScore;

    @InputArgument(flag = "--integer-quals")
    private Boolean integerQualityScore;

    @InputArgument(flag = "-n")
    private Integer seedMaximumMismatches = 2;

    @InputArgument(flag = "-e")
    private Integer maxSumOfMismatchQuals = 70;

    @InputArgument(flag = "-v")
    private Integer reportEndToEndHits;

    @InputArgument(flag = "-I")
    private Integer minimumInsertSize = 0;

    @InputArgument(flag = "-X")
    private Integer maximumInsertSize = 250;

    @InputArgument(flag = "-k")
    private Integer reportGoodAlignmentsPerRead = 1;

    @InputArgument(flag = "-m")
    private Integer suppressAlignments;

    @InputArgument(flag = "-Q")
    private File quals;

    @InputArgument(flag = "-s")
    private Integer skipNReads;

    @InputArgument(flag = "-u")
    private Integer stopAfterNReads;

    @InputArgument(flag = "--nofw")
    private Boolean doNotAlignToForwardReferenceStrand;

    @InputArgument(flag = "--norc")
    private Boolean doNotAlignToReverseComplementReferenceStrand;

    @InputArgument(flag = "--maxbts")
    private Integer maxNBacktracks;

    @InputArgument(flag = "--pairtries")
    private Integer maxAttemptsToFindMate;

    @InputArgument(flag = "--tryhard")
    private Boolean tryHardToFindValidAlignments;

    @InputArgument(flag = "--chunkmbs")
    private Integer maxMemoryForBestFirstSearch = 64;

    @InputArgument(flag = "--all")
    private Boolean reportAllAlignmentsPerRead;

    @InputArgument(flag = "--time")
    private Boolean printWallClockTimeTakenBySearchPhases;

    @InputArgument(flag = "-B")
    private Integer leftMostRefOffset;

    @InputArgument(flag = "--refout")
    private Boolean writeAlignmentsToMapFilePerReference;

    @InputArgument(flag = "--refidx")
    private Boolean useZeroBasedIndex;

    @InputArgument(flag = "--al")
    private File writeAlignedReads;

    @InputArgument(flag = "--un")
    private File writeUnAlignedReads;

    @InputArgument(flag = "--fullref")
    private Boolean writeEntireRefName;

    @InputArgument(flag = "--mapq")
    private Integer mappingQuality;

    @InputArgument(flag = "--sam-nohead")
    private Boolean suppressHeaderLines;

    @InputArgument(flag = "--sam-nosq")
    private Boolean suppressSQHeader;

    @InputArgument(flag = "-o")
    private Integer offRateOfIndex;

    @Override
    public Class<?> getModuleClass() {
        return Bowtie.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(), getWorkflowName()
                .toUpperCase());
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

    public File getHits() {
        return hits;
    }

    public void setHits(File hits) {
        this.hits = hits;
    }

    public Boolean getQueryInputFiles() {
        return queryInputFiles;
    }

    public void setQueryInputFiles(Boolean queryInputFiles) {
        this.queryInputFiles = queryInputFiles;
    }

    public Integer getSeedLength() {
        return seedLength;
    }

    public void setSeedLength(Integer seedLength) {
        this.seedLength = seedLength;
    }

    public Boolean getNoMAQRound() {
        return noMAQRound;
    }

    public void setNoMAQRound(Boolean noMAQRound) {
        this.noMAQRound = noMAQRound;
    }

    public Boolean getBest() {
        return best;
    }

    public void setBest(Boolean best) {
        this.best = best;
    }

    public File getIndex() {
        return index;
    }

    public void setIndex(File index) {
        this.index = index;
    }

    public Boolean getQuiet() {
        return quiet;
    }

    public void setQuiet(Boolean quiet) {
        this.quiet = quiet;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public Integer getSeed() {
        return seed;
    }

    public void setSeed(Integer seed) {
        this.seed = seed;
    }

    public Boolean getSamFormat() {
        return samFormat;
    }

    public void setSamFormat(Boolean samFormat) {
        this.samFormat = samFormat;
    }

    public Boolean getPhred33QualityScore() {
        return phred33QualityScore;
    }

    public void setPhred33QualityScore(Boolean phred33QualityScore) {
        this.phred33QualityScore = phred33QualityScore;
    }

    public Boolean getPhred64QualityScore() {
        return phred64QualityScore;
    }

    public void setPhred64QualityScore(Boolean phred64QualityScore) {
        this.phred64QualityScore = phred64QualityScore;
    }

    public Boolean getSolexaQualityScore() {
        return solexaQualityScore;
    }

    public void setSolexaQualityScore(Boolean solexaQualityScore) {
        this.solexaQualityScore = solexaQualityScore;
    }

    public Boolean getSolexa13QualityScore() {
        return solexa13QualityScore;
    }

    public void setSolexa13QualityScore(Boolean solexa13QualityScore) {
        this.solexa13QualityScore = solexa13QualityScore;
    }

    public Boolean getIntegerQualityScore() {
        return integerQualityScore;
    }

    public void setIntegerQualityScore(Boolean integerQualityScore) {
        this.integerQualityScore = integerQualityScore;
    }

    public Integer getSeedMaximumMismatches() {
        return seedMaximumMismatches;
    }

    public void setSeedMaximumMismatches(Integer seedMaximumMismatches) {
        this.seedMaximumMismatches = seedMaximumMismatches;
    }

    public Integer getMaxSumOfMismatchQuals() {
        return maxSumOfMismatchQuals;
    }

    public void setMaxSumOfMismatchQuals(Integer maxSumOfMismatchQuals) {
        this.maxSumOfMismatchQuals = maxSumOfMismatchQuals;
    }

    public Integer getReportEndToEndHits() {
        return reportEndToEndHits;
    }

    public void setReportEndToEndHits(Integer reportEndToEndHits) {
        this.reportEndToEndHits = reportEndToEndHits;
    }

    public Integer getMinimumInsertSize() {
        return minimumInsertSize;
    }

    public void setMinimumInsertSize(Integer minimumInsertSize) {
        this.minimumInsertSize = minimumInsertSize;
    }

    public Integer getMaximumInsertSize() {
        return maximumInsertSize;
    }

    public void setMaximumInsertSize(Integer maximumInsertSize) {
        this.maximumInsertSize = maximumInsertSize;
    }

    public Integer getReportGoodAlignmentsPerRead() {
        return reportGoodAlignmentsPerRead;
    }

    public void setReportGoodAlignmentsPerRead(Integer reportGoodAlignmentsPerRead) {
        this.reportGoodAlignmentsPerRead = reportGoodAlignmentsPerRead;
    }

    public Integer getSuppressAlignments() {
        return suppressAlignments;
    }

    public void setSuppressAlignments(Integer suppressAlignments) {
        this.suppressAlignments = suppressAlignments;
    }

    public File getQuals() {
        return quals;
    }

    public void setQuals(File quals) {
        this.quals = quals;
    }

    public Integer getSkipNReads() {
        return skipNReads;
    }

    public void setSkipNReads(Integer skipNReads) {
        this.skipNReads = skipNReads;
    }

    public Integer getStopAfterNReads() {
        return stopAfterNReads;
    }

    public void setStopAfterNReads(Integer stopAfterNReads) {
        this.stopAfterNReads = stopAfterNReads;
    }

    public Boolean getDoNotAlignToForwardReferenceStrand() {
        return doNotAlignToForwardReferenceStrand;
    }

    public void setDoNotAlignToForwardReferenceStrand(Boolean doNotAlignToForwardReferenceStrand) {
        this.doNotAlignToForwardReferenceStrand = doNotAlignToForwardReferenceStrand;
    }

    public Boolean getDoNotAlignToReverseComplementReferenceStrand() {
        return doNotAlignToReverseComplementReferenceStrand;
    }

    public void setDoNotAlignToReverseComplementReferenceStrand(Boolean doNotAlignToReverseComplementReferenceStrand) {
        this.doNotAlignToReverseComplementReferenceStrand = doNotAlignToReverseComplementReferenceStrand;
    }

    public Integer getMaxNBacktracks() {
        return maxNBacktracks;
    }

    public void setMaxNBacktracks(Integer maxNBacktracks) {
        this.maxNBacktracks = maxNBacktracks;
    }

    public Integer getMaxAttemptsToFindMate() {
        return maxAttemptsToFindMate;
    }

    public void setMaxAttemptsToFindMate(Integer maxAttemptsToFindMate) {
        this.maxAttemptsToFindMate = maxAttemptsToFindMate;
    }

    public Boolean getTryHardToFindValidAlignments() {
        return tryHardToFindValidAlignments;
    }

    public void setTryHardToFindValidAlignments(Boolean tryHardToFindValidAlignments) {
        this.tryHardToFindValidAlignments = tryHardToFindValidAlignments;
    }

    public Integer getMaxMemoryForBestFirstSearch() {
        return maxMemoryForBestFirstSearch;
    }

    public void setMaxMemoryForBestFirstSearch(Integer maxMemoryForBestFirstSearch) {
        this.maxMemoryForBestFirstSearch = maxMemoryForBestFirstSearch;
    }

    public Boolean getReportAllAlignmentsPerRead() {
        return reportAllAlignmentsPerRead;
    }

    public void setReportAllAlignmentsPerRead(Boolean reportAllAlignmentsPerRead) {
        this.reportAllAlignmentsPerRead = reportAllAlignmentsPerRead;
    }

    public Boolean getPrintWallClockTimeTakenBySearchPhases() {
        return printWallClockTimeTakenBySearchPhases;
    }

    public void setPrintWallClockTimeTakenBySearchPhases(Boolean printWallClockTimeTakenBySearchPhases) {
        this.printWallClockTimeTakenBySearchPhases = printWallClockTimeTakenBySearchPhases;
    }

    public Integer getLeftMostRefOffset() {
        return leftMostRefOffset;
    }

    public void setLeftMostRefOffset(Integer leftMostRefOffset) {
        this.leftMostRefOffset = leftMostRefOffset;
    }

    public Boolean getWriteAlignmentsToMapFilePerReference() {
        return writeAlignmentsToMapFilePerReference;
    }

    public void setWriteAlignmentsToMapFilePerReference(Boolean writeAlignmentsToMapFilePerReference) {
        this.writeAlignmentsToMapFilePerReference = writeAlignmentsToMapFilePerReference;
    }

    public Boolean getUseZeroBasedIndex() {
        return useZeroBasedIndex;
    }

    public void setUseZeroBasedIndex(Boolean useZeroBasedIndex) {
        this.useZeroBasedIndex = useZeroBasedIndex;
    }

    public File getWriteAlignedReads() {
        return writeAlignedReads;
    }

    public void setWriteAlignedReads(File writeAlignedReads) {
        this.writeAlignedReads = writeAlignedReads;
    }

    public File getWriteUnAlignedReads() {
        return writeUnAlignedReads;
    }

    public void setWriteUnAlignedReads(File writeUnAlignedReads) {
        this.writeUnAlignedReads = writeUnAlignedReads;
    }

    public Boolean getWriteEntireRefName() {
        return writeEntireRefName;
    }

    public void setWriteEntireRefName(Boolean writeEntireRefName) {
        this.writeEntireRefName = writeEntireRefName;
    }

    public Integer getMappingQuality() {
        return mappingQuality;
    }

    public void setMappingQuality(Integer mappingQuality) {
        this.mappingQuality = mappingQuality;
    }

    public Boolean getSuppressHeaderLines() {
        return suppressHeaderLines;
    }

    public void setSuppressHeaderLines(Boolean suppressHeaderLines) {
        this.suppressHeaderLines = suppressHeaderLines;
    }

    public Boolean getSuppressSQHeader() {
        return suppressSQHeader;
    }

    public void setSuppressSQHeader(Boolean suppressSQHeader) {
        this.suppressSQHeader = suppressSQHeader;
    }

    public Integer getOffRateOfIndex() {
        return offRateOfIndex;
    }

    public void setOffRateOfIndex(Integer offRateOfIndex) {
        this.offRateOfIndex = offRateOfIndex;
    }

    public static void main(String[] args) {

        Bowtie module = new Bowtie();
        module.setWorkflowName("TEST");
        module.setFastq1(new File("input1.fastq"));
        module.setFastq2(new File("input2.fastq"));
        module.setIndex(new File("index.txt"));
        module.setHits(new File("hits.txt"));

        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
