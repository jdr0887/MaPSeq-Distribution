package edu.unc.mapseq.module.impl.samtools;

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

@Application(name = "DetermineMedian", executable = "$%s_SAMTOOLS_HOME/bin/samtools view %s | /usr/bin/head -1 | /bin/cut -f 10 | awk '{print length($1)}' > %s")
public class DetermineMedian extends Module {

    @NotNull(message = "Input is required", groups = InputValidations.class)
    @FileIsReadable(message = "Invalid input file", groups = InputValidations.class)
    @FileIsNotEmpty(message = "input is empty", groups = InputValidations.class)
    @InputArgument
    private File input;

    @NotNull(message = "Output is required", groups = InputValidations.class)
    @FileIsReadable(message = "Invalid output file", groups = OutputValidations.class)
    @FileIsNotEmpty(message = "output is empty", groups = OutputValidations.class)
    @InputArgument
    private File output;

    public DetermineMedian() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return DetermineMedian.class;
    }

    @Override
    public ModuleOutput call() throws ModuleException {
        CommandInput commandInput = new CommandInput();
        commandInput.setCommand(String.format(getModuleClass().getAnnotation(Application.class).executable(),
                getWorkflowName().toUpperCase(), input.getAbsolutePath(), output.getAbsolutePath()));
        CommandOutput commandOutput;
        try {
            Executor executor = BashExecutor.getInstance();
            commandOutput = executor.execute(commandInput);
        } catch (ExecutorException e) {
            throw new ModuleException(e);
        }
        return new ShellModuleOutput(commandOutput);
    }

    public File getInput() {
        return input;
    }

    public void setInput(File input) {
        this.input = input;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    @Override
    public String toString() {
        return String.format("DetermineMedian [input=%s, output=%s, toString()=%s]", input, output, super.toString());
    }

}
