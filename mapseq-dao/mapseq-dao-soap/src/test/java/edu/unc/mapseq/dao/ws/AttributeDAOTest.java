package edu.unc.mapseq.dao.ws;

import org.junit.Test;

import edu.unc.mapseq.dao.AttributeDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Attribute;

public class AttributeDAOTest {

    @Test
    public void save() {

        WSDAOManager daoMgr = WSDAOManager.getInstance("edu/unc/mapseq/dao/ws/mapseq-dao-beans-test.xml");

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
