package edu.unc.mapseq.workflow.core;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOBeanService;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.Job;
import edu.unc.mapseq.dao.model.MimeType;

public class WorkflowUtil {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowUtil.class);

    public static File findFileByJobAndMimeTypeAndWorkflowId(MaPSeqDAOBeanService mapseqDAOBeanService,
            Set<FileData> fileDataSet, Class<?> clazz, MimeType mimeType, Long workflowId) {
        logger.debug("ENTERING findFileByJobAndMimeTypeAndWorkflowId(Set<FileData>, Class<?>, MimeType, Long)");

        File ret = null;

        if (fileDataSet == null) {
            logger.warn("fileDataSet was null...returning empty file list");
            return ret;
        }

        logger.info("fileDataSet.size() = {}", fileDataSet.size());

        for (FileData fileData : fileDataSet) {
            if (!fileData.getMimeType().equals(mimeType)) {
                continue;
            }
            logger.debug(fileData.toString());
            try {
                List<Job> jobList = mapseqDAOBeanService.getJobDAO().findByFileDataIdAndWorkflowId(fileData.getId(),
                        clazz.getName(), workflowId);
                if (CollectionUtils.isEmpty(jobList)) {
                    logger.warn("No Jobs found");
                    continue;
                }
                for (Job job : jobList) {
                    if (job.getName().contains(clazz.getSimpleName())) {
                        logger.debug(job.toString());
                        ret = new File(fileData.getPath(), fileData.getName());
                        break;
                    }
                }
            } catch (MaPSeqDAOException e) {
                e.printStackTrace();
            }
        }
        if (ret != null) {
            logger.info("File: {}", ret.getAbsolutePath());
        }
        return ret;
    }

}
