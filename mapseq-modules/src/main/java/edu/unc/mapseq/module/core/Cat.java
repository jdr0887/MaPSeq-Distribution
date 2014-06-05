package edu.unc.mapseq.module.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import edu.unc.mapseq.module.DefaultModuleOutput;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;

/**
 * 
 * @author jdr0887
 */
@Application(name = "Cat", isWorkflowRunIdOptional = true)
public class Cat extends Module {

    @NotNull(message = "files is required", groups = InputValidations.class)
    @InputArgument(delimiter = "")
    private List<File> files;

    @FileIsReadable(message = "File does not exist", groups = OutputValidations.class)
    @NotNull(message = "Output file name is required", groups = InputValidations.class)
    @OutputArgument(redirect = true)
    private File output;

    public Cat() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return Cat.class;
    }

    @Override
    public ModuleOutput call() throws Exception {
        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();
        int exitCode = 0;
        try {
            FileUtils.touch(output);
            BufferedWriter bw = new BufferedWriter(new FileWriter(output));
            for (File f : files) {
                IOUtils.copyLarge(new FileReader(f), bw);
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
            moduleOutput.setError(new StringBuilder(e.getMessage()));
            moduleOutput.setExitCode(exitCode);
        }
        moduleOutput.setExitCode(exitCode);
        return moduleOutput;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public static void main(String[] args) {

        Cat module = new Cat();
        List<File> inputList = new ArrayList<File>();
        inputList.add(new File("/tmp", "hello.txt"));
        inputList.add(new File("/tmp", "world.txt"));
        module.setFiles(inputList);
        module.setOutput(new File("/tmp", "helloworldqwer.txt"));

        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
