package edu.unc.mapseq.module.fastx;

import java.io.File;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.Executable;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsNotEmpty;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "FastqQualityTrimmer")
@Executable(value = "$%s_FASTX_TOOLKIT_HOME/bin/fastx_clipper")
public class FastxClipper extends Module {

    @FileIsReadable(message = "inFile is not readable", groups = InputValidations.class)
    @FileIsNotEmpty(message = "inFile is empty", groups = InputValidations.class)
    @NotNull(message = "inFile is required", groups = InputValidations.class)
    @InputArgument(flag = "-i")
    private File inFile;

    @FileIsNotEmpty(message = "outFile is empty", groups = OutputValidations.class)
    @NotNull(message = "outFile is required", groups = InputValidations.class)
    @OutputArgument(flag = "-o", persistFileData = true, mimeType = MimeType.TEXT_KEY_VALUE)
    private File outFile;

    public FastxClipper() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return FastxClipper.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Executable.class).value(), getWorkflowName().toUpperCase());
    }

    public File getInFile() {
        return inFile;
    }

    public void setInFile(File inFile) {
        this.inFile = inFile;
    }

    public File getOutFile() {
        return outFile;
    }

    public void setOutFile(File outFile) {
        this.outFile = outFile;
    }

}
