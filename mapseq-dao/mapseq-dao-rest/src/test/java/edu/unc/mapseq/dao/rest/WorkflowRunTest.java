package edu.unc.mapseq.dao.rest;

import java.util.List;

import org.junit.Test;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.RESTDAOManager;
import edu.unc.mapseq.dao.model.WorkflowRun;

public class WorkflowRunTest {

    @Test
    public void testFindByWorkflowRunId() {
        RESTDAOManager daoMgr = RESTDAOManager.getInstance();
        try {
            List<WorkflowRun> entityList = daoMgr.getMaPSeqDAOBeanService().getWorkflowRunDAO().findByWorkflowId(10L);
            if (entityList != null && entityList.size() > 0) {
                for (WorkflowRun entity : entityList) {
                    System.out.println(entity.toString());
                }
            }
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

    }

}
