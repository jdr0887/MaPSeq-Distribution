package edu.unc.mapseq.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import org.apache.commons.lang.StringUtils;

import edu.unc.mapseq.dao.StudyDAO;
import edu.unc.mapseq.dao.model.Study;
import edu.unc.mapseq.dao.ws.WebServiceDAOManager;

public class ListStudies implements Runnable {

    private final static HelpFormatter helpFormatter = new HelpFormatter();

    private final static Options cliOptions = new Options();

    private final WebServiceDAOManager daoMgr = WebServiceDAOManager.getInstance();

    public ListStudies() {
        super();
    }

    @Override
    public void run() {

        List<Study> studyList = new ArrayList<Study>();
        StudyDAO studyDAO = daoMgr.getWSDAOBean().getStudyDAO();

        try {
            studyList.addAll(studyDAO.findAll());
        } catch (Exception e) {
        }

        Collections.sort(studyList, new Comparator<Study>() {

            @Override
            public int compare(Study w1, Study w2) {
                if (StringUtils.isNotEmpty(w1.getName()) && StringUtils.isNotEmpty(w2.getName())) {
                    return w1.getName().compareTo(w2.getName());
                }
                return 0;
            }

        });

        if (studyList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter(sb, Locale.US);
            formatter.format("%1$-8s %2$-40s %3$s%n", "ID", "Name", "Grant");
            for (Study study : studyList) {
                formatter.format("%1$-8s %2$-40s %3$s%n", study.getId(), study.getName(), study.getGrant());
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
