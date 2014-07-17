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
import edu.unc.mapseq.dao.model.Job;
import edu.unc.mapseq.ws.JobService;

public class JobServiceImpl implements JobService {

    private final Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);

    private JobDAO jobDAO;

    @Override
    public Job findById(Long id) {
        logger.debug("ENTERING findById(Long)");
        Job job = null;
        if (id == null) {
            logger.warn("id is null");
            return job;
        }
        try {
            job = jobDAO.findById(id);
        } catch (MaPSeqDAOException e) {
            logger.error("Error", e);
        }
        return job;
    }

    @Override
    public Long save(Job processing) {
        logger.debug("ENTERING save(Job)");
        Long id = null;
        try {
            id = jobDAO.save(processing);
            logger.debug("job: {}", processing);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return id;
    }

    @Override
    public List<Job> findByWorkflowRunId(Long workflowRunId) {
        logger.debug("ENTERING findByWorkflowRunId(Long)");
        List<Job> ret = new ArrayList<Job>();
        if (workflowRunId == null) {
            logger.warn("workflowRunId is null");
            return ret;
        }
        try {
            ret.addAll(jobDAO.findByWorkflowRunId(workflowRunId));
        } catch (MaPSeqDAOException e) {
            logger.error("Error", e);
        }
        return ret;
    }

    @Override
    public List<Job> findByCreatorAndWorkflowIdAndCreationDateRange(Long accountId, Long workflowId, String startDate,
            String endDate) {
        logger.debug("ENTERING findByCreatorAndWorkflowIdAndCreationDateRange(Long, Long, String, String)");
        List<Job> ret = new ArrayList<Job>();
        if (accountId == null) {
            logger.warn("accountId is null");
            return ret;
        }
        if (workflowId == null) {
            logger.warn("workflowId is null");
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
            ret.addAll(jobDAO.findByCreatorAndWorkflowIdAndCreationDateRange(accountId, workflowId, parsedStartDate,
                    parsedEndDate));
        } catch (ParseException | MaPSeqDAOException e) {
            logger.error("Error", e);
        }
        return ret;
    }

    public JobDAO getJobDAO() {
        return jobDAO;
    }

    public void setJobDAO(JobDAO jobDAO) {
        this.jobDAO = jobDAO;
    }

}
