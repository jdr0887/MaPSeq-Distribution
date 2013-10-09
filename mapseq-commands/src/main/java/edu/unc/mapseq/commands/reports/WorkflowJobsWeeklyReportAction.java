package edu.unc.mapseq.commands.reports;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;
import org.jfree.data.category.DefaultCategoryDataset;
import org.renci.charts.ChartManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.AccountDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.WorkflowDAO;
import edu.unc.mapseq.dao.model.Account;
import edu.unc.mapseq.dao.model.Job;
import edu.unc.mapseq.dao.model.Workflow;

@Command(scope = "mapseq", name = "generate-weekly-workflow-run-report", description = "")
public class WorkflowJobsWeeklyReportAction extends AbstractAction {

    private final Logger logger = LoggerFactory.getLogger(WorkflowJobsWeeklyReportAction.class);

    private MaPSeqDAOBean maPSeqDAOBean;

    @Argument(index = 0, name = "workflowId", description = "Workflow Id", required = true, multiValued = false)
    private Long workflowId;

    @Argument(index = 1, name = "toEmailAddress", description = "To Email Address", required = true, multiValued = false)
    private String toEmailAddress;

    @Override
    protected Object doExecute() throws Exception {
        logger.debug("ENTERING doExecute()");
        Date endDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(endDate);
        c.add(Calendar.WEEK_OF_YEAR, -1);
        Date startDate = c.getTime();

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        // String username = "rc_renci.svc";
        String username = System.getProperty("user.name");
        try {

            AccountDAO accountDAO = maPSeqDAOBean.getAccountDAO();
            Account account = accountDAO.findByName(username);
            Map<String, List<Long>> map = new HashMap<String, List<Long>>();
            List<Job> jobList = maPSeqDAOBean.getJobDAO().findByCreatorAndWorkflowIdAndCreationDateRange(
                    account.getId(), workflowId, startDate, endDate);
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

        } catch (Exception e) {
            e.printStackTrace();
        }

        ChartManager chartMgr = ChartManager.getInstance();
        File chartFile = null;
        try {
            WorkflowDAO workflowDAO = maPSeqDAOBean.getWorkflowDAO();
            Workflow workflow = workflowDAO.findById(workflowId);

            String title = String.format("MaPSeq :: %s :: Jobs (%s - %s)", workflow.getName(),
                    DateFormatUtils.format(startDate, "MM/dd"), DateFormatUtils.format(endDate, "MM/dd"));
            chartFile = chartMgr.saveAsPNG(chartMgr.createLayeredBarChart(title, "Job", "Duration (Min)", dataset),
                    800, 400);
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("report.getAbsolutePath(): {}", chartFile.getAbsolutePath());

        Workflow workflow = getMaPSeqDAOBean().getWorkflowDAO().findById(workflowId);
        String subject = String.format("MaPSeq :: %s :: Jobs (%s - %s)", workflow.getName(),
                DateFormatUtils.format(startDate, "MM/dd"), DateFormatUtils.format(endDate, "MM/dd"));

        EmailAttachment attachment = new EmailAttachment();
        attachment.setPath(chartFile.getAbsolutePath());
        attachment.setDisposition(EmailAttachment.ATTACHMENT);
        attachment.setDescription(subject);
        attachment.setName(chartFile.getName());

        MultiPartEmail email = new MultiPartEmail();
        email.setHostName("localhost");
        email.addTo(toEmailAddress);
        email.setFrom(String.format("%s@unc.edu", username));
        email.setSubject(subject);
        email.setMsg("See Attached");
        email.attach(attachment);

        email.send();

        chartFile.delete();
        return null;
    }

    public MaPSeqDAOBean getMaPSeqDAOBean() {
        return maPSeqDAOBean;
    }

    public void setMaPSeqDAOBean(MaPSeqDAOBean maPSeqDAOBean) {
        this.maPSeqDAOBean = maPSeqDAOBean;
    }

    public String getToEmailAddress() {
        return toEmailAddress;
    }

    public void setToEmailAddress(String toEmailAddress) {
        this.toEmailAddress = toEmailAddress;
    }

    public Long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

}
