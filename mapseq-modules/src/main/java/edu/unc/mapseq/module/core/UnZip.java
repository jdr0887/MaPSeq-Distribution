package edu.unc.mapseq.module.core;

import java.io.File;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsNotEmpty;
import edu.unc.mapseq.module.constraints.FileIsReadable;

/**
 * 
 * @author jdr0887
 * 
 */
@Application(name = "UnZip", executable = "/usr/bin/unzip", isWorkflowRunIdOptional = true)
public class UnZip extends Module {

    @NotNull(message = "Zip is required", groups = InputValidations.class)
    @FileIsReadable(message = "Zip file does not exist or is not readable", groups = InputValidations.class)
    @InputArgument(order = 2)
    private File zip;

    @FileIsNotEmpty(message = "invalid output file", groups = OutputValidations.class)
    @OutputArgument(flag = "-d")
    private File extract;

    @InputArgument(flag = "-o", order = 1)
    private Boolean overwrite;

    public UnZip() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return UnZip.class;
    }

    public File getZip() {
        return zip;
    }

    public void setZip(File zip) {
        this.zip = zip;
    }

    public File getExtract() {
        return extract;
    }

    public void setExtract(File extract) {
        this.extract = extract;
    }

    public Boolean getOverwrite() {
        return overwrite;
    }

    public void setOverwrite(Boolean overwrite) {
        this.overwrite = overwrite;
    }

    public static void main(String[] args) {
        UnZip module = new UnZip();
        module.setZip(new File("/tmp", "asdf.zip"));
        module.setExtract(new File("/tmp"));
        module.setOverwrite(Boolean.TRUE);
        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
