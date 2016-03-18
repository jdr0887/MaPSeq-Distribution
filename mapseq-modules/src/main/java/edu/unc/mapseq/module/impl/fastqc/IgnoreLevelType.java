package edu.unc.mapseq.module.impl.fastqc;

public enum IgnoreLevelType {

    NONE(false, false),

    WARNING(true, false),

    ERROR(true, true);

    private boolean ignoreWarning;

    private boolean ignoreError;

    private IgnoreLevelType(boolean ignoreWarning, boolean ignoreError) {
        this.ignoreWarning = ignoreWarning;
        this.ignoreError = ignoreError;
    }

    public boolean isIgnoreWarning() {
        return ignoreWarning;
    }

    public void setIgnoreWarning(boolean ignoreWarning) {
        this.ignoreWarning = ignoreWarning;
    }

    public boolean isIgnoreError() {
        return ignoreError;
    }

    public void setIgnoreError(boolean ignoreError) {
        this.ignoreError = ignoreError;
    }

}
