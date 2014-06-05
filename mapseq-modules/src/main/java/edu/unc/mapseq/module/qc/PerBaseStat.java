package edu.unc.mapseq.module.qc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

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
@Application(name = "PerBaseStat")
public class PerBaseStat extends Module {

    @InputArgument()
    private File inFile;

    @InputArgument()
    private File outFile;

    public PerBaseStat() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return PerBaseStat.class;
    }

    @Override
    public ModuleOutput call() throws ModuleException {

        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();

        int cycle1 = 36;
        int cycle2 = 50;
        int cycle3 = 75;

        int exitCode = 0;
        try {
            int lineCount = 0;

            BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
            bw.write(String.format("File name: %s\n", outFile.getAbsolutePath()));

            String line;
            BufferedReader br = new BufferedReader(new FileReader(inFile));
            while ((line = br.readLine()) != null) {
                ++lineCount;
                if ((lineCount != cycle1) && (lineCount != cycle2) && (lineCount != cycle3)) {
                    continue;
                }
                String[] tokens = line.trim().split("\t");

                int q1 = Integer.valueOf(tokens[6]);
                bw.write(String.format("Cycle_%d_25_percent\t%d\n", lineCount, q1));

                int median = Integer.valueOf(tokens[7]);
                bw.write(String.format("Cycle_%d_median\t%d\n", lineCount, median));

                int q3 = Integer.valueOf(tokens[8]);
                bw.write(String.format("Cycle_%d_75_percent\t%d\n", lineCount, q3));
                bw.flush();
            }
            bw.close();
            br.close();

            FileData fm = new FileData();
            fm.setMimeType(MimeType.TEXT_STAT_SUMMARY);
            fm.setName(outFile.getName());
            addFileData(fm);

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

}
