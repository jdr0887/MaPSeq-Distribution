package edu.unc.mapseq.module.gatk;

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

@Application(name = "GATKApplyRecalibration", executable = "$JAVA_HOME/bin/java -Xmx4g -Djava.io.tmpdir=$MAPSEQ_HOME/tmp -jar $%s_GATK_HOME/GenomeAnalysisTK.jar --analysis_type ApplyRecalibration")
public class GATKApplyRecalibration extends Module {

    @NotNull(message = "input is required", groups = InputValidations.class)
    @FileIsReadable(message = "input is not readable", groups = InputValidations.class)
    @InputArgument(flag = "--input")
    private File input;

    @NotNull(message = "referenceSequence is required", groups = InputValidations.class)
    @FileIsReadable(message = "referenceSequence is not readable", groups = InputValidations.class)
    @InputArgument(flag = "--reference_sequence")
    private File referenceSequence;

    @InputArgument(flag = "--num_threads")
    private Integer numThreads;

    @NotNull(message = "out is required", groups = InputValidations.class)
    @FileIsReadable(message = "out is not readable", groups = OutputValidations.class)
    @OutputArgument(flag = "--out", persistFileData = true, mimeType = MimeType.TEXT_VCF)
    private File out;

    @NotNull(message = "recalFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "recalFile is not readable", groups = InputValidations.class)
    @InputArgument(flag = "--recal_file")
    private File recalFile;

    @NotNull(message = "tranchesFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "tranchesFile is not readable", groups = InputValidations.class)
    @InputArgument(flag = "--tranches_file")
    private File tranchesFile;

    @InputArgument(flag = "--phone_home")
    private String phoneHome;

    @InputArgument(flag = "--downsampling_type")
    private String downsamplingType;

    @InputArgument(flag = "--validation_strictness")
    private String validationStrictness;

    @InputArgument(flag = "--intervals")
    private File intervals;

    @InputArgument(flag = "--mode")
    private String mode;

    @InputArgument(flag = "--ts_filter_level")
    private Double tsFilterLevel;

    @Override
    public Class<?> getModuleClass() {
        return GATKApplyRecalibration.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(),
                getWorkflowName().toUpperCase());
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

    public File getOut() {
        return out;
    }

    public void setOut(File out) {
        this.out = out;
    }

    public File getInput() {
        return input;
    }

    public void setInput(File input) {
        this.input = input;
    }

    public File getRecalFile() {
        return recalFile;
    }

    public void setRecalFile(File recalFile) {
        this.recalFile = recalFile;
    }

    public File getTranchesFile() {
        return tranchesFile;
    }

    public void setTranchesFile(File tranchesFile) {
        this.tranchesFile = tranchesFile;
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

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Double getTsFilterLevel() {
        return tsFilterLevel;
    }

    public void setTsFilterLevel(Double tsFilterLevel) {
        this.tsFilterLevel = tsFilterLevel;
    }

    @Override
    public String toString() {
        return String.format(
                "GATKApplyRecalibration [input=%s, referenceSequence=%s, numThreads=%s, out=%s, recalFile=%s, tranchesFile=%s, phoneHome=%s, downsamplingType=%s, validationStrictness=%s, intervals=%s, mode=%s, tsFilterLevel=%s, toString()=%s]",
                input, referenceSequence, numThreads, out, recalFile, tranchesFile, phoneHome, downsamplingType,
                validationStrictness, intervals, mode, tsFilterLevel, super.toString());
    }

    public static void main(String[] args) {
        GATKApplyRecalibration module = new GATKApplyRecalibration();
        module.setDryRun(true);
        module.setWorkflowName("TEST");
        module.setInput(new File("/tmp/input"));
        module.setOut(new File("/tmp/output"));
        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
