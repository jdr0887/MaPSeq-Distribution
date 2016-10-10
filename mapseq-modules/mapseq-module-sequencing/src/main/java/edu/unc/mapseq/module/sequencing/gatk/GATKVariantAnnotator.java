package edu.unc.mapseq.module.sequencing.gatk;

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
import edu.unc.mapseq.module.constraints.Contains;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "GATKApplyRecalibration", executable = "$JAVA7_HOME/bin/java -Xmx4g -Djava.io.tmpdir=$MAPSEQ_CLIENT_HOME/tmp -jar $%s_GATK_HOME/GenomeAnalysisTK.jar --analysis_type VariantAnnotator")
public class GATKVariantAnnotator extends Module {

    @NotNull(message = "referenceSequence is required", groups = InputValidations.class)
    @FileIsReadable(message = "referenceSequence is not readable", groups = InputValidations.class)
    @InputArgument(flag = "--reference_sequence")
    private File referenceSequence;

    @NotNull(message = "vcf is required", groups = InputValidations.class)
    @FileIsReadable(message = "vcf is not readable", groups = InputValidations.class)
    @InputArgument(flag = "-V")
    private File vcf;

    @NotNull(message = "out is required", groups = InputValidations.class)
    @FileIsReadable(message = "out is not readable", groups = OutputValidations.class)
    @OutputArgument(flag = "-o", persistFileData = true, mimeType = MimeType.TEXT_VCF)
    private File out;

    @NotNull(message = "bam is required", groups = InputValidations.class)
    @FileIsReadable(message = "bam is not readable", groups = InputValidations.class)
    @InputArgument(flag = "-I")
    private File bam;

    @NotNull(message = "annotation is required", groups = InputValidations.class)
    @Contains(values = { "FisherStrand", "QualByDepth", "ReadPosRankSumTest", "DepthPerAlleleBySample", "HomopolymerRun",
            "SpanningDeletions" })
    @InputArgument(flag = "-A")
    private List<String> annotation;

    @InputArgument(flag = "--phone_home")
    private String phoneHome;

    @Override
    public Class<?> getModuleClass() {
        return GATKVariantAnnotator.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(), getWorkflowName().toUpperCase());
    }

    public String getPhoneHome() {
        return phoneHome;
    }

    public void setPhoneHome(String phoneHome) {
        this.phoneHome = phoneHome;
    }

    public File getReferenceSequence() {
        return referenceSequence;
    }

    public void setReferenceSequence(File referenceSequence) {
        this.referenceSequence = referenceSequence;
    }

    public File getVcf() {
        return vcf;
    }

    public void setVcf(File vcf) {
        this.vcf = vcf;
    }

    public File getOut() {
        return out;
    }

    public void setOut(File out) {
        this.out = out;
    }

    public File getBam() {
        return bam;
    }

    public void setBam(File bam) {
        this.bam = bam;
    }

    public List<String> getAnnotation() {
        return annotation;
    }

    public void setAnnotation(List<String> annotation) {
        this.annotation = annotation;
    }

    @Override
    public String toString() {
        return String.format("GATKVariantAnnotator [referenceSequence=%s, vcf=%s, out=%s, bam=%s, annotation=%s]", referenceSequence, vcf,
                out, bam, annotation);
    }

    public static void main(String[] args) {

        GATKVariantAnnotator module = new GATKVariantAnnotator();
        module.setWorkflowName("NCGENES");
        module.setPhoneHome("NO_ET");
        module.setReferenceSequence(
                new File("/proj/renci/sequence_analysis/references/BUILD.37.1/bwa061sam0118", "BUILD.37.1.sorted.shortid.fa"));
        module.setVcf(new File("/tmp", "freebayes.vcf"));
        module.setBam(new File("/tmp", "asdf.bam"));
        module.setOut(new File("/tmp", "gatk.vcf"));
        List<String> annotationList = new ArrayList<String>();
        annotationList.add("QD");
        annotationList.add("HaplotypeScore");
        annotationList.add("MQRankSum");
        annotationList.add("ReadPosRankSum");
        annotationList.add("MQ");
        annotationList.add("FS");
        module.setAnnotation(annotationList);

        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
