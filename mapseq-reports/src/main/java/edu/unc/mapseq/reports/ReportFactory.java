package edu.unc.mapseq.reports;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.renci.charts.ChartManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.Account;
import edu.unc.mapseq.dao.model.EntityAttribute;
import edu.unc.mapseq.dao.model.Job;
import edu.unc.mapseq.dao.model.Workflow;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.dao.model.WorkflowRunStatusType;

public class ReportFactory {

    private static final Logger logger = LoggerFactory.getLogger(ReportFactory.class);

    private static final ChartManager chartMgr = ChartManager.getInstance();

    public static File createWorkflowJobsPerClusterReport(MaPSeqDAOBean maPSeqDAOBean, Account account,
            Workflow workflow, Date startDate, Date endDate) {
        logger.debug("ENTERING createWorkflowJobsPerClusterReport(MaPSeqDAOBean, Account, Workflow, Date, Date)");

        File chartFile = null;

        try {

            DefaultPieDataset dataset = new DefaultPieDataset();

            Map<String, Integer> map = new HashMap<String, Integer>();
            List<Job> jobList = maPSeqDAOBean.getJobDAO().findByCreatorAndWorkflowIdAndCreationDateRange(
                    account.getId(), workflow.getId(), startDate, endDate);

            for (Job job : jobList) {
                Set<EntityAttribute> attributeSet = job.getAttributes();
                for (EntityAttribute attribute : attributeSet) {
                    String name = attribute.getName();
                    String value = attribute.getValue();
                    if (StringUtils.isNotEmpty(name) && name.equals("siteName") && !map.containsKey(value)) {
                        map.put(value, 0);
                    }
                }
            }

            for (Job job : jobList) {
                Set<EntityAttribute> attributeSet = job.getAttributes();
                for (EntityAttribute attribute : attributeSet) {
                    String name = attribute.getName();
                    String value = attribute.getValue();
                    if (StringUtils.isNotEmpty(name) && name.equals("siteName") && map.containsKey(value)) {
                        map.put(value, map.get(value) + 1);
                    }
                }
            }

            for (String key : map.keySet()) {
                dataset.setValue(key, map.get(key));
            }

            ChartManager chartMgr = ChartManager.getInstance();
            chartFile = chartMgr.createPieChartAsPNG(
                    String.format("MaPSeq :: %s%nJobs Per Cluster (%s - %s)", workflow.getName(),
                            DateFormatUtils.format(startDate, "MM/dd"), DateFormatUtils.format(endDate, "MM/dd")),
                    dataset);

        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("report.getAbsolutePath(): {}", chartFile.getAbsolutePath());
        return chartFile;
    }

    public static File createWorkflowRunReport(MaPSeqDAOBean maPSeqDAOBean, Account account, Date startDate,
            Date endDate) {
        logger.debug("ENTERING createWorkflowRunReport(MaPSeqDAOBean, Date, Date)");

        File chartFile = null;

        try {

            DefaultPieDataset dataset = new DefaultPieDataset();

            WorkflowRunDAO workflowRunDAO = maPSeqDAOBean.getWorkflowRunDAO();
            List<WorkflowRun> workflowRunList = workflowRunDAO.findByCreatorAndCreationDateRange(account.getId(),
                    startDate, endDate);

            Map<String, Integer> map = new HashMap<String, Integer>();

            for (WorkflowRun workflowRun : workflowRunList) {
                if (workflowRun.getStatus().equals(WorkflowRunStatusType.DONE)) {
                    String workflowName = workflowRun.getWorkflow().getName();
                    if (!map.containsKey(workflowName)) {
                        map.put(workflowName, 0);
                    }
                }
            }

            for (WorkflowRun workflowRun : workflowRunList) {
                if (workflowRun.getStatus().equals(WorkflowRunStatusType.DONE)) {
                    String workflowName = workflowRun.getWorkflow().getName();
                    if (map.containsKey(workflowName)) {
                        map.put(workflowName, map.get(workflowName) + 1);
                    }
                }
            }

            for (String key : map.keySet()) {
                dataset.setValue(key, map.get(key));
            }

            ChartManager chartMgr = ChartManager.getInstance();
            chartFile = chartMgr.createPieChartAsPNG(String.format("MaPSeq%nWorkflow Runs (%s - %s)",
                    DateFormatUtils.format(startDate, "MM/dd"), DateFormatUtils.format(endDate, "MM/dd")), dataset);
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("report.getAbsolutePath(): {}", chartFile.getAbsolutePath());
        return chartFile;

    }

    public static File createWorkflowJobsReport(MaPSeqDAOBean maPSeqDAOBean, Account account, Workflow workflow,
            Date startDate, Date endDate) {
        logger.debug("ENTERING createWorkflowJobsReport(MaPSeqDAOBean, Account, Workflow, Date, Date)");

        File chartFile = null;
        try {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            Map<String, List<Long>> map = new HashMap<String, List<Long>>();
            List<Job> jobList = maPSeqDAOBean.getJobDAO().findByCreatorAndWorkflowIdAndCreationDateRange(
                    account.getId(), workflow.getId(), startDate, endDate);

            for (Job job : jobList) {
                String jobName = job.getName();
                if (StringUtils.isNotEmpty(jobName)) {
                    jobName = jobName.substring(jobName.lastIndexOf(".") + 1, jobName.length());
                    if (!map.containsKey(jobName)) {
                        map.put(jobName, new ArrayList<Long>());
                    }
                }
            }

            for (Job job : jobList) {
                String jobName = job.getName();
                if (StringUtils.isNotEmpty(jobName)) {
                    jobName = jobName.substring(jobName.lastIndexOf(".") + 1, jobName.length());
                    if (map.containsKey(jobName) && job.getStartDate() != null && job.getEndDate() != null) {
                        map.get(jobName).add(((job.getEndDate().getTime() - job.getStartDate().getTime()) / 1000) / 60);
                    }
                }
            }

            String series1 = "Average Duration";
            for (String key : map.keySet()) {

                List<Long> jobDurationList = map.get(key);
                Long total = 0L;
                for (Long duration : jobDurationList) {
                    total += duration;
                }

                dataset.setValue(total / jobDurationList.size(), series1, key);
            }

            String series2 = "Max Duration";
            for (String key : map.keySet()) {
                List<Long> jobDurationList = map.get(key);
                Collections.sort(jobDurationList, new Comparator<Long>() {

                    @Override
                    public int compare(Long o1, Long o2) {
                        if (o1 < o2) {
                            return 1;
                        }
                        if (o1 > o2) {
                            return -1;
                        }
                        return 0;
                    }

                });
                dataset.setValue(jobDurationList.get(0), series2, key);
            }

            String title = String.format("MaPSeq :: %s :: Jobs (%s - %s)", workflow.getName(),
                    DateFormatUtils.format(startDate, "MM/dd"), DateFormatUtils.format(endDate, "MM/dd"));
            chartFile = chartMgr.saveAsPNG(chartMgr.createLayeredBarChart(title, "Job", "Duration (Min)", dataset),
                    800, 400);
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("report.getAbsolutePath(): {}", chartFile.getAbsolutePath());

        return chartFile;
    }

}
