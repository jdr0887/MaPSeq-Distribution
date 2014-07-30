package edu.unc.mapseq.dao.rs;

import java.util.List;

import org.junit.Test;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.HTSFSample;

public class HTSFSampleTest {

    @Test
    public void testFindBySequencerRunId() {
        RSDAOManager daoMgr = RSDAOManager.getInstance("edu/unc/mapseq/dao/rs/mapseq-dao-beans-test.xml");
        try {
            List<HTSFSample> entityList = daoMgr.getMaPSeqDAOBean().getHTSFSampleDAO().findBySequencerRunId(108078L);
            if (entityList != null && entityList.size() > 0) {
                for (HTSFSample entity : entityList) {
                    System.out.println(entity.toString());
                }
            }
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

    }

}
