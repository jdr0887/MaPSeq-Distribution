package edu.unc.mapseq.module.sequencing.filter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;

import edu.unc.mapseq.module.DefaultModuleOutput;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsNotEmpty;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "StripTrailingTabs")
public class StripTrailingTabs extends Module {

    @NotNull(message = "input is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "input file is empty", groups = InputValidations.class)
    @FileIsReadable(message = "input file is not readable", groups = InputValidations.class)
    @InputArgument
    private File input;

    @NotNull(message = "output is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "output file is empty", groups = OutputValidations.class)
    @InputArgument
    private File output;

    @Override
    public Class<?> getModuleClass() {
        return StripTrailingTabs.class;
    }

    @Override
    public ModuleOutput call() throws Exception {
        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();
        int exitCode = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(input));
            BufferedWriter bw = new BufferedWriter(new FileWriter(output));
            String line;
            while ((line = br.readLine()) != null) {
                line = StringUtils.removeEnd(line, "\t");
                bw.write(String.format("%s%n", line));
                bw.flush();
            }
            bw.close();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            moduleOutput.setError(new StringBuilder(e.getMessage()));
            moduleOutput.setExitCode(-1);
            return moduleOutput;
        }
        moduleOutput.setExitCode(exitCode);
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

    @Override
    public String toString() {
        return String.format("StripTrailingTabs [input=%s, output=%s, toString()=%s]", input, output, super.toString());
    }

    public static void main(String[] args) {
        StripTrailingTabs runner = new StripTrailingTabs();
        runner.setInput(new File("/home/jdr0887/tmp/rsem.isoforms.results"));
        runner.setOutput(new File("/home/jdr0887/tmp/rsem.isoforms.results.stripped"));
        try {
            runner.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
