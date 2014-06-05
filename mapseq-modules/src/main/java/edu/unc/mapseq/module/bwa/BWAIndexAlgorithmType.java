package edu.unc.mapseq.module.bwa;

public enum BWAIndexAlgorithmType {

    IS("is"),

    BWTSW("bwtsw");

    private String value;

    private BWAIndexAlgorithmType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
