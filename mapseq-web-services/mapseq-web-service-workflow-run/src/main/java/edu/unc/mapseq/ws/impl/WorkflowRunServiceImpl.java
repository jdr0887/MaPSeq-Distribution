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
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.ws.WorkflowRunService;

public class WorkflowRunServiceImpl implements WorkflowRunService {

    private final Logger logger = LoggerFactory.getLogger(WorkflowRunServiceImpl.class);

    private WorkflowRunDAO workflowRunDAO;

    public WorkflowRunServiceImpl() {
        super();
    }

    @Override
    public WorkflowRun findById(Long id) {
        logger.debug("ENTERING findById(Long)");
        WorkflowRun workflowRun = null;
        if (id == null) {
            logger.warn("id is null");
            return workflowRun;
        }
        try {
            workflowRun = workflowRunDAO.findById(id);
            if (workflowRun != null) {
                logger.debug(workflowRun.toString());
            }
        } catch (MaPSeqDAOException e) {
            logger.warn("Problem with workflowRunDAO.findById({})", id);
            logger.error("MaPSeqDAOException", e);
        }
        return workflowRun;
    }

    @Override
    public List<WorkflowRun> findByName(String name) {
        logger.debug("ENTERING findByName(String)");
        List<WorkflowRun> ret = new ArrayList<WorkflowRun>();
        if (StringUtils.isEmpty(name)) {
            logger.warn("name is empty");
            return ret;
        }
        try {
            ret.addAll(workflowRunDAO.findByName(name));
        } catch (MaPSeqDAOException e) {
            logger.warn("Problem with workflowRunDAO.findByName({})", name);
            logger.error("MaPSeqDAOException", e);
        }
        return ret;
    }

    @Override
    public List<WorkflowRun> findByStudyNameAndSampleNameAndWorkflowName(String studyName, String sampleName,
            String workflowName) {
        logger.debug("ENTERING findByStudyNameAndSampleNameAndWorkflowName(String, String, String)");
        List<WorkflowRun> ret = new ArrayList<WorkflowRun>();
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
            ret.addAll(workflowRunDAO.findByStudyNameAndSampleNameAndWorkflowName(studyName, sampleName, workflowName));
        } catch (MaPSeqDAOException e) {
            logger.warn("Problem with workflowRunDAO.findByStudyNameAndSampleNameAndWorkflowName({})", sampleName);
            logger.error("MaPSeqDAOException", e);
        }
        return ret;
    }

    @Override
    public WorkflowRun findByWorkflowRunAttemptId(Long workflowRunAttemptId) {
        logger.debug("ENTERING findByWorkflowRunAttemptId(Long)");
        if (workflowRunAttemptId == null) {
            logger.warn("workflowRunAttemptId is empty");
            return null;
        }
        try {
            return workflowRunDAO.findByWorkflowRunAttemptId(workflowRunAttemptId);
        } catch (MaPSeqDAOException e) {
            logger.warn("Problem with workflowRunDAO.findByWorkflowRunAttemptId({})", workflowRunAttemptId);
            logger.error("MaPSeqDAOException", e);
        }
        return null;
    }

    @Override
    public Long save(WorkflowRun workflowRun) {
        logger.debug("ENTERING save");
        Long workflowRunId = null;
        try {
            workflowRunId = workflowRunDAO.save(workflowRun);
        } catch (MaPSeqDAOException e) {
            logger.warn("Problem with workflowRunDAO.save({})", workflowRun);
            logger.error("MaPSeqDAOException", e);
        }
        return workflowRunId;
    }

    @Override
    public List<WorkflowRun> findByCreatedDateRange(String startDate, String endDate) {
        logger.debug("ENTERING findByCreatorAndDateRange(Long, String, String)");
        List<WorkflowRun> ret = new ArrayList<WorkflowRun>();
        if (StringUtils.isEmpty(startDate)) {
            logger.warn("startDate is empty");
            return ret;
        }
        if (StringUtils.isEmpty(endDate)) {
            logger.warn("endDate is empty");
            return ret;
        }
        try {
            Date parsedStartDate = DateUtils.parseDate(startDate,
                    new String[] { DateFormatUtils.ISO_DATE_FORMAT.getPattern() });
            Date parsedEndDate = DateUtils.parseDate(endDate,
                    new String[] { DateFormatUtils.ISO_DATE_FORMAT.getPattern() });
            ret.addAll(workflowRunDAO.findByCreatedDateRange(parsedStartDate, parsedEndDate));
        } catch (ParseException e1) {
            e1.printStackTrace();
        } catch (MaPSeqDAOException e) {
            logger.error("MaPSeqDAOException", e);
        }
        return ret;
    }

    @Override
    public List<WorkflowRun> findByFlowcellId(Long flowcellId) throws MaPSeqDAOException {
        logger.debug("ENTERING findByFlowcellId(Long)");
        List<WorkflowRun> ret = new ArrayList<WorkflowRun>();
        if (flowcellId == null) {
            logger.warn("flowcellId is null");
            return ret;
        }
        try {
            ret.addAll(workflowRunDAO.findByFlowcellId(flowcellId));
        } catch (MaPSeqDAOException e) {
            logger.error("MaPSeqDAOException", e);
        }
        return ret;
    }

    @Override
    public List<WorkflowRun> findByFlowcellIdAndWorkflowId(Long flowcellId, Long workflowId) {
        logger.debug("ENTERING findByFlowcellIdAndWorkflowId(Long, Long)");
        List<WorkflowRun> ret = new ArrayList<WorkflowRun>();
        if (flowcellId == null) {
            logger.warn("flowcellId is null");
            return ret;
        }
        if (workflowId == null) {
            logger.warn("workflowId is null");
            return ret;
        }
        try {
            ret.addAll(workflowRunDAO.findByFlowcellIdAndWorkflowId(flowcellId, workflowId));
        } catch (MaPSeqDAOException e) {
            logger.error("MaPSeqDAOException", e);
        }
        return ret;
    }

    @Override
    public List<WorkflowRun> findBySampleId(Long sampleId) throws MaPSeqDAOException {
        logger.debug("ENTERING findBySampleId(Long)");
        List<WorkflowRun> ret = new ArrayList<WorkflowRun>();
        if (sampleId == null) {
            logger.warn("sampleId is null");
            return ret;
        }
        try {
            ret.addAll(workflowRunDAO.findBySampleId(sampleId));
        } catch (MaPSeqDAOException e) {
            logger.error("MaPSeqDAOException", e);
        }
        return ret;
    }

    @Override
    public List<WorkflowRun> findByStudyId(Long studyId) throws MaPSeqDAOException {
        logger.debug("ENTERING findByStudyId(Long)");
        List<WorkflowRun> ret = new ArrayList<WorkflowRun>();
        if (studyId == null) {
            logger.warn("studyId is null");
            return ret;
        }
        try {
            ret.addAll(workflowRunDAO.findByStudyId(studyId));
        } catch (MaPSeqDAOException e) {
            logger.error("MaPSeqDAOException", e);
        }
        return ret;
    }

    @Override
    public List<WorkflowRun> findByWorkflowId(Long workflowId) throws MaPSeqDAOException {
        logger.debug("ENTERING findByWorkflowId(Long)");
        List<WorkflowRun> ret = new ArrayList<WorkflowRun>();
        if (workflowId == null) {
            logger.warn("workflowId is null");
            return ret;
        }
        try {
            ret.addAll(workflowRunDAO.findByWorkflowId(workflowId));
        } catch (MaPSeqDAOException e) {
            logger.error("MaPSeqDAOException", e);
        }
        return ret;
    }

    public WorkflowRunDAO getWorkflowRunDAO() {
        return workflowRunDAO;
    }

    public void setWorkflowRunDAO(WorkflowRunDAO workflowRunDAO) {
        this.workflowRunDAO = workflowRunDAO;
    }

}
