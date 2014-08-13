package edu.unc.mapseq.commands;

import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.JobDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.WorkflowRunAttemptDAO;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.Job;
import edu.unc.mapseq.dao.model.Sample;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;

@Command(scope = "mapseq", name = "delete-sample", description = "Delete Sample")
public class DeleteSampleAction extends AbstractAction {

    private MaPSeqDAOBean maPSeqDAOBean;

    @Argument(index = 0, name = "sampleId", description = "Sample Identifier", required = true, multiValued = true)
    private List<Long> sampleIdList;

    public DeleteSampleAction() {
        super();
    }

    @Override
    public Object doExecute() {

        if (this.sampleIdList != null && this.sampleIdList.size() > 0) {

            SampleDAO sampleDAO = maPSeqDAOBean.getSampleDAO();
            WorkflowRunAttemptDAO workflowRunAttemptDAO = maPSeqDAOBean.getWorkflowRunAttemptDAO();
            WorkflowRunDAO workflowRunDAO = maPSeqDAOBean.getWorkflowRunDAO();
            JobDAO jobDAO = maPSeqDAOBean.getJobDAO();

            for (Long id : this.sampleIdList) {
                try {
                    Sample sample = sampleDAO.findById(id);
                    List<WorkflowRun> workflowRunList = workflowRunDAO.findBySampleId(sample.getId());

                    if (workflowRunList != null && !workflowRunList.isEmpty()) {

                        for (WorkflowRun workflowRun : workflowRunList) {

                            List<WorkflowRunAttempt> attempts = workflowRunAttemptDAO.findByWorkflowRunId(workflowRun
                                    .getId());

                            if (attempts != null && !attempts.isEmpty()) {
                                for (WorkflowRunAttempt attempt : workflowRun.getAttempts()) {
                                    List<Job> jobList = jobDAO.findByWorkflowRunAttemptId(attempt.getId());
                                    if (jobList != null && jobList.size() > 0) {
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

                    sample.setAttributes(null);
                    sample.setFileDatas(null);
                    sampleDAO.save(sample);

                    sampleDAO.delete(sample);
                    System.out.printf("Deleted Sample: %s", id);

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
        return sampleIdList;
    }

    public void setHtsfSampleIdList(List<Long> htsfSampleIdList) {
        this.sampleIdList = htsfSampleIdList;
    }

}
