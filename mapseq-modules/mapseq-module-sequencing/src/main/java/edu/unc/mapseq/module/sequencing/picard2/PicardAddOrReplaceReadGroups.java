package edu.unc.mapseq.module.sequencing.picard2;

import java.io.File;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.Contains;
import edu.unc.mapseq.module.constraints.FileIsNotEmpty;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "PicardAddOrReplaceReadGroups", executable = "$JAVA8_HOME/bin/java -Xmx4g -jar $%s_PICARD2_HOME/picard.jar AddOrReplaceReadGroups TMP_DIR=$MAPSEQ_CLIENT_HOME/tmp VALIDATION_STRINGENCY=SILENT RGDS=GENERATED_BY_MAPSEQ")
public class PicardAddOrReplaceReadGroups extends Module {

    @NotNull(message = "Input is required", groups = InputValidations.class)
    @FileIsReadable(message = "input file is not readable", groups = InputValidations.class)
    @FileIsNotEmpty(message = "input file is empty", groups = InputValidations.class)
    @InputArgument(flag = "INPUT", delimiter = "=")
    private File input;

    @NotNull(message = "Output is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "output file is empty", groups = OutputValidations.class)
    @OutputArgument(flag = "OUTPUT", delimiter = "=", persistFileData = true, mimeType = MimeType.APPLICATION_BAM)
    private File output;

    @NotNull(message = "sortOrder is required", groups = InputValidations.class)
    @Contains(values = { "unsorted", "queryname", "coordinate" })
    @InputArgument(flag = "SORT_ORDER", delimiter = "=")
    private String sortOrder;

    @NotNull(message = "readGroupId is required", groups = InputValidations.class)
    @InputArgument(flag = "RGID", delimiter = "=", wrapValueInSingleQuotes = true)
    private String readGroupId;

    @NotNull(message = "readGroupLibrary is required", groups = InputValidations.class)
    @InputArgument(flag = "RGLB", delimiter = "=", wrapValueInSingleQuotes = true)
    private String readGroupLibrary;

    @NotNull(message = "readGroupPlatform is required", groups = InputValidations.class)
    @InputArgument(flag = "RGPL", delimiter = "=", wrapValueInSingleQuotes = true)
    private String readGroupPlatform;

    @NotNull(message = "readGroupPlatformUnit is required", groups = InputValidations.class)
    @InputArgument(flag = "RGPU", delimiter = "=", wrapValueInSingleQuotes = true)
    private String readGroupPlatformUnit;

    @NotNull(message = "readGroupSampleName is required", groups = InputValidations.class)
    @InputArgument(flag = "RGSM", delimiter = "=", wrapValueInSingleQuotes = true)
    private String readGroupSampleName;

    @InputArgument(flag = "RGCN", delimiter = "=", wrapValueInSingleQuotes = true)
    private String readGroupCenterName;

    @InputArgument(flag = "MAX_RECORDS_IN_RAM", delimiter = "=")
    private Integer maxRecordsInRAM = 1000000;

    public PicardAddOrReplaceReadGroups() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return PicardAddOrReplaceReadGroups.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(), getWorkflowName().toUpperCase());
    }

    public Integer getMaxRecordsInRAM() {
        return maxRecordsInRAM;
    }

    public void setMaxRecordsInRAM(Integer maxRecordsInRAM) {
        this.maxRecordsInRAM = maxRecordsInRAM;
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

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getReadGroupId() {
        return readGroupId;
    }

    public void setReadGroupId(String readGroupId) {
        this.readGroupId = readGroupId;
    }

    public String getReadGroupLibrary() {
        return readGroupLibrary;
    }

    public void setReadGroupLibrary(String readGroupLibrary) {
        this.readGroupLibrary = readGroupLibrary;
    }

    public String getReadGroupPlatform() {
        return readGroupPlatform;
    }

    public void setReadGroupPlatform(String readGroupPlatform) {
        this.readGroupPlatform = readGroupPlatform;
    }

    public String getReadGroupPlatformUnit() {
        return readGroupPlatformUnit;
    }

    public void setReadGroupPlatformUnit(String readGroupPlatformUnit) {
        this.readGroupPlatformUnit = readGroupPlatformUnit;
    }

    public String getReadGroupSampleName() {
        return readGroupSampleName;
    }

    public void setReadGroupSampleName(String readGroupSampleName) {
        this.readGroupSampleName = readGroupSampleName;
    }

    public String getReadGroupCenterName() {
        return readGroupCenterName;
    }

    public void setReadGroupCenterName(String readGroupCenterName) {
        this.readGroupCenterName = readGroupCenterName;
    }

    @Override
    public String toString() {
        return String.format(
                "PicardAddOrReplaceReadGroups [input=%s, output=%s, sortOrder=%s, readGroupId=%s, readGroupLibrary=%s, readGroupPlatform=%s, readGroupPlatformUnit=%s, readGroupSampleName=%s, readGroupCenterName=%s, maxRecordsInRAM=%s]",
                input, output, sortOrder, readGroupId, readGroupLibrary, readGroupPlatform, readGroupPlatformUnit, readGroupSampleName,
                readGroupCenterName, maxRecordsInRAM);
    }

    public static void main(String[] args) {
        PicardAddOrReplaceReadGroups module = new PicardAddOrReplaceReadGroups();
        module.setWorkflowName("TEST");
        module.setReadGroupId("151123_UNC16-SN851_0629_AH5FG2ADXX-AAACAT_L002");
        module.setReadGroupLibrary("H5FG2ADXX_NCG_00731_L2_AAACAT");
        module.setReadGroupPlatform("Illumina HiSeq 2000");
        module.setReadGroupPlatformUnit("Illumina HiSeq 2000");
        module.setReadGroupSampleName("H5FG2ADXX NCG_00731_L2_AAACAT");
        module.setReadGroupCenterName("UNC");
        module.setSortOrder("coordinate");
        module.setInput(new File("/tmp", "input.sam"));
        module.setOutput(new File("/tmp", "output.bam"));
        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
