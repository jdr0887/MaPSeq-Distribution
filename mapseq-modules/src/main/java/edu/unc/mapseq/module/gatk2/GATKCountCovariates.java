package edu.unc.mapseq.module.gatk2;

import java.io.File;
import java.util.List;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "GATKCountCovariates", executable = "$JAVA_HOME/bin/java -Xmx4g -Djava.io.tmpdir=$MAPSEQ_HOME/tmp -jar $%s_GATK2_HOME/GenomeAnalysisTK.jar --analysis_type CountCovariates")
public class GATKCountCovariates extends Module {

    @NotNull(message = "key is required", groups = InputValidations.class)
    @FileIsReadable(message = "key is not readable", groups = InputValidations.class)
    @InputArgument(flag = "")
    private File key;

    @NotNull(message = "inputFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "inputFile is not readable", groups = InputValidations.class)
    @InputArgument(flag = "--input_file")
    private File inputFile;

    @NotNull(message = "referenceSequence is required", groups = InputValidations.class)
    @FileIsReadable(message = "referenceSequence is not readable", groups = InputValidations.class)
    @InputArgument(flag = "--reference_sequence")
    private File referenceSequence;

    @InputArgument(flag = "--num_threads")
    private Integer numThreads;

    @NotNull(message = "recalFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "recalFile is not readable", groups = OutputValidations.class)
    @OutputArgument(flag = "--recal_file", persistFileData = true, mimeType = MimeType.TEXT_RECALIBRATION)
    private File recalFile;

    @InputArgument(flag = "--context_size")
    private Integer contextSize;

    @InputArgument(flag = "--cov")
    private List<String> covariate;

    @InputArgument(flag = "--dont_sort_output")
    private Boolean dontSortOutput = Boolean.FALSE;

    @InputArgument(flag = "--homopolymer_nback")
    private Integer homopolymerNBack;

    @InputArgument(flag = "--knownSites")
    private List<File> knownSites;

    @InputArgument(flag = "--standard_covs")
    private Boolean standardCovs = Boolean.FALSE;

    @InputArgument(flag = "--validation_strictness")
    private String validationStrictness;

    @InputArgument(flag = "--phone_home")
    private String phoneHome;

    @InputArgument(flag = "--downsampling_type")
    private String downsamplingType;

    @Override
    public Class<?> getModuleClass() {
        return GATKCountCovariates.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(),
                getWorkflowName().toUpperCase());
    }

    public File getKey() {
        return key;
    }

    public void setKey(File key) {
        this.key = key;
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

    public File getInputFile() {
        return inputFile;
    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }

    public File getReferenceSequence() {
        return referenceSequence;
    }

    public void setReferenceSequence(File referenceSequence) {
        this.referenceSequence = referenceSequence;
    }

    public Integer getNumThreads() {
        return numThreads;
    }

    public void setNumThreads(Integer numThreads) {
        this.numThreads = numThreads;
    }

    public File getRecalFile() {
        return recalFile;
    }

    public void setRecalFile(File recalFile) {
        this.recalFile = recalFile;
    }

    public Integer getContextSize() {
        return contextSize;
    }

    public void setContextSize(Integer contextSize) {
        this.contextSize = contextSize;
    }

    public List<String> getCovariate() {
        return covariate;
    }

    public void setCovariate(List<String> covariate) {
        this.covariate = covariate;
    }

    public Boolean getDontSortOutput() {
        return dontSortOutput;
    }

    public void setDontSortOutput(Boolean dontSortOutput) {
        this.dontSortOutput = dontSortOutput;
    }

    public Integer getHomopolymerNBack() {
        return homopolymerNBack;
    }

    public void setHomopolymerNBack(Integer homopolymerNBack) {
        this.homopolymerNBack = homopolymerNBack;
    }

    public List<File> getKnownSites() {
        return knownSites;
    }

    public void setKnownSites(List<File> knownSites) {
        this.knownSites = knownSites;
    }

    public Boolean getStandardCovs() {
        return standardCovs;
    }

    public void setStandardCovs(Boolean standardCovs) {
        this.standardCovs = standardCovs;
    }

    public String getValidationStrictness() {
        return validationStrictness;
    }

    public void setValidationStrictness(String validationStrictness) {
        this.validationStrictness = validationStrictness;
    }

    @Override
    public String toString() {
        return String.format(
                "GATKCountCovariates [key=%s, inputFile=%s, referenceSequence=%s, numThreads=%s, recalFile=%s, contextSize=%s, covariate=%s, dontSortOutput=%s, homopolymerNBack=%s, knownSites=%s, standardCovs=%s, validationStrictness=%s, phoneHome=%s, downsamplingType=%s, toString()=%s]",
                key, inputFile, referenceSequence, numThreads, recalFile, contextSize, covariate, dontSortOutput,
                homopolymerNBack, knownSites, standardCovs, validationStrictness, phoneHome, downsamplingType,
                super.toString());
    }

}
