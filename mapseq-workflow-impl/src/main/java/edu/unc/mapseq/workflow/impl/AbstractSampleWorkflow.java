package edu.unc.mapseq.workflow.impl;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.config.RunModeType;
import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBeanService;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.dao.model.Sample;
import edu.unc.mapseq.dao.model.Study;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.workflow.WorkflowException;

public abstract class AbstractSampleWorkflow extends AbstractWorkflow {

    private static final Logger logger = LoggerFactory.getLogger(AbstractSampleWorkflow.class);

    public AbstractSampleWorkflow() {
        super();
    }

    @Override
    public void init() throws WorkflowException {
        super.init();

        Set<Sample> samples = getAggregatedSamples();

        if (samples != null && !samples.isEmpty()) {

            RunModeType runMode = RunModeType.PROD;
            String version = getVersion();
            if (StringUtils.isEmpty(version) || (StringUtils.isNotEmpty(version) && version.contains("SNAPSHOT"))) {
                runMode = RunModeType.DEV;
            }

            for (Sample sample : samples) {
                try {

                    File outdir;
                    switch (runMode) {
                        case DEV:
                        case STAGING:
                            outdir = new File(getBaseOutputDirectory(), runMode.toString().toLowerCase());
                            break;
                        case PROD:
                        default:
                            outdir = getBaseOutputDirectory();
                            break;
                    }

                    Study study = sample.getStudy();
                    File studyDir = new File(outdir, study.getName());
                    File flowcellOutputDirectory = new File(studyDir, sample.getFlowcell().getName());
                    File sampleOutputDir = new File(flowcellOutputDirectory,
                            String.format("L%03d_%s", sample.getLaneIndex(), sample.getBarcode()));
                    logger.info("creating sample output directory: {}", sampleOutputDir.getAbsolutePath());
                    sampleOutputDir.mkdirs();
                    // this will produce: /proj/seq/mapseq/RENCI/<study>/<flowcell>/<lane>_<barcode>

                    if (sample.getOutputDirectory() == null || (sample.getOutputDirectory() != null
                            && !sample.getOutputDirectory().equals(sampleOutputDir.getAbsolutePath()))) {
                        sample.setOutputDirectory(sampleOutputDir.getAbsolutePath());
                        getWorkflowBeanService().getMaPSeqDAOBeanService().getSampleDAO().save(sample);
                    }
                } catch (MaPSeqDAOException e) {
                    logger.error("Could not persist Sample");
                    throw new WorkflowException("Could not persist Sample");
                }
            }
        }

    }

    @Override
    public void preRun() throws WorkflowException {
        super.preRun();
        Map<String, String> attributes = getWorkflowBeanService().getAttributes();
        if (attributes != null && !attributes.isEmpty()) {
            for (String key : attributes.keySet()) {
                logger.info("{}: {}", key, attributes.get(key));
            }
        }
    }

    protected Set<Sample> getAggregatedSamples() throws WorkflowException {

        MaPSeqDAOBeanService mapseqDAOBeanService = getWorkflowBeanService().getMaPSeqDAOBeanService();
        SampleDAO sampleDAO = mapseqDAOBeanService.getSampleDAO();
        FlowcellDAO flowcellDAO = mapseqDAOBeanService.getFlowcellDAO();

        Set<Sample> sampleSet = new HashSet<Sample>();

        WorkflowRun workflowRun = getWorkflowRunAttempt().getWorkflowRun();
        try {
            List<Sample> samples = sampleDAO.findByWorkflowRunId(workflowRun.getId());
            if (samples != null && !samples.isEmpty()) {
                sampleSet.addAll(samples);
            }
            List<Flowcell> flowcells = flowcellDAO.findByWorkflowRunId(workflowRun.getId());
            if (flowcells != null && !flowcells.isEmpty()) {
                for (Flowcell flowcell : flowcells) {
                    List<Sample> sampleList = sampleDAO.findByFlowcellId(flowcell.getId());
                    if (sampleList != null && !sampleList.isEmpty()) {
                        sampleSet.addAll(sampleList);
                    }
                }
            }
        } catch (MaPSeqDAOException e) {
            logger.error("MaPSeq Error", e);
        }

        if (sampleSet.isEmpty()) {
            logger.error("Found no samples");
            throw new WorkflowException("Found no samples");
        }

        return sampleSet;
    }

    public File findFileByMimeTypeAndSuffix(Set<FileData> fileDataSet, MimeType mimeType, String suffix) {
        logger.debug("ENTERING findFileByMimeTypeAndSuffix(Set<FileData>, MimeType, String)");
        File ret = null;
        if (fileDataSet == null) {
            logger.warn("fileDataSet was null...returning empty file list");
            return ret;
        }
        logger.info("fileDataSet.size() = {}", fileDataSet.size());
        for (FileData fileData : fileDataSet) {
            if (fileData.getMimeType().equals(mimeType) && fileData.getName().endsWith(suffix)) {
                ret = new File(fileData.getPath(), fileData.getName());
                break;
            }
        }
        return ret;
    }

}
