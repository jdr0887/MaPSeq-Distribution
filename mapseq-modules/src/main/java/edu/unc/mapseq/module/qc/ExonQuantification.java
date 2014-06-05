package edu.unc.mapseq.module.qc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import edu.unc.mapseq.module.DefaultModuleOutput;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;

@Application(name = "ExonQuantification")
public class ExonQuantification extends Module {

    @InputArgument
    private File numberOfReads;

    @InputArgument
    private File median;

    @InputArgument
    private File compositeExons;

    @InputArgument
    private File pileup;

    @InputArgument
    private File output;

    @Override
    public ModuleOutput call() throws Exception {

        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();
        moduleOutput.setExitCode(0);

        try {
            Set<String> chromosomeSet = new HashSet<String>();
            List<CompositeExonData> compositeExonDataList = new ArrayList<CompositeExonData>();

            String line;

            BufferedReader br = new BufferedReader(new FileReader(compositeExons));
            while ((line = br.readLine()) != null) {
                line = line.trim();
                String[] array1Split = line.split(":");
                String[] array2Split = array1Split[1].split("-");

                int p1, p2 = 0;

                int v1 = Integer.valueOf(array2Split[0]);
                int v2 = Integer.valueOf(array2Split[1]);

                if (v2 > v1) {
                    p1 = v1;
                    p2 = v2;
                } else {
                    p1 = v2;
                    p2 = v1;
                }
                chromosomeSet.add(array1Split[0]);
                compositeExonDataList.add(new CompositeExonData(array1Split[0], p1, p2, null, 0));
            }
            br.close();

            BufferedWriter bw = new BufferedWriter(new FileWriter(output));
            for (CompositeExonData compositeExonData : compositeExonDataList) {
                int length = compositeExonData.getStopIndex() - compositeExonData.getStartIndex() + 1;
                int baseCounts = compositeExonData.getBaseCounts();
                int coverage = baseCounts / length;
                Integer med = Integer.valueOf(FileUtils.readFileToString(this.median).trim());
                Integer numReads = Integer.valueOf(FileUtils.readFileToString(this.numberOfReads).trim());
                double rpkm = (baseCounts / med * Math.pow(10, 9)) / (numReads * length);
                bw.write(String.format("%s\t%d\t%d\t%d", compositeExonData.getOutFormat(), baseCounts, coverage, rpkm));
            }
            bw.flush();
            bw.close();
        } catch (Exception e) {
            moduleOutput.setError(new StringBuilder(e.getMessage()));
            moduleOutput.setExitCode(-1);
            return moduleOutput;
        }

        return moduleOutput;
    }

    @Override
    public Class<?> getModuleClass() {
        return ExonQuantification.class;
    }

    public File getNumberOfReads() {
        return numberOfReads;
    }

    public void setNumberOfReads(File numberOfReads) {
        this.numberOfReads = numberOfReads;
    }

    public File getMedian() {
        return median;
    }

    public void setMedian(File median) {
        this.median = median;
    }

    public File getCompositeExons() {
        return compositeExons;
    }

    public void setCompositeExons(File compositeExons) {
        this.compositeExons = compositeExons;
    }

    public File getPileup() {
        return pileup;
    }

    public void setPileup(File pileup) {
        this.pileup = pileup;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    class CompositeExonData {

        private String chromosome;

        private Integer startIndex;

        private Integer stopIndex;

        private String outFormat;

        private Integer baseCounts;

        public CompositeExonData() {
            super();
        }

        public CompositeExonData(String chromosome, Integer startIndex, Integer stopIndex, String outFormat,
                Integer baseCounts) {
            super();
            this.chromosome = chromosome;
            this.startIndex = startIndex;
            this.stopIndex = stopIndex;
            this.outFormat = outFormat;
            this.baseCounts = baseCounts;
        }

        public String getChromosome() {
            return chromosome;
        }

        public void setChromosome(String chromosome) {
            this.chromosome = chromosome;
        }

        public Integer getStartIndex() {
            return startIndex;
        }

        public void setStartIndex(Integer startIndex) {
            this.startIndex = startIndex;
        }

        public Integer getStopIndex() {
            return stopIndex;
        }

        public void setStopIndex(Integer stopIndex) {
            this.stopIndex = stopIndex;
        }

        public String getOutFormat() {
            return outFormat;
        }

        public void setOutFormat(String outFormat) {
            this.outFormat = outFormat;
        }

        public Integer getBaseCounts() {
            return baseCounts;
        }

        public void setBaseCounts(Integer baseCounts) {
            this.baseCounts = baseCounts;
        }

    }

}
