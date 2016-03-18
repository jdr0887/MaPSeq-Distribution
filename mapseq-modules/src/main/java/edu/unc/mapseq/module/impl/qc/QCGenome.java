package edu.unc.mapseq.module.impl.qc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.validation.constraints.NotNull;

import org.apache.commons.io.FileUtils;

import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.module.DefaultModuleOutput;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleException;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;

/**
 * To get alignment to QC genome, currenly, human rRNA and viral genome
 * 
 * Expected output: "mock" alignment to qc genome, text file with key values
 * 
 * @author jdr0887
 * 
 */
@Application(name = "QCGenome")
public class QCGenome extends Module {

    @NotNull(message = "samFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "Input file is not readable: samFile", groups = InputValidations.class)
    @InputArgument
    private File samFile;

    @NotNull(message = "qcdbkey is required", groups = InputValidations.class)
    @FileIsReadable(message = "Input file is not readable: qcdbkey", groups = InputValidations.class)
    @InputArgument
    private File qcdbkey;

    @InputArgument
    private File outFile;

    public QCGenome() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return QCGenome.class;
    }

    @Override
    public ModuleOutput call() throws ModuleException {

        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();

        int exitCode = 0;
        try {

            List<String> qcdbkeyFileLines = FileUtils.readLines(qcdbkey);

            Map<String, String> qcdbkeyMap = new HashMap<String, String>();
            for (String line : qcdbkeyFileLines) {
                StringTokenizer st = new StringTokenizer(line, "\t");
                qcdbkeyMap.put(st.nextToken(), st.nextToken());
            }

            int viral = 0, rrna = 0, readTotal = 0;

            BufferedReader br = new BufferedReader(new FileReader(samFile));

            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("@")) {
                    continue;
                }
                StringTokenizer st = new StringTokenizer(line, "\t");
                ++readTotal;
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

                String type = qcdbkeyMap.get(c3);
                if (type.equals("rRNA")) {
                    rrna++;
                }
                if (type.equals("viral")) {
                    viral++;
                }

            }

            br.close();

            StringBuilder sb = new StringBuilder();
            sb.append("readTotal: ").append(readTotal).append("\n");
            sb.append("rRNA: ").append(rrna).append("\n");
            sb.append("percent rRNA: ").append(Float.valueOf(100 * rrna / readTotal)).append("\n");
            sb.append("viral: ").append(viral).append("\n");
            sb.append("percent viral: ").append(Float.valueOf(100 * viral / readTotal)).append("\n");

            FileUtils.writeStringToFile(outFile, sb.toString());

            FileData fm = new FileData();
            fm.setName(outFile.getName());
            fm.setMimeType(MimeType.TEXT_QC_GENOME);
            getFileDatas().add(fm);

        } catch (Exception e) {
            e.printStackTrace();
            moduleOutput.setError(new StringBuilder(e.getMessage()));
            moduleOutput.setExitCode(exitCode);
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

    public File getQcdbkey() {
        return qcdbkey;
    }

    public void setQcdbkey(File qcdbkey) {
        this.qcdbkey = qcdbkey;
    }

    public File getOutFile() {
        return outFile;
    }

    public void setOutFile(File outFile) {
        this.outFile = outFile;
    }

    @Override
    public String toString() {
        return String.format("QCGenome [samFile=%s, qcdbkey=%s, outFile=%s, toString()=%s]", samFile, qcdbkey, outFile,
                super.toString());
    }

}
