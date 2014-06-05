package edu.unc.mapseq.dao.rs;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import edu.unc.mapseq.dao.AccountDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.PlatformDAO;
import edu.unc.mapseq.dao.SequencerRunDAO;
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
            entity.setCreator(accountDAO.findByName("jreilly"));
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
