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
import org.apache.commons.collections.CollectionUtils;

import edu.unc.mapseq.dao.MaPSeqDAOBeanService;
import edu.unc.mapseq.dao.model.Study;
import edu.unc.mapseq.dao.rest.RESTDAOManager;

public class ListStudies implements Runnable {

    private final static HelpFormatter helpFormatter = new HelpFormatter();

    private final static Options cliOptions = new Options();

    public ListStudies() {
        super();
    }

    @Override
    public void run() {

        // WSDAOManager daoMgr = WSDAOManager.getInstance();
        RESTDAOManager daoMgr = RESTDAOManager.getInstance();
        MaPSeqDAOBeanService mapseqDAOBeanService = daoMgr.getMaPSeqDAOBeanService();

        try {
            List<Study> studyList = mapseqDAOBeanService.getStudyDAO().findAll();
            if (CollectionUtils.isNotEmpty(studyList)) {
                StringBuilder sb = new StringBuilder();
                Formatter formatter = new Formatter(sb, Locale.US);
                formatter.format("%1$-8s %2$-40s%n", "ID", "Name");
                for (Study study : studyList) {
                    formatter.format("%1$-8s %2$-40s%n", study.getId(), study.getName());
                    formatter.flush();
                }
                System.out.println(formatter.toString());
                formatter.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("static-access")
    public static void main(String[] args) {
        cliOptions.addOption(OptionBuilder.withArgName("help").withDescription("print this help message")
                .withLongOpt("help").create("?"));

        CommandLineParser commandLineParser = new GnuParser();
        ListStudies main = new ListStudies();
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
