package edu.unc.mapseq.module.sequencing.bowtie2;

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

@Application(name = "Bowtie2", executable = "$%s_BOWTIE2_HOME/bowtie2")
public class Bowtie2 extends Module {

    @NotNull(message = "fastq1 is required", groups = InputValidations.class)
    @FileIsReadable(message = "fastq2 does not exist or is not readable", groups = InputValidations.class)
    @InputArgument(flag = "-1", order = 1, delimiter = "")
    private File fastq1;

    @NotNull(message = "fastq2 is required", groups = InputValidations.class)
    @FileIsReadable(message = "fastq2 does not exist or is not readable", groups = InputValidations.class)
    @InputArgument(flag = "-2", order = 2, delimiter = "")
    private File fastq2;

    @NotNull(message = "index is required", groups = InputValidations.class)
    @InputArgument(flag = "-x", order = 0, delimiter = "")
    private File index;

    @NotNull(message = "sam file is required", groups = InputValidations.class)
    @FileIsReadable(message = "sam file does not exist or is not readable", groups = OutputValidations.class)
    @OutputArgument(flag = "-S", mimeType = MimeType.TEXT_SAM, persistFileData = true, delimiter = "")
    private File sam;

    @InputArgument(flag = "-q")
    private Boolean queryInputFiles;

    @InputArgument(flag = "-s")
    private Integer skipNReads;

    @InputArgument(flag = "-u")
    private Integer stopAfterNReads;

    @InputArgument(flag = "--phred33")
    private Boolean phred33QualityScore;

    @InputArgument(flag = "--phred64")
    private Boolean phred64QualityScore;

    @InputArgument(flag = "--int-quals")
    private Boolean intQualityScore;

    @InputArgument(flag = "--nofw")
    private Boolean doNotAlignToForwardReferenceStrand;

    @InputArgument(flag = "--norc")
    private Boolean doNotAlignToReverseComplementReferenceStrand;

    @InputArgument(flag = "--all")
    private Boolean reportAllAlignmentsPerRead;

    @InputArgument(flag = "-k")
    private Integer reportGoodAlignmentsPerRead = 1;

    @InputArgument(flag = "--threads")
    private Integer threads = 1;

    @InputArgument(flag = "-o")
    private Integer offRateOfIndex;

    @InputArgument(flag = "-I")
    private Integer minimumInsertSize = 0;

    @InputArgument(flag = "-X")
    private Integer maximumInsertSize = 500;

    @InputArgument(flag = "--seed")
    private Integer seed;

    @InputArgument(flag = "--quiet")
    private Boolean quiet;

    @InputArgument(flag = "--no-sq")
    private Boolean suppressSQHeader;

    @InputArgument(flag = "--no-head")
    private Boolean suppressHeaderLines;

    @InputArgument(flag = "--time")
    private Boolean printWallClockTimeTakenBySearchPhases;

    @InputArgument(flag = "--al")
    private File writeAlignedReads;

    @InputArgument(flag = "--un")
    private File writeUnAlignedReads;

    @InputArgument(flag = "--reorder")
    private Boolean matchReadOrder;

    @InputArgument(flag = "--qseq")
    private Boolean qSeqFormat;

    @InputArgument(flag = "-N")
    private Integer maxNMismatches;

    @InputArgument(flag = "-L")
    private Integer lengthOfSeedSubstrings;

    @InputArgument(flag = "--ignore-quals")
    private Boolean ignoreQuals;

    @InputArgument(flag = "--very-fast")
    private Boolean veryFast;

    @InputArgument(flag = "--fast")
    private Boolean fast;

    @InputArgument(flag = "--sensitive")
    private Boolean sensitive;

    @InputArgument(flag = "--very-sensitive")
    private Boolean verySensitive;

    @InputArgument(flag = "--ma")
    private Integer matchBonus;

    @InputArgument(flag = "--mp")
    private Integer maxPenalty;

    @InputArgument(flag = "--np")
    private Integer penaltyForNonDNARead;

    @InputArgument(flag = "-D")
    private Integer giveUpExtending;

    @InputArgument(flag = "-R")
    private Integer repetitiveSeeds;

    @InputArgument(flag = "--no-mixed")
    private Boolean noMixed;

    @InputArgument(flag = "--no-discordant")
    private Boolean noDiscordant;

    @InputArgument(flag = "--no-dovetail")
    private Boolean noDovetail;

    @InputArgument(flag = "--no-contain")
    private Boolean noContain;

    @InputArgument(flag = "--no-overlap")
    private Boolean noOverlap;

    @InputArgument(flag = "--qc-filter")
    private Boolean qcFilter;

    @InputArgument(flag = "--non-deterministic")
    private Boolean nonDeterministic;

    @Override
    public Class<?> getModuleClass() {
        return Bowtie2.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(),
                getWorkflowName().toUpperCase());
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

    public File getSam() {
        return sam;
    }

    public void setSam(File sam) {
        this.sam = sam;
    }

    public Boolean getQueryInputFiles() {
        return queryInputFiles;
    }

    public void setQueryInputFiles(Boolean queryInputFiles) {
        this.queryInputFiles = queryInputFiles;
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

    public Boolean getIntQualityScore() {
        return intQualityScore;
    }

    public void setIntQualityScore(Boolean intQualityScore) {
        this.intQualityScore = intQualityScore;
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

    public Boolean getReportAllAlignmentsPerRead() {
        return reportAllAlignmentsPerRead;
    }

    public void setReportAllAlignmentsPerRead(Boolean reportAllAlignmentsPerRead) {
        this.reportAllAlignmentsPerRead = reportAllAlignmentsPerRead;
    }

    public Integer getReportGoodAlignmentsPerRead() {
        return reportGoodAlignmentsPerRead;
    }

    public void setReportGoodAlignmentsPerRead(Integer reportGoodAlignmentsPerRead) {
        this.reportGoodAlignmentsPerRead = reportGoodAlignmentsPerRead;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public Integer getOffRateOfIndex() {
        return offRateOfIndex;
    }

    public void setOffRateOfIndex(Integer offRateOfIndex) {
        this.offRateOfIndex = offRateOfIndex;
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

    public Integer getSeed() {
        return seed;
    }

    public void setSeed(Integer seed) {
        this.seed = seed;
    }

    public Boolean getQuiet() {
        return quiet;
    }

    public void setQuiet(Boolean quiet) {
        this.quiet = quiet;
    }

    public Boolean getSuppressSQHeader() {
        return suppressSQHeader;
    }

    public void setSuppressSQHeader(Boolean suppressSQHeader) {
        this.suppressSQHeader = suppressSQHeader;
    }

    public Boolean getSuppressHeaderLines() {
        return suppressHeaderLines;
    }

    public void setSuppressHeaderLines(Boolean suppressHeaderLines) {
        this.suppressHeaderLines = suppressHeaderLines;
    }

    public Boolean getPrintWallClockTimeTakenBySearchPhases() {
        return printWallClockTimeTakenBySearchPhases;
    }

    public void setPrintWallClockTimeTakenBySearchPhases(Boolean printWallClockTimeTakenBySearchPhases) {
        this.printWallClockTimeTakenBySearchPhases = printWallClockTimeTakenBySearchPhases;
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

    public Boolean getMatchReadOrder() {
        return matchReadOrder;
    }

    public void setMatchReadOrder(Boolean matchReadOrder) {
        this.matchReadOrder = matchReadOrder;
    }

    public Boolean getQSeqFormat() {
        return qSeqFormat;
    }

    public void setQSeqFormat(Boolean qSeqFormat) {
        this.qSeqFormat = qSeqFormat;
    }

    public Integer getMaxNMismatches() {
        return maxNMismatches;
    }

    public void setMaxNMismatches(Integer maxNMismatches) {
        this.maxNMismatches = maxNMismatches;
    }

    public Integer getLengthOfSeedSubstrings() {
        return lengthOfSeedSubstrings;
    }

    public void setLengthOfSeedSubstrings(Integer lengthOfSeedSubstrings) {
        this.lengthOfSeedSubstrings = lengthOfSeedSubstrings;
    }

    public Boolean getIgnoreQuals() {
        return ignoreQuals;
    }

    public void setIgnoreQuals(Boolean ignoreQuals) {
        this.ignoreQuals = ignoreQuals;
    }

    public Boolean getVeryFast() {
        return veryFast;
    }

    public void setVeryFast(Boolean veryFast) {
        this.veryFast = veryFast;
    }

    public Boolean getFast() {
        return fast;
    }

    public void setFast(Boolean fast) {
        this.fast = fast;
    }

    public Boolean getSensitive() {
        return sensitive;
    }

    public void setSensitive(Boolean sensitive) {
        this.sensitive = sensitive;
    }

    public Boolean getVerySensitive() {
        return verySensitive;
    }

    public void setVerySensitive(Boolean verySensitive) {
        this.verySensitive = verySensitive;
    }

    public Integer getMatchBonus() {
        return matchBonus;
    }

    public void setMatchBonus(Integer matchBonus) {
        this.matchBonus = matchBonus;
    }

    public Integer getMaxPenalty() {
        return maxPenalty;
    }

    public void setMaxPenalty(Integer maxPenalty) {
        this.maxPenalty = maxPenalty;
    }

    public Integer getPenaltyForNonDNARead() {
        return penaltyForNonDNARead;
    }

    public void setPenaltyForNonDNARead(Integer penaltyForNonDNARead) {
        this.penaltyForNonDNARead = penaltyForNonDNARead;
    }

    public Integer getGiveUpExtending() {
        return giveUpExtending;
    }

    public void setGiveUpExtending(Integer giveUpExtending) {
        this.giveUpExtending = giveUpExtending;
    }

    public Integer getRepetitiveSeeds() {
        return repetitiveSeeds;
    }

    public void setRepetitiveSeeds(Integer repetitiveSeeds) {
        this.repetitiveSeeds = repetitiveSeeds;
    }

    public Boolean getNoMixed() {
        return noMixed;
    }

    public void setNoMixed(Boolean noMixed) {
        this.noMixed = noMixed;
    }

    public Boolean getNoDiscordant() {
        return noDiscordant;
    }

    public void setNoDiscordant(Boolean noDiscordant) {
        this.noDiscordant = noDiscordant;
    }

    public Boolean getNoDovetail() {
        return noDovetail;
    }

    public void setNoDovetail(Boolean noDovetail) {
        this.noDovetail = noDovetail;
    }

    public Boolean getNoContain() {
        return noContain;
    }

    public void setNoContain(Boolean noContain) {
        this.noContain = noContain;
    }

    public Boolean getNoOverlap() {
        return noOverlap;
    }

    public void setNoOverlap(Boolean noOverlap) {
        this.noOverlap = noOverlap;
    }

    public Boolean getQcFilter() {
        return qcFilter;
    }

    public void setQcFilter(Boolean qcFilter) {
        this.qcFilter = qcFilter;
    }

    public Boolean getNonDeterministic() {
        return nonDeterministic;
    }

    public void setNonDeterministic(Boolean nonDeterministic) {
        this.nonDeterministic = nonDeterministic;
    }

    @Override
    public String toString() {
        return String.format(
                "Bowtie2 [fastq1=%s, fastq2=%s, index=%s, sam=%s, queryInputFiles=%s, skipNReads=%s, stopAfterNReads=%s, phred33QualityScore=%s, phred64QualityScore=%s, intQualityScore=%s, doNotAlignToForwardReferenceStrand=%s, doNotAlignToReverseComplementReferenceStrand=%s, reportAllAlignmentsPerRead=%s, reportGoodAlignmentsPerRead=%s, threads=%s, offRateOfIndex=%s, minimumInsertSize=%s, maximumInsertSize=%s, seed=%s, quiet=%s, suppressSQHeader=%s, suppressHeaderLines=%s, printWallClockTimeTakenBySearchPhases=%s, writeAlignedReads=%s, writeUnAlignedReads=%s, matchReadOrder=%s, qSeqFormat=%s, maxNMismatches=%s, lengthOfSeedSubstrings=%s, ignoreQuals=%s, veryFast=%s, fast=%s, sensitive=%s, verySensitive=%s, matchBonus=%s, maxPenalty=%s, penaltyForNonDNARead=%s, giveUpExtending=%s, repetitiveSeeds=%s, noMixed=%s, noDiscordant=%s, noDovetail=%s, noContain=%s, noOverlap=%s, qcFilter=%s, nonDeterministic=%s, toString()=%s]",
                fastq1, fastq2, index, sam, queryInputFiles, skipNReads, stopAfterNReads, phred33QualityScore,
                phred64QualityScore, intQualityScore, doNotAlignToForwardReferenceStrand,
                doNotAlignToReverseComplementReferenceStrand, reportAllAlignmentsPerRead, reportGoodAlignmentsPerRead,
                threads, offRateOfIndex, minimumInsertSize, maximumInsertSize, seed, quiet, suppressSQHeader,
                suppressHeaderLines, printWallClockTimeTakenBySearchPhases, writeAlignedReads, writeUnAlignedReads,
                matchReadOrder, qSeqFormat, maxNMismatches, lengthOfSeedSubstrings, ignoreQuals, veryFast, fast,
                sensitive, verySensitive, matchBonus, maxPenalty, penaltyForNonDNARead, giveUpExtending,
                repetitiveSeeds, noMixed, noDiscordant, noDovetail, noContain, noOverlap, qcFilter, nonDeterministic,
                super.toString());
    }

    public static void main(String[] args) {

        Bowtie2 module = new Bowtie2();
        module.setWorkflowName("TEST");
        module.setFastq1(new File("input1.fastq"));
        module.setFastq2(new File("input2.fastq"));
        // module.setIndex(new File("index.txt"));
        module.setSam(new File("output.sam"));

        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
