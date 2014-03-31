package edu.unc.mapseq.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SFTPTransferOutputTask implements Callable<Integer> {

    private final Logger logger = LoggerFactory.getLogger(SFTPTransferOutputTask.class);

    private String username;

    private String host;

    private List<String> files;

    private String remoteDirectory;

    public SFTPTransferOutputTask(String username, String host, List<String> files, String remoteDirectory) {
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
        String home = System.getProperty("user.home");
        String knownHostsFilename = String.format("%s/.ssh/known_hosts", home);
        String identity = String.format("%s/.ssh/id_rsa", home);

        JSch sch = new JSch();
        ChannelSftp sftpChannel = null;
        FileInputStream fis = null;
        Session session = null;
        try {
            sch.addIdentity(identity);
            sch.setKnownHosts(knownHostsFilename);
            session = sch.getSession(username, host, 22);
            session.setConfig("compression.s2c", "none");
            session.setConfig("compression.c2s", "none");
            session.setConfig("cipher.s2c", "arcfour128");
            session.setConfig("cipher.c2s", "arcfour128");
            session.connect(30000);

            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect(10 * 1000);
            // SftpATTRS attrs = sftpChannel.stat(remoteDirectory);
            sftpChannel.cd(remoteDirectory);
            for (String file : files) {
                try {
                    fis = new FileInputStream(new File(file));
                    sftpChannel.put(fis, file, new TransferProgressMonitor(), ChannelSftp.OVERWRITE);
                    sftpChannel.chmod(0644, file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (JSchException | SftpException e) {
            exitCode = -1;
            e.printStackTrace();
        } finally {
            if (sftpChannel != null) {
                sftpChannel.exit();
                sftpChannel.disconnect();
            }
            if (session != null) {
                session.disconnect();
                logger.debug("session.isConnected() = {}", session.isConnected());
            }
        }
        return exitCode;
    }
}
