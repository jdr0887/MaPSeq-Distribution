package edu.unc.mapseq.main;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public abstract class AbstractSFTP implements Callable<Integer> {

    private final Logger logger = LoggerFactory.getLogger(AbstractSFTP.class);

    static enum DIRECTION {
        PUT, GET
    }

    private String username;

    private String host;

    private String remoteDirectory;

    private List<String> fileList;

    public abstract DIRECTION getDirection();

    @Override
    public Integer call() {

        String home = System.getProperty("user.home");
        String knownHostsFilename = home + "/.ssh/known_hosts";

        JSch sch = new JSch();
        Session session = null;

        int exitCode = 0;

        try {
            sch.addIdentity(home + "/.ssh/id_rsa");
            sch.setKnownHosts(knownHostsFilename);
            session = sch.getSession(username, host, 22);
            session.setConfig("compression.s2c", "none");
            session.setConfig("compression.c2s", "none");
            session.setConfig("cipher.s2c", "arcfour128");
            session.setConfig("cipher.c2s", "arcfour128");
            session.connect(30000);

            ExecutorService executorService = Executors.newFixedThreadPool(fileList.size());

            for (String f : fileList) {
                switch (getDirection()) {
                    case PUT:
                        executorService.submit(new SFTPTransferOutputTask(session, new File(f), remoteDirectory));
                        break;
                    case GET:
                        executorService.submit(new SFTPTransferInputTask(session, f, remoteDirectory));
                        break;
                }

            }

            executorService.shutdown();
            executorService.awaitTermination(20, TimeUnit.MINUTES);

        } catch (JSchException e) {
            logger.error("JSchException", e);
            exitCode = -1;
        } catch (InterruptedException e) {
            logger.error("InterruptedException", e);
            exitCode = -1;
        } finally {
            if (session != null) {
                session.disconnect();
                logger.debug("session.isConnected() = {}", session.isConnected());
            }
        }
        return exitCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getRemoteDirectory() {
        return remoteDirectory;
    }

    public void setRemoteDirectory(String remoteDirectory) {
        this.remoteDirectory = remoteDirectory;
    }

    public List<String> getFileList() {
        return fileList;
    }

    public void setFileList(List<String> fileList) {
        this.fileList = fileList;
    }

}
