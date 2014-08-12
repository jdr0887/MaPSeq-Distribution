package edu.unc.mapseq.commands;

import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.JobDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowRunAttemptDAO;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.Job;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;

@Command(scope = "mapseq", name = "delete-workflow-run", description = "Delete WorkflowRun")
public class DeleteWorkflowRunAction extends AbstractAction {

    private MaPSeqDAOBean maPSeqDAOBean;

    @Argument(index = 0, name = "workflowRunId", description = "WorkflowRun Identifier", required = true, multiValued = true)
    private List<Long> workflowRunIdList;

    public DeleteWorkflowRunAction() {
        super();
    }

    @Override
    public Object doExecute() {

        if (this.workflowRunIdList != null && !this.workflowRunIdList.isEmpty()) {

            WorkflowRunDAO workflowRunDAO = maPSeqDAOBean.getWorkflowRunDAO();
            WorkflowRunAttemptDAO workflowRunAttemptDAO = maPSeqDAOBean.getWorkflowRunAttemptDAO();
            JobDAO jobDAO = maPSeqDAOBean.getJobDAO();

            for (Long workflowRunId : this.workflowRunIdList) {
                try {
                    WorkflowRun workflowRun = workflowRunDAO.findById(workflowRunId);

                    for (WorkflowRunAttempt attempt : workflowRun.getAttempts()) {

                        List<Job> jobList = jobDAO.findByWorkflowRunAttemptId(attempt.getId());
                        if (jobList != null && jobList.size() > 0) {
                            jobDAO.delete(jobList);
                            System.out.printf("%d Job entities deleted", jobList.size());
                        }
                        workflowRunAttemptDAO.delete(attempt);
                        System.out.printf("Deleted WorkflowRunAttempt: ", attempt.getId());
                    }
                    workflowRunDAO.delete(workflowRun);
                    System.out.println("Deleted WorkflowRun: " + workflowRun.getId());

                } catch (MaPSeqDAOException e) {
                    e.printStackTrace();
                }
            }

        }

        return null;
    }

    public MaPSeqDAOBean getMaPSeqDAOBean() {
        return maPSeqDAOBean;
    }

    public void setMaPSeqDAOBean(MaPSeqDAOBean maPSeqDAOBean) {
        this.maPSeqDAOBean = maPSeqDAOBean;
    }

    public List<Long> getWorkflowRunIdList() {
        return workflowRunIdList;
    }

    public void setWorkflowRunIdList(List<Long> workflowRunIdList) {
        this.workflowRunIdList = workflowRunIdList;
    }

}
