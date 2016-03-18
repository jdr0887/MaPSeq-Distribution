package edu.unc.mapseq.module.impl.core;

import java.io.File;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
@Application(name = "Echo", executable = "/bin/echo", isWorkflowRunIdOptional = true)
public class Echo extends Module {

    @Size(min = 0, message = "Greeting is required", groups = InputValidations.class)
    @NotNull(message = "Greeting is required", groups = InputValidations.class)
    @InputArgument(delimiter = "")
    private String greeting;

    @FileIsReadable(message = "File does not exist", groups = OutputValidations.class)
    @NotNull(message = "Output file name is required", groups = InputValidations.class)
    @OutputArgument(redirect = true, persistFileData = true)
    private File output;

    public Echo() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return Echo.class;
    }

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    @Override
    public String toString() {
        return String.format("Echo [greeting=%s, output=%s, toString()=%s]", greeting, output, super.toString());
    }

    public static void main(String[] args) {
        Echo module = new Echo();
        module.setGreeting("Hello");
        module.setOutput(new File("/tmp", "hello.txt"));

        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
