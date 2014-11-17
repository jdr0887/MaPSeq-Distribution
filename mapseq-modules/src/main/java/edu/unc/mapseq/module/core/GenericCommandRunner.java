package edu.unc.mapseq.module.core;

import java.util.ArrayList;
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

@Application(name = "GenericCommandRunner")
public class GenericCommandRunner extends Module {

    @NotNull(message = "executable is required", groups = InputValidations.class)
    @InputArgument
    private String executable;

    @InputArgument
    private List<String> argument;

    @Override
    public ModuleOutput call() throws Exception {
        CommandInput commandInput = new CommandInput();
        StringBuilder commandSB = new StringBuilder();
        commandSB.append(this.executable);
        if (argument != null) {
            for (String arg : argument) {
                commandSB.append(String.format(" %s", arg));
            }
        }
        commandInput.setCommand(commandSB.toString());
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
        return GenericCommandRunner.class;
    }

    public String getExecutable() {
        return executable;
    }

    public void setExecutable(String executable) {
        this.executable = executable;
    }

    public List<String> getArgument() {
        return argument;
    }

    public void setArgument(List<String> argument) {
        this.argument = argument;
    }

    public static void main(String[] args) {
        GenericCommandRunner runner = new GenericCommandRunner();
        runner.setExecutable("/bin/echo");
        List<String> argumentList = new ArrayList<String>();
        argumentList.add("--input");
        argumentList.add("adsf");
        argumentList.add("--output");
        argumentList.add("qwer");
        runner.setArgument(argumentList);
        try {
            runner.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
