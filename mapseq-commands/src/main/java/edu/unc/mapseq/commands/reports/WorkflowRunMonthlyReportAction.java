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

import edu.unc.mapseq.dao.AccountDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.model.Account;
import edu.unc.mapseq.reports.ReportFactory;

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

        String username = System.getProperty("user.name");
        AccountDAO accountDAO = maPSeqDAOBean.getAccountDAO();
        Account account = accountDAO.findByName(username);

        File chartFile = ReportFactory.createWorkflowRunReport(maPSeqDAOBean, account, startDate, endDate);

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
