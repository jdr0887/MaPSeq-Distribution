package edu.unc.mapseq.module.mapsplice;

import java.io.File;
import java.util.List;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;
import edu.unc.mapseq.module.constraints.FileListIsReadable;

@Application(name = "ReadChromoSize", executable = "$%s_MAPSPLICE_HOME/bin/read_chromo_size")
public class ReadChromoSize extends Module {

    @NotNull(message = "chromosomeIndex is required", groups = InputValidations.class)
    @FileIsReadable(message = "chromosomeIndex does not exist or is not readable", groups = OutputValidations.class)
    @InputArgument(order = 100, delimiter = "")
    private File chromosomeIndex;

    @NotNull(message = "chromosomeHeadOutput is required", groups = InputValidations.class)
    @FileIsReadable(message = "chromosomeHeadOutput does not exist or is not readable", groups = OutputValidations.class)
    @InputArgument(order = 101, delimiter = "")
    private File chromosomeHeadOutput;

    @NotNull(message = "chromosomeSizesOutput is required", groups = InputValidations.class)
    @FileIsReadable(message = "chromosomeSizesOutput does not exist or is not readable", groups = OutputValidations.class)
    @InputArgument(order = 102, delimiter = "")
    private File chromosomeSizesOutput;

    @NotNull(message = "chromosomeNamesOutput is required", groups = InputValidations.class)
    @FileIsReadable(message = "chromosomeNamesOutput does not exist or is not readable", groups = OutputValidations.class)
    @InputArgument(order = 103, delimiter = "")
    private File chromosomeNamesOutput;

    @NotNull(message = "samFile is required", groups = InputValidations.class)
    @FileListIsReadable(message = "samFile does not exist or is not readable", groups = InputValidations.class)
    @InputArgument(order = 0, delimiter = "")
    private List<File> samFile;

    @Override
    public Class<?> getModuleClass() {
        return ReadChromoSize.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(), getWorkflowName()
                .toUpperCase());
    }

    public File getChromosomeIndex() {
        return chromosomeIndex;
    }

    public void setChromosomeIndex(File chromosomeIndex) {
        this.chromosomeIndex = chromosomeIndex;
    }

    public File getChromosomeHeadOutput() {
        return chromosomeHeadOutput;
    }

    public void setChromosomeHeadOutput(File chromosomeHeadOutput) {
        this.chromosomeHeadOutput = chromosomeHeadOutput;
    }

    public File getChromosomeSizesOutput() {
        return chromosomeSizesOutput;
    }

    public void setChromosomeSizesOutput(File chromosomeSizesOutput) {
        this.chromosomeSizesOutput = chromosomeSizesOutput;
    }

    public File getChromosomeNamesOutput() {
        return chromosomeNamesOutput;
    }

    public void setChromosomeNamesOutput(File chromosomeNamesOutput) {
        this.chromosomeNamesOutput = chromosomeNamesOutput;
    }

    public List<File> getSamFile() {
        return samFile;
    }

    public void setSamFile(List<File> samFile) {
        this.samFile = samFile;
    }

}
