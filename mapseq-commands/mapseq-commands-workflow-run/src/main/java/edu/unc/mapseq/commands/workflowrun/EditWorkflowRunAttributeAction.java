package edu.unc.mapseq.commands.workflowrun;

import java.util.Set;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.AttributeDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.Attribute;
import edu.unc.mapseq.dao.model.WorkflowRun;

@Command(scope = "mapseq", name = "edit-workflow-run-attribute", description = "Edit WorkflowRun Attribute")
@Service
public class EditWorkflowRunAttributeAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(EditWorkflowRunAttributeAction.class);

    @Reference
    private WorkflowRunDAO workflowRunDAO;

    @Reference
    private AttributeDAO attributeDAO;

    @Argument(index = 0, name = "workflowRunId", description = "WorkflowRun Identifier", required = true, multiValued = false)
    private Long workflowRunId;

    @Argument(index = 1, name = "name", description = "the attribute key", required = true, multiValued = false)
    private String name;

    @Argument(index = 2, name = "value", description = "the attribute value", required = true, multiValued = false)
    private String value;

    public EditWorkflowRunAttributeAction() {
        super();
    }

    @Override
    public Object execute() {
        logger.debug("ENTERING execute()");

        try {
            WorkflowRun entity = workflowRunDAO.findById(workflowRunId);
            if (entity == null) {
                System.out.println("WorkflowRun was not found");
                return null;
            }

            Set<Attribute> attributeSet = entity.getAttributes();
            if (attributeSet != null && attributeSet.size() > 0) {
                for (Attribute attribute : attributeSet) {
                    if (attribute.getName().equals(name)) {
                        attribute.setValue(value);
                        try {
                            attributeDAO.save(attribute);
                        } catch (MaPSeqDAOException e) {
                            logger.error("MaPSeqDAOException", e);
                        }
                        break;
                    }
                }
            }
        } catch (MaPSeqDAOException e) {
        }

        return null;
    }

    public Long getWorkflowRunId() {
        return workflowRunId;
    }

    public void setWorkflowRunId(Long workflowRunId) {
        this.workflowRunId = workflowRunId;
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

}
