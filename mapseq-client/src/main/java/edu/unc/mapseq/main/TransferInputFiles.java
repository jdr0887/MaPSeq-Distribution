package edu.unc.mapseq.main;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;

public class TransferInputFiles extends AbstractSFTP {

    public TransferInputFiles() {
        super();
    }

    @Override
    public DIRECTION getDirection() {
        return DIRECTION.GET;
    }

    @SuppressWarnings("static-access")
    public static void main(String[] args) {

        cliOptions.addOption(OptionBuilder.withLongOpt("host").withArgName("host").isRequired().hasArg().create());
        cliOptions.addOption(OptionBuilder.withLongOpt("remoteDirectory").withArgName("remoteDirectory").isRequired()
                .hasArg().create());
        cliOptions.addOption(OptionBuilder.withLongOpt("username").withArgName("username").isRequired().hasArg()
                .create());
        cliOptions.addOption(OptionBuilder.withLongOpt("fileName").withArgName("fileName").hasArgs().create());
        cliOptions.addOption(OptionBuilder.withLongOpt("help").withDescription("print this help message")
                .withLongOpt("help").create("?"));

        CommandLineParser commandLineParser = new GnuParser();
        TransferInputFiles main = new TransferInputFiles();
        int exitCode = 0;
        try {
            CommandLine commandLine = commandLineParser.parse(cliOptions, args);
            if (commandLine.hasOption("?")) {
                helpFormatter.printHelp(main.getClass().getSimpleName(), cliOptions);
                return;
            }

            if (commandLine.hasOption("fileName")) {
                List<String> fList = Arrays.asList(commandLine.getOptionValues("fileName"));
                main.setFileList(fList);
            }

            if (commandLine.hasOption("remoteDirectory")) {
                main.setRemoteDirectory(commandLine.getOptionValue("remoteDirectory"));
            }

            if (commandLine.hasOption("host")) {
                main.setHost(commandLine.getOptionValue("host"));
            }

            if (commandLine.hasOption("username")) {
                main.setUsername(commandLine.getOptionValue("username"));
            }

            exitCode = main.call();
        } catch (ParseException e) {
            exitCode = -1;
            System.err.println(("Parsing Failed: " + e.getMessage()));
            helpFormatter.printHelp(main.getClass().getSimpleName(), cliOptions);
        } catch (Exception e) {
            exitCode = -1;
            e.printStackTrace();
        } finally {
            System.exit(exitCode);
        }
    }

}
