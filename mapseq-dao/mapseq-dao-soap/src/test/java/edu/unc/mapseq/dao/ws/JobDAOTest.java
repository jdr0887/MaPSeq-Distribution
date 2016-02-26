package edu.unc.mapseq.dao.ws;

import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;

import edu.unc.mapseq.dao.JobDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SOAPDAOManager;
import edu.unc.mapseq.dao.WorkflowRunAttemptDAO;
import edu.unc.mapseq.dao.model.Job;
import edu.unc.mapseq.dao.model.JobStatusType;
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;
import edu.unc.mapseq.dao.soap.JobDAOImpl;

public class JobDAOTest {

    @Test
    public void testFindById() {
        JobDAOImpl jobDAO = new JobDAOImpl();
        try {
            Job job = jobDAO.findById(4L);
            assertNotNull(job);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSave() {

        Job job = new Job();
        job.setFinished(new Date());
        job.setExitCode(0);
        job.setName("asdfasdqwer");
        job.setStarted(new Date());
        job.setStatus(JobStatusType.RUNNING);

        SOAPDAOManager daoMgr = SOAPDAOManager.getInstance();
        JobDAO jobDAO = daoMgr.getMaPSeqDAOBeanService().getJobDAO();

        try {
            Long id = jobDAO.save(job);
            System.out.println(id);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testSaveWithWorkflowRun() throws MaPSeqDAOException {
        SOAPDAOManager wsDAOMgr = SOAPDAOManager.getInstance();
        WorkflowRunAttemptDAO workflowRunAttemptDAO = wsDAOMgr.getMaPSeqDAOBeanService().getWorkflowRunAttemptDAO();
        WorkflowRunAttempt workflowRunAttempt = workflowRunAttemptDAO.findById(1267L);

        Job job = new Job();
        job.setWorkflowRunAttempt(workflowRunAttempt);
        job.setFinished(new Date());
        job.setExitCode(0);
        job.setName("asdfasd");
        job.setStarted(new Date());
        job.setStatus(JobStatusType.RUNNING);

        JobDAO jobDAO = wsDAOMgr.getMaPSeqDAOBeanService().getJobDAO();
        try {
            Long id = jobDAO.save(job);
            System.out.println(id);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testSaveMTOM() throws MaPSeqDAOException {
        SOAPDAOManager wsDAOMgr = SOAPDAOManager.getInstance();

        Job job = new Job();
        job.setStdout("\u0007a");
        job.setStderr("\u0001b");
        job.setFinished(new Date());
        job.setExitCode(0);
        job.setName("asdfasd");
        job.setStarted(new Date());
        job.setStatus(JobStatusType.RUNNING);

        JobDAO jobDAO = wsDAOMgr.getMaPSeqDAOBeanService().getJobDAO();
        try {
            Long id = jobDAO.save(job);
            System.out.println(id);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

    }

}
