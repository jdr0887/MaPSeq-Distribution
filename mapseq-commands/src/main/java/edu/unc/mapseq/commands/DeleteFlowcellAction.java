package edu.unc.mapseq.commands;

import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.JobDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.WorkflowRunAttemptDAO;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.model.Job;
import edu.unc.mapseq.dao.model.Sample;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;

@Command(scope = "mapseq", name = "delete-flowcell", description = "Delete Flowcell")
public class DeleteFlowcellAction extends AbstractAction {

    private MaPSeqDAOBean maPSeqDAOBean;

    @Argument(index = 0, name = "flowcellId", description = "Flowcell Identifier", required = true, multiValued = true)
    private List<Long> flowcellIdList;

    public DeleteFlowcellAction() {
        super();
    }

    @Override
    public Object doExecute() {

        if (this.flowcellIdList != null && !this.flowcellIdList.isEmpty()) {

            FlowcellDAO flowcellDAO = maPSeqDAOBean.getFlowcellDAO();
            SampleDAO sampleDAO = maPSeqDAOBean.getSampleDAO();
            WorkflowRunDAO workflowRunDAO = maPSeqDAOBean.getWorkflowRunDAO();
            WorkflowRunAttemptDAO workflowRunAttemptDAO = maPSeqDAOBean.getWorkflowRunAttemptDAO();
            JobDAO jobDAO = maPSeqDAOBean.getJobDAO();

            for (Long sequencerRunId : this.flowcellIdList) {
                try {
                    Flowcell flowcell = flowcellDAO.findById(sequencerRunId);
                    List<WorkflowRun> workflowRunList = workflowRunDAO.findByFlowcellId(flowcell.getId());

                    if (workflowRunList != null && !workflowRunList.isEmpty()) {

                        for (WorkflowRun workflowRun : workflowRunList) {

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

                        }

                    }

                    List<Sample> sampleList = sampleDAO.findByFlowcellId(flowcell.getId());

                    if (sampleList != null && sampleList.size() > 0) {
                        for (Sample entity : sampleList) {
                            sampleDAO.delete(entity);
                        }
                        System.out.printf("%d Sample entities deleted%n", sampleList.size());
                    }

                    flowcellDAO.delete(flowcell);
                    System.out.printf("Deleted Flowcell: %s%n", flowcell.getId());

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

    public List<Long> getFlowcellIdList() {
        return flowcellIdList;
    }

    public void setFlowcellIdList(List<Long> flowcellIdList) {
        this.flowcellIdList = flowcellIdList;
    }

}
