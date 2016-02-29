package edu.unc.mapseq.dao.rest;

import java.util.List;

import org.junit.Test;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.RESTDAOManager;
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;

public class WorkflowRunAttemptTest {

    @Test
    public void testFindByWorkflowRunId() {
        RESTDAOManager daoMgr = RESTDAOManager.getInstance();
        try {
            List<WorkflowRunAttempt> entityList = daoMgr.getMaPSeqDAOBeanService().getWorkflowRunAttemptDAO()
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
