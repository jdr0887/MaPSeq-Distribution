package edu.unc.mapseq.workflow.sequencing;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBeanService;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.dao.model.Sample;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.workflow.WorkflowException;
import edu.unc.mapseq.workflow.core.AbstractWorkflow;

public abstract class AbstractSequencingWorkflow extends AbstractWorkflow {

    private static final Logger logger = LoggerFactory.getLogger(AbstractSequencingWorkflow.class);

    public AbstractSequencingWorkflow() {
        super();
    }

    @Override
    public void init() throws WorkflowException {
        super.init();

        Set<Sample> samples = getAggregatedSamples();

        String outputDirectory = System.getenv("MAPSEQ_OUTPUT_DIRECTORY");

        if (CollectionUtils.isNotEmpty(samples)) {

            for (Sample sample : samples) {
                try {
                    File studyDirectory = new File(outputDirectory, sample.getStudy().getName());
                    File analysisDirectory = new File(studyDirectory, "analysis");
                    File flowcellDirectory = new File(analysisDirectory, sample.getFlowcell().getName());
                    File sampleOutputDir = new File(flowcellDirectory,
                            String.format("L%03d_%s", sample.getLaneIndex(), sample.getBarcode()));
                    sampleOutputDir.mkdirs();

                    if (sample.getOutputDirectory() == null || (sample.getOutputDirectory() != null
                            && !sample.getOutputDirectory().equals(sampleOutputDir.getAbsolutePath()))) {
                        logger.info("creating sample output directory: {}", sampleOutputDir.getAbsolutePath());
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
        if (MapUtils.isNotEmpty(attributes)) {
            attributes.forEach((key, value) -> logger.info("{}: {}", key, attributes.get(key)));
        }
    }

    @Override
    public void postRun() throws WorkflowException {
        super.postRun();

        Set<Sample> sampleSet = getAggregatedSamples();

        if (CollectionUtils.isNotEmpty(sampleSet)) {
            for (Sample sample : sampleSet) {
                if ("Undetermined".equals(sample.getBarcode())) {
                    continue;
                }
                Set<FileData> fileDataSet = sample.getFileDatas();
                if (CollectionUtils.isNotEmpty(fileDataSet)) {
                    Iterator<FileData> fileDataIter = fileDataSet.iterator();
                    while (fileDataIter.hasNext()) {
                        FileData fileData = fileDataIter.next();
                        if (!fileData.toFile().exists()) {
                            logger.warn("File doesn't exist: {}", fileData.toString());
                            fileDataIter.remove();
                        }
                    }
                }
                try {
                    getWorkflowBeanService().getMaPSeqDAOBeanService().getSampleDAO().save(sample);
                } catch (MaPSeqDAOException e) {
                    e.printStackTrace();
                }
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
            if (CollectionUtils.isNotEmpty(samples)) {
                sampleSet.addAll(samples);
            }
            List<Flowcell> flowcells = flowcellDAO.findByWorkflowRunId(workflowRun.getId());
            if (CollectionUtils.isNotEmpty(flowcells)) {
                for (Flowcell flowcell : flowcells) {
                    List<Sample> sampleList = sampleDAO.findByFlowcellId(flowcell.getId());
                    if (CollectionUtils.isNotEmpty(sampleList)) {
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

    public File createOutputDirectory(Sample sample) {
        logger.debug("ENTERING createOutputDirectory(Sample)");
        String outputDirectory = System.getenv("MAPSEQ_OUTPUT_DIRECTORY");
        File systemDirectory = new File(outputDirectory, getSystem().getValue());
        File studyDirectory = new File(systemDirectory, sample.getStudy().getName());
        File analysisDirectory = new File(studyDirectory, "analysis");
        File flowcellDirectory = new File(analysisDirectory, sample.getFlowcell().getName());
        File sampleOutputDir = new File(flowcellDirectory, String.format("L%03d_%s", sample.getLaneIndex(), sample.getBarcode()));
        File workflowDirectory = new File(sampleOutputDir, getName());
        if (!workflowDirectory.exists()) {
            workflowDirectory.mkdirs();
        }
        logger.debug("output directory: {}", workflowDirectory.getAbsolutePath());
        return workflowDirectory;
    }
}
