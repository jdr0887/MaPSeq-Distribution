package edu.unc.mapseq.main;

import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SequencerRunDAO;
import edu.unc.mapseq.dao.model.Account;
import edu.unc.mapseq.dao.model.SequencerRun;
import edu.unc.mapseq.dao.model.SequencerRunStatusType;
import edu.unc.mapseq.dao.ws.WSDAOManager;

public class CreateSequencerRun implements Callable<Long> {

    private final static HelpFormatter helpFormatter = new HelpFormatter();

    private final static Options cliOptions = new Options();

    private Long platformId = 66L;

    private String baseRunFolder;

    private String name;

    private SequencerRunStatusType status = SequencerRunStatusType.COMPLETED;

    public CreateSequencerRun() {
        super();
    }

    @Override
    public Long call() {
        WSDAOManager daoMgr = WSDAOManager.getInstance();
        // RSDAOManager daoMgr = RSDAOManager.getInstance();

        MaPSeqDAOBean mapseqDAOBean = daoMgr.getMaPSeqDAOBean();

        Account account = null;
        try {
            account = mapseqDAOBean.getAccountDAO().findByName(System.getProperty("user.name"));
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

        if (account == null) {
            System.out.println("Must register account first");
            return null;
        }

        Pattern pattern = Pattern.compile("^\\d+_.+_\\d+_.+$");
        Matcher matcher = pattern.matcher(name);
        if (!matcher.matches()) {
            System.err.println("Invalid fastq name: " + name);
            System.err.println("Please use <date>_<machineID>_<technicianID>_<flowcell>");
            System.err.println("For example: 120110_UNC13-SN749_0141_AD0J7WACXX");
            return null;
        }

        SequencerRun sequencerRun = new SequencerRun();
        try {
            sequencerRun.setCreator(account);
            sequencerRun.setName(this.getName());
            if (StringUtils.isNotEmpty(this.baseRunFolder)) {
                sequencerRun.setBaseDirectory(this.baseRunFolder);
            }
            sequencerRun.setPlatform(mapseqDAOBean.getPlatformDAO().findById(this.platformId));
            sequencerRun.setStatus(status);
            SequencerRunDAO sequencerRunDAO = mapseqDAOBean.getSequencerRunDAO();
            Long sequencerRunId = sequencerRunDAO.save(sequencerRun);
            return sequencerRunId;
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

    public Long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
    }

    public String getBaseRunFolder() {
        return baseRunFolder;
    }

    public void setBaseRunFolder(String baseRunFolder) {
        this.baseRunFolder = baseRunFolder;
    }

    public SequencerRunStatusType getStatus() {
        return status;
    }

    public void setStatus(SequencerRunStatusType status) {
        this.status = status;
    }

    public static HelpFormatter getHelpformatter() {
        return helpFormatter;
    }

    @SuppressWarnings("static-access")
    public static void main(String[] args) {
        cliOptions.addOption(OptionBuilder.withArgName("name").withLongOpt("name")
                .withDescription("The flowcell...format should be: <date>_<sequencerID>_<technicianID>_<flowcell>")
                .isRequired().hasArg().create());

        cliOptions.addOption(OptionBuilder.withArgName("platformId").withLongOpt("platformId").hasArg().create());

        cliOptions.addOption(OptionBuilder.withArgName("baseRunFolder").withLongOpt("baseRunFolder").hasArg().create());

        StringBuilder sb = new StringBuilder();
        for (SequencerRunStatusType type : SequencerRunStatusType.values()) {
            sb.append(", ").append(type.toString());
        }

        cliOptions.addOption(OptionBuilder.withArgName("status").isRequired().hasArg()
                .withDescription(sb.toString().replaceFirst(", ", "")).withLongOpt("status").create());

        cliOptions.addOption(OptionBuilder.withLongOpt("help").withDescription("print this help message").create("?"));

        CommandLineParser commandLineParser = new GnuParser();
        CreateSequencerRun main = new CreateSequencerRun();
        try {
            CommandLine commandLine = commandLineParser.parse(cliOptions, args);
            if (commandLine.hasOption("?")) {
                helpFormatter.printHelp(main.getClass().getSimpleName(), cliOptions);
                return;
            }

            if (commandLine.hasOption("name")) {
                main.setName(commandLine.getOptionValue("name"));
            }

            if (commandLine.hasOption("platformId")) {
                main.setPlatformId(Long.valueOf(commandLine.getOptionValue("platformId")));
            }

            if (commandLine.hasOption("baseRunFolder")) {
                main.setBaseRunFolder(commandLine.getOptionValue("baseRunFolder"));
            }

            if (commandLine.hasOption("status")) {
                main.setStatus(SequencerRunStatusType.valueOf(commandLine.getOptionValue("status")));
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
