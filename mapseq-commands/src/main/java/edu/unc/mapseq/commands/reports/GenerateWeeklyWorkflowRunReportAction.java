package edu.unc.mapseq.commands.reports;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.reporting.ReportManager;

@Command(scope = "mapseq", name = "generate-weekly-workflow-run-report", description = "")
public class GenerateWeeklyWorkflowRunReportAction extends AbstractAction {

    private final Logger logger = LoggerFactory.getLogger(GenerateWeeklyWorkflowRunReportAction.class);

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
        c.add(Calendar.WEEK_OF_YEAR, -1);
        File report = reportMgr.createWorkflowRunPieChart(getMaPSeqDAOBean(), c.getTime(), date);
        logger.info("report.getAbsolutePath(): {}", report.getAbsolutePath());

        // EmailAttachment attachment = new EmailAttachment();
        // attachment.setPath(report.getAbsolutePath());
        // attachment.setDisposition(EmailAttachment.ATTACHMENT);
        // attachment.setDescription("Picture of John");
        // attachment.setName("John");
        //
        // // Create the email message
        // MultiPartEmail email = new MultiPartEmail();
        // email.setHostName("localhost");
        // email.addTo("jdr0887@renci.org");
        // email.setFrom("jdr0887@renci.org", "Jason");
        // email.setSubject("The picture");
        // email.setMsg("Here is the picture you wanted");
        //
        // // add the attachment
        // email.attach(attachment);
        //ClassLoader bakCL = Thread.currentThread().getContextClassLoader();
        // // Thread.currentThread().setContextClassLoader(null);
        // Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        // try {
        // email.send();
        // } finally {
        // Thread.currentThread().setContextClassLoader(bakCL);
        // }

        // Properties properties = System.getProperties();
        // properties.setProperty("mail.smtp.host", "localhost");
        // Session session = Session.getDefaultInstance(properties);
        //
        // try {
        // MimeMessage message = new MimeMessage(session);
        // message.setFrom(new InternetAddress(String.format("%s@unc.edu", System.getProperty("user.name"))));
        // message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmailAddress));
        // message.setSubject(String.format("MaPSeq Weekly WorkflowRun Report (%s - %s)",
        // DateFormatUtils.format(c.getTime(), "MM/dd"), DateFormatUtils.format(date, "MM/dd")));
        // message.setText("See Attachments");
        //
        // BodyPart messageBodyPart = new MimeBodyPart();
        // messageBodyPart.setText("See Attachments");
        //
        // Multipart multipart = new MimeMultipart();
        // multipart.addBodyPart(messageBodyPart);
        //
        // messageBodyPart = new MimeBodyPart();
        // messageBodyPart.setHeader("Content-Type", "image/png");
        // DataSource source = new FileDataSource(report);
        // messageBodyPart.setDataHandler(new DataHandler(source));
        // messageBodyPart.setFileName(report.getName());
        // multipart.addBodyPart(messageBodyPart);
        //
        // // MimeBodyPart messageBodyPart = new MimeBodyPart();
        // // messageBodyPart.attachFile(report);
        // // Multipart multipart = new MimeMultipart();
        // // multipart.addBodyPart(messageBodyPart);
        //
        // message.setContent(multipart);
        //
        // //Thread.currentThread().setContextClassLoader(null);
        // Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        // try {
        // Transport.send(message);
        // } finally {
        // Thread.currentThread().setContextClassLoader(bakCL);
        // }
        //
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
