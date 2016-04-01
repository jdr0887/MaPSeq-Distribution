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
import javax.xml.bind.PropertyException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Attribute;
import edu.unc.mapseq.dao.model.Flowcell;

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
