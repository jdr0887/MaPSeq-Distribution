package edu.unc.mapseq.module.sequencing.filter;

import java.io.File;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsNotEmpty;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "FilterVariant", executable = "$JAVA8_HOME/bin/java -Xmx4g -jar $%s_SEQUENCING_TOOLS/filter-vcf.jar")
public class FilterVariant extends Module {

    @NotNull(message = "intervalList is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "intervalList file is empty", groups = InputValidations.class)
    @FileIsReadable(message = "intervalList file is not readable", groups = InputValidations.class)
    @InputArgument(flag = "--interval-list", delimiter = " ")
    private File intervalList;

    @NotNull(message = "input is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "input file is empty", groups = InputValidations.class)
    @FileIsReadable(message = "input file is not readable", groups = InputValidations.class)
    @InputArgument(flag = "--input", delimiter = " ")
    private File input;

    @NotNull(message = "output is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "output file is empty", groups = OutputValidations.class)
    @OutputArgument(flag = "--output", delimiter = " ", persistFileData = true, mimeType = MimeType.TEXT_VCF)
    private File output;

    @InputArgument(flag = "--missing", delimiter = " ")
    private Boolean withMissing = Boolean.FALSE;

    @Override
    public Class<?> getModuleClass() {
        return FilterVariant.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(), getWorkflowName().toUpperCase());
    }

    public File getIntervalList() {
        return intervalList;
    }

    public void setIntervalList(File intervalList) {
        this.intervalList = intervalList;
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

    public Boolean getWithMissing() {
        return withMissing;
    }

    public void setWithMissing(Boolean withMissing) {
        this.withMissing = withMissing;
    }

    @Override
    public String toString() {
        return String.format("FilterVariant [intervalList=%s, input=%s, output=%s, withMissing=%s, toString()=%s]", intervalList, input,
                output, withMissing, super.toString());
    }

    public static void main(String[] args) {
        FilterVariant module = new FilterVariant();

        // module.setIntervalList(new File("/home/jdr0887/tmp", "ic_snp_v2.list"));
        // module.setInput(new File("/home/jdr0887/tmp/130522_UNC11-SN627_0299_AD25TUACXX",
        // "130522_UNC11-SN627_0299_AD25TUACXX_ACAGTG_L006.fixed-rg.deduped.realign.fixmate.recal.vcf"));
        // module.setOutput(new File("/home/jdr0887/tmp/130522_UNC11-SN627_0299_AD25TUACXX",
        // "130522_UNC11-SN627_0299_AD25TUACXX_ACAGTG_L006.fixed-rg.deduped.realign.fixmate.recal.variant.vcf"));
        // module.setWithMissing(Boolean.TRUE);

        // module.setIntervalList(new File("/home/jdr0887/tmp", "ic_snp_v2.list"));
        // module.setInput(new File(
        // "/home/jdr0887/tmp/130522_UNC11-SN627_0299_AD25TUACXX",
        // "130522_UNC11-SN627_0299_AD25TUACXX_ACAGTG_L006.fixed-rg.deduped.realign.fixmate.recal.variant.recalibrated.filtered.vcf"));
        // module.setOutput(new File("/home/jdr0887/tmp/130522_UNC11-SN627_0299_AD25TUACXX",
        // "130522_UNC11-SN627_0299_AD25TUACXX_ACAGTG_L006.fixed-rg.deduped.realign.fixmate.recal.variant.ic_snps.vcf"));

        // module.setInput(new File(
        // "/home/jdr0887/tmp/130201_UNC14-SN744_0304_BC1NK7ACXX",
        // "130201_UNC14-SN744_0304_BC1NK7ACXX_ACTTGA_L006.fixed-rg.deduped.realign.fixmate.recal.variant.recalibrated.filtered.vcf"));
        // module.setOutput(new File(
        // "/home/jdr0887/tmp/130201_UNC14-SN744_0304_BC1NK7ACXX",
        // "130201_UNC14-SN744_0304_BC1NK7ACXX_ACTTGA_L006.fixed-rg.deduped.realign.fixmate.recal.variant.recalibrated.filtered.dxid_25_v_19.vcf"));
        // module.setIntervalList(new File("/home/jdr0887/tmp", "genes_dxid_25_v_19.interval_list"));

        module.setInput(new File("/home/jdr0887/FilterVariant",
                "150616_UNC18-D00493_0237_BC74YFANXX_TTAGGC_L005.fixed-rg.deduped.realign.fixmate.recal.vcf"));
        module.setOutput(new File("/home/jdr0887/FilterVariant",
                "150616_UNC18-D00493_0237_BC74YFANXX_TTAGGC_L005.fixed-rg.deduped.realign.fixmate.recal.variant.vcf"));
        module.setIntervalList(new File("/home/jdr0887/FilterVariant", "ic_snp_v2.list"));
        module.setWithMissing(Boolean.TRUE);

        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
