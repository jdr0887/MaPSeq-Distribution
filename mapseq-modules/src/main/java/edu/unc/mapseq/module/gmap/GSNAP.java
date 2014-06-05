package edu.unc.mapseq.module.gmap;

import java.io.File;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.Executable;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "GSNAP")
@Executable(value = "$%s_GMAP_GSNAP_HOME/bin/gsnap")
public class GSNAP extends Module {

    @NotNull(message = "fastq is required", groups = InputValidations.class)
    @FileIsReadable(message = "fastq does not exist or is not readable", groups = InputValidations.class)
    @InputArgument(flag = "", delimiter = "", order = 100)
    private File fastq;

    @NotNull(message = "output is required", groups = InputValidations.class)
    @FileIsReadable(message = "output does not exist or is not readable", groups = OutputValidations.class)
    @OutputArgument(redirect = true)
    private File output;

    @InputArgument(flag = "--dir", delimiter = "=")
    private File genomeDirectory;

    @NotNull(message = "genomeDatabase is required", groups = InputValidations.class)
    @InputArgument(flag = "--db", delimiter = "=")
    private String genomeDatabase;

    @InputArgument(flag = "--kmer", delimiter = "=")
    private Integer kmerSize;

    @InputArgument(flag = "--basesize", delimiter = "=")
    private Integer baseSize;

    @InputArgument(flag = "--sampling", delimiter = "=")
    private Integer sampling;

    @InputArgument(flag = "--part", delimiter = "=")
    private String part;

    @InputArgument(flag = "--input-buffer-size", delimiter = "=")
    private Integer inputBufferSize;

    @InputArgument(flag = "--barcode-length", delimiter = "=")
    private Integer barcodeLength;

    @InputArgument(flag = "--orientation", delimiter = "=")
    private String orientation;

    @InputArgument(flag = "--fastq-id-start", delimiter = "=")
    private Integer fastqIdStart;

    @InputArgument(flag = "--fastq-id-end", delimiter = "=")
    private Integer fastqIdEnd;

    @InputArgument(flag = "--filter-chastity", delimiter = "=")
    private String filterChastity;

    @InputArgument(flag = "--gunzip", delimiter = "=")
    private Boolean gunzip;

    @InputArgument(flag = "--batch", delimiter = "=")
    private Integer batch;

    @InputArgument(flag = "--max-mismatches", delimiter = "=")
    private Float maxMismatches;

    @InputArgument(flag = "--query-unk-mismatch", delimiter = "=")
    private Integer queryUnkMismatch;

    @InputArgument(flag = "--genome-unk-mismatch", delimiter = "=")
    private Integer genomeUnkMismatch;

    @InputArgument(flag = "--terminal-threshold", delimiter = "=")
    private Integer terminalThreshold;

    @InputArgument(flag = "--indel-penalty", delimiter = "=")
    private Integer indelPenalty;

    @InputArgument(flag = "--indel-endlength", delimiter = "=")
    private Integer indelEndLength;

    @InputArgument(flag = "--max-middle-insertions", delimiter = "=")
    private Integer maxMiddleInsertions;

    @InputArgument(flag = "--max-middle-deletions", delimiter = "=")
    private Integer maxMiddleDeletions;

    @InputArgument(flag = "--max-end-insertions", delimiter = "=")
    private Integer maxEndInsertions;

    @InputArgument(flag = "--max-end-deletions", delimiter = "=")
    private Integer maxEndDeletions;

    @InputArgument(flag = "--suboptimal-levels", delimiter = "=")
    private Integer subOptimalLevels;

    @InputArgument(flag = "--adapter-strip", delimiter = "=")
    private String adapterStrip;

    @InputArgument(flag = "--trim-mismatch-score", delimiter = "=")
    private Integer trimMismatchScore;

    @InputArgument(flag = "--trim-indel-score", delimiter = "=")
    private Integer trimIndelScore;

    @InputArgument(flag = "--snpsdir", delimiter = "=")
    private String snpsDirectory;

    @InputArgument(flag = "--use-snps", delimiter = "=")
    private String useSnps;

    @InputArgument(flag = "--cmetdir", delimiter = "=")
    private String cmetDirectory;

    @InputArgument(flag = "--atoidir", delimiter = "=")
    private String atoiDirectory;

    @InputArgument(flag = "--mode", delimiter = "=")
    private String mode;

    @InputArgument(flag = "--tallydir", delimiter = "=")
    private String tallyDirectory;

    @InputArgument(flag = "--use-tally", delimiter = "=")
    private String useTally;

    @InputArgument(flag = "--runlengthdir", delimiter = "=")
    private String runLengthDirectory;

    @InputArgument(flag = "--use-runlength", delimiter = "=")
    private String useRunLength;

    @InputArgument(flag = "--nthreads", delimiter = "=")
    private Integer threads;

    @InputArgument(flag = "--gmap-mode", delimiter = "=")
    private String gmapMode;

    @InputArgument(flag = "--trigger-score-for-gmap", delimiter = "=")
    private Integer triggerScoreForGmap;

    @InputArgument(flag = "--gmap-min-coverage", delimiter = "=")
    private Float gmapMinCoverage;

    @InputArgument(flag = "--max-gmap-pairsearch", delimiter = "=")
    private Integer maxGmapPairSearch;

    @InputArgument(flag = "--max-gmap-terminal", delimiter = "=")
    private Integer maxGmapTerminal;

    @InputArgument(flag = "--max-gmap-improvement", delimiter = "=")
    private Integer maxGmapImprovement;

    @InputArgument(flag = "--microexon-spliceprob", delimiter = "=")
    private Float microExonSpliceProbability;

    @InputArgument(flag = "--novelsplicing", delimiter = "=")
    private Integer novelSplicing;

    @InputArgument(flag = "--splicingdir", delimiter = "=")
    private String splicingDirectory;

    @InputArgument(flag = "--use-splicing", delimiter = "=")
    private String useSplicing;

    @InputArgument(flag = "--ambig-splice-noclip", delimiter = "=")
    private Boolean ambigSpliceNoClip;

    @InputArgument(flag = "--localsplicedist", delimiter = "=")
    private Integer localSpliceDistance;

    @InputArgument(flag = "--local-splice-penalty", delimiter = "=")
    private Integer localSplicePenalty;

    @InputArgument(flag = "--distant-splice-penalty", delimiter = "=")
    private Integer distantSplicePenalty;

    @InputArgument(flag = "--distant-splice-endlength", delimiter = "=")
    private Integer distantSpliceEndLength;

    @InputArgument(flag = "--shortend-splice-endlength", delimiter = "=")
    private Integer shortendSpliceEndLength;

    @InputArgument(flag = "--distant-splice-identity", delimiter = "=")
    private Float distantSpliceIdentity;

    @InputArgument(flag = "--antistranded-penalty", delimiter = "=")
    private Integer antiStrandedPenalty;

    @InputArgument(flag = "--merge-distant-samechr", delimiter = "=")
    private Boolean mergeDistantSameChromosome;

    @InputArgument(flag = "--pairmax-dna", delimiter = "=")
    private Integer pairMaxDNA;

    @InputArgument(flag = "--pairmax-rna", delimiter = "=")
    private Integer pairMaxRNA;

    @InputArgument(flag = "--pairexpect", delimiter = "=")
    private Integer pairExpect;

    @InputArgument(flag = "--pairdev", delimiter = "=")
    private Integer pairDeviation;

    @InputArgument(flag = "--quality-protocol", delimiter = "=")
    private String qualityProtocol;

    @InputArgument(flag = "--quality-zero-score", delimiter = "=")
    private Integer qualityZeroScore;

    @InputArgument(flag = "--quality-print-shift", delimiter = "=")
    private Integer qualityPrintShift;

    @InputArgument(flag = "--npaths", delimiter = "=")
    private Integer numberOfPaths;

    @InputArgument(flag = "--quiet-if-excessive", delimiter = "=")
    private Boolean quietIfExcessive;

    @InputArgument(flag = "--ordered", delimiter = "=")
    private Boolean ordered;

    @InputArgument(flag = "--show-refdiff", delimiter = "=")
    private Boolean showReferenceDifferences;

    @InputArgument(flag = "--clip-overlap", delimiter = "=")
    private Boolean clipOverlap;

    @InputArgument(flag = "--print-snps", delimiter = "=")
    private Boolean printSNPS;

    @InputArgument(flag = "--failsonly", delimiter = "=")
    private Boolean failsOnly;

    @InputArgument(flag = "--nofails", delimiter = "=")
    private Boolean noFails;

    @InputArgument(flag = "--fails-as-input", delimiter = "=")
    private Boolean failsAsInput;

    @InputArgument(flag = "--format", delimiter = "=")
    private String format;

    @InputArgument(flag = "--split-output", delimiter = "=")
    private String splitOutput;

    @InputArgument(flag = "--output-buffer-size", delimiter = "=")
    private Integer outputBufferSize;

    @InputArgument(flag = "--no-sam-headers", delimiter = "=")
    private Boolean noSAMHeaders;

    @InputArgument(flag = "--sam-headers-batch", delimiter = "=")
    private Integer SAMHeadersBatch;

    @InputArgument(flag = "--sam-use-0M", delimiter = "=")
    private Boolean SAMUseOM;

    @InputArgument(flag = "--sam-multiple-primaries", delimiter = "=")
    private Boolean SAMMultiplePrimaries;

    @InputArgument(flag = "--read-group-id", delimiter = "=")
    private String readGroupId;

    @InputArgument(flag = "--read-group-name", delimiter = "=")
    private String readGroupName;

    @InputArgument(flag = "--read-group-library", delimiter = "=")
    private String readGroupLibrary;

    @InputArgument(flag = "--read-group-platform", delimiter = "=")
    private String readGroupPlatform;

    @Override
    public Class<?> getModuleClass() {
        return GSNAP.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Executable.class).value(), getWorkflowName().toUpperCase());
    }

    public File getFastq() {
        return fastq;
    }

    public void setFastq(File fastq) {
        this.fastq = fastq;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public File getGenomeDirectory() {
        return genomeDirectory;
    }

    public void setGenomeDirectory(File genomeDirectory) {
        this.genomeDirectory = genomeDirectory;
    }

    public String getGenomeDatabase() {
        return genomeDatabase;
    }

    public void setGenomeDatabase(String genomeDatabase) {
        this.genomeDatabase = genomeDatabase;
    }

    public Integer getKmerSize() {
        return kmerSize;
    }

    public void setKmerSize(Integer kmerSize) {
        this.kmerSize = kmerSize;
    }

    public Integer getBaseSize() {
        return baseSize;
    }

    public void setBaseSize(Integer baseSize) {
        this.baseSize = baseSize;
    }

    public Integer getSampling() {
        return sampling;
    }

    public void setSampling(Integer sampling) {
        this.sampling = sampling;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public Integer getInputBufferSize() {
        return inputBufferSize;
    }

    public void setInputBufferSize(Integer inputBufferSize) {
        this.inputBufferSize = inputBufferSize;
    }

    public Integer getBarcodeLength() {
        return barcodeLength;
    }

    public void setBarcodeLength(Integer barcodeLength) {
        this.barcodeLength = barcodeLength;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public Integer getFastqIdStart() {
        return fastqIdStart;
    }

    public void setFastqIdStart(Integer fastqIdStart) {
        this.fastqIdStart = fastqIdStart;
    }

    public Integer getFastqIdEnd() {
        return fastqIdEnd;
    }

    public void setFastqIdEnd(Integer fastqIdEnd) {
        this.fastqIdEnd = fastqIdEnd;
    }

    public String getFilterChastity() {
        return filterChastity;
    }

    public void setFilterChastity(String filterChastity) {
        this.filterChastity = filterChastity;
    }

    public Boolean getGunzip() {
        return gunzip;
    }

    public void setGunzip(Boolean gunzip) {
        this.gunzip = gunzip;
    }

    public Integer getBatch() {
        return batch;
    }

    public void setBatch(Integer batch) {
        this.batch = batch;
    }

    public Float getMaxMismatches() {
        return maxMismatches;
    }

    public void setMaxMismatches(Float maxMismatches) {
        this.maxMismatches = maxMismatches;
    }

    public Integer getQueryUnkMismatch() {
        return queryUnkMismatch;
    }

    public void setQueryUnkMismatch(Integer queryUnkMismatch) {
        this.queryUnkMismatch = queryUnkMismatch;
    }

    public Integer getGenomeUnkMismatch() {
        return genomeUnkMismatch;
    }

    public void setGenomeUnkMismatch(Integer genomeUnkMismatch) {
        this.genomeUnkMismatch = genomeUnkMismatch;
    }

    public Integer getTerminalThreshold() {
        return terminalThreshold;
    }

    public void setTerminalThreshold(Integer terminalThreshold) {
        this.terminalThreshold = terminalThreshold;
    }

    public Integer getIndelPenalty() {
        return indelPenalty;
    }

    public void setIndelPenalty(Integer indelPenalty) {
        this.indelPenalty = indelPenalty;
    }

    public Integer getIndelEndLength() {
        return indelEndLength;
    }

    public void setIndelEndLength(Integer indelEndLength) {
        this.indelEndLength = indelEndLength;
    }

    public Integer getMaxMiddleInsertions() {
        return maxMiddleInsertions;
    }

    public void setMaxMiddleInsertions(Integer maxMiddleInsertions) {
        this.maxMiddleInsertions = maxMiddleInsertions;
    }

    public Integer getMaxMiddleDeletions() {
        return maxMiddleDeletions;
    }

    public void setMaxMiddleDeletions(Integer maxMiddleDeletions) {
        this.maxMiddleDeletions = maxMiddleDeletions;
    }

    public Integer getMaxEndInsertions() {
        return maxEndInsertions;
    }

    public void setMaxEndInsertions(Integer maxEndInsertions) {
        this.maxEndInsertions = maxEndInsertions;
    }

    public Integer getMaxEndDeletions() {
        return maxEndDeletions;
    }

    public void setMaxEndDeletions(Integer maxEndDeletions) {
        this.maxEndDeletions = maxEndDeletions;
    }

    public Integer getSubOptimalLevels() {
        return subOptimalLevels;
    }

    public void setSubOptimalLevels(Integer subOptimalLevels) {
        this.subOptimalLevels = subOptimalLevels;
    }

    public String getAdapterStrip() {
        return adapterStrip;
    }

    public void setAdapterStrip(String adapterStrip) {
        this.adapterStrip = adapterStrip;
    }

    public Integer getTrimMismatchScore() {
        return trimMismatchScore;
    }

    public void setTrimMismatchScore(Integer trimMismatchScore) {
        this.trimMismatchScore = trimMismatchScore;
    }

    public Integer getTrimIndelScore() {
        return trimIndelScore;
    }

    public void setTrimIndelScore(Integer trimIndelScore) {
        this.trimIndelScore = trimIndelScore;
    }

    public String getSnpsDirectory() {
        return snpsDirectory;
    }

    public void setSnpsDirectory(String snpsDirectory) {
        this.snpsDirectory = snpsDirectory;
    }

    public String getUseSnps() {
        return useSnps;
    }

    public void setUseSnps(String useSnps) {
        this.useSnps = useSnps;
    }

    public String getCmetDirectory() {
        return cmetDirectory;
    }

    public void setCmetDirectory(String cmetDirectory) {
        this.cmetDirectory = cmetDirectory;
    }

    public String getAtoiDirectory() {
        return atoiDirectory;
    }

    public void setAtoiDirectory(String atoiDirectory) {
        this.atoiDirectory = atoiDirectory;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getTallyDirectory() {
        return tallyDirectory;
    }

    public void setTallyDirectory(String tallyDirectory) {
        this.tallyDirectory = tallyDirectory;
    }

    public String getUseTally() {
        return useTally;
    }

    public void setUseTally(String useTally) {
        this.useTally = useTally;
    }

    public String getRunLengthDirectory() {
        return runLengthDirectory;
    }

    public void setRunLengthDirectory(String runLengthDirectory) {
        this.runLengthDirectory = runLengthDirectory;
    }

    public String getUseRunLength() {
        return useRunLength;
    }

    public void setUseRunLength(String useRunLength) {
        this.useRunLength = useRunLength;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public String getGmapMode() {
        return gmapMode;
    }

    public void setGmapMode(String gmapMode) {
        this.gmapMode = gmapMode;
    }

    public Integer getTriggerScoreForGmap() {
        return triggerScoreForGmap;
    }

    public void setTriggerScoreForGmap(Integer triggerScoreForGmap) {
        this.triggerScoreForGmap = triggerScoreForGmap;
    }

    public Float getGmapMinCoverage() {
        return gmapMinCoverage;
    }

    public void setGmapMinCoverage(Float gmapMinCoverage) {
        this.gmapMinCoverage = gmapMinCoverage;
    }

    public Integer getMaxGmapPairSearch() {
        return maxGmapPairSearch;
    }

    public void setMaxGmapPairSearch(Integer maxGmapPairSearch) {
        this.maxGmapPairSearch = maxGmapPairSearch;
    }

    public Integer getMaxGmapTerminal() {
        return maxGmapTerminal;
    }

    public void setMaxGmapTerminal(Integer maxGmapTerminal) {
        this.maxGmapTerminal = maxGmapTerminal;
    }

    public Integer getMaxGmapImprovement() {
        return maxGmapImprovement;
    }

    public void setMaxGmapImprovement(Integer maxGmapImprovement) {
        this.maxGmapImprovement = maxGmapImprovement;
    }

    public Float getMicroExonSpliceProbability() {
        return microExonSpliceProbability;
    }

    public void setMicroExonSpliceProbability(Float microExonSpliceProbability) {
        this.microExonSpliceProbability = microExonSpliceProbability;
    }

    public Integer getNovelSplicing() {
        return novelSplicing;
    }

    public void setNovelSplicing(Integer novelSplicing) {
        this.novelSplicing = novelSplicing;
    }

    public String getSplicingDirectory() {
        return splicingDirectory;
    }

    public void setSplicingDirectory(String splicingDirectory) {
        this.splicingDirectory = splicingDirectory;
    }

    public String getUseSplicing() {
        return useSplicing;
    }

    public void setUseSplicing(String useSplicing) {
        this.useSplicing = useSplicing;
    }

    public Boolean getAmbigSpliceNoClip() {
        return ambigSpliceNoClip;
    }

    public void setAmbigSpliceNoClip(Boolean ambigSpliceNoClip) {
        this.ambigSpliceNoClip = ambigSpliceNoClip;
    }

    public Integer getLocalSpliceDistance() {
        return localSpliceDistance;
    }

    public void setLocalSpliceDistance(Integer localSpliceDistance) {
        this.localSpliceDistance = localSpliceDistance;
    }

    public Integer getLocalSplicePenalty() {
        return localSplicePenalty;
    }

    public void setLocalSplicePenalty(Integer localSplicePenalty) {
        this.localSplicePenalty = localSplicePenalty;
    }

    public Integer getDistantSplicePenalty() {
        return distantSplicePenalty;
    }

    public void setDistantSplicePenalty(Integer distantSplicePenalty) {
        this.distantSplicePenalty = distantSplicePenalty;
    }

    public Integer getDistantSpliceEndLength() {
        return distantSpliceEndLength;
    }

    public void setDistantSpliceEndLength(Integer distantSpliceEndLength) {
        this.distantSpliceEndLength = distantSpliceEndLength;
    }

    public Integer getShortendSpliceEndLength() {
        return shortendSpliceEndLength;
    }

    public void setShortendSpliceEndLength(Integer shortendSpliceEndLength) {
        this.shortendSpliceEndLength = shortendSpliceEndLength;
    }

    public Float getDistantSpliceIdentity() {
        return distantSpliceIdentity;
    }

    public void setDistantSpliceIdentity(Float distantSpliceIdentity) {
        this.distantSpliceIdentity = distantSpliceIdentity;
    }

    public Integer getAntiStrandedPenalty() {
        return antiStrandedPenalty;
    }

    public void setAntiStrandedPenalty(Integer antiStrandedPenalty) {
        this.antiStrandedPenalty = antiStrandedPenalty;
    }

    public Boolean getMergeDistantSameChromosome() {
        return mergeDistantSameChromosome;
    }

    public void setMergeDistantSameChromosome(Boolean mergeDistantSameChromosome) {
        this.mergeDistantSameChromosome = mergeDistantSameChromosome;
    }

    public Integer getPairMaxDNA() {
        return pairMaxDNA;
    }

    public void setPairMaxDNA(Integer pairMaxDNA) {
        this.pairMaxDNA = pairMaxDNA;
    }

    public Integer getPairMaxRNA() {
        return pairMaxRNA;
    }

    public void setPairMaxRNA(Integer pairMaxRNA) {
        this.pairMaxRNA = pairMaxRNA;
    }

    public Integer getPairExpect() {
        return pairExpect;
    }

    public void setPairExpect(Integer pairExpect) {
        this.pairExpect = pairExpect;
    }

    public Integer getPairDeviation() {
        return pairDeviation;
    }

    public void setPairDeviation(Integer pairDeviation) {
        this.pairDeviation = pairDeviation;
    }

    public String getQualityProtocol() {
        return qualityProtocol;
    }

    public void setQualityProtocol(String qualityProtocol) {
        this.qualityProtocol = qualityProtocol;
    }

    public Integer getQualityZeroScore() {
        return qualityZeroScore;
    }

    public void setQualityZeroScore(Integer qualityZeroScore) {
        this.qualityZeroScore = qualityZeroScore;
    }

    public Integer getQualityPrintShift() {
        return qualityPrintShift;
    }

    public void setQualityPrintShift(Integer qualityPrintShift) {
        this.qualityPrintShift = qualityPrintShift;
    }

    public Integer getNumberOfPaths() {
        return numberOfPaths;
    }

    public void setNumberOfPaths(Integer numberOfPaths) {
        this.numberOfPaths = numberOfPaths;
    }

    public Boolean getQuietIfExcessive() {
        return quietIfExcessive;
    }

    public void setQuietIfExcessive(Boolean quietIfExcessive) {
        this.quietIfExcessive = quietIfExcessive;
    }

    public Boolean getOrdered() {
        return ordered;
    }

    public void setOrdered(Boolean ordered) {
        this.ordered = ordered;
    }

    public Boolean getShowReferenceDifferences() {
        return showReferenceDifferences;
    }

    public void setShowReferenceDifferences(Boolean showReferenceDifferences) {
        this.showReferenceDifferences = showReferenceDifferences;
    }

    public Boolean getClipOverlap() {
        return clipOverlap;
    }

    public void setClipOverlap(Boolean clipOverlap) {
        this.clipOverlap = clipOverlap;
    }

    public Boolean getPrintSNPS() {
        return printSNPS;
    }

    public void setPrintSNPS(Boolean printSNPS) {
        this.printSNPS = printSNPS;
    }

    public Boolean getFailsOnly() {
        return failsOnly;
    }

    public void setFailsOnly(Boolean failsOnly) {
        this.failsOnly = failsOnly;
    }

    public Boolean getNoFails() {
        return noFails;
    }

    public void setNoFails(Boolean noFails) {
        this.noFails = noFails;
    }

    public Boolean getFailsAsInput() {
        return failsAsInput;
    }

    public void setFailsAsInput(Boolean failsAsInput) {
        this.failsAsInput = failsAsInput;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getSplitOutput() {
        return splitOutput;
    }

    public void setSplitOutput(String splitOutput) {
        this.splitOutput = splitOutput;
    }

    public Integer getOutputBufferSize() {
        return outputBufferSize;
    }

    public void setOutputBufferSize(Integer outputBufferSize) {
        this.outputBufferSize = outputBufferSize;
    }

    public Boolean getNoSAMHeaders() {
        return noSAMHeaders;
    }

    public void setNoSAMHeaders(Boolean noSAMHeaders) {
        this.noSAMHeaders = noSAMHeaders;
    }

    public Integer getSAMHeadersBatch() {
        return SAMHeadersBatch;
    }

    public void setSAMHeadersBatch(Integer sAMHeadersBatch) {
        SAMHeadersBatch = sAMHeadersBatch;
    }

    public Boolean getSAMUseOM() {
        return SAMUseOM;
    }

    public void setSAMUseOM(Boolean sAMUseOM) {
        SAMUseOM = sAMUseOM;
    }

    public Boolean getSAMMultiplePrimaries() {
        return SAMMultiplePrimaries;
    }

    public void setSAMMultiplePrimaries(Boolean sAMMultiplePrimaries) {
        SAMMultiplePrimaries = sAMMultiplePrimaries;
    }

    public String getReadGroupId() {
        return readGroupId;
    }

    public void setReadGroupId(String readGroupId) {
        this.readGroupId = readGroupId;
    }

    public String getReadGroupName() {
        return readGroupName;
    }

    public void setReadGroupName(String readGroupName) {
        this.readGroupName = readGroupName;
    }

    public String getReadGroupLibrary() {
        return readGroupLibrary;
    }

    public void setReadGroupLibrary(String readGroupLibrary) {
        this.readGroupLibrary = readGroupLibrary;
    }

    public String getReadGroupPlatform() {
        return readGroupPlatform;
    }

    public void setReadGroupPlatform(String readGroupPlatform) {
        this.readGroupPlatform = readGroupPlatform;
    }

    public static void main(String[] args) {

        GSNAP module = new GSNAP();
        module.setWorkflowName("TEST");
        module.setGenomeDatabase("hg19");
        module.setUseSnps("chrAll.snps.05.gmap");
        module.setThreads(4);
        module.setTrimMismatchScore(0);
        module.setBatch(2);
        module.setGmapMode("none");
        module.setTerminalThreshold(10);
        module.setMaxMismatches(1F);
        module.setQueryUnkMismatch(1);
        module.setGenomeUnkMismatch(1);
        module.setKmerSize(11);
        module.setIndelPenalty(5);
        module.setBaseSize(11);
        module.setFastq(new File(
                "/proj/seq/mapseq/HTSF/dev/130513_UNC13-SN749_0260_AD1WMHACXX/ChIPSeq/L008_GTCCGC/130513_UNC13-SN749_0260_AD1WMHACXX_GTCCGC_L008_R1.fastq"));
        module.setOutput(new File(
                "/proj/seq/mapseq/HTSF/dev/130513_UNC13-SN749_0260_AD1WMHACXX/ChIPSeq/L008_GTCCGC/130513_UNC13-SN749_0260_AD1WMHACXX_GTCCGC_L008_R1.sam"));

        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
