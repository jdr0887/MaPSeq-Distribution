package edu.unc.mapseq.dao.rs;

import java.util.List;

import org.junit.Test;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;

public class WorkflowRunAttemptTest {

    @Test
    public void testFindByWorkflowRunId() {
        RSDAOManager daoMgr = RSDAOManager.getInstance("edu/unc/mapseq/dao/rs/mapseq-dao-beans-test.xml");
        try {
            List<WorkflowRunAttempt> entityList = daoMgr.getMaPSeqDAOBean().getWorkflowRunAttemptDAO()
                    .findByWorkflowId(10L);
            if (entityList != null && entityList.size() > 0) {
                for (WorkflowRunAttempt entity : entityList) {
                    System.out.println(entity.toString());
                }
            }
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

    }

}
