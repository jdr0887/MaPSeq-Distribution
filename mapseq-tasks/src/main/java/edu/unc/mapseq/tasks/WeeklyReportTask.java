package edu.unc.mapseq.tasks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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

import edu.unc.mapseq.dao.JobDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowRunAttemptDAO;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.Job;
import edu.unc.mapseq.dao.model.Workflow;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;
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
        File pdfFile = null;

        try {

            pdfFile = File.createTempFile("weeklyReport-", ".pdf");

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            writer.setCompressionLevel(9);

            document.open();
            document.setMargins(10, 10, 10, 10);

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

            document.add(new Paragraph());
            File workflowRunCountReportFile = ReportFactory.createWorkflowRunCountReport(workflowRunAttemptList,
                    startDate, endDate);
            Image img = Image.getInstance(workflowRunCountReportFile.getAbsolutePath());
            img.setAlignment(Element.ALIGN_CENTER);
            img.scalePercent(60, 60);
            document.add(img);
            workflowRunCountReportFile.delete();

            document.add(new Paragraph());
            File workflowRunDurationReportFile = ReportFactory.createWorkflowRunDurationReport(workflowRunAttemptList,
                    startDate, endDate);
            img = Image.getInstance(workflowRunDurationReportFile.getAbsolutePath());
            img.setAlignment(Element.ALIGN_CENTER);
            img.scalePercent(60, 60);
            document.add(img);
            workflowRunDurationReportFile.delete();

            Set<Workflow> workflowSet = new HashSet<Workflow>();
            for (WorkflowRun workflowRun : workflowRunList) {
                workflowSet.add(workflowRun.getWorkflow());
            }

            JobDAO jobDAO = maPSeqDAOBean.getJobDAO();

            Set<Workflow> synchronizedWorkflowSet = Collections.synchronizedSet(workflowSet);

            for (Workflow workflow : synchronizedWorkflowSet) {

                logger.debug(workflow.toString());

                document.newPage();

                List<Job> jobList = jobDAO.findByWorkflowIdAndCreatedDateRange(workflow.getId(), startDate, endDate);

                document.add(new Paragraph());
                File workflowJobsPerClusterReportFile = ReportFactory.createWorkflowJobCountPerClusterReport(jobList,
                        workflow, startDate, endDate);
                img = Image.getInstance(workflowJobsPerClusterReportFile.getAbsolutePath());
                img.setAlignment(Element.ALIGN_CENTER);
                img.scalePercent(65, 65);
                document.add(img);

                workflowJobsPerClusterReportFile.delete();

                document.add(new Paragraph());
                File workflowJobsReportFile = ReportFactory.createWorkflowJobsReport(jobList, workflow, startDate,
                        endDate);
                img = Image.getInstance(workflowJobsReportFile.getAbsolutePath());
                img.scalePercent(70, 70);
                img.setAlignment(Element.ALIGN_CENTER);
                document.add(img);

                workflowJobsReportFile.delete();

            }

            document.close();

        } catch (IOException | DocumentException | MaPSeqDAOException e) {
            logger.error("Error", e);
        } catch (Exception e) {
            logger.error("Error", e);
        }

        if (pdfFile != null) {

            try {
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

            } catch (EmailException e) {
                e.printStackTrace();
            }

            pdfFile.delete();

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
