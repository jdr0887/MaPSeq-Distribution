package edu.unc.mapseq.commands.core.workflowrun;

import java.text.DateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.WorkflowRunAttemptDAO;
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;

@Command(scope = "mapseq", name = "list-workflow-run-attempts", description = "List WorkflowRunAttempt instances")
@Service
public class ListWorkflowRunAttemptsAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(ListWorkflowRunAttemptsAction.class);

    @Argument(index = 0, name = "workflowRunId", description = "WorkflowRun identifier", required = true, multiValued = false)
    private Long workflowRunId;

    @Reference
    private WorkflowRunAttemptDAO workflowRunAttemptDAO;

    public ListWorkflowRunAttemptsAction() {
        super();
    }

    @Override
    public Object execute() {
        logger.debug("ENTERING execute()");

        try {

            List<WorkflowRunAttempt> attempts = workflowRunAttemptDAO.findByWorkflowRunId(workflowRunId);

            if (CollectionUtils.isEmpty(attempts)) {
                System.out.printf("No WorkflowRunAttempt found");
                return null;
            }

            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter(sb, Locale.US);
            String format = "%1$-12s %2$-20s %3$-20s %4$-20s %5$-16s %6$-16s %7$s%n";
            formatter.format(format, "ID", "Created", "Started", "Finished", "Status", "CondorJobId", "Submit Dir");

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

                formatter.format(format, attempt.getId(), formattedCreatedDate, formattedStartDate, formattedEndDate,
                        attempt.getStatus().toString(), attempt.getCondorDAGClusterId(), attempt.getSubmitDirectory());
                formatter.flush();

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

}
