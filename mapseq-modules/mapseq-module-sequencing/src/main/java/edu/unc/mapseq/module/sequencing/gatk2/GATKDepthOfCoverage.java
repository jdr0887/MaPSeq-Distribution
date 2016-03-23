package edu.unc.mapseq.module.sequencing.gatk2;

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
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "GATKDepthOfCoverage", executable = "$JAVA_HOME/bin/java -Xmx4g -Djava.io.tmpdir=$MAPSEQ_CLIENT_HOME/tmp -jar $%s_GATK2_HOME/GenomeAnalysisTK.jar --analysis_type DepthOfCoverage")
public class GATKDepthOfCoverage extends Module {

    @NotNull(message = "key is required", groups = InputValidations.class)
    @FileIsReadable(message = "key is not readable", groups = InputValidations.class)
    @InputArgument(flag = "")
    private File key;

    @NotNull(message = "inputFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "inputFile is not readable", groups = InputValidations.class)
    @InputArgument(flag = "")
    private File inputFile;

    @NotNull(message = "referenceSequence is required", groups = InputValidations.class)
    @FileIsReadable(message = "referenceSequence is not readable", groups = InputValidations.class)
    @InputArgument(flag = "")
    private File referenceSequence;

    @InputArgument(flag = "")
    private String phoneHome;

    @InputArgument(flag = "")
    private String downsamplingType;

    @NotNull(message = "out is required", groups = InputValidations.class)
    @InputArgument(flag = "")
    private String outputPrefix;

    @InputArgument(flag = "")
    private Boolean omitDepthOutputAtEachBase = Boolean.FALSE;

    @InputArgument(flag = "")
    private String validationStrictness;

    @InputArgument(flag = "")
    private File intervals;

    @InputArgument()
    private File workDirectory;

    @Override
    public Class<?> getModuleClass() {
        return GATKDepthOfCoverage.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(),
                getWorkflowName().toUpperCase());
    }

    @Override
    public ModuleOutput call() throws Exception {
        CommandInput commandInput = new CommandInput();
        StringBuilder command = new StringBuilder();
        command.append(getExecutable());
        command.append(" --input_file ").append(inputFile.getAbsolutePath());
        command.append(" --gatk_key ").append(key.getAbsolutePath());
        command.append(" --reference_sequence ").append(referenceSequence.getAbsolutePath());

        if (StringUtils.isNotEmpty(phoneHome)) {
            command.append(" --phone_home ").append(phoneHome);
        }

        if (StringUtils.isNotEmpty(downsamplingType)) {
            command.append(" --downsampling_type ").append(downsamplingType);
        }

        if (StringUtils.isNotEmpty(validationStrictness)) {
            command.append(" --validation_strictness ").append(validationStrictness);
        }

        if (intervals != null) {
            command.append(" --intervals ").append(intervals.getAbsolutePath());
        }

        if (omitDepthOutputAtEachBase) {
            command.append(" --omitDepthOutputAtEachBase ");
        }

        if (workDirectory != null) {
            command.append(" --out ").append(workDirectory.getAbsolutePath()).append("/").append(outputPrefix);
        } else {
            command.append(" --out ").append(System.getProperty("user.dir")).append("/").append(outputPrefix);
        }

        commandInput.setCommand(command.toString());
        CommandOutput commandOutput;
        try {
            Executor executor = BashExecutor.getInstance();
            commandOutput = executor.execute(commandInput);
        } catch (ExecutorException e) {
            throw new ModuleException(e);
        }

        FileData fileData = new FileData();
        fileData.setName(String.format("%s.sample_summary", outputPrefix));
        fileData.setMimeType(MimeType.TEXT_DEPTH_OF_COVERAGE_SUMMARY);
        getFileDatas().add(fileData);

        return new ShellModuleOutput(commandOutput);
    }

    public File getKey() {
        return key;
    }

    public void setKey(File key) {
        this.key = key;
    }

    public File getInputFile() {
        return inputFile;
    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }

    public File getReferenceSequence() {
        return referenceSequence;
    }

    public void setReferenceSequence(File referenceSequence) {
        this.referenceSequence = referenceSequence;
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

    public String getOutputPrefix() {
        return outputPrefix;
    }

    public void setOutputPrefix(String outputPrefix) {
        this.outputPrefix = outputPrefix;
    }

    public Boolean getOmitDepthOutputAtEachBase() {
        return omitDepthOutputAtEachBase;
    }

    public void setOmitDepthOutputAtEachBase(Boolean omitDepthOutputAtEachBase) {
        this.omitDepthOutputAtEachBase = omitDepthOutputAtEachBase;
    }

    public String getValidationStrictness() {
        return validationStrictness;
    }

    public void setValidationStrictness(String validationStrictness) {
        this.validationStrictness = validationStrictness;
    }

    public File getIntervals() {
        return intervals;
    }

    public void setIntervals(File intervals) {
        this.intervals = intervals;
    }

    public File getWorkDirectory() {
        return workDirectory;
    }

    public void setWorkDirectory(File workDirectory) {
        this.workDirectory = workDirectory;
    }

    @Override
    public String toString() {
        return String.format(
                "GATKDepthOfCoverage [key=%s, inputFile=%s, referenceSequence=%s, phoneHome=%s, downsamplingType=%s, outputPrefix=%s, omitDepthOutputAtEachBase=%s, validationStrictness=%s, intervals=%s, workDirectory=%s, toString()=%s]",
                key, inputFile, referenceSequence, phoneHome, downsamplingType, outputPrefix, omitDepthOutputAtEachBase,
                validationStrictness, intervals, workDirectory, super.toString());
    }

}
