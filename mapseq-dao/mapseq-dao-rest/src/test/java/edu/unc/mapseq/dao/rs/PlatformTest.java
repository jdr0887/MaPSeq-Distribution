package edu.unc.mapseq.dao.rs;

import java.util.List;

import org.junit.Test;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Platform;

public class PlatformTest {

    @Test
    public void testFindAll() {
        RSDAOManager daoMgr = RSDAOManager.getInstance("edu/unc/mapseq/dao/rs/mapseq-dao-beans-test.xml");
        try {
            List<Platform> platformList = daoMgr.getMaPSeqDAOBean().getPlatformDAO().findAll();
            if (platformList != null && platformList.size() > 0) {
                for (Platform p : platformList) {
                    System.out.println(p.getName());
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
            Platform platform = daoMgr.getMaPSeqDAOBean().getPlatformDAO().findById(45L);
            System.out.println(platform.getName());
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

    }

}
