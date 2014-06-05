package edu.unc.mapseq.workflow.model;

import java.util.List;

public class WorkflowEntity {

    private String entityType;

    private Long guid;

    private String name;

    private List<WorkflowEntityAttribute> attributes;

    public WorkflowEntity() {
        super();
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getGuid() {
        return guid;
    }

    public void setGuid(Long guid) {
        this.guid = guid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<WorkflowEntityAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<WorkflowEntityAttribute> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return String.format("WorkflowEntity [entityType=%s, guid=%s, name=%s]", entityType, guid, name);
    }

}
