package edu.unc.mapseq.module.mapsplice;

import java.io.File;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "FilterOriginalFusion", executable = "$%s_MAPSPLICE_HOME/bin/filteroriginalfusion")
public class FilterOriginalFusion extends Module {

    @NotNull(message = "junction is required", groups = InputValidations.class)
    @FileIsReadable(message = "junction does not exist or is not readable", groups = InputValidations.class)
    @InputArgument(order = 0, delimiter = "")
    private File junction;

    @NotNull(message = "minimumMismatches is required", groups = InputValidations.class)
    @InputArgument(order = 1, delimiter = "")
    private Integer minimumMismatches;

    @NotNull(message = "minimumLPQ is required", groups = InputValidations.class)
    @InputArgument(order = 2, delimiter = "")
    private Double minimumLPQ;

    @NotNull(message = "remainingJunctions is required", groups = InputValidations.class)
    @FileIsReadable(message = "remainingJunctions does not exist or is not readable", groups = OutputValidations.class)
    @InputArgument(order = 3, delimiter = "")
    private File remainingJunctions;

    @NotNull(message = "filteredJunctions is required", groups = InputValidations.class)
    @FileIsReadable(message = "filteredJunctions does not exist or is not readable", groups = OutputValidations.class)
    @InputArgument(order = 4, delimiter = "")
    private File filteredJunctions;

    @Override
    public Class<?> getModuleClass() {
        return FilterOriginalFusion.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(), getWorkflowName()
                .toUpperCase());
    }

    public File getJunction() {
        return junction;
    }

    public void setJunction(File junction) {
        this.junction = junction;
    }

    public Integer getMinimumMismatches() {
        return minimumMismatches;
    }

    public void setMinimumMismatches(Integer minimumMismatches) {
        this.minimumMismatches = minimumMismatches;
    }

    public Double getMinimumLPQ() {
        return minimumLPQ;
    }

    public void setMinimumLPQ(Double minimumLPQ) {
        this.minimumLPQ = minimumLPQ;
    }

    public File getRemainingJunctions() {
        return remainingJunctions;
    }

    public void setRemainingJunctions(File remainingJunctions) {
        this.remainingJunctions = remainingJunctions;
    }

    public File getFilteredJunctions() {
        return filteredJunctions;
    }

    public void setFilteredJunctions(File filteredJunctions) {
        this.filteredJunctions = filteredJunctions;
    }

}
