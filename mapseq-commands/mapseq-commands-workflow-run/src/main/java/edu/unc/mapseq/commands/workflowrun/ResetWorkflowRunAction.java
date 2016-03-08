package edu.unc.mapseq.commands.workflowrun;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowRunAttemptDAO;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;
import edu.unc.mapseq.dao.model.WorkflowRunAttemptStatusType;

@Command(scope = "mapseq", name = "reset-workflow-run", description = "Reset WorkflowRun")
@Service
public class ResetWorkflowRunAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(ResetWorkflowRunAction.class);

    @Reference
    private WorkflowRunDAO workflowRunDAO;

    @Reference
    private WorkflowRunAttemptDAO workflowRunAttemptDAO;

    @Argument(index = 0, name = "workflowRunId", description = "Workflow Run Identifier", required = true, multiValued = true)
    private List<Long> workflowRunIdList;

    public ResetWorkflowRunAction() {
        super();
    }

    @Override
    public Object execute() {
        logger.debug("ENTERING execute()");

        if (this.workflowRunIdList != null && this.workflowRunIdList.size() > 0) {

            for (Long workflowRunId : this.workflowRunIdList) {
                logger.debug("resetting WorkflowRun: {}", workflowRunId);
                try {
                    List<WorkflowRunAttempt> attempts = workflowRunAttemptDAO.findByWorkflowRunId(workflowRunId);

                    ListIterator<WorkflowRunAttempt> iter = new ArrayList<WorkflowRunAttempt>(attempts)
                            .listIterator(attempts.size());
                    if (iter.hasPrevious()) {
                        WorkflowRunAttempt latestAttempt = iter.previous();
                        latestAttempt.setStatus(WorkflowRunAttemptStatusType.RESET);
                        Date date = new Date();
                        if (latestAttempt.getStarted() == null) {
                            latestAttempt.setStarted(date);
                        }
                        latestAttempt.setFinished(date);
                        workflowRunAttemptDAO.save(latestAttempt);
                    }

                    WorkflowRunAttempt attempt = new WorkflowRunAttempt();
                    attempt.setStatus(WorkflowRunAttemptStatusType.PENDING);
                    attempt.setWorkflowRun(workflowRunDAO.findById(workflowRunId));
                    workflowRunAttemptDAO.save(attempt);

                } catch (MaPSeqDAOException e) {
                    e.printStackTrace();
                }
            }

        }

        return null;
    }

    public List<Long> getWorkflowRunIdList() {
        return workflowRunIdList;
    }

    public void setWorkflowRunIdList(List<Long> workflowRunIdList) {
        this.workflowRunIdList = workflowRunIdList;
    }

}
