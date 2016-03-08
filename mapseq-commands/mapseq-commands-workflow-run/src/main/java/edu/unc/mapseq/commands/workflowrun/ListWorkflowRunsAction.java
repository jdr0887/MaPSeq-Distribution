package edu.unc.mapseq.commands.workflowrun;

import java.text.DateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.WorkflowRunAttemptDAO;
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;

@Command(scope = "mapseq", name = "list-workflow-runs", description = "List WorkflowRun instances")
@Service
public class ListWorkflowRunsAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(ListWorkflowRunsAction.class);

    @Argument(index = 0, name = "workflowId", description = "Workflow identifier", required = true, multiValued = false)
    private Long workflowId;

    @Reference
    private WorkflowRunAttemptDAO workflowRunAttemptDAO;

    public ListWorkflowRunsAction() {
        super();
    }

    @Override
    public Object execute() {
        logger.debug("ENTERING execute()");

        try {
            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter(sb, Locale.US);
            String format = "%1$-12s %2$-16s %3$-56s %4$-20s %5$-20s %6$-20s %7$s%n";
            formatter.format(format, "ID", "WorkflowRun ID", "WorkflowRun Name", "Created", "Started", "Finished",
                    "Status");

            List<WorkflowRunAttempt> attempts = workflowRunAttemptDAO.findByWorkflowId(workflowId);

            if (attempts != null && !attempts.isEmpty()) {
                for (WorkflowRunAttempt attempt : attempts) {

                    Date createdDate = attempt.getCreated();
                    String formattedCreatedDate = "";
                    if (createdDate != null) {
                        formattedCreatedDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                                .format(createdDate);
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

                    formatter.format(format, attempt.getId(), attempt.getWorkflowRun().getId(),
                            attempt.getWorkflowRun().getName(), formattedCreatedDate, formattedStartDate,
                            formattedEndDate, attempt.getStatus().toString());
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

    public Long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

}
