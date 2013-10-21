package edu.unc.mapseq.tasks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import edu.unc.mapseq.dao.AccountDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.Account;
import edu.unc.mapseq.dao.model.Job;
import edu.unc.mapseq.dao.model.Workflow;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.reports.ReportFactory;

public class WeeklyReportTask implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(WeeklyReportTask.class);

    private MaPSeqDAOBean maPSeqDAOBean;

    private String toEmailAddress;

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

        Document document = new Document(PageSize.LETTER.rotate());

        try {
            File pdfFile = File.createTempFile("weeklyReport-", ".pdf");

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            writer.setCompressionLevel(0);

            document.open();
            document.setMargins(5, 5, 5, 5);

            String username = System.getProperty("user.name");
            // String username = "rc_renci.svc";
            AccountDAO accountDAO = maPSeqDAOBean.getAccountDAO();
            Account account = accountDAO.findByName(username);

            WorkflowRunDAO workflowRunDAO = maPSeqDAOBean.getWorkflowRunDAO();
            List<WorkflowRun> workflowRunList = workflowRunDAO.findByCreatorAndCreationDateRange(account.getId(),
                    startDate, endDate);

            document.add(new Paragraph());
            File workflowRunReportFile = ReportFactory.createWorkflowRunReport(workflowRunList, account, startDate,
                    endDate);
            Image img = Image.getInstance(workflowRunReportFile.getAbsolutePath());
            img.setAlignment(Element.ALIGN_CENTER);
            img.scalePercent(80, 80);
            document.add(img);

            workflowRunReportFile.delete();

            Set<Workflow> workflowSet = new HashSet<Workflow>();
            for (WorkflowRun workflowRun : workflowRunList) {
                workflowSet.add(workflowRun.getWorkflow());
            }

            Set<Workflow> synchronizedWorkflowSet = Collections.synchronizedSet(workflowSet);
            for (Workflow workflow : synchronizedWorkflowSet) {

                document.newPage();

                List<Job> jobList = maPSeqDAOBean.getJobDAO().findByCreatorAndWorkflowIdAndCreationDateRange(
                        account.getId(), workflow.getId(), startDate, endDate);

                document.add(new Paragraph());
                File workflowJobsPerClusterReportFile = ReportFactory.createWorkflowJobsPerClusterReport(jobList,
                        account, workflow, startDate, endDate);
                img = Image.getInstance(workflowJobsPerClusterReportFile.getAbsolutePath());
                img.setAlignment(Element.ALIGN_CENTER);
                img.scalePercent(60, 60);
                document.add(img);

                document.add(new Paragraph());
                File workflowJobsReportFile = ReportFactory.createWorkflowJobsReport(jobList, account, workflow,
                        startDate, endDate);
                img = Image.getInstance(workflowJobsReportFile.getAbsolutePath());
                img.scalePercent(80, 80);
                img.setAlignment(Element.ALIGN_CENTER);
                document.add(img);

                workflowJobsPerClusterReportFile.delete();
                workflowJobsReportFile.delete();
            }

            document.close();

            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(pdfFile.getAbsolutePath());
            attachment.setDisposition(EmailAttachment.ATTACHMENT);
            attachment.setDescription("MaPSeq Weekly Report");
            attachment.setName(pdfFile.getName());

            MultiPartEmail email = new MultiPartEmail();
            email.setHostName("localhost");
            email.addTo(toEmailAddress);
            email.setFrom(String.format("%s@unc.edu", System.getProperty("user.name")));
            email.setSubject("MaPSeq Weekly Report");
            email.setMsg("See Attached");
            email.attach(attachment);

            email.send();

            pdfFile.delete();
        } catch (IOException | DocumentException | MaPSeqDAOException | EmailException e) {
            e.printStackTrace();
        }

    }

    public String getToEmailAddress() {
        return toEmailAddress;
    }

    public void setToEmailAddress(String toEmailAddress) {
        this.toEmailAddress = toEmailAddress;
    }

    public MaPSeqDAOBean getMaPSeqDAOBean() {
        return maPSeqDAOBean;
    }

    public void setMaPSeqDAOBean(MaPSeqDAOBean maPSeqDAOBean) {
        this.maPSeqDAOBean = maPSeqDAOBean;
    }

}
