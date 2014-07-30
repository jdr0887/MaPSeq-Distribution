package edu.unc.mapseq.reports;

import java.awt.Font;
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
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.renci.charts.ChartManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.model.Attribute;
import edu.unc.mapseq.dao.model.Job;
import edu.unc.mapseq.dao.model.Workflow;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;
import edu.unc.mapseq.dao.model.WorkflowRunAttemptStatusType;

public class ReportFactory {

    private static final Logger logger = LoggerFactory.getLogger(ReportFactory.class);

    private static final ChartManager chartMgr = ChartManager.getInstance();

    public static File createWorkflowJobCountPerClusterReport(List<Job> jobList, Workflow workflow, Date startDate,
            Date endDate) {
        logger.debug("ENTERING createWorkflowJobCountPerClusterReport(List<Job>, Workflow, Date, Date)");

        File chartFile = null;

        try {

            DefaultPieDataset dataset = new DefaultPieDataset();

            Map<String, Integer> map = new HashMap<String, Integer>();

            for (Job job : jobList) {
                Set<Attribute> attributeSet = job.getAttributes();
                if (attributeSet != null && !attributeSet.isEmpty()) {
                    for (Attribute attribute : attributeSet) {
                        String name = attribute.getName();
                        String value = attribute.getValue();
                        if (StringUtils.isNotEmpty(name) && name.equals("siteName") && !map.containsKey(value)) {
                            map.put(value, 0);
                        }
                    }
                }
            }

            for (Job job : jobList) {
                Set<Attribute> attributeSet = job.getAttributes();
                if (attributeSet != null && !attributeSet.isEmpty()) {
                    for (Attribute attribute : attributeSet) {
                        String name = attribute.getName();
                        String value = attribute.getValue();
                        if (StringUtils.isNotEmpty(name) && name.equals("siteName") && map.containsKey(value)) {
                            map.put(value, map.get(value) + 1);
                        }
                    }
                }
            }

            for (String key : map.keySet()) {
                dataset.setValue(key, map.get(key));
            }

            ChartManager chartMgr = ChartManager.getInstance();

            JFreeChart chart = chartMgr.createPieChart(
                    String.format("MaPSeq :: Job Count Per Cluster :: %s", workflow.getName()), dataset);
            Font font = new Font("Dialog", Font.PLAIN, 12);
            chart.addSubtitle(new TextTitle(String.format("(%s - %s)", DateFormatUtils.format(startDate, "MM/dd"),
                    DateFormatUtils.format(endDate, "MM/dd")), font));
            chartFile = chartMgr.saveAsPNG(chart, 600, 400);

        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("report.getAbsolutePath(): {}", chartFile.getAbsolutePath());
        return chartFile;
    }

    public static File createJobPerClusterReport(List<Job> jobList, String jobName, Date startDate, Date endDate) {
        logger.debug("ENTERING createJobPerClusterReport(List<Job> jobList, String, Date, Date)");

        File chartFile = null;

        try {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            List<JobSiteDurationBean> jobSiteDurationList = new ArrayList<JobSiteDurationBean>();

            for (Job job : jobList) {
                Set<Attribute> attributeSet = job.getAttributes();
                if (attributeSet != null && !attributeSet.isEmpty()) {
                    for (Attribute attribute : attributeSet) {
                        String name = attribute.getName();
                        String value = attribute.getValue();
                        if (StringUtils.isNotEmpty(name) && name.equals("siteName")) {
                            jobSiteDurationList.add(new JobSiteDurationBean(job.getName(), value));
                        }
                    }
                }
            }

            for (Job job : jobList) {
                Set<Attribute> attributeSet = job.getAttributes();
                if (attributeSet != null && !attributeSet.isEmpty()) {
                    for (Attribute attribute : attributeSet) {
                        String name = attribute.getName();
                        String value = attribute.getValue();
                        if (StringUtils.isNotEmpty(name) && name.equals("siteName")) {
                            for (JobSiteDurationBean jobSiteDurationBean : jobSiteDurationList) {
                                if (jobSiteDurationBean.getJobName().equals(job.getName())
                                        && jobSiteDurationBean.getSiteName().equals(value) && job.getStarted() != null
                                        && job.getFinished() != null) {
                                    jobSiteDurationBean.getDuration().add(
                                            ((job.getFinished().getTime() - job.getStarted().getTime()) / 1000) / 60);
                                }
                            }
                        }
                    }
                }
            }

            String series1 = "Average Duration";
            for (JobSiteDurationBean jobSiteDurationBean : jobSiteDurationList) {

                List<Long> jobDurationList = jobSiteDurationBean.getDuration();
                Long total = 0L;
                for (Long duration : jobDurationList) {
                    total += duration;
                }

                dataset.setValue(total / jobDurationList.size(), series1, jobSiteDurationBean.getSiteName());
            }

            String series2 = "Max Duration";
            for (JobSiteDurationBean jobSiteDurationBean : jobSiteDurationList) {
                List<Long> jobDurationList = jobSiteDurationBean.getDuration();
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
                dataset.setValue(jobDurationList.get(0), series2, jobSiteDurationBean.getSiteName());
            }

            String title = String.format("MaPSeq :: Job Duration :: %s", jobName);
            JFreeChart chart = chartMgr.createLayeredBarChart(title, "Site", "Duration (Min)", dataset);
            Font font = new Font("Dialog", Font.PLAIN, 12);
            chart.addSubtitle(new TextTitle(String.format("(%s - %s)", DateFormatUtils.format(startDate, "MM/dd"),
                    DateFormatUtils.format(endDate, "MM/dd")), font));
            chartFile = chartMgr.saveAsPNG(chart, 800, 400);
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("report.getAbsolutePath(): {}", chartFile.getAbsolutePath());
        return chartFile;
    }

    public static File createWorkflowRunCountReport(List<WorkflowRun> workflowRunList, Date startDate, Date endDate) {
        logger.debug("ENTERING createWorkflowRunCountReport(MaPSeqDAOBean, Date, Date)");

        File chartFile = null;

        try {

            DefaultPieDataset dataset = new DefaultPieDataset();

            Map<String, Integer> map = new HashMap<String, Integer>();

            for (WorkflowRun workflowRun : workflowRunList) {
                for (WorkflowRunAttempt attempt : workflowRun.getAttempts()) {
                    if (attempt.getStatus().equals(WorkflowRunAttemptStatusType.DONE)) {
                        String workflowName = workflowRun.getWorkflow().getName();
                        if (!map.containsKey(workflowName)) {
                            map.put(workflowName, 0);
                        }
                    }
                }
            }

            for (WorkflowRun workflowRun : workflowRunList) {
                for (WorkflowRunAttempt attempt : workflowRun.getAttempts()) {
                    if (attempt.getStatus().equals(WorkflowRunAttemptStatusType.DONE)) {
                        String workflowName = workflowRun.getWorkflow().getName();
                        if (map.containsKey(workflowName)) {
                            map.put(workflowName, map.get(workflowName) + 1);
                        }
                    }
                }
            }

            for (String key : map.keySet()) {
                dataset.setValue(key, map.get(key));
            }

            ChartManager chartMgr = ChartManager.getInstance();
            JFreeChart chart = chartMgr.createPieChart("MaPSeq :: WorkflowRun Count", dataset);
            Font font = new Font("Dialog", Font.PLAIN, 12);
            chart.addSubtitle(new TextTitle(String.format("(%s - %s)", DateFormatUtils.format(startDate, "MM/dd"),
                    DateFormatUtils.format(endDate, "MM/dd")), font));
            chartFile = chartMgr.saveAsPNG(chart, 600, 400);
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("report.getAbsolutePath(): {}", chartFile.getAbsolutePath());
        return chartFile;

    }

    public static File createWorkflowRunDurationReport(List<WorkflowRun> workflowRunList, Date startDate, Date endDate) {
        logger.debug("ENTERING createWorkflowRunDurationReport(MaPSeqDAOBean, Date, Date)");

        File chartFile = null;

        try {

            DefaultPieDataset dataset = new DefaultPieDataset();

            Map<String, List<Long>> map = new HashMap<String, List<Long>>();

            for (WorkflowRun workflowRun : workflowRunList) {
                for (WorkflowRunAttempt attempt : workflowRun.getAttempts()) {
                    if (attempt.getStatus().equals(WorkflowRunAttemptStatusType.DONE)) {
                        String workflowName = workflowRun.getWorkflow().getName();
                        if (!map.containsKey(workflowName)) {
                            map.put(workflowName, new ArrayList<Long>());
                        }
                    }
                }
            }

            for (WorkflowRun workflowRun : workflowRunList) {
                for (WorkflowRunAttempt attempt : workflowRun.getAttempts()) {
                    if (attempt.getStatus().equals(WorkflowRunAttemptStatusType.DONE)) {
                        String workflowName = workflowRun.getWorkflow().getName();
                        if (map.containsKey(workflowName)) {
                            Date sDate = attempt.getStarted();
                            Date eDate = attempt.getFinished();
                            map.get(workflowName).add(eDate.getTime() - sDate.getTime());
                        }
                    }
                }
            }

            for (String key : map.keySet()) {

                List<Long> jobDurationList = map.get(key);
                Long total = 0L;
                for (Long duration : jobDurationList) {
                    total += duration;
                }

                dataset.setValue(key, total.intValue());

            }

            ChartManager chartMgr = ChartManager.getInstance();
            JFreeChart chart = chartMgr.createPieChart("MaPSeq :: WorkflowRun Duration", dataset);
            PiePlot piePlot = (PiePlot) chart.getPlot();
            piePlot.setLabelGenerator(new CustomLabelGenerator());
            Font font = new Font("Dialog", Font.PLAIN, 12);
            chart.addSubtitle(new TextTitle(String.format("(%s - %s)", DateFormatUtils.format(startDate, "MM/dd"),
                    DateFormatUtils.format(endDate, "MM/dd")), font));
            chartFile = chartMgr.saveAsPNG(chart, 600, 400);
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("report.getAbsolutePath(): {}", chartFile.getAbsolutePath());
        return chartFile;

    }

    public static File createWorkflowJobsReport(List<Job> jobList, Workflow workflow, Date startDate, Date endDate) {
        logger.debug("ENTERING createWorkflowJobsReport(List<Job>, Account, Workflow, Date, Date)");

        File chartFile = null;
        try {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            Map<String, List<Long>> map = new HashMap<String, List<Long>>();

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
                    Date sDate = job.getStarted();
                    Date eDate = job.getFinished();

                    if (map.containsKey(jobName) && sDate != null && eDate != null) {
                        map.get(jobName).add(((eDate.getTime() - sDate.getTime()) / 1000) / 60);
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

            String title = String.format("MaPSeq :: Job Duration :: %s", workflow.getName());
            JFreeChart chart = chartMgr.createLayeredBarChart(title, "Job", "Duration (Min)", dataset);
            Font font = new Font("Dialog", Font.PLAIN, 12);
            chart.addSubtitle(new TextTitle(String.format("(%s - %s)", DateFormatUtils.format(startDate, "MM/dd"),
                    DateFormatUtils.format(endDate, "MM/dd")), font));

            chartFile = chartMgr.saveAsPNG(chart, 800, 400);
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("report.getAbsolutePath(): {}", chartFile.getAbsolutePath());

        return chartFile;
    }

}
