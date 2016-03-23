package edu.unc.mapseq.module.sequencing.qc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.module.DefaultModuleOutput;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleException;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;

/**
 * 
 */
@Application(name = "NormBedExonQuant")
public class NormBedExonQuant extends Module {

    private final Logger logger = LoggerFactory.getLogger(NormBedExonQuant.class);

    @InputArgument
    private File compositeBed;

    @InputArgument
    private File inFile;

    @InputArgument
    private File outFile;

    public NormBedExonQuant() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return NormBedExonQuant.class;
    }

    @Override
    public ModuleOutput call() throws ModuleException {

        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();

        int exitCode = 0;
        try {

            ArrayList<String> outputOrder = new ArrayList<String>();

            String line;
            int lineCount = 0;
            BufferedReader br1 = new BufferedReader(new FileReader(compositeBed));
            while ((line = br1.readLine()) != null) {
                String id = (line.split("\t"))[4];
                outputOrder.add(lineCount++, id);
            }
            br1.close();

            Map<String, String> exonData = new HashMap<String, String>();

            int total = 0;
            BufferedReader br2 = new BufferedReader(new FileReader(inFile));
            while ((line = br2.readLine()) != null) {
                logger.info(line);
                String[] splitArray = line.split("\t");
                String id = splitArray[4];
                String sCounts = splitArray[5];
                String length = splitArray[7];
                String cov = splitArray[8];
                Integer counts = Integer.valueOf(sCounts);
                total += counts;

                exonData.put(id, sCounts + "\t" + length + "\t" + cov);
            }
            br2.close();

            BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
            int outputLength = outputOrder.size();
            for (int i = 0; i <= outputLength; i++) {
                String id = outputOrder.get(i);
                String exonDataValue = exonData.get(id);
                if (exonDataValue != null) {
                    String[] splitArray = exonDataValue.split("\t");
                    String sCounts = splitArray[0];
                    String sLength = splitArray[1];
                    String cov = splitArray[2];

                    Integer counts = Integer.valueOf(sCounts);
                    Integer length = Integer.valueOf(sLength);

                    Double rpkm = 1.0e9 * counts / ((length + 1.0) * total);
                    bw.write(String.format("%s\t%s\t%s\t%24.12f\n", id, sCounts, cov, rpkm));
                }
            }
            bw.close();

            FileData fm = new FileData();
            fm.setMimeType(MimeType.TEXT_STAT_SUMMARY);
            fm.setName(outFile.getName());
            getFileDatas().add(fm);

        } catch (Exception e) {
            e.printStackTrace();
            moduleOutput.setError(new StringBuilder(e.getMessage()));
            moduleOutput.setExitCode(exitCode);
            return moduleOutput;
        }

        moduleOutput.setExitCode(exitCode);

        return moduleOutput;

    }

    public File getCompositeBed() {
        return compositeBed;
    }

    public void setCompositeBed(File compositeBed) {
        this.compositeBed = compositeBed;
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
        return String.format("NormBedExonQuant [logger=%s, compositeBed=%s, inFile=%s, outFile=%s, toString()=%s]",
                logger, compositeBed, inFile, outFile, super.toString());
    }

    public static void main(String[] args) {
        NormBedExonQuant module = new NormBedExonQuant();
        module.setWorkflowName("TEST");
        module.setDryRun(Boolean.TRUE);
        module.setCompositeBed(new File("/tmp", "composite_exons.bed"));
        module.setInFile(
                new File("/tmp", "150514_UNC11-SN627_0400_AC5J46ACXX_GTCCGC_L005.fixed-rg.sorted.coverageBedOut.txt"));
        module.setOutFile(new File("/tmp",
                "150514_UNC11-SN627_0400_AC5J46ACXX_GTCCGC_L005.fixed-rg.sorted.normBedExonQuantOut.txt"));

        try {
            module.call();
        } catch (ModuleException e) {
            e.printStackTrace();
        }

    }
}
