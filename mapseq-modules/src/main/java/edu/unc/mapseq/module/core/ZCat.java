package edu.unc.mapseq.module.core;

import java.io.File;

import javax.validation.constraints.NotNull;

import org.renci.common.exec.BashExecutor;
import org.renci.common.exec.CommandInput;
import org.renci.common.exec.CommandOutput;
import org.renci.common.exec.Executor;
import org.renci.common.exec.ExecutorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.model.FileData;
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
import edu.unc.mapseq.module.constraints.FileIsNotEmpty;

/**
 * 
 * @author jdr0887
 * 
 */
@Application(name = "ZCat", isWorkflowRunIdOptional = true)
@Executable(value = "/bin/zcat")
public class ZCat extends Module {

    private final Logger logger = LoggerFactory.getLogger(ZCat.class);

    @NotNull(message = "Output File is required", groups = InputValidations.class)
    @OutputArgument
    private File outputFile;

    @NotNull(message = "directory is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "invalid output file", groups = OutputValidations.class)
    @InputArgument
    private File directory;

    @NotNull(message = "regularExpression is required", groups = InputValidations.class)
    @InputArgument
    private String regularExpression;

    @InputArgument
    private MimeType mimeType;

    public ZCat() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return ZCat.class;
    }

    @Override
    public ModuleOutput call() throws ModuleException {
        logger.debug("ENTERING call()");
        CommandOutput commandOutput;
        try {
            StringBuilder command = new StringBuilder();
            command.append(getModuleClass().getAnnotation(Executable.class).value());
            command.append(" ").append(regularExpression);
            command.append(" > ").append(outputFile.getAbsolutePath());

            CommandInput commandInput = new CommandInput();
            commandInput.setWorkDir(directory);
            commandInput.setCommand(command.toString());

            Executor executor = BashExecutor.getInstance();
            commandOutput = executor.execute(commandInput, new File(System.getProperty("user.home"), ".mapseqrc"));

            if (mimeType != null) {
                FileData fileData = new FileData();
                fileData.setName(outputFile.getName());
                fileData.setMimeType(mimeType);
                addFileData(fileData);
            }

        } catch (ExecutorException e) {
            throw new ModuleException(e);
        }
        return new ShellModuleOutput(commandOutput);
    }

    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public File getDirectory() {
        return directory;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public String getRegularExpression() {
        return regularExpression;
    }

    public void setRegularExpression(String regularExpression) {
        this.regularExpression = regularExpression;
    }

    public MimeType getMimeType() {
        return mimeType;
    }

    public void setMimeType(MimeType mimeType) {
        this.mimeType = mimeType;
    }

}
