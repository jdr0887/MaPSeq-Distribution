package edu.unc.mapseq.module.sequencing.gatk2;

import java.io.File;
import java.util.ArrayList;
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

@Application(name = "GATKBaseRecalibrator", executable = "$JAVA_HOME/bin/java -Xmx4g -Djava.io.tmpdir=$MAPSEQ_CLIENT_HOME/tmp -jar $%s_GATK2_HOME/GenomeAnalysisTK.jar --analysis_type BaseRecalibrator")
public class GATKBaseRecalibrator extends Module {

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
    @FileIsReadable(message = "out is not readable", groups = OutputValidations.class)
    @OutputArgument(flag = "--out", persistFileData = true, mimeType = MimeType.TEXT_GATK_REPORT)
    private File out;

    @NotNull(message = "knownSites is required", groups = InputValidations.class)
    @InputArgument(flag = "--knownSites")
    private List<File> knownSites;

    @InputArgument(flag = "--phone_home")
    private String phoneHome;

    @Override
    public Class<?> getModuleClass() {
        return GATKBaseRecalibrator.class;
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

    public File getInputFile() {
        return inputFile;
    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }

    public List<File> getKnownSites() {
        return knownSites;
    }

    public void setKnownSites(List<File> knownSites) {
        this.knownSites = knownSites;
    }

    @Override
    public String toString() {
        return String.format(
                "GATKBaseRecalibrator [key=%s, inputFile=%s, referenceSequence=%s, out=%s, knownSites=%s, phoneHome=%s, toString()=%s]",
                key, inputFile, referenceSequence, out, knownSites, phoneHome, super.toString());
    }

    public static void main(String[] args) {
        GATKBaseRecalibrator module = new GATKBaseRecalibrator();
        module.setWorkflowName("TEST");
        module.setInputFile(new File("/tmp", "input"));
        module.setKey(new File("/tmp", "key"));
        module.setReferenceSequence(new File("/tmp", "referenceSequence.fa"));
        module.setOut(new File("/tmp", "out"));
        module.setPhoneHome("NO_ET");

        List<File> knownSites = new ArrayList<File>();
        knownSites.add(new File("/tmp", "site1"));
        knownSites.add(new File("/tmp", "site2"));
        module.setKnownSites(knownSites);

        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
