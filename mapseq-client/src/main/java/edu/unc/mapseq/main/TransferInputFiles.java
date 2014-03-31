package edu.unc.mapseq.main;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransferInputFiles extends AbstractTransferFiles {

    private final Logger logger = LoggerFactory.getLogger(TransferInputFiles.class);

    public TransferInputFiles() {
        super();
    }

    @Override
    public Integer call() {
        logger.debug("ENTERING call()");
        int exitCode = 0;
        try {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            try {
                // SFTPTransferInputTask task = new SFTPTransferInputTask(getUsername(), getHost(), getFileList(),
                // getRemoteDirectory());
                RSyncTransferInputTask task = new RSyncTransferInputTask(getUsername(), getHost(), getFileList(),
                        getRemoteDirectory());
                exitCode = executorService.submit(task).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            executorService.shutdown();
            executorService.awaitTermination(20, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            logger.error("InterruptedException", e);
            exitCode = -1;
        }
        return exitCode;
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
