package edu.unc.mapseq.main;

import java.text.DateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;
import edu.unc.mapseq.dao.rest.RESTDAOManager;

public class ListWorkflowRunsTest {

    @Test
    public void testRun() {
        RESTDAOManager daoMgr = RESTDAOManager.getInstance();

        try {
            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter(sb, Locale.US);
            formatter.format("%1$-8s %2$-25s %3$-54s %4$-18s %5$-18s %6$-18s %7$s%n", "ID", "Workflow Name",
                    "Workflow Run Name", "Created Date", "Start Date", "End Date", "Status");

            List<WorkflowRunAttempt> attempts = daoMgr.getMaPSeqDAOBeanService().getWorkflowRunAttemptDAO()
                    .findByWorkflowId(8L);
            if (attempts != null && !attempts.isEmpty()) {

                for (WorkflowRunAttempt attempt : attempts) {

                    WorkflowRun workflowRun = attempt.getWorkflowRun();

                    Date createdDate = workflowRun.getCreated();
                    String formattedCreatedDate = "";
                    if (createdDate != null) {
                        formattedCreatedDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                                .format(createdDate);
                    }

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
                    formatter.format("%1$-8s %2$-25s %3$-54s %4$-18s %5$-18s %6$-18s %7$s%n", workflowRun.getId(),
                            workflowRun.getWorkflow().getName(), workflowRun.getName(), formattedCreatedDate,
                            formattedStartDate, formattedEndDate, attempt.getStatus().getState());
                    formatter.flush();

                }
                System.out.println(formatter.toString());
            }
            formatter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
