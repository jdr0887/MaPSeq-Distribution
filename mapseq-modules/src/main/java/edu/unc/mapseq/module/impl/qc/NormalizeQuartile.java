package edu.unc.mapseq.module.impl.qc;

import java.io.File;

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
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;

/**
 * 
 * @author jdr0887
 * 
 */
@Application(name = "NormalizeQuartile", executable = "perl $MAPSEQ_CLIENT_HOME/bin/quartile_norm.pl")
public class NormalizeQuartile extends Module {

    @NotNull(message = "column is required", groups = InputValidations.class)
    @InputArgument
    private Integer column;

    @NotNull(message = "quantile is required", groups = InputValidations.class)
    @InputArgument
    private Integer quantile;

    @NotNull(message = "target is required", groups = InputValidations.class)
    @InputArgument
    private Integer target;

    @NotNull(message = "output is required", groups = InputValidations.class)
    @FileIsReadable(message = "output is not readable", groups = OutputValidations.class)
    @OutputArgument
    private File output;

    @NotNull(message = "input is required", groups = InputValidations.class)
    @FileIsReadable(message = "input is not readable", groups = InputValidations.class)
    @InputArgument
    private File input;

    @Override
    public ModuleOutput call() throws Exception {
        CommandInput commandInput = new CommandInput();
        StringBuilder command = new StringBuilder();
        command.append(getModuleClass().getAnnotation(Application.class).executable());
        command.append(" -c ").append(column.toString());
        command.append(" -q ").append(quantile.toString());
        command.append(" -t ").append(target.toString());
        command.append(" -o ").append(output.getAbsolutePath());
        command.append(" ").append(input.getAbsolutePath());
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

    @Override
    public Class<?> getModuleClass() {
        return NormalizeQuartile.class;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public Integer getQuantile() {
        return quantile;
    }

    public void setQuantile(Integer quantile) {
        this.quantile = quantile;
    }

    public Integer getTarget() {
        return target;
    }

    public void setTarget(Integer target) {
        this.target = target;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public File getInput() {
        return input;
    }

    public void setInput(File input) {
        this.input = input;
    }

    @Override
    public String toString() {
        return String.format(
                "NormalizeQuartile [column=%s, quantile=%s, target=%s, output=%s, input=%s, toString()=%s]", column,
                quantile, target, output, input, super.toString());
    }

}
