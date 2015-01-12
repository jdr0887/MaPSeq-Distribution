package edu.unc.mapseq.commands.workflow;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.WorkflowDAO;
import edu.unc.mapseq.dao.model.Workflow;

@Command(scope = "mapseq", name = "list-workflows", description = "List Workflows")
public class ListWorkflowsAction extends AbstractAction {

    private final Logger logger = LoggerFactory.getLogger(ListWorkflowsAction.class);

    private MaPSeqDAOBean maPSeqDAOBean;

    public ListWorkflowsAction() {
        super();
    }

    @Override
    public Object doExecute() {
        logger.debug("ENTERING doExecute()");

        List<Workflow> workflowList = new ArrayList<Workflow>();
        WorkflowDAO workflowDAO = maPSeqDAOBean.getWorkflowDAO();
        try {
            workflowList.addAll(workflowDAO.findAll());
        } catch (Exception e) {
        }

        if (workflowList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            String format = "%1$-12s %2$-20s %3$-30s%n";
            Formatter formatter = new Formatter(sb, Locale.US);
            formatter.format(format, "ID", "Created", "Name");
            for (Workflow workflow : workflowList) {

                Date createdDate = workflow.getCreated();
                String formattedCreatedDate = "";
                if (createdDate != null) {
                    formattedCreatedDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(
                            createdDate);
                }

                formatter.format(format, workflow.getId(), formattedCreatedDate, workflow.getName());
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
