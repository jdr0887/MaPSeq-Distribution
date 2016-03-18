package edu.unc.mapseq.module.impl.qc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.module.DefaultModuleOutput;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleException;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsNotEmpty;
import edu.unc.mapseq.module.constraints.FileIsReadable;

/**
 * 
 * @author jdr0887
 * 
 */
@Application(name = "AlignStat")
public class AlignStat extends Module {

    @NotNull(message = "inFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "Input file is not readable", groups = InputValidations.class)
    @InputArgument(description = "Input BAM file")
    private File inFile;

    @NotNull(message = "outFile is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "invalid output file", groups = OutputValidations.class)
    @InputArgument(description = "Output file flowcell.lane.alignStat.txt")
    private File outFile;

    public AlignStat() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return AlignStat.class;
    }

    @Override
    public ModuleOutput call() throws ModuleException {

        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();

        int exitCode = 0;
        try {

            int totalCount = 0;
            int mappedCount = 0;
            String line;
            BufferedReader br = new BufferedReader(new FileReader(inFile));
            while ((line = br.readLine()) != null) {
                if (line.startsWith("@")) {
                    continue;
                }
                StringTokenizer tokenizer = new StringTokenizer("\t", line);
                ++totalCount;
                String c1 = tokenizer.nextToken();
                String c2 = tokenizer.nextToken();
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
                ++mappedCount;
            }
            br.close();

            int percent = (mappedCount / totalCount) * 100;

            Properties props = new Properties();
            props.put("alignstat_query_file", inFile.getAbsolutePath());
            props.put("alignstat_total_read", totalCount);
            props.put("alignstat_mapped_read", mappedCount);
            props.put("alignstat_mapped_percent", percent);
            props.put("alignstat_flag", (percent < 30 || percent > 90) ? 1 : 0);

            FileWriter fw = new FileWriter(outFile);
            props.store(fw, null);
            fw.flush();
            fw.close();

        } catch (Exception e) {
            e.printStackTrace();
            moduleOutput.setError(new StringBuilder(e.getMessage()));
            moduleOutput.setExitCode(exitCode);
            return moduleOutput;
        }

        moduleOutput.setExitCode(exitCode);

        return moduleOutput;
    }

    public File getInFile() {
        return inFile;
    }

    public void setInFile(File inFile) {
        this.inFile = inFile;
    }

    public File getOutFile() {
        return outFile;
    }

    public void setOutFile(File outFile) {
        this.outFile = outFile;
    }

    @Override
    public String toString() {
        return String.format("AlignStat [inFile=%s, outFile=%s, toString()=%s]", inFile, outFile, super.toString());
    }

}
