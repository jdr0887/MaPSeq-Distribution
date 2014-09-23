package edu.unc.mapseq.module.samtools;

import java.io.File;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
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
import edu.unc.mapseq.module.annotations.Executable;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.constraints.FileIsNotEmpty;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "SAMToolsSort")
@Executable(value = "$%s_SAMTOOLS_HOME/bin/samtools sort")
public class SAMToolsSort extends Module {

    @NotNull(message = "Input is required", groups = InputValidations.class)
    @FileIsReadable(message = "Invalid input file", groups = InputValidations.class)
    @FileIsNotEmpty(message = "input is empty", groups = InputValidations.class)
    @InputArgument(order = 2, delimiter = "")
    private File input;

    @NotNull(message = "Output is required", groups = InputValidations.class)
    @OutputArgument(redirect = true, mimeType = MimeType.APPLICATION_BAM)
    private File output;

    public SAMToolsSort() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return SAMToolsSort.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Executable.class).value(), getWorkflowName().toUpperCase());
    }

    @Override
    public ModuleOutput call() throws ModuleException {

        CommandInput commandInput = new CommandInput();
        StringBuilder command = new StringBuilder();
        command.append(getExecutable());
        command.append(" -o ");

        String name = input.getName();
        if (name.endsWith(".bam")) {
            name = StringUtils.removeEnd(name, ".bam");
        }

        command.append(input.getAbsolutePath()).append(" ").append(name).append(" > ").append(output.getAbsolutePath());
        commandInput.setCommand(command.toString());
        CommandOutput commandOutput;
        try {
            Executor executor = BashExecutor.getInstance();
            commandOutput = executor.execute(commandInput, new File(System.getProperty("user.home"), ".mapseqrc"));
        } catch (ExecutorException e) {
            throw new ModuleException(e);
        }
        FileData fm = new FileData();
        fm.setName(output.getName());
        fm.setMimeType(MimeType.APPLICATION_BAM);
        getFileDatas().add(fm);

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

    public static void main(String[] args) {
        SAMToolsSort module = new SAMToolsSort();
        module.setWorkflowName("TEST");
        module.setInput(new File("/tmp", "asdf"));
        module.setOutput(new File("/tmp", "qwer"));
        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
