package edu.unc.mapseq.dao.rs;

import java.util.List;

import org.junit.Test;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Workflow;

public class WorkflowTest {

    @Test
    public void testFindAll() {
        RSDAOManager daoMgr = RSDAOManager.getInstance("edu/unc/mapseq/dao/rs/mapseq-dao-beans-test.xml");
        try {
            List<Workflow> entityList = daoMgr.getMaPSeqDAOBean().getWorkflowDAO().findAll();
            if (entityList != null && entityList.size() > 0) {
                for (Workflow entity : entityList) {
                    System.out.println(entity.toString());
                }
            }
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

    }

}
