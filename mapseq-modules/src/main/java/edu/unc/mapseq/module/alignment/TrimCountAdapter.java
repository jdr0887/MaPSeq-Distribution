package edu.unc.mapseq.module.alignment;

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

import javax.validation.constraints.NotNull;

import org.apache.commons.io.FileUtils;
import org.biojava3.core.sequence.DNASequence;

import edu.unc.mapseq.module.DefaultModuleOutput;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleException;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;

/**
 * Trim adapter segment (and any following sequence) from reads, and get count & % of reads that contain adapter
 * segment.
 * 
 * This module takes a given fastq file and trims all reads to eliminate any adapter contamination. The 'adapter' is the
 * reverse complement of the sequencing primer, which the user must provide. A non-A/C/G/T base in the primer is
 * converted to an N, and all Ns in the adapter are considered matches to any base in the read. (The opposite case is
 * not true -- Ns in read sequences are NOT considered matches to the adapter bases.) Otherwise, no mis-matches are
 * allowed. The minimum length for an adapter/read match is 5 bases. The script checks for internal & terminal adapter
 * segments. The trimmed read (& trimmed base scores) are output in fastq format, unless the effective read length is
 * zero [an adapter dimer]. The module also reports the count & percentage of reads that contain adapter. A collection
 * of basic trimming statistics is an optional output as well.
 * 
 * Expected output: outfastq (required), outqc (required), outstats (optional)
 * 
 * @author jdr0887
 * 
 */
@Application(name = "TrimCountAdapter")
public class TrimCountAdapter extends Module {

    @NotNull(message = "inFastq is required", groups = InputValidations.class)
    @FileIsReadable(groups = InputValidations.class)
    @InputArgument
    private File inFastq;

    @NotNull(message = "primerSeq is required", groups = InputValidations.class)
    @FileIsReadable(groups = InputValidations.class)
    @InputArgument
    private File primerSeq;

    @NotNull(message = "outFastq is required", groups = InputValidations.class)
    @FileIsReadable(groups = OutputValidations.class)
    @OutputArgument
    private File outFastq;

    @NotNull(message = "outQC is required", groups = InputValidations.class)
    @FileIsReadable(groups = OutputValidations.class)
    @OutputArgument
    private File outQC;

    @NotNull(message = "outStats is required", groups = InputValidations.class)
    @FileIsReadable(groups = OutputValidations.class)
    @OutputArgument
    private File outStats;

    public TrimCountAdapter() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return TrimCountAdapter.class;
    }

    @Override
    public ModuleOutput call() throws ModuleException {
        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();

        int exitCode = 0;
        try {

            FileUtils.touch(outFastq);

            Map<Integer, Integer> readLengthMap = new HashMap<Integer, Integer>();

            String primer = FileUtils.readFileToString(primerSeq);

            DNASequence rnaSeq = new DNASequence(primer);
            String reverseComplement = rnaSeq.getReverseComplement().getSequenceAsString();
            // allow Ns in primer/adapter to be matches to all bases
            reverseComplement = reverseComplement.replace("N", ".");
            int reverseComplementLength = reverseComplement.length();
            String seed = reverseComplement.substring(0, 5);

            BufferedReader br = new BufferedReader(new FileReader(inFastq));
            BufferedWriter bw = new BufferedWriter(new FileWriter(outFastq));
            int totalReadCount = 0;
            int adapterReadCount = 0;
            int sequenceLength = 0;
            while (true) {
                String sequenceId = br.readLine();
                if (sequenceId == null) {
                    break;
                }
                totalReadCount++;
                String rawSequence = br.readLine().trim();
                String optionalSequenceId = br.readLine();
                String qualityValues = br.readLine();

                sequenceLength = rawSequence.length();

                int tmpSequenceLength = sequenceLength;
                String readSegment = "";
                String adapterSegment = "";

                if (rawSequence.contains(seed)) {

                    for (int i = 0; i < sequenceLength - 4; i++) {
                        int endSize = sequenceLength - i;
                        if (endSize > reverseComplementLength) {
                            readSegment = rawSequence.substring(i, i + reverseComplementLength);
                            adapterSegment = reverseComplement;
                        } else {
                            readSegment = rawSequence.substring(i, i + endSize);
                            adapterSegment = reverseComplement.substring(0, endSize);
                        }
                        if (readSegment.contains(adapterSegment)) {
                            tmpSequenceLength = i;
                            break;
                        }
                    }

                }
                String usableSequence = rawSequence.substring(0, tmpSequenceLength);
                if (!usableSequence.equals(rawSequence)) {
                    adapterReadCount++;
                }
                String usableScore = qualityValues.substring(0, tmpSequenceLength);
                if (tmpSequenceLength > 0) {
                    bw.write(String.format("%s\n%s\n%s\n%s\n", sequenceId, usableSequence, optionalSequenceId,
                            usableScore));
                    bw.flush();
                }

                readLengthMap.put(tmpSequenceLength,
                        readLengthMap.get(tmpSequenceLength) != null ? readLengthMap.get(tmpSequenceLength) + 1 : 0);
            }
            bw.close();
            br.close();

            double percent = 100 * adapterReadCount / totalReadCount;

            int flag = 1;
            if (percent < 15) {
                flag = 0;
            }

            FileUtils.touch(outQC);
            FileUtils.writeStringToFile(outQC, String.format(
                    "run_status\tSUCCESS\nadapter_ct\t%s\nadapter_percent\t%s\nadapter_flat\t%s", adapterReadCount,
                    percent, flag));

            FileUtils.touch(outStats);

            int ct1 = 0;
            int ct2 = 0;
            int ct3 = 0;
            int avgsum = 0;
            List<Integer> sizes = new ArrayList<Integer>();
            for (int i = 0; i < sequenceLength; i++) {
                if (readLengthMap.get(i) == null) {
                    readLengthMap.put(i, 0);
                }
                if (i == sequenceLength) {
                    ct3 += readLengthMap.get(i);
                } else if (i > 0) {
                    ct2 += readLengthMap.get(i);
                } else {
                    ct1 += readLengthMap.get(i);
                }
                avgsum += readLengthMap.get(i) * i;
                for (int j = 0; j < readLengthMap.get(i); j++) {
                    sizes.add(i);
                }
            }

            Collections.sort(sizes);

            int median = 0;
            if (sizes.size() % 2 == 0) {
                median = (sizes.get((sizes.size() - 1) / 2) + sizes.get((sizes.size() + 1) / 2)) / 2;
            } else {
                median = sizes.get(sizes.size() / 2);
            }
            double avg = avgsum / sizes.size();
            int keepsum = ct2 + ct3;
            int allsum = ct1 + ct2 + ct3;
            String pctAD = String.format("%.2f", (double) (100 * readLengthMap.get(sequenceLength) / allsum));

            bw = new BufferedWriter(new FileWriter(outStats));
            bw.write(String.format("RemovedReadCt\t%s\n", ct1));
            bw.write(String.format("TrimmedReadCt\t%s\n", ct2));
            bw.write(String.format("UntrimmedReadCt\t%s\n", ct3));
            bw.write(String.format("RetainedReadCt\t%s\n", keepsum));
            bw.write(String.format("MedianEffReadLength\t%s\n", median));
            bw.write(String.format("MeanEffReadLength\t%s\n", avg));
            bw.write(String.format("AdapterDimer\t%s\n", pctAD));

            for (int i = 0; i < sequenceLength + 1; i++) {
                bw.write(String.format("EffReadLength=%d\t%s\n", i, readLengthMap.get(i)));
            }

            bw.flush();
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

    public File getInFastq() {
        return inFastq;
    }

    public void setInFastq(File inFastq) {
        this.inFastq = inFastq;
    }

    public File getPrimerSeq() {
        return primerSeq;
    }

    public void setPrimerSeq(File primerSeq) {
        this.primerSeq = primerSeq;
    }

    public File getOutFastq() {
        return outFastq;
    }

    public void setOutFastq(File outFastq) {
        this.outFastq = outFastq;
    }

    public File getOutQC() {
        return outQC;
    }

    public void setOutQC(File outQC) {
        this.outQC = outQC;
    }

    public File getOutStats() {
        return outStats;
    }

    public void setOutStats(File outStats) {
        this.outStats = outStats;
    }

}
