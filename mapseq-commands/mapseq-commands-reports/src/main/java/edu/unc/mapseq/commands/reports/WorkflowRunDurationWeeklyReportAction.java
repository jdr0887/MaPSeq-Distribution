package edu.unc.mapseq.commands.reports;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowRunAttemptDAO;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;
import edu.unc.mapseq.reports.ReportFactory;

@Command(scope = "mapseq", name = "generate-workflow-run-duration-weekly-report", description = "")
@Service
public class WorkflowRunDurationWeeklyReportAction implements Action {

    private final Logger logger = LoggerFactory.getLogger(WorkflowRunDurationWeeklyReportAction.class);

    @Reference
    private WorkflowRunDAO workflowRunDAO;

    @Reference
    private WorkflowRunAttemptDAO workflowRunAttemptDAO;

    @Argument(index = 0, name = "toEmailAddress", description = "To Email Address", required = true, multiValued = false)
    private String toEmailAddress;

    @Override
    public Object execute() {
        logger.debug("ENTERING doExecute()");

        try {
            Date endDate = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(endDate);
            c.add(Calendar.WEEK_OF_YEAR, -1);
            Date startDate = c.getTime();

            List<WorkflowRun> workflowRunList = workflowRunDAO.findByCreatedDateRange(startDate, endDate);

            List<WorkflowRunAttempt> workflowRunAttemptList = new ArrayList<>();

            if (CollectionUtils.isNotEmpty(workflowRunList)) {
                for (WorkflowRun workflowRun : workflowRunList) {
                    List<WorkflowRunAttempt> toAdd = workflowRunAttemptDAO.findByWorkflowRunId(workflowRun.getId());
                    if (CollectionUtils.isNotEmpty(toAdd)) {
                        workflowRunAttemptList.addAll(toAdd);
                    }
                }
            }

            File chartFile = ReportFactory.createWorkflowRunDurationReport(workflowRunAttemptList, startDate, endDate);

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
        } catch (MaPSeqDAOException | EmailException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getToEmailAddress() {
        return toEmailAddress;
    }

    public void setToEmailAddress(String toEmailAddress) {
        this.toEmailAddress = toEmailAddress;
    }

}
