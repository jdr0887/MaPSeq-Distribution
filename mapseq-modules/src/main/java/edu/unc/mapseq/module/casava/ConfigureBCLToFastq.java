package edu.unc.mapseq.module.casava;

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
import edu.unc.mapseq.module.annotations.Executable;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.constraints.FileIsNotEmpty;
import edu.unc.mapseq.module.constraints.FileIsReadable;

/**
 * 
 * @author jdr0887
 * 
 */
@Application(name = "ConfigureBCLToFastQ")
@Executable(value = "$PERL_HOME/bin/perl $%s_CASAVA_HOME/bin/configureBclToFastq.pl")
public class ConfigureBCLToFastq extends Module {

    @NotNull(message = "workDir is required", groups = InputValidations.class)
    @InputArgument
    private File outputDir;

    @InputArgument
    private Integer fastqClusterCount;

    @NotNull(message = "inputDir is required", groups = InputValidations.class)
    @FileIsReadable(message = "inputDir is not readable", groups = InputValidations.class)
    @InputArgument
    private File inputDir;

    @NotNull(message = "sampleSheet is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "sampleSheet is empty", groups = InputValidations.class)
    @FileIsReadable(message = "sampleSheet is not readable", groups = InputValidations.class)
    @InputArgument
    private File sampleSheet;

    @InputArgument
    private Boolean force;

    @InputArgument
    private Boolean ignoreMissingBCL;

    @InputArgument
    private Boolean mismatches;

    @InputArgument
    private Boolean ignoreMissingStats;

    @InputArgument
    private Integer tiles;

    public ConfigureBCLToFastq() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return ConfigureBCLToFastq.class;
    }

    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Executable.class).value(), getWorkflowName().toUpperCase());
    }

    @Override
    public ModuleOutput call() throws ModuleException {
        CommandInput commandInput = new CommandInput();
        StringBuilder command = new StringBuilder();
        command.append(getExecutable());

        try {

            command.append(" --output-dir ").append(outputDir.getAbsolutePath());
            command.append(" --input-dir ").append(inputDir.getAbsolutePath());

            if (fastqClusterCount != null) {
                command.append(" --fastq-cluster-count ").append(fastqClusterCount);
            }

            command.append(" --sample-sheet ").append(sampleSheet.getAbsolutePath());

            if (tiles != null) {
                command.append(" --tiles s_").append(tiles.toString()).append("_*");
            }

            if (force != null && force) {
                command.append(" --force");
            }

            if (mismatches != null && mismatches) {
                command.append(" --mismatches 1");
            }

            if (ignoreMissingBCL != null && ignoreMissingBCL) {
                command.append(" --ignore-missing-bcl");
            }

            if (ignoreMissingStats != null && ignoreMissingStats) {
                command.append(" --ignore-missing-stats");
            }

        } catch (SecurityException e1) {
            e1.printStackTrace();
        }

        commandInput.setCommand(command.toString());

        CommandOutput commandOutput;
        try {
            Executor executor = BashExecutor.getInstance();
            commandOutput = executor.execute(commandInput, new File(System.getProperty("user.home"), ".mapseqrc"));
        } catch (ExecutorException e) {
            throw new ModuleException(e);
        }
        return new ShellModuleOutput(commandOutput);
    }

    public File getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir;
    }

    public File getInputDir() {
        return inputDir;
    }

    public void setInputDir(File inputDir) {
        this.inputDir = inputDir;
    }

    public File getSampleSheet() {
        return sampleSheet;
    }

    public void setSampleSheet(File sampleSheet) {
        this.sampleSheet = sampleSheet;
    }

    public Boolean getForce() {
        return force;
    }

    public void setForce(Boolean force) {
        this.force = force;
    }

    public Integer getFastqClusterCount() {
        return fastqClusterCount;
    }

    public void setFastqClusterCount(Integer fastqClusterCount) {
        this.fastqClusterCount = fastqClusterCount;
    }

    public Integer getTiles() {
        return tiles;
    }

    public void setTiles(Integer tiles) {
        this.tiles = tiles;
    }

    public Boolean getMismatches() {
        return mismatches;
    }

    public void setMismatches(Boolean mismatches) {
        this.mismatches = mismatches;
    }

    public Boolean getIgnoreMissingBCL() {
        return ignoreMissingBCL;
    }

    public void setIgnoreMissingBCL(Boolean ignoreMissingBCL) {
        this.ignoreMissingBCL = ignoreMissingBCL;
    }

    public Boolean getIgnoreMissingStats() {
        return ignoreMissingStats;
    }

    public void setIgnoreMissingStats(Boolean ignoreMissingStats) {
        this.ignoreMissingStats = ignoreMissingStats;
    }

}