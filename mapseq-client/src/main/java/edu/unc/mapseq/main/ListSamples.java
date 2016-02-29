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

import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBeanService;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.RESTDAOManager;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.model.Sample;

public class ListSamples implements Runnable {

    private final static HelpFormatter helpFormatter = new HelpFormatter();

    private final static Options cliOptions = new Options();

    private Long flowcellId;

    private String name;

    public ListSamples() {
        super();
    }

    @Override
    public void run() {
        // WSDAOManager daoMgr = WSDAOManager.getInstance();
        RESTDAOManager daoMgr = RESTDAOManager.getInstance();

        MaPSeqDAOBeanService maPSeqDAOBeanService = daoMgr.getMaPSeqDAOBeanService();
        FlowcellDAO flowcellDAO = maPSeqDAOBeanService.getFlowcellDAO();
        SampleDAO sampleDAO = maPSeqDAOBeanService.getSampleDAO();

        if (flowcellId == null && StringUtils.isEmpty(name)) {
            System.out.println("sequencerRunId & name can't both be null/empty");
            return;
        }

        List<Sample> sampleList = new ArrayList<Sample>();

        if (flowcellId != null) {
            try {
                Flowcell flowcell = flowcellDAO.findById(flowcellId);
                sampleList.addAll(sampleDAO.findByFlowcellId(flowcell.getId()));
            } catch (MaPSeqDAOException e) {
            }
        }

        if (StringUtils.isNotEmpty(name)) {
            try {
                sampleList.addAll(sampleDAO.findByName(name));
            } catch (MaPSeqDAOException e) {
            }
        }

        if (sampleList != null && sampleList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter(sb, Locale.US);
            formatter.format("%1$-12s %2$-40s %3$-6s %4$s%n", "Sample ID", "Sample Name", "Lane", "Barcode");
            Collections.sort(sampleList, new Comparator<Sample>() {
                @Override
                public int compare(Sample sr1, Sample sr2) {
                    return sr1.getId().compareTo(sr2.getId());
                }
            });

            for (Sample sample : sampleList) {
                formatter.format("%1$-12s %2$-40s %3$-6s %4$s%n", sample.getId(), sample.getName(),
                        sample.getLaneIndex(), sample.getBarcode());
                formatter.flush();
            }
            System.out.println(formatter.toString());
            formatter.close();
        }

    }

    public Long getFlowcellId() {
        return flowcellId;
    }

    public void setFlowcellId(Long flowcellId) {
        this.flowcellId = flowcellId;
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
        ListSamples main = new ListSamples();
        try {

            CommandLine commandLine = commandLineParser.parse(cliOptions, args);
            if (commandLine.hasOption("?")) {
                helpFormatter.printHelp(main.getClass().getSimpleName(), cliOptions);
                return;
            }

            if (commandLine.hasOption("flowcellId")) {
                String flowcellIdValue = commandLine.getOptionValue("flowcellId");
                main.setFlowcellId(Long.valueOf(flowcellIdValue));
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
