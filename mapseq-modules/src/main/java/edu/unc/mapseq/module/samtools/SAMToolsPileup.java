package edu.unc.mapseq.module.samtools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.Executable;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsNotEmpty;
import edu.unc.mapseq.module.constraints.FileIsReadable;
import edu.unc.mapseq.module.constraints.FileListIsReadable;

@Application(name = "SAMToolsPileup")
@Executable(value = "$%s_SAMTOOLS_HOME/bin/samtools mpileup")
public class SAMToolsPileup extends Module {

    @NotNull(message = "Input is required", groups = InputValidations.class)
    @FileListIsReadable(message = "Invalid input file", groups = InputValidations.class)
    @InputArgument(flag = "", delimiter = "")
    private List<File> input;

    @NotNull(message = "Output is required", groups = InputValidations.class)
    @FileIsReadable(message = "Invalid output file", groups = OutputValidations.class)
    @FileIsNotEmpty(message = "output is empty", groups = OutputValidations.class)
    @OutputArgument(redirect = true, persistFileData = true, mimeType = MimeType.TEXT_PILEUP)
    private File output;

    @InputArgument(flag = "-B", description = "Disable probabilistic realignment for the computation of base alignment quality (BAQ). BAQ is the Phred-scaled probability of a read base being misaligned. Applying this option greatly helps to reduce false SNPs caused by misalignments.")
    private Boolean disableProbabilisticRealignment = false;

    @InputArgument(flag = "-C", description = "Coefficient for downgrading mapping quality for reads containing excessive mismatches. Given a read with a phred-scaled probability q of being generated from the mapped position, the new mapping quality is about sqrt((INT-q)/INT)*INT. A zero value disables this functionality; if enabled, the recommended value for BWA is 50.", defaultValue = "0")
    private Integer mappingQualityCoefficient;

    @InputArgument(flag = "-d", description = "At a position, read maximally INT reads per input BAM.", defaultValue = "250")
    private Integer readsPerInput;

    @InputArgument(flag = "-E", description = "Extended BAQ computation. This option helps sensitivity especially for MNPs, but may hurt specificity a little bit.")
    private Boolean extendedBAQComputation = false;

    @InputArgument(flag = "-f", description = "The faidx-indexed reference file in the FASTA format. The file can be optionally compressed by razip.")
    private File referenceFile;

    @InputArgument(flag = "-l", description = "BED or position list file containing a list of regions or sites where pileup or BCF should be generated")
    private File positionListFile;

    @InputArgument(flag = "-q", description = "Minimum mapping quality for an alignment to be used", defaultValue = "0")
    private Integer minimumMappingQuality;

    @InputArgument(flag = "-Q", description = "Minimum base quality for a base to be considered", defaultValue = "13")
    private Integer minimumBaseQuality;

    @InputArgument(flag = "-r", description = "Only generate pileup in region", defaultValue = "all sites")
    private String region;

    public SAMToolsPileup() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return SAMToolsPileup.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Executable.class).value(), getWorkflowName().toUpperCase());
    }

    public List<File> getInput() {
        return input;
    }

    public void setInput(List<File> input) {
        this.input = input;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public Boolean getDisableProbabilisticRealignment() {
        return disableProbabilisticRealignment;
    }

    public void setDisableProbabilisticRealignment(Boolean disableProbabilisticRealignment) {
        this.disableProbabilisticRealignment = disableProbabilisticRealignment;
    }

    public Integer getMappingQualityCoefficient() {
        return mappingQualityCoefficient;
    }

    public void setMappingQualityCoefficient(Integer mappingQualityCoefficient) {
        this.mappingQualityCoefficient = mappingQualityCoefficient;
    }

    public Integer getReadsPerInput() {
        return readsPerInput;
    }

    public void setReadsPerInput(Integer readsPerInput) {
        this.readsPerInput = readsPerInput;
    }

    public Boolean getExtendedBAQComputation() {
        return extendedBAQComputation;
    }

    public void setExtendedBAQComputation(Boolean extendedBAQComputation) {
        this.extendedBAQComputation = extendedBAQComputation;
    }

    public File getReferenceFile() {
        return referenceFile;
    }

    public void setReferenceFile(File referenceFile) {
        this.referenceFile = referenceFile;
    }

    public File getPositionListFile() {
        return positionListFile;
    }

    public void setPositionListFile(File positionListFile) {
        this.positionListFile = positionListFile;
    }

    public Integer getMinimumMappingQuality() {
        return minimumMappingQuality;
    }

    public void setMinimumMappingQuality(Integer minimumMappingQuality) {
        this.minimumMappingQuality = minimumMappingQuality;
    }

    public Integer getMinimumBaseQuality() {
        return minimumBaseQuality;
    }

    public void setMinimumBaseQuality(Integer minimumBaseQuality) {
        this.minimumBaseQuality = minimumBaseQuality;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public static void main(String[] args) {
        SAMToolsPileup runner = new SAMToolsPileup();
        runner.setOutput(new File(
                "/home/jdr0887/tmp/120731_UNC14-SN744_0253_AD135LACXX_GTTTCG_L008.fixed-rg.sorted.pileup"));
        List<File> pileupInputList = new ArrayList<File>();
        pileupInputList.add(new File(
                "/home/jdr0887/tmp/120731_UNC14-SN744_0253_AD135LACXX_GTTTCG_L008.fixed-rg.sorted.bam"));
        runner.setInput(pileupInputList);
        runner.setWorkflowName("NCGenes");
        try {
            runner.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
