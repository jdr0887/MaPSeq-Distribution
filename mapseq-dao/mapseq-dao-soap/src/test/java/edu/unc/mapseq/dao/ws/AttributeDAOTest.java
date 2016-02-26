package edu.unc.mapseq.dao.ws;

import org.junit.Test;

import edu.unc.mapseq.dao.AttributeDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SOAPDAOManager;
import edu.unc.mapseq.dao.model.Attribute;

public class AttributeDAOTest {

    @Test
    public void save() {

        SOAPDAOManager daoMgr = SOAPDAOManager.getInstance();

        final AttributeDAO attributeDAO = daoMgr.getMaPSeqDAOBeanService().getAttributeDAO();

        Attribute attribute = new Attribute("asdf", "asdf");
        try {
            Long id = attributeDAO.save(attribute);
            System.out.println(id);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
    }

}
