package edu.unc.mapseq.module.impl.ubu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import edu.unc.bioinf.ubu.sam.SAMFilter;
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

@Application(name = "UBUSamFilter")
public class UBUSamFilter extends Module {

    @NotNull(message = "input is required", groups = InputValidations.class)
    @FileIsReadable(message = "input does not exist or is not readable", groups = InputValidations.class)
    @InputArgument
    private File input;

    @NotNull(message = "output is required", groups = InputValidations.class)
    @FileIsReadable(message = "output does not exist or is not readable", groups = OutputValidations.class)
    @InputArgument
    private File output;

    @InputArgument
    private Boolean stripIndels;

    @InputArgument
    private Integer maxInsert;

    @InputArgument
    private Integer mapq;

    @Override
    public Class<?> getModuleClass() {
        return UBUSamFilter.class;
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

        if (stripIndels != null && stripIndels) {
            argumentList.add("--strip-indels");
        }

        if (maxInsert != null) {
            argumentList.add("--max-insert");
            argumentList.add(this.maxInsert.toString());
        }

        if (mapq != null) {
            argumentList.add(" --mapq ");
            argumentList.add(this.mapq.toString());
        }

        try {
            SAMFilter.run(argumentList.toArray(new String[argumentList.size()]));
        } catch (Exception e) {
            exitCode = -1;
            e.printStackTrace();
            moduleOutput.setError(new StringBuilder(e.getMessage()));
            return moduleOutput;
        }
        moduleOutput.setExitCode(exitCode);

        FileData fm = new FileData();
        fm.setMimeType(MimeType.APPLICATION_BAM);
        fm.setName(output.getName());
        getFileDatas().add(fm);

        return moduleOutput;
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

    public Boolean getStripIndels() {
        return stripIndels;
    }

    public void setStripIndels(Boolean stripIndels) {
        this.stripIndels = stripIndels;
    }

    public Integer getMaxInsert() {
        return maxInsert;
    }

    public void setMaxInsert(Integer maxInsert) {
        this.maxInsert = maxInsert;
    }

    public Integer getMapq() {
        return mapq;
    }

    public void setMapq(Integer mapq) {
        this.mapq = mapq;
    }

    @Override
    public String toString() {
        return String.format("UBUSamFilter [input=%s, output=%s, stripIndels=%s, maxInsert=%s, mapq=%s, toString()=%s]",
                input, output, stripIndels, maxInsert, mapq, super.toString());
    }

}
