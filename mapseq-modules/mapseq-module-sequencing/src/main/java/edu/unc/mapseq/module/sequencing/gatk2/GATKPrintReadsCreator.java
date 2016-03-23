package edu.unc.mapseq.module.sequencing.gatk2;

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
import edu.unc.mapseq.module.sequencing.gatk.GATKPhoneHomeType;

@Application(name = "GATKPrintReadsCreator", executable = "$JAVA_HOME/bin/java -Xmx4g -Djava.io.tmpdir=$MAPSEQ_CLIENT_HOME/tmp -jar $%s_GATK2_HOME/GenomeAnalysisTK.jar --analysis_type PrintReads")
public class GATKPrintReadsCreator extends Module {

    @NotNull(message = "key is required", groups = InputValidations.class)
    @FileIsReadable(message = "key is not readable", groups = InputValidations.class)
    @InputArgument(flag = "--gatk_key")
    private File key;

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
    @OutputArgument(flag = "--out", persistFileData = true, mimeType = MimeType.APPLICATION_BAM)
    private File out;

    @NotNull(message = "baseQualityScoreRecalibrationFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "baseQualityScoreRecalibrationFile is not readable", groups = InputValidations.class)
    @InputArgument(flag = "--BQSR")
    private File baseQualityScoreRecalibrationFile;

    @InputArgument(flag = "--phone_home")
    private String phoneHome;

    @Override
    public Class<?> getModuleClass() {
        return GATKPrintReadsCreator.class;
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

    public File getOut() {
        return out;
    }

    public void setOut(File out) {
        this.out = out;
    }

    public File getBaseQualityScoreRecalibrationFile() {
        return baseQualityScoreRecalibrationFile;
    }

    public void setBaseQualityScoreRecalibrationFile(File baseQualityScoreRecalibrationFile) {
        this.baseQualityScoreRecalibrationFile = baseQualityScoreRecalibrationFile;
    }

    public String getPhoneHome() {
        return phoneHome;
    }

    public void setPhoneHome(String phoneHome) {
        this.phoneHome = phoneHome;
    }

    @Override
    public String toString() {
        return String.format(
                "GATKPrintReadsCreator [key=%s, inputFile=%s, referenceSequence=%s, out=%s, baseQualityScoreRecalibrationFile=%s, phoneHome=%s, toString()=%s]",
                key, inputFile, referenceSequence, out, baseQualityScoreRecalibrationFile, phoneHome, super.toString());
    }

    public static void main(String[] args) {

        GATKPrintReadsCreator module = new GATKPrintReadsCreator();
        module.setReferenceSequence(new File(
                "$NIDAUCSFVARIANTCALLING_REFERENCES_DIRECTORY/BUILD.37.1/bwa061sam0118/BUILD.37.1.sorted.shortid.fa"));
        module.setKey(
                new File("$NIDAUCSFVARIANTCALLING_SEQUENCE_ANALYSIS_RESOURCES_DIRECTORY/gatk/key/xiao_renci.org.key"));
        module.setPhoneHome(GATKPhoneHomeType.NO_ET.toString());

        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
