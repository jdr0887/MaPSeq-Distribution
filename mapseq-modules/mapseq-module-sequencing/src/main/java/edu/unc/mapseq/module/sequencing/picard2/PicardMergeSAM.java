package edu.unc.mapseq.module.sequencing.picard2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
import edu.unc.mapseq.module.constraints.FileListIsReadable;

@Application(name = "PicardMergeSAM", executable = "$JAVA8_HOME/bin/java -Xmx4g -jar $%s_PICARD2_HOME/picard.jar MergeSamFiles TMP_DIR=$MAPSEQ_CLIENT_HOME/tmp VALIDATION_STRINGENCY=SILENT")
public class PicardMergeSAM extends Module {

    @NotNull(message = "Input is required", groups = InputValidations.class)
    @InputArgument(flag = "INPUT", delimiter = "=", description = "The BAM or SAM file to parse.")
    @FileListIsReadable(message = "input file is not readable", groups = InputValidations.class)
    private List<File> input;

    @NotNull(message = "Output is required", groups = InputValidations.class)
    @FileIsReadable(message = "Invalid output file", groups = OutputValidations.class)
    @FileIsNotEmpty(message = "output file is empty", groups = OutputValidations.class)
    @OutputArgument(flag = "OUTPUT", delimiter = "=", persistFileData = true, mimeType = MimeType.APPLICATION_BAM)
    private File output;

    @NotNull(message = "sortOrder is required", groups = InputValidations.class)
    @Contains(values = { "unsorted", "queryname", "coordinate" })
    @InputArgument(flag = "SORT_ORDER", delimiter = "=")
    private String sortOrder;

    @InputArgument(flag = "MAX_RECORDS_IN_RAM", delimiter = "=")
    private Integer maxRecordsInRAM = 1000000;

    public PicardMergeSAM() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return PicardMergeSAM.class;
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

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public List<File> getInput() {
        return input;
    }

    public void setInput(List<File> input) {
        this.input = input;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public String toString() {
        return String.format("PicardMergeSAM [input=%s, output=%s, sortOrder=%s, maxRecordsInRAM=%s, toString()=%s]", input, output,
                sortOrder, maxRecordsInRAM, super.toString());
    }

    public static void main(String[] args) {
        PicardMergeSAM module = new PicardMergeSAM();
        module.setWorkflowName("TEST");
        List<File> inputList = new ArrayList<File>();
        inputList.add(new File("/tmp", "1.bam"));
        inputList.add(new File("/tmp", "2.bam"));
        module.setInput(inputList);
        module.setOutput(new File("/tmp", "asdf.bam"));
        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
