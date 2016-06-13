package edu.unc.mapseq.commands.validation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.renci.common.exec.BashExecutor;
import org.renci.common.exec.CommandInput;
import org.renci.common.exec.CommandOutput;
import org.renci.common.exec.Executor;
import org.renci.common.exec.ExecutorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.FileDataDAO;
import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.StudyDAO;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.model.Sample;
import edu.unc.mapseq.dao.model.Study;

@Command(scope = "mapseq", name = "check-irods-registration", description = "Check IRODS Registration")
@Service
public class CheckIRODSRegistrationAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(CheckIRODSRegistrationAction.class);

    @Reference
    private FlowcellDAO flowcellDAO;

    @Reference
    private StudyDAO studyDAO;

    @Reference
    private SampleDAO sampleDAO;

    @Reference
    private FileDataDAO fileDataDAO;

    @Option(name = "--sampleId", description = "sampleId", required = false, multiValued = false)
    private Long sampleId;

    public CheckIRODSRegistrationAction() {
        super();
    }

    @Override
    public Object execute() {
        final Set<Sample> samples = new HashSet<Sample>();
        try {
            if (sampleId != null) {
                // do one sample
                Sample sample = sampleDAO.findById(sampleId);
                samples.add(sample);
            } else {
                // do all samples
                List<Study> studyList = studyDAO.findByName("NC_GENES");
                if (CollectionUtils.isNotEmpty(studyList)) {
                    Study ncgenesStudy = studyList.get(0);
                    List<Flowcell> flowcellList = flowcellDAO.findByStudyId(ncgenesStudy.getId());
                    if (CollectionUtils.isNotEmpty(flowcellList)) {
                        for (Flowcell flowcell : flowcellList) {
                            List<Sample> sampleList = sampleDAO.findByFlowcellId(flowcell.getId());
                            if (CollectionUtils.isNotEmpty(sampleList)) {
                                samples.addAll(sampleList);
                            }
                        }
                    }
                }
            }
        } catch (MaPSeqDAOException e1) {
            e1.printStackTrace();
        }

        try {
            ExecutorService es = Executors.newSingleThreadExecutor();
            final File irodsValidationFile = new File("/tmp", "irods-validation.txt");
            es.submit(() -> {
                try (FileWriter fw = new FileWriter(irodsValidationFile); BufferedWriter bw = new BufferedWriter(fw)) {
                    if (CollectionUtils.isNotEmpty(samples)) {
                        for (Sample sample : samples) {
                            bw.write(sample.toString());
                            bw.newLine();
                            check(bw, sample.getFlowcell(), sample);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            es.shutdown();
            es.awaitTermination(2L, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void check(BufferedWriter bw, Flowcell flowcell, Sample sample) throws IOException {

        int idx = sample.getName().lastIndexOf("-");
        String participantId = idx != -1 ? sample.getName().substring(0, idx) : sample.getName();

        String ncgenesIRODSDirectory = String.format("/MedGenZone/sequence_data/ncgenes/%s", participantId);
        CommandOutput commandOutput = checkForDirectoryExistence(ncgenesIRODSDirectory);
        if (commandOutput.getExitCode() != 0) {
            bw.write(ncgenesIRODSDirectory);
            bw.newLine();
        }

        // first check for casava generated files
        File ncgenesCASAVADirectory = new File(sample.getOutputDirectory(), "NCGenesCASAVA");
        File fastqR1File = new File(ncgenesCASAVADirectory,
                String.format("%s_%s_L%03d_R%d.fastq.gz", flowcell.getName(), sample.getBarcode(), sample.getLaneIndex(), 1));
        String fastqR1FileInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId, fastqR1File.getName());
        commandOutput = checkForFileExistence(fastqR1FileInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(fastqR1FileInIrods);
            bw.newLine();
        }

        File fastqR2File = new File(ncgenesCASAVADirectory,
                String.format("%s_%s_L%03d_R%d.fastq.gz", flowcell.getName(), sample.getBarcode(), sample.getLaneIndex(), 2));
        String fastqR2FileInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId, fastqR2File.getName());
        commandOutput = checkForFileExistence(fastqR2FileInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(fastqR2FileInIrods);
            bw.newLine();
        }

        // first check for baseline generated files
        File ncgenesBaselineDirectory = new File(sample.getOutputDirectory(), "NCGenesBaseline");

        String fastqLaneRootName = String.format("%s_%s_L%03d", flowcell.getName(), sample.getBarcode(), sample.getLaneIndex());

        File writeVCFHeaderOut = new File(ncgenesBaselineDirectory, fastqLaneRootName + ".vcf.hdr");
        String writeVCFHeaderOutInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId,
                writeVCFHeaderOut.getName());
        commandOutput = checkForFileExistence(writeVCFHeaderOutInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(writeVCFHeaderOutInIrods);
            bw.newLine();
        }

        File fastqcR1Output = new File(ncgenesBaselineDirectory, fastqLaneRootName + "_R1.fastqc.zip");
        String fastqcR1OutputInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId, fastqcR1Output.getName());
        commandOutput = checkForFileExistence(fastqcR1OutputInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(fastqcR1OutputInIrods);
            bw.newLine();
        }

        File fastqcR2Output = new File(ncgenesBaselineDirectory, fastqLaneRootName + "_R1.fastqc.zip");
        String fastqcR2OutputInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId, fastqcR2Output.getName());
        commandOutput = checkForFileExistence(fastqcR2OutputInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(fastqcR2OutputInIrods);
            bw.newLine();
        }

        File bwaSAMPairedEndOutFile = new File(ncgenesBaselineDirectory, fastqLaneRootName + ".sam");
        File fixRGOutput = new File(ncgenesBaselineDirectory, bwaSAMPairedEndOutFile.getName().replace(".sam", ".fixed-rg.bam"));
        File picardMarkDuplicatesOutput = new File(ncgenesBaselineDirectory, fixRGOutput.getName().replace(".bam", ".deduped.bam"));
        File indelRealignerOut = new File(ncgenesBaselineDirectory, picardMarkDuplicatesOutput.getName().replace(".bam", ".realign.bam"));
        File picardFixMateOutput = new File(ncgenesBaselineDirectory, indelRealignerOut.getName().replace(".bam", ".fixmate.bam"));
        File gatkTableRecalibrationOut = new File(ncgenesBaselineDirectory, picardFixMateOutput.getName().replace(".bam", ".recal.bam"));
        String gatkTableRecalibrationOutInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId,
                gatkTableRecalibrationOut.getName());
        commandOutput = checkForFileExistence(gatkTableRecalibrationOutInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(gatkTableRecalibrationOutInIrods);
            bw.newLine();
        }

        File gatkTableRecalibrationIndexOut = new File(ncgenesBaselineDirectory,
                gatkTableRecalibrationOut.getName().replace(".bam", ".bai"));
        String gatkTableRecalibrationIndexOutInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId,
                gatkTableRecalibrationIndexOut.getName());
        commandOutput = checkForFileExistence(gatkTableRecalibrationIndexOutInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(gatkTableRecalibrationIndexOutInIrods);
            bw.newLine();
        }

        File sampleCumulativeCoverageCountsFile = new File(ncgenesBaselineDirectory,
                gatkTableRecalibrationOut.getName().replace(".bam", ".coverage.sample_cumulative_coverage_counts"));
        String sampleCumulativeCoverageCountsFileInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId,
                sampleCumulativeCoverageCountsFile.getName());
        commandOutput = checkForFileExistence(sampleCumulativeCoverageCountsFileInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(sampleCumulativeCoverageCountsFileInIrods);
            bw.newLine();
        }

        File sampleCumulativeCoverageProportionsFile = new File(ncgenesBaselineDirectory,
                gatkTableRecalibrationOut.getName().replace(".bam", ".coverage.sample_cumulative_coverage_proportions"));
        String sampleCumulativeCoverageProportionsFileInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId,
                sampleCumulativeCoverageProportionsFile.getName());
        commandOutput = checkForFileExistence(sampleCumulativeCoverageProportionsFileInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(sampleCumulativeCoverageProportionsFileInIrods);
            bw.newLine();
        }

        File sampleIntervalStatisticsFile = new File(ncgenesBaselineDirectory,
                gatkTableRecalibrationOut.getName().replace(".bam", ".coverage.sample_interval_statistics"));
        String sampleIntervalStatisticsFileInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId,
                sampleIntervalStatisticsFile.getName());
        commandOutput = checkForFileExistence(sampleIntervalStatisticsFileInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(sampleIntervalStatisticsFileInIrods);
            bw.newLine();
        }

        File sampleIntervalSummaryFile = new File(ncgenesBaselineDirectory,
                gatkTableRecalibrationOut.getName().replace(".bam", ".coverage.sample_interval_summary"));
        String sampleIntervalSummaryFileInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId,
                sampleIntervalSummaryFile.getName());
        commandOutput = checkForFileExistence(sampleIntervalSummaryFileInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(sampleIntervalSummaryFileInIrods);
            bw.newLine();
        }

        File sampleStatisticsFile = new File(ncgenesBaselineDirectory,
                gatkTableRecalibrationOut.getName().replace(".bam", ".coverage.sample_statistics"));
        String sampleStatisticsFileInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId,
                sampleStatisticsFile.getName());
        commandOutput = checkForFileExistence(sampleStatisticsFileInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(sampleStatisticsFileInIrods);
            bw.newLine();
        }

        File sampleSummaryFile = new File(ncgenesBaselineDirectory,
                gatkTableRecalibrationOut.getName().replace(".bam", ".coverage.sample_summary"));
        String sampleSummaryFileInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId,
                sampleSummaryFile.getName());
        commandOutput = checkForFileExistence(sampleSummaryFileInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(sampleSummaryFileInIrods);
            bw.newLine();
        }

        File samtoolsFlagstatOut = new File(ncgenesBaselineDirectory,
                gatkTableRecalibrationOut.getName().replace(".bam", ".samtools.flagstat"));
        String samtoolsFlagstatOutInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId,
                samtoolsFlagstatOut.getName());
        commandOutput = checkForFileExistence(samtoolsFlagstatOutInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(samtoolsFlagstatOutInIrods);
            bw.newLine();
        }

        File gatkFlagstatOut = new File(ncgenesBaselineDirectory, gatkTableRecalibrationOut.getName().replace(".bam", ".gatk.flagstat"));
        String gatkFlagstatOutInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId, gatkFlagstatOut.getName());
        commandOutput = checkForFileExistence(gatkFlagstatOutInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(gatkFlagstatOutInIrods);
            bw.newLine();
        }

        File filterVariant1Output = new File(ncgenesBaselineDirectory, gatkTableRecalibrationOut.getName().replace(".bam", ".variant.vcf"));
        File gatkApplyRecalibrationOut = new File(ncgenesBaselineDirectory,
                filterVariant1Output.getName().replace(".vcf", ".recalibrated.filtered.vcf"));
        String gatkApplyRecalibrationOutInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId,
                gatkApplyRecalibrationOut.getName());
        commandOutput = checkForFileExistence(gatkApplyRecalibrationOutInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(gatkApplyRecalibrationOutInIrods);
            bw.newLine();
        }

        File filterVariant2Output = new File(ncgenesBaselineDirectory, filterVariant1Output.getName().replace(".vcf", ".ic_snps.vcf"));
        String filterVariant2OutputInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId,
                filterVariant2Output.getName());
        commandOutput = checkForFileExistence(filterVariant2OutputInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(filterVariant2OutputInIrods);
            bw.newLine();
        }
        bw.flush();

    }

    private CommandOutput checkForDirectoryExistence(String directory) {
        CommandOutput ret = null;
        try {
            File mapseqrc = new File(System.getProperty("user.home"), ".mapseqrc");
            Executor executor = BashExecutor.getInstance();
            CommandInput commandInput = new CommandInput(String.format("$NCGENESBASELINE_IRODS_HOME/ils %s%n", directory));
            ret = executor.execute(commandInput, mapseqrc);
        } catch (ExecutorException e) {
            logger.error(e.getMessage(), e);
        }
        return ret;
    }

    private CommandOutput checkForFileExistence(String file) {
        CommandOutput ret = null;
        try {
            File mapseqrc = new File(System.getProperty("user.home"), ".mapseqrc");
            Executor executor = BashExecutor.getInstance();
            CommandInput commandInput = new CommandInput(String.format("$NCGENESBASELINE_IRODS_HOME/ils %s%n", file));
            ret = executor.execute(commandInput, mapseqrc);
        } catch (ExecutorException e) {
            logger.error(e.getMessage(), e);
        }
        return ret;
    }

}
