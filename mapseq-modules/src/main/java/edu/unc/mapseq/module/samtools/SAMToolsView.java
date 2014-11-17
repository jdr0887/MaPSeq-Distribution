package edu.unc.mapseq.module.samtools;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.io.FileUtils;
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
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsNotEmpty;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "SAMToolsView")
@Executable(value = "SAMTOOLS_HOME/bin/samtools view")
public class SAMToolsView extends Module {

    @NotNull(message = "Input is required", groups = InputValidations.class)
    @FileIsReadable(message = "Invalid input file", groups = InputValidations.class)
    @FileIsNotEmpty(message = "input is empty", groups = InputValidations.class)
    @InputArgument(description = "The BAM file.")
    private File input;

    @NotNull(message = "Output is required", groups = InputValidations.class)
    @FileIsReadable(message = "Invalid output file", groups = OutputValidations.class)
    @FileIsNotEmpty(message = "output is empty", groups = OutputValidations.class)
    @InputArgument(description = "The pileup output file.")
    private File output;

    @InputArgument
    private Boolean includeHeader;

    @InputArgument
    private Boolean outputHeaderOnly;

    @InputArgument
    private Boolean bamFormat;

    @InputArgument
    private Boolean samInputFormat;

    @InputArgument
    private Integer outputAlignmentsWithBitsPresentInFlag;

    @InputArgument
    private Integer skipAlignmentsWithBitsPresentInFlag;

    @InputArgument
    private String onlyOutputReadsInLibrary;

    @InputArgument
    private Integer skipAlignmentsWithMAPQ;

    @InputArgument
    private String onlyOutputReadsInReadGroup;

    @InputArgument
    private File outputReadsInReadGroups;

    @InputArgument
    private File regionsFile;

    @InputArgument
    private File referenceSequence;

    public SAMToolsView() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return SAMToolsView.class;
    }

    @Override
    public ModuleOutput call() throws ModuleException {

        CommandInput commandInput = new CommandInput();
        StringBuilder command = new StringBuilder();
        command.append(String.format("$%s_%s", getWorkflowName().toUpperCase(),
                getModuleClass().getAnnotation(Executable.class).value()));

        if (this.includeHeader != null && this.includeHeader) {
            command.append(" -h");
        }

        if (this.outputHeaderOnly != null && outputHeaderOnly) {
            command.append(" -H");
        }

        if (this.bamFormat != null && this.bamFormat) {
            command.append(" -b");
        }

        if (this.samInputFormat != null && this.samInputFormat) {
            command.append(" -S");
        }

        if (outputAlignmentsWithBitsPresentInFlag != null) {
            command.append(" -f ").append(outputAlignmentsWithBitsPresentInFlag.toString());
        }

        if (skipAlignmentsWithBitsPresentInFlag != null) {
            command.append(" -F ").append(skipAlignmentsWithBitsPresentInFlag.toString());
        }

        if (StringUtils.isNotEmpty(onlyOutputReadsInLibrary)) {
            command.append(" -l ").append(onlyOutputReadsInLibrary);
        }

        if (skipAlignmentsWithMAPQ != null) {
            command.append(" -q ").append(skipAlignmentsWithMAPQ.toString());
        }

        if (StringUtils.isNotEmpty(onlyOutputReadsInReadGroup)) {
            command.append(" -r ").append(onlyOutputReadsInReadGroup);
        }

        if (outputReadsInReadGroups != null && outputReadsInReadGroups.exists()) {
            command.append(" -R ").append(outputReadsInReadGroups.getAbsolutePath());
        }

        if (referenceSequence != null && referenceSequence.exists()) {
            command.append(" -T ").append(referenceSequence.getAbsolutePath());
        }

        command.append(" -o ").append(output.getAbsolutePath());

        command.append(" ").append(input.getAbsolutePath());

        if (regionsFile != null && regionsFile.exists()) {
            try {
                List<String> lines = FileUtils.readLines(regionsFile);
                for (String region : lines) {
                    command.append(" ").append(region);
                }
            } catch (IOException e) {
                throw new ModuleException(e);
            }
        }

        commandInput.setCommand(command.toString());
        CommandOutput commandOutput;
        try {
            Executor executor = BashExecutor.getInstance();
            commandOutput = executor.execute(commandInput);
        } catch (ExecutorException e) {
            throw new ModuleException(e);
        }
        FileData fm = new FileData();
        fm.setName(output.getName());
        fm.setMimeType(MimeType.APPLICATION_BAM);
        getFileDatas().add(fm);

        return new ShellModuleOutput(commandOutput);
    }

    public Boolean getSamInputFormat() {
        return samInputFormat;
    }

    public void setSamInputFormat(Boolean samInputFormat) {
        this.samInputFormat = samInputFormat;
    }

    public File getReferenceSequence() {
        return referenceSequence;
    }

    public void setReferenceSequence(File referenceSequence) {
        this.referenceSequence = referenceSequence;
    }

    public File getInput() {
        return input;
    }

    public void setInput(File input) {
        this.input = input;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public Boolean getIncludeHeader() {
        return includeHeader;
    }

    public void setIncludeHeader(Boolean includeHeader) {
        this.includeHeader = includeHeader;
    }

    public Boolean getOutputHeaderOnly() {
        return outputHeaderOnly;
    }

    public void setOutputHeaderOnly(Boolean outputHeaderOnly) {
        this.outputHeaderOnly = outputHeaderOnly;
    }

    public Boolean getBamFormat() {
        return bamFormat;
    }

    public void setBamFormat(Boolean bamFormat) {
        this.bamFormat = bamFormat;
    }

    public Integer getOutputAlignmentsWithBitsPresentInFlag() {
        return outputAlignmentsWithBitsPresentInFlag;
    }

    public void setOutputAlignmentsWithBitsPresentInFlag(Integer outputAlignmentsWithBitsPresentInFlag) {
        this.outputAlignmentsWithBitsPresentInFlag = outputAlignmentsWithBitsPresentInFlag;
    }

    public Integer getSkipAlignmentsWithBitsPresentInFlag() {
        return skipAlignmentsWithBitsPresentInFlag;
    }

    public void setSkipAlignmentsWithBitsPresentInFlag(Integer skipAlignmentsWithBitsPresentInFlag) {
        this.skipAlignmentsWithBitsPresentInFlag = skipAlignmentsWithBitsPresentInFlag;
    }

    public String getOnlyOutputReadsInLibrary() {
        return onlyOutputReadsInLibrary;
    }

    public void setOnlyOutputReadsInLibrary(String onlyOutputReadsInLibrary) {
        this.onlyOutputReadsInLibrary = onlyOutputReadsInLibrary;
    }

    public Integer getSkipAlignmentsWithMAPQ() {
        return skipAlignmentsWithMAPQ;
    }

    public void setSkipAlignmentsWithMAPQ(Integer skipAlignmentsWithMAPQ) {
        this.skipAlignmentsWithMAPQ = skipAlignmentsWithMAPQ;
    }

    public String getOnlyOutputReadsInReadGroup() {
        return onlyOutputReadsInReadGroup;
    }

    public void setOnlyOutputReadsInReadGroup(String onlyOutputReadsInReadGroup) {
        this.onlyOutputReadsInReadGroup = onlyOutputReadsInReadGroup;
    }

    public File getOutputReadsInReadGroups() {
        return outputReadsInReadGroups;
    }

    public void setOutputReadsInReadGroups(File outputReadsInReadGroups) {
        this.outputReadsInReadGroups = outputReadsInReadGroups;
    }

    public File getRegionsFile() {
        return regionsFile;
    }

    public void setRegionsFile(File regionsFile) {
        this.regionsFile = regionsFile;
    }

}
