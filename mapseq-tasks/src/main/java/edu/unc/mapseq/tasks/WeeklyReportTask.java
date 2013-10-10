package edu.unc.mapseq.tasks;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.AccountDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Account;
import edu.unc.mapseq.reports.ReportFactory;

public class WeeklyReportTask implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(WeeklyReportTask.class);

    private MaPSeqDAOBean maPSeqDAOBean;

    public WeeklyReportTask() {
        super();
    }

    @Override
    public void run() {
        logger.debug("ENTERING run()");

        Date endDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(endDate);
        c.add(Calendar.WEEK_OF_YEAR, -1);
        Date startDate = c.getTime();

        try {
            File pdfFile = File.createTempFile("weeklyReport-", ".pdf");

            String username = System.getProperty("user.name");
            AccountDAO accountDAO = maPSeqDAOBean.getAccountDAO();
            Account account = accountDAO.findByName(username);

            File workflowRunReportFile = ReportFactory.createWorkflowRunReport(maPSeqDAOBean, account, startDate,
                    endDate);

            
            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(pdfFile.getAbsolutePath());
            attachment.setDisposition(EmailAttachment.ATTACHMENT);
            attachment.setDescription("MaPSeq Weekly Report");
            attachment.setName(pdfFile.getName());

            MultiPartEmail email = new MultiPartEmail();
            email.setHostName("localhost");
            email.addTo("seqware-users@code.renci.org");
            email.setFrom(String.format("%s@unc.edu", System.getProperty("user.name")));
            email.setSubject("MaPSeq Weekly Report");
            email.setMsg("See Attached");
            email.attach(attachment);

            email.send();
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EmailException e) {
            e.printStackTrace();
        }

    }

    public MaPSeqDAOBean getMaPSeqDAOBean() {
        return maPSeqDAOBean;
    }

    public void setMaPSeqDAOBean(MaPSeqDAOBean maPSeqDAOBean) {
        this.maPSeqDAOBean = maPSeqDAOBean;
    }

}
