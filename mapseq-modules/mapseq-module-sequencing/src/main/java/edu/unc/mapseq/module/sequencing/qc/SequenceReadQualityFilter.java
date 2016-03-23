package edu.unc.mapseq.module.sequencing.qc;

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

@Application(name = "SequenceReadQualityFilter", executable = "perl $MAPSEQ_CLIENT_HOME/bin/sw_module_qualFilter.pl")
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
        CommandInput commandInput = new CommandInput();
        StringBuilder command = new StringBuilder();
        command.append(String.format(getModuleClass().getAnnotation(Application.class).executable()));

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

    @Override
    public String toString() {
        return String.format("SequenceReadQualityFilter [inFile=%s, outFile=%s, toString()=%s]", inFile, outFile,
                super.toString());
    }

}
