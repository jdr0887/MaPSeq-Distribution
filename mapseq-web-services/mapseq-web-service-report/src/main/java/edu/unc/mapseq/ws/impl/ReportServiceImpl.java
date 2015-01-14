package edu.unc.mapseq.ws.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowRunAttemptDAO;
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;
import edu.unc.mapseq.dao.model.WorkflowRunAttemptStatusType;
import edu.unc.mapseq.ws.reports.ReportDataItem;
import edu.unc.mapseq.ws.reports.ReportService;

public class ReportServiceImpl implements ReportService {

    private final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

    private WorkflowRunAttemptDAO workflowRunAttemptDAO;

    public ReportServiceImpl() {
        super();
    }

    @Override
    public List<ReportDataItem> findWorkflowRunCount(String started, String finished, String status) {

        List<ReportDataItem> ret = new ArrayList<ReportDataItem>();

        if (StringUtils.isEmpty(started)) {
            logger.warn("started is empty");
            return ret;
        }
        if (StringUtils.isEmpty(finished)) {
            logger.warn("finished is empty");
            return ret;
        }
        if (StringUtils.isEmpty(status)) {
            logger.warn("status is empty");
            return ret;
        }

        WorkflowRunAttemptStatusType statusType = null;

        for (WorkflowRunAttemptStatusType type : WorkflowRunAttemptStatusType.values()) {
            if (type.name().equals(status)) {
                statusType = type;
                break;
            }
        }

        if (statusType == null) {
            logger.warn("statusType is null");
            return ret;
        }

        List<WorkflowRunAttempt> attempts = new ArrayList<WorkflowRunAttempt>();

        try {
            Date parsedStartDate = DateUtils.parseDate(started,
                    new String[] { DateFormatUtils.ISO_DATE_FORMAT.getPattern() });
            Date parsedEndDate = DateUtils.parseDate(finished,
                    new String[] { DateFormatUtils.ISO_DATE_FORMAT.getPattern() });
            List<WorkflowRunAttempt> foundAttempts = workflowRunAttemptDAO.findByCreatedDateRangeAndStatus(
                    parsedStartDate, parsedEndDate, statusType);
            if (foundAttempts != null && !foundAttempts.isEmpty()) {
                attempts.addAll(foundAttempts);
            }
        } catch (ParseException e1) {
            e1.printStackTrace();
        } catch (MaPSeqDAOException e) {
            logger.error("MaPSeqDAOException", e);
        }

        Map<String, Double> map = new HashMap<String, Double>();

        for (WorkflowRunAttempt attempt : attempts) {
            String workflowName = attempt.getWorkflowRun().getWorkflow().getName();
            if (!map.containsKey(workflowName)) {
                map.put(workflowName, 0D);
            }
        }

        for (WorkflowRunAttempt attempt : attempts) {
            String workflowName = attempt.getWorkflowRun().getWorkflow().getName();
            if (map.containsKey(workflowName)) {
                map.put(workflowName, map.get(workflowName) + 1);
            }
        }

        for (String key : map.keySet()) {
            ret.add(new ReportDataItem(key, map.get(key)));
        }

        return ret;
    }

    @Override
    public List<ReportDataItem> findWorkflowRunDuration(String started, String finished, String status) {

        List<ReportDataItem> ret = new ArrayList<ReportDataItem>();

        if (StringUtils.isEmpty(started)) {
            logger.warn("started is empty");
            return ret;
        }
        if (StringUtils.isEmpty(finished)) {
            logger.warn("finished is empty");
            return ret;
        }
        if (StringUtils.isEmpty(status)) {
            logger.warn("status is empty");
            return ret;
        }

        WorkflowRunAttemptStatusType statusType = null;

        for (WorkflowRunAttemptStatusType type : WorkflowRunAttemptStatusType.values()) {
            if (type.name().equals(status)) {
                statusType = type;
                break;
            }
        }

        if (statusType == null) {
            logger.warn("statusType is null");
            return ret;
        }

        List<WorkflowRunAttempt> attempts = new ArrayList<WorkflowRunAttempt>();

        try {
            Date parsedStartDate = DateUtils.parseDate(started,
                    new String[] { DateFormatUtils.ISO_DATE_FORMAT.getPattern() });
            Date parsedEndDate = DateUtils.parseDate(finished,
                    new String[] { DateFormatUtils.ISO_DATE_FORMAT.getPattern() });
            List<WorkflowRunAttempt> foundAttempts = workflowRunAttemptDAO.findByCreatedDateRangeAndStatus(
                    parsedStartDate, parsedEndDate, statusType);
            if (foundAttempts != null && !foundAttempts.isEmpty()) {
                attempts.addAll(foundAttempts);
            }
        } catch (ParseException e1) {
            e1.printStackTrace();
        } catch (MaPSeqDAOException e) {
            logger.error("MaPSeqDAOException", e);
        }

        Map<String, List<Long>> map = new HashMap<String, List<Long>>();

        for (WorkflowRunAttempt attempt : attempts) {
            String workflowName = attempt.getWorkflowRun().getWorkflow().getName();
            if (!map.containsKey(workflowName)) {
                map.put(workflowName, new ArrayList<Long>());
            }
        }

        for (WorkflowRunAttempt attempt : attempts) {
            String workflowName = attempt.getWorkflowRun().getWorkflow().getName();
            if (map.containsKey(workflowName)) {
                Date sDate = attempt.getStarted();
                Date eDate = attempt.getFinished();
                map.get(workflowName).add(eDate.getTime() - sDate.getTime());
            }
        }

        for (String key : map.keySet()) {

            List<Long> jobDurationList = map.get(key);
            Long total = 0L;
            for (Long duration : jobDurationList) {
                total += duration;
            }

            ret.add(new ReportDataItem(key, total.doubleValue()));

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
