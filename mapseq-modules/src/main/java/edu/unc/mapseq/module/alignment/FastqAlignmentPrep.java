package edu.unc.mapseq.module.alignment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;

import edu.unc.mapseq.module.DefaultModuleOutput;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleException;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsNotEmpty;
import edu.unc.mapseq.module.constraints.FileIsReadable;

/**
 * 
 * @author jdr0887
 * 
 */
@Application(name = "FastqAlignmentPrep")
public class FastqAlignmentPrep extends Module {

    @NotNull(message = "r1Fastq is required", groups = InputValidations.class)
    @FileIsReadable(message = "r1Fastq is not readable", groups = InputValidations.class)
    @FileIsNotEmpty(message = "r1Fastq is empty", groups = InputValidations.class)
    @InputArgument
    private File r1Fastq;

    @NotNull(message = "r2Fastq is required", groups = InputValidations.class)
    @FileIsReadable(message = "r2Fastq is not readable", groups = InputValidations.class)
    @FileIsNotEmpty(message = "r2Fastq is empty", groups = InputValidations.class)
    @InputArgument
    private File r2Fastq;

    @NotNull(message = "r1FastqOutput is required", groups = InputValidations.class)
    @FileIsReadable(message = "r1FastqOutput is not readable", groups = OutputValidations.class)
    @FileIsNotEmpty(message = "r1FastqOutput is empty", groups = OutputValidations.class)
    @OutputArgument
    private File r1FastqOutput;

    @NotNull(message = "r2FastqOutput is required", groups = InputValidations.class)
    @FileIsReadable(message = "r2FastqOutput is not readable", groups = OutputValidations.class)
    @FileIsNotEmpty(message = "r2FastqOutput is empty", groups = OutputValidations.class)
    @OutputArgument
    private File r2FastqOutput;

    @NotNull(message = "fastqUnpairedReadsOutput is required", groups = InputValidations.class)
    @FileIsReadable(message = "fastqUnpairedReadsOutput is not readable", groups = OutputValidations.class)
    @FileIsNotEmpty(message = "fastqUnpairedReadsOutput is empty", groups = OutputValidations.class)
    @OutputArgument
    private File fastqUnpairedReadsOutput;

    @Override
    public Class<?> getModuleClass() {
        return FastqAlignmentPrep.class;
    }

    @Override
    public ModuleOutput call() throws ModuleException {

        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();

        int exitCode = 0;
        try {

            BufferedWriter r1FastqBW = new BufferedWriter(new FileWriter(r1FastqOutput));
            BufferedWriter r2FastqBW = new BufferedWriter(new FileWriter(r2FastqOutput));
            BufferedWriter unpairedReadsFastqBW = new BufferedWriter(new FileWriter(fastqUnpairedReadsOutput));

            BufferedReader r1FastqBR = new BufferedReader(new FileReader(r1Fastq));
            BufferedReader r2FastqBR = new BufferedReader(new FileReader(r2Fastq));

            while (true) {

                String r1FastqIdentifier = r1FastqBR.readLine();
                String actualR1Id = null;

                String[] seqIdSpaceSplit = r1FastqIdentifier.split(" ");
                if (seqIdSpaceSplit != null && seqIdSpaceSplit.length > 0) {
                    String[] seqIdPoundSplit = seqIdSpaceSplit[0].split("#");
                    if (seqIdPoundSplit != null && seqIdPoundSplit.length > 0) {
                        actualR1Id = seqIdPoundSplit[0].replace("@", "");
                    }
                }

                String r2FastqIdentifier = r2FastqBR.readLine();
                String actualR2Id = null;

                seqIdSpaceSplit = r2FastqIdentifier.split(" ");
                if (seqIdSpaceSplit != null && seqIdSpaceSplit.length > 0) {
                    String[] seqIdPoundSplit = seqIdSpaceSplit[0].split("#");
                    if (seqIdPoundSplit != null && seqIdPoundSplit.length > 0) {
                        actualR2Id = seqIdPoundSplit[0].replace("@", "");
                    }
                }

                // need to set the break on reading just one line from each file
                if (StringUtils.isEmpty(actualR1Id) && StringUtils.isEmpty(actualR2Id)) {
                    break;
                }

                String r1FastqSequence = r1FastqBR.readLine();
                String r1FastqDescription = r1FastqBR.readLine();
                String r1FastqQualityValues = r1FastqBR.readLine();

                String r2FastqSequence = r2FastqBR.readLine();
                String r2FastqDescription = r2FastqBR.readLine();
                String r2FastqQualityValues = r2FastqBR.readLine();

                if (StringUtils.isNotEmpty(actualR1Id) && StringUtils.isNotEmpty(actualR2Id)
                        && actualR1Id.equals(actualR2Id)) {
                    r1FastqBW.write(String.format("%s\n%s\n%s\n%s\n", r1FastqIdentifier, r1FastqSequence,
                            r1FastqDescription, r1FastqQualityValues));
                    r1FastqBW.flush();
                    r2FastqBW.write(String.format("%s\n%s\n%s\n%s\n", r2FastqIdentifier, r2FastqSequence,
                            r2FastqDescription, r2FastqQualityValues));
                    r2FastqBW.flush();
                } else {
                    unpairedReadsFastqBW.write(String.format("%s\n%s\n%s\n%s\n", r1FastqIdentifier, r1FastqSequence,
                            r1FastqDescription, r1FastqQualityValues));
                    unpairedReadsFastqBW.write(String.format("%s\n%s\n%s\n%s\n", r2FastqIdentifier, r2FastqSequence,
                            r2FastqDescription, r2FastqQualityValues));
                    unpairedReadsFastqBW.flush();
                }

            }

            r1FastqBW.close();
            r2FastqBW.close();
            unpairedReadsFastqBW.close();

            r1FastqBR.close();
            r2FastqBR.close();

        } catch (Exception e) {
            e.printStackTrace();
            moduleOutput.setError(new StringBuilder(e.getMessage()));
            moduleOutput.setExitCode(exitCode);
            return moduleOutput;
        }

        moduleOutput.setExitCode(exitCode);

        return moduleOutput;

    }

    public File getR1Fastq() {
        return r1Fastq;
    }

    public void setR1Fastq(File r1Fastq) {
        this.r1Fastq = r1Fastq;
    }

    public File getR2Fastq() {
        return r2Fastq;
    }

    public void setR2Fastq(File r2Fastq) {
        this.r2Fastq = r2Fastq;
    }

    public File getR1FastqOutput() {
        return r1FastqOutput;
    }

    public void setR1FastqOutput(File r1FastqOutput) {
        this.r1FastqOutput = r1FastqOutput;
    }

    public File getR2FastqOutput() {
        return r2FastqOutput;
    }

    public void setR2FastqOutput(File r2FastqOutput) {
        this.r2FastqOutput = r2FastqOutput;
    }

    public File getFastqUnpairedReadsOutput() {
        return fastqUnpairedReadsOutput;
    }

    public void setFastqUnpairedReadsOutput(File fastqUnpairedReadsOutput) {
        this.fastqUnpairedReadsOutput = fastqUnpairedReadsOutput;
    }

}
