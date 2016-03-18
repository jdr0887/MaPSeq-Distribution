package edu.unc.mapseq.module.ubu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import edu.unc.bioinf.ubu.sam.GenomeToTranscriptome;
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

@Application(name = "UBUSamTranslate")
public class UBUSamTranslate extends Module {

    @NotNull(message = "bed is required", groups = InputValidations.class)
    @FileIsReadable(message = "bed does not exist or is not readable", groups = InputValidations.class)
    @InputArgument
    private File bed;

    @NotNull(message = "input is required", groups = InputValidations.class)
    @FileIsReadable(message = "input does not exist or is not readable", groups = InputValidations.class)
    @InputArgument
    private File input;

    @NotNull(message = "output is required", groups = InputValidations.class)
    @FileIsReadable(message = "output does not exist or is not readable", groups = OutputValidations.class)
    @InputArgument
    private File output;

    @InputArgument
    private File order;

    @InputArgument
    private Boolean xgTags;

    @InputArgument
    private Boolean reverse;

    @Override
    public Class<?> getModuleClass() {
        return UBUSamTranslate.class;
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
        argumentList.add("--bed");
        argumentList.add(this.bed.getAbsolutePath());

        if (order != null) {
            argumentList.add("--order");
            argumentList.add(this.order.getAbsolutePath());
        }

        if (xgTags != null) {
            argumentList.add("--xgtags");
        }

        if (reverse != null) {
            argumentList.add("--reverse");
        }

        try {
            GenomeToTranscriptome.run(argumentList.toArray(new String[argumentList.size()]));
        } catch (Exception e) {
            exitCode = -1;
            e.printStackTrace();
            moduleOutput.setError(new StringBuilder(e.getMessage()));
            return moduleOutput;
        }
        moduleOutput.setExitCode(exitCode);

        FileData fileData = new FileData();
        fileData.setMimeType(MimeType.APPLICATION_BAM);
        fileData.setName(output.getName());
        getFileDatas().add(fileData);
        return moduleOutput;
    }

    public File getBed() {
        return bed;
    }

    public void setBed(File bed) {
        this.bed = bed;
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

    public File getOrder() {
        return order;
    }

    public void setOrder(File order) {
        this.order = order;
    }

    public Boolean getXgTags() {
        return xgTags;
    }

    public void setXgTags(Boolean xgTags) {
        this.xgTags = xgTags;
    }

    public Boolean getReverse() {
        return reverse;
    }

    public void setReverse(Boolean reverse) {
        this.reverse = reverse;
    }

    @Override
    public String toString() {
        return String.format(
                "UBUSamTranslate [bed=%s, input=%s, output=%s, order=%s, xgTags=%s, reverse=%s, toString()=%s]", bed,
                input, output, order, xgTags, reverse, super.toString());
    }

}
