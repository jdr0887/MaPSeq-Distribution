package edu.unc.mapseq.dao.rs;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Attribute;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.model.FlowcellStatusType;

public class FlowcellTest {

    @Test
    public void testFindAll() {
        RSDAOManager daoMgr = RSDAOManager.getInstance("edu/unc/mapseq/dao/rs/mapseq-dao-beans-test.xml");
        try {
            List<Flowcell> entityList = daoMgr.getMaPSeqDAOBean().getFlowcellDAO().findAll();
            if (entityList != null && entityList.size() > 0) {
                for (Flowcell entity : entityList) {
                    System.out.println(entity.toString());
                    assertTrue(entity.getSamples() == null);
                }
            }
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testFindByCreationDateRange() {
        RSDAOManager daoMgr = RSDAOManager.getInstance("edu/unc/mapseq/dao/rs/mapseq-dao-beans-test.xml");
        try {

            Date parsedStartDate = DateUtils.parseDate("2014-07-01",
                    new String[] { DateFormatUtils.ISO_DATE_FORMAT.getPattern() });
            Date parsedEndDate = DateUtils.parseDate("2014-07-11",
                    new String[] { DateFormatUtils.ISO_DATE_FORMAT.getPattern() });

            List<Flowcell> entityList = daoMgr.getMaPSeqDAOBean().getFlowcellDAO()
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
        RSDAOManager daoMgr = RSDAOManager.getInstance("edu/unc/mapseq/dao/rs/mapseq-dao-beans-test.xml");
        try {
            Flowcell entity = daoMgr.getMaPSeqDAOBean().getFlowcellDAO().findById(108078L);
            System.out.println(entity.toString());
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSave() {
        RSDAOManager daoMgr = RSDAOManager.getInstance("edu/unc/mapseq/dao/rs/mapseq-dao-beans-test.xml");
        FlowcellDAO flowcellDAO = daoMgr.getMaPSeqDAOBean().getFlowcellDAO();
        try {
            Flowcell entity = new Flowcell();
            entity.setBaseDirectory("adsf");
            entity.setName("test");
            entity.setStatus(FlowcellStatusType.COMPLETED);
            Long id = flowcellDAO.save(entity);
            System.out.println(id);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
    }

}
