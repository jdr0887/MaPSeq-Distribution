package edu.unc.mapseq.main;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Comparator;
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

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.Account;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.dao.ws.WebServiceDAOManager;

public class ListMyWorkflowRuns implements Runnable {

    private final static HelpFormatter helpFormatter = new HelpFormatter();

    private final static Options cliOptions = new Options();

    public ListMyWorkflowRuns() {
        super();
    }

    @Override
    public void run() {

        WebServiceDAOManager daoMgr = WebServiceDAOManager.getInstance();

        Account account = null;
        try {
            account = daoMgr.getWSDAOBean().getAccountDAO().findByName(System.getProperty("user.name"));
        } catch (MaPSeqDAOException e) {
        }

        if (account == null) {
            System.out.println("Must register account first");
            return;
        }

        List<WorkflowRun> workflowRunList = null;
        WorkflowRunDAO workflowRunDAO = daoMgr.getWSDAOBean().getWorkflowRunDAO();
        try {
            workflowRunList = workflowRunDAO.findByCreator(account.getId());
        } catch (MaPSeqDAOException e) {
        }
        if (workflowRunList != null && workflowRunList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter(sb, Locale.US);
            formatter.format("%1$-10s %2$-25s %3$-30s %4$-22s %5$-22s %6$s%n", "ID", "Workflow Name",
                    "Workflow Run Name", "Start Date", "End Date", "Status");

            Collections.sort(workflowRunList, new Comparator<WorkflowRun>() {

                @Override
                public int compare(WorkflowRun wr1, WorkflowRun wr2) {
                    if (wr1.getStartDate() != null && wr2.getStartDate() != null) {
                        if (wr1.getStartDate().before(wr2.getStartDate())) {
                            return -1;
                        }
                        if (wr1.getStartDate().after(wr2.getStartDate())) {
                            return 1;
                        }
                        if (wr1.getStartDate().equals(wr2.getStartDate())) {
                            return 0;
                        }
                    }
                    if (wr1.getStartDate() != null && wr2.getStartDate() == null) {
                        return -1;
                    }
                    if (wr1.getStartDate() == null && wr2.getStartDate() == null) {
                        return 1;
                    }
                    return wr1.getCreationDate().compareTo(wr2.getCreationDate());
                }

            });

            for (WorkflowRun workflowRun : workflowRunList) {

                Date startDate = workflowRun.getStartDate();
                String formattedStartDate = "";
                if (startDate != null) {
                    formattedStartDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(
                            startDate);
                }
                Date endDate = workflowRun.getEndDate();
                String formattedEndDate = "";
                if (endDate != null) {
                    formattedEndDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(
                            endDate);
                }
                formatter.format("%1$-10s %2$-25s %3$-30s %4$-22s %5$-22s %6$s%n", workflowRun.getId(), workflowRun
                        .getWorkflow().getName(), workflowRun.getName(), formattedStartDate, formattedEndDate,
                        workflowRun.getStatus().getState());
                formatter.flush();

            }
            System.out.println(formatter.toString());
            formatter.close();
        }

    }

    @SuppressWarnings("static-access")
    public static void main(String[] args) {

        cliOptions.addOption(OptionBuilder.withArgName("help").withDescription("print this help message")
                .withLongOpt("help").create("?"));

        CommandLineParser commandLineParser = new GnuParser();
        ListMyWorkflowRuns main = new ListMyWorkflowRuns();
        try {
            CommandLine commandLine = commandLineParser.parse(cliOptions, args);
            if (commandLine.hasOption("?")) {
                helpFormatter.printHelp(main.getClass().getSimpleName(), cliOptions);
                return;
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
