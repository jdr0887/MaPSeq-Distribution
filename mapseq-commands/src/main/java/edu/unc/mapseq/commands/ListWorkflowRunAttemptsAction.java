package edu.unc.mapseq.commands;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowRunAttemptDAO;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;

@Command(scope = "mapseq", name = "list-workflow-run-attempts", description = "List WorkflowRunAttempt instances")
public class ListWorkflowRunAttemptsAction extends AbstractAction {

    @Argument(index = 0, name = "workflowId", description = "Workflow identifier", required = true, multiValued = false)
    private Long workflowId;

    private MaPSeqDAOBean maPSeqDAOBean;

    public ListWorkflowRunAttemptsAction() {
        super();
    }

    @Override
    public Object doExecute() {

        List<WorkflowRunAttempt> attempts = new ArrayList<WorkflowRunAttempt>();
        WorkflowRunAttemptDAO workflowRunAttemptDAO = maPSeqDAOBean.getWorkflowRunAttemptDAO();
        try {
            attempts.addAll(workflowRunAttemptDAO.findByWorkflowId(workflowId));
        } catch (MaPSeqDAOException e) {
        }
        try {
            if (attempts != null && !attempts.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                Formatter formatter = new Formatter(sb, Locale.US);
                formatter.format("%1$-8s %2$-25s %3$-54s %4$-18s %5$-18s %6$-18s %7$-18s %8$-18s %9$s%n", "ID",
                        "Workflow Name", "Workflow Run Name", "Created Date", "Start Date", "End Date", "Status",
                        "Submit Directory", "Condor DAG ClusterId");

                for (WorkflowRunAttempt attempt : attempts) {

                    WorkflowRun workflowRun = attempt.getWorkflowRun();
                    Date createdDate = workflowRun.getCreated();
                    String formattedCreatedDate = "";
                    if (createdDate != null) {
                        formattedCreatedDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                                .format(createdDate);
                    }

                    Date startDate = attempt.getStarted();
                    String formattedStartDate = "";
                    if (startDate != null) {
                        formattedStartDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(
                                startDate);
                    }
                    Date endDate = attempt.getFinished();
                    String formattedEndDate = "";
                    if (endDate != null) {
                        formattedEndDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(
                                endDate);
                    }
                    formatter.format("%1$-8s %2$-25s %3$-54s %4$-18s %5$-18s %6$-18s %7$-18s %8$-18s %9$s%n",
                            workflowRun.getId(), workflowRun.getWorkflow().getName(), workflowRun.getName(),
                            formattedCreatedDate, formattedStartDate, formattedEndDate, attempt.getStatus().getState(),
                            attempt.getSubmitDirectory(), attempt.getCondorDAGClusterId());
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

    public Long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

    public MaPSeqDAOBean getMaPSeqDAOBean() {
        return maPSeqDAOBean;
    }

    public void setMaPSeqDAOBean(MaPSeqDAOBean maPSeqDAOBean) {
        this.maPSeqDAOBean = maPSeqDAOBean;
    }

}
