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
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;

@Command(scope = "mapseq", name = "list-workflow-run-attempts", description = "List WorkflowRunAttempt instances")
public class ListWorkflowRunAttemptsAction extends AbstractAction {

    @Argument(index = 0, name = "workflowRunId", description = "WorkflowRun identifier", required = true, multiValued = false)
    private Long workflowRunId;

    private MaPSeqDAOBean maPSeqDAOBean;

    public ListWorkflowRunAttemptsAction() {
        super();
    }

    @Override
    public Object doExecute() {

        WorkflowRunAttemptDAO workflowRunAttemptDAO = maPSeqDAOBean.getWorkflowRunAttemptDAO();

        try {
            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter(sb, Locale.US);
            formatter.format("%1$-12s %2$-20s %3$-20s %4$-20s %5$-16s %6$-16s %7$s%n", "ID", "Created Date",
                    "Start Date", "End Date", "Status", "CondorJobId", "Submit Dir");

            List<WorkflowRunAttempt> attempts = workflowRunAttemptDAO.findByWorkflowRunId(workflowRunId);

            String formattedCreatedDate = "";
            String formattedStartDate = "";
            String formattedEndDate = "";

            if (attempts != null && !attempts.isEmpty()) {
                for (WorkflowRunAttempt attempt : attempts) {

                    Date createdDate = attempt.getCreated();
                    if (createdDate != null) {
                        formattedCreatedDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                                .format(createdDate);
                    }

                    Date startDate = attempt.getStarted();
                    if (startDate != null) {
                        formattedStartDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(
                                startDate);
                    }
                    Date endDate = attempt.getFinished();
                    if (endDate != null) {
                        formattedEndDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(
                                endDate);
                    }

                    formatter.format("%1$-12s %2$-20s %3$-20s %4$-20s %5$-16s %6$-16s %7$s%n", attempt.getId(),
                            formattedCreatedDate, formattedStartDate, formattedEndDate, attempt.getStatus().toString(),
                            attempt.getCondorDAGClusterId(), attempt.getSubmitDirectory());
                    formatter.flush();

                }

            }

            System.out.print(formatter.toString());
            formatter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public Long getWorkflowRunId() {
        return workflowRunId;
    }

    public void setWorkflowRunId(Long workflowRunId) {
        this.workflowRunId = workflowRunId;
    }

    public MaPSeqDAOBean getMaPSeqDAOBean() {
        return maPSeqDAOBean;
    }

    public void setMaPSeqDAOBean(MaPSeqDAOBean maPSeqDAOBean) {
        this.maPSeqDAOBean = maPSeqDAOBean;
    }

}
