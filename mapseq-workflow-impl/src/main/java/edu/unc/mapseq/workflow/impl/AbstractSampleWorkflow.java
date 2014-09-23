package edu.unc.mapseq.workflow.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.config.RunModeType;
import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.model.Job;
import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.dao.model.Sample;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.workflow.WorkflowException;

public abstract class AbstractSampleWorkflow extends AbstractWorkflow {

    private final Logger logger = LoggerFactory.getLogger(AbstractWorkflow.class);

    public AbstractSampleWorkflow() {
        super();
    }

    @Override
    public void init() throws WorkflowException {
        super.init();

        Set<Sample> samples = getAggregatedSamples();

        if (samples != null && !samples.isEmpty()) {
            for (Sample sample : samples) {
                try {
                    RunModeType runMode = RunModeType.PROD;
                    String version = getVersion();
                    if (StringUtils.isEmpty(version)
                            || (StringUtils.isNotEmpty(version) && version.contains("SNAPSHOT"))) {
                        runMode = RunModeType.DEV;
                    }

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

                    File flowcellOutputDirectory = new File(outdir, sample.getFlowcell().getName());
                    File sampleOutputDir = new File(flowcellOutputDirectory, String.format("L%03d_%s",
                            sample.getLaneIndex(), sample.getBarcode()));
                    sampleOutputDir.mkdirs();
                    // this will produce: /proj/seq/mapseq/RENCI/<flowcell>/<lane>_<barcode>
                    if (StringUtils.isNotEmpty(sample.getOutputDirectory())
                            && !sample.getOutputDirectory().equals(sampleOutputDir.getAbsolutePath())) {
                        sample.setOutputDirectory(sampleOutputDir.getAbsolutePath());
                        MaPSeqDAOBean mapseqDAOBean = getWorkflowBeanService().getMaPSeqDAOBean();
                        mapseqDAOBean.getSampleDAO().save(sample);
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

        Set<Sample> sampleSet = new HashSet<Sample>();

        WorkflowRun workflowRun = getWorkflowRunAttempt().getWorkflowRun();
        try {
            MaPSeqDAOBean mapseqDAOBean = getWorkflowBeanService().getMaPSeqDAOBean();
            List<Sample> samples = mapseqDAOBean.getSampleDAO().findByWorkflowRunId(workflowRun.getId());
            if (samples != null && !samples.isEmpty()) {
                sampleSet.addAll(samples);
            }
            List<Flowcell> flowcells = mapseqDAOBean.getFlowcellDAO().findByWorkflowRunId(workflowRun.getId());
            if (flowcells != null && !flowcells.isEmpty()) {
                for (Flowcell flowcell : flowcells) {
                    sampleSet.addAll(mapseqDAOBean.getSampleDAO().findByFlowcellId(flowcell.getId()));
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

    protected List<File> lookupFileByJobAndMimeTypeAndWorkflowId(Set<FileData> fileDataSet, Class<?> clazz,
            MimeType mimeType, Long workflowId) {
        logger.debug("ENTERING lookupFileByJobAndMimeTypeAndWorkflowId(Set<FileData>, Class<?>, MimeType, Long)");

        List<File> ret = new ArrayList<File>();

        if (fileDataSet == null) {
            logger.warn("fileDataSet was null...returning empty file list");
            return ret;
        }

        logger.info("fileDataSet.size() = {}", fileDataSet.size());
        MaPSeqDAOBean mapseqDAOBean = getWorkflowBeanService().getMaPSeqDAOBean();

        for (FileData fileData : fileDataSet) {
            if (fileData.getMimeType().equals(mimeType)) {
                List<Job> jobList = null;
                try {
                    jobList = mapseqDAOBean.getJobDAO().findByFileDataIdAndWorkflowId(fileData.getId(),
                            clazz.getName(), workflowId);
                } catch (MaPSeqDAOException e) {
                    e.printStackTrace();
                }
                if (jobList != null && jobList.size() > 0) {
                    for (Job job : jobList) {
                        if (job.getName().contains(clazz.getSimpleName())) {
                            logger.debug("using FileData: {}", fileData.toString());
                            logger.debug("from Job: {}", job.toString());
                            ret.add(new File(fileData.getPath(), fileData.getName()));
                            break;
                        }
                    }
                }
            }
        }
        return ret;
    }

}
