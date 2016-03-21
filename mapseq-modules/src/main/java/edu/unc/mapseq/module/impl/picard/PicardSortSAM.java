package edu.unc.mapseq.module.impl.picard;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import net.sf.picard.sam.SortSam;
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

@Application(name = "PicardSortSAM")
public class PicardSortSAM extends Module {

    @NotNull(message = "Input is required", groups = InputValidations.class)
    @FileIsReadable(message = "input file is not readable", groups = InputValidations.class)
    @FileIsNotEmpty(message = "input file is empty", groups = InputValidations.class)
    @InputArgument(description = "The BAM or SAM file to sort.")
    private File input;

    @NotNull(message = "Output is required", groups = InputValidations.class)
    @FileIsReadable(message = "output file is not readable", groups = OutputValidations.class)
    @FileIsNotEmpty(message = "output file is empty", groups = OutputValidations.class)
    @OutputArgument(persistFileData = true, mimeType = MimeType.APPLICATION_BAM)
    private File output;

    @NotNull(message = "Output is required", groups = InputValidations.class)
    @Contains(values = { "unsorted", "queryname", "coordinate" })
    @InputArgument(description = "The sorted BAM or SAM output file.")
    private String sortOrder;

    @InputArgument
    private Integer maxRecordsInRAM = 1000000;

    public PicardSortSAM() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return PicardSortSAM.class;
    }

    @Override
    public ModuleOutput call() throws ModuleException {

        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();

        int exitCode = 0;
        try {

            List<String> argumentList = new ArrayList<String>();
            argumentList.add(String.format("MAX_RECORDS_IN_RAM=%d", maxRecordsInRAM));
            argumentList.add("VALIDATION_STRINGENCY=SILENT");
            argumentList.add("SORT_ORDER=" + sortOrder);
            argumentList.add(String.format("TMP_DIR=%s/tmp", System.getenv("MAPSEQ_CLIENT_HOME")));
            argumentList.add("OUTPUT=" + output.getAbsolutePath());
            argumentList.add("INPUT=" + input.getAbsolutePath());

            exitCode = new SortSam().instanceMain(argumentList.toArray(new String[argumentList.size()]));
        } catch (Exception e) {
            e.printStackTrace();
            moduleOutput.setError(new StringBuilder(e.getMessage()));
            moduleOutput.setExitCode(-1);
            return moduleOutput;
        }
        moduleOutput.setExitCode(exitCode);

        FileData fm = new FileData();
        fm.setName(output.getName());
        fm.setPath(output.getParentFile().getAbsolutePath());
        fm.setMimeType(MimeType.APPLICATION_BAM);
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

    @Override
    public String toString() {
        return String.format("PicardSortSAM [input=%s, output=%s, sortOrder=%s, maxRecordsInRAM=%s, toString()=%s]",
                input, output, sortOrder, maxRecordsInRAM, super.toString());
    }

}
