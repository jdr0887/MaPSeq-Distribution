package edu.unc.mapseq.dao.rs;

import java.util.List;

import org.junit.Test;

import edu.unc.mapseq.dao.MaPSeqDAOException;
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

}
