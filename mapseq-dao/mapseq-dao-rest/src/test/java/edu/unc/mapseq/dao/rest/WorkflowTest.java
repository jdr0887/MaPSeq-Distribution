package edu.unc.mapseq.dao.rest;

import java.util.List;

import org.junit.Test;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Workflow;

public class WorkflowTest {

    @Test
    public void testFindAll() {
        RESTDAOManager daoMgr = RESTDAOManager.getInstance();
        try {
            List<Workflow> entityList = daoMgr.getMaPSeqDAOBeanService().getWorkflowDAO().findAll();
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
