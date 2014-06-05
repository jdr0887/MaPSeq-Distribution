package edu.unc.mapseq.ws.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowPlanDAO;
import edu.unc.mapseq.dao.model.WorkflowPlan;
import edu.unc.mapseq.ws.WorkflowPlanService;

public class WorkflowPlanServiceImpl implements WorkflowPlanService {

    private final Logger logger = LoggerFactory.getLogger(WorkflowPlanServiceImpl.class);

    private WorkflowPlanDAO workflowPlanDAO;

    public WorkflowPlanServiceImpl() {
        super();
    }

    @Override
    public List<WorkflowPlan> findBySequencerRunAndWorkflowName(Long sequencerRunId, String workflowName) {
        logger.debug("ENTERING findBySequencerRunAndWorkflowName(Long, String)");
        List<WorkflowPlan> ret = new ArrayList<WorkflowPlan>();
        if (sequencerRunId == null) {
            logger.warn("sequencerRunId is null");
            return ret;
        }
        if (StringUtils.isEmpty(workflowName)) {
            logger.warn("workflowName is empty");
            return ret;
        }
        try {
            ret = workflowPlanDAO.findBySequencerRunAndWorkflowName(sequencerRunId, workflowName);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public Long save(WorkflowPlan workflowPlan) {
        logger.debug("ENTERING save(WorkflowPlan)");
        Long id = null;
        try {
            id = workflowPlanDAO.save(workflowPlan);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return id;
    }

    @Override
    public List<WorkflowPlan> findBySequencerRunId(Long sequencerRunId) {
        logger.debug("ENTERING findBySequencerRunId(Long)");
        List<WorkflowPlan> ret = new ArrayList<WorkflowPlan>();
        if (sequencerRunId == null) {
            logger.warn("sequencerRunId is null");
            return ret;
        }
        try {
            ret.addAll(workflowPlanDAO.findBySequencerRunId(sequencerRunId));
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public List<WorkflowPlan> findByHTSFSampleId(Long htsfSampleId) {
        logger.info("ENTERING findByHTSFSampleId(Long)");
        List<WorkflowPlan> ret = new ArrayList<WorkflowPlan>();
        if (htsfSampleId == null) {
            logger.warn("htsfSampleId is null");
            return ret;
        }
        try {
            ret.addAll(workflowPlanDAO.findByHTSFSampleId(htsfSampleId));
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public List<WorkflowPlan> findByWorkflowRunId(Long workflowRunId) {
        logger.info("ENTERING findByWorkflowRunId(Long)");
        List<WorkflowPlan> ret = new ArrayList<WorkflowPlan>();
        if (workflowRunId == null) {
            logger.warn("workflowRunId is null");
            return ret;
        }
        try {
            ret.addAll(workflowPlanDAO.findByWorkflowRunId(workflowRunId));
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public List<WorkflowPlan> findByStudyName(String studyName) throws MaPSeqDAOException {
        logger.info("ENTERING findByStudyName(String)");
        List<WorkflowPlan> ret = new ArrayList<WorkflowPlan>();
        if (StringUtils.isEmpty(studyName)) {
            logger.warn("studyName was empty");
            return ret;
        }
        try {
            ret.addAll(workflowPlanDAO.findByStudyName(studyName));
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public List<WorkflowPlan> findByStudyNameAndSampleNameAndWorkflowName(String studyName, String sampleName,
            String workflowName) {
        logger.info("ENTERING findByStudyNameAndSampleNameAndWorkflowName(String, String, String)");
        List<WorkflowPlan> ret = new ArrayList<WorkflowPlan>();

        if (StringUtils.isEmpty(studyName)) {
            logger.warn("studyName is empty");
            return ret;
        }
        if (StringUtils.isEmpty(sampleName)) {
            logger.warn("sampleName is empty");
            return ret;
        }
        if (StringUtils.isEmpty(workflowName)) {
            logger.warn("workflowName is empty");
            return ret;
        }

        try {
            ret.addAll(workflowPlanDAO.findByStudyNameAndSampleNameAndWorkflowName(studyName, sampleName, workflowName));
        } catch (Exception e) {
        }
        return ret;
    }

    public WorkflowPlanDAO getWorkflowPlanDAO() {
        return workflowPlanDAO;
    }

    public void setWorkflowPlanDAO(WorkflowPlanDAO workflowPlanDAO) {
        this.workflowPlanDAO = workflowPlanDAO;
    }

}
