package edu.unc.mapseq.main;

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

import edu.unc.mapseq.dao.MaPSeqDAOBeanService;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Workflow;
import edu.unc.mapseq.dao.rest.RESTDAOManager;

public class ListWorkflows implements Runnable {

    private final static HelpFormatter helpFormatter = new HelpFormatter();

    private final static Options cliOptions = new Options();

    public ListWorkflows() {
        super();
    }

    @Override
    public void run() {

        // WSDAOManager daoMgr = WSDAOManager.getInstance();
        RESTDAOManager daoMgr = RESTDAOManager.getInstance();
        MaPSeqDAOBeanService mapseqDAOBeanService = daoMgr.getMaPSeqDAOBeanService();

        try {
            List<Workflow> workflowList = mapseqDAOBeanService.getWorkflowDAO().findAll();

            if (workflowList.size() > 0) {
                StringBuilder sb = new StringBuilder();
                Formatter formatter = new Formatter(sb, Locale.US);
                formatter.format("%1$-8s %2$-30s%n", "ID", "Name");
                for (Workflow workflow : workflowList) {
                    formatter.format("%1$-8s %2$-30s%n", workflow.getId(), workflow.getName());
                    formatter.flush();
                }
                System.out.println(formatter.toString());
                formatter.close();
            }
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("static-access")
    public static void main(String[] args) {

        cliOptions.addOption(OptionBuilder.withArgName("help").withDescription("print this help message")
                .withLongOpt("help").create("?"));

        CommandLineParser commandLineParser = new GnuParser();
        ListWorkflows main = new ListWorkflows();
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
