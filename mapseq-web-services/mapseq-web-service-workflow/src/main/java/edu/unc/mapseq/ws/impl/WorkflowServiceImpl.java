package edu.unc.mapseq.ws.impl;

import java.util.List;

import org.apache.cxf.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowDAO;
import edu.unc.mapseq.dao.model.Workflow;
import edu.unc.mapseq.ws.WorkflowService;

public class WorkflowServiceImpl implements WorkflowService {

    private final Logger logger = LoggerFactory.getLogger(WorkflowServiceImpl.class);

    private WorkflowDAO workflowDAO;

    @Override
    public List<Workflow> findAll() {
        logger.debug("ENTERING findAll()");
        List<Workflow> workflow = null;
        try {
            workflow = workflowDAO.findAll();
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return workflow;
    }

    @Override
    public Workflow findById(Long id) {
        logger.debug("ENTERING findById(Long)");
        Workflow workflow = null;
        if (id == null) {
            logger.warn("id is null");
            return workflow;
        }
        try {
            workflow = workflowDAO.findById(id);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return workflow;
    }

    @Override
    public Workflow findByName(String name) {
        logger.debug("ENTERING findByName(String)");
        Workflow workflow = null;
        if (StringUtils.isEmpty(name)) {
            logger.warn("name is emtpy");
            return workflow;
        }
        try {
            workflow = workflowDAO.findByName(name);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return workflow;
    }

    @Override
    public Long save(Workflow entity) {
        logger.debug("ENTERING save(Workflow)");
        Long ret = null;
        try {
            ret = workflowDAO.save(entity);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public WorkflowDAO getWorkflowDAO() {
        return workflowDAO;
    }

    public void setWorkflowDAO(WorkflowDAO workflowDAO) {
        this.workflowDAO = workflowDAO;
    }

}
