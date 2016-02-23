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
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import edu.unc.mapseq.dao.JobDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBeanService;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowRunAttemptDAO;
import edu.unc.mapseq.dao.model.Job;
import edu.unc.mapseq.dao.model.Workflow;
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;
import edu.unc.mapseq.reports.ReportFactory;

public class WeeklyReportTask implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(WeeklyReportTask.class);

    private MaPSeqDAOBeanService maPSeqDAOBeanService;

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
        c.add(Calendar.WEEK_OF_YEAR, -4);
        Date startDate = c.getTime();

        Document document = new Document(PageSize.LETTER.rotate());
        File pdfFile = null;

        try {

            pdfFile = File.createTempFile("weeklyReport-", ".pdf");

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            writer.setCompressionLevel(9);

            document.open();

            PdfPTable summaryTable = new PdfPTable(1);
            summaryTable.setWidthPercentage(100);

            Font font = FontFactory.getFont(FontFactory.COURIER_BOLD);
            font.setSize(18);

            Phrase phrase = new Phrase("MaPSeq :: Overview", font);

            PdfPCell titleCell = new PdfPCell(phrase);
            titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            titleCell.setVerticalAlignment(Element.ALIGN_CENTER);
            titleCell.setBorder(0);
            titleCell.setPaddingBottom(10);
            summaryTable.addCell(titleCell);

            WorkflowRunAttemptDAO workflowRunAttemptDAO = maPSeqDAOBeanService.getWorkflowRunAttemptDAO();
            List<WorkflowRunAttempt> workflowRunAttemptList = workflowRunAttemptDAO.findByCreatedDateRange(startDate,
                    endDate);

            File workflowRunCountReportFile = ReportFactory.createWorkflowRunCountReport(workflowRunAttemptList,
                    startDate, endDate);
            Image img = Image.getInstance(workflowRunCountReportFile.getAbsolutePath());
            img.setAlignment(Element.ALIGN_CENTER);
            img.scalePercent(60, 60);
            PdfPCell imgCell = new PdfPCell(img);
            imgCell.setBorder(0);
            imgCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            imgCell.setVerticalAlignment(Element.ALIGN_CENTER);
            summaryTable.addCell(imgCell);
            workflowRunCountReportFile.delete();

            File workflowRunDurationReportFile = ReportFactory.createWorkflowRunDurationReport(workflowRunAttemptList,
                    startDate, endDate);
            img = Image.getInstance(workflowRunDurationReportFile.getAbsolutePath());
            img.setAlignment(Element.ALIGN_CENTER);
            img.scalePercent(60, 60);
            imgCell = new PdfPCell(img);
            imgCell.setBorder(0);
            imgCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            imgCell.setVerticalAlignment(Element.ALIGN_CENTER);
            summaryTable.addCell(imgCell);
            workflowRunDurationReportFile.delete();
            summaryTable.completeRow();

            document.add(summaryTable);

            Set<Workflow> workflowSet = new HashSet<Workflow>();
            for (WorkflowRunAttempt attempt : workflowRunAttemptList) {
                workflowSet.add(attempt.getWorkflowRun().getWorkflow());
            }

            JobDAO jobDAO = maPSeqDAOBeanService.getJobDAO();

            Set<Workflow> synchronizedWorkflowSet = Collections.synchronizedSet(workflowSet);

            for (Workflow workflow : synchronizedWorkflowSet) {

                logger.debug(workflow.toString());
                document.newPage();

                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100);

                phrase = new Phrase(String.format("MaPSeq :: %s", workflow.getName()), font);

                titleCell = new PdfPCell(phrase);
                titleCell.setColspan(2);
                titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                titleCell.setVerticalAlignment(Element.ALIGN_CENTER);
                titleCell.setBorder(0);
                titleCell.setPaddingBottom(10);
                table.addCell(titleCell);

                List<Job> jobList = jobDAO.findByWorkflowIdAndCreatedDateRange(workflow.getId(), startDate, endDate);

                File workflowJobsPerClusterReportFile = ReportFactory.createWorkflowJobCountPerClusterReport(jobList,
                        workflow, startDate, endDate);
                img = Image.getInstance(workflowJobsPerClusterReportFile.getAbsolutePath());
                img.setAlignment(Element.ALIGN_CENTER);
                img.scalePercent(50, 50);
                imgCell = new PdfPCell(img);
                imgCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                imgCell.setVerticalAlignment(Element.ALIGN_CENTER);
                imgCell.setBorder(0);
                table.addCell(imgCell);
                workflowJobsPerClusterReportFile.delete();

                List<WorkflowRunAttempt> attempts = workflowRunAttemptDAO.findByCreatedDateRangeAndWorkflowId(startDate,
                        endDate, workflow.getId());
                File workflowRunAttemptReportFile = ReportFactory.createWorkflowRunCountReport(workflow.getName(),
                        attempts, startDate, endDate);
                img = Image.getInstance(workflowRunAttemptReportFile.getAbsolutePath());
                img.setAlignment(Element.ALIGN_CENTER);
                img.scalePercent(50, 50);
                imgCell = new PdfPCell(img);
                imgCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                imgCell.setVerticalAlignment(Element.ALIGN_CENTER);
                imgCell.setBorder(0);
                table.addCell(imgCell);
                workflowRunAttemptReportFile.delete();
                table.completeRow();

                File workflowJobsReportFile = ReportFactory.createWorkflowJobsReport(jobList, workflow, startDate,
                        endDate);
                img = Image.getInstance(workflowJobsReportFile.getAbsolutePath());
                img.scalePercent(70, 70);
                img.setAlignment(Element.ALIGN_CENTER);
                imgCell = new PdfPCell(img);
                imgCell.setColspan(2);
                imgCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                imgCell.setVerticalAlignment(Element.ALIGN_CENTER);
                imgCell.setBorder(0);
                table.addCell(imgCell);
                workflowJobsReportFile.delete();

                document.add(table);
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

    public MaPSeqDAOBeanService getMaPSeqDAOBeanService() {
        return maPSeqDAOBeanService;
    }

    public void setMaPSeqDAOBeanService(MaPSeqDAOBeanService maPSeqDAOBeanService) {
        this.maPSeqDAOBeanService = maPSeqDAOBeanService;
    }

}
