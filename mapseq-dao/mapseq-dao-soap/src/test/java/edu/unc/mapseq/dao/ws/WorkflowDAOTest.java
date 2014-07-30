package edu.unc.mapseq.dao.ws;

import java.util.List;

import org.junit.Test;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowDAO;
import edu.unc.mapseq.dao.model.Workflow;

public class WorkflowDAOTest {

    @Test
    public void testSave() {

        Workflow workflow = new Workflow();
        workflow.setName("test");

        WSDAOManager wsDAOMgr = WSDAOManager.getInstance("edu/unc/mapseq/dao/ws/mapseq-dao-beans-test.xml");
        // WSDAOManager wsDAOMgr = WSDAOManager.getInstance();
        WorkflowDAO workflowDAO = wsDAOMgr.getMaPSeqDAOBean().getWorkflowDAO();
        try {
            Long id = workflowDAO.save(workflow);
            System.out.println(id);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testFindAll() {

        WSDAOManager wsDAOMgr = WSDAOManager.getInstance("edu/unc/mapseq/dao/ws/mapseq-dao-beans-test.xml");
        WorkflowDAO workflowDAO = wsDAOMgr.getMaPSeqDAOBean().getWorkflowDAO();
        try {
            List<Workflow> workflowList = workflowDAO.findAll();
            for (Workflow workflow : workflowList) {
                System.out.println(workflow.toString());
            }
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

    }

}
