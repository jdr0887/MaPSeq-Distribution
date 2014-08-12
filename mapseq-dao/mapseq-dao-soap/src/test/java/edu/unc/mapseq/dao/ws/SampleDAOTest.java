package edu.unc.mapseq.dao.ws;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.junit.Test;

import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.dao.model.Sample;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;

public class SampleDAOTest {

    @Test
    public void testSave() {

        WSDAOManager daoMgr = WSDAOManager.getInstance("edu/unc/mapseq/dao/ws/mapseq-dao-beans-test.xml");

        Set<FileData> fileDataSet = new HashSet<FileData>();

        try {

            FileData read1FastqFD = new FileData();
            read1FastqFD.setMimeType(MimeType.FASTQ);
            File read1Fastq = new File(
                    "/home/jdr0887/tmp/TCGA-B0-4836-01A-01R-1305-07_TCGA001900_NoIndex_L001_R1.fastq.gz");
            read1FastqFD.setName(read1Fastq.getName());
            read1FastqFD.setPath(read1Fastq.getParentFile().getAbsolutePath());

            List<FileData> fileDataList = daoMgr.getMaPSeqDAOBean().getFileDataDAO().findByExample(read1FastqFD);
            if (fileDataList != null && fileDataList.size() > 0) {
                read1FastqFD = fileDataList.get(0);
            } else {
                Long id = daoMgr.getMaPSeqDAOBean().getFileDataDAO().save(read1FastqFD);
                read1FastqFD = daoMgr.getMaPSeqDAOBean().getFileDataDAO().findById(id);
            }
            fileDataSet.add(read1FastqFD);

            File read2Fastq = new File(
                    "/home/jdr0887/tmp/TCGA-B0-4836-01A-01R-1305-07_TCGA001900_NoIndex_L001_R2.fastq.gz");
            if (read2Fastq != null) {
                FileData read2FastqFD = new FileData();
                read2FastqFD.setMimeType(MimeType.FASTQ);
                read2FastqFD.setName(read2Fastq.getName());
                read2FastqFD.setPath(read2Fastq.getParentFile().getAbsolutePath());
                fileDataList = daoMgr.getMaPSeqDAOBean().getFileDataDAO().findByExample(read2FastqFD);
                if (fileDataList != null && fileDataList.size() > 0) {
                    read2FastqFD = fileDataList.get(0);
                } else {
                    Long id = daoMgr.getMaPSeqDAOBean().getFileDataDAO().save(read2FastqFD);
                    read2FastqFD = daoMgr.getMaPSeqDAOBean().getFileDataDAO().findById(id);
                }
                fileDataSet.add(read2FastqFD);
            }

            SampleDAO htsfSampleDAO = daoMgr.getMaPSeqDAOBean().getSampleDAO();

            Sample htsfSample = new Sample();
            htsfSample.setName("asdf");
            htsfSample.setBarcode("ATTCGA");
            htsfSample.setStudy(daoMgr.getMaPSeqDAOBean().getStudyDAO().findById(45823L));
            htsfSample.setLaneIndex(1);
            htsfSample.setFlowcell(daoMgr.getMaPSeqDAOBean().getFlowcellDAO().findById(48432L));
            htsfSample.setFileDatas(fileDataSet);

            try {
                JAXBContext context = JAXBContext.newInstance(Sample.class);
                Marshaller m = context.createMarshaller();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                File moduleClassXMLFile = new File("/tmp/sample.xml");
                FileWriter fw = new FileWriter(moduleClassXMLFile);
                m.marshal(htsfSample, fw);
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (PropertyException e1) {
                e1.printStackTrace();
            } catch (JAXBException e1) {
                e1.printStackTrace();
            }

            Long id = htsfSampleDAO.save(htsfSample);
            htsfSample.setId(id);
            System.out.println("HTSFSampleID: " + id);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testFindByFlowcellIdAndSampleName() throws Exception {

        WSDAOManager daoMgr = WSDAOManager.getInstance("edu/unc/mapseq/dao/ws/mapseq-dao-beans-test.xml");
        SampleDAO hTSFSampleDAO = daoMgr.getMaPSeqDAOBean().getSampleDAO();
        // List<HTSFSample> htsfSampleList = hTSFSampleDAO.findByFlowcellIdAndSampleName(27352L, "NCG_00007%");
        List<Sample> sampleList = hTSFSampleDAO.findByFlowcellId(56470L);
        if (sampleList != null && sampleList.size() > 0) {
            for (Sample sample : sampleList) {
                try {
                    JAXBContext context = JAXBContext.newInstance(Sample.class);
                    Marshaller m = context.createMarshaller();
                    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                    File moduleClassXMLFile = new File("/tmp", String.format("%s.xml", sample.getName()));
                    FileWriter fw = new FileWriter(moduleClassXMLFile);
                    m.marshal(sample, fw);
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (PropertyException e1) {
                    e1.printStackTrace();
                } catch (JAXBException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    @Test
    public void testFileDataRegex() throws Exception {

        WSDAOManager daoMgr = WSDAOManager.getInstance("edu/unc/mapseq/dao/ws/mapseq-dao-beans-test.xml");

        FlowcellDAO flowcellDAO = daoMgr.getMaPSeqDAOBean().getFlowcellDAO();

        Long flowcellId = 27352L;
        Flowcell flowcell = flowcellDAO.findById(flowcellId);
        SampleDAO sampleDAO = daoMgr.getMaPSeqDAOBean().getSampleDAO();

        List<Sample> sampleList = sampleDAO.findByNameAndFlowcellId("NCG_00142%", flowcellId);

        if (sampleList != null && sampleList.size() > 0) {
            for (Sample sample : sampleList) {

                Set<FileData> fileDataSet = sample.getFileDatas();
                List<String> readPairList = new ArrayList<String>();

                if (fileDataSet != null && fileDataSet.size() > 0) {
                    for (FileData fileData : sample.getFileDatas()) {
                        MimeType mimeType = fileData.getMimeType();
                        if (mimeType != null && mimeType.equals(MimeType.FASTQ)) {
                            Pattern patternR1 = Pattern.compile("^" + flowcell.getName() + ".*_L00"
                                    + sample.getLaneIndex() + "_R1\\.fastq\\.gz$");
                            Matcher matcherR1 = patternR1.matcher(fileData.getName());
                            File file = new File(fileData.getPath(), fileData.getName());
                            if (matcherR1.matches()) {
                                readPairList.add(file.getAbsolutePath());
                            }

                            Pattern patternR2 = Pattern.compile("^" + flowcell.getName() + ".*_L00"
                                    + sample.getLaneIndex() + "_R2\\.fastq\\.gz$");
                            Matcher matcherR2 = patternR2.matcher(fileData.getName());
                            if (matcherR2.matches()) {
                                readPairList.add(file.getAbsolutePath());
                            }
                        }
                    }
                }

                for (String s : readPairList) {
                    System.out.println(s);
                }
            }
        }
    }

    @Test
    public void testWorkflowPlan() {
        WSDAOManager daoMgr = WSDAOManager.getInstance("edu/unc/mapseq/dao/ws/mapseq-dao-beans-test.xml");
        List<WorkflowRun> wpList = new ArrayList<WorkflowRun>();
        WorkflowRunDAO workflowRunDAO = daoMgr.getMaPSeqDAOBean().getWorkflowRunDAO();
        String sampleName = "NCG_00142%";

        try {
            List<WorkflowRun> wfRunList = workflowRunDAO.findByStudyNameAndSampleNameAndWorkflowName("NC_GENES",
                    sampleName, "NCGenes");
            // List<WorkflowPlan> wfPlanList = workflowPlanDAO.findByStudyNameAndSampleNameAndWorkflowName("NC_GENES",
            // sampleName, "NCGenes");
            if (wfRunList != null) {
                wpList.addAll(wfRunList);
            }
        } catch (MaPSeqDAOException e) {
        }

        if (wpList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter(sb, Locale.US);
            formatter.format("%1$-35s %2$-18s %3$-20s %4$-24s %5$s%n", "Sample Name", "Workflow Name",
                    "Workflow Run Status", "Start Date", "End Date");
            for (WorkflowRun wr : wpList) {

                try {
                    JAXBContext context = JAXBContext.newInstance(WorkflowRun.class);
                    Marshaller m = context.createMarshaller();
                    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                    File moduleClassXMLFile = new File("/tmp", String.format("%s-%d.xml", "WorkflowRun", wr.getId()));
                    FileWriter fw = new FileWriter(moduleClassXMLFile);
                    m.marshal(wr, fw);
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (PropertyException e1) {
                    e1.printStackTrace();
                } catch (JAXBException e1) {
                    e1.printStackTrace();
                }

                for (Sample sample : wr.getSamples()) {

                    try {
                        JAXBContext context = JAXBContext.newInstance(Sample.class);
                        Marshaller m = context.createMarshaller();
                        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                        File moduleClassXMLFile = new File("/tmp", String.format("%s.xml", sample.getName()));
                        FileWriter fw = new FileWriter(moduleClassXMLFile);
                        m.marshal(sample, fw);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (PropertyException e1) {
                        e1.printStackTrace();
                    } catch (JAXBException e1) {
                        e1.printStackTrace();
                    }
                }

                for (WorkflowRunAttempt workflowRunAttempt : wr.getAttempts()) {

                    Date startDate = workflowRunAttempt.getStarted();
                    String formattedStartDate = "";
                    if (startDate != null) {
                        formattedStartDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(
                                workflowRunAttempt.getStarted());
                    }
                    Date endDate = workflowRunAttempt.getFinished();
                    String formattedEndDate = "";
                    if (endDate != null) {
                        formattedEndDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(
                                workflowRunAttempt.getFinished());
                    }
                    formatter.format("%1$-18s %2$-20s %3$-24s %4$s%n", wr.getWorkflow().getName(), workflowRunAttempt
                            .getStatus().getState(), formattedStartDate, formattedEndDate);
                }
                formatter.flush();
            }
            System.out.println(formatter.toString());
            formatter.close();
        }

    }

}