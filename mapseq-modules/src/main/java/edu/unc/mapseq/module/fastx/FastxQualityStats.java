package edu.unc.mapseq.module.fastx;

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

/**
 * Performing per lane based statistics
 * 
 * Underlying script: fastx_quality_stats executable installed Dependency: fastx_quality_stats from
 * http://hannonlab.cshl.edu/fastx_toolkit/commandline.html Currently installed at: /usr/bin/fastx_quality_stats
 * Expected output: qual_filter.txt
 * 
 */
@Application(name = "FastxQualityStats", executable = "$%s_FASTX_TOOLKIT_HOME/bin/fastx_quality_stats -Q33")
public class FastxQualityStats extends Module {

    @FileIsReadable(message = "inFile is not readable", groups = InputValidations.class)
    @FileIsNotEmpty(message = "inFile is empty", groups = InputValidations.class)
    @NotNull(message = "inFile is required", groups = InputValidations.class)
    @InputArgument(flag = "-i")
    private File inFile;

    @FileIsNotEmpty(message = "outFile is empty", groups = OutputValidations.class)
    @NotNull(message = "outFile is required", groups = InputValidations.class)
    @OutputArgument(flag = "-o", persistFileData = true)
    private File outFile;

    public FastxQualityStats() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return FastxQualityStats.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(),
                getWorkflowName().toUpperCase());
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

    @Override
    public String toString() {
        return String.format("FastxQualityStats [inFile=%s, outFile=%s, toString()=%s]", inFile, outFile,
                super.toString());
    }

}
