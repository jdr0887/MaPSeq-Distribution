package edu.unc.mapseq.commands.sequencing.sample;

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
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.SampleWorkflowRunDependencyDAO;
import edu.unc.mapseq.dao.WorkflowRunAttemptDAO;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.Job;
import edu.unc.mapseq.dao.model.Sample;
import edu.unc.mapseq.dao.model.SampleWorkflowRunDependency;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;

@Command(scope = "mapseq", name = "delete-sample", description = "Delete Sample")
@Service
public class DeleteSampleAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(DeleteSampleAction.class);

    @Reference
    private MaPSeqDAOBeanService maPSeqDAOBeanService;

    @Argument(index = 0, name = "sampleId", description = "Sample Identifier", required = true, multiValued = true)
    private List<Long> sampleIdList;

    public DeleteSampleAction() {
        super();
    }

    @Override
    public Object execute() {
        logger.debug("ENTERING execute()");

        SampleDAO sampleDAO = maPSeqDAOBeanService.getSampleDAO();
        WorkflowRunAttemptDAO workflowRunAttemptDAO = maPSeqDAOBeanService.getWorkflowRunAttemptDAO();
        WorkflowRunDAO workflowRunDAO = maPSeqDAOBeanService.getWorkflowRunDAO();
        JobDAO jobDAO = maPSeqDAOBeanService.getJobDAO();
        SampleWorkflowRunDependencyDAO sampleWorkflowRunDependencyDAO = maPSeqDAOBeanService.getSampleWorkflowRunDependencyDAO();

        if (CollectionUtils.isEmpty(sampleIdList)) {
            logger.warn("Sample list is empty");
            return null;
        }

        try {

            for (Long id : this.sampleIdList) {
                Sample sample = sampleDAO.findById(id);
                List<WorkflowRun> workflowRunList = workflowRunDAO.findBySampleId(sample.getId());

                if (CollectionUtils.isEmpty(workflowRunList)) {
                    logger.warn("No WorkflowRuns found");
                    continue;
                }

                for (WorkflowRun workflowRun : workflowRunList) {
                    List<WorkflowRunAttempt> attempts = workflowRunAttemptDAO.findByWorkflowRunId(workflowRun.getId());
                    if (CollectionUtils.isEmpty(attempts)) {
                        logger.warn("No WorkflowRunAttempts found");
                        continue;
                    }
                    
                    for (WorkflowRunAttempt attempt : attempts) {
                        List<Job> jobList = jobDAO.findByWorkflowRunAttemptId(attempt.getId());
                        if (CollectionUtils.isNotEmpty(jobList)) {
                            for (Job job : jobList) {
                                job.setAttributes(null);
                                job.setFileDatas(null);
                                jobDAO.save(job);
                            }
                            jobDAO.delete(jobList);
                            logger.info("Number of Jobs deleted: {}", jobList.size());
                        }
                    }
                    workflowRunAttemptDAO.delete(attempts);
                    logger.info("Number of WorkflowRunAttempts deleted: {}", attempts.size());

                    workflowRun.setAttributes(null);
                    workflowRun.setFileDatas(null);
                    workflowRunDAO.save(workflowRun);
                }
                workflowRunDAO.delete(workflowRunList);
                logger.info("Number of WorkflowRuns deleted: {}", workflowRunList.size());

                List<SampleWorkflowRunDependency> sampleWorkflowRunDepedencyList = sampleWorkflowRunDependencyDAO
                        .findBySampleId(sample.getId());
                sampleWorkflowRunDependencyDAO.delete(sampleWorkflowRunDepedencyList);

                sample.setAttributes(null);
                sample.setFileDatas(null);
                sampleDAO.save(sample);

                logger.info("Deleting Sample: {}", sample.toString());
                sampleDAO.delete(sample);

            }
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Long> getSampleIdList() {
        return sampleIdList;
    }

    public void setSampleIdList(List<Long> sampleIdList) {
        this.sampleIdList = sampleIdList;
    }

}
