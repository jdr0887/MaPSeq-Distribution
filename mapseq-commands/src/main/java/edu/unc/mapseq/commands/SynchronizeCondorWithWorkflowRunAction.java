package edu.unc.mapseq.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;
import org.renci.jlrm.JLRMException;
import org.renci.jlrm.condor.CondorJobStatusType;
import org.renci.jlrm.condor.cli.CondorLookupDAGStatusCallable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.Account;
import edu.unc.mapseq.dao.model.Workflow;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.dao.model.WorkflowRunStatusType;

@Command(scope = "mapseq", name = "synchronize-condor-with-workflow-run", description = "Synchronize Condor with WorkflowRun")
public class SynchronizeCondorWithWorkflowRunAction extends AbstractAction {

    private final Logger logger = LoggerFactory.getLogger(SynchronizeCondorWithWorkflowRunAction.class);

    @Argument(index = 0, name = "workflowRunId", description = "WorkflowRun identifier", required = true, multiValued = true)
    private List<Long> workflowRunIdList;

    private MaPSeqDAOBean maPSeqDAOBean;

    public SynchronizeCondorWithWorkflowRunAction() {
        super();
    }

    @Override
    public Object doExecute() {
        logger.info("ENTERING doExecute()");
        Account account = null;
        try {
            account = maPSeqDAOBean.getAccountDAO().findByName(System.getProperty("user.name"));
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

        if (account == null) {
            logger.error("No account found");
            return null;
        }

        List<WorkflowRun> workflowRunList = new ArrayList<WorkflowRun>();
        WorkflowRunDAO workflowRunDAO = maPSeqDAOBean.getWorkflowRunDAO();
        try {
            if (workflowRunIdList != null) {
                for (Long id : workflowRunIdList) {
                    workflowRunList.add(workflowRunDAO.findById(id));
                }
            }
        } catch (MaPSeqDAOException e) {
        }

        try {

            if (workflowRunList != null && workflowRunList.size() > 0) {

                Collections.sort(workflowRunList, new Comparator<WorkflowRun>() {
                    @Override
                    public int compare(WorkflowRun wr1, WorkflowRun wr2) {
                        return wr1.getId().compareTo(wr2.getId());
                    }
                });

                for (final WorkflowRun workflowRun : workflowRunList) {
                    logger.debug(workflowRun.toString());
                    Workflow workflow = workflowRun.getWorkflow();
                    workflow.getName();
                    File dagOutFile = new File(workflowRun.getSubmitDirectory(), String.format("%s.dag.dagman.out",
                            workflow.getName()));
                    CondorLookupDAGStatusCallable callable = new CondorLookupDAGStatusCallable(dagOutFile);
                    CondorJobStatusType statusType = CondorJobStatusType.UNEXPANDED;
                    try {
                        statusType = callable.call();
                    } catch (JLRMException e) {
                        e.printStackTrace();
                    }

                    boolean jobFinished = false;
                    switch (statusType.getCode()) {
                        case 1:
                        case 2:
                            jobFinished = false;
                            break;
                        case 3:
                        case 4:
                        case 5:
                            jobFinished = true;
                            break;
                        default:
                            jobFinished = false;
                            break;
                    }

                    if (jobFinished) {

                        List<String> dagFileLines = FileUtils.readLines(dagOutFile);
                        for (String line : dagFileLines) {
                            if (line.contains("All jobs Completed!")) {
                                String[] lineSplit = line.split(" ");
                                if (workflowRun.getStartDate() == null) {
                                    Calendar c = Calendar.getInstance();
                                    c.setTime(workflowRun.getCreationDate());
                                    c.add(Calendar.MINUTE, 2);
                                    workflowRun.setStartDate(c.getTime());
                                }
                                Date endDate = DateUtils.parseDate(String.format("%s %s", lineSplit[0], lineSplit[1]),
                                        new String[] { "MM/dd/yy HH:mm:ss" });
                                Calendar c = Calendar.getInstance();
                                c.setTime(endDate);
                                workflowRun.setEndDate(c.getTime());
                            }
                        }
                        workflowRun.setStatus(WorkflowRunStatusType.DONE);
                        workflowRunDAO.save(workflowRun);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public List<Long> getWorkflowRunIdList() {
        return workflowRunIdList;
    }

    public void setWorkflowRunIdList(List<Long> workflowRunIdList) {
        this.workflowRunIdList = workflowRunIdList;
    }

    public MaPSeqDAOBean getMaPSeqDAOBean() {
        return maPSeqDAOBean;
    }

    public void setMaPSeqDAOBean(MaPSeqDAOBean maPSeqDAOBean) {
        this.maPSeqDAOBean = maPSeqDAOBean;
    }

}
