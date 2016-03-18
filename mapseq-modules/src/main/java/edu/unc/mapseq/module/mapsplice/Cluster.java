package edu.unc.mapseq.module.mapsplice;

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

@Application(name = "Cluster", executable = "$%s_MAPSPLICE_HOME/bin/cluster")
public class Cluster extends Module {

    private final Logger logger = LoggerFactory.getLogger(Cluster.class);

    @NotNull(message = "clusterDirectory is required", groups = InputValidations.class)
    @FileIsReadable(message = "clusterDirectory does not exist or is not readable", groups = InputValidations.class)
    @InputArgument(delimiter = "")
    private File clusterDirectory;

    @Override
    public Class<?> getModuleClass() {
        return Cluster.class;
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
        command.append(" ").append(clusterDirectory.getAbsolutePath()).append("/");
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

    public File getClusterDirectory() {
        return clusterDirectory;
    }

    public void setClusterDirectory(File clusterDirectory) {
        this.clusterDirectory = clusterDirectory;
    }

    @Override
    public String toString() {
        return String.format("Cluster [logger=%s, clusterDirectory=%s, toString()=%s]", logger, clusterDirectory,
                super.toString());
    }

}
