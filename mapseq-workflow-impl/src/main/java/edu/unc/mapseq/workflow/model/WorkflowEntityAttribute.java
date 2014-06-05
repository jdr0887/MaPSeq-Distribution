package edu.unc.mapseq.workflow.model;

public class WorkflowEntityAttribute {

    private String name;

    private String value;

    public WorkflowEntityAttribute() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("WorkflowEntityAttribute [name=%s, value=%s]", name, value);
    }

}
