package edu.unc.mapseq.module.mapsplice;

import java.io.File;

import javax.validation.constraints.NotNull;

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

@Application(name = "ReadsToUnmappedSAM")
@Executable(value = "$%s_MAPSPLICE_HOME/bin/reads2unmappedsam")
public class ReadsToUnmappedSAM extends Module {

    private final Logger logger = LoggerFactory.getLogger(ReadsToUnmappedSAM.class);

    @NotNull(message = "output is required", groups = InputValidations.class)
    @FileIsReadable(message = "output does not exist or is not readable", groups = OutputValidations.class)
    @InputArgument
    private File output;

    @NotNull(message = "input is required", groups = InputValidations.class)
    @FileIsReadable(message = "input does not exist or is not readable", groups = InputValidations.class)
    @InputArgument
    private File input;

    @InputArgument
    private Boolean fastq = Boolean.TRUE;

    public ReadsToUnmappedSAM() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return ReadsToUnmappedSAM.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Executable.class).value(), getWorkflowName().toUpperCase());
    }

    @Override
    public ModuleOutput call() throws Exception {
        logger.debug("ENTERING call()");
        StringBuilder command = new StringBuilder(getExecutable());
        command.append(" ").append(output.getAbsolutePath());
        if (fastq) {
            command.append(" 1");
        }
        command.append(" ").append(input.getAbsolutePath());
        CommandInput commandInput = new CommandInput();
        logger.info("command.toString(): {}", command.toString());
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

    public File getInput() {
        return input;
    }

    public void setInput(File input) {
        this.input = input;
    }

    public Boolean getFastq() {
        return fastq;
    }

    public void setFastq(Boolean fastq) {
        this.fastq = fastq;
    }

    public static void main(String[] args) {
        ReadsToUnmappedSAM module = new ReadsToUnmappedSAM();
        module.setWorkflowName("TEST");
        module.setInput(new File(
                "/proj/seq/mapseq/LBG/131104_UNC15-SN850_0337_BC2LWCACXX/RNASeq/C01_P-4-CALGB1066769-1/tmp/remap/remap_unmapped.1"));
        module.setOutput(new File(
                "/proj/seq/mapseq/LBG/131104_UNC15-SN850_0337_BC2LWCACXX/RNASeq/C01_P-4-CALGB1066769-1/tmp/remap/remap_unmapped.1.sam"));
        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
