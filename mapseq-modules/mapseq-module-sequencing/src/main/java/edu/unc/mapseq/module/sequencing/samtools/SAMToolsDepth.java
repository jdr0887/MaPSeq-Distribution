package edu.unc.mapseq.module.sequencing.samtools;

import java.io.File;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.constraints.FileIsNotEmpty;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "SAMToolsDepth", executable = "$%s_SAMTOOLS_HOME/bin/samtools depth")
public class SAMToolsDepth extends Module {

    @NotNull(message = "Input BAM is required", groups = InputValidations.class)
    @FileIsReadable(message = "Invalid BAM input file", groups = InputValidations.class)
    @FileIsNotEmpty(message = "input BAM is empty", groups = InputValidations.class)
    @InputArgument(order = 1, flag = "-b")
    private File bed;

    @NotNull(message = "bam is required", groups = InputValidations.class)
    @FileIsReadable(message = "Invalid bam file", groups = InputValidations.class)
    @FileIsNotEmpty(message = "bam is empty", groups = InputValidations.class)
    @InputArgument(order = 99, delimiter = "")
    private File bam;

    @InputArgument(order = 2, flag = "-q", description = "Only count reads with base quality greater than N")
    private Integer baseQuality = 20;

    @InputArgument(order = 3, flag = "-Q", description = "Only count reads with mapping quality greater than N")
    private Integer mappingQuality = 20;

    @NotNull(message = "Output is required", groups = InputValidations.class)
    @OutputArgument(redirect = true, persistFileData = true, mimeType = MimeType.TEXT_COVERAGE_DEPTH)
    private File output;

    public SAMToolsDepth() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return SAMToolsDepth.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(), getWorkflowName().toUpperCase());
    }

    public File getBed() {
        return bed;
    }

    public void setBed(File bed) {
        this.bed = bed;
    }

    public File getBam() {
        return bam;
    }

    public void setBam(File bam) {
        this.bam = bam;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public Integer getMappingQuality() {
        return mappingQuality;
    }

    public void setMappingQuality(Integer mappingQuality) {
        this.mappingQuality = mappingQuality;
    }

    public Integer getBaseQuality() {
        return baseQuality;
    }

    public void setBaseQuality(Integer baseQuality) {
        this.baseQuality = baseQuality;
    }

    @Override
    public String toString() {
        return String.format("SAMToolsDepth [bed=%s, bam=%s, baseQuality=%s, mappingQuality=%s, output=%s]", bed, bam, baseQuality,
                mappingQuality, output);
    }

    public static void main(String[] args) {
        /// projects/mapseq/apps/samtools-1.3.1/bin/samtools depth -b
        /// /projects/mapseq/data/resources/intervals/gs/GS_439_Target_Intervals_build37.bed ./NCG_00300R09.merged.bam >
        /// NCG_00300R09.merged.samtools.depth.txt

        SAMToolsDepth module = new SAMToolsDepth();
        module.setWorkflowName("TEST");
        module.setBed(new File("/tmp", "qwer.bed"));
        module.setBam(new File("/tmp", "asdf.bam"));
        module.setOutput(new File("/tmp", "zxcv.txt"));
        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
