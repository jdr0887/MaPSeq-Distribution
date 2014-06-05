package edu.unc.mapseq.workflow.model;

import java.util.List;

public class WorkflowMessage {

    private String accountName;

    private List<WorkflowEntity> entities;

    public WorkflowMessage() {
        super();
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public List<WorkflowEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<WorkflowEntity> entities) {
        this.entities = entities;
    }

    @Override
    public String toString() {
        return String.format("WorkflowMessage [accountName=%s]", accountName);
    }

}
