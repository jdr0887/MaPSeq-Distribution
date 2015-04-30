package edu.unc.mapseq.module.alignment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.module.DefaultModuleOutput;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.PileupBean;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsNotEmpty;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "PileupToBedGraph")
public class PileupToBedGraph extends Module {

    @NotNull(message = "inFile is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "inFile is empty", groups = InputValidations.class)
    @FileIsReadable(message = "inFile is not readable", groups = InputValidations.class)
    @InputArgument
    private File inFile;

    @NotNull(message = "outFile is required", groups = OutputValidations.class)
    @FileIsNotEmpty(message = "outFile is empty", groups = OutputValidations.class)
    @FileIsReadable(message = "outFile is not readable", groups = OutputValidations.class)
    @OutputArgument
    private File outFile;

    @NotNull(message = "chromosomeSizes is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "chromosomeSizes is empty", groups = InputValidations.class)
    @FileIsReadable(message = "chromosomeSizes is not readable", groups = InputValidations.class)
    @InputArgument
    private File chromosomeSizes;

    @Override
    public Class<?> getModuleClass() {
        return PileupToBedGraph.class;
    }

    @Override
    public ModuleOutput call() throws Exception {

        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();

        int exitCode = 0;
        try {

            List<String> chromosomeList = new ArrayList<String>();
            for (int i = 1; i < 23; i++) {
                chromosomeList.add("chr" + i);
            }
            chromosomeList.add("chrX");
            chromosomeList.add("chrY");
            chromosomeList.add("chrM");

            Map<String, Integer> chromosomeSizesMap = new HashMap<String, Integer>();
            BufferedReader br = new BufferedReader(new FileReader(chromosomeSizes));
            String line;
            while ((line = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line);
                chromosomeSizesMap.put(st.nextToken(), Integer.valueOf(st.nextToken()));
            }

            BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));

            for (String chromosome : chromosomeList) {

                List<PileupBean> pileupList = new ArrayList<PileupBean>();

                br = new BufferedReader(new FileReader(inFile));
                while ((line = br.readLine()) != null) {
                    StringTokenizer st = new StringTokenizer(line, "\t");
                    String chromosomeName = st.nextToken();
                    String coordinate = st.nextToken();
                    String referenceBase = st.nextToken();
                    String consensusBases = st.nextToken();
                    // to reduce memory consumed, read in only one chromosome at a time
                    if (chromosome.equals(chromosomeName)) {
                        pileupList.add(new PileupBean(Integer.valueOf(coordinate), consensusBases, null, null, null,
                                null, null, null, null));
                    }
                }
                br.close();

                String format = "%s\t%d\t%d\t%s\n";

                int x1 = 0;
                int x2 = 0;

                for (int i = 0; i < pileupList.size(); ++i) {
                    PileupBean bean = pileupList.get(i);

                    if (i == 0) {
                        x2 = bean.getCoordinate() - 1;
                        bw.write(String.format(format, chromosome, x1, x2, 0));
                        x1 = x2;
                    } else {
                        PileupBean previousBean = pileupList.get(i - 1);
                        if (bean.getCoordinate() == previousBean.getCoordinate() + 1) {
                            continue;
                        }

                        x2 = previousBean.getCoordinate();
                        bw.write(String.format(format, chromosome, x1, x2, previousBean.getConsensusBases()));
                        x1 = x2;

                        x2 = bean.getCoordinate() - 1;
                        if (x1 > x2) {
                            continue;
                        }

                        if (x2 > previousBean.getCoordinate()) {
                            int chromosomeSize = chromosomeSizesMap.get(chromosome);
                            if (x2 > chromosomeSize) {
                                x2 = chromosomeSize;
                            }
                            bw.write(String.format(format, chromosome, x1, x2, 0));
                        } else {
                            bw.write(String.format(format, chromosome, x1, x2, bean.getConsensusBases()));
                        }
                        x1 = x2;

                    }

                    bw.flush();
                }

            }

            bw.close();

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

    public File getChromosomeSizes() {
        return chromosomeSizes;
    }

    public void setChromosomeSizes(File chromosomeSizes) {
        this.chromosomeSizes = chromosomeSizes;
    }

}
