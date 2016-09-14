package edu.unc.mapseq.module.sequencing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.module.DefaultModuleOutput;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;

@Application(name = "SureSelectTriggerSplitter")
public class SureSelectTriggerSplitter extends Module {

    private static final Logger logger = LoggerFactory.getLogger(SureSelectTriggerSplitter.class);

    @NotNull(message = "workDirectory", groups = InputValidations.class)
    @InputArgument(description = "workdir")
    private File workDirectory;

    @NotNull(message = "intervalList is required", groups = InputValidations.class)
    @InputArgument(description = "SureSelect panel interval file in NC_000001.10:762022-762345, for example, format")
    private File intervalList;

    @NotNull(message = "subjectName is required", groups = InputValidations.class)
    @InputArgument(description = "Sample ID")
    private String subjectName;

    @NotNull(message = "gender is required", groups = InputValidations.class)
    @InputArgument(description = "Sample gender (F|M)")
    private String gender;

    @NotNull(message = "numberOfSubsets is required", groups = InputValidations.class)
    @InputArgument(description = "Number of subsets")
    private Integer numberOfSubsets;

    @NotNull(message = "par1Coordinate is required", groups = InputValidations.class)
    @InputArgument(description = "ChrX PAR1 coordinate in 60001-2699520, for example hg19, format (1-base)")
    private String par1Coordinate;

    @NotNull(message = "par2Coordinate is required", groups = InputValidations.class)
    @InputArgument(description = "ChrX PAR2 coordinate in 154931044-155260560, for example hg19, format (1-base)")
    private String par2Coordinate;

    @NotNull(message = "output is required", groups = InputValidations.class)
    @OutputArgument(description = "Output file base name")
    private String outputPrefix;

    @Override
    public Class<?> getModuleClass() {
        return SureSelectTriggerSplitter.class;
    }

    @Override
    public ModuleOutput call() throws Exception {
        logger.debug("ENTERING call()");

        logger.info(this.toString());

        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();
        moduleOutput.setExitCode(0);
        
        String[] par1CoordinateSplit = par1Coordinate.split("-");
        Range<Integer> p1Range = Range.between(Integer.valueOf(par1CoordinateSplit[0]), Integer.valueOf(par1CoordinateSplit[1]));

        String[] par2CoordinateSplit = par2Coordinate.split("-");
        Range<Integer> p2Range = Range.between(Integer.valueOf(par2CoordinateSplit[0]), Integer.valueOf(par2CoordinateSplit[1]));

        List<String> lines = FileUtils.readLines(intervalList);

        if (numberOfSubsets >= lines.size()) {
            logger.error("Number of subsets is greater than the number of rows");
            moduleOutput.setExitCode(1);
            return moduleOutput;
        }

        Integer chunks = Math.abs(lines.size() / numberOfSubsets);
        if ((chunks % numberOfSubsets) != 0) {
            chunks++;
        }

        logger.info("Max number of intervals per subset: {}", chunks);

        File ploidyBedFile = new File(workDirectory, String.format("%s.ploidy.bed", outputPrefix));

        try (FileWriter ploidyFW = new FileWriter(ploidyBedFile); BufferedWriter ploidyBW = new BufferedWriter(ploidyFW)) {

            int previousChunkIndex = 0;
            for (int i = 0; i < numberOfSubsets; ++i) {

                int currentChunkIndex = (i + 1) * chunks;
                if (currentChunkIndex > lines.size()) {
                    currentChunkIndex = lines.size();
                }
                List<String> subLines = lines.subList(previousChunkIndex, currentChunkIndex);
                File intervalBedFile = new File(workDirectory, String.format("%s.interval.set%d.bed", outputPrefix, i + 1));
                try (FileWriter intervalFW = new FileWriter(intervalBedFile); BufferedWriter intervalBW = new BufferedWriter(intervalFW)) {
                    for (String line : subLines) {
                        String[] lineSplit = line.split(":");
                        String chromosome = lineSplit[0];
                        String range = lineSplit[1];
                        String[] rangeSplit = range.split("-");

                        Range<Integer> r = Range.between(Integer.valueOf(rangeSplit[0]), Integer.valueOf(rangeSplit[1]));

                        intervalBW.write(String.format("%s\t%d\t%d", chromosome, r.getMinimum() - 1, r.getMaximum()));
                        intervalBW.newLine();
                        intervalBW.flush();

                        if (chromosome.contains("NC_000023")) {
                            if ("M".equals(gender) && (p1Range.isBeforeRange(r) || p2Range.isBeforeRange(r))) {
                                ploidyBW.write(String.format("%s\t%d\t%d\t%s\t%d", chromosome, r.getMinimum() - 1, r.getMaximum(),
                                        subjectName, 1));
                                ploidyBW.newLine();
                                ploidyBW.flush();
                            }
                        }

                        if (chromosome.contains("NC_000024")) {
                            ploidyBW.write(String.format("%s\t%d\t%d\t%s\t%d", chromosome, r.getMinimum() - 1, r.getMaximum(), subjectName,
                                    "F".equals(gender) ? 0 : 1));
                            ploidyBW.newLine();
                            ploidyBW.flush();
                        }

                    }
                }

                previousChunkIndex = currentChunkIndex;
            }

        } catch (IOException e) {
            logger.error(e.getMessage());
            moduleOutput.setExitCode(1);
        }
        return moduleOutput;
    }

    public File getWorkDirectory() {
        return workDirectory;
    }

    public void setWorkDirectory(File workDirectory) {
        this.workDirectory = workDirectory;
    }

    public File getIntervalList() {
        return intervalList;
    }

    public void setIntervalList(File intervalList) {
        this.intervalList = intervalList;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getNumberOfSubsets() {
        return numberOfSubsets;
    }

    public void setNumberOfSubsets(Integer numberOfSubsets) {
        this.numberOfSubsets = numberOfSubsets;
    }

    public String getPar1Coordinate() {
        return par1Coordinate;
    }

    public void setPar1Coordinate(String par1Coordinate) {
        this.par1Coordinate = par1Coordinate;
    }

    public String getPar2Coordinate() {
        return par2Coordinate;
    }

    public void setPar2Coordinate(String par2Coordinate) {
        this.par2Coordinate = par2Coordinate;
    }

    public String getOutputPrefix() {
        return outputPrefix;
    }

    public void setOutputPrefix(String outputPrefix) {
        this.outputPrefix = outputPrefix;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    @Override
    public String toString() {
        return String.format(
                "SureSelectTriggerSplitter [intervalList=%s, subjectName=%s, gender=%s, numberOfSubsets=%s, par1Coordinate=%s, par2Coordinate=%s, outputPrefix=%s]",
                intervalList, subjectName, gender, numberOfSubsets, par1Coordinate, par2Coordinate, outputPrefix);
    }

    public static void main(String[] args) {
        SureSelectTriggerSplitter module = new SureSelectTriggerSplitter();
        module.setWorkflowName("TEST");
        module.setWorkDirectory(new File("/tmp"));
        module.setNumberOfSubsets(12);
        module.setSubjectName("NCG_1234");
        module.setPar1Coordinate("10001-2781479");
        module.setPar2Coordinate("155701383-156030895");
        module.setGender("M");
        module.setIntervalList(new File("/tmp", "agilent_v4_capture_region_pm_75.shortid.interval_list"));
        module.setOutputPrefix("qwer");
        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
