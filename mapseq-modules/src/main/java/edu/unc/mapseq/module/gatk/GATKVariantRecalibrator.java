package edu.unc.mapseq.module.gatk;

import java.io.File;
import java.util.List;

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
import edu.unc.mapseq.module.annotations.Executable;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "GATKVariantRecalibrator")
@Executable(value = "$JAVA_HOME/bin/java -Xmx4g -Djava.io.tmpdir=$MAPSEQ_HOME/tmp -jar $%s_GATK_HOME/GenomeAnalysisTK.jar --analysis_type VariantRecalibrator")
public class GATKVariantRecalibrator extends Module {

    @NotNull(message = "input is required", groups = InputValidations.class)
    @FileIsReadable(message = "input is not readable", groups = InputValidations.class)
    @InputArgument(flag = "--input")
    private File input;

    @NotNull(message = "referenceSequence is required", groups = InputValidations.class)
    @FileIsReadable(message = "referenceSequence is not readable", groups = InputValidations.class)
    @InputArgument(flag = "--reference_sequence")
    private File referenceSequence;

    @NotNull(message = "recalFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "recalFile is not readable", groups = OutputValidations.class)
    @InputArgument(flag = "--recal_file")
    private File recalFile;

    @InputArgument(flag = "--num_threads")
    private Integer numThreads;

    @InputArgument(flag = "--maxGaussians")
    private Integer maxGaussians;

    @InputArgument(flag = "--validation_strictness")
    private String validationStrictness;

    @InputArgument(flag = "--intervals")
    private File intervals;

    @InputArgument(flag = "--percentBadVariants")
    private Double percentBadVariants;

    @InputArgument(flag = "--use_annotation")
    private List<String> useAnnotation;

    @Override
    public ModuleOutput call() throws Exception {
        CommandInput commandInput = new CommandInput();
        StringBuilder command = new StringBuilder();
        command.append(getExecutable());
        command.append(" --input ").append(input.getAbsolutePath());
        command.append(" --reference_sequence ").append(referenceSequence.getAbsolutePath());
        command.append(" --recal_file ").append(recalFile.getAbsolutePath());
        command.append(" --tranches_file ").append(tranchesFile.getAbsolutePath());

        if (percentBadVariants != null) {
            command.append(" --percentBadVariants ").append(percentBadVariants);
        }

        if (StringUtils.isNotEmpty(phoneHome)) {
            command.append(" --phone_home ").append(phoneHome);
        }

        if (maxGaussians != null) {
            command.append(" --maxGaussians ").append(maxGaussians);
        }

        if (StringUtils.isNotEmpty(downsamplingType)) {
            command.append(" --downsampling_type ").append(downsamplingType);
        }

        if (numThreads != null && numThreads > 1) {
            command.append(" --num_threads ").append(numThreads);
        }

        if (StringUtils.isNotEmpty(validationStrictness)) {
            command.append(" --validation_strictness ").append(validationStrictness);
        }

        if (intervals != null && intervals.exists()) {
            command.append(" --intervals ").append(intervals.getAbsolutePath());
        }

        if (rscriptFile != null && rscriptFile.exists()) {
            command.append(" --rscript_file ").append(rscriptFile.getAbsolutePath());
        }

        if (useAnnotation != null && useAnnotation.size() > 0) {
            for (String value : useAnnotation) {
                command.append(" --use_annotation ").append(value);
            }
        }

        if (resource != null && resource.size() > 0) {
            for (String value : resource) {
                if (value.contains("|")) {
                    command.append(" --resource").append(value.replace('|', ' '));
                }
                if (value.contains("^")) {
                    command.append(" --resource").append(value.replace('^', ' '));
                }
            }
        }

        if (StringUtils.isNotEmpty(mode)) {
            command.append(" --mode ").append(mode);
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
        fileData.setName(recalFile.getName());
        fileData.setMimeType(MimeType.TEXT_RECALIBRATION);
        getFileDatas().add(fileData);

        fileData = new FileData();
        fileData.setName(tranchesFile.getName());
        fileData.setMimeType(MimeType.TEXT_TRANCHES);
        getFileDatas().add(fileData);

        if (rscriptFile != null && rscriptFile.exists()) {

            fileData = new FileData();
            fileData.setName(rscriptFile.getName());
            fileData.setMimeType(MimeType.APPLICATION_R);
            getFileDatas().add(fileData);

        }

        return new ShellModuleOutput(commandOutput);
    }

    @InputArgument(flag = "--resource")
    private List<String> resource;

    @InputArgument(flag = "--mode")
    private String mode;

    @NotNull(message = "tranchesFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "tranchesFile is not readable", groups = OutputValidations.class)
    @InputArgument(flag = "--tranches_file")
    private File tranchesFile;

    @OutputArgument(flag = "--rscript_file", persistFileData = true, mimeType = MimeType.APPLICATION_R)
    private File rscriptFile;

    @InputArgument(flag = "--phone_home")
    private String phoneHome;

    @InputArgument(flag = "--downsampling_type")
    private String downsamplingType;

    @Override
    public Class<?> getModuleClass() {
        return GATKVariantRecalibrator.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Executable.class).value(), getWorkflowName().toUpperCase());
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

    public File getRecalFile() {
        return recalFile;
    }

    public void setRecalFile(File recalFile) {
        this.recalFile = recalFile;
    }

    public Integer getNumThreads() {
        return numThreads;
    }

    public void setNumThreads(Integer numThreads) {
        this.numThreads = numThreads;
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

    public List<String> getUseAnnotation() {
        return useAnnotation;
    }

    public void setUseAnnotation(List<String> useAnnotation) {
        this.useAnnotation = useAnnotation;
    }

    public List<String> getResource() {
        return resource;
    }

    public void setResource(List<String> resource) {
        this.resource = resource;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public File getTranchesFile() {
        return tranchesFile;
    }

    public void setTranchesFile(File tranchesFile) {
        this.tranchesFile = tranchesFile;
    }

    public File getRscriptFile() {
        return rscriptFile;
    }

    public void setRscriptFile(File rscriptFile) {
        this.rscriptFile = rscriptFile;
    }

    public Integer getMaxGaussians() {
        return maxGaussians;
    }

    public void setMaxGaussians(Integer maxGaussians) {
        this.maxGaussians = maxGaussians;
    }

    public Double getPercentBadVariants() {
        return percentBadVariants;
    }

    public void setPercentBadVariants(Double percentBadVariants) {
        this.percentBadVariants = percentBadVariants;
    }

}
