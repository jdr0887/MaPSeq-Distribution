package edu.unc.mapseq.module.impl.core;

import java.io.File;

import org.apache.commons.lang.StringUtils;
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
import edu.unc.mapseq.module.constraints.FileIsReadable;

/**
 * 
 * @author jdr0887
 */
@Application(name = "Make", executable = "/usr/bin/make")
public class Make extends Module {

    @InputArgument(delimiter = "")
    private String target;

    @InputArgument(delimiter = "", flag = "-j")
    private Integer threads;

    @FileIsReadable(message = "File does not exist", groups = InputValidations.class)
    @OutputArgument(delimiter = "")
    private File workDir;

    public Make() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return Make.class;
    }

    @Override
    public ModuleOutput call() throws ModuleException {
        CommandInput commandInput = new CommandInput();
        StringBuilder command = new StringBuilder();
        command.append(getModuleClass().getAnnotation(Application.class).executable());

        if (threads != null) {
            try {
                command.append(" ");
                command.append(getModuleClass().getDeclaredField("threads").getAnnotation(InputArgument.class).flag());
                command.append(" ");
                command.append(threads);
            } catch (Exception e) {
                throw new ModuleException(e);
            }
        }

        if (StringUtils.isNotEmpty(target)) {
            command.append(" ").append(target);
        }

        commandInput.setCommand(command.toString());
        commandInput.setWorkDir(this.workDir);

        CommandOutput commandOutput;
        try {
            Executor executor = BashExecutor.getInstance();
            commandOutput = executor.execute(commandInput);
        } catch (ExecutorException e) {
            throw new ModuleException(e);
        }
        return new ShellModuleOutput(commandOutput);
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public File getWorkDir() {
        return workDir;
    }

    public void setWorkDir(File workDir) {
        this.workDir = workDir;
    }

    @Override
    public String toString() {
        return String.format("Make [target=%s, threads=%s, workDir=%s, toString()=%s]", target, threads, workDir,
                super.toString());
    }

    public static void main(String[] args) {
        Make module = new Make();

        module.setThreads(2);
        module.setTarget("asdf");
        module.setWorkDir(new File("/tmp"));

        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
