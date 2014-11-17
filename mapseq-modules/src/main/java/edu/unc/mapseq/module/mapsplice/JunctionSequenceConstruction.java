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

import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleException;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.ShellModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.Executable;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "Bowtie")
@Executable(value = "$%s_MAPSPLICE_HOME/bin/junc_db")
public class JunctionSequenceConstruction extends Module {

    private final Logger logger = LoggerFactory.getLogger(JunctionSequenceConstruction.class);

    @NotNull(message = "minimumAnchor is required", groups = InputValidations.class)
    @InputArgument(order = 0, delimiter = "")
    private Integer minimumAnchor;

    @NotNull(message = "maximumAnchor is required", groups = InputValidations.class)
    @InputArgument(order = 1, delimiter = "")
    private Integer maximumAnchor;

    @NotNull(message = "maximumSequenceThreshold is required", groups = InputValidations.class)
    @InputArgument(order = 2, delimiter = "")
    private Integer maximumSequenceThreshold;

    @NotNull(message = "junction is required", groups = InputValidations.class)
    @FileIsReadable(message = "junction file does not exist or is not readable", groups = InputValidations.class)
    @InputArgument(order = 3, delimiter = "")
    private File junction;

    @NotNull(message = "referenceSequenceDirectory is required", groups = InputValidations.class)
    @FileIsReadable(message = "referenceSequenceDirectory does not exist or is not readable", groups = InputValidations.class)
    @InputArgument(order = 4, delimiter = "")
    private File referenceSequenceDirectory;

    @NotNull(message = "output is required", groups = InputValidations.class)
    @FileIsReadable(message = "output does not exist or is not readable", groups = OutputValidations.class)
    @OutputArgument(mimeType = MimeType.BOWTIE_HITS, persistFileData = true, delimiter = "")
    private File output;

    @Override
    public Class<?> getModuleClass() {
        return JunctionSequenceConstruction.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Executable.class).value(), getWorkflowName().toUpperCase());
    }

    @Override
    public ModuleOutput call() throws Exception {
        logger.debug("ENTERING call()");
        StringBuilder command = new StringBuilder(getExecutable());
        command.append(" ").append(this.minimumAnchor.toString());
        command.append(" ").append(this.maximumAnchor.toString());
        command.append(" ").append(this.maximumSequenceThreshold.toString());
        command.append(" ").append(this.junction.getAbsolutePath());
        command.append(" ").append(this.referenceSequenceDirectory.getAbsolutePath());
        if (this.referenceSequenceDirectory.isDirectory()
                && !this.referenceSequenceDirectory.getAbsolutePath().endsWith("/")) {
            command.append("/");
        }
        command.append(" ").append(this.output.getAbsolutePath());
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

    public Integer getMinimumAnchor() {
        return minimumAnchor;
    }

    public void setMinimumAnchor(Integer minimumAnchor) {
        this.minimumAnchor = minimumAnchor;
    }

    public Integer getMaximumAnchor() {
        return maximumAnchor;
    }

    public void setMaximumAnchor(Integer maximumAnchor) {
        this.maximumAnchor = maximumAnchor;
    }

    public Integer getMaximumSequenceThreshold() {
        return maximumSequenceThreshold;
    }

    public void setMaximumSequenceThreshold(Integer maximumSequenceThreshold) {
        this.maximumSequenceThreshold = maximumSequenceThreshold;
    }

    public File getJunction() {
        return junction;
    }

    public void setJunction(File junction) {
        this.junction = junction;
    }

    public File getReferenceSequenceDirectory() {
        return referenceSequenceDirectory;
    }

    public void setReferenceSequenceDirectory(File referenceSequenceDirectory) {
        this.referenceSequenceDirectory = referenceSequenceDirectory;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public static void main(String[] args) {

        JunctionSequenceConstruction module = new JunctionSequenceConstruction();
        module.setWorkflowName("TEST");
        module.setOutput(new File("/home/jdr0887/data/mapsplice/tmp/remap", "synthetic_alljunc_sequence.txt"));
        module.setReferenceSequenceDirectory(new File("/home/jdr0887/data/mapsplice/hg19_M_rCRS/chromosomes"));
        module.setMinimumAnchor(2);
        module.setMaximumAnchor(38);
        module.setMaximumSequenceThreshold(400);
        module.setJunction(new File("/home/jdr0887/data/mapsplice/tmp/remap", "best_junction.txt"));

        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
