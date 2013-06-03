package edu.unc.mapseq.commands;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.Account;
import edu.unc.mapseq.dao.model.WorkflowRun;

@Command(scope = "mapseq", name = "list-my-workflow-runs", description = "List my WorkflowRun instances")
public class ListMyWorkflowRunsAction extends AbstractAction {

    @Argument(index = 0, name = "workflowId", description = "Workflow identifier", required = false, multiValued = false)
    private Long workflowId;

    private MaPSeqDAOBean mapseqDAOBean;

    public ListMyWorkflowRunsAction() {
        super();
    }

    @Override
    public Object doExecute() {

        Account account = null;
        try {
            account = mapseqDAOBean.getAccountDAO().findByName(System.getProperty("user.name"));
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

        if (account == null) {
            System.err.println("No account found");
            return null;
        }

        List<WorkflowRun> workflowRunList = new ArrayList<WorkflowRun>();
        WorkflowRunDAO workflowRunDAO = mapseqDAOBean.getWorkflowRunDAO();
        try {
            if (workflowId != null) {
                workflowRunList.addAll(workflowRunDAO.findByWorkflowId(workflowId));
            } else {
                workflowRunList.addAll(workflowRunDAO.findByCreator(account.getId()));
            }
        } catch (MaPSeqDAOException e) {
        }
        try {
            if (workflowRunList != null && workflowRunList.size() > 0) {
                StringBuilder sb = new StringBuilder();
                Formatter formatter = new Formatter(sb, Locale.US);
                formatter.format("%1$-8s %2$-25s %3$-54s %4$-18s %5$-18s %6$-18s %7$s%n", "ID", "Workflow Name",
                        "Workflow Run Name", "Created Date", "Start Date", "End Date", "Status");

                Collections.sort(workflowRunList, new Comparator<WorkflowRun>() {
                    @Override
                    public int compare(WorkflowRun wr1, WorkflowRun wr2) {
                        return wr1.getId().compareTo(wr2.getId());
                    }
                });

                for (WorkflowRun workflowRun : workflowRunList) {

                    if (!account.equals(workflowRun.getCreator())) {
                        continue;
                    }

                    Date createdDate = workflowRun.getCreationDate();
                    String formattedCreatedDate = "";
                    if (createdDate != null) {
                        formattedCreatedDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(
                                createdDate);
                    }
                    Date startDate = workflowRun.getStartDate();
                    String formattedStartDate = "";
                    if (startDate != null) {
                        formattedStartDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(
                                startDate);
                    }
                    Date endDate = workflowRun.getEndDate();
                    String formattedEndDate = "";
                    if (endDate != null) {
                        formattedEndDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(
                                endDate);
                    }
                    formatter.format("%1$-8s %2$-25s %3$-54s %4$-18s %5$-18s %6$-18s %7$s%n", workflowRun.getId(),
                            workflowRun.getWorkflow().getName(), workflowRun.getName(), formattedCreatedDate,
                            formattedStartDate, formattedEndDate, workflowRun.getStatus().getState());
                    formatter.flush();

                }
                System.out.println(formatter.toString());
                formatter.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public MaPSeqDAOBean getMapseqDAOBean() {
        return mapseqDAOBean;
    }

    public void setMapseqDAOBean(MaPSeqDAOBean mapseqDAOBean) {
        this.mapseqDAOBean = mapseqDAOBean;
    }

}
