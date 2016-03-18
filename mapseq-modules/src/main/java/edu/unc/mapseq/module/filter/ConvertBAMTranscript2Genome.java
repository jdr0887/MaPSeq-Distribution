package edu.unc.mapseq.module.filter;

import java.io.File;

import javax.validation.constraints.NotNull;

import org.renci.common.exec.BashExecutor;
import org.renci.common.exec.CommandInput;
import org.renci.common.exec.CommandOutput;
import org.renci.common.exec.Executor;
import org.renci.common.exec.ExecutorException;

import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleException;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.ShellModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsNotEmpty;
import edu.unc.mapseq.module.constraints.FileIsReadable;

/**
 * 
 * @author jdr0887
 * 
 */
@Application(name = "ConvertBAMTranscript2Genome", executable = "perl $MAPSEQ_HOME/bin/sw_module_ConvertBAMTranscript2Genome.pl")
public class ConvertBAMTranscript2Genome extends Module {

    @NotNull(message = "inFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "Input file is not readable", groups = InputValidations.class)
    @InputArgument(description = "Input BAM file with reads mapped to transcript coordinates")
    private File inFile;

    @NotNull(message = "outFile is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "invalid output file", groups = OutputValidations.class)
    @InputArgument(description = "Output BAM file with reads mapped to genomic coordinates; <outfile>.bai is also generated.")
    private File outFile;

    @NotNull(message = "transcriptDB is required", groups = InputValidations.class)
    @FileIsReadable(message = "transcriptDB file is not readable", groups = InputValidations.class)
    @InputArgument(description = "data file specifying transcript-to-genome mapping, this is the flat file output of the PrepTranscriptDB module; "
            + "format = each transcript on a new line, tab-delimited, 7 columns: transcript, associated gene, transcript length, "
            + "genomic coordinates,transcript coordinates, and CDS start and stop in transcript coordinates; "
            + "example: uc004fvz.2{tab}CDY1|9085{tab}2363{tab}chrY:26194161-26192244,26191823-26191379:-{tab}1-1918,1919-2363{tab}327{tab}1991")
    private File transcriptDB;

    @NotNull(message = "sqHeader is required", groups = InputValidations.class)
    @FileIsReadable(message = "sqHeader file is not readable", groups = InputValidations.class)
    @InputArgument(description = "data file containing @SQ records for genome in SAM format")
    private File sqHeader;

    public ConvertBAMTranscript2Genome() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return ConvertBAMTranscript2Genome.class;
    }

    @Override
    public ModuleOutput call() throws ModuleException {

        CommandInput commandInput = new CommandInput();
        StringBuilder command = new StringBuilder();
        command.append(getModuleClass().getAnnotation(Application.class).executable());
        command.append(" ").append(inFile.getAbsolutePath());
        command.append(" ").append(outFile.getAbsolutePath());
        command.append(" ").append(transcriptDB.getAbsolutePath());
        command.append(" ").append(sqHeader.getAbsolutePath());
        commandInput.setCommand(command.toString());
        CommandOutput commandOutput;
        try {
            Executor executor = BashExecutor.getInstance();
            commandOutput = executor.execute(commandInput);
        } catch (ExecutorException e) {
            throw new ModuleException(e);
        }
        return new ShellModuleOutput(commandOutput);
    }

    public File getInFile() {
        return inFile;
    }

    public void setInFile(File inFile) {
        this.inFile = inFile;
    }

    public File getOutFile() {
        return outFile;
    }

    public void setOutFile(File outFile) {
        this.outFile = outFile;
    }

    public File getTranscriptDB() {
        return transcriptDB;
    }

    public void setTranscriptDB(File transcriptDB) {
        this.transcriptDB = transcriptDB;
    }

    public File getSqHeader() {
        return sqHeader;
    }

    public void setSqHeader(File sqHeader) {
        this.sqHeader = sqHeader;
    }

    @Override
    public String toString() {
        return String.format(
                "ConvertBAMTranscript2Genome [inFile=%s, outFile=%s, transcriptDB=%s, sqHeader=%s, toString()=%s]",
                inFile, outFile, transcriptDB, sqHeader, super.toString());
    }

}
