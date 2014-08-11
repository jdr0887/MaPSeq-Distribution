package edu.unc.mapseq.main;

import java.util.ArrayList;
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

import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.rs.RSDAOManager;

public class ListFlowcells implements Runnable {

    private final static HelpFormatter helpFormatter = new HelpFormatter();

    private final static Options cliOptions = new Options();

    public ListFlowcells() {
        super();
    }

    @Override
    public void run() {

        // WSDAOManager daoMgr = WSDAOManager.getInstance();
        RSDAOManager daoMgr = RSDAOManager.getInstance();
        MaPSeqDAOBean maPSeqDAOBean = daoMgr.getMaPSeqDAOBean();

        List<Flowcell> flowcellList = new ArrayList<Flowcell>();
        FlowcellDAO sequencerRunDAO = maPSeqDAOBean.getFlowcellDAO();
        try {
            flowcellList.addAll(sequencerRunDAO.findAll());
        } catch (Exception e) {
        }

        if (!flowcellList.isEmpty()) {

            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter(sb, Locale.US);
            formatter.format("%1$-8s %2$-38s %3$-42s%n", "ID", "Name", "Base Directory");
            for (Flowcell flowcell : flowcellList) {
                formatter.format("%1$-8s %2$-38s %3$-42s%n", flowcell.getId(), flowcell.getName(),
                        flowcell.getBaseDirectory());
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
        ListFlowcells main = new ListFlowcells();
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
