package edu.unc.mapseq.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.WorkflowDAO;
import edu.unc.mapseq.dao.model.Workflow;

@Command(scope = "mapseq", name = "list-workflows", description = "List Workflows")
public class ListWorkflowsAction extends AbstractAction {

    private MaPSeqDAOBean maPSeqDAOBean;

    public ListWorkflowsAction() {
        super();
    }

    @Override
    public Object doExecute() {

        List<Workflow> workflowList = new ArrayList<Workflow>();
        WorkflowDAO workflowDAO = maPSeqDAOBean.getWorkflowDAO();
        try {
            workflowList.addAll(workflowDAO.findAll());
        } catch (Exception e) {
        }

        Collections.sort(workflowList, new Comparator<Workflow>() {

            @Override
            public int compare(Workflow w1, Workflow w2) {
                return w1.getId().compareTo(w2.getId());
            }

        });

        if (workflowList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter(sb, Locale.US);
            formatter.format("%1$-8s %2$-30s%n", "ID", "Name");
            for (Workflow workflow : workflowList) {
                formatter.format("%1$-8s %2$-30s%n", workflow.getId(), workflow.getName());
                formatter.flush();
            }
            System.out.println(formatter.toString());
            formatter.close();
        }

        return null;
    }

    public MaPSeqDAOBean getMaPSeqDAOBean() {
        return maPSeqDAOBean;
    }

    public void setMaPSeqDAOBean(MaPSeqDAOBean maPSeqDAOBean) {
        this.maPSeqDAOBean = maPSeqDAOBean;
    }

}
