package edu.unc.mapseq.module.sequencing.gatk2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "GATKUnifiedGenotyper", executable = "$JAVA7_HOME/bin/java -Xmx4g -Djava.io.tmpdir=$MAPSEQ_CLIENT_HOME/tmp -jar $%s_GATK2_HOME/GenomeAnalysisTK.jar --analysis_type UnifiedGenotyper")
public class GATKUnifiedGenotyper extends Module {

    @NotNull(message = "key is required", groups = InputValidations.class)
    @FileIsReadable(message = "key is not readable", groups = InputValidations.class)
    @InputArgument(flag = "--gatk_key")
    private File key;

    @NotNull(message = "inputFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "inputFile is not readable", groups = InputValidations.class)
    @InputArgument(flag = "--input_file")
    private File inputFile;

    @NotNull(message = "referenceSequence is required", groups = InputValidations.class)
    @FileIsReadable(message = "referenceSequence is not readable", groups = InputValidations.class)
    @InputArgument(flag = "--reference_sequence")
    private File referenceSequence;

    @InputArgument(flag = "--num_threads")
    private Integer numThreads;

    @NotNull(message = "out is required", groups = InputValidations.class)
    @FileIsReadable(message = "out is not readable", groups = OutputValidations.class)
    @OutputArgument(flag = "--out", persistFileData = true, mimeType = MimeType.TEXT_VCF)
    private File out;

    @InputArgument(flag = "--validation_strictness")
    private String validationStrictness;

    @InputArgument(flag = "--intervals")
    private File intervals;

    @InputArgument(flag = "-metrics")
    private File metrics;

    @InputArgument(flag = "--annotation")
    private List<String> annotation;

    @InputArgument(flag = "--dbsnp")
    private File dbsnp;

    @InputArgument(flag = "--output_mode")
    private String outputMode;

    @InputArgument(flag = "-stand_call_conf")
    private Double standCallConf;

    @InputArgument(flag = "-stand_emit_conf")
    private Double standEmitConf;

    @InputArgument(flag = "--genotype_likelihoods_model")
    private String genotypeLikelihoodsModel;

    @InputArgument(flag = "--downsample_to_coverage")
    private Integer downsampleToCoverage;

    @InputArgument(flag = "--phone_home")
    private String phoneHome;

    @InputArgument(flag = "--downsampling_type")
    private String downsamplingType;

    @Override
    public Class<?> getModuleClass() {
        return GATKUnifiedGenotyper.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(),
                getWorkflowName().toUpperCase());
    }

    public File getKey() {
        return key;
    }

    public void setKey(File key) {
        this.key = key;
    }

    public String getPhoneHome() {
        return phoneHome;
    }

    public void setPhoneHome(String phoneHome) {
        this.phoneHome = phoneHome;
    }

    public String getDownsamplingType() {
        return downsamplingType;
    }

    public void setDownsamplingType(String downsamplingType) {
        this.downsamplingType = downsamplingType;
    }

    public File getInputFile() {
        return inputFile;
    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }

    public File getReferenceSequence() {
        return referenceSequence;
    }

    public void setReferenceSequence(File referenceSequence) {
        this.referenceSequence = referenceSequence;
    }

    public Integer getNumThreads() {
        return numThreads;
    }

    public void setNumThreads(Integer numThreads) {
        this.numThreads = numThreads;
    }

    public File getOut() {
        return out;
    }

    public void setOut(File out) {
        this.out = out;
    }

    public String getValidationStrictness() {
        return validationStrictness;
    }

    public void setValidationStrictness(String validationStrictness) {
        this.validationStrictness = validationStrictness;
    }

    public File getIntervals() {
        return intervals;
    }

    public void setIntervals(File intervals) {
        this.intervals = intervals;
    }

    public File getMetrics() {
        return metrics;
    }

    public void setMetrics(File metrics) {
        this.metrics = metrics;
    }

    public List<String> getAnnotation() {
        return annotation;
    }

    public void setAnnotation(List<String> annotation) {
        this.annotation = annotation;
    }

    public File getDbsnp() {
        return dbsnp;
    }

    public void setDbsnp(File dbsnp) {
        this.dbsnp = dbsnp;
    }

    public String getOutputMode() {
        return outputMode;
    }

    public void setOutputMode(String outputMode) {
        this.outputMode = outputMode;
    }

    public Double getStandCallConf() {
        return standCallConf;
    }

    public void setStandCallConf(Double standCallConf) {
        this.standCallConf = standCallConf;
    }

    public Double getStandEmitConf() {
        return standEmitConf;
    }

    public void setStandEmitConf(Double standEmitConf) {
        this.standEmitConf = standEmitConf;
    }

    public Integer getDownsampleToCoverage() {
        return downsampleToCoverage;
    }

    public void setDownsampleToCoverage(Integer downsampleToCoverage) {
        this.downsampleToCoverage = downsampleToCoverage;
    }

    public String getGenotypeLikelihoodsModel() {
        return genotypeLikelihoodsModel;
    }

    public void setGenotypeLikelihoodsModel(String genotypeLikelihoodsModel) {
        this.genotypeLikelihoodsModel = genotypeLikelihoodsModel;
    }

    @Override
    public String toString() {
        return String.format(
                "GATKUnifiedGenotyper [key=%s, inputFile=%s, referenceSequence=%s, numThreads=%s, out=%s, validationStrictness=%s, intervals=%s, metrics=%s, annotation=%s, dbsnp=%s, outputMode=%s, standCallConf=%s, standEmitConf=%s, genotypeLikelihoodsModel=%s, downsampleToCoverage=%s, phoneHome=%s, downsamplingType=%s, toString()=%s]",
                key, inputFile, referenceSequence, numThreads, out, validationStrictness, intervals, metrics,
                annotation, dbsnp, outputMode, standCallConf, standEmitConf, genotypeLikelihoodsModel,
                downsampleToCoverage, phoneHome, downsamplingType, super.toString());
    }

    public static void main(String[] args) {
        GATKUnifiedGenotyper module = new GATKUnifiedGenotyper();
        module.setWorkflowName("TEST");
        module.setPhoneHome("NO_ET");
        module.setDownsamplingType("NONE");
        module.setReferenceSequence(new File("/tmp", "refseq"));
        module.setDbsnp(new File("/tmp", "dbsnp"));
        module.setStandCallConf(30.0);
        module.setStandEmitConf(0.0);
        module.setGenotypeLikelihoodsModel("BOTH");
        module.setInputFile(new File("/tmp", "input"));
        module.setNumThreads(4);
        module.setOut(new File("/tmp", "out.vcf"));
        module.setIntervals(new File("/tmp", "intervals"));
        module.setOutputMode("EMIT_ALL_SITES");
        module.setMetrics(new File("/tmp", "metrics"));
        module.setDownsampleToCoverage(250);

        List<String> annotationList = new ArrayList<String>();
        annotationList.add("AlleleBalance");
        annotationList.add("DepthOfCoverage");
        annotationList.add("HomopolymerRun");
        annotationList.add("MappingQualityZero");
        annotationList.add("QualByDepth");
        annotationList.add("RMSMappingQuality");
        annotationList.add("HaplotypeScore");

        module.setAnnotation(annotationList);

        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
