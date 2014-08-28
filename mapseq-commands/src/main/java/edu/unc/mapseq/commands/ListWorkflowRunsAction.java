package edu.unc.mapseq.commands;

import java.text.DateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.WorkflowRunAttemptDAO;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;

@Command(scope = "mapseq", name = "list-workflow-runs", description = "List WorkflowRun instances")
public class ListWorkflowRunsAction extends AbstractAction {

    @Argument(index = 0, name = "workflowId", description = "Workflow identifier", required = true, multiValued = false)
    private Long workflowId;

    private MaPSeqDAOBean maPSeqDAOBean;

    public ListWorkflowRunsAction() {
        super();
    }

    @Override
    public Object doExecute() {

        WorkflowRunDAO workflowRunDAO = maPSeqDAOBean.getWorkflowRunDAO();

        WorkflowRunAttemptDAO workflowRunAttemptDAO = maPSeqDAOBean.getWorkflowRunAttemptDAO();

        try {
            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter(sb, Locale.US);
            formatter.format("%1$-12s %2$-54s %3$-18s %4$-18s %5$-18s %6$-12s %7$-14s %8$s%n", "ID",
                    "Workflow Run Name", "Created Date", "Start Date", "End Date", "Status", "Condor JobId",
                    "Submit Directory");

            List<WorkflowRun> workflowRunList = workflowRunDAO.findByWorkflowId(workflowId);

            if (workflowRunList != null && !workflowRunList.isEmpty()) {
                for (WorkflowRun workflowRun : workflowRunList) {

                    Date createdDate = workflowRun.getCreated();
                    String formattedCreatedDate = "";
                    if (createdDate != null) {
                        formattedCreatedDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                                .format(createdDate);
                    }

                    formatter.format("%1$-12s %2$-54s %3$s%n", workflowRun.getId(), workflowRun.getName(),
                            formattedCreatedDate);

                    List<WorkflowRunAttempt> attempts = workflowRunAttemptDAO.findByWorkflowRunId(workflowRun.getId());

                    if (attempts != null && !attempts.isEmpty()) {
                        for (WorkflowRunAttempt attempt : attempts) {

                            createdDate = attempt.getCreated();
                            if (createdDate != null) {
                                formattedCreatedDate = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                                        DateFormat.SHORT).format(createdDate);
                            }

                            Date startDate = attempt.getStarted();
                            String formattedStartDate = "";
                            if (startDate != null) {
                                formattedStartDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                                        .format(startDate);
                            }
                            Date endDate = attempt.getFinished();
                            String formattedEndDate = "";
                            if (endDate != null) {
                                formattedEndDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                                        .format(endDate);
                            }

                            formatter.format("%1$-12s %2$-54s %3$-18s %4$-18s %5$-18s %6$-12s %7$-14s %8$s%n", "--",
                                    "--", formattedCreatedDate, formattedStartDate, formattedEndDate, attempt
                                            .getStatus().toString(), attempt.getCondorDAGClusterId(), attempt
                                            .getSubmitDirectory());
                            formatter.flush();

                        }
                    }

                }

            }

            System.out.print(formatter.toString());
            formatter.close();
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
