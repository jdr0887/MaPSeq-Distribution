package edu.unc.mapseq.module.mapsplice;

import java.io.File;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;

@Application(name = "MapSplice", executable = "python $%s_MAPSPLICE_HOME/mapsplice.py", wallTime = 5L)
public class MapSplice extends Module {

    @NotNull(message = "fastqR1 is required", groups = InputValidations.class)
    @InputArgument(flag = "-1")
    private File fastqR1;

    @NotNull(message = "fastqR2 is required", groups = InputValidations.class)
    @InputArgument(flag = "-2")
    private File fastqR2;

    @NotNull(message = "chromosomeDirectory is required", groups = InputValidations.class)
    @InputArgument(flag = "-c")
    private File chromosomeDirectory;

    @InputArgument(flag = "--all-chromosomes-files")
    private File allChromosomeFiles;

    @NotNull(message = "bowtieIndexPath is required", groups = InputValidations.class)
    @InputArgument(flag = "-x")
    private File bowtieIndexPath;

    @InputArgument(flag = "-p")
    private Integer threads;

    @NotNull(message = "output is required", groups = InputValidations.class)
    @OutputArgument(flag = "-o")
    private File output;

    @InputArgument(flag = "--bam")
    private Boolean bam;

    @InputArgument(flag = "--keep-tmp")
    private Boolean keepIntermediateFiles;

    @InputArgument(flag = "--seglen")
    private Integer segmentLength;

    @InputArgument(flag = "--min-map-len")
    private Integer minimumMapLength;

    @InputArgument(flag = "--min-len")
    private Integer minimumLength;

    @InputArgument(flag = "--splice-mis")
    private Integer spliceMis;

    @InputArgument(flag = "--max-append-mis")
    private Integer maxAppendMis;

    @InputArgument(flag = "--max-hits")
    private Integer maxHits;

    @InputArgument(flag = "--min-intron")
    private Integer minIntron;

    @InputArgument(flag = "--max-intron")
    private Integer maxIntron;

    @InputArgument(flag = "--qual-scale")
    private String qualScale;

    @InputArgument(flag = "--del")
    private Integer del;

    @InputArgument(flag = "--ins")
    private Integer ins;

    @InputArgument(flag = "--non-canonical")
    private Boolean nonCanonical;

    @InputArgument(flag = "--fusion")
    private Boolean fusionNonCanonical;

    @Override
    public Class<?> getModuleClass() {
        return MapSplice.class;
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

    public File getChromosomeDirectory() {
        return chromosomeDirectory;
    }

    public void setChromosomeDirectory(File chromosomeDirectory) {
        this.chromosomeDirectory = chromosomeDirectory;
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

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public Boolean getBam() {
        return bam;
    }

    public void setBam(Boolean bam) {
        this.bam = bam;
    }

    public Boolean getKeepIntermediateFiles() {
        return keepIntermediateFiles;
    }

    public void setKeepIntermediateFiles(Boolean keepIntermediateFiles) {
        this.keepIntermediateFiles = keepIntermediateFiles;
    }

    public Integer getSegmentLength() {
        return segmentLength;
    }

    public void setSegmentLength(Integer segmentLength) {
        this.segmentLength = segmentLength;
    }

    public Integer getMinimumMapLength() {
        return minimumMapLength;
    }

    public void setMinimumMapLength(Integer minimumMapLength) {
        this.minimumMapLength = minimumMapLength;
    }

    public Integer getMinimumLength() {
        return minimumLength;
    }

    public void setMinimumLength(Integer minimumLength) {
        this.minimumLength = minimumLength;
    }

    public Integer getSpliceMis() {
        return spliceMis;
    }

    public void setSpliceMis(Integer spliceMis) {
        this.spliceMis = spliceMis;
    }

    public Integer getMaxAppendMis() {
        return maxAppendMis;
    }

    public void setMaxAppendMis(Integer maxAppendMis) {
        this.maxAppendMis = maxAppendMis;
    }

    public Integer getMaxHits() {
        return maxHits;
    }

    public void setMaxHits(Integer maxHits) {
        this.maxHits = maxHits;
    }

    public Integer getMinIntron() {
        return minIntron;
    }

    public void setMinIntron(Integer minIntron) {
        this.minIntron = minIntron;
    }

    public Integer getMaxIntron() {
        return maxIntron;
    }

    public void setMaxIntron(Integer maxIntron) {
        this.maxIntron = maxIntron;
    }

    public String getQualScale() {
        return qualScale;
    }

    public void setQualScale(String qualScale) {
        this.qualScale = qualScale;
    }

    public Integer getDel() {
        return del;
    }

    public void setDel(Integer del) {
        this.del = del;
    }

    public Integer getIns() {
        return ins;
    }

    public void setIns(Integer ins) {
        this.ins = ins;
    }

    public Boolean getNonCanonical() {
        return nonCanonical;
    }

    public void setNonCanonical(Boolean nonCanonical) {
        this.nonCanonical = nonCanonical;
    }

    public Boolean getFusionNonCanonical() {
        return fusionNonCanonical;
    }

    public void setFusionNonCanonical(Boolean fusionNonCanonical) {
        this.fusionNonCanonical = fusionNonCanonical;
    }

    public File getAllChromosomeFiles() {
        return allChromosomeFiles;
    }

    public void setAllChromosomeFiles(File allChromosomeFiles) {
        this.allChromosomeFiles = allChromosomeFiles;
    }

    @Override
    public String toString() {
        return String.format(
                "MapSplice [fastqR1=%s, fastqR2=%s, chromosomeDirectory=%s, allChromosomeFiles=%s, bowtieIndexPath=%s, threads=%s, output=%s, bam=%s, keepIntermediateFiles=%s, segmentLength=%s, minimumMapLength=%s, minimumLength=%s, spliceMis=%s, maxAppendMis=%s, maxHits=%s, minIntron=%s, maxIntron=%s, qualScale=%s, del=%s, ins=%s, nonCanonical=%s, fusionNonCanonical=%s, toString()=%s]",
                fastqR1, fastqR2, chromosomeDirectory, allChromosomeFiles, bowtieIndexPath, threads, output, bam,
                keepIntermediateFiles, segmentLength, minimumMapLength, minimumLength, spliceMis, maxAppendMis, maxHits,
                minIntron, maxIntron, qualScale, del, ins, nonCanonical, fusionNonCanonical, super.toString());
    }

}
