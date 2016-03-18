package edu.unc.mapseq.module.impl.qc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

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

/**
 * 
 * @author jdr0887
 * 
 */
@Application(name = "GenomeSampleQC")
public class GenomeSampleQC extends Module {

    @NotNull(message = "samFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "Input file is not readable: samFile", groups = InputValidations.class)
    @InputArgument
    private File samFile;

    @NotNull(message = "outFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "File was not created: outFile", groups = OutputValidations.class)
    @InputArgument
    private File outFile;

    @NotNull(message = "key is required", groups = InputValidations.class)
    @InputArgument
    private String key;

    @NotNull(message = "propertiesFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "Input file is not readable: propertiesFile", groups = OutputValidations.class)
    @InputArgument
    private File propertiesFile;

    public GenomeSampleQC() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return GenomeSampleQC.class;
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

            int count = 0;

            String line;
            BufferedReader br = new BufferedReader(new FileReader(samFile));
            while ((line = br.readLine()) != null) {
                if (line.startsWith("@")) {
                    continue;
                }
                StringTokenizer st = new StringTokenizer(line, "\t");
                String c1 = st.nextToken();
                String c2 = st.nextToken();
                String c3 = st.nextToken();

                Map<Integer, Integer> fMap = new HashMap<Integer, Integer>();
                Integer thisN = 1024 * 2;
                int in = Integer.valueOf(c2);
                for (int i = 10; i >= 0; i--) {
                    thisN = thisN / 2;
                    if (in >= thisN) {
                        fMap.put(i, 1);
                        in -= thisN;
                    } else {
                        fMap.put(i, 0);
                    }
                }
                if (fMap.get(2) == 1) {
                    continue;
                }
                ++count;

            }

            br.close();

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

    public File getSamFile() {
        return samFile;
    }

    public void setSamFile(File samFile) {
        this.samFile = samFile;
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
        return String.format("GenomeSampleQC [samFile=%s, outFile=%s, key=%s, propertiesFile=%s, toString()=%s]",
                samFile, outFile, key, propertiesFile, super.toString());
    }

}
