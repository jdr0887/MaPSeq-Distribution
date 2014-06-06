package edu.unc.mapseq.workflow.impl;

import java.io.File;

import edu.unc.mapseq.config.RunModeType;

public class IRODSBean {

    private File file;

    private String type;

    private String version;

    private String dx;

    private RunModeType runMode;

    public IRODSBean() {
        super();
    }

    public IRODSBean(File file, String type, String version, String dx, RunModeType runMode) {
        super();
        this.file = file;
        this.type = type;
        this.version = version;
        this.dx = dx;
        this.runMode = runMode;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDx() {
        return dx;
    }

    public void setDx(String dx) {
        this.dx = dx;
    }

    public RunModeType getRunMode() {
        return runMode;
    }

    public void setRunMode(RunModeType runMode) {
        this.runMode = runMode;
    }

}
