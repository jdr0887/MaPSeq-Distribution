package edu.unc.mapseq.commands.core.reports;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

import edu.unc.mapseq.dao.JobDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowDAO;
import edu.unc.mapseq.dao.model.Job;
import edu.unc.mapseq.dao.model.Workflow;
import edu.unc.mapseq.reports.ReportFactory;

@Command(scope = "mapseq", name = "generate-workflow-job-per-cluster-weekly-report", description = "")
@Service
public class WorkflowJobCountPerClusterWeeklyReportAction implements Action {

    private final Logger logger = LoggerFactory.getLogger(WorkflowJobCountPerClusterWeeklyReportAction.class);

    @Reference
    private WorkflowDAO workflowDAO;

    @Reference
    private JobDAO jobDAO;

    @Argument(index = 0, name = "workflowId", description = "Workflow Id", required = true, multiValued = false)
    private Long workflowId;

    @Argument(index = 1, name = "toEmailAddress", description = "To Email Address", required = true, multiValued = false)
    private String toEmailAddress;

    @Override
    public Object execute() {
        logger.debug("ENTERING execute()");

        try {
            Workflow workflow = workflowDAO.findById(workflowId);

            Date endDate = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(endDate);
            c.add(Calendar.WEEK_OF_YEAR, -1);
            Date startDate = c.getTime();

            List<Job> jobList = jobDAO.findByWorkflowIdAndCreatedDateRange(workflow.getId(), startDate, endDate);

            File chartFile = ReportFactory.createWorkflowJobCountPerClusterReport(jobList, workflow, startDate,
                    endDate);

            String subject = String.format("MaPSeq : %s : Jobs Per Cluster Report : (%s - %s)", workflow.getName(),
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

    public Long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

}
