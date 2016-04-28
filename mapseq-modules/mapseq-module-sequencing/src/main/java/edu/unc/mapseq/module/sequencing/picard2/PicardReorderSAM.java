package edu.unc.mapseq.module.sequencing.picard2;

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
import edu.unc.mapseq.module.constraints.FileIsNotEmpty;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "PicardReorderSAM", executable = "$JAVA8_HOME/bin/java -Xmx4g -Djava.io.tmpdir=$MAPSEQ_CLIENT_HOME/tmp -jar $%s_PICARD2_HOME/picard.jar ReorderSam %s")
public class PicardReorderSAM extends Module {

    @NotNull(message = "Input is required", groups = InputValidations.class)
    @FileIsReadable(message = "input file is not readable", groups = InputValidations.class)
    @FileIsNotEmpty(message = "input file is empty", groups = InputValidations.class)
    @InputArgument
    private File input;

    @NotNull(message = "Output is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "output file is empty", groups = OutputValidations.class)
    @OutputArgument(persistFileData = true, mimeType = MimeType.APPLICATION_BAM)
    private File output;

    @NotNull(message = "referenceSequence is required", groups = InputValidations.class)
    @FileIsReadable(message = "Invalid referenceSequence file", groups = InputValidations.class)
    @InputArgument
    private File referenceSequence;

    @InputArgument
    private Integer maxRecordsInRAM = 1000000;

    @InputArgument
    private Boolean createIndex = Boolean.FALSE;

    public PicardReorderSAM() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return PicardReorderSAM.class;
    }

    @Override
    public String getExecutable() {
        List<String> argumentList = new ArrayList<String>();
        argumentList.add(String.format("MAX_RECORDS_IN_RAM=%d", maxRecordsInRAM));
        argumentList.add("VALIDATION_STRINGENCY=SILENT");
        argumentList.add(String.format("CREATE_INDEX=%s", createIndex.toString()));
        argumentList.add(String.format("TMP_DIR=%s/tmp", System.getenv("MAPSEQ_CLIENT_HOME")));
        argumentList.add(String.format("REFERENCE=%s", referenceSequence.getAbsolutePath()));
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

    public File getReferenceSequence() {
        return referenceSequence;
    }

    public void setReferenceSequence(File referenceSequence) {
        this.referenceSequence = referenceSequence;
    }

    public Boolean getCreateIndex() {
        return createIndex;
    }

    public void setCreateIndex(Boolean createIndex) {
        this.createIndex = createIndex;
    }

    @Override
    public String toString() {
        return String.format(
                "PicardReorderSAM [input=%s, output=%s, referenceSequence=%s, maxRecordsInRAM=%s, createIndex=%s, toString()=%s]", input,
                output, referenceSequence, maxRecordsInRAM, createIndex, super.toString());
    }

}
