package edu.unc.mapseq.module.mapsplice;

import java.io.File;
import java.io.FileInputStream;
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
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "FusionSAM2JunctionFilterAnchorNewFormat", executable = "$%s_MAPSPLICE_HOME/bin/fusionsam2junc_filteranchor_newfmt")
public class FusionSAM2JunctionFilterAnchorNewFormat extends Module {

    private final Logger logger = LoggerFactory.getLogger(FusionSAM2JunctionFilterAnchorNewFormat.class);

    @NotNull(message = "sam is required", groups = InputValidations.class)
    @FileIsReadable(message = "sam does not exist or is not readable", groups = InputValidations.class)
    @InputArgument
    private File sam;

    @NotNull(message = "junction is required", groups = InputValidations.class)
    @FileIsReadable(message = "junction does not exist or is not readable", groups = OutputValidations.class)
    @InputArgument
    private File junction;

    @NotNull(message = "readLength is required", groups = InputValidations.class)
    @FileIsReadable(message = "readLength does not exist or is not readable", groups = InputValidations.class)
    @InputArgument
    private File readLength;

    @NotNull(message = "minimumAnchor is required", groups = InputValidations.class)
    @InputArgument
    private Integer minimumAnchor;

    @NotNull(message = "referenceSequenceDirectory is required", groups = InputValidations.class)
    @FileIsReadable(message = "referenceSequenceDirectory does not exist or is not readable", groups = InputValidations.class)
    @InputArgument
    private File referenceSequenceDirectory;

    @Override
    public Class<?> getModuleClass() {
        return FusionSAM2JunctionFilterAnchorNewFormat.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(), getWorkflowName()
                .toUpperCase());
    }

    @Override
    public ModuleOutput call() throws Exception {
        logger.debug("ENTERING call()");

        Properties readLengthProperties = new Properties();
        FileInputStream fis = new FileInputStream(this.readLength);
        readLengthProperties.loadFromXML(fis);
        fis.close();

        StringBuilder command = new StringBuilder(getExecutable());

        command.append(" ").append(junction.getAbsolutePath());
        command.append(" ").append(readLengthProperties.getProperty("maxLength"));
        command.append(" ").append(minimumAnchor.toString());
        command.append(" ").append(referenceSequenceDirectory.getAbsolutePath());
        command.append(" ").append(sam.getAbsolutePath());

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

    public File getSam() {
        return sam;
    }

    public void setSam(File sam) {
        this.sam = sam;
    }

    public File getJunction() {
        return junction;
    }

    public void setJunction(File junction) {
        this.junction = junction;
    }

    public File getReadLength() {
        return readLength;
    }

    public void setReadLength(File readLength) {
        this.readLength = readLength;
    }

    public Integer getMinimumAnchor() {
        return minimumAnchor;
    }

    public void setMinimumAnchor(Integer minimumAnchor) {
        this.minimumAnchor = minimumAnchor;
    }

    public File getReferenceSequenceDirectory() {
        return referenceSequenceDirectory;
    }

    public void setReferenceSequenceDirectory(File referenceSequenceDirectory) {
        this.referenceSequenceDirectory = referenceSequenceDirectory;
    }

}
