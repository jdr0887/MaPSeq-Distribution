package edu.unc.mapseq.module.sequencing.bwa;

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

/**
 * 
 * @author jdr0887
 * 
 */
@Application(name = "BWA :: MEM", executable = "$%s_BWA_HOME/bin/bwa mem")
public class BWAMEM extends Module {

    @NotNull(message = "fastq1 is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "fastq1 is empty", groups = InputValidations.class)
    @FileIsReadable(message = "fastq1 is not readable", groups = InputValidations.class)
    @InputArgument(order = 99, delimiter = "")
    private File fastq1;

    @NotNull(message = "fastq2 is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "fastq2 is empty", groups = InputValidations.class)
    @FileIsReadable(message = "fastq2 is not readable", groups = InputValidations.class)
    @InputArgument(order = 100, delimiter = "")
    private File fastq2;

    @NotNull(message = "outFile is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "outFile is empty", groups = OutputValidations.class)
    @OutputArgument(redirect = true, persistFileData = true, mimeType = MimeType.TEXT_SAM)
    private File outFile;

    @NotNull(message = "fastaDB is required", groups = InputValidations.class)
    @FileIsReadable(message = "fastaDB does is not readable", groups = InputValidations.class)
    @InputArgument(order = 50, delimiter = "")
    private File fastaDB;

    @NotNull(message = "Threads to use", groups = InputValidations.class)
    @InputArgument(flag = "-t", order = 1)
    private Integer threads;

    @InputArgument(flag = "-k", order = 2, description = "Minimum seed length")
    private Integer minimumSeedLength;

    @InputArgument(flag = "-w", order = 3, description = "Band width")
    private Integer bandWidth;

    @InputArgument(flag = "-d", order = 4, description = "Off-diagonal X-dropoff")
    private Integer offDiagonalXDropoff;

    @InputArgument(flag = "-r", order = 5, description = "Trigger re-seeding for a MEM longer than minSeedLen*FLOAT")
    private Float reSeeding;

    @InputArgument(flag = "-c", order = 6, description = "Discard a MEM if it has more than INT occurence in the genome.")
    private Integer discardMEM;

    @InputArgument(flag = "-P", order = 7, description = "In the paired-end mode, perform SW to rescue missing hits only but do not try to find hits that fit a proper pair.")
    private Boolean performSW;

    @InputArgument(flag = "-A", order = 8, description = "Matching score.")
    private Integer matchingScore;

    @InputArgument(flag = "-B", order = 9, description = "Mismatch penalty.")
    private Integer mismatchPenalty;

    @InputArgument(flag = "-O", order = 10, description = "Gap open penalty.")
    private Integer gapOpenPenalty;

    @InputArgument(flag = "-E", order = 11, description = "Gap extension penalty.")
    private Integer gapExtensionPenalty;

    @InputArgument(flag = "-L", order = 12, description = "Clipping penalty.")
    private Integer clippingPenalty;

    @InputArgument(flag = "-U", order = 13, description = "Penalty for an unpaired read pair.")
    private Integer unpairedReadPairPenalty;

    @InputArgument(flag = "-p", order = 14, description = "Assume the first input query file is interleaved paired-end FASTA/Q.")
    private Boolean interleavePE;

    @InputArgument(flag = "-R", order = 15, description = "Complete read group header line.")
    private String headerLine;

    @InputArgument(flag = "-T", order = 16, description = "Don’t output alignment with score lower than INT.")
    private Integer lowerAlignmentThreshold;

    @InputArgument(flag = "-a", order = 17, description = "Output all found alignments for single-end or unpaired paired-end reads.")
    private Integer outputAllAlignments;

    @InputArgument(flag = "-C", order = 18, description = "Append append FASTA/Q comment to SAM output.")
    private String appendComment;

    @InputArgument(flag = "-H", order = 19, description = "Use hard clipping ’H’ in the SAM output.")
    private Boolean hardClipping;

    @InputArgument(flag = "-M", order = 20, description = "Mark shorter split hits as secondary")
    private Boolean markShorterSplitHits;

    @InputArgument(flag = "-v", order = 21, description = "Control the verbose level of the output.")
    private Integer verbosity;

    public BWAMEM() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return BWAMEM.class;
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

    public File getOutFile() {
        return outFile;
    }

    public void setOutFile(File outFile) {
        this.outFile = outFile;
    }

    public File getFastaDB() {
        return fastaDB;
    }

    public void setFastaDB(File fastaDB) {
        this.fastaDB = fastaDB;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public Integer getMinimumSeedLength() {
        return minimumSeedLength;
    }

    public void setMinimumSeedLength(Integer minimumSeedLength) {
        this.minimumSeedLength = minimumSeedLength;
    }

    public Integer getBandWidth() {
        return bandWidth;
    }

    public void setBandWidth(Integer bandWidth) {
        this.bandWidth = bandWidth;
    }

    public Integer getOffDiagonalXDropoff() {
        return offDiagonalXDropoff;
    }

    public void setOffDiagonalXDropoff(Integer offDiagonalXDropoff) {
        this.offDiagonalXDropoff = offDiagonalXDropoff;
    }

    public Float getReSeeding() {
        return reSeeding;
    }

    public void setReSeeding(Float reSeeding) {
        this.reSeeding = reSeeding;
    }

    public Integer getDiscardMEM() {
        return discardMEM;
    }

    public void setDiscardMEM(Integer discardMEM) {
        this.discardMEM = discardMEM;
    }

    public Boolean getPerformSW() {
        return performSW;
    }

    public void setPerformSW(Boolean performSW) {
        this.performSW = performSW;
    }

    public Integer getMatchingScore() {
        return matchingScore;
    }

    public void setMatchingScore(Integer matchingScore) {
        this.matchingScore = matchingScore;
    }

    public Integer getMismatchPenalty() {
        return mismatchPenalty;
    }

    public void setMismatchPenalty(Integer mismatchPenalty) {
        this.mismatchPenalty = mismatchPenalty;
    }

    public Integer getGapOpenPenalty() {
        return gapOpenPenalty;
    }

    public void setGapOpenPenalty(Integer gapOpenPenalty) {
        this.gapOpenPenalty = gapOpenPenalty;
    }

    public Integer getGapExtensionPenalty() {
        return gapExtensionPenalty;
    }

    public void setGapExtensionPenalty(Integer gapExtensionPenalty) {
        this.gapExtensionPenalty = gapExtensionPenalty;
    }

    public Integer getClippingPenalty() {
        return clippingPenalty;
    }

    public void setClippingPenalty(Integer clippingPenalty) {
        this.clippingPenalty = clippingPenalty;
    }

    public Integer getUnpairedReadPairPenalty() {
        return unpairedReadPairPenalty;
    }

    public void setUnpairedReadPairPenalty(Integer unpairedReadPairPenalty) {
        this.unpairedReadPairPenalty = unpairedReadPairPenalty;
    }

    public Boolean getInterleavePE() {
        return interleavePE;
    }

    public void setInterleavePE(Boolean interleavePE) {
        this.interleavePE = interleavePE;
    }

    public String getHeaderLine() {
        return headerLine;
    }

    public void setHeaderLine(String headerLine) {
        this.headerLine = headerLine;
    }

    public Integer getLowerAlignmentThreshold() {
        return lowerAlignmentThreshold;
    }

    public void setLowerAlignmentThreshold(Integer lowerAlignmentThreshold) {
        this.lowerAlignmentThreshold = lowerAlignmentThreshold;
    }

    public Integer getOutputAllAlignments() {
        return outputAllAlignments;
    }

    public void setOutputAllAlignments(Integer outputAllAlignments) {
        this.outputAllAlignments = outputAllAlignments;
    }

    public String getAppendComment() {
        return appendComment;
    }

    public void setAppendComment(String appendComment) {
        this.appendComment = appendComment;
    }

    public Boolean getHardClipping() {
        return hardClipping;
    }

    public void setHardClipping(Boolean hardClipping) {
        this.hardClipping = hardClipping;
    }

    public Boolean getMarkShorterSplitHits() {
        return markShorterSplitHits;
    }

    public void setMarkShorterSplitHits(Boolean markShorterSplitHits) {
        this.markShorterSplitHits = markShorterSplitHits;
    }

    public Integer getVerbosity() {
        return verbosity;
    }

    public void setVerbosity(Integer verbosity) {
        this.verbosity = verbosity;
    }

    @Override
    public String toString() {
        return String.format(
                "BWAMEM [fastq1=%s, fastq2=%s, outFile=%s, fastaDB=%s, threads=%s, minimumSeedLength=%s, bandWidth=%s, offDiagonalXDropoff=%s, reSeeding=%s, discardMEM=%s, performSW=%s, matchingScore=%s, mismatchPenalty=%s, gapOpenPenalty=%s, gapExtensionPenalty=%s, clippingPenalty=%s, unpairedReadPairPenalty=%s, interleavePE=%s, headerLine=%s, lowerAlignmentThreshold=%s, outputAllAlignments=%s, appendComment=%s, hardClipping=%s, markShorterSplitHits=%s, verbosity=%s, toString()=%s]",
                fastq1, fastq2, outFile, fastaDB, threads, minimumSeedLength, bandWidth, offDiagonalXDropoff, reSeeding,
                discardMEM, performSW, matchingScore, mismatchPenalty, gapOpenPenalty, gapExtensionPenalty,
                clippingPenalty, unpairedReadPairPenalty, interleavePE, headerLine, lowerAlignmentThreshold,
                outputAllAlignments, appendComment, hardClipping, markShorterSplitHits, verbosity, super.toString());
    }

    public static void main(String[] args) {

        BWAMEM module = new BWAMEM();
        module.setWorkflowName("TEST");
        module.setFastq1(new File("/tmp", "fastq1.gz"));
        module.setFastq2(new File("/tmp", "fastq2.gz"));
        module.setFastaDB(new File("/tmp", "fasta.db"));
        module.setOutFile(new File("/tmp", "out.sam"));
        module.setThreads(4);

        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
