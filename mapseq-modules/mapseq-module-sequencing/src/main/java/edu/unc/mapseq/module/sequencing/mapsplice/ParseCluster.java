package edu.unc.mapseq.module.sequencing.mapsplice;

import java.io.File;

import javax.validation.constraints.NotNull;

import org.renci.common.exec.BashExecutor;
import org.renci.common.exec.CommandInput;
import org.renci.common.exec.CommandOutput;
import org.renci.common.exec.Executor;
import org.renci.common.exec.ExecutorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleException;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.ShellModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "ParseCluster", executable = "$%s_MAPSPLICE_HOME/bin/parseCluster")
public class ParseCluster extends Module {

    private final Logger logger = LoggerFactory.getLogger(ParseCluster.class);

    @NotNull(message = "input is required", groups = InputValidations.class)
    @FileIsReadable(message = "input does not exist or is not readable", groups = InputValidations.class)
    @InputArgument(order = 0, delimiter = "")
    private File input;

    @NotNull(message = "outputDirectory is required", groups = InputValidations.class)
    @InputArgument(order = 1, delimiter = "")
    private File outputDirectory;

    @Override
    public Class<?> getModuleClass() {
        return ParseCluster.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(),
                getWorkflowName().toUpperCase());
    }

    @Override
    public ModuleOutput call() throws Exception {
        logger.debug("ENTERING call()");
        StringBuilder command = new StringBuilder(getExecutable());
        command.append(" ").append(input.getAbsolutePath());
        command.append(" ").append(outputDirectory.getAbsolutePath());
        if (this.outputDirectory.isDirectory() && !this.outputDirectory.getAbsolutePath().endsWith("/")) {
            command.append("/");
        }
        CommandInput commandInput = new CommandInput();
        logger.info("command.toString(): {}", command.toString());
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

    public File getInput() {
        return input;
    }

    public void setInput(File input) {
        this.input = input;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @Override
    public String toString() {
        return String.format("ParseCluster [logger=%s, input=%s, outputDirectory=%s, toString()=%s]", logger, input,
                outputDirectory, super.toString());
    }

}
