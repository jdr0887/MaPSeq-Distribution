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
@Application(name = "BWA :: Align", executable = "$%s_BWA_HOME/bin/bwa aln")
public class BWAAlign extends Module {

    @NotNull(message = "fastq is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "fastq is empty", groups = InputValidations.class)
    @FileIsReadable(message = "fastq is not readable", groups = InputValidations.class)
    @InputArgument(order = 3, delimiter = "")
    private File fastq;

    @NotNull(message = "outFile is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "outFile is empty", groups = OutputValidations.class)
    @OutputArgument(redirect = true, persistFileData = true, mimeType = MimeType.BWA_SAI)
    private File outFile;

    @NotNull(message = "fastaDB is required", groups = InputValidations.class)
    @FileIsReadable(message = "fastaDB does is not readable", groups = InputValidations.class)
    @InputArgument(order = 2, delimiter = "")
    private File fastaDB;

    @NotNull(message = "Threads to use", groups = InputValidations.class)
    @InputArgument(flag = "-t", order = 1)
    private Integer threads;

    public BWAAlign() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return BWAAlign.class;
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

    public File getOutFile() {
        return outFile;
    }

    public void setOutFile(File outFile) {
        this.outFile = outFile;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public File getFastaDB() {
        return fastaDB;
    }

    public void setFastaDB(File fastaDB) {
        this.fastaDB = fastaDB;
    }

    @Override
    public String toString() {
        return String.format("BWAAlign [fastq=%s, outFile=%s, fastaDB=%s, threads=%s, toString()=%s]", fastq, outFile,
                fastaDB, threads, super.toString());
    }

    public static void main(String[] args) {

        BWAAlign module = new BWAAlign();
        module.setWorkflowName("TEST");
        module.setFastq(new File("/tmp", "fastq.gz"));
        module.setFastaDB(new File("/tmp", "fasta.db"));
        module.setOutFile(new File("/tmp", "out.sam"));
        module.setThreads(4);

        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
