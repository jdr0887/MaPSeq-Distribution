package edu.unc.mapseq.module.sequencing.samtools;

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

@Application(name = "DetermineNumberOfReads", executable = "/usr/bin/head -1 %s | /bin/cut -f 1 -d ' ' > %s")
public class DetermineNumberOfReads extends Module {

    @NotNull(message = "flagstatInput is required", groups = InputValidations.class)
    @FileIsReadable(message = "Invalid flagstatInput file", groups = InputValidations.class)
    @FileIsNotEmpty(message = "flagstatInput is empty", groups = InputValidations.class)
    @InputArgument
    private File flagstatInput;

    @NotNull(message = "Output is required", groups = InputValidations.class)
    @FileIsReadable(message = "Invalid output file", groups = OutputValidations.class)
    @FileIsNotEmpty(message = "output is empty", groups = OutputValidations.class)
    @InputArgument
    private File output;

    public DetermineNumberOfReads() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return DetermineNumberOfReads.class;
    }

    @Override
    public ModuleOutput call() throws ModuleException {
        CommandInput commandInput = new CommandInput();
        commandInput.setCommand(String.format(getModuleClass().getAnnotation(Application.class).executable(),
                flagstatInput.getAbsolutePath(), output.getAbsolutePath()));
        CommandOutput commandOutput;
        try {
            Executor executor = BashExecutor.getInstance();
            commandOutput = executor.execute(commandInput);
        } catch (ExecutorException e) {
            throw new ModuleException(e);
        }
        return new ShellModuleOutput(commandOutput);
    }

    public File getFlagstatInput() {
        return flagstatInput;
    }

    public void setFlagstatInput(File flagstatInput) {
        this.flagstatInput = flagstatInput;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    @Override
    public String toString() {
        return String.format("DetermineNumberOfReads [flagstatInput=%s, output=%s, toString()=%s]", flagstatInput,
                output, super.toString());
    }

}
