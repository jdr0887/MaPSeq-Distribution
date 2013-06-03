package edu.unc.mapseq.main;

import java.util.Date;
import java.util.concurrent.Callable;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Workflow;
import edu.unc.mapseq.dao.ws.WebServiceDAOManager;

public class CreateWorkflow implements Callable<Long> {

    private final static HelpFormatter helpFormatter = new HelpFormatter();

    private final static Options cliOptions = new Options();

    private String name;

    public CreateWorkflow() {
        super();
    }

    @Override
    public Long call() {
        WebServiceDAOManager daoMgr = WebServiceDAOManager.getInstance();
        Date d = new Date();
        try {
            Workflow workflow = new Workflow();
            workflow.setCreationDate(d);
            workflow.setModificationDate(d);
            workflow.setCreator(daoMgr.getWSDAOBean().getAccountDAO().findByName(System.getProperty("user.name")));
            workflow.setName(name);
            Long workflowId = daoMgr.getWSDAOBean().getWorkflowDAO().save(workflow);
            return workflowId;
        } catch (MaPSeqDAOException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SuppressWarnings("static-access")
    public static void main(String[] args) {

        cliOptions.addOption(OptionBuilder.withArgName("name").isRequired().hasArg().create("name"));
        cliOptions.addOption(OptionBuilder.withArgName("help").withDescription("print this help message")
                .withLongOpt("help").create("?"));

        CommandLineParser commandLineParser = new GnuParser();
        CreateWorkflow main = new CreateWorkflow();
        try {
            CommandLine commandLine = commandLineParser.parse(cliOptions, args);
            if (commandLine.hasOption("?")) {
                helpFormatter.printHelp(main.getClass().getSimpleName(), cliOptions);
                return;
            }

            if (commandLine.hasOption("name")) {
                main.setName(commandLine.getOptionValue("name"));
            }

            System.out.println(main.call());
        } catch (ParseException e) {
            System.err.println(("Parsing Failed: " + e.getMessage()));
            helpFormatter.printHelp(main.getClass().getSimpleName(), cliOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
