package edu.unc.mapseq.dao.ws;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Attribute;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.model.Sample;
import edu.unc.mapseq.dao.model.Workflow;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;
import edu.unc.mapseq.dao.soap.SOAPDAOManager;

public class FlowcellDAOTest {

    @Test
    public void testFindByCreationDateRange() {
        SOAPDAOManager daoMgr = SOAPDAOManager.getInstance();
        try {

            Date parsedStartDate = DateUtils.parseDate("2014-07-01",
                    new String[] { DateFormatUtils.ISO_DATE_FORMAT.getPattern() });
            Date parsedEndDate = DateUtils.parseDate("2014-07-11",
                    new String[] { DateFormatUtils.ISO_DATE_FORMAT.getPattern() });

            List<Flowcell> entityList = daoMgr.getMaPSeqDAOBeanService().getFlowcellDAO()
                    .findByCreatedDateRange(parsedStartDate, parsedEndDate);
            if (entityList != null && entityList.size() > 0) {
                for (Flowcell flowcell : entityList) {
                    // System.out.println(flowcell.toString());
                    Set<String> attributeNameSet = new HashSet<String>();

                    List<Sample> sampleList = daoMgr.getMaPSeqDAOBeanService().getSampleDAO()
                            .findByFlowcellId(flowcell.getId());
                    for (Sample sample : sampleList) {
                        // System.out.println(sample.toString());
                        Set<Attribute> attributeSet = sample.getAttributes();
                        for (Attribute attribute : attributeSet) {
                            // System.out.printf("%s:%s%n", attribute.getName(), attribute.getValue());
                            attributeNameSet.add(attribute.getName());
                        }
                    }

                    if (!attributeNameSet.contains("q30YieldPassingFiltering")) {
                        System.out.println(flowcell.toString());
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testSave() {
        SOAPDAOManager daoMgr = SOAPDAOManager.getInstance();
        FlowcellDAO flowcellDAO = daoMgr.getMaPSeqDAOBeanService().getFlowcellDAO();
        try {
            Flowcell entity = new Flowcell("test");
            entity.setBaseDirectory("adsf");
            Long id = flowcellDAO.save(entity);
            System.out.println(id);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void ncgenesMigrationPull() {
        try {
            SOAPDAOManager daoMgr = SOAPDAOManager.getInstance();

            JAXBContext workflowContext = JAXBContext.newInstance(Workflow.class);
            Marshaller workflowMarshaller = workflowContext.createMarshaller();
            workflowMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            JAXBContext flowcellContext = JAXBContext.newInstance(Flowcell.class);
            Marshaller flowcellMarshaller = flowcellContext.createMarshaller();
            flowcellMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            JAXBContext sampleContext = JAXBContext.newInstance(Sample.class);
            Marshaller sampleMarshaller = sampleContext.createMarshaller();
            sampleMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            JAXBContext workflowRunContext = JAXBContext.newInstance(WorkflowRun.class);
            Marshaller workflowRunMarshaller = workflowRunContext.createMarshaller();
            workflowRunMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            JAXBContext workflowRunAttemptContext = JAXBContext.newInstance(WorkflowRunAttempt.class);
            Marshaller workflowRunAttemptMarshaller = workflowRunAttemptContext.createMarshaller();
            workflowRunAttemptMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            List<Workflow> workflowList = daoMgr.getMaPSeqDAOBeanService().getWorkflowDAO().findAll();
            if (CollectionUtils.isNotEmpty(workflowList)) {
                for (Workflow workflow : workflowList) {
                    File moduleClassXMLFile = new File("/tmp/mapseq/workflows",
                            String.format("%s-%d.xml", "Workflow", workflow.getId()));
                    moduleClassXMLFile.getParentFile().mkdirs();
                    try (FileWriter fw = new FileWriter(moduleClassXMLFile)) {
                        workflowMarshaller.marshal(workflow, fw);
                    }
                }
            }

            List<Flowcell> flowcellList = daoMgr.getMaPSeqDAOBeanService().getFlowcellDAO().findByStudyName("NC_GENES");
            if (CollectionUtils.isNotEmpty(flowcellList)) {
                for (Flowcell flowcell : flowcellList) {

                    System.out.println(flowcell.toString());

                    File flowcellXMLFile = new File("/tmp/mapseq/flowcells",
                            String.format("%s-%d.xml", "Flowcell", flowcell.getId()));
                    flowcellXMLFile.getParentFile().mkdirs();
                    try (FileWriter fw = new FileWriter(flowcellXMLFile)) {
                        sampleMarshaller.marshal(flowcell, fw);
                    }

                    List<WorkflowRun> flowcellWorkflowRunList = daoMgr.getMaPSeqDAOBeanService().getWorkflowRunDAO()
                            .findByFlowcellId(flowcell.getId());
                    if (CollectionUtils.isNotEmpty(flowcellWorkflowRunList)) {
                        for (WorkflowRun workflowRun : flowcellWorkflowRunList) {

                            System.out.println(workflowRun.toString());

                            File workflowRunXMLFile = new File("/tmp/mapseq/flowcells/workflowruns",
                                    String.format("%s-%d.xml", "WorkflowRun", workflowRun.getId()));
                            workflowRunXMLFile.getParentFile().mkdirs();
                            try (FileWriter fw = new FileWriter(workflowRunXMLFile)) {
                                workflowRunMarshaller.marshal(workflowRun, fw);
                            }

                            List<WorkflowRunAttempt> workflowRunAttemptList = daoMgr.getMaPSeqDAOBeanService()
                                    .getWorkflowRunAttemptDAO().findByWorkflowRunId(workflowRun.getId());

                            if (CollectionUtils.isNotEmpty(workflowRunAttemptList)) {
                                for (WorkflowRunAttempt workflowRunAttempt : workflowRunAttemptList) {
                                    File workflowRunAttemptXMLFile = new File(
                                            "/tmp/mapseq/flowcells/workflowrunattempts", String.format("%s-%d.xml",
                                                    "WorkflowRunAttempt", workflowRunAttempt.getId()));
                                    workflowRunAttemptXMLFile.getParentFile().mkdirs();
                                    try (FileWriter fw = new FileWriter(workflowRunAttemptXMLFile)) {
                                        workflowRunAttemptMarshaller.marshal(workflowRunAttempt, fw);
                                    }
                                }
                            }

                        }
                    }

                    List<Sample> sampleList = daoMgr.getMaPSeqDAOBeanService().getSampleDAO()
                            .findByFlowcellId(flowcell.getId());
                    if (CollectionUtils.isNotEmpty(sampleList)) {
                        for (Sample sample : sampleList) {

                            System.out.println(sample.toString());

                            File sampleXMLFile = new File("/tmp/mapseq/samples",
                                    String.format("%s-%d.xml", "Sample", sample.getId()));
                            sampleXMLFile.getParentFile().mkdirs();
                            try (FileWriter fw = new FileWriter(sampleXMLFile)) {
                                sampleMarshaller.marshal(sample, fw);
                            }

                            List<WorkflowRun> sampleWorkflowRunList = daoMgr.getMaPSeqDAOBeanService()
                                    .getWorkflowRunDAO().findBySampleId(sample.getId());

                            if (CollectionUtils.isNotEmpty(sampleWorkflowRunList)) {
                                for (WorkflowRun workflowRun : sampleWorkflowRunList) {

                                    System.out.println(workflowRun.toString());

                                    File moduleClassXMLFile = new File("/tmp/mapseq/samples/workflowruns",
                                            String.format("%s-%d.xml", "WorkflowRun", workflowRun.getId()));
                                    moduleClassXMLFile.getParentFile().mkdirs();
                                    try (FileWriter fw = new FileWriter(moduleClassXMLFile)) {
                                        workflowRunMarshaller.marshal(workflowRun, fw);
                                    }

                                    List<WorkflowRunAttempt> workflowRunAttemptList = daoMgr.getMaPSeqDAOBeanService()
                                            .getWorkflowRunAttemptDAO().findByWorkflowRunId(workflowRun.getId());

                                    if (CollectionUtils.isNotEmpty(workflowRunAttemptList)) {
                                        for (WorkflowRunAttempt workflowRunAttempt : workflowRunAttemptList) {
                                            File workflowRunAttemptXMLFile = new File(
                                                    "/tmp/mapseq/samples/workflowrunattempts",
                                                    String.format("%s-%d.xml", "WorkflowRunAttempt",
                                                            workflowRunAttempt.getId()));
                                            workflowRunAttemptXMLFile.getParentFile().mkdirs();
                                            try (FileWriter fw = new FileWriter(workflowRunAttemptXMLFile)) {
                                                workflowRunAttemptMarshaller.marshal(workflowRunAttempt, fw);
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }

                }
            }
        } catch (MaPSeqDAOException | JAXBException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void ncgenesWorkflowRunsPull() {
        try {
            SOAPDAOManager daoMgr = SOAPDAOManager.getInstance();

            JAXBContext workflowRunContext = JAXBContext.newInstance(WorkflowRun.class);
            Marshaller workflowRunMarshaller = workflowRunContext.createMarshaller();
            workflowRunMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            JAXBContext workflowRunAttemptContext = JAXBContext.newInstance(WorkflowRunAttempt.class);
            Marshaller workflowRunAttemptMarshaller = workflowRunAttemptContext.createMarshaller();
            workflowRunAttemptMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            List<Flowcell> flowcellList = daoMgr.getMaPSeqDAOBeanService().getFlowcellDAO().findByStudyName("NC_GENES");

            if (CollectionUtils.isNotEmpty(flowcellList)) {

                for (Flowcell flowcell : flowcellList) {

                    List<Sample> sampleList = daoMgr.getMaPSeqDAOBeanService().getSampleDAO()
                            .findByFlowcellId(flowcell.getId());

                    if (CollectionUtils.isNotEmpty(sampleList)) {
                        for (Sample sample : sampleList) {

                            List<WorkflowRun> sampleWorkflowRunList = daoMgr.getMaPSeqDAOBeanService()
                                    .getWorkflowRunDAO().findBySampleId(sample.getId());

                            if (CollectionUtils.isNotEmpty(sampleWorkflowRunList)) {
                                for (WorkflowRun workflowRun : sampleWorkflowRunList) {
                                    workflowRun.getSamples().add(sample);
                                    File workflowRunXMLFile = new File("/tmp/mapseq/workflowruns",
                                            String.format("%s-%d.xml", "WorkflowRun", workflowRun.getId()));
                                    workflowRunXMLFile.getParentFile().mkdirs();
                                    try (FileWriter fw = new FileWriter(workflowRunXMLFile)) {
                                        workflowRunMarshaller.marshal(workflowRun, fw);
                                    }

                                    List<WorkflowRunAttempt> workflowRunAttemptList = daoMgr.getMaPSeqDAOBeanService()
                                            .getWorkflowRunAttemptDAO().findByWorkflowRunId(workflowRun.getId());

                                    if (CollectionUtils.isNotEmpty(workflowRunAttemptList)) {
                                        for (WorkflowRunAttempt workflowRunAttempt : workflowRunAttemptList) {
                                            File workflowRunAttemptXMLFile = new File("/tmp/mapseq/workflowrunattempts",
                                                    String.format("%s-%d.xml", "WorkflowRunAttempt",
                                                            workflowRunAttempt.getId()));
                                            workflowRunAttemptXMLFile.getParentFile().mkdirs();
                                            try (FileWriter fw = new FileWriter(workflowRunAttemptXMLFile)) {
                                                workflowRunAttemptMarshaller.marshal(workflowRunAttempt, fw);
                                            }

                                        }
                                    }

                                }
                            }

                        }
                    }

                }

            }
        } catch (MaPSeqDAOException | JAXBException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void ncgenesWorkflowRunAttemptsPull() {
        try {
            SOAPDAOManager daoMgr = SOAPDAOManager.getInstance();

            JAXBContext workflowRunAttemptContext = JAXBContext.newInstance(WorkflowRunAttempt.class);
            Marshaller workflowRunAttemptMarshaller = workflowRunAttemptContext.createMarshaller();
            workflowRunAttemptMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            List<Flowcell> flowcellList = daoMgr.getMaPSeqDAOBeanService().getFlowcellDAO().findByStudyName("NC_GENES");

            if (CollectionUtils.isNotEmpty(flowcellList)) {
                for (Flowcell flowcell : flowcellList) {

                    List<Sample> sampleList = daoMgr.getMaPSeqDAOBeanService().getSampleDAO()
                            .findByFlowcellId(flowcell.getId());

                    if (CollectionUtils.isNotEmpty(sampleList)) {
                        for (Sample sample : sampleList) {

                            List<WorkflowRun> workflowRunList = daoMgr.getMaPSeqDAOBeanService().getWorkflowRunDAO()
                                    .findBySampleId(sample.getId());

                            if (CollectionUtils.isNotEmpty(workflowRunList)) {
                                for (WorkflowRun workflowRun : workflowRunList) {

                                    List<WorkflowRunAttempt> workflowRunAttemptList = daoMgr.getMaPSeqDAOBeanService()
                                            .getWorkflowRunAttemptDAO().findByWorkflowRunId(workflowRun.getId());

                                    if (CollectionUtils.isNotEmpty(workflowRunList)) {
                                        for (WorkflowRunAttempt workflowRunAttempt : workflowRunAttemptList) {
                                            File moduleClassXMLFile = new File("/tmp/mapseq/workflowrunattempts",
                                                    String.format("%s-%d.xml", "WorkflowRunAttempt",
                                                            workflowRunAttempt.getId()));
                                            moduleClassXMLFile.getParentFile().mkdirs();
                                            try (FileWriter fw = new FileWriter(moduleClassXMLFile)) {
                                                workflowRunAttemptMarshaller.marshal(workflowRunAttempt, fw);
                                            }

                                        }
                                    }

                                }
                            }

                        }
                    }

                }

            }
        } catch (MaPSeqDAOException | JAXBException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createRsyncScripts() {
        File script = new File("/tmp", "rsync-ncgenes-flowcells.sh");
        if (script.exists()) {
            script.delete();
        }
        try (FileWriter fw = new FileWriter(script); BufferedWriter bw = new BufferedWriter(fw)) {

            bw.write("#!/bin/bash");
            bw.newLine();

            Set<String> flowcellNameSet = new HashSet<>();

            Files.list(new File("/tmp/flowcells").toPath()).parallel().forEach(a -> {
                try {
                    JAXBContext context = JAXBContext.newInstance(Flowcell.class);
                    Unmarshaller unmarshaller = context.createUnmarshaller();
                    Flowcell flowcell = (Flowcell) unmarshaller.unmarshal(a.toFile());
                    flowcellNameSet.add(flowcell.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            Collections.synchronizedSet(flowcellNameSet);

            List<String> flowcellNameList = new ArrayList<>(flowcellNameSet);
            flowcellNameList.sort((a, b) -> a.compareTo(b));
            flowcellNameList.forEach(a -> {
                try {
                    bw.write(String.format(
                            "rsync -a --rsh='ssh -c arcfour' rc_renci.svc@152.19.198.149:/proj/seq/mapseq/RENCI/%1$s/ /projects/sequence_analysis/medgenwork/NC_GENES/analysis/%1$s/",
                            a));
                    bw.newLine();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
