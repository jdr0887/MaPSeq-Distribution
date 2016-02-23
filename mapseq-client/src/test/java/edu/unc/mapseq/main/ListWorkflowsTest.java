package edu.unc.mapseq.main;

import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Workflow;
import edu.unc.mapseq.dao.ws.WSDAOManager;

public class ListWorkflowsTest {

    @Test
    public void testRun() {

        try {
            WSDAOManager daoMgr = WSDAOManager.getInstance();
            List<Workflow> workflowList = daoMgr.getMaPSeqDAOBeanService().getWorkflowDAO().findAll();

            if (!workflowList.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                Formatter formatter = new Formatter(sb, Locale.US);
                formatter.format("%1$-5s %2$-20s%n", "ID", "Instrument");
                for (Workflow workflow : workflowList) {
                    formatter.format("%1$-5s %2$-20s%n", workflow.getId(), workflow.getName());
                }
                System.out.println(formatter.toString());
                formatter.close();
            }
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

    }

}
