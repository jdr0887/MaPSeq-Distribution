package edu.unc.mapseq.module.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
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

/**
 * 
 * @author jdr0887
 */
@Application(name = "Sort")
@Executable(value = "/bin/sort")
// @Executable(value = "/usr/bin/sort")
public class Sort extends Module {

    private final Logger logger = LoggerFactory.getLogger(Sort.class);

    @NotNull(message = "input is required", groups = InputValidations.class)
    @FileListIsReadable(message = "input does not exist", groups = InputValidations.class)
    @InputArgument
    private List<File> input;

    @NotNull(message = "output is required", groups = InputValidations.class)
    @FileIsReadable(message = "output does not exist", groups = OutputValidations.class)
    @InputArgument
    private File output;

    @InputArgument
    private File tmpDirectory;

    @InputArgument
    private Integer bufferSize;

    @InputArgument
    private String key;

    public Sort() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return Sort.class;
    }

    @Override
    public ModuleOutput call() throws Exception {
        logger.debug("ENTERING call()");

        StringBuilder command = new StringBuilder(getExecutable());
        if (this.bufferSize != null) {
            command.append(" -S ").append(this.bufferSize.toString());
        }

        if (StringUtils.isNotEmpty(this.key)) {
            command.append(" -k ").append(this.key);
        }

        if (this.tmpDirectory != null && this.tmpDirectory.exists()) {
            command.append(" -T ").append(this.tmpDirectory.getAbsolutePath());
        }

        command.append(" -o ").append(this.output.getAbsolutePath());

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

    public List<File> getInput() {
        return input;
    }

    public void setInput(List<File> input) {
        this.input = input;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public File getTmpDirectory() {
        return tmpDirectory;
    }

    public void setTmpDirectory(File tmpDirectory) {
        this.tmpDirectory = tmpDirectory;
    }

    public Integer getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(Integer bufferSize) {
        this.bufferSize = bufferSize;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public static void main(String[] args) {
        Sort module = new Sort();
        List<File> inputList = new ArrayList<File>();
        inputList.add(new File("/tmp", "asdf.txt"));
        inputList.add(new File("/tmp", "qwer.txt"));
        module.setInput(inputList);
        module.setOutput(new File("/tmp", "zxcv.txt"));
        module.setTmpDirectory(new File("/tmp"));
        module.setBufferSize(3500000);

        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
