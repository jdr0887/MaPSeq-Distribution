package edu.unc.mapseq.module.bwa;

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
@Application(name = "BWA :: SAMPE", executable = "$%s_BWA_HOME/bin/bwa sampe")
public class BWASAMPairedEnd extends Module {

    @NotNull(message = "fastq1 is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "fastq1 is empty", groups = InputValidations.class)
    @FileIsReadable(message = "fastq1 does is not readable", groups = InputValidations.class)
    @InputArgument(delimiter = "", order = 4)
    private File fastq1;

    @NotNull(message = "fastq2 is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "fastq2 is empty", groups = InputValidations.class)
    @FileIsReadable(message = "fastq2 does is not readable", groups = InputValidations.class)
    @InputArgument(delimiter = "", order = 5)
    private File fastq2;

    @NotNull(message = "sai1 is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "sai1 is empty", groups = InputValidations.class)
    @FileIsReadable(message = "sai1 does is not readable", groups = InputValidations.class)
    @InputArgument(delimiter = "", order = 2)
    private File sai1;

    @NotNull(message = "sai2 is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "sai2 is empty", groups = InputValidations.class)
    @FileIsReadable(message = "sai2 is not readable", groups = InputValidations.class)
    @InputArgument(delimiter = "", order = 3)
    private File sai2;

    @NotNull(message = "fastaDB is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "fastaDB is empty", groups = InputValidations.class)
    @FileIsReadable(message = "fastaDB does is not readable", groups = InputValidations.class)
    @InputArgument(delimiter = "", order = 1)
    private File fastaDB;

    @NotNull(message = "outFile is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "outFile is empty", groups = OutputValidations.class)
    @OutputArgument(redirect = true, persistFileData = true, mimeType = MimeType.BWA_BAM)
    private File outFile;

    public BWASAMPairedEnd() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return BWASAMPairedEnd.class;
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

    public File getSai1() {
        return sai1;
    }

    public void setSai1(File sai1) {
        this.sai1 = sai1;
    }

    public File getSai2() {
        return sai2;
    }

    public void setSai2(File sai2) {
        this.sai2 = sai2;
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

    @Override
    public String toString() {
        return String.format(
                "BWASAMPairedEnd [fastq1=%s, fastq2=%s, sai1=%s, sai2=%s, fastaDB=%s, outFile=%s, toString()=%s]",
                fastq1, fastq2, sai1, sai2, fastaDB, outFile, super.toString());
    }

    public static void main(String[] args) {
        BWASAMPairedEnd module = new BWASAMPairedEnd();
        module.setWorkflowName("TEST");
        module.setFastq1(new File("/tmp", "fastq1.gz"));
        module.setFastq2(new File("/tmp", "fastq2.gz"));
        module.setSai1(new File("/tmp", "sai1"));
        module.setSai2(new File("/tmp", "sai2"));
        module.setFastaDB(new File("/tmp", "fasta.db"));
        module.setOutFile(new File("/tmp", "out.bam"));

        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
