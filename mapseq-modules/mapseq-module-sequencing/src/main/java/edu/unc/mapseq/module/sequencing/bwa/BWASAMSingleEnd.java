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
@Application(name = "BWA :: SAMSE", executable = "$%s_BWA_HOME/bin/bwa samse")
public class BWASAMSingleEnd extends Module {

    @NotNull(message = "fastq is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "fastq is empty", groups = InputValidations.class)
    @FileIsReadable(message = "fastq is is not readable", groups = InputValidations.class)
    @InputArgument(delimiter = "", order = 3)
    private File fastq;

    @NotNull(message = "sai is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "fastaDB is empty", groups = InputValidations.class)
    @FileIsReadable(message = "sai does is not readable", groups = InputValidations.class)
    @InputArgument(delimiter = "", order = 2)
    private File sai;

    @NotNull(message = "fastaDB is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "fastaDB is empty", groups = InputValidations.class)
    @FileIsReadable(message = "fastaDB does is not readable", groups = InputValidations.class)
    @InputArgument(delimiter = "", order = 1)
    private File fastaDB;

    @NotNull(message = "outFile is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "outFile is empty", groups = OutputValidations.class)
    @OutputArgument(redirect = true, persistFileData = true, mimeType = MimeType.BWA_BAM)
    private File outFile;

    public BWASAMSingleEnd() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return BWASAMSingleEnd.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(),
                getWorkflowName().toUpperCase());
    }

    public File getFastq() {
        return fastq;
    }

    public void setFastq(File fastq) {
        this.fastq = fastq;
    }

    public File getSai() {
        return sai;
    }

    public void setSai(File sai) {
        this.sai = sai;
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
        return String.format("BWASAMSingleEnd [fastq=%s, sai=%s, fastaDB=%s, outFile=%s, toString()=%s]", fastq, sai,
                fastaDB, outFile, super.toString());
    }

    public static void main(String[] args) {
        BWASAMSingleEnd module = new BWASAMSingleEnd();
        module.setWorkflowName("TEST");
        module.setFastq(new File("/tmp", "fastq1.gz"));
        module.setSai(new File("/tmp", "sai1"));
        module.setFastaDB(new File("/tmp", "fasta.db"));
        module.setOutFile(new File("/tmp", "out.bam"));

        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
