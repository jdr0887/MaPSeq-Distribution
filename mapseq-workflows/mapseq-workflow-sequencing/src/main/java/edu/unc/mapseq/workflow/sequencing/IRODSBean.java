package edu.unc.mapseq.workflow.sequencing;

import java.io.File;

public class IRODSBean {

    private File file;

    private String type;

    private String version;

    private String dx;

    public IRODSBean() {
        super();
    }

    public IRODSBean(File file, String type, String version, String dx) {
        super();
        this.file = file;
        this.type = type;
        this.version = version;
        this.dx = dx;
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

}
