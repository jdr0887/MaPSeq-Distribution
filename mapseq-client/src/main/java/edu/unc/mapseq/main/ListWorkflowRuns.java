package edu.unc.mapseq.main;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowRunAttemptDAO;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;
import edu.unc.mapseq.dao.rs.RSDAOManager;

public class ListWorkflowRuns implements Runnable {

    private final static HelpFormatter helpFormatter = new HelpFormatter();

    private final static Options cliOptions = new Options();

    private Long workflowId;

    public ListWorkflowRuns() {
        super();
    }

    @Override
    public void run() {

        // WSDAOManager daoMgr = WSDAOManager.getInstance();
        RSDAOManager daoMgr = RSDAOManager.getInstance();

        MaPSeqDAOBean maPSeqDAOBean = daoMgr.getMaPSeqDAOBean();

        WorkflowRunDAO workflowRunDAO = maPSeqDAOBean.getWorkflowRunDAO();

        WorkflowRunAttemptDAO workflowRunAttemptDAO = maPSeqDAOBean.getWorkflowRunAttemptDAO();

        try {
            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter(sb, Locale.US);
            formatter.format("%1$-12s %2$-54s %3$-18s %4$-18s %5$-18s %6$-12s %7$-14s %8$s%n", "ID",
                    "Workflow Run Name", "Created Date", "Start Date", "End Date", "Status", "Condor JobId",
                    "Submit Directory");

            List<WorkflowRun> workflowRunList = workflowRunDAO.findByWorkflowId(workflowId);

            if (workflowRunList != null && !workflowRunList.isEmpty()) {
                for (WorkflowRun workflowRun : workflowRunList) {

                    Date createdDate = workflowRun.getCreated();
                    String formattedCreatedDate = "";
                    if (createdDate != null) {
                        formattedCreatedDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                                .format(createdDate);
                    }

                    formatter.format("%1$-12s %2$-54s %3$s%n", workflowRun.getId(), workflowRun.getName(),
                            formattedCreatedDate);

                    List<WorkflowRunAttempt> attempts = workflowRunAttemptDAO.findByWorkflowRunId(workflowRun.getId());

                    if (attempts != null && !attempts.isEmpty()) {
                        for (WorkflowRunAttempt attempt : attempts) {

                            createdDate = attempt.getCreated();
                            if (createdDate != null) {
                                formattedCreatedDate = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                                        DateFormat.SHORT).format(createdDate);
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

                            formatter.format("%1$-12s %2$-54s %3$-18s %4$-18s %5$-18s %6$-12s %7$-14s %8$s%n", "--",
                                    "--", formattedCreatedDate, formattedStartDate, formattedEndDate, attempt
                                            .getStatus().toString(), attempt.getCondorDAGClusterId(), attempt
                                            .getSubmitDirectory());
                            formatter.flush();

                        }
                    }

                }

            }

            System.out.print(formatter.toString());
            formatter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

    @SuppressWarnings("static-access")
    public static void main(String[] args) {

        cliOptions.addOption(OptionBuilder.withArgName("workflowId").withLongOpt("workflowId").isRequired().hasArg()
                .create());
        cliOptions.addOption(OptionBuilder.withArgName("help").withDescription("print this help message")
                .withLongOpt("help").create("?"));

        CommandLineParser commandLineParser = new GnuParser();
        ListWorkflowRuns main = new ListWorkflowRuns();
        try {
            CommandLine commandLine = commandLineParser.parse(cliOptions, args);
            if (commandLine.hasOption("?")) {
                helpFormatter.printHelp(main.getClass().getSimpleName(), cliOptions);
                return;
            }

            if (commandLine.hasOption("workflowId")) {
                main.setWorkflowId(Long.valueOf(commandLine.getOptionValue("workflowId")));
            }

            main.run();
        } catch (ParseException e) {
            System.err.println(("Parsing Failed: " + e.getMessage()));
            helpFormatter.printHelp(main.getClass().getSimpleName(), cliOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
