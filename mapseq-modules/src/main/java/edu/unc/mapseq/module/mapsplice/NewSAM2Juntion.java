package edu.unc.mapseq.module.mapsplice;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

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
import edu.unc.mapseq.module.annotations.Executable;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;
import edu.unc.mapseq.module.constraints.FileListIsReadable;

@Application(name = "NewSAM2Juntion")
@Executable(value = "$%s_MAPSPLICE_HOME/bin/newsam2junc")
public class NewSAM2Juntion extends Module {

    private final Logger logger = LoggerFactory.getLogger(NewSAM2Juntion.class);

    @NotNull(message = "junctionFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "junctionFile does not exist or is not readable", groups = OutputValidations.class)
    @InputArgument(order = 0)
    private File junctionFile;

    @NotNull(message = "readLength is required", groups = InputValidations.class)
    @FileIsReadable(message = "readLength does not exist or is not readable", groups = InputValidations.class)
    @InputArgument(order = 1)
    private File readLength;

    @NotNull(message = "chromosomeFilesDirectory is required", groups = InputValidations.class)
    @InputArgument(order = 2)
    private File chromosomeFilesDirectory;

    @NotNull(message = "minimumIntron is required", groups = InputValidations.class)
    @InputArgument(order = 3)
    private Integer minimumIntron;

    @NotNull(message = "maximumIntron is required", groups = InputValidations.class)
    @InputArgument(order = 4)
    private Integer maximumIntron;

    @NotNull(message = "minimumAnchor is required", groups = InputValidations.class)
    @InputArgument(order = 5)
    private Integer minimumAnchor;

    @NotNull(message = "input is required", groups = InputValidations.class)
    @FileListIsReadable(message = "input does not exist or is not readable", groups = InputValidations.class)
    @InputArgument(order = 10)
    private List<File> input;

    @Override
    public Class<?> getModuleClass() {
        return NewSAM2Juntion.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Executable.class).value(), getWorkflowName().toUpperCase());
    }

    @Override
    public ModuleOutput call() throws Exception {
        logger.debug("ENTERING call()");
        StringBuilder command = new StringBuilder(getExecutable());

        command.append(" ").append(junctionFile.getAbsolutePath());
        Properties readLengthProperties = new Properties();
        FileInputStream fis = new FileInputStream(readLength);
        readLengthProperties.loadFromXML(fis);
        fis.close();

        command.append(" ").append(readLengthProperties.getProperty("maxLength"));
        command.append(" ").append(chromosomeFilesDirectory.getAbsolutePath());
        command.append(" ").append(minimumIntron);
        command.append(" ").append(maximumIntron);
        command.append(" ").append(minimumAnchor);
        for (File f : input) {
            command.append(" ").append(f.getAbsolutePath());
        }

        CommandInput commandInput = new CommandInput();
        logger.info("command.toString(): {}", command.toString());
        commandInput.setCommand(command.toString());
        CommandOutput commandOutput;
        try {
            Executor executor = BashExecutor.getInstance();
            commandOutput = executor.execute(commandInput, new File(System.getProperty("user.home"), ".mapseqrc"));
        } catch (ExecutorException e) {
            throw new ModuleException(e);
        }
        return new ShellModuleOutput(commandOutput);
    }

    public File getJunctionFile() {
        return junctionFile;
    }

    public void setJunctionFile(File junctionFile) {
        this.junctionFile = junctionFile;
    }

    public File getReadLength() {
        return readLength;
    }

    public void setReadLength(File readLength) {
        this.readLength = readLength;
    }

    public File getChromosomeFilesDirectory() {
        return chromosomeFilesDirectory;
    }

    public void setChromosomeFilesDirectory(File chromosomeFilesDirectory) {
        this.chromosomeFilesDirectory = chromosomeFilesDirectory;
    }

    public Integer getMinimumIntron() {
        return minimumIntron;
    }

    public void setMinimumIntron(Integer minimumIntron) {
        this.minimumIntron = minimumIntron;
    }

    public Integer getMaximumIntron() {
        return maximumIntron;
    }

    public void setMaximumIntron(Integer maximumIntron) {
        this.maximumIntron = maximumIntron;
    }

    public Integer getMinimumAnchor() {
        return minimumAnchor;
    }

    public void setMinimumAnchor(Integer minimumAnchor) {
        this.minimumAnchor = minimumAnchor;
    }

    public List<File> getInput() {
        return input;
    }

    public void setInput(List<File> input) {
        this.input = input;
    }

}
