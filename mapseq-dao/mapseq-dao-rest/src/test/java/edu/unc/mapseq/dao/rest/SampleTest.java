package edu.unc.mapseq.dao.rest;

import java.util.List;

import org.junit.Test;

import edu.unc.mapseq.dao.AttributeDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.RESTDAOManager;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.model.Attribute;
import edu.unc.mapseq.dao.model.Sample;

public class SampleTest {

    @Test
    public void testFindByFlowcellId() {
        RESTDAOManager daoMgr = RESTDAOManager.getInstance();
        try {
            List<Sample> entityList = daoMgr.getMaPSeqDAOBeanService().getSampleDAO().findByFlowcellId(108078L);
            if (entityList != null && entityList.size() > 0) {
                for (Sample entity : entityList) {
                    System.out.println(entity.toString());
                }
            }
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAddFileDataToSample() {
        RESTDAOManager daoMgr = RESTDAOManager.getInstance();
        try {
            SampleDAO sampleDAO = daoMgr.getMaPSeqDAOBeanService().getSampleDAO();
            sampleDAO.addFileData(2L, 2216370L);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testAddAttributeToSample() {
        RESTDAOManager daoMgr = RESTDAOManager.getInstance();
        try {
            SampleDAO sampleDAO = daoMgr.getMaPSeqDAOBeanService().getSampleDAO();
            AttributeDAO attributeDAO = daoMgr.getMaPSeqDAOBeanService().getAttributeDAO();

            Sample sample = sampleDAO.findById(2041809L);
            Attribute attribute = new Attribute("fuzz", "buzz");
            attribute.setId(attributeDAO.save(attribute));
            sample.getAttributes().add(attribute);
            sampleDAO.save(sample);

        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
    }

}
