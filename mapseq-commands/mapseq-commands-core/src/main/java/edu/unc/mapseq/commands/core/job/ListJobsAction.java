package edu.unc.mapseq.commands.core.job;

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

import edu.unc.mapseq.dao.JobDAO;
import edu.unc.mapseq.dao.WorkflowRunAttemptDAO;
import edu.unc.mapseq.dao.model.Job;

@Command(scope = "mapseq", name = "list-jobs", description = "List Jobs")
@Service
public class ListJobsAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(ListJobsAction.class);

    @Argument(index = 0, name = "workflowRunAttemptId", description = "WorkflowRunAttempt identifier", required = true, multiValued = false)
    private Long workflowRunAttemptId;

    @Reference
    private WorkflowRunAttemptDAO workflowRunAttemptDAO;

    @Reference
    private JobDAO jobDAO;

    public ListJobsAction() {
        super();
    }

    @Override
    public Object execute() {
        logger.debug("ENTERING execute()");

        try {

            List<Job> jobs = jobDAO.findByWorkflowRunAttemptId(workflowRunAttemptId);

            if (CollectionUtils.isEmpty(jobs)) {
                logger.warn("No Jobs found using workflowRunAttemptId: {}", workflowRunAttemptId);
                return null;
            }

            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter(sb, Locale.US);
            String format = "%1$-12s %2$-20s %3$-20s %4$-20s %5$-16s %6$s%n";
            formatter.format(format, "ID", "Created", "Started", "Finished", "Status", "CommandLine");

            for (Job job : jobs) {

                Date createdDate = job.getCreated();
                String formattedCreatedDate = "";
                if (createdDate != null) {
                    formattedCreatedDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(createdDate);
                }

                Date startDate = job.getStarted();
                String formattedStartDate = "";
                if (startDate != null) {
                    formattedStartDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(startDate);
                }
                Date endDate = job.getFinished();
                String formattedEndDate = "";
                if (endDate != null) {
                    formattedEndDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(endDate);
                }

                formatter.format(format, job.getId(), formattedCreatedDate, formattedStartDate, formattedEndDate,
                        job.getStatus().toString(), job.getCommandLine());
                formatter.flush();

            }

            System.out.print(formatter.toString());
            formatter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public Long getWorkflowRunAttemptId() {
        return workflowRunAttemptId;
    }

    public void setWorkflowRunAttemptId(Long workflowRunAttemptId) {
        this.workflowRunAttemptId = workflowRunAttemptId;
    }

    public JobDAO getJobDAO() {
        return jobDAO;
    }

    public void setJobDAO(JobDAO jobDAO) {
        this.jobDAO = jobDAO;
    }

}
