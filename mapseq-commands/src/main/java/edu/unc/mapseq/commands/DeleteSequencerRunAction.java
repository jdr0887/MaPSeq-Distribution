package edu.unc.mapseq.commands;

import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.HTSFSampleDAO;
import edu.unc.mapseq.dao.JobDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SequencerRunDAO;
import edu.unc.mapseq.dao.WorkflowPlanDAO;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.HTSFSample;
import edu.unc.mapseq.dao.model.Job;
import edu.unc.mapseq.dao.model.SequencerRun;
import edu.unc.mapseq.dao.model.WorkflowPlan;
import edu.unc.mapseq.dao.model.WorkflowRun;

@Command(scope = "mapseq", name = "delete-sequencer-run", description = "Delete SequencerRun")
public class DeleteSequencerRunAction extends AbstractAction {

    private MaPSeqDAOBean mapseqDAOBean;

    @Argument(index = 0, name = "sequencerRunId", description = "Sequencer Run Identifier", required = true, multiValued = true)
    private List<Long> sequencerRunIdList;

    public DeleteSequencerRunAction() {
        super();
    }

    @Override
    public Object doExecute() {

        if (this.sequencerRunIdList != null && this.sequencerRunIdList.size() > 0) {

            SequencerRunDAO sequencerRunDAO = mapseqDAOBean.getSequencerRunDAO();
            HTSFSampleDAO htsfSampleDAO = mapseqDAOBean.getHTSFSampleDAO();
            WorkflowPlanDAO workflowPlanDAO = mapseqDAOBean.getWorkflowPlanDAO();
            WorkflowRunDAO workflowRunDAO = mapseqDAOBean.getWorkflowRunDAO();
            JobDAO jobDAO = mapseqDAOBean.getJobDAO();

            for (Long sequencerRunId : this.sequencerRunIdList) {
                try {
                    SequencerRun sr = sequencerRunDAO.findById(sequencerRunId);
                    List<WorkflowPlan> workflowPlanList = workflowPlanDAO.findBySequencerRunId(sr.getId());

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

                    List<HTSFSample> sampleList = htsfSampleDAO.findBySequencerRunId(sr.getId());

                    if (sampleList != null && sampleList.size() > 0) {
                        for (HTSFSample entity : sampleList) {
                            htsfSampleDAO.delete(entity);
                        }
                        System.out.printf("%d HTSFSample entities deleted", workflowPlanList.size());
                    }

                    sequencerRunDAO.delete(sr);
                    System.out.println("Deleted SequencerRun: " + sr.getId());

                } catch (MaPSeqDAOException e) {
                    e.printStackTrace();
                }
            }

        }

        return null;
    }

    public MaPSeqDAOBean getMapseqDAOBean() {
        return mapseqDAOBean;
    }

    public void setMapseqDAOBean(MaPSeqDAOBean mapseqDAOBean) {
        this.mapseqDAOBean = mapseqDAOBean;
    }

}
