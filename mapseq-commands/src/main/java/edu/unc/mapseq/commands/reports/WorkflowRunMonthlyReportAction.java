package edu.unc.mapseq.commands.reports;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.Account;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.dao.model.WorkflowRunStatusType;

@Command(scope = "mapseq", name = "generate-monthly-workflow-run-report", description = "")
public class WorkflowRunMonthlyReportAction extends AbstractAction {

    private final Logger logger = LoggerFactory.getLogger(WorkflowRunMonthlyReportAction.class);

    private MaPSeqDAOBean maPSeqDAOBean;

    @Argument(index = 0, name = "toEmailAddress", description = "To Email Address", required = true, multiValued = false)
    private String toEmailAddress;

    @Override
    protected Object doExecute() throws Exception {
        logger.debug("ENTERING doExecute()");
        Date endDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(endDate);
        c.add(Calendar.WEEK_OF_YEAR, -4);
        Date startDate = c.getTime();

        WorkflowRunDAO workflowRunDAO = maPSeqDAOBean.getWorkflowRunDAO();

        DefaultPieDataset dataset = new DefaultPieDataset();

        String username = System.getProperty("user.name");
        try {

            AccountDAO accountDAO = maPSeqDAOBean.getAccountDAO();
            Account account = accountDAO.findByName(username);
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

        } catch (Exception e) {
            e.printStackTrace();
        }

        ChartManager chartMgr = ChartManager.getInstance();
        File chartFile = null;
        try {
            chartFile = chartMgr.createPieChartAsPNG(String.format("MaPSeq%nWorkflow Runs (%s - %s)",
                    DateFormatUtils.format(startDate, "MM/dd"), DateFormatUtils.format(endDate, "MM/dd")), dataset);
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("report.getAbsolutePath(): {}", chartFile.getAbsolutePath());

        String subject = String.format("MaPSeq :: Monthly WorkflowRun Report (%s - %s)",
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

}
