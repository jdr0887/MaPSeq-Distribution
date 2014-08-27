package edu.unc.mapseq.commands;

import java.util.List;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
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

            for (Long flowcellId : this.flowcellIdList) {
                try {
                    Flowcell flowcell = flowcellDAO.findById(flowcellId);
                    List<WorkflowRun> workflowRunList = workflowRunDAO.findByFlowcellId(flowcell.getId());

                    if (workflowRunList != null && !workflowRunList.isEmpty()) {

                        for (WorkflowRun workflowRun : workflowRunList) {

                            List<WorkflowRunAttempt> attempts = workflowRunAttemptDAO.findByWorkflowRunId(workflowRun
                                    .getId());

                            if (attempts != null && !attempts.isEmpty()) {

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

                        }
                        workflowRunDAO.delete(workflowRunList);
                        System.out.printf("%d WorkflowRuns deleted%n", workflowRunList.size());

                    }

                    List<Sample> sampleList = sampleDAO.findByFlowcellId(flowcell.getId());

                    if (sampleList != null && sampleList.size() > 0) {
                        for (Sample sample : sampleList) {
                            sample.setAttributes(null);
                            sample.setFileDatas(null);
                            sampleDAO.save(sample);
                        }
                        sampleDAO.delete(sampleList);
                        System.out.printf("%d Samples deleted%n", sampleList.size());
                    }

                    flowcell.setAttributes(null);
                    flowcell.setFileDatas(null);
                    flowcellDAO.save(flowcell);

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
