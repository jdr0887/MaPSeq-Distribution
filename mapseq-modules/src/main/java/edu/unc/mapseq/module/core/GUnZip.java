package edu.unc.mapseq.module.core;

import java.io.File;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.constraints.FileIsReadable;

/**
 * 
 * @author jdr0887
 * 
 */
@Application(name = "GUnZip", executable = "/bin/gunzip", isWorkflowRunIdOptional = true)
public class GUnZip extends Module {

    @NotNull(message = "gzFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "gzFile does not exist or is not readable", groups = InputValidations.class)
    @InputArgument(flag = "-c")
    private File gzFile;

    @NotNull(message = "extract file is required", groups = InputValidations.class)
    @OutputArgument(redirect = true)
    private File extractFile;

    public GUnZip() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return GUnZip.class;
    }

    public File getGzFile() {
        return gzFile;
    }

    public void setGzFile(File gzFile) {
        this.gzFile = gzFile;
    }

    public File getExtractFile() {
        return extractFile;
    }

    public void setExtractFile(File extractFile) {
        this.extractFile = extractFile;
    }

    public static void main(String[] args) {
        GUnZip module = new GUnZip();
        module.setGzFile(new File("/tmp", "gzipFile.gz"));
        module.setExtractFile(new File("/tmp"));
        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
