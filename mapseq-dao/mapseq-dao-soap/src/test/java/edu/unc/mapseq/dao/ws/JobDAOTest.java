package edu.unc.mapseq.dao.ws;

import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;

import edu.unc.mapseq.dao.JobDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.Job;
import edu.unc.mapseq.dao.model.JobStatusType;
import edu.unc.mapseq.dao.model.WorkflowRun;

public class JobDAOTest {

    @Test
    public void testFindById() {
        JobDAO jobDAO = new JobDAOImpl();
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
        job.setDescription("asdf");
        job.setEndDate(new Date());
        job.setExitCode(0);
        job.setName("asdfasdqwer");
        job.setStartDate(new Date());
        job.setStatus(JobStatusType.RUNNING);

        WSDAOManager wsDAOMgr = WSDAOManager.getInstance("edu/unc/mapseq/dao/ws/mapseq-dao-beans-test.xml");
        JobDAO jobDAO = wsDAOMgr.getMaPSeqDAOBean().getJobDAO();

        try {
            Long id = jobDAO.save(job);
            System.out.println(id);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testSaveWithWorkflowRun() throws MaPSeqDAOException {
        WSDAOManager wsDAOMgr = WSDAOManager.getInstance();
        WorkflowRunDAO workflowRunDAO = wsDAOMgr.getMaPSeqDAOBean().getWorkflowRunDAO();
        WorkflowRun workflowRun = workflowRunDAO.findById(1267L);

        Job job = new Job();
        job.setWorkflowRun(workflowRun);
        job.setDescription("asdf");
        job.setEndDate(new Date());
        job.setExitCode(0);
        job.setName("asdfasd");
        job.setStartDate(new Date());
        job.setStatus(JobStatusType.RUNNING);

        JobDAO jobDAO = wsDAOMgr.getMaPSeqDAOBean().getJobDAO();
        try {
            Long id = jobDAO.save(job);
            System.out.println(id);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testSaveMTOM() throws MaPSeqDAOException {
        WSDAOManager wsDAOMgr = WSDAOManager.getInstance("edu/unc/mapseq/dao/ws/mapseq-dao-beans-test.xml");

        Job job = new Job();
        job.setStdout("\u0007a");
        job.setStderr("\u0001b");
        job.setDescription("asdf");
        job.setEndDate(new Date());
        job.setExitCode(0);
        job.setName("asdfasd");
        job.setStartDate(new Date());
        job.setStatus(JobStatusType.RUNNING);

        JobDAO jobDAO = wsDAOMgr.getMaPSeqDAOBean().getJobDAO();
        try {
            Long id = jobDAO.save(job);
            System.out.println(id);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

    }

}
