package edu.unc.mapseq.main;

import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public abstract class AbstractTransferFiles implements Callable<Integer> {

    protected final static HelpFormatter helpFormatter = new HelpFormatter();

    protected final static Options cliOptions = new Options();

    private String username;

    private String host;

    private String remoteDirectory;

    private List<String> fileList;

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
