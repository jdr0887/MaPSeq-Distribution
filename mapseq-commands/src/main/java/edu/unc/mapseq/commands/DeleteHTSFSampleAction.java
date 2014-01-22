package edu.unc.mapseq.commands;

import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.HTSFSampleDAO;
import edu.unc.mapseq.dao.JobDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowPlanDAO;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.HTSFSample;
import edu.unc.mapseq.dao.model.Job;
import edu.unc.mapseq.dao.model.WorkflowPlan;
import edu.unc.mapseq.dao.model.WorkflowRun;

@Command(scope = "mapseq", name = "delete-htsf-sample", description = "Delete HTSFSample")
public class DeleteHTSFSampleAction extends AbstractAction {

    private MaPSeqDAOBean maPSeqDAOBean;

    @Argument(index = 0, name = "htsfSampleId", description = "HTSFSample Identifier", required = true, multiValued = true)
    private List<Long> htsfSampleIdList;

    public DeleteHTSFSampleAction() {
        super();
    }

    @Override
    public Object doExecute() {

        if (this.htsfSampleIdList != null && this.htsfSampleIdList.size() > 0) {

            HTSFSampleDAO htsfSampleDAO = maPSeqDAOBean.getHTSFSampleDAO();
            WorkflowPlanDAO workflowPlanDAO = maPSeqDAOBean.getWorkflowPlanDAO();
            WorkflowRunDAO workflowRunDAO = maPSeqDAOBean.getWorkflowRunDAO();
            JobDAO jobDAO = maPSeqDAOBean.getJobDAO();

            for (Long id : this.htsfSampleIdList) {
                try {
                    HTSFSample sample = htsfSampleDAO.findById(id);
                    List<WorkflowPlan> workflowPlanList = workflowPlanDAO.findByHTSFSampleId(sample.getId());

                    if (workflowPlanList != null && workflowPlanList.size() > 0) {

                        for (WorkflowPlan workflowPlan : workflowPlanList) {

                            WorkflowRun workflowRun = workflowPlan.getWorkflowRun();
                            List<Job> jobList = jobDAO.findByWorkflowRunId(workflowRun.getId());
                            if (jobList != null && jobList.size() > 0) {
                                jobDAO.delete(jobList);
                                System.out.printf("%d Job entities deleted", jobList.size());
                            }
                            workflowRunDAO.delete(workflowRun);

                            workflowPlanDAO.delete(workflowPlan);
                            System.out.println("Deleted WorkflowPlan: " + workflowPlan.getId());
                        }

                    }

                    htsfSampleDAO.delete(sample);
                    System.out.printf("Deleted HTSFSample: %s", id);

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

    public List<Long> getHtsfSampleIdList() {
        return htsfSampleIdList;
    }

    public void setHtsfSampleIdList(List<Long> htsfSampleIdList) {
        this.htsfSampleIdList = htsfSampleIdList;
    }

}
