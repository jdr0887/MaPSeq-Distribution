package edu.unc.mapseq.module.impl.core;

import java.io.File;

import javax.validation.constraints.NotNull;

import org.renci.common.exec.BashExecutor;
import org.renci.common.exec.CommandInput;
import org.renci.common.exec.CommandOutput;
import org.renci.common.exec.Executor;
import org.renci.common.exec.ExecutorException;

import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.MimeType;
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

@Application(name = "Postscript2PDF", executable = "/usr/bin/ps2pdf")
public class Postcript2PDF extends Module {

    @NotNull(message = "input is required", groups = InputValidations.class)
    @FileIsReadable(message = "input is not readable", groups = InputValidations.class)
    @FileIsNotEmpty(message = "input is empty", groups = InputValidations.class)
    @InputArgument
    private File input;

    @NotNull(message = "output is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "output is empty", groups = OutputValidations.class)
    @InputArgument
    private File output;

    public Postcript2PDF() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return Postcript2PDF.class;
    }

    @Override
    public ModuleOutput call() throws Exception {
        CommandInput commandInput = new CommandInput();
        StringBuilder command = new StringBuilder();
        command.append(getModuleClass().getAnnotation(Application.class).executable());
        command.append(" ").append(input.getAbsolutePath()).append(" ").append(output.getAbsolutePath());
        commandInput.setCommand(command.toString());
        CommandOutput commandOutput;
        try {
            Executor executor = BashExecutor.getInstance();
            commandOutput = executor.execute(commandInput);
        } catch (ExecutorException e) {
            throw new ModuleException(e);
        }

        FileData fileData = new FileData();
        fileData.setMimeType(MimeType.APPLICATION_PDF);
        fileData.setName(output.getName());
        getFileDatas().add(fileData);

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
        return String.format("Postcript2PDF [input=%s, output=%s, toString()=%s]", input, output, super.toString());
    }

}
