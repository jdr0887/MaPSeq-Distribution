package edu.unc.mapseq.module.fastx;

import java.io.File;

import javax.validation.constraints.NotNull;

import org.renci.common.exec.BashExecutor;
import org.renci.common.exec.CommandInput;
import org.renci.common.exec.CommandOutput;
import org.renci.common.exec.Executor;

import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleException;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.ShellModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsNotEmpty;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "FastxNucleotideDistributionGraph", executable = "$%s_FASTX_TOOLKIT_HOME/bin/fastx_nucleotide_distribution_graph.sh")
public class FastxNucleotideDistributionGraph extends Module {

    @FileIsReadable(message = "input is not readable", groups = InputValidations.class)
    @FileIsNotEmpty(message = "input is empty", groups = InputValidations.class)
    @NotNull(message = "input is required", groups = InputValidations.class)
    @InputArgument(flag = "")
    private File input;

    @FileIsNotEmpty(message = "outFile is empty", groups = OutputValidations.class)
    @NotNull(message = "output is required", groups = InputValidations.class)
    @OutputArgument(flag = "")
    private File output;

    @InputArgument(flag = "-p")
    private Boolean generatePostcript;

    public FastxNucleotideDistributionGraph() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return FastxNucleotideDistributionGraph.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(),
                getWorkflowName().toUpperCase());
    }

    @Override
    public ModuleOutput call() throws ModuleException {

        CommandInput commandInput = new CommandInput();
        StringBuilder command = new StringBuilder();
        command.append(getExecutable());

        CommandOutput commandOutput = null;
        try {
            if (generatePostcript != null && generatePostcript) {
                command.append(" -p ");
            }
            command.append(" -i ").append(input.getAbsolutePath());
            command.append(" -o ").append(output.getAbsolutePath());

            commandInput.setCommand(command.toString());
            Executor executor = BashExecutor.getInstance();
            commandOutput = executor.execute(commandInput);

            FileData fm = new FileData();
            if (generatePostcript != null && generatePostcript) {
                fm.setMimeType(MimeType.APPLICATION_POSTSCRIPT);
            } else {
                fm.setMimeType(MimeType.IMAGE_PNG);
            }
            fm.setName(output.getName());
            getFileDatas().add(fm);

        } catch (Exception e) {
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

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public Boolean getGeneratePostcript() {
        return generatePostcript;
    }

    public void setGeneratePostcript(Boolean generatePostcript) {
        this.generatePostcript = generatePostcript;
    }

    @Override
    public String toString() {
        return String.format(
                "FastxNucleotideDistributionGraph [input=%s, output=%s, generatePostcript=%s, toString()=%s]", input,
                output, generatePostcript, super.toString());
    }

}
