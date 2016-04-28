package edu.unc.mapseq.module.sequencing.picard;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;

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

@Application(name = "PicardAddOrReplaceReadGroups", executable = "$JAVA7_HOME/bin/java -Xmx4g -Djava.io.tmpdir=$MAPSEQ_CLIENT_HOME/tmp -jar $%s_PICARD_HOME/picard.jar AddOrReplaceReadGroups %s")
public class PicardAddOrReplaceReadGroups extends Module {

    @NotNull(message = "Input is required", groups = InputValidations.class)
    @FileIsReadable(message = "input file is not readable", groups = InputValidations.class)
    @FileIsNotEmpty(message = "input file is empty", groups = InputValidations.class)
    @InputArgument
    private File input;

    @NotNull(message = "Output is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "output file is empty", groups = OutputValidations.class)
    @OutputArgument(persistFileData = true, mimeType = MimeType.APPLICATION_BAM)
    private File output;

    @NotNull(message = "sortOrder is required", groups = InputValidations.class)
    @Contains(values = { "unsorted", "queryname", "coordinate" })
    @InputArgument
    private String sortOrder;

    @NotNull(message = "readGroupId is required", groups = InputValidations.class)
    @InputArgument
    private String readGroupId;

    @NotNull(message = "readGroupLibrary is required", groups = InputValidations.class)
    @InputArgument
    private String readGroupLibrary;

    @NotNull(message = "readGroupPlatform is required", groups = InputValidations.class)
    @InputArgument
    private String readGroupPlatform;

    @NotNull(message = "readGroupPlatformUnit is required", groups = InputValidations.class)
    @InputArgument
    private String readGroupPlatformUnit;

    @NotNull(message = "readGroupSampleName is required", groups = InputValidations.class)
    @InputArgument
    private String readGroupSampleName;

    @InputArgument
    private String readGroupCenterName;

    @InputArgument
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
        List<String> argumentList = new ArrayList<String>();
        argumentList.add("VALIDATION_STRINGENCY=SILENT");
        argumentList.add(String.format("SORT_ORDER=%s", this.sortOrder));
        argumentList.add(String.format("MAX_RECORDS_IN_RAM=%d", maxRecordsInRAM));
        argumentList.add(String.format("TMP_DIR=%s/tmp", System.getenv("MAPSEQ_CLIENT_HOME")));
        argumentList.add(String.format("RGID='%s'", this.readGroupId));
        argumentList.add(String.format("RGLB='%s'", this.readGroupLibrary));
        argumentList.add(String.format("RGPL='%s'", this.readGroupPlatform));
        argumentList.add(String.format("RGPU='%s'", this.readGroupPlatformUnit));
        argumentList.add(String.format("RGSM='%s'", this.readGroupSampleName));
        if (StringUtils.isNotEmpty(readGroupCenterName)) {
            argumentList.add(String.format("RGCN='%s'", this.readGroupCenterName));
        }
        argumentList.add("RGDS=GENERATED_BY_MAPSEQ");
        argumentList.add(String.format("OUTPUT=%s", output.getAbsolutePath()));
        argumentList.add(String.format("INPUT=%s", input.getAbsolutePath()));
        String args = StringUtils.join(argumentList, " ");
        return String.format(getModuleClass().getAnnotation(Application.class).executable(), getWorkflowName().toUpperCase(), args);
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

}
