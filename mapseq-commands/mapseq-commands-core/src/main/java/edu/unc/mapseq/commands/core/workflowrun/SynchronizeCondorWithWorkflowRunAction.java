package edu.unc.mapseq.commands.core.workflowrun;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.renci.jlrm.JLRMException;
import org.renci.jlrm.condor.CondorJobStatusType;
import org.renci.jlrm.condor.cli.CondorLookupDAGStatusCallable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.WorkflowRunAttemptDAO;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.Workflow;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;
import edu.unc.mapseq.dao.model.WorkflowRunAttemptStatusType;

@Command(scope = "mapseq", name = "synchronize-condor-with-workflow-run", description = "Synchronize Condor with WorkflowRun")
@Service
public class SynchronizeCondorWithWorkflowRunAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(SynchronizeCondorWithWorkflowRunAction.class);

    @Argument(index = 0, name = "workflowRunId", description = "WorkflowRun identifier", required = true, multiValued = true)
    private List<Long> workflowRunIdList;

    @Reference
    private WorkflowRunDAO workflowRunDAO;

    @Reference
    private WorkflowRunAttemptDAO workflowRunAttemptDAO;

    public SynchronizeCondorWithWorkflowRunAction() {
        super();
    }

    @Override
    public Object execute() {
        logger.info("ENTERING doExecute()");

        try {
            List<WorkflowRun> workflowRunList = new ArrayList<WorkflowRun>();
            if (workflowRunIdList != null) {
                for (Long id : workflowRunIdList) {
                    workflowRunList.add(workflowRunDAO.findById(id));
                }
            }

            if (CollectionUtils.isNotEmpty(workflowRunList)) {
                workflowRunList.sort((a, b) -> a.getId().compareTo(b.getId()));

                for (WorkflowRun workflowRun : workflowRunList) {

                    logger.debug(workflowRun.toString());
                    Workflow workflow = workflowRun.getWorkflow();

                    List<WorkflowRunAttempt> attempts = workflowRunAttemptDAO.findByWorkflowRunId(workflowRun.getId());

                    if (CollectionUtils.isNotEmpty(attempts)) {
                        for (WorkflowRunAttempt attempt : attempts) {

                            File dagOutFile = new File(attempt.getSubmitDirectory(),
                                    String.format("%s.dag.dagman.out", workflow.getName()));

                            if (!dagOutFile.exists()) {
                                System.out.printf("%s doesn't exist%n", dagOutFile.getAbsolutePath());
                                continue;
                            }

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
                                        if (attempt.getStarted() == null) {
                                            Calendar c = Calendar.getInstance();
                                            c.setTime(workflowRun.getCreated());
                                            c.add(Calendar.MINUTE, 2);
                                            attempt.setStarted(c.getTime());
                                        }
                                        Date endDate = DateUtils.parseDate(
                                                String.format("%s %s", lineSplit[0], lineSplit[1]),
                                                new String[] { "MM/dd/yy HH:mm:ss" });
                                        Calendar c = Calendar.getInstance();
                                        c.setTime(endDate);
                                        attempt.setFinished(c.getTime());
                                    }
                                }
                                attempt.setStatus(WorkflowRunAttemptStatusType.DONE);
                                workflowRunAttemptDAO.save(attempt);
                            }

                        }
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

}
