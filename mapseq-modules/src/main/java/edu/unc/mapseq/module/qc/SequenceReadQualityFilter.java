package edu.unc.mapseq.module.qc;

import java.io.File;

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
import edu.unc.mapseq.module.annotations.Executable;
import edu.unc.mapseq.module.annotations.InputArgument;

@Application(name = "SequenceReadQualityFilter")
@Executable(value = "perl $MAPSEQ_HOME/bin/sw_module_qualFilter.pl")
public class SequenceReadQualityFilter extends Module {

    @InputArgument()
    private String inFile;

    @InputArgument()
    private String outFile;

    public SequenceReadQualityFilter() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return SequenceReadQualityFilter.class;
    }

    @Override
    public ModuleOutput call() throws ModuleException {
        File userHome = new File(System.getProperty("user.home"));

        CommandInput commandInput = new CommandInput();
        StringBuilder command = new StringBuilder();
        command.append(String.format(getModuleClass().getAnnotation(Executable.class).value()));

        commandInput.setCommand(command.toString());
        CommandOutput commandOutput;
        try {
            Executor executor = BashExecutor.getInstance();
            commandOutput = executor.execute(commandInput, new File(userHome, ".mapseqrc"));
        } catch (ExecutorException e) {
            throw new ModuleException(e);
        }
        return new ShellModuleOutput(commandOutput);
    }

    public String getInFile() {
        return inFile;
    }

    public void setInFile(String inFile) {
        this.inFile = inFile;
    }

    public String getOutFile() {
        return outFile;
    }

    public void setOutFile(String outFile) {
        this.outFile = outFile;
    }

}