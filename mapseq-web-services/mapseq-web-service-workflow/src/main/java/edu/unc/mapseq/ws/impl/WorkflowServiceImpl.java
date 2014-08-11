package edu.unc.mapseq.ws.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
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
            logger.error("Error", e);
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
            logger.error("Error", e);
        }
        return workflow;
    }

    @Override
    public List<Workflow> findByCreatedDateRange(String started, String finished) {
        logger.debug("ENTERING findByCreatedDateRange(String, String)");
        List<Workflow> ret = new ArrayList<>();
        if (StringUtils.isEmpty(started)) {
            logger.warn("started is emtpy");
            return ret;
        }
        if (StringUtils.isEmpty(finished)) {
            logger.warn("finished is emtpy");
            return ret;
        }
        try {
            Date parsedStartDate = DateUtils.parseDate(started,
                    new String[] { DateFormatUtils.ISO_DATE_FORMAT.getPattern() });
            Date parsedEndDate = DateUtils.parseDate(finished,
                    new String[] { DateFormatUtils.ISO_DATE_FORMAT.getPattern() });
            ret.addAll(workflowDAO.findByCreatedDateRange(parsedStartDate, parsedEndDate));
        } catch (ParseException e) {
            logger.error("Error", e);
        } catch (MaPSeqDAOException e) {
            logger.error("Error", e);
        }
        return ret;
    }

    @Override
    public List<Workflow> findByName(String name) {
        logger.debug("ENTERING findByName(String)");
        List<Workflow> ret = new ArrayList<>();
        if (StringUtils.isEmpty(name)) {
            logger.warn("name is emtpy");
            return ret;
        }
        try {
            ret.addAll(workflowDAO.findByName(name));
        } catch (MaPSeqDAOException e) {
            logger.error("Error", e);
        }
        return ret;
    }

    @Override
    public Long save(Workflow entity) {
        logger.debug("ENTERING save(Workflow)");
        Long ret = null;
        try {
            ret = workflowDAO.save(entity);
        } catch (MaPSeqDAOException e) {
            logger.error("Error", e);
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
