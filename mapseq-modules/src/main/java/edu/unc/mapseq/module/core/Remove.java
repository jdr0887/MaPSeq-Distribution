package edu.unc.mapseq.module.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.io.FileUtils;

import edu.unc.mapseq.module.DefaultModuleOutput;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleException;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;

/**
 * 
 * @author jdr0887
 */
@Application(name = "Remove", isWorkflowRunIdOptional = true)
public class Remove extends Module {

    @NotNull(message = "file is required", groups = InputValidations.class)
    @InputArgument
    private List<File> file;

    public Remove() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return Remove.class;
    }

    @Override
    public ModuleOutput call() throws ModuleException {
        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();
        try {
            for (File f : file) {
                if (f.exists()) {
                    FileUtils.forceDelete(f);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            moduleOutput.setError(new StringBuilder(e.getMessage()));
            moduleOutput.setExitCode(-1);
        }
        moduleOutput.setExitCode(0);
        return moduleOutput;
    }

    public List<File> getFile() {
        return file;
    }

    public void setFile(List<File> file) {
        this.file = file;
    }

    public static void main(String[] args) {
        Remove module = new Remove();
        List<File> fileList = new ArrayList<File>();
        fileList.add(new File("/home/jdr0887/tmp", "qwer"));
        fileList.add(new File("/home/jdr0887/tmp", "asdf"));
        fileList.add(new File("/home/jdr0887/tmp", "zxcv"));
        module.setFile(fileList);
        try {
            module.call();
        } catch (ModuleException e) {
            e.printStackTrace();
        }

    }

}
