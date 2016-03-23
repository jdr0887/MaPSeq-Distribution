package edu.unc.mapseq.module.sequencing.filter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.IntRange;

import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.module.DefaultModuleOutput;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsNotEmpty;
import edu.unc.mapseq.module.constraints.FileIsReadable;

//java implementation is 3x faster
@Application(name = "FilterVariant")
// @Application(name = "FilterVariant", executable = "/proj/renci/rc_renci/scripts/python/utils/filter_vcf.py")
public class FilterVariant extends Module {

    @NotNull(message = "intervalList is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "intervalList file is empty", groups = InputValidations.class)
    @FileIsReadable(message = "intervalList file is not readable", groups = InputValidations.class)
    @InputArgument
    private File intervalList;

    @NotNull(message = "input is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "input file is empty", groups = InputValidations.class)
    @FileIsReadable(message = "input file is not readable", groups = InputValidations.class)
    @InputArgument
    private File input;

    @NotNull(message = "output is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "output file is empty", groups = OutputValidations.class)
    @OutputArgument
    private File output;

    @InputArgument
    private Boolean withMissing = Boolean.FALSE;

    @Override
    public Class<?> getModuleClass() {
        return FilterVariant.class;
    }

    @Override
    public ModuleOutput call() throws Exception {
        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();

        int exitCode = 0;
        Map<String, List<IntRange>> map = new HashMap<String, List<IntRange>>();
        try (FileReader fr = new FileReader(intervalList); BufferedReader br = new BufferedReader(fr)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (StringUtils.isEmpty(line.trim()) || line.startsWith("#")) {
                    continue;
                }
                String[] lineArray = line.split(":");
                String chromosome = lineArray[0];
                map.put(chromosome, new ArrayList<IntRange>());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileReader fr = new FileReader(intervalList); BufferedReader br = new BufferedReader(fr)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (StringUtils.isEmpty(line.trim()) || line.startsWith("#")) {
                    continue;
                }
                String[] lineArray = line.split(":");
                String chromosome = lineArray[0];
                String position = lineArray[1];
                Integer start, end;

                if (position.contains("-")) {
                    String[] positionSplit = position.split("-");
                    start = Integer.valueOf(positionSplit[0]);
                    end = Integer.valueOf(positionSplit[1]);
                } else {
                    start = Integer.valueOf(position);
                    end = start;
                }

                IntRange range = new IntRange(start, end);
                map.get(chromosome).add(range);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileWriter fw = new FileWriter(output);
                BufferedWriter bw = new BufferedWriter(fw);
                FileReader fr = new FileReader(input);
                BufferedReader br = new BufferedReader(fr)) {
            String line;
            line: while ((line = br.readLine()) != null) {

                if (line.startsWith("#")) {
                    bw.write(line);
                    bw.newLine();
                } else {
                    String[] lineSplit = line.split("\t");
                    String chromosome = lineSplit[0];
                    String position = lineSplit[1];

                    List<IntRange> rangeList = map.get(chromosome);
                    if (rangeList != null) {
                        for (IntRange range : rangeList) {
                            if (range.containsInteger(Integer.valueOf(position.trim()))) {
                                bw.write(line);
                                bw.newLine();
                                continue line;
                            }
                        }
                    }

                    if (withMissing && lineSplit.length > 3) {

                        String alternateAllele = lineSplit[4];

                        List<String> formatKeyList = Arrays.asList(lineSplit[8].split(":"));
                        List<String> formatValueList = Arrays.asList(lineSplit[9].split(":"));

                        if (!".".equals(alternateAllele.trim())) {
                            bw.write(line);
                            bw.newLine();
                        } else if (formatValueList.get(formatKeyList.indexOf("GT")).contains(".")) {
                            bw.write(line);
                            bw.newLine();
                        }

                    }

                }

                bw.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            moduleOutput.setError(new StringBuilder(e.getMessage()));
            moduleOutput.setExitCode(-1);
            return moduleOutput;
        }
        moduleOutput.setExitCode(exitCode);

        FileData fileData = new FileData();
        fileData.setName(output.getName());
        fileData.setMimeType(MimeType.TEXT_VCF);
        getFileDatas().add(fileData);

        return moduleOutput;
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
        return String.format("FilterVariant [intervalList=%s, input=%s, output=%s, withMissing=%s, toString()=%s]",
                intervalList, input, output, withMissing, super.toString());
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
            long start = System.currentTimeMillis();
            module.call();
            long end = System.currentTimeMillis();
            System.out.println((end - start) / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
