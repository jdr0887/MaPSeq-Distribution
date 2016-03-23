package edu.unc.mapseq.module.sequencing.qc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.validation.constraints.NotNull;

import org.apache.commons.io.FileUtils;
import org.biojava3.core.sequence.RNASequence;

import edu.unc.mapseq.module.DefaultModuleOutput;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleException;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;

/**
 * 
 * @author jdr0887
 * 
 */
@Application(name = "RNASampleQC")
public class RNASampleQC extends Module {

    @NotNull(message = "fastqFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "Input file is not readable: fastqFile", groups = InputValidations.class)
    @InputArgument
    private File fastq;

    @NotNull(message = "fastaDB is required", groups = InputValidations.class)
    @FileIsReadable(message = "Input file is not readable: fastaDB", groups = InputValidations.class)
    @InputArgument
    private File fastaDB;

    @NotNull(message = "outFile is required", groups = InputValidations.class)
    @InputArgument
    private File outFile;

    @NotNull(message = "key is required", groups = InputValidations.class)
    @InputArgument
    private String key;

    @NotNull(message = "propertiesFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "Input file is not readable: propertiesFile", groups = InputValidations.class)
    @InputArgument
    private File propertiesFile;

    public RNASampleQC() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return RNASampleQC.class;
    }

    @Override
    public ModuleOutput call() throws ModuleException {

        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();

        Properties props = new Properties();

        int exitCode = 0;
        try {

            props.load(new FileInputStream(propertiesFile));

            if (props.contains("sampleTotal")) {
                throw new ModuleException("sampleTotal not found in properties file");
            }

            Map<String, Boolean> microRNASequenceMap = new HashMap<String, Boolean>();

            String line;
            BufferedReader br = new BufferedReader(new FileReader(fastaDB));
            while ((line = br.readLine()) != null) {
                if (line.startsWith(">")) {
                    continue;
                }
                line = line.replace(" ", "");
                microRNASequenceMap.put(line, true);
                RNASequence rnaSeq = new RNASequence(line);
                String complement = rnaSeq.getComplement().getSequenceAsString();
                microRNASequenceMap.put(complement, true);
            }
            br.close();

            int count = 0;
            StringBuilder sb = new StringBuilder();
            if (microRNASequenceMap.size() == 0) {
                sb.append(key).append("\tERROR: no_ref_seqs\n");
            } else {
                br = new BufferedReader(new FileReader(fastq));
                while ((line = br.readLine()) != null) {

                    for (String microRNA : microRNASequenceMap.keySet()) {
                        if (line.equals(microRNA)) {
                            ++count;
                            break;
                        }
                    }
                    br.readLine();
                    br.readLine();

                }
                br.close();
            }

            String sampleTotal = props.getProperty("sampleTotal");
            FileUtils.touch(outFile);
            FileUtils
                    .writeStringToFile(
                            outFile, String.format("%s.count = %d\n%s.sampleTotal = %s\n%s.percentile = %d", key, count,
                                    key, sampleTotal, key, Float.valueOf(100 * count / Integer.valueOf(sampleTotal))),
                    true);

        } catch (Exception e) {
            e.printStackTrace();
            moduleOutput.setError(new StringBuilder(e.getMessage()));
            moduleOutput.setExitCode(exitCode);
            return moduleOutput;
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

    public File getFastaDB() {
        return fastaDB;
    }

    public void setFastaDB(File fastaDB) {
        this.fastaDB = fastaDB;
    }

    public File getOutFile() {
        return outFile;
    }

    public void setOutFile(File outFile) {
        this.outFile = outFile;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public File getPropertiesFile() {
        return propertiesFile;
    }

    public void setPropertiesFile(File propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    @Override
    public String toString() {
        return String.format("RNASampleQC [fastq=%s, fastaDB=%s, outFile=%s, key=%s, propertiesFile=%s, toString()=%s]",
                fastq, fastaDB, outFile, key, propertiesFile, super.toString());
    }

}
