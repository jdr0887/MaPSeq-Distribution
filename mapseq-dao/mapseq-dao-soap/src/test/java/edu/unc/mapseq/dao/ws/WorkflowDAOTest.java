package edu.unc.mapseq.dao.ws;

import java.util.List;

import org.junit.Test;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SOAPDAOManager;
import edu.unc.mapseq.dao.WorkflowDAO;
import edu.unc.mapseq.dao.model.Workflow;

public class WorkflowDAOTest {

    @Test
    public void testSave() {

        Workflow workflow = new Workflow();
        workflow.setName("test");

        SOAPDAOManager wsDAOMgr = SOAPDAOManager.getInstance();
        // WSDAOManager wsDAOMgr = WSDAOManager.getInstance();
        WorkflowDAO workflowDAO = wsDAOMgr.getMaPSeqDAOBeanService().getWorkflowDAO();
        try {
            Long id = workflowDAO.save(workflow);
            System.out.println(id);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testFindAll() {

        SOAPDAOManager wsDAOMgr = SOAPDAOManager.getInstance();
        WorkflowDAO workflowDAO = wsDAOMgr.getMaPSeqDAOBeanService().getWorkflowDAO();
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
