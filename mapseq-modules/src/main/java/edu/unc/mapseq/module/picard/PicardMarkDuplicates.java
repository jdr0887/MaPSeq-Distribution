package edu.unc.mapseq.module.picard;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import net.sf.picard.sam.MarkDuplicates;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.module.DefaultModuleOutput;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleException;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsNotEmpty;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "PicardMarkDuplicates")
public class PicardMarkDuplicates extends Module {

    @NotNull(message = "Input is required", groups = InputValidations.class)
    @FileIsReadable(message = "input file is not readable", groups = InputValidations.class)
    @FileIsNotEmpty(message = "input file is empty", groups = InputValidations.class)
    @InputArgument
    private File input;

    @NotNull(message = "Output is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "output file is empty", groups = OutputValidations.class)
    @InputArgument
    private File output;

    @NotNull(message = "metricsFile is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "metricsFile file is empty", groups = OutputValidations.class)
    @InputArgument
    private File metricsFile;

    public PicardMarkDuplicates() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return PicardMarkDuplicates.class;
    }

    @Override
    public ModuleOutput call() throws ModuleException {

        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();

        int exitCode = 0;
        try {

            List<String> argumentList = new ArrayList<String>();

            argumentList.add("VALIDATION_STRINGENCY=SILENT");
            argumentList.add("REMOVE_DUPLICATES=true");
            argumentList.add(String.format("TMP_DIR=%s/tmp", System.getenv("MAPSEQ_HOME")));
            argumentList.add("OUTPUT=" + output.getAbsolutePath());
            argumentList.add("INPUT=" + input.getAbsolutePath());
            argumentList.add("METRICS_FILE=" + metricsFile.getAbsolutePath());

            exitCode = new MarkDuplicates().instanceMain(argumentList.toArray(new String[argumentList.size()]));
        } catch (Exception e) {
            e.printStackTrace();
            moduleOutput.setError(new StringBuilder(e.getMessage()));
            moduleOutput.setExitCode(-1);
            return moduleOutput;
        }
        moduleOutput.setExitCode(exitCode);

        FileData fm = new FileData();
        fm.setName(output.getName());
        fm.setMimeType(MimeType.APPLICATION_BAM);
        getFileDatas().add(fm);

        fm = new FileData();
        fm.setName(metricsFile.getName());
        fm.setMimeType(MimeType.PICARD_MARK_DUPLICATE_METRICS);
        getFileDatas().add(fm);

        return moduleOutput;

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

}
