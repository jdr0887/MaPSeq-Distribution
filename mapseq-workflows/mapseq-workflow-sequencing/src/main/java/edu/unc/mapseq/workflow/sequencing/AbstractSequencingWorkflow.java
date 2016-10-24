package edu.unc.mapseq.workflow.sequencing;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.dao.model.Sample;
import edu.unc.mapseq.workflow.WorkflowException;
import edu.unc.mapseq.workflow.core.AbstractWorkflow;

public abstract class AbstractSequencingWorkflow extends AbstractWorkflow {

    private static final Logger logger = LoggerFactory.getLogger(AbstractSequencingWorkflow.class);

    public AbstractSequencingWorkflow() {
        super();
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

        Set<Sample> sampleSet = SequencingWorkflowUtil.getAggregatedSamples(this.getWorkflowBeanService().getMaPSeqDAOBeanService(),
                this.getWorkflowRunAttempt());

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
