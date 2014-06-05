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

import edu.unc.mapseq.dao.JobDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowPlanDAO;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.Job;
import edu.unc.mapseq.dao.model.WorkflowPlan;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.dao.model.WorkflowRunStatusType;
import edu.unc.mapseq.ws.WorkflowRunService;

public class WorkflowRunServiceImpl implements WorkflowRunService {

    private final Logger logger = LoggerFactory.getLogger(WorkflowRunServiceImpl.class);

    private WorkflowRunDAO workflowRunDAO;

    private WorkflowPlanDAO workflowPlanDAO;

    private JobDAO jobDAO;

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
            logger.debug(workflowRun.toString());
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
    public void delete(Long workflowRunId) {
        logger.debug("ENTERING delete(Long)");
        if (workflowRunId == null) {
            logger.warn("workflowRunId was null");
            return;
        }
        try {
            WorkflowRun wr = workflowRunDAO.findById(workflowRunId);
            if (wr != null) {
                List<WorkflowPlan> workflowPlanList = workflowPlanDAO.findByWorkflowRunId(wr.getId());
                for (WorkflowPlan entity : workflowPlanList) {
                    logger.warn("Deleting WorkflowPlan: " + entity.getId());
                    workflowPlanDAO.delete(entity);
                }
                List<Job> jobList = jobDAO.findByWorkflowRunId(wr.getId());
                for (Job entity : jobList) {
                    logger.warn("Deleting Job: " + entity.getId());
                    jobDAO.delete(entity);
                }
                logger.warn("Deleting WorkflowRun: " + wr.getId());
                workflowRunDAO.delete(wr);
            }
        } catch (MaPSeqDAOException e) {
            logger.warn("Problem with workflowRunDAO.delete({})", workflowRunId);
            logger.error("MaPSeqDAOException", e);
        }
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
    public List<WorkflowRun> findByCreatorAndStatusAndCreationDateRange(Long accountId, String status,
            String startDate, String endDate) {
        logger.debug("ENTERING findByCreatorAndStatusAndCreationDateRange");
        List<WorkflowRun> ret = new ArrayList<WorkflowRun>();

        if (accountId == null) {
            logger.warn("accountId is null");
            return ret;
        }
        if (StringUtils.isEmpty(startDate)) {
            logger.warn("startDate is empty");
            return ret;
        }
        if (StringUtils.isEmpty(endDate)) {
            logger.warn("endDate is empty");
            return ret;
        }
        if (StringUtils.isEmpty(status)) {
            logger.warn("status is empty");
            return ret;
        }

        try {
            Date parsedStartDate = DateUtils.parseDate(startDate,
                    new String[] { DateFormatUtils.ISO_DATE_FORMAT.getPattern() });
            Date parsedEndDate = DateUtils.parseDate(endDate,
                    new String[] { DateFormatUtils.ISO_DATE_FORMAT.getPattern() });
            ret.addAll(workflowRunDAO.findByCreatorAndStatusAndCreationDateRange(accountId,
                    WorkflowRunStatusType.valueOf(status), parsedStartDate, parsedEndDate));
        } catch (ParseException | MaPSeqDAOException e) {
            logger.warn("Problem with workflowRunDAO.findByCreator({})", accountId);
            logger.error("MaPSeqDAOException", e);
        }
        return ret;
    }

    @Override
    public List<WorkflowRun> findByCreatorAndCreationDateRange(Long accountId, String startDate, String endDate) {
        logger.debug("ENTERING findByCreatorAndDateRange(Long, String, String)");
        List<WorkflowRun> ret = new ArrayList<WorkflowRun>();
        if (accountId == null) {
            logger.warn("accountId is null");
            return ret;
        }
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
            ret.addAll(workflowRunDAO.findByCreatorAndCreationDateRange(accountId, parsedStartDate, parsedEndDate));
        } catch (ParseException e1) {
            e1.printStackTrace();
        } catch (MaPSeqDAOException e) {
            logger.warn("Problem with workflowRunDAO.findByCreator({})", accountId);
            logger.error("MaPSeqDAOException", e);
        }
        return ret;
    }

    public WorkflowPlanDAO getWorkflowPlanDAO() {
        return workflowPlanDAO;
    }

    public void setWorkflowPlanDAO(WorkflowPlanDAO workflowPlanDAO) {
        this.workflowPlanDAO = workflowPlanDAO;
    }

    public JobDAO getJobDAO() {
        return jobDAO;
    }

    public void setJobDAO(JobDAO jobDAO) {
        this.jobDAO = jobDAO;
    }

    public WorkflowRunDAO getWorkflowRunDAO() {
        return workflowRunDAO;
    }

    public void setWorkflowRunDAO(WorkflowRunDAO workflowRunDAO) {
        this.workflowRunDAO = workflowRunDAO;
    }

}
