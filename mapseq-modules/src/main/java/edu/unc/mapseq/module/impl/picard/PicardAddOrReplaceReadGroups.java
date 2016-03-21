package edu.unc.mapseq.module.impl.picard;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import net.sf.picard.sam.AddOrReplaceReadGroups;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.module.DefaultModuleOutput;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleException;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.Contains;
import edu.unc.mapseq.module.constraints.FileIsNotEmpty;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "PicardAddOrReplaceReadGroups")
public class PicardAddOrReplaceReadGroups extends Module {

    private final Logger logger = LoggerFactory.getLogger(PicardAddOrReplaceReadGroups.class);

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
    public ModuleOutput call() throws ModuleException {

        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();
        int exitCode = 0;
        try {

            List<String> argumentList = new ArrayList<String>();

            argumentList.add("VALIDATION_STRINGENCY=SILENT");
            argumentList.add(String.format("SORT_ORDER=%s", this.sortOrder));
            argumentList.add(String.format("MAX_RECORDS_IN_RAM=%d", maxRecordsInRAM));
            argumentList.add(String.format("TMP_DIR=%s/tmp", System.getenv("MAPSEQ_CLIENT_HOME")));
            argumentList.add(String.format("RGID=%s", this.readGroupId));
            argumentList.add(String.format("RGLB=%s", this.readGroupLibrary));
            argumentList.add(String.format("RGPL=%s", this.readGroupPlatform));
            argumentList.add(String.format("RGPU=%s", this.readGroupPlatformUnit));
            argumentList.add(String.format("RGSM=%s", this.readGroupSampleName));
            if (StringUtils.isNotEmpty(readGroupCenterName)) {
                argumentList.add(String.format("RGCN=%s", this.readGroupCenterName));
            }
            argumentList.add("RGDS=GENERATED_BY_MAPSEQ");
            argumentList.add("OUTPUT=" + output.getAbsolutePath());
            argumentList.add("INPUT=" + input.getAbsolutePath());

            exitCode = new AddOrReplaceReadGroups().instanceMain(argumentList.toArray(new String[argumentList.size()]));
        } catch (Exception e) {
            logger.error("PicardAddOrReplaceReadGroups Error", e);
            moduleOutput.setError(new StringBuilder(e.getMessage()));
            moduleOutput.setExitCode(-1);
            return moduleOutput;
        }
        moduleOutput.setExitCode(exitCode);

        FileData fm = new FileData();
        if (output.getName().endsWith(".bam")) {
            fm.setMimeType(MimeType.APPLICATION_BAM);
        } else {
            fm.setMimeType(MimeType.TEXT_SAM);
        }
        fm.setPath(output.getParentFile().getAbsolutePath());
        fm.setName(output.getName());
        getFileDatas().add(fm);

        return moduleOutput;
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
                "PicardAddOrReplaceReadGroups [logger=%s, input=%s, output=%s, sortOrder=%s, readGroupId=%s, readGroupLibrary=%s, readGroupPlatform=%s, readGroupPlatformUnit=%s, readGroupSampleName=%s, readGroupCenterName=%s, maxRecordsInRAM=%s, toString()=%s]",
                logger, input, output, sortOrder, readGroupId, readGroupLibrary, readGroupPlatform,
                readGroupPlatformUnit, readGroupSampleName, readGroupCenterName, maxRecordsInRAM, super.toString());
    }

}
