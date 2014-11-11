package edu.unc.mapseq.dao.rs;

import java.util.List;

import org.junit.Test;

import edu.unc.mapseq.dao.AttributeDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.model.Attribute;
import edu.unc.mapseq.dao.model.Sample;

public class SampleTest {

    @Test
    public void testFindByFlowcellId() {
        RSDAOManager daoMgr = RSDAOManager.getInstance("edu/unc/mapseq/dao/rs/mapseq-dao-beans-test.xml");
        try {
            List<Sample> entityList = daoMgr.getMaPSeqDAOBean().getSampleDAO().findByFlowcellId(108078L);
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
        RSDAOManager daoMgr = RSDAOManager.getInstance("edu/unc/mapseq/dao/rs/mapseq-dao-beans-test.xml");
        try {
            SampleDAO sampleDAO = daoMgr.getMaPSeqDAOBean().getSampleDAO();
            sampleDAO.addFileDataToSample(2L, 2216370L);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testAddAttributeToSample() {
        RSDAOManager daoMgr = RSDAOManager.getInstance("edu/unc/mapseq/dao/rs/mapseq-dao-beans-test.xml");
        try {
            SampleDAO sampleDAO = daoMgr.getMaPSeqDAOBean().getSampleDAO();
            AttributeDAO attributeDAO = daoMgr.getMaPSeqDAOBean().getAttributeDAO();

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
