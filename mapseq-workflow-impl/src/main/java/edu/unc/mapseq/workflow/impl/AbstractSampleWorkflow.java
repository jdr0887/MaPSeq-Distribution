package edu.unc.mapseq.workflow.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import edu.unc.mapseq.dao.model.Job;
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

    public String getRootFastqName(String name) {
        logger.debug("ENTERING getRootFastqName(String)");

        if (name.endsWith(".fastq.gz")) {
            return StringUtils.removeEnd(name, ".fastq.gz");
        }
        if (name.endsWith(".fastq")) {
            return StringUtils.removeEnd(name, ".fastq");
        }
        return name;
    }

    public List<File> getReadPairList(Sample sample) {
        logger.debug("ENTERING getReadPairList(Set<FileData>, String, Integer)");

        List<File> readPairList = new ArrayList<File>();

        Set<FileData> fileDataSet = sample.getFileDatas();
        if (fileDataSet == null) {
            logger.warn("fileDataSet was null...returning empty readPairList");
            return readPairList;
        }

        logger.info("fileDataSet.size() = {}", fileDataSet.size());

        for (FileData fileData : fileDataSet) {
            MimeType mimeType = fileData.getMimeType();
            if (mimeType != null && mimeType.equals(MimeType.FASTQ)) {
                Pattern patternR1 = Pattern.compile(String.format("^%s.*_L00%d_R1\\.fastq\\.gz$",
                        sample.getFlowcell().getName(), sample.getLaneIndex()));
                Matcher matcherR1 = patternR1.matcher(fileData.getName());
                File file = new File(fileData.getPath(), fileData.getName());
                if (matcherR1.matches()) {
                    logger.debug("found file: {}", file.getAbsolutePath());
                    readPairList.add(file);
                }

                Pattern patternR2 = Pattern.compile(String.format("^%s.*_L00%d_R2\\.fastq\\.gz$",
                        sample.getFlowcell().getName(), sample.getLaneIndex()));
                Matcher matcherR2 = patternR2.matcher(fileData.getName());
                if (matcherR2.matches()) {
                    logger.debug("found file: {}", file.getAbsolutePath());
                    readPairList.add(file);
                }
            }
        }
        readPairList.sort((a, b) -> a.getName().compareTo(b.getName()));

        return readPairList;
    }

    public File findFileByJobAndMimeTypeAndWorkflowId(Set<FileData> fileDataSet, Class<?> clazz, MimeType mimeType,
            Long workflowId) {
        logger.debug("ENTERING findFileByJobAndMimeTypeAndWorkflowId(Set<FileData>, Class<?>, MimeType, Long)");

        File ret = null;

        if (fileDataSet == null) {
            logger.warn("fileDataSet was null...returning empty file list");
            return ret;
        }

        logger.info("fileDataSet.size() = {}", fileDataSet.size());

        for (FileData fileData : fileDataSet) {
            if (fileData.getMimeType().equals(mimeType)) {
                List<Job> jobList = null;
                try {
                    jobList = getWorkflowBeanService().getMaPSeqDAOBeanService().getJobDAO()
                            .findByFileDataIdAndWorkflowId(fileData.getId(), clazz.getName(), workflowId);
                } catch (MaPSeqDAOException e) {
                    e.printStackTrace();
                }
                if (jobList != null && jobList.size() > 0) {
                    for (Job job : jobList) {
                        if (job.getName().contains(clazz.getSimpleName())) {
                            logger.debug("using FileData: {}", fileData.toString());
                            logger.debug("from Job: {}", job.toString());
                            ret = new File(fileData.getPath(), fileData.getName());
                            break;
                        }
                    }
                }
            }
        }
        return ret;
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
