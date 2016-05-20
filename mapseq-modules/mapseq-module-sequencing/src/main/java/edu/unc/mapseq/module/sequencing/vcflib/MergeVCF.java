package edu.unc.mapseq.module.sequencing.vcflib;

import java.io.File;

import javax.validation.constraints.NotNull;

import org.renci.common.exec.BashExecutor;
import org.renci.common.exec.CommandInput;
import org.renci.common.exec.CommandOutput;
import org.renci.common.exec.Executor;
import org.renci.common.exec.ExecutorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

@Application(name = "MergeVCF", executable = "/bin/cat %1$s/%2$s | $%3$s_VCFLIB_HOME/scripts/vcffirstheader | $%3$s_VCFLIB_HOME/bin/vcfstreamsort -w 1000 | $%3$s_VCFLIB_HOME/bin/vcfuniqalleles > %4$s")
public class MergeVCF extends Module {

    private static final Logger logger = LoggerFactory.getLogger(MergeVCF.class);

    @NotNull(message = "input is required", groups = InputValidations.class)
    @InputArgument
    private String input;

    @NotNull(message = "workDirectory is required", groups = InputValidations.class)
    @InputArgument
    private File workDirectory;

    @NotNull(message = "output is required", groups = InputValidations.class)
    @OutputArgument(redirect = true, persistFileData = true, mimeType = MimeType.TEXT_VCF)
    private File output;

    public MergeVCF() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return MergeVCF.class;
    }

    @Override
    public ModuleOutput call() throws Exception {
        CommandInput commandInput = new CommandInput();
        StringBuilder command = new StringBuilder(String.format(getModuleClass().getAnnotation(Application.class).executable(),
                workDirectory.getAbsolutePath(), input, getWorkflowName().toUpperCase(), output));
        commandInput.setCommand(command.toString());
        logger.info(command.toString());
        CommandOutput commandOutput;
        try {
            Executor executor = BashExecutor.getInstance();
            commandOutput = executor.execute(commandInput);
        } catch (ExecutorException e) {
            throw new ModuleException(e);
        }
        FileData fm = new FileData();
        fm.setMimeType(MimeType.TEXT_VCF);
        fm.setName(output.getName());
        getFileDatas().add(fm);
        return new ShellModuleOutput(commandOutput);
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public File getWorkDirectory() {
        return workDirectory;
    }

    public void setWorkDirectory(File workDirectory) {
        this.workDirectory = workDirectory;
    }

    public static void main(String[] args) {
        MergeVCF module = new MergeVCF();
        module.setWorkflowName("TEST");
        module.setWorkDirectory(new File("/tmp"));
        module.setInput("NCG_00531.*.vcf");
        module.setOutput(new File("/tmp", "output.vcf"));
        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
