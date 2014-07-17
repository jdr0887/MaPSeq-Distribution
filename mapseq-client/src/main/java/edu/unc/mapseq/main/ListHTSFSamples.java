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

import edu.unc.mapseq.dao.AccountDAO;
import edu.unc.mapseq.dao.HTSFSampleDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SequencerRunDAO;
import edu.unc.mapseq.dao.model.Account;
import edu.unc.mapseq.dao.model.HTSFSample;
import edu.unc.mapseq.dao.model.SequencerRun;
import edu.unc.mapseq.dao.rs.RSDAOManager;

public class ListHTSFSamples implements Runnable {

    private final static HelpFormatter helpFormatter = new HelpFormatter();

    private final static Options cliOptions = new Options();

    private Long sequencerRunId;

    private String name;

    public ListHTSFSamples() {
        super();
    }

    @Override
    public void run() {
        // WSDAOManager daoMgr = WSDAOManager.getInstance();
        RSDAOManager daoMgr = RSDAOManager.getInstance();

        MaPSeqDAOBean maPSeqDAOBean = daoMgr.getMaPSeqDAOBean();
        SequencerRunDAO sequencerRunDAO = maPSeqDAOBean.getSequencerRunDAO();
        HTSFSampleDAO htsfSampleDAO = maPSeqDAOBean.getHTSFSampleDAO();

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

        if (sequencerRunId == null && StringUtils.isEmpty(name)) {
            System.out.println("sequencerRunId & name can't both be null/empty");
            return;
        }

        List<HTSFSample> htsfSampleList = new ArrayList<HTSFSample>();

        if (sequencerRunId != null) {
            try {
                SequencerRun sequencerRun = sequencerRunDAO.findById(sequencerRunId);
                htsfSampleList.addAll(htsfSampleDAO.findBySequencerRunId(sequencerRun.getId()));
            } catch (MaPSeqDAOException e) {
            }
        }

        if (StringUtils.isNotEmpty(name)) {
            try {
                htsfSampleList.addAll(htsfSampleDAO.findByName(name));
            } catch (MaPSeqDAOException e) {
            }
        }

        if (htsfSampleList != null && htsfSampleList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter(sb, Locale.US);
            formatter.format("%1$-12s %2$-40s %3$-6s %4$s%n", "Sample ID", "Sample Name", "Lane", "Barcode");
            Collections.sort(htsfSampleList, new Comparator<HTSFSample>() {
                @Override
                public int compare(HTSFSample sr1, HTSFSample sr2) {
                    return sr1.getId().compareTo(sr2.getId());
                }
            });

            for (HTSFSample sample : htsfSampleList) {
                formatter.format("%1$-12s %2$-40s %3$-6s %4$s%n", sample.getId(), sample.getName(),
                        sample.getLaneIndex(), sample.getBarcode());
                formatter.flush();
            }
            System.out.println(formatter.toString());
            formatter.close();
        }

    }

    public Long getSequencerRunId() {
        return sequencerRunId;
    }

    public void setSequencerRunId(Long sequencerRunId) {
        this.sequencerRunId = sequencerRunId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SuppressWarnings("static-access")
    public static void main(String[] args) {
        cliOptions.addOption(OptionBuilder.withArgName("sequencerRunId").isRequired().hasArgs()
                .withDescription("Sequencer Run ID").withLongOpt("sequencerRunId").create());
        cliOptions.addOption(OptionBuilder.withArgName("help").withDescription("print this help message")
                .withLongOpt("help").create("?"));

        CommandLineParser commandLineParser = new GnuParser();
        ListHTSFSamples main = new ListHTSFSamples();
        try {

            CommandLine commandLine = commandLineParser.parse(cliOptions, args);
            if (commandLine.hasOption("?")) {
                helpFormatter.printHelp(main.getClass().getSimpleName(), cliOptions);
                return;
            }

            if (commandLine.hasOption("sequencerRunId")) {
                String sequencerRunIdValue = commandLine.getOptionValue("sequencerRunId");
                main.setSequencerRunId(Long.valueOf(sequencerRunIdValue));
            }

            if (commandLine.hasOption("name")) {
                String name = commandLine.getOptionValue("name");
                main.setName(name);
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
