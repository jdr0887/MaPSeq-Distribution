package edu.unc.mapseq.module.qc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.validation.constraints.NotNull;

import org.apache.commons.io.FileUtils;

import edu.unc.mapseq.module.DefaultModuleOutput;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleException;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "RandomFastq")
public class RandomSampleFastq extends Module {

    @NotNull(message = "fastq is required", groups = InputValidations.class)
    @FileIsReadable(message = "Input file is not readable: fastq", groups = InputValidations.class)
    @InputArgument
    private File fastq;

    @NotNull(message = "outFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "File was not created: fastq", groups = OutputValidations.class)
    @InputArgument
    private File outFile;

    @NotNull(message = "propertiesFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "File was not created: propertiesFile", groups = OutputValidations.class)
    @InputArgument
    private File propertiesFile;

    public RandomSampleFastq() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return RandomSampleFastq.class;
    }

    @Override
    public ModuleOutput call() throws ModuleException {

        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();

        int exitCode = 0;
        try {

            BufferedReader br = new BufferedReader(new FileReader(fastq));
            Integer lineCount = 0;
            String line;
            while ((line = br.readLine()) != null) {
                ++lineCount;
            }
            br.close();

            Integer totalReads = lineCount / 4;
            Long totalSamples = Math.round(0.01 * totalReads);

            Map<Integer, Boolean> readMap = new HashMap<Integer, Boolean>();
            Random random = new Random();
            for (int i = 0; i < totalSamples; ++i) {
                Integer key = random.nextInt(totalReads);
                if (readMap.containsKey(key)) {
                    continue;
                }
                readMap.put(key, true);
            }

            Integer readCount = -1;

            if (outFile.exists()) {
                outFile.delete();
            }
            FileUtils.touch(outFile);

            BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
            br = new BufferedReader(new FileReader(fastq));
            while ((line = br.readLine()) != null) {
                if (line.startsWith("@")) {
                    readCount++;
                    Boolean canRead = readMap.get(readCount) != null && readMap.get(readCount) ? true : false;
                    if (canRead) {
                        bw.write(line);
                        bw.write(br.readLine());
                        bw.write(br.readLine());
                        bw.write(br.readLine());
                        bw.flush();
                    }
                }
            }
            br.close();
            bw.close();

            if (propertiesFile.exists()) {
                propertiesFile.delete();
            }
            FileUtils.touch(propertiesFile);
            FileUtils.writeStringToFile(propertiesFile, String.format("sampleTotal=%d\n", totalSamples));

        } catch (Exception e) {
            e.printStackTrace();
            moduleOutput.setError(new StringBuilder(e.getMessage()));
            moduleOutput.setExitCode(exitCode);
        }

        moduleOutput.setExitCode(exitCode);

        return moduleOutput;

    }

    public File getFastq() {
        return fastq;
    }

    public void setFastq(File fastq) {
        this.fastq = fastq;
    }

    public File getOutFile() {
        return outFile;
    }

    public void setOutFile(File outFile) {
        this.outFile = outFile;
    }

    public File getPropertiesFile() {
        return propertiesFile;
    }

    public void setPropertiesFile(File propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    @Override
    public String toString() {
        return String.format("RandomSampleFastq [fastq=%s, outFile=%s, propertiesFile=%s, toString()=%s]", fastq,
                outFile, propertiesFile, super.toString());
    }

}
