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

    private List<Long> sequencerRunIdList;

    public ListHTSFSamples() {
        super();
    }

    @Override
    public void run() {
        //WSDAOManager daoMgr = WSDAOManager.getInstance();
        RSDAOManager daoMgr = RSDAOManager.getInstance();

        MaPSeqDAOBean mapseqDAOBean = daoMgr.getMaPSeqDAOBean();

        Account account = null;
        try {
            account = mapseqDAOBean.getAccountDAO().findByName(System.getProperty("user.name"));
        } catch (MaPSeqDAOException e) {
        }

        if (account == null) {
            System.out.println("Must register account first");
            return;
        }

        List<SequencerRun> srList = new ArrayList<SequencerRun>();
        SequencerRunDAO sequencerRunDAO = mapseqDAOBean.getSequencerRunDAO();
        for (Long sequencerRunId : sequencerRunIdList) {
            SequencerRun sequencerRun = null;
            try {
                sequencerRun = sequencerRunDAO.findById(sequencerRunId);

            } catch (MaPSeqDAOException e) {
            }
            if (sequencerRun != null) {
                srList.add(sequencerRun);
            }
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
            formatter.format("%1$-18s %2$-18s %3$-36s %4$s%n", "HTSF Sample ID", "Sample Name", "Lane", "Barcode");
            for (SequencerRun sequencerRun : srList) {
                HTSFSampleDAO htsfSampleDAO = mapseqDAOBean.getHTSFSampleDAO();
                try {
                    List<HTSFSample> htsfSampleList = htsfSampleDAO.findBySequencerRunId(sequencerRun.getId());
                    for (HTSFSample sample : htsfSampleList) {
                        if ("Undetermined".equals(sample.getBarcode())) {
                            continue;
                        }
                        formatter.format("%1$-18s %2$-18s %3$-36s %4$s%n", sample.getId(), sample.getName(),
                                sample.getLaneIndex(), sample.getBarcode());
                        formatter.flush();
                    }
                } catch (MaPSeqDAOException e) {
                }
            }
            System.out.println(formatter.toString());
            formatter.close();
        }

    }

    public List<Long> getSequencerRunIdList() {
        return sequencerRunIdList;
    }

    public void setSequencerRunIdList(List<Long> sequencerRunIdList) {
        this.sequencerRunIdList = sequencerRunIdList;
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

            List<Long> sequencerRunIdList = new ArrayList<Long>();
            if (commandLine.hasOption("sequencerRunId")) {
                String[] sequencerRunIdArray = commandLine.getOptionValues("sequencerRunId");
                for (String sequencerRunId : sequencerRunIdArray) {
                    Long id = null;
                    try {
                        id = Long.valueOf(sequencerRunId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (id != null) {
                        sequencerRunIdList.add(id);
                    }
                }
                main.setSequencerRunIdList(sequencerRunIdList);
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
