package edu.unc.mapseq.module.mapsplice;

import java.io.File;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "SetUnmappedBitFlag", executable = "$%s_MAPSPLICE_HOME/bin/SetUnmappedBitFlag")
public class SetUnmappedBitFlag extends Module {

    @NotNull(message = "unmappedSAM is required", groups = InputValidations.class)
    @FileIsReadable(message = "unmappedSAM does not exist or is not readable", groups = InputValidations.class)
    @InputArgument(order = 0, delimiter = "")
    private File unmappedSAM;

    @NotNull(message = "unmappedSetBit is required", groups = InputValidations.class)
    @FileIsReadable(message = "unmappedSetBit does not exist or is not readable", groups = OutputValidations.class)
    @InputArgument(order = 1, delimiter = "")
    private File unmappedSetBit;

    @Override
    public Class<?> getModuleClass() {
        return SetUnmappedBitFlag.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(), getWorkflowName()
                .toUpperCase());
    }

    public File getUnmappedSAM() {
        return unmappedSAM;
    }

    public void setUnmappedSAM(File unmappedSAM) {
        this.unmappedSAM = unmappedSAM;
    }

    public File getUnmappedSetBit() {
        return unmappedSetBit;
    }

    public void setUnmappedSetBit(File unmappedSetBit) {
        this.unmappedSetBit = unmappedSetBit;
    }

}
