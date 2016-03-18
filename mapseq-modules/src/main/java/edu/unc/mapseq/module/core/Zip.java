package edu.unc.mapseq.module.core;

import java.io.File;
import java.util.List;

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
import edu.unc.mapseq.module.constraints.FileIsReadable;
import edu.unc.mapseq.module.constraints.FileListIsReadable;

/**
 * 
 * @author jdr0887
 * 
 */
@Application(name = "Zip", executable = "/usr/bin/zip -j ", isWorkflowRunIdOptional = true)
public class Zip extends Module {

    @NotNull(message = "Zip is required", groups = InputValidations.class)
    @FileIsReadable(message = "Zip file does is not readable", groups = OutputValidations.class)
    @InputArgument
    private File output;

    @NotNull(message = "Entry is required", groups = InputValidations.class)
    @FileListIsReadable(message = "One or more entries is not readable", groups = InputValidations.class)
    @InputArgument(description = "Files to zip")
    private List<File> entry;

    public Zip() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return Zip.class;
    }

    @Override
    public ModuleOutput call() throws ModuleException {
        CommandInput commandInput = new CommandInput();
        StringBuilder command = new StringBuilder();
        command.append(getModuleClass().getAnnotation(Application.class).executable());

        try {
            command.append(output.getAbsolutePath());
            for (File f : entry) {
                command.append(" ").append(f.getAbsolutePath());
            }
        } catch (Exception e) {
            throw new ModuleException(e);
        }

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

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public List<File> getEntry() {
        return entry;
    }

    public void setEntry(List<File> entry) {
        this.entry = entry;
    }

    @Override
    public String toString() {
        return String.format("Zip [output=%s, entry=%s, toString()=%s]", output, entry, super.toString());
    }

    public static void main(String[] args) {

    }
}
