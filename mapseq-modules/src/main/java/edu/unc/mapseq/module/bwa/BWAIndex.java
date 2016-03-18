package edu.unc.mapseq.module.bwa;

import java.io.File;

import javax.validation.constraints.NotNull;

import org.renci.common.exec.BashExecutor;
import org.renci.common.exec.CommandInput;
import org.renci.common.exec.CommandOutput;
import org.renci.common.exec.Executor;

import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleException;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.ShellModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;

/**
 * 
 * @author jdr0887
 * 
 */
@Application(name = "BWA :: Index", executable = "$%s_BWA_HOME/bin/bwa index")
public class BWAIndex extends Module {

    @NotNull(message = "fastaDB is required", groups = InputValidations.class)
    @FileIsReadable(message = "fastaDB is not readable", groups = InputValidations.class)
    @InputArgument
    private File fastaDB;

    @NotNull(message = "symlinkFileName is required", groups = InputValidations.class)
    @InputArgument
    private File symlinkFile;

    @NotNull(message = "workDir is required", groups = InputValidations.class)
    @InputArgument
    private File workDir;

    @NotNull(message = "algorithm type", groups = InputValidations.class)
    @InputArgument(flag = "-a")
    private BWAIndexAlgorithmType algorithm;

    public BWAIndex() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return BWAIndex.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(),
                getWorkflowName().toUpperCase());
    }

    @Override
    public ModuleOutput call() throws ModuleException {
        CommandOutput commandOutput = null;
        try {

            StringBuilder command = new StringBuilder();
            command.append("/bin/ln -s ").append(fastaDB).append(" ").append(symlinkFile.getAbsolutePath()).append(";");
            command.append(getExecutable());
            command.append(" ")
                    .append(getModuleClass().getDeclaredField("algorithm").getAnnotation(InputArgument.class).flag())
                    .append(" ").append(algorithm.getValue());
            command.append(" ").append(symlinkFile.getAbsolutePath());

            CommandInput commandInput = new CommandInput();
            File mapseqTmpDir = new File(System.getenv("MAPSEQ_HOME"), "tmp");
            commandInput.setWorkDir(mapseqTmpDir);
            commandInput.setCommand(command.toString());
            Executor executor = BashExecutor.getInstance();
            commandOutput = executor.execute(commandInput);
        } catch (Exception e) {
            throw new ModuleException(e);
        }

        return new ShellModuleOutput(commandOutput);
    }

    public File getFastaDB() {
        return fastaDB;
    }

    public void setFastaDB(File fastaDB) {
        this.fastaDB = fastaDB;
    }

    public BWAIndexAlgorithmType getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(BWAIndexAlgorithmType algorithm) {
        this.algorithm = algorithm;
    }

    public File getWorkDir() {
        return workDir;
    }

    public void setWorkDir(File workDir) {
        this.workDir = workDir;
    }

    public File getSymlinkFile() {
        return symlinkFile;
    }

    public void setSymlinkFile(File symlinkFile) {
        this.symlinkFile = symlinkFile;
    }

    @Override
    public String toString() {
        return String.format("BWAIndex [fastaDB=%s, symlinkFile=%s, workDir=%s, algorithm=%s, toString()=%s]", fastaDB,
                symlinkFile, workDir, algorithm, super.toString());
    }

}
