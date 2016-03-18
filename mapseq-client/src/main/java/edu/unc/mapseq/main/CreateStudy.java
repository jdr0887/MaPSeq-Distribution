package edu.unc.mapseq.main;

import java.util.concurrent.Callable;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import edu.unc.mapseq.dao.MaPSeqDAOBeanService;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Study;
import edu.unc.mapseq.dao.soap.SOAPDAOManager;

public class CreateStudy implements Callable<String> {

    private final static HelpFormatter helpFormatter = new HelpFormatter();

    private final static Options cliOptions = new Options();

    private String name;

    public CreateStudy() {
        super();
    }

    @Override
    public String call() {
        SOAPDAOManager daoMgr = SOAPDAOManager.getInstance();
        // RSDAOManager daoMgr = RSDAOManager.getInstance();
        MaPSeqDAOBeanService maPSeqDAOBeanService = daoMgr.getMaPSeqDAOBeanService();

        try {
            Study study = new Study(name);
            study.setId(maPSeqDAOBeanService.getStudyDAO().save(study));
            return study.toString();
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

        cliOptions.addOption(OptionBuilder.withArgName("name").withLongOpt("name").isRequired().hasArg().create());
        cliOptions.addOption(OptionBuilder.withArgName("help").withDescription("print this help message")
                .withLongOpt("help").create("?"));

        CommandLineParser commandLineParser = new GnuParser();
        CreateStudy main = new CreateStudy();
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
