package edu.unc.mapseq.commands.reports;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.reporting.ReportManager;

@Command(scope = "mapseq", name = "generate-monthly-workflow-run-report", description = "")
public class GenerateMonthlyWorkflowRunReportAction extends AbstractAction {

    private final Logger logger = LoggerFactory.getLogger(GenerateMonthlyWorkflowRunReportAction.class);

    private MaPSeqDAOBean maPSeqDAOBean;

    @Argument(index = 0, name = "toEmailAddress", description = "To Email Address", required = true, multiValued = false)
    private String toEmailAddress;

    @Override
    protected Object doExecute() throws Exception {
        logger.debug("ENTERING doExecute()");
        ReportManager reportMgr = ReportManager.getInstance();
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.WEEK_OF_YEAR, -4);
        File report = reportMgr.createWorkflowRunPieChart(getMaPSeqDAOBean(), c.getTime(), date);
        logger.info("report.getAbsolutePath(): {}", report.getAbsolutePath());

        // Properties properties = System.getProperties();
        // properties.setProperty("mail.smtp.host", "localhost");
        // Session session = Session.getDefaultInstance(properties);
        // try {
        // MimeMessage message = new MimeMessage(session);
        // message.setFrom(new InternetAddress(String.format("%s@unc.edu", System.getProperty("user.name"))));
        // message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmailAddress));
        // message.setSubject(String.format("MaPSeq Monthly WorkflowRun Report (%s - %s)",
        // DateFormatUtils.format(c.getTime(), "MM/dd"), DateFormatUtils.format(date, "MM/dd")));
        //
        // BodyPart messageBodyPart = new MimeBodyPart();
        // messageBodyPart.setText("See Attachments");
        // Multipart multipart = new MimeMultipart();
        // multipart.addBodyPart(messageBodyPart);
        // messageBodyPart = new MimeBodyPart();
        // DataSource source = new FileDataSource(report);
        // messageBodyPart.setDataHandler(new DataHandler(source));
        // messageBodyPart.setFileName(report.getName());
        // multipart.addBodyPart(messageBodyPart);
        // message.setContent(multipart);
        //
        // Transport.send(message);
        // } catch (MessagingException mex) {
        // mex.printStackTrace();
        // }
        // report.delete();
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
