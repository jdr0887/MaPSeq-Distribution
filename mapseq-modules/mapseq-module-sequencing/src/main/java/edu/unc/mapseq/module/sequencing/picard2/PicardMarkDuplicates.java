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

@Application(name = "PicardMarkDuplicates", executable = "$JAVA8_HOME/bin/java -Xmx4g -Djava.io.tmpdir=$MAPSEQ_CLIENT_HOME/tmp -jar $%s_PICARD2_HOME/picard.jar MarkDuplicates %s")
public class PicardMarkDuplicates extends Module {

    @NotNull(message = "Input is required", groups = InputValidations.class)
    @FileIsReadable(message = "input file is not readable", groups = InputValidations.class)
    @FileIsNotEmpty(message = "input file is empty", groups = InputValidations.class)
    @InputArgument
    private File input;

    @NotNull(message = "Output is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "output file is empty", groups = OutputValidations.class)
    @OutputArgument(persistFileData = true, mimeType = MimeType.APPLICATION_BAM)
    private File output;

    @NotNull(message = "metricsFile is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "metricsFile file is empty", groups = OutputValidations.class)
    @OutputArgument(persistFileData = true, mimeType = MimeType.PICARD_MARK_DUPLICATE_METRICS)
    private File metricsFile;

    @InputArgument
    private Integer maxRecordsInRAM = 1000000;

    public PicardMarkDuplicates() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return PicardMarkDuplicates.class;
    }

    @Override
    public String getExecutable() {
        List<String> argumentList = new ArrayList<String>();
        argumentList.add(String.format("MAX_RECORDS_IN_RAM=%d", maxRecordsInRAM));
        argumentList.add("VALIDATION_STRINGENCY=SILENT");
        argumentList.add("REMOVE_DUPLICATES=true");
        argumentList.add(String.format("TMP_DIR=%s/tmp", System.getenv("MAPSEQ_CLIENT_HOME")));
        argumentList.add(String.format("OUTPUT=%s", output.getAbsolutePath()));
        argumentList.add(String.format("INPUT=%s", input.getAbsolutePath()));
        argumentList.add(String.format("METRICS_FILE=%s", metricsFile.getAbsolutePath()));
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

    public File getMetricsFile() {
        return metricsFile;
    }

    public void setMetricsFile(File metricsFile) {
        this.metricsFile = metricsFile;
    }

    @Override
    public String toString() {
        return String.format("PicardMarkDuplicates [input=%s, output=%s, metricsFile=%s, maxRecordsInRAM=%s, toString()=%s]", input, output,
                metricsFile, maxRecordsInRAM, super.toString());
    }

}
