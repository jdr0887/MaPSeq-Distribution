package edu.unc.mapseq.module.impl.qc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;

import edu.unc.mapseq.module.Module;
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
@Application(name = "GeneCountsRPKM")
public class GeneCountsRPKM extends Module {

    @NotNull(message = "samFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "Input file is not readable: bamFile", groups = InputValidations.class)
    @InputArgument
    private File samFile;

    @NotNull(message = "transcriptDB is required", groups = InputValidations.class)
    @FileIsReadable(message = "Input file is not readable: transcriptDB", groups = InputValidations.class)
    @InputArgument
    private File transcriptDB;

    @NotNull(message = "geneLength is required", groups = InputValidations.class)
    @InputArgument
    private String geneLength;

    @NotNull(message = "outTR is required", groups = InputValidations.class)
    @FileIsReadable(message = "Output file is not readable: outTR", groups = OutputValidations.class)
    @InputArgument
    private File outTR;

    @NotNull(message = "outGENE is required", groups = InputValidations.class)
    @FileIsReadable(message = "Output file is not readable: outGENE", groups = OutputValidations.class)
    @InputArgument
    private File outGENE;

    @Override
    public Class<?> getModuleClass() {
        return GeneCountsRPKM.class;
    }

    @Override
    public ModuleOutput call() throws Exception {

        List<String> toList = new ArrayList<String>();
        List<String> gene2LengthList = new ArrayList<String>();
        Map<String, String> transcript2GeneMap = new HashMap<String, String>();
        Map<String, String> gene2LengthMap = new HashMap<String, String>();

        Map<String, Integer> transcriptLengthMap = new HashMap<String, Integer>();
        Map<String, Integer> geneLengthMap = new HashMap<String, Integer>();
        String line;
        BufferedReader br = new BufferedReader(new FileReader(transcriptDB));
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("\t");
            String c1 = tokens[0];
            String c2 = tokens[1];
            String c3 = tokens[2];
            transcriptLengthMap.put(c1, Integer.valueOf(c3));
            toList.add(c1);
            if (StringUtils.isEmpty(c2)) {
                transcript2GeneMap.put(c1, c2);
                if (gene2LengthMap.containsKey(c2)) {
                    gene2LengthMap.put(c2, gene2LengthMap.get(c2) + c3 + ",");
                } else {
                    gene2LengthMap.put(c2, c3);
                }
            }

        }
        br.close();

        List<String> goList = new ArrayList<String>();
        for (String key : gene2LengthMap.keySet()) {
            String value = gene2LengthMap.get(key);
            String[] lengthArray = value.split(",");

            List<String> lengthList = Arrays.asList(lengthArray);
            Collections.sort(lengthList);

            String uselen = "";

            if ("shortest".equals(geneLength)) {
                uselen = lengthList.get(0);
            }
            if ("longest".equals(geneLength)) {
                uselen = lengthList.get(lengthList.size() - 1);
            }
            if ("mean".equals(geneLength)) {
                int sum = 0;
                for (String a : lengthList) {
                    sum += Integer.valueOf(a);
                }
                uselen = Math.round(sum / lengthList.size()) + "";
            }
            if ("median".equals(geneLength)) {
                double median = 0d;
                if (lengthList.size() % 2 == 0) {
                    median = Double.valueOf(lengthList.get(Math.round(lengthList.size() - 1 / 2))
                            + lengthList.get(Math.round(lengthList.size() - 1 / 2))) / 2;
                } else {
                    median = Double.valueOf(lengthList.get(Math.round(lengthList.size() - 1 / 2)));
                }
                uselen = Math.round(median) + "";
            }
            if ("".equals(geneLength)) {
                lengthList.set(lengthList.indexOf(key), "");
            }

            goList.add(key);
        }

        Collections.sort(goList);
        Collections.sort(toList);

        int totalRead = 0;
        Map<String, String> tr2BasesMap = new HashMap<String, String>();
        Map<String, Integer> tr2CountsMap = new HashMap<String, Integer>();
        Map<String, String> gene2BasesMap = new HashMap<String, String>();
        Map<String, Integer> gene2CountsMap = new HashMap<String, Integer>();
        br = new BufferedReader(new FileReader(samFile));
        while ((line = br.readLine()) != null) {
            if (line.startsWith("@")) {
                continue;
            }
            StringTokenizer st = new StringTokenizer(line, "\t");
            String c1 = st.nextToken();
            String c2 = st.nextToken();
            String c3 = st.nextToken();
            String c4 = st.nextToken();
            String c5 = st.nextToken();
            String c6 = st.nextToken();
            ++totalRead;
            int bitFlag = Integer.valueOf(c2);

            Map<Integer, Integer> fMap = new HashMap<Integer, Integer>();
            Integer thisN = 1024 * 2;
            for (int i = 10; i >= 0; i--) {
                thisN = thisN / 2;
                if (bitFlag >= thisN) {
                    fMap.put(i, 1);
                    bitFlag -= thisN;
                } else {
                    fMap.put(i, 0);
                }
            }

            if (fMap.get(2) == 1) {
                continue;
            }

            String[] nonDigitCigarSplit = c6.split("\\D+");
            String[] digitCigarSplit = c6.split("\\d+");

            List<String> list = new ArrayList<String>(Arrays.asList(digitCigarSplit));
            list.remove(0);
            list.remove(1);
            digitCigarSplit = list.toArray(digitCigarSplit);

            String baseCount = "";
            for (int i = 0; i < nonDigitCigarSplit.length + 1; ++i) {
                if (digitCigarSplit[i] != null && "M".equals(digitCigarSplit[i]) || "I".equals(digitCigarSplit[i])) {
                    baseCount += nonDigitCigarSplit[i];
                }
            }
            tr2BasesMap.put(c3, tr2BasesMap.get(c3) != null ? tr2BasesMap.get(c3) + baseCount : baseCount);
            tr2CountsMap.put(c3, tr2CountsMap.get(c3) != null ? tr2CountsMap.get(c3) + 1 : 0);

            if (transcript2GeneMap.containsKey(c3)) {
                String gene = transcript2GeneMap.get(c3);
                gene2BasesMap.put(gene, gene2BasesMap.get(c3) != null ? gene2BasesMap.get(c3) + baseCount : baseCount);
                gene2CountsMap.put(gene, gene2CountsMap.get(c3) != null ? gene2CountsMap.get(c3) + 1 : 0);
            }

        }

        return null;
    }

    public File getSamFile() {
        return samFile;
    }

    public void setSamFile(File samFile) {
        this.samFile = samFile;
    }

    public File getTranscriptDB() {
        return transcriptDB;
    }

    public void setTranscriptDB(File transcriptDB) {
        this.transcriptDB = transcriptDB;
    }

    public String getGeneLength() {
        return geneLength;
    }

    public void setGeneLength(String geneLength) {
        this.geneLength = geneLength;
    }

    public File getOutTR() {
        return outTR;
    }

    public void setOutTR(File outTR) {
        this.outTR = outTR;
    }

    public File getOutGENE() {
        return outGENE;
    }

    public void setOutGENE(File outGENE) {
        this.outGENE = outGENE;
    }

    @Override
    public String toString() {
        return String.format(
                "GeneCountsRPKM [samFile=%s, transcriptDB=%s, geneLength=%s, outTR=%s, outGENE=%s, toString()=%s]",
                samFile, transcriptDB, geneLength, outTR, outGENE, super.toString());
    }

}
