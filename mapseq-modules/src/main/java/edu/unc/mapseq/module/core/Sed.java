package edu.unc.mapseq.module.core;

import java.io.File;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.module.Module;
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
@Application(name = "Sed", executable = "/bin/sed", isWorkflowRunIdOptional = true)
public class Sed extends Module {

    @NotNull(message = "regularExpression is required", groups = InputValidations.class)
    @InputArgument(flag = "-e")
    private String regularExpression;

    @NotNull(message = "source is required", groups = InputValidations.class)
    @FileIsReadable(message = "source does not exist", groups = InputValidations.class)
    @InputArgument
    private File source;

    @FileIsReadable(message = "File does not exist", groups = OutputValidations.class)
    @NotNull(message = "Output file name is required", groups = InputValidations.class)
    @OutputArgument(redirect = true, persistFileData = true)
    private File output;

    public Sed() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return Sed.class;
    }

    public String getRegularExpression() {
        return regularExpression;
    }

    public void setRegularExpression(String regularExpression) {
        this.regularExpression = "'" + regularExpression + "'";
    }

    public File getSource() {
        return source;
    }

    public void setSource(File source) {
        this.source = source;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    @Override
    public String toString() {
        return String.format("Sed [regularExpression=%s, source=%s, output=%s, toString()=%s]", regularExpression,
                source, output, super.toString());
    }

    public static void main(String[] args) {
        Sed module = new Sed();
        module.setRegularExpression("s/world/sed/");
        module.setSource(new File("/home/mapseqdev", "helloworld.txt"));
        module.setOutput(new File("/home/mapseqdev", "hellosed.txt"));

        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
