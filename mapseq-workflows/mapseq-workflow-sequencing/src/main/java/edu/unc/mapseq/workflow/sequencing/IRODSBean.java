package edu.unc.mapseq.workflow.sequencing;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class IRODSBean {

    private File file;

    private List<ImmutablePair<String, String>> attributes;

    public IRODSBean() {
        super();
    }

    public IRODSBean(File file, List<ImmutablePair<String, String>> attributes) {
        super();
        this.file = file;
        this.attributes = attributes;
    }

    public List<ImmutablePair<String, String>> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<ImmutablePair<String, String>> attributes) {
        this.attributes = attributes;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

}
