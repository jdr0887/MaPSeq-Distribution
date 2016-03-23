package edu.unc.mapseq.module.sequencing.staden;

import java.io.File;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.renci.common.exec.BashExecutor;
import org.renci.common.exec.CommandInput;
import org.renci.common.exec.CommandOutput;
import org.renci.common.exec.Executor;
import org.renci.common.exec.ExecutorException;

import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleException;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.ShellModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;

/**
 * 
 * @author jdr0887
 * 
 */
@Application(name = "IlluminaToSRF", executable = "$%s_STADEN_IO_LIB_HOME/bin/illumina2srf")
public class IlluminaToSRF extends Module {

    @NotNull(message = "qSeqPath is required", groups = InputValidations.class)
    @FileIsReadable(message = "qSeqPath does not exist", groups = InputValidations.class)
    @InputArgument(description = "This is the path to qseq files in an Illumina-supplied run folder")
    private String qSeqPath;

    @NotNull(message = "lane is required", groups = InputValidations.class)
    @InputArgument(description = "The lane to convert (starting at 1). If the code for this module breaks it's likely here if Illumina changes its file naming conventions. It currently uses <qseq-path>/s_<lane>_*_qseq.txt")
    private Integer lane;

    @NotNull(message = "numberOfQSeqFiles is required", groups = InputValidations.class)
    @InputArgument(description = "The number of <qseq-path>/s_<lane>_*_qseq.txt files per lane. As of RTA 1.6.x this is 120 on the Illumina GAII. Clearly this value can/will change")
    private Integer numberOfQSeqFiles;

    @Max(message = "numberOfReads is greater than 2", value = 2, groups = InputValidations.class)
    @Min(message = "numberOfReads is less than 1", value = 1, groups = InputValidations.class)
    @NotNull(message = "numberOfReads is required", groups = InputValidations.class)
    @InputArgument(description = "The number of reads, for the current Illumina platform this is 1 or 2.")
    private Integer numberOfReads;

    @InputArgument(description = "CURRENTLY IGNORED: this will let you pass in the barcode sequence. The net result is an SRF file that only contains reads with this barcode.")
    private String barcode;

    @NotNull(message = "output is required", groups = InputValidations.class)
    @InputArgument(description = "This is the output SRF file name")
    private File output;

    public IlluminaToSRF() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return IlluminaToSRF.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(),
                getWorkflowName().toUpperCase());
    }

    @Override
    public ModuleOutput call() throws ModuleException {
        CommandInput commandInput = new CommandInput();
        StringBuilder command = new StringBuilder();
        command.append(getExecutable());
        command.append(" -force_config_machine_name");
        command.append(" -o ").append(output.getAbsolutePath());
        command.append(" ").append(qSeqPath).append(File.separator).append("s_").append(lane.toString())
                .append("_*qseq.txt");
        commandInput.setCommand(command.toString());
        CommandOutput commandOutput;
        try {
            Executor executor = BashExecutor.getInstance();
            commandOutput = executor.execute(commandInput);
        } catch (ExecutorException e) {
            throw new ModuleException(e);
        }

        FileData fm = new FileData();
        fm.setMimeType(MimeType.APPLICATION_SRF);
        fm.setName(output.getName());
        getFileDatas().add(fm);

        return new ShellModuleOutput(commandOutput);
    }

    public String getQSeqPath() {
        return qSeqPath;
    }

    public void setQSeqPath(String qSeqPath) {
        this.qSeqPath = qSeqPath;
    }

    public Integer getLane() {
        return lane;
    }

    public void setLane(Integer lane) {
        this.lane = lane;
    }

    public Integer getNumberOfQSeqFiles() {
        return numberOfQSeqFiles;
    }

    public void setNumberOfQSeqFiles(Integer numberOfQSeqFiles) {
        this.numberOfQSeqFiles = numberOfQSeqFiles;
    }

    public Integer getNumberOfReads() {
        return numberOfReads;
    }

    public void setNumberOfReads(Integer numberOfReads) {
        this.numberOfReads = numberOfReads;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    @Override
    public String toString() {
        return String.format(
                "IlluminaToSRF [qSeqPath=%s, lane=%s, numberOfQSeqFiles=%s, numberOfReads=%s, barcode=%s, output=%s, toString()=%s]",
                qSeqPath, lane, numberOfQSeqFiles, numberOfReads, barcode, output, super.toString());
    }

}
