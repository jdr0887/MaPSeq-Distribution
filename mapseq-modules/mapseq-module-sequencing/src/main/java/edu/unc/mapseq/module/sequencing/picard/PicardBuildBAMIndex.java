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
import edu.unc.mapseq.module.constraints.FileIsNotEmpty;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "PicardBuildBAMIndex", executable = "$JAVA7_HOME/bin/java -Xmx4g -Djava.io.tmpdir=$MAPSEQ_CLIENT_HOME/tmp -jar $%s_PICARD_HOME/picard.jar BuildBAMIndex %s")
public class PicardBuildBAMIndex extends Module {

    @NotNull(message = "Input is required", groups = InputValidations.class)
    @FileIsReadable(message = "input file is not readable", groups = InputValidations.class)
    @FileIsNotEmpty(message = "input file is empty", groups = InputValidations.class)
    @InputArgument
    private File input;

    @NotNull(message = "Output is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "output file is empty", groups = OutputValidations.class)
    @OutputArgument(persistFileData = true, mimeType = MimeType.APPLICATION_BAM_INDEX)
    private File output;

    @InputArgument
    private Integer maxRecordsInRAM = 1000000;

    public PicardBuildBAMIndex() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return PicardBuildBAMIndex.class;
    }

    @Override
    public String getExecutable() {
        List<String> argumentList = new ArrayList<String>();
        argumentList.add(String.format("MAX_RECORDS_IN_RAM=%d", maxRecordsInRAM));
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

    @Override
    public String toString() {
        return String.format("PicardBuildBAMIndex [input=%s, output=%s, maxRecordsInRAM=%s, toString()=%s]", input, output, maxRecordsInRAM,
                super.toString());
    }

}
