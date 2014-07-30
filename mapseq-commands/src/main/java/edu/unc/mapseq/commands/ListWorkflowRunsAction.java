package edu.unc.mapseq.commands;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;

@Command(scope = "mapseq", name = "list-workflow-runs", description = "List WorkflowRun instances")
public class ListWorkflowRunsAction extends AbstractAction {

    @Argument(index = 0, name = "workflowId", description = "Workflow identifier", required = true, multiValued = false)
    private Long workflowId;

    @Option(name = "-l", description = "long format", required = false, multiValued = false)
    private Boolean longFormat;

    private MaPSeqDAOBean maPSeqDAOBean;

    public ListWorkflowRunsAction() {
        super();
    }

    @Override
    public Object doExecute() {

        List<WorkflowRun> workflowRunList = new ArrayList<WorkflowRun>();
        WorkflowRunDAO workflowRunDAO = maPSeqDAOBean.getWorkflowRunDAO();
        try {
            workflowRunList.addAll(workflowRunDAO.findByWorkflowId(workflowId));
        } catch (MaPSeqDAOException e) {
        }
        try {
            if (workflowRunList != null && workflowRunList.size() > 0) {
                StringBuilder sb = new StringBuilder();
                Formatter formatter = new Formatter(sb, Locale.US);
                if (longFormat != null && longFormat) {
                    formatter.format("%1$-8s %2$-25s %3$-54s %4$-18s %5$-18s %6$-18s %7$-18s %8$-18s %9$s%n", "ID",
                            "Workflow Name", "Workflow Run Name", "Created Date", "Start Date", "End Date", "Status",
                            "Submit Directory", "Condor DAG ClusterId");
                } else {
                    formatter.format("%1$-8s %2$-25s %3$-54s %4$-18s %5$-18s %6$-18s %7$s%n", "ID", "Workflow Name",
                            "Workflow Run Name", "Created Date", "Start Date", "End Date", "Status");
                }

                Collections.sort(workflowRunList, new Comparator<WorkflowRun>() {
                    @Override
                    public int compare(WorkflowRun wr1, WorkflowRun wr2) {
                        return wr1.getId().compareTo(wr2.getId());
                    }
                });

                for (WorkflowRun workflowRun : workflowRunList) {

                    Date createdDate = workflowRun.getCreated();
                    String formattedCreatedDate = "";
                    if (createdDate != null) {
                        formattedCreatedDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                                .format(createdDate);
                    }

                    for (WorkflowRunAttempt attempt : workflowRun.getAttempts()) {

                        Date startDate = attempt.getStarted();
                        String formattedStartDate = "";
                        if (startDate != null) {
                            formattedStartDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                                    .format(startDate);
                        }
                        Date endDate = attempt.getFinished();
                        String formattedEndDate = "";
                        if (endDate != null) {
                            formattedEndDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                                    .format(endDate);
                        }
                        if (longFormat != null && longFormat) {
                            formatter.format("%1$-8s %2$-25s %3$-54s %4$-18s %5$-18s %6$-18s %7$-18s %8$-18s %9$s%n",
                                    workflowRun.getId(), workflowRun.getWorkflow().getName(), workflowRun.getName(),
                                    formattedCreatedDate, formattedStartDate, formattedEndDate, attempt.getStatus()
                                            .getState(), attempt.getSubmitDirectory(), attempt.getCondorDAGClusterId());
                        } else {
                            formatter.format("%1$-8s %2$-25s %3$-54s %4$-18s %5$-18s %6$-18s %7$s%n", workflowRun
                                    .getId(), workflowRun.getWorkflow().getName(), workflowRun.getName(),
                                    formattedCreatedDate, formattedStartDate, formattedEndDate, attempt.getStatus()
                                            .getState());

                        }
                        formatter.flush();

                    }

                }
                System.out.println(formatter.toString());
                formatter.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public Long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

    public Boolean getLongFormat() {
        return longFormat;
    }

    public void setLongFormat(Boolean longFormat) {
        this.longFormat = longFormat;
    }

    public MaPSeqDAOBean getMaPSeqDAOBean() {
        return maPSeqDAOBean;
    }

    public void setMaPSeqDAOBean(MaPSeqDAOBean maPSeqDAOBean) {
        this.maPSeqDAOBean = maPSeqDAOBean;
    }

}
