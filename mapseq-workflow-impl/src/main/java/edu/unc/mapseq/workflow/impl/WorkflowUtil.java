package edu.unc.mapseq.workflow.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOBeanService;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.Job;
import edu.unc.mapseq.dao.model.MimeType;

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
                Pattern patternR1 = Pattern
                        .compile(String.format("^%s.*_L00%d_R1\\.fastq\\.gz$", sequencerRunName, laneIndex));
                Matcher matcherR1 = patternR1.matcher(fileData.getName());
                File file = new File(fileData.getPath(), fileData.getName());
                if (matcherR1.matches()) {
                    logger.debug("found file: {}", file.getAbsolutePath());
                    readPairList.add(file);
                }

                Pattern patternR2 = Pattern
                        .compile(String.format("^%s.*_L00%d_R2\\.fastq\\.gz$", sequencerRunName, laneIndex));
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

    public static File findFileByJobAndMimeTypeAndWorkflowId(MaPSeqDAOBeanService maPSeqDAOBeanService,
            Set<FileData> fileDataSet, Class<?> clazz, MimeType mimeType, Long workflowId) {
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
                    jobList = maPSeqDAOBeanService.getJobDAO().findByFileDataIdAndWorkflowId(fileData.getId(),
                            clazz.getName(), workflowId);
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

    public static File findFileByMimeTypeAndSuffix(Set<FileData> fileDataSet, MimeType mimeType, String suffix) {
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
