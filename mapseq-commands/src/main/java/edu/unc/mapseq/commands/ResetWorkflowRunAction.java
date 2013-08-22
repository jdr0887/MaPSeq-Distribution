package edu.unc.mapseq.commands;

import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.dao.model.WorkflowRunStatusType;

@Command(scope = "mapseq", name = "reset-workflow-run", description = "Reset WorkflowRun")
public class ResetWorkflowRunAction extends AbstractAction {

    private final Logger logger = LoggerFactory.getLogger(ResetWorkflowRunAction.class);

    private MaPSeqDAOBean maPSeqDAOBean;

    @Argument(index = 0, name = "workflowRunId", description = "Workflow Run Identifier", required = true, multiValued = true)
    private List<Long> workflowRunIdList;

    public ResetWorkflowRunAction() {
        super();
    }

    @Override
    public Object doExecute() {
        logger.debug("ENTERING doExecute()");

        if (this.workflowRunIdList != null && this.workflowRunIdList.size() > 0) {

            WorkflowRunDAO workflowRunDAO = maPSeqDAOBean.getWorkflowRunDAO();

            for (Long workflowRunId : this.workflowRunIdList) {
                logger.debug("resetting WorkflowRun: {}", workflowRunId);
                try {
                    WorkflowRun wr = workflowRunDAO.findById(workflowRunId);
                    wr.setStartDate(null);
                    wr.setEndDate(null);
                    wr.setDequeuedDate(null);
                    wr.setStatus(WorkflowRunStatusType.PENDING);
                    workflowRunDAO.save(wr);
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
