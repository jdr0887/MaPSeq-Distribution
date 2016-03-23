package edu.unc.mapseq.module.sequencing.qc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.MimeType;
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
import edu.unc.mapseq.module.sequencing.PileupBean;

/**
 * 
 * @author jdr0887
 * 
 */
@Application(name = "CoverageXTranscript")
public class CoverageAcrossTranscript extends Module {

    @NotNull(message = "pileupFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "pileupFile is not readable", groups = InputValidations.class)
    @InputArgument(description = "")
    private File pileupFile;

    @NotNull(message = "outFile is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "invalid output file", groups = OutputValidations.class)
    @InputArgument(description = "two column no header column 1 is relative position in transcript [0-100] and column 2 is counts")
    private File outFile;

    @NotNull(message = "mapFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "mapFile is not readable", groups = InputValidations.class)
    @InputArgument(description = "transcript mapfile")
    private File mapFile;

    public CoverageAcrossTranscript() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return CoverageAcrossTranscript.class;
    }

    @Override
    public ModuleOutput call() throws ModuleException {

        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();

        // normalize transcript length to 100 bases
        int xnorm = 100;
        // normalize counts to 100k read bases per transcript
        int ynorm = 100000;

        int exitCode = 0;
        try {

            Map<String, Integer> transcriptLengthMap = new HashMap<String, Integer>();
            String line;
            BufferedReader br = new BufferedReader(new FileReader(mapFile));
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split("\t");
                String chromosomeName = tokens[0];
                String coordinate = tokens[2];
                transcriptLengthMap.put(chromosomeName, Integer.valueOf(coordinate));
            }
            br.close();

            Map<String, List<PileupBean>> pileupMap = new HashMap<String, List<PileupBean>>();

            br = new BufferedReader(new FileReader(pileupFile));
            while ((line = br.readLine()) != null) {
                StringTokenizer tokenizer = new StringTokenizer(line, "\t");
                String chromosomeName = tokenizer.nextToken();
                pileupMap.put(chromosomeName, new ArrayList<PileupBean>());
            }
            br.close();

            // may need to change this so that we don't keep too much data in memory
            br = new BufferedReader(new FileReader(pileupFile));
            while ((line = br.readLine()) != null) {
                StringTokenizer tokenizer = new StringTokenizer(line, "\t");
                String chromosomeName = tokenizer.nextToken();
                String coordinate = tokenizer.nextToken();
                String referenceBase = tokenizer.nextToken();
                String readBases = tokenizer.nextToken();
                pileupMap.get(chromosomeName).add(new PileupBean(Integer.valueOf(coordinate.trim()), null,
                        Integer.valueOf(readBases.trim()), null, null));
            }
            br.close();

            Map<Long, Double> ctsMap = new HashMap<Long, Double>();
            Map<Long, List<Double>> normCtsMap = new HashMap<Long, List<Double>>();
            for (String chromosomeName : pileupMap.keySet()) {

                List<PileupBean> beanList = pileupMap.get(chromosomeName);
                int total = 0;
                for (PileupBean bean : beanList) {
                    total += bean.getReadBases();
                }

                Map<Integer, Integer> asdfMap = new HashMap<Integer, Integer>();
                for (PileupBean bean : beanList) {
                    asdfMap.put(bean.getCoordinate(), bean.getReadBases());
                }

                Integer trMapCoordinate = transcriptLengthMap.get(chromosomeName);
                for (int i = 0; i < trMapCoordinate + 1; i++) {
                    double x = ((double) (i * xnorm)) / trMapCoordinate;
                    long xRounded = Math.round(x);
                    long xx = ((x - xRounded) >= 0.5) ? xRounded + 1 : xRounded;
                    Integer c = asdfMap.get(i);
                    if (c == null) {
                        c = 0;
                    }
                    double yy = ((double) (c * ynorm)) / total;
                    if (normCtsMap.get(xx) == null) {
                        normCtsMap.put(xx, new ArrayList<Double>());
                    }
                    if (yy > 0) {
                        normCtsMap.get(xx).add(yy);
                    }
                }

                for (Long key : normCtsMap.keySet()) {
                    double yy = 0.0;
                    for (Double yValue : normCtsMap.get(key)) {
                        yy += yValue;
                    }
                    // FIX ME...perl version seems to start index at 0, in java need to do -1 to match divisor
                    ctsMap.put(key, (double) (yy / pileupMap.size() - 1));
                }

            }

            BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
            List<Long> sortedKeys = new ArrayList<Long>(ctsMap.keySet());
            Collections.sort(sortedKeys);
            for (Long key : sortedKeys) {
                bw.append(String.format("%d\t%f\n", key, ctsMap.get(key)));
            }

            bw.flush();
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

    public File getOutFile() {
        return outFile;
    }

    public void setOutFile(File outFile) {
        this.outFile = outFile;
    }

    public File getMapFile() {
        return mapFile;
    }

    public void setMapFile(File mapFile) {
        this.mapFile = mapFile;
    }

    public File getPileupFile() {
        return pileupFile;
    }

    public void setPileupFile(File pileupFile) {
        this.pileupFile = pileupFile;
    }

    @Override
    public String toString() {
        return String.format("CoverageAcrossTranscript [pileupFile=%s, outFile=%s, mapFile=%s, toString()=%s]",
                pileupFile, outFile, mapFile, super.toString());
    }

}
