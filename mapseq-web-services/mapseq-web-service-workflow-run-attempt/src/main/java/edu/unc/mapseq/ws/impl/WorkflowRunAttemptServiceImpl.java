package edu.unc.mapseq.ws.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowRunAttemptDAO;
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;
import edu.unc.mapseq.ws.WorkflowRunAttemptService;

public class WorkflowRunAttemptServiceImpl implements WorkflowRunAttemptService {

    private final Logger logger = LoggerFactory.getLogger(WorkflowRunAttemptServiceImpl.class);

    private WorkflowRunAttemptDAO workflowRunAttemptDAO;

    public WorkflowRunAttemptServiceImpl() {
        super();
    }

    @Override
    public WorkflowRunAttempt findById(Long id) {
        logger.debug("ENTERING findById(Long)");
        WorkflowRunAttempt workflowRunAttempt = null;
        if (id == null) {
            logger.warn("id is null");
            return workflowRunAttempt;
        }
        try {
            workflowRunAttempt = workflowRunAttemptDAO.findById(id);
            logger.debug(workflowRunAttempt.toString());
        } catch (MaPSeqDAOException e) {
            logger.warn("Problem with workflowRunAttemptDAO.findById({})", id);
            logger.error("MaPSeqDAOException", e);
        }
        return workflowRunAttempt;
    }

    @Override
    public List<WorkflowRunAttempt> findByWorkflowRunId(Long workflowRunId) throws MaPSeqDAOException {
        logger.debug("ENTERING findByWorkflowRunId(Long)");
        List<WorkflowRunAttempt> ret = new ArrayList<>();
        if (workflowRunId == null) {
            logger.warn("workflowRunId is null");
            return ret;
        }
        try {
            ret.addAll(workflowRunAttemptDAO.findByWorkflowRunId(workflowRunId));
        } catch (MaPSeqDAOException e) {
            logger.warn("Problem with workflowRunAttemptDAO.findByWorkflowRunId({})", workflowRunId);
            logger.error("MaPSeqDAOException", e);
        }
        return ret;
    }

    @Override
    public List<WorkflowRunAttempt> findByWorkflowId(Long workflowId) throws MaPSeqDAOException {
        logger.debug("ENTERING findByWorkflowId(Long)");
        List<WorkflowRunAttempt> ret = new ArrayList<>();
        if (workflowId == null) {
            logger.warn("workflowId is null");
            return ret;
        }
        try {
            ret.addAll(workflowRunAttemptDAO.findByWorkflowId(workflowId));
        } catch (MaPSeqDAOException e) {
            logger.warn("Problem with workflowRunAttemptDAO.findByWorkflowId({})", workflowId);
            logger.error("MaPSeqDAOException", e);
        }
        return ret;
    }

    public WorkflowRunAttemptDAO getWorkflowRunAttemptDAO() {
        return workflowRunAttemptDAO;
    }

    public void setWorkflowRunAttemptDAO(WorkflowRunAttemptDAO workflowRunAttemptDAO) {
        this.workflowRunAttemptDAO = workflowRunAttemptDAO;
    }

}
