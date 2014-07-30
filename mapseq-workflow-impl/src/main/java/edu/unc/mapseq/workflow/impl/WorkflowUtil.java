package edu.unc.mapseq.workflow;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.config.RunModeType;
import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.HTSFSample;
import edu.unc.mapseq.dao.model.Job;
import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.workflow.WorkflowException;

public class WorkflowUtil {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowUtil.class);

    public static String getRootFastqName(String name) {
        logger.debug("ENTERING getRootFastqName(String)");

        if (name.endsWith(".fastq.gz")) {
            return StringUtils.removeEnd(name, ".fastq.gz");
        }
        if (name.endsWith(".fastq")) {
            return StringUtils.removeEnd(name, ".fastq");
        }
        return name;
    }

    public static List<File> lookupFileByJobAndMimeTypeAndWorkflowId(Set<FileData> fileDataSet,
            MaPSeqDAOBean mapseqDAOBean, Class<?> clazz, MimeType mimeType, Long workflowId) {
        logger.debug("ENTERING lookupFileByJobAndMimeType");

        List<File> ret = new ArrayList<File>();

        if (fileDataSet == null) {
            logger.warn("fileDataSet was null...returning empty file list");
            return ret;
        }

        logger.info("fileDataSet.size() = {}", fileDataSet.size());

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

    public static List<File> getReadPairList(Set<FileData> fileDataSet, String sequencerRunName, Integer laneIndex) {
        logger.debug("ENTERING getReadPairList(Set<FileData>, String, Integer)");

        List<File> readPairList = new ArrayList<File>();

        if (fileDataSet == null) {
            logger.warn("fileDataSet was null...returning empty readPairList");
            return readPairList;
        }

        logger.info("fileDataSet.size() = {}", fileDataSet.size());

        for (FileData fileData : fileDataSet) {
            MimeType mimeType = fileData.getMimeType();
            if (mimeType != null && mimeType.equals(MimeType.FASTQ)) {
                Pattern patternR1 = Pattern.compile(String.format("^%s.*_L00%d_R1\\.fastq\\.gz$", sequencerRunName,
                        laneIndex));
                Matcher matcherR1 = patternR1.matcher(fileData.getName());
                File file = new File(fileData.getPath(), fileData.getName());
                if (matcherR1.matches()) {
                    logger.debug("found file: {}", file.getAbsolutePath());
                    readPairList.add(file);
                }

                Pattern patternR2 = Pattern.compile(String.format("^%s.*_L00%d_R2\\.fastq\\.gz$", sequencerRunName,
                        laneIndex));
                Matcher matcherR2 = patternR2.matcher(fileData.getName());
                if (matcherR2.matches()) {
                    logger.debug("found file: {}", file.getAbsolutePath());
                    readPairList.add(file);
                }
            }
        }
        Collections.sort(readPairList);

        return readPairList;
    }

    public static File createOutputDirectory(String sequencerRunName, HTSFSample htsfSample, String workflowName,
            String version) throws WorkflowException {
        File baseDir;

        RunModeType runMode = RunModeType.PROD;
        if (StringUtils.isEmpty(version) || (StringUtils.isNotEmpty(version) && version.contains("SNAPSHOT"))) {
            runMode = RunModeType.DEV;
        }

        switch (runMode) {
            case DEV:
            case STAGING:
                baseDir = new File(getOutputDirectory(), runMode.toString().toLowerCase());
                break;
            case PROD:
            default:
                baseDir = getOutputDirectory();
                break;
        }
        File sequencerRunOutputDirectory = new File(baseDir, sequencerRunName);
        File workflowDir = new File(sequencerRunOutputDirectory, workflowName);
        File htsfSampleOutputDir = new File(workflowDir, String.format("L%03d_%s", htsfSample.getLaneIndex(),
                htsfSample.getBarcode()));
        File tmpDir = new File(htsfSampleOutputDir, "tmp");
        tmpDir.mkdirs();

        try {
            htsfSample.setOutputDirectory(htsfSampleOutputDir.getAbsolutePath());
            getWorkflowBeanService().getMaPSeqDAOBean().getHTSFSampleDAO().save(htsfSample);
        } catch (MaPSeqDAOException e1) {
            logger.error("Could not persist HTSFSample");
            throw new WorkflowException("Could not persist HTSFSample");
        }

        return htsfSampleOutputDir;
    }

    public Set<HTSFSample> getAggregateHTSFSampleSet() throws WorkflowException {

        Set<HTSFSample> htsfSampleSet = new HashSet<HTSFSample>();

        if (getWorkflowPlan().getSequencerRun() == null && getWorkflowPlan().getHTSFSamples() == null) {
            logger.error("Don't have either sequencerRun and htsfSample");
            throw new WorkflowException("Don't have either sequencerRun and htsfSample");
        }

        if (getWorkflowPlan().getSequencerRun() != null) {
            logger.info("sequencerRun: {}", getWorkflowPlan().getSequencerRun().toString());
            try {
                htsfSampleSet.addAll(getWorkflowBeanService().getMaPSeqDAOBean().getHTSFSampleDAO()
                        .findBySequencerRunId(getWorkflowPlan().getSequencerRun().getId()));
            } catch (MaPSeqDAOException e) {
                logger.error("problem getting HTSFSamples");
            }
        }

        if (getWorkflowPlan().getHTSFSamples() != null) {
            htsfSampleSet.addAll(getWorkflowPlan().getHTSFSamples());
        }

        return htsfSampleSet;
    }

}
