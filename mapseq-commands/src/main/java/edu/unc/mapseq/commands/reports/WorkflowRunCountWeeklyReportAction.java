package edu.unc.mapseq.commands.reports;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.WorkflowRunAttemptDAO;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;
import edu.unc.mapseq.reports.ReportFactory;

@Command(scope = "mapseq", name = "generate-workflow-run-count-weekly-report", description = "")
public class WorkflowRunCountWeeklyReportAction extends AbstractAction {

    private final Logger logger = LoggerFactory.getLogger(WorkflowRunCountWeeklyReportAction.class);

    private MaPSeqDAOBean maPSeqDAOBean;

    @Argument(index = 0, name = "toEmailAddress", description = "To Email Address", required = true, multiValued = false)
    private String toEmailAddress;

    @Override
    protected Object doExecute() throws Exception {
        logger.debug("ENTERING doExecute()");

        Date endDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(endDate);
        c.add(Calendar.WEEK_OF_YEAR, -1);
        Date startDate = c.getTime();

        WorkflowRunDAO workflowRunDAO = maPSeqDAOBean.getWorkflowRunDAO();
        WorkflowRunAttemptDAO workflowRunAttemptDAO = maPSeqDAOBean.getWorkflowRunAttemptDAO();
        List<WorkflowRun> workflowRunList = workflowRunDAO.findByCreatedDateRange(startDate, endDate);
        List<WorkflowRunAttempt> workflowRunAttemptList = new ArrayList<>();

        if (workflowRunList != null && !workflowRunList.isEmpty()) {
            for (WorkflowRun workflowRun : workflowRunList) {
                List<WorkflowRunAttempt> toAdd = workflowRunAttemptDAO.findByWorkflowRunId(workflowRun.getId());
                if (toAdd != null && !toAdd.isEmpty()) {
                    workflowRunAttemptList.addAll(toAdd);
                }
            }
        }

        File chartFile = ReportFactory.createWorkflowRunCountReport(workflowRunAttemptList, startDate, endDate);

        String subject = String.format("MaPSeq : WorkflowRunAttempts (%s - %s)",
                DateFormatUtils.format(startDate, "MM/dd"), DateFormatUtils.format(endDate, "MM/dd"));

        EmailAttachment attachment = new EmailAttachment();
        attachment.setPath(chartFile.getAbsolutePath());
        attachment.setDisposition(EmailAttachment.ATTACHMENT);
        attachment.setDescription(subject);
        attachment.setName(chartFile.getName());

        MultiPartEmail email = new MultiPartEmail();
        email.setHostName("localhost");
        email.addTo(toEmailAddress);
        email.setFrom(String.format("%s@unc.edu", System.getProperty("user.name")));
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
