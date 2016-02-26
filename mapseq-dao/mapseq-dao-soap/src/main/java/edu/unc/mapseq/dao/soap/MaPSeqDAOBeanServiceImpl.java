package edu.unc.mapseq.dao.soap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.unc.mapseq.dao.AttributeDAO;
import edu.unc.mapseq.dao.FileDataDAO;
import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.JobDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBeanService;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.StudyDAO;
import edu.unc.mapseq.dao.WorkflowDAO;
import edu.unc.mapseq.dao.WorkflowRunAttemptDAO;
import edu.unc.mapseq.dao.WorkflowRunDAO;

@Component
public class MaPSeqDAOBeanServiceImpl implements MaPSeqDAOBeanService {

    @Autowired
    private AttributeDAO attributeDAO;

    @Autowired
    private FlowcellDAO flowcellDAO;

    @Autowired
    private FileDataDAO fileDataDAO;

    @Autowired
    private JobDAO jobDAO;

    @Autowired
    private SampleDAO sampleDAO;

    @Autowired
    private StudyDAO studyDAO;

    @Autowired
    private WorkflowDAO workflowDAO;

    @Autowired
    private WorkflowRunDAO workflowRunDAO;

    @Autowired
    private WorkflowRunAttemptDAO workflowRunAttemptDAO;

    public MaPSeqDAOBeanServiceImpl() {
        super();
    }

    public AttributeDAO getAttributeDAO() {
        return attributeDAO;
    }

    public void setAttributeDAO(AttributeDAO attributeDAO) {
        this.attributeDAO = attributeDAO;
    }

    public FlowcellDAO getFlowcellDAO() {
        return flowcellDAO;
    }

    public void setFlowcellDAO(FlowcellDAO flowcellDAO) {
        this.flowcellDAO = flowcellDAO;
    }

    public FileDataDAO getFileDataDAO() {
        return fileDataDAO;
    }

    public void setFileDataDAO(FileDataDAO fileDataDAO) {
        this.fileDataDAO = fileDataDAO;
    }

    public JobDAO getJobDAO() {
        return jobDAO;
    }

    public void setJobDAO(JobDAO jobDAO) {
        this.jobDAO = jobDAO;
    }

    public SampleDAO getSampleDAO() {
        return sampleDAO;
    }

    public void setSampleDAO(SampleDAO sampleDAO) {
        this.sampleDAO = sampleDAO;
    }

    public StudyDAO getStudyDAO() {
        return studyDAO;
    }

    public void setStudyDAO(StudyDAO studyDAO) {
        this.studyDAO = studyDAO;
    }

    public WorkflowDAO getWorkflowDAO() {
        return workflowDAO;
    }

    public void setWorkflowDAO(WorkflowDAO workflowDAO) {
        this.workflowDAO = workflowDAO;
    }

    public WorkflowRunDAO getWorkflowRunDAO() {
        return workflowRunDAO;
    }

    public void setWorkflowRunDAO(WorkflowRunDAO workflowRunDAO) {
        this.workflowRunDAO = workflowRunDAO;
    }

    public WorkflowRunAttemptDAO getWorkflowRunAttemptDAO() {
        return workflowRunAttemptDAO;
    }

    public void setWorkflowRunAttemptDAO(WorkflowRunAttemptDAO workflowRunAttemptDAO) {
        this.workflowRunAttemptDAO = workflowRunAttemptDAO;
    }

}
