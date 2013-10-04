package edu.unc.mapseq.commands.reports;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.model.Workflow;
import edu.unc.mapseq.reporting.ReportManager;

@Command(scope = "mapseq", name = "generate-weekly-workflow-run-report", description = "")
public class GenerateWeeklyWorkflowJobsReportAction extends AbstractAction {

    private final Logger logger = LoggerFactory.getLogger(GenerateWeeklyWorkflowJobsReportAction.class);

    private MaPSeqDAOBean maPSeqDAOBean;

    @Argument(index = 0, name = "workflowId", description = "Workflow Id", required = true, multiValued = false)
    private Long workflowId;

    @Argument(index = 1, name = "toEmailAddress", description = "To Email Address", required = true, multiValued = false)
    private String toEmailAddress;

    @Override
    protected Object doExecute() throws Exception {
        logger.debug("ENTERING doExecute()");
        ReportManager reportMgr = ReportManager.getInstance();
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.WEEK_OF_YEAR, -1);
        File report = reportMgr.createWorkflowJobDurationLayeredBarChart(getMaPSeqDAOBean(), workflowId, c.getTime(),
                date);
        logger.info("report.getAbsolutePath(): {}", report.getAbsolutePath());

        Workflow workflow = getMaPSeqDAOBean().getWorkflowDAO().findById(workflowId);
        String subject = String.format("MaPSeq :: %s :: Weekly Workflow  Report (%s - %s)", workflow.getName(),
                DateFormatUtils.format(c.getTime(), "MM/dd"), DateFormatUtils.format(date, "MM/dd"));

        EmailAttachment attachment = new EmailAttachment();
        attachment.setPath(report.getAbsolutePath());
        attachment.setDisposition(EmailAttachment.ATTACHMENT);
        attachment.setDescription(subject);
        attachment.setName(report.getName());

        MultiPartEmail email = new MultiPartEmail();
        email.setHostName("localhost");
        email.addTo(toEmailAddress);
        email.setFrom(String.format("%s@unc.edu", System.getProperty("user.name")));
        email.setSubject(subject);
        email.setMsg("See Attached");
        email.attach(attachment);

        email.send();

        report.delete();
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
