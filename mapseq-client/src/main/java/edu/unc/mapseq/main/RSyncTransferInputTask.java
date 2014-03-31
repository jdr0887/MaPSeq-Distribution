package edu.unc.mapseq.main;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.renci.common.exec.BashExecutor;
import org.renci.common.exec.CommandInput;
import org.renci.common.exec.CommandOutput;
import org.renci.common.exec.Executor;
import org.renci.common.exec.ExecutorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RSyncTransferInputTask implements Callable<Integer> {

    private final Logger logger = LoggerFactory.getLogger(RSyncTransferInputTask.class);

    private String username;

    private String host;

    private List<String> files;

    private String remoteDirectory;

    public RSyncTransferInputTask(String username, String host, List<String> files, String remoteDirectory) {
        super();
        this.username = username;
        this.host = host;
        this.files = files;
        this.remoteDirectory = remoteDirectory;
    }

    @Override
    public Integer call() {
        logger.debug("ENTERING run()");
        int exitCode = 0;
        CommandInput commandInput = new CommandInput();
        commandInput.setWorkDir(new File("."));
        String rsyncCommand = "$RSYNC_HOME/rsync --rsh=\"ssh -c arcfour\"";
        StringBuilder commandSB = new StringBuilder();
        if (files.size() == 1) {
            commandSB.append(String.format("%s %s@%s:%s/%s .", rsyncCommand, username, host, remoteDirectory,
                    files.get(0)));
        } else if (files.size() > 1) {
            commandSB.append(String.format("%s %s@%s:%s/{%s} .", rsyncCommand, username, host, remoteDirectory,
                    StringUtils.join(files, ",")));
        }
        commandInput.setCommand(commandSB.toString());
        String home = System.getProperty("user.home");
        try {
            Executor executor = BashExecutor.getInstance();
            CommandOutput commandOutput = executor.execute(commandInput, new File(home, ".mapseqrc"));
            exitCode = commandOutput.getExitCode();
            System.out.println(commandOutput.getStdout());
        } catch (ExecutorException e) {
            exitCode = -1;
            e.printStackTrace();
        }
        return exitCode;
    }
}
