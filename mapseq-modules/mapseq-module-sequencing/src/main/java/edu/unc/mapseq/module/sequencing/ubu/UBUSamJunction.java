package edu.unc.mapseq.module.sequencing.ubu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import edu.unc.bioinf.ubu.sam.SpliceJunctionCounter;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.module.DefaultModuleOutput;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "UBUSamJunction")
public class UBUSamJunction extends Module {

    @NotNull(message = "junctions is required", groups = InputValidations.class)
    @FileIsReadable(message = "junctions does not exist or is not readable", groups = InputValidations.class)
    @InputArgument
    private File junctions;

    @NotNull(message = "input is required", groups = InputValidations.class)
    @FileIsReadable(message = "junctions does not exist or is not readable", groups = InputValidations.class)
    @InputArgument
    private File input;

    @NotNull(message = "output is required", groups = InputValidations.class)
    @FileIsReadable(message = "output does not exist or is not readable", groups = OutputValidations.class)
    @InputArgument
    private File output;

    @Override
    public Class<?> getModuleClass() {
        return UBUSamJunction.class;
    }

    @Override
    public ModuleOutput call() throws Exception {

        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();
        int exitCode = 0;

        List<String> argumentList = new ArrayList<String>();
        argumentList.add("--in");
        argumentList.add(this.input.getAbsolutePath());
        argumentList.add("--out");
        argumentList.add(this.output.getAbsolutePath());
        argumentList.add("--junctions");
        argumentList.add(this.junctions.getAbsolutePath());

        try {
            SpliceJunctionCounter.run(argumentList.toArray(new String[argumentList.size()]));
        } catch (Exception e) {
            exitCode = -1;
            e.printStackTrace();
            moduleOutput.setError(new StringBuilder(e.getMessage()));
            return moduleOutput;
        }
        moduleOutput.setExitCode(exitCode);

        FileData fileData = new FileData();
        fileData.setMimeType(MimeType.TEXT_PLAIN);
        fileData.setName(output.getName());
        getFileDatas().add(fileData);

        return moduleOutput;
    }

    public File getJunctions() {
        return junctions;
    }

    public void setJunctions(File junctions) {
        this.junctions = junctions;
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
        return String.format("UBUSamJunction [junctions=%s, input=%s, output=%s, toString()=%s]", junctions, input,
                output, super.toString());
    }

}
