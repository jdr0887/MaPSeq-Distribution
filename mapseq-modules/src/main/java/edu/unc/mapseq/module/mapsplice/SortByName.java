package edu.unc.mapseq.module.mapsplice;

import java.io.File;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "SortByName", executable = "$%s_MAPSPLICE_HOME/bin/cluster")
public class SortByName extends Module {

    @NotNull(message = "clusterDirectory is required", groups = InputValidations.class)
    @FileIsReadable(message = "clusterDirectory does not exist or is not readable", groups = InputValidations.class)
    @InputArgument(delimiter = "")
    private File clusterDirectory;

    @Override
    public Class<?> getModuleClass() {
        return SortByName.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(), getWorkflowName()
                .toUpperCase());
    }

    public File getClusterDirectory() {
        return clusterDirectory;
    }

    public void setClusterDirectory(File clusterDirectory) {
        this.clusterDirectory = clusterDirectory;
    }

}
