package edu.unc.mapseq.module.impl.gatk;

import java.io.File;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.renci.common.exec.BashExecutor;
import org.renci.common.exec.CommandInput;
import org.renci.common.exec.CommandOutput;
import org.renci.common.exec.Executor;
import org.renci.common.exec.ExecutorException;

import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleException;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.ShellModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsNotEmpty;
import edu.unc.mapseq.module.constraints.FileIsReadable;

/**
 * 
 * @author jdr0887
 * 
 */
@Application(name = "GATKIndelRealigner", executable = "$JAVA_HOME/bin/java -Xmx4g -Djava.io.tmpdir=$MAPSEQ_CLIENT_HOME/tmp -jar $%s_GATK_HOME/GenomeAnalysisTK.jar --analysis_type IndelRealigner")
public class GATKIndelRealigner extends Module {

    @NotNull(message = "inFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "Input file is not readable", groups = InputValidations.class)
    @InputArgument(flag = "")
    private File input;

    @NotNull(message = "referenceSequence is required", groups = InputValidations.class)
    @FileIsReadable(message = "referenceSequence is not readable", groups = InputValidations.class)
    @InputArgument(flag = "")
    private File referenceSequence;

    @NotNull(message = "targetIntervals is required", groups = InputValidations.class)
    @FileIsReadable(message = "targetIntervals is not readable", groups = InputValidations.class)
    @InputArgument(flag = "")
    private File targetIntervals;

    @NotNull(message = "known is required", groups = InputValidations.class)
    @FileIsReadable(message = "known is not readable", groups = InputValidations.class)
    @InputArgument(flag = "")
    private File knownAlleles;

    @InputArgument(flag = "")
    private Boolean standardCovs = Boolean.FALSE;

    @NotNull(message = "out is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "invalid output file", groups = OutputValidations.class)
    @InputArgument(flag = "")
    private File out;

    @InputArgument(flag = "")
    private String phoneHome;

    @InputArgument(flag = "")
    private String downsamplingType;

    @Override
    public Class<?> getModuleClass() {
        return GATKIndelRealigner.class;
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
        command.append(" --input_file ").append(input.getAbsolutePath());
        command.append(" --reference_sequence ").append(referenceSequence.getAbsolutePath());
        command.append(" --targetIntervals ").append(targetIntervals.getAbsolutePath());
        command.append(" --knownAlleles ").append(knownAlleles.getAbsolutePath());
        command.append(" --out ").append(out.getAbsolutePath());

        if (StringUtils.isNotEmpty(phoneHome)) {
            command.append(" --phone_home ").append(phoneHome);
        }

        if (StringUtils.isNotEmpty(downsamplingType)) {
            command.append(" --downsampling_type ").append(downsamplingType);
        }

        if (standardCovs) {
            command.append("--standard_covs");
        }

        commandInput.setCommand(command.toString());
        CommandOutput commandOutput;
        try {
            Executor executor = BashExecutor.getInstance();
            commandOutput = executor.execute(commandInput);
            if (!commandOutput.getStdout().toString().contains("Total runtime")) {
                // commandOutput.setDescription("The stdout file seems not have a line for the \"Total runtime\"");
                commandOutput.setExitCode(-1);
            }
            if (!commandOutput.getStdout().toString().contains("reads were filtered out")) {
                // commandOutput.setDescription("The stdout file seems not have a line for the number of \"reads were
                // filtered out\"");
                commandOutput.setExitCode(-1);
            }
        } catch (ExecutorException e) {
            throw new ModuleException(e);
        }

        if (commandOutput != null && commandOutput.getExitCode() == 0) {
            FileData fileData = new FileData();
            fileData.setName(out.getName());
            fileData.setMimeType(MimeType.APPLICATION_BAM);
            getFileDatas().add(fileData);
        }

        return new ShellModuleOutput(commandOutput);
    }

    public String getPhoneHome() {
        return phoneHome;
    }

    public void setPhoneHome(String phoneHome) {
        this.phoneHome = phoneHome;
    }

    public String getDownsamplingType() {
        return downsamplingType;
    }

    public void setDownsamplingType(String downsamplingType) {
        this.downsamplingType = downsamplingType;
    }

    public File getInput() {
        return input;
    }

    public void setInput(File input) {
        this.input = input;
    }

    public File getReferenceSequence() {
        return referenceSequence;
    }

    public void setReferenceSequence(File referenceSequence) {
        this.referenceSequence = referenceSequence;
    }

    public File getKnownAlleles() {
        return knownAlleles;
    }

    public void setKnownAlleles(File knownAlleles) {
        this.knownAlleles = knownAlleles;
    }

    public Boolean getStandardCovs() {
        return standardCovs;
    }

    public void setStandardCovs(Boolean standardCovs) {
        this.standardCovs = standardCovs;
    }

    public File getTargetIntervals() {
        return targetIntervals;
    }

    public void setTargetIntervals(File targetIntervals) {
        this.targetIntervals = targetIntervals;
    }

    public File getOut() {
        return out;
    }

    public void setOut(File out) {
        this.out = out;
    }

    @Override
    public String toString() {
        return String.format(
                "GATKIndelRealigner [input=%s, referenceSequence=%s, targetIntervals=%s, knownAlleles=%s, standardCovs=%s, out=%s, phoneHome=%s, downsamplingType=%s, toString()=%s]",
                input, referenceSequence, targetIntervals, knownAlleles, standardCovs, out, phoneHome, downsamplingType,
                super.toString());
    }

}
