package edu.unc.mapseq.tasks;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

import org.junit.Test;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class Scratch {

    @Test
    public void test() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.add(Calendar.DATE, 7);
        long delay = c.getTimeInMillis() - System.currentTimeMillis();
        System.out.println(delay);
    }

    @Test
    public void testPDF() throws Exception {
        File pdfFile = File.createTempFile("weeklyReport-", ".pdf");

        Document document = new Document(PageSize.LETTER.rotate());

        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
        writer.setCompressionLevel(0);
        document.open();
        document.setMargins(10, 10, 10, 10);
        
        document.add(new Paragraph());
        File workflowRunReportFile = new File("/home/jdr0887/tmp/png/chart-6268670401298662495.png");
        Image img = Image.getInstance(workflowRunReportFile.getAbsolutePath());
        img.setAlignment(Element.ALIGN_CENTER);
        document.add(img);

        document.newPage();
        
        document.add(new Paragraph());
        File workflowJobsPerClusterReportFile = new File("/home/jdr0887/tmp/png/chart-3736966714272996099.png");
        img = Image.getInstance(workflowJobsPerClusterReportFile.getAbsolutePath());
        img.scalePercent(65, 65);
        img.setAlignment(Element.ALIGN_CENTER);
        document.add(img);

        document.add(new Paragraph());
        File workflowJobsReportFile = new File("/home/jdr0887/tmp/png/chart-6662184132046297448.png");
        img = Image.getInstance(workflowJobsReportFile.getAbsolutePath());
        img.scalePercent(75, 75);
        img.setAlignment(Element.ALIGN_CENTER);
        document.add(img);

        document.close();

    }

}
