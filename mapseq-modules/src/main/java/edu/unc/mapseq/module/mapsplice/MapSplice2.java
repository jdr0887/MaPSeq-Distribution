package edu.unc.mapseq.module.mapsplice;

import java.io.File;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;

@Application(name = "MapSplice2", executable = "python $%s_MAPSPLICE2_HOME/bin/mapsplice_multi_thread.py", wallTime = 5L)
public class MapSplice2 extends Module {

    @NotNull(message = "fastqR1 is required", groups = InputValidations.class)
    @InputArgument(flag = "-1")
    private File fastqR1;

    @NotNull(message = "fastqR2 is required", groups = InputValidations.class)
    @InputArgument(flag = "-2")
    private File fastqR2;

    @NotNull(message = "chromosomeDirectory is required", groups = InputValidations.class)
    @InputArgument(flag = "-c")
    private File chromosomeFilesDirectory;

    @InputArgument(flag = "--all-chromosomes-files")
    private File allChromosomeFiles;

    @InputArgument(flag = "-B")
    private File bowtieIndexPath;

    @InputArgument(flag = "-X")
    private Integer threads;

    @OutputArgument(flag = "-o")
    private File outputDirectory;

    @InputArgument(flag = "-i")
    private Integer minIntronLength;

    @InputArgument(flag = "-x")
    private Integer maxIntronLength;

    @InputArgument(flag = "-Q")
    private String readsFormat;

    @InputArgument(flag = "--pairend")
    private Boolean pairend;

    @InputArgument(flag = "--non-canonical")
    private Boolean nonCanonical;

    @InputArgument(flag = "--semi-canonical")
    private Boolean semiCanonical;

    @InputArgument(flag = "--fusion")
    private Boolean fusion;

    @InputArgument(flag = "-n")
    private Integer minAnchor;

    @InputArgument(flag = "-m")
    private Integer spliceMismatches;

    @InputArgument(flag = "-w")
    private Integer readWidth;

    @InputArgument(flag = "-S")
    private String fastaFilesExt;

    @InputArgument(flag = "-G")
    private Integer numseq;

    @InputArgument(flag = "-L")
    private Integer seqLength;

    @InputArgument(flag = "--full-running")
    private Boolean fullRunning;

    @InputArgument(flag = "--search-whole-chromosome")
    private Boolean searchWholeChromosome;

    @InputArgument(flag = "--map-segments-directly")
    private Boolean mapSegmentsDirectly;

    @InputArgument(flag = "--run-MapPER")
    private Boolean runMapPER;

    @InputArgument(flag = "--not-rem-temp")
    private Boolean notREMTemp;

    @InputArgument(flag = "-E")
    private Integer segmentMismatches;

    @Override
    public Class<?> getModuleClass() {
        return MapSplice2.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(),
                getWorkflowName().toUpperCase());
    }

    public File getFastqR1() {
        return fastqR1;
    }

    public void setFastqR1(File fastqR1) {
        this.fastqR1 = fastqR1;
    }

    public File getFastqR2() {
        return fastqR2;
    }

    public void setFastqR2(File fastqR2) {
        this.fastqR2 = fastqR2;
    }

    public File getChromosomeFilesDirectory() {
        return chromosomeFilesDirectory;
    }

    public void setChromosomeFilesDirectory(File chromosomeFilesDirectory) {
        this.chromosomeFilesDirectory = chromosomeFilesDirectory;
    }

    public File getAllChromosomeFiles() {
        return allChromosomeFiles;
    }

    public void setAllChromosomeFiles(File allChromosomeFiles) {
        this.allChromosomeFiles = allChromosomeFiles;
    }

    public File getBowtieIndexPath() {
        return bowtieIndexPath;
    }

    public void setBowtieIndexPath(File bowtieIndexPath) {
        this.bowtieIndexPath = bowtieIndexPath;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public Integer getMinIntronLength() {
        return minIntronLength;
    }

    public void setMinIntronLength(Integer minIntronLength) {
        this.minIntronLength = minIntronLength;
    }

    public Integer getMaxIntronLength() {
        return maxIntronLength;
    }

    public void setMaxIntronLength(Integer maxIntronLength) {
        this.maxIntronLength = maxIntronLength;
    }

    public String getReadsFormat() {
        return readsFormat;
    }

    public void setReadsFormat(String readsFormat) {
        this.readsFormat = readsFormat;
    }

    public Boolean getPairend() {
        return pairend;
    }

    public void setPairend(Boolean pairend) {
        this.pairend = pairend;
    }

    public Boolean getNonCanonical() {
        return nonCanonical;
    }

    public void setNonCanonical(Boolean nonCanonical) {
        this.nonCanonical = nonCanonical;
    }

    public Boolean getSemiCanonical() {
        return semiCanonical;
    }

    public void setSemiCanonical(Boolean semiCanonical) {
        this.semiCanonical = semiCanonical;
    }

    public Boolean getFusion() {
        return fusion;
    }

    public void setFusion(Boolean fusion) {
        this.fusion = fusion;
    }

    public Integer getMinAnchor() {
        return minAnchor;
    }

    public void setMinAnchor(Integer minAnchor) {
        this.minAnchor = minAnchor;
    }

    public Integer getSpliceMismatches() {
        return spliceMismatches;
    }

    public void setSpliceMismatches(Integer spliceMismatches) {
        this.spliceMismatches = spliceMismatches;
    }

    public Integer getReadWidth() {
        return readWidth;
    }

    public void setReadWidth(Integer readWidth) {
        this.readWidth = readWidth;
    }

    public String getFastaFilesExt() {
        return fastaFilesExt;
    }

    public void setFastaFilesExt(String fastaFilesExt) {
        this.fastaFilesExt = fastaFilesExt;
    }

    public Integer getNumseq() {
        return numseq;
    }

    public void setNumseq(Integer numseq) {
        this.numseq = numseq;
    }

    public Integer getSeqLength() {
        return seqLength;
    }

    public void setSeqLength(Integer seqLength) {
        this.seqLength = seqLength;
    }

    public Boolean getFullRunning() {
        return fullRunning;
    }

    public void setFullRunning(Boolean fullRunning) {
        this.fullRunning = fullRunning;
    }

    public Boolean getSearchWholeChromosome() {
        return searchWholeChromosome;
    }

    public void setSearchWholeChromosome(Boolean searchWholeChromosome) {
        this.searchWholeChromosome = searchWholeChromosome;
    }

    public Boolean getMapSegmentsDirectly() {
        return mapSegmentsDirectly;
    }

    public void setMapSegmentsDirectly(Boolean mapSegmentsDirectly) {
        this.mapSegmentsDirectly = mapSegmentsDirectly;
    }

    public Boolean getRunMapPER() {
        return runMapPER;
    }

    public void setRunMapPER(Boolean runMapPER) {
        this.runMapPER = runMapPER;
    }

    public Boolean getNotREMTemp() {
        return notREMTemp;
    }

    public void setNotREMTemp(Boolean notREMTemp) {
        this.notREMTemp = notREMTemp;
    }

    public Integer getSegmentMismatches() {
        return segmentMismatches;
    }

    public void setSegmentMismatches(Integer segmentMismatches) {
        this.segmentMismatches = segmentMismatches;
    }

    @Override
    public String toString() {
        return String.format(
                "MapSplice2 [fastqR1=%s, fastqR2=%s, chromosomeFilesDirectory=%s, allChromosomeFiles=%s, bowtieIndexPath=%s, threads=%s, outputDirectory=%s, minIntronLength=%s, maxIntronLength=%s, readsFormat=%s, pairend=%s, nonCanonical=%s, semiCanonical=%s, fusion=%s, minAnchor=%s, spliceMismatches=%s, readWidth=%s, fastaFilesExt=%s, numseq=%s, seqLength=%s, fullRunning=%s, searchWholeChromosome=%s, mapSegmentsDirectly=%s, runMapPER=%s, notREMTemp=%s, segmentMismatches=%s, toString()=%s]",
                fastqR1, fastqR2, chromosomeFilesDirectory, allChromosomeFiles, bowtieIndexPath, threads,
                outputDirectory, minIntronLength, maxIntronLength, readsFormat, pairend, nonCanonical, semiCanonical,
                fusion, minAnchor, spliceMismatches, readWidth, fastaFilesExt, numseq, seqLength, fullRunning,
                searchWholeChromosome, mapSegmentsDirectly, runMapPER, notREMTemp, segmentMismatches, super.toString());
    }

}
