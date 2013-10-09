package edu.unc.mapseq.commands.reports;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;
import org.jfree.data.general.DefaultPieDataset;
import org.renci.charts.ChartManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.AccountDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.WorkflowDAO;
import edu.unc.mapseq.dao.model.Account;
import edu.unc.mapseq.dao.model.EntityAttribute;
import edu.unc.mapseq.dao.model.Job;
import edu.unc.mapseq.dao.model.Workflow;

@Command(scope = "mapseq", name = "generate-weekly-jobs-per-cluster-report", description = "")
public class WorkflowJobsPerClusterWeeklyReportAction extends AbstractAction {

    private final Logger logger = LoggerFactory.getLogger(WorkflowJobsPerClusterWeeklyReportAction.class);

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

        DefaultPieDataset dataset = new DefaultPieDataset();
        String username = System.getProperty("user.name");

        try {

            AccountDAO accountDAO = maPSeqDAOBean.getAccountDAO();
            Account account = accountDAO.findByName(username);

            Map<String, Integer> map = new HashMap<String, Integer>();
            List<Job> jobList = maPSeqDAOBean.getJobDAO().findByCreatorAndWorkflowIdAndCreationDateRange(
                    account.getId(), workflowId, startDate, endDate);
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

        } catch (Exception e) {
            e.printStackTrace();
        }

        ChartManager chartMgr = ChartManager.getInstance();
        File chartFile = null;
        WorkflowDAO workflowDAO = maPSeqDAOBean.getWorkflowDAO();
        Workflow workflow = workflowDAO.findById(workflowId);
        try {
            chartFile = chartMgr.createPieChartAsPNG(
                    String.format("MaPSeq :: %s%nJobs Per Cluster (%s - %s)", workflow.getName(),
                            DateFormatUtils.format(startDate, "MM/dd"), DateFormatUtils.format(endDate, "MM/dd")),
                    dataset);
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("report.getAbsolutePath(): {}", chartFile.getAbsolutePath());

        String subject = String.format("MaPSeq :: %s :: Weekly Jobs Per Cluster Report (%s - %s)", workflow.getName(),
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
