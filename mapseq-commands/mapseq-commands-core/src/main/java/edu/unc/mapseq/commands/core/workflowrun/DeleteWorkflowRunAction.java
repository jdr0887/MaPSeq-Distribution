package edu.unc.mapseq.commands.core.workflowrun;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.JobDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBeanService;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowRunAttemptDAO;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.Job;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;

@Command(scope = "mapseq", name = "delete-workflow-run", description = "Delete WorkflowRun")
@Service
public class DeleteWorkflowRunAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(DeleteWorkflowRunAction.class);

    @Reference
    private MaPSeqDAOBeanService maPSeqDAOBeanService;

    @Argument(index = 0, name = "workflowRunId", description = "WorkflowRun Identifier", required = true, multiValued = true)
    private List<Long> workflowRunIdList;

    public DeleteWorkflowRunAction() {
        super();
    }

    @Override
    public Object execute() {
        logger.debug("ENTERING execute()");

        if (CollectionUtils.isEmpty(workflowRunIdList)) {
            System.out.printf("Please specify at least one WorkflowRun.id value");
            return null;
        }

        WorkflowRunDAO workflowRunDAO = maPSeqDAOBeanService.getWorkflowRunDAO();
        WorkflowRunAttemptDAO workflowRunAttemptDAO = maPSeqDAOBeanService.getWorkflowRunAttemptDAO();
        JobDAO jobDAO = maPSeqDAOBeanService.getJobDAO();

        for (Long workflowRunId : workflowRunIdList) {
            try {
                WorkflowRun workflowRun = workflowRunDAO.findById(workflowRunId);

                List<WorkflowRunAttempt> attempts = workflowRunAttemptDAO.findByWorkflowRunId(workflowRun.getId());

                if (CollectionUtils.isNotEmpty(attempts)) {

                    for (WorkflowRunAttempt attempt : attempts) {

                        List<Job> jobList = jobDAO.findByWorkflowRunAttemptId(attempt.getId());
                        if (jobList != null && !jobList.isEmpty()) {
                            for (Job job : jobList) {
                                job.setAttributes(null);
                                job.setFileDatas(null);
                                jobDAO.save(job);
                            }
                            jobDAO.delete(jobList);
                            System.out.printf("%d Jobs deleted%n", jobList.size());
                        }
                    }
                    workflowRunAttemptDAO.delete(attempts);
                    System.out.printf("%d WorkflowRunAttempts deleted%n", attempts.size());
                }

                workflowRun.setAttributes(null);
                workflowRun.setFileDatas(null);
                workflowRunDAO.save(workflowRun);

                workflowRunDAO.delete(workflowRun);
                System.out.println("WorkflowRun deleted");

            } catch (MaPSeqDAOException e) {
                e.printStackTrace();
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
