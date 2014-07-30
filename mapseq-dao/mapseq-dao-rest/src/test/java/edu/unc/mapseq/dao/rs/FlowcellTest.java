package edu.unc.mapseq.dao.rs;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import edu.unc.mapseq.dao.AccountDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.PlatformDAO;
import edu.unc.mapseq.dao.SequencerRunDAO;
import edu.unc.mapseq.dao.model.EntityAttribute;
import edu.unc.mapseq.dao.model.SequencerRun;
import edu.unc.mapseq.dao.model.SequencerRunStatusType;

public class SequencerRunTest {

    @Test
    public void testFindAll() {
        RSDAOManager daoMgr = RSDAOManager.getInstance("edu/unc/mapseq/dao/rs/mapseq-dao-beans-test.xml");
        try {
            List<SequencerRun> entityList = daoMgr.getMaPSeqDAOBean().getSequencerRunDAO().findAll();
            if (entityList != null && entityList.size() > 0) {
                for (SequencerRun entity : entityList) {
                    System.out.println(entity.toString());
                    assertTrue(entity.getHTSFSamples() == null);
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

            List<SequencerRun> entityList = daoMgr.getMaPSeqDAOBean().getSequencerRunDAO()
                    .findByCreationDateRange(parsedStartDate, parsedEndDate);
            if (entityList != null && entityList.size() > 0) {
                for (SequencerRun entity : entityList) {
                    System.out.println(entity.toString());
                    Set<EntityAttribute> attributeSet = entity.getAttributes();
                    for (EntityAttribute attribute : attributeSet) {
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
            SequencerRun entity = daoMgr.getMaPSeqDAOBean().getSequencerRunDAO().findById(108078L);
            System.out.println(entity.toString());
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSave() {
        RSDAOManager daoMgr = RSDAOManager.getInstance("edu/unc/mapseq/dao/rs/mapseq-dao-beans-test.xml");
        SequencerRunDAO sequencerRunDAO = daoMgr.getMaPSeqDAOBean().getSequencerRunDAO();
        AccountDAO accountDAO = daoMgr.getMaPSeqDAOBean().getAccountDAO();
        PlatformDAO platformDAO = daoMgr.getMaPSeqDAOBean().getPlatformDAO();
        try {
            SequencerRun entity = new SequencerRun();
            entity.setBaseDirectory("adsf");
            entity.setCreator(accountDAO.findByName("jreilly").get(0));
            entity.setName("test");
            entity.setDescription("test");
            entity.setPlatform(platformDAO.findById(66L));
            entity.setStatus(SequencerRunStatusType.COMPLETED);
            Long id = sequencerRunDAO.save(entity);
            System.out.println(id);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
    }

}
