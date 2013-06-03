package edu.unc.mapseq.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.karaf.shell.console.AbstractAction;
import org.renci.common.exec.BashExecutor;
import org.renci.common.exec.CommandInput;
import org.renci.common.exec.CommandOutput;
import org.renci.common.exec.ExecutorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.JobDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowPlanDAO;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.Job;
import edu.unc.mapseq.dao.model.WorkflowPlan;
import edu.unc.mapseq.dao.model.WorkflowRun;

@Command(scope = "mapseq", name = "delete-workflow-run", description = "Delete WorkflowRun")
public class DeleteWorkflowRunAction extends AbstractAction {

    private final Logger logger = LoggerFactory.getLogger(DeleteWorkflowRunAction.class);

    private MaPSeqDAOBean mapseqDAOBean;

    @Argument(index = 0, name = "workflowRunId", description = "Workflow Run Identifier", required = false, multiValued = true)
    private List<Long> workflowRunIdList;

    @Option(name = "-r", aliases = {}, description = "Workflow Run ID Range", required = false, multiValued = false)
    private String workflowRunIdRange;

    public DeleteWorkflowRunAction() {
        super();
    }

    @Override
    public Object doExecute() {

        List<Long> wrIdList = new ArrayList<Long>();

        if (this.workflowRunIdList != null && this.workflowRunIdList.size() > 0) {
            wrIdList.addAll(this.workflowRunIdList);
        }

        if (StringUtils.isNotEmpty(this.workflowRunIdRange)) {
            String[] split = this.workflowRunIdRange.trim().split("-");
            String rangeStart = split[0];
            String rangeEnd = split[1];
            for (int i = Integer.valueOf(rangeStart); i <= Integer.valueOf(rangeEnd); ++i) {
                wrIdList.add(Long.valueOf(i));
            }
        }

        WorkflowRunDAO workflowRunDAO = mapseqDAOBean.getWorkflowRunDAO();
        JobDAO jobDAO = mapseqDAOBean.getJobDAO();
        WorkflowPlanDAO workflowPlanDAO = mapseqDAOBean.getWorkflowPlanDAO();

        for (Long workflowRunId : wrIdList) {
            try {
                WorkflowRun workflowRun = workflowRunDAO.findById(workflowRunId);
                if (workflowRun != null) {

                    List<WorkflowPlan> workflowPlanList = workflowPlanDAO.findByWorkflowRunId(workflowRun.getId());

                    for (WorkflowPlan entity : workflowPlanList) {
                        logger.info("Deleting WorkflowPlan: " + entity.getId());
                        workflowPlanDAO.delete(entity);
                    }

                    List<Job> jobList = jobDAO.findByWorkflowRunId(workflowRun.getId());
                    jobDAO.delete(jobList);

                    logger.info("Deleting WorkflowRun: " + workflowRun.getId());
                    if (workflowRun.getCondorDAGClusterId() != null) {
                        CommandInput commandInput = new CommandInput();
                        String condorHome = System.getenv("CONDOR_HOME");
                        String command = String.format("%s/bin/condor_rm %d.0", condorHome,
                                workflowRun.getCondorDAGClusterId());
                        commandInput.setCommand(command);
                        try {
                            CommandOutput commandOutput = BashExecutor.getInstance().execute(commandInput,
                                    new File(System.getProperty("user.home"), ".bashrc"),
                                    new File(System.getProperty("user.home"), ".mapseqrc"));
                            logger.debug("commandOutput.getExitCode(): {}", commandOutput.getExitCode());
                        } catch (ExecutorException e) {
                            logger.error("Error", e);
                        }
                    }
                    workflowRunDAO.delete(workflowRun);
                }
            } catch (MaPSeqDAOException e) {
                e.printStackTrace();
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
