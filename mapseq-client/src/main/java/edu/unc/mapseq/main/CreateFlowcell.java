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

import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.ws.WSDAOManager;

public class CreateFlowcell implements Callable<String> {

    private final static HelpFormatter helpFormatter = new HelpFormatter();

    private final static Options cliOptions = new Options();

    private String baseRunFolder;

    private String name;

    public CreateFlowcell() {
        super();
    }

    @Override
    public String call() {
        WSDAOManager daoMgr = WSDAOManager.getInstance();
        // RSDAOManager daoMgr = RSDAOManager.getInstance();

        MaPSeqDAOBean maPSeqDAOBean = daoMgr.getMaPSeqDAOBean();

        Pattern pattern = Pattern.compile("^\\d+_.+_\\d+_.+$");
        Matcher matcher = pattern.matcher(name);
        if (!matcher.matches()) {
            System.err.println("Invalid fastq name: " + name);
            System.err.println("Please use <date>_<machineID>_<technicianID>_<flowcell>");
            System.err.println("For example: 120110_UNC13-SN749_0141_AD0J7WACXX");
            return null;
        }

        Flowcell flowcell = new Flowcell();
        try {
            flowcell.setName(this.getName());
            if (StringUtils.isNotEmpty(this.baseRunFolder)) {
                flowcell.setBaseDirectory(this.baseRunFolder);
            }
            FlowcellDAO flowcellDAO = maPSeqDAOBean.getFlowcellDAO();
            Long id = flowcellDAO.save(flowcell);
            flowcell.setId(id);
            return flowcell.toString();
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

    public String getBaseRunFolder() {
        return baseRunFolder;
    }

    public void setBaseRunFolder(String baseRunFolder) {
        this.baseRunFolder = baseRunFolder;
    }

    public static HelpFormatter getHelpformatter() {
        return helpFormatter;
    }

    @SuppressWarnings("static-access")
    public static void main(String[] args) {
        cliOptions.addOption(OptionBuilder.withArgName("name").withLongOpt("name")
                .withDescription("The flowcell...format should be: <date>_<sequencerID>_<technicianID>_<flowcell>")
                .isRequired().hasArg().create());
        cliOptions.addOption(OptionBuilder.withArgName("baseRunFolder").withLongOpt("baseRunFolder").hasArg().create());
        cliOptions.addOption(OptionBuilder.withLongOpt("help").withDescription("print this help message").create("?"));

        CommandLineParser commandLineParser = new GnuParser();
        CreateFlowcell main = new CreateFlowcell();
        try {
            CommandLine commandLine = commandLineParser.parse(cliOptions, args);
            if (commandLine.hasOption("?")) {
                helpFormatter.printHelp(main.getClass().getSimpleName(), cliOptions);
                return;
            }

            if (commandLine.hasOption("name")) {
                main.setName(commandLine.getOptionValue("name"));
            }

            if (commandLine.hasOption("baseRunFolder")) {
                main.setBaseRunFolder(commandLine.getOptionValue("baseRunFolder"));
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
