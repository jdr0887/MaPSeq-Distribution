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

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SequencerRunDAO;
import edu.unc.mapseq.dao.model.Account;
import edu.unc.mapseq.dao.model.SequencerRun;
import edu.unc.mapseq.dao.rs.RSDAOManager;

public class ListSequencerRuns implements Runnable {

    private final static HelpFormatter helpFormatter = new HelpFormatter();

    private final static Options cliOptions = new Options();

    public ListSequencerRuns() {
        super();
    }

    @Override
    public void run() {

        // WSDAOManager daoMgr = WSDAOManager.getInstance();
        RSDAOManager daoMgr = RSDAOManager.getInstance();
        MaPSeqDAOBean maPSeqDAOBean = daoMgr.getMaPSeqDAOBean();

        List<Account> accountList = null;
        try {
            accountList = maPSeqDAOBean.getAccountDAO().findByName(System.getProperty("user.name"));
            if (accountList == null || (accountList != null && accountList.isEmpty())) {
                System.err.printf("Account doesn't exist: %s%n", System.getProperty("user.name"));
                System.err.println("Must register account first");
                return;
            }
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

        Account account = accountList.get(0);

        List<SequencerRun> srList = new ArrayList<SequencerRun>();
        SequencerRunDAO sequencerRunDAO = maPSeqDAOBean.getSequencerRunDAO();
        try {
            srList.addAll(sequencerRunDAO.findAll());
        } catch (Exception e) {
        }

        if (srList.size() > 0) {

            Collections.sort(srList, new Comparator<SequencerRun>() {

                @Override
                public int compare(SequencerRun sr1, SequencerRun sr2) {
                    if (sr1.getCreationDate().before(sr2.getCreationDate())) {
                        return -1;
                    }
                    if (sr1.getCreationDate().after(sr2.getCreationDate())) {
                        return 1;
                    }
                    if (sr1.getCreationDate().equals(sr2.getCreationDate())) {
                        return 0;
                    }
                    return 0;
                }

            });

            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter(sb, Locale.US);
            formatter.format("%1$-8s %2$-38s %3$-42s %4$s%n", "ID", "Name", "Base Directory", "Creator");
            for (SequencerRun sequencerRun : srList) {
                String creatorName = "";
                Account creator = sequencerRun.getCreator();
                if (creator != null) {
                    creatorName = creator.getName();
                }
                formatter.format("%1$-8s %2$-38s %3$-42s %4$s%n", sequencerRun.getId(), sequencerRun.getName(),
                        sequencerRun.getBaseDirectory(), creatorName);
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
        ListSequencerRuns main = new ListSequencerRuns();
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
