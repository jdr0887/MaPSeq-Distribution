package edu.unc.mapseq.dao.rest;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

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

public class FlowcellTest {

    @Test
    public void testFindAll() {
        RESTDAOManager daoMgr = RESTDAOManager.getInstance();
        try {
            List<Flowcell> entityList = daoMgr.getMaPSeqDAOBeanService().getFlowcellDAO().findAll();
            if (entityList != null && entityList.size() > 0) {
                for (Flowcell entity : entityList) {
                    System.out.println(entity.toString());
                    assertTrue(entity.getSamples() == null);
                    Set<Attribute> attributeSet = entity.getAttributes();
                    for (Attribute attribute : attributeSet) {
                        System.out.printf("%s:%s%n", attribute.getName(), attribute.getValue());
                    }
                }
            }
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void findByStudyName() {
        try {
            RESTDAOManager daoMgr = RESTDAOManager.getInstance();
            List<Flowcell> entityList = daoMgr.getMaPSeqDAOBeanService().getFlowcellDAO().findByStudyName("NC_GENES");
            if (CollectionUtils.isNotEmpty(entityList)) {
                for (Flowcell flowcell : entityList) {
                    JAXBContext context = JAXBContext.newInstance(Flowcell.class);
                    Marshaller m = context.createMarshaller();
                    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                    File moduleClassXMLFile = new File("/tmp/flowcells",
                            String.format("%s-%d.xml", "Flowcell", flowcell.getId()));
                    FileWriter fw = new FileWriter(moduleClassXMLFile);
                    m.marshal(flowcell, fw);
                }
            }
        } catch (MaPSeqDAOException | JAXBException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void ncgenesMigrationPull() {
        try {
            RESTDAOManager daoMgr = RESTDAOManager.getInstance();

            JAXBContext workflowContext = JAXBContext.newInstance(Workflow.class);
            Marshaller workflowMarshaller = workflowContext.createMarshaller();
            workflowMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            JAXBContext sampleContext = JAXBContext.newInstance(Sample.class);
            Marshaller sampleMarshaller = sampleContext.createMarshaller();
            sampleMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            JAXBContext workflowRunContext = JAXBContext.newInstance(WorkflowRun.class);
            Marshaller workflowRunMarshaller = workflowRunContext.createMarshaller();
            workflowRunMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

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

                    List<Sample> sampleList = daoMgr.getMaPSeqDAOBeanService().getSampleDAO()
                            .findByFlowcellId(flowcell.getId());

                    if (CollectionUtils.isNotEmpty(sampleList)) {
                        for (Sample sample : sampleList) {

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
                                    File moduleClassXMLFile = new File("/tmp/mapseq/workflowruns",
                                            String.format("%s-%d.xml", "WorkflowRun", workflowRun.getId()));
                                    moduleClassXMLFile.getParentFile().mkdirs();
                                    try (FileWriter fw = new FileWriter(moduleClassXMLFile)) {
                                        workflowRunMarshaller.marshal(workflowRun, fw);
                                    }
                                }
                            }

                        }
                    }

                    // List<WorkflowRun> flowcellWorkflowRunList = daoMgr.getMaPSeqDAOBeanService().getWorkflowRunDAO()
                    // .findByFlowcellId(flowcell.getId());
                    //
                    // if (CollectionUtils.isNotEmpty(flowcellWorkflowRunList)) {
                    //
                    // for (WorkflowRun workflowRun : flowcellWorkflowRunList) {
                    //
                    // File moduleClassXMLFile = new File("/tmp/mapseq/workflowruns",
                    // String.format("%s-%d.xml", "WorkflowRun", workflowRun.getId()));
                    // moduleClassXMLFile.getParentFile().mkdirs();
                    // FileWriter fw = new FileWriter(moduleClassXMLFile);
                    // workflowRunMarshaller.marshal(workflowRun, fw);
                    //
                    // }
                    // }

                }
            }
        } catch (MaPSeqDAOException | JAXBException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFindByCreationDateRange() {
        RESTDAOManager daoMgr = RESTDAOManager.getInstance();
        try {

            Date parsedStartDate = DateUtils.parseDate("2014-07-01",
                    new String[] { DateFormatUtils.ISO_DATE_FORMAT.getPattern() });
            Date parsedEndDate = DateUtils.parseDate("2014-07-11",
                    new String[] { DateFormatUtils.ISO_DATE_FORMAT.getPattern() });

            List<Flowcell> entityList = daoMgr.getMaPSeqDAOBeanService().getFlowcellDAO()
                    .findByCreatedDateRange(parsedStartDate, parsedEndDate);
            if (entityList != null && entityList.size() > 0) {
                for (Flowcell entity : entityList) {
                    System.out.println(entity.toString());
                    Set<Attribute> attributeSet = entity.getAttributes();
                    for (Attribute attribute : attributeSet) {
                        System.out.printf("%s:%s%n", attribute.getName(), attribute.getValue());
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
    public void testFindById() {
        RESTDAOManager daoMgr = RESTDAOManager.getInstance();
        try {
            Flowcell entity = daoMgr.getMaPSeqDAOBeanService().getFlowcellDAO().findById(108078L);
            System.out.println(entity.toString());
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSave() {
        RESTDAOManager daoMgr = RESTDAOManager.getInstance();
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

}
