package edu.unc.mapseq.module.impl.gatk;

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

@Application(name = "GATKFlagStat", executable = "$JAVA_HOME/bin/java -Xmx4g -Djava.io.tmpdir=$MAPSEQ_HOME/tmp -jar $%s_GATK_HOME/GenomeAnalysisTK.jar --analysis_type FlagStat")
public class GATKFlagStat extends Module {

    @NotNull(message = "inputFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "inputFile is not readable", groups = InputValidations.class)
    @InputArgument(flag = "--input_file")
    private File inputFile;

    @NotNull(message = "referenceSequence is required", groups = InputValidations.class)
    @FileIsReadable(message = "referenceSequence is not readable", groups = InputValidations.class)
    @InputArgument(flag = "--reference_sequence")
    private File referenceSequence;

    @NotNull(message = "out is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "invalid output file", groups = OutputValidations.class)
    @OutputArgument(flag = "--out", persistFileData = true, mimeType = MimeType.TEXT_STAT_SUMMARY)
    private File out;

    @NotNull(message = "intervals is required", groups = InputValidations.class)
    @FileIsReadable(message = "intervals is not readable", groups = InputValidations.class)
    @InputArgument(flag = "--intervals")
    private File intervals;

    @InputArgument(flag = "--phone_home")
    private String phoneHome;

    @InputArgument(flag = "--downsampling_type")
    private String downsamplingType;

    @Override
    public Class<?> getModuleClass() {
        return GATKFlagStat.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(),
                getWorkflowName().toUpperCase());
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

    public File getOut() {
        return out;
    }

    public void setOut(File out) {
        this.out = out;
    }

    public File getIntervals() {
        return intervals;
    }

    public void setIntervals(File intervals) {
        this.intervals = intervals;
    }

    @Override
    public String toString() {
        return String.format(
                "GATKFlagStat [inputFile=%s, referenceSequence=%s, out=%s, intervals=%s, phoneHome=%s, downsamplingType=%s, toString()=%s]",
                inputFile, referenceSequence, out, intervals, phoneHome, downsamplingType, super.toString());
    }

    public static void main(String[] args) {
        GATKFlagStat flagstat = new GATKFlagStat();
        flagstat.setWorkflowName("TEST");
        flagstat.setInputFile(new File("/tmp", "input"));
        flagstat.setOut(new File("/tmp", "output"));
        flagstat.setIntervals(new File("/tmp", "intervals"));
        flagstat.setDownsamplingType(GATKDownsamplingType.NONE.toString());
        flagstat.setPhoneHome(GATKPhoneHomeType.NO_ET.toString());
        try {
            flagstat.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
