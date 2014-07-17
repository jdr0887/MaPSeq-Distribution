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
import org.apache.commons.lang.StringUtils;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.PlatformDAO;
import edu.unc.mapseq.dao.model.Platform;
import edu.unc.mapseq.dao.rs.RSDAOManager;

public class ListPlatforms implements Runnable {

    private final static HelpFormatter helpFormatter = new HelpFormatter();

    private final static Options cliOptions = new Options();

    private String platformName;

    private String platformInstrument;

    public ListPlatforms() {
        super();
    }

    @Override
    public void run() {

        // WSDAOManager daoMgr = WSDAOManager.getInstance();
        RSDAOManager daoMgr = RSDAOManager.getInstance();
        MaPSeqDAOBean mapseqDAOBean = daoMgr.getMaPSeqDAOBean();

        List<Platform> platformList = new ArrayList<Platform>();
        PlatformDAO platformDAO = mapseqDAOBean.getPlatformDAO();
        if (StringUtils.isNotEmpty(this.platformName) && StringUtils.isNotEmpty(this.platformInstrument)) {
            try {
                platformList.add(platformDAO.findByInstrumentAndModel(platformName, platformInstrument));
            } catch (MaPSeqDAOException e) {
                e.printStackTrace();
            }
        } else if (StringUtils.isNotEmpty(this.platformName) && StringUtils.isEmpty(this.platformInstrument)) {
            try {
                platformList.addAll(platformDAO.findByInstrument(platformName));
            } catch (MaPSeqDAOException e) {
                e.printStackTrace();
            }
        } else if (StringUtils.isEmpty(this.platformName) && StringUtils.isEmpty(this.platformInstrument)) {
            try {
                platformList.addAll(platformDAO.findAll());
            } catch (MaPSeqDAOException e) {
                e.printStackTrace();
            }
        }

        if (platformList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter(sb, Locale.US);
            formatter.format("%1$-4s %2$-20s %3$s%n", "ID", "Instrument", "Model");
            for (Platform platform : platformList) {
                formatter.format("%1$-4s %2$-20s %3$s%n", platform.getId(), platform.getInstrument(),
                        platform.getInstrumentModel());
                formatter.flush();
            }
            System.out.println(formatter.toString());
            formatter.close();
        }

    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getPlatformInstrument() {
        return platformInstrument;
    }

    public void setPlatformInstrument(String platformInstrument) {
        this.platformInstrument = platformInstrument;
    }

    @SuppressWarnings("static-access")
    public static void main(String[] args) {

        cliOptions.addOption(OptionBuilder.withArgName("platformName").hasArg().create("platformName"));
        cliOptions.addOption(OptionBuilder.withArgName("platformInstrument").hasArg().create("platformInstrument"));
        cliOptions.addOption(OptionBuilder.withArgName("help").withDescription("print this help message")
                .withLongOpt("help").create("?"));

        CommandLineParser commandLineParser = new GnuParser();
        ListPlatforms main = new ListPlatforms();
        try {
            CommandLine commandLine = commandLineParser.parse(cliOptions, args);
            if (commandLine.hasOption("?")) {
                helpFormatter.printHelp(main.getClass().getSimpleName(), cliOptions);
                return;
            }

            if (commandLine.hasOption("platformName")) {
                main.setPlatformName(commandLine.getOptionValue("platformName"));
            }

            if (commandLine.hasOption("platformInstrument")) {
                main.setPlatformName(commandLine.getOptionValue("platformInstrument"));
            }

            if (commandLine.hasOption("flowcellName")) {
                main.setPlatformName(commandLine.getOptionValue("flowcellName"));
            }

            if (commandLine.hasOption("runConfigurationName")) {
                main.setPlatformName(commandLine.getOptionValue("runConfigurationName"));
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
