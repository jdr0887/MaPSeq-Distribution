package edu.unc.mapseq.module.alignment;

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

@Application(name = "BedGraphToBigWig", executable = "$%s_UCSC_HOME/bin/bedGraphToBigWig")
public class BedGraphToBigWig extends Module {

    @NotNull(message = "inFile is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "inFile is empty", groups = InputValidations.class)
    @FileIsReadable(message = "inFile is not readable", groups = InputValidations.class)
    @InputArgument(order = 1, delimiter = "")
    private File inFile;

    @NotNull(message = "chromosomeSizes is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "chromosomeSizes is empty", groups = InputValidations.class)
    @FileIsReadable(message = "chromosomeSizes is not readable", groups = InputValidations.class)
    @InputArgument(order = 2, delimiter = "")
    private File chromosomeSizes;

    @NotNull(message = "outFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "outFile is not readable", groups = OutputValidations.class)
    @OutputArgument(delimiter = "")
    private File outFile;

    @Override
    public Class<?> getModuleClass() {
        return BedGraphToBigWig.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(), getWorkflowName()
                .toUpperCase());
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

    public File getChromosomeSizes() {
        return chromosomeSizes;
    }

    public void setChromosomeSizes(File chromosomeSizes) {
        this.chromosomeSizes = chromosomeSizes;
    }

    public static void main(String[] args) {

        BedGraphToBigWig module = new BedGraphToBigWig();
        module.setInFile(new File("/tmp", "inFile.txt"));
        module.setChromosomeSizes(new File("/tmp", "choromosome.txt"));
        module.setOutFile(new File("/tmp", "output.txt"));
        module.setWorkflowName("TEST");
        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
