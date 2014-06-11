package edu.unc.mapseq.commands;

import java.util.Set;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.EntityAttribute;
import edu.unc.mapseq.dao.model.WorkflowRun;

@Command(scope = "mapseq", name = "create-workflow-run-attribute", description = "Create WorkflowRun Attributes")
public class CreateWorkflowRunAttributeAction extends AbstractAction {

    private final Logger logger = LoggerFactory.getLogger(CreateWorkflowRunAttributeAction.class);

    private MaPSeqDAOBean maPSeqDAOBean;

    @Argument(index = 0, name = "workflowRunId", description = "WorkflowRun Identifier", required = true, multiValued = false)
    private Long workflowRunId;

    @Argument(index = 1, name = "name", description = "the attribute key", required = true, multiValued = false)
    private String name;

    @Argument(index = 2, name = "value", description = "the attribute value", required = true, multiValued = false)
    private String value;

    public CreateWorkflowRunAttributeAction() {
        super();
    }

    @Override
    public Object doExecute() {

        WorkflowRunDAO workflowRunDAO = maPSeqDAOBean.getWorkflowRunDAO();
        WorkflowRun entity = null;
        try {
            entity = workflowRunDAO.findById(workflowRunId);
        } catch (MaPSeqDAOException e) {
        }
        if (entity == null) {
            System.out.println("HTSFSample was not found");
            return null;
        }

        Set<EntityAttribute> attributeSet = entity.getAttributes();
        if (attributeSet != null && attributeSet.size() > 0) {
            attributeSet.add(new EntityAttribute(name, value));
            try {
                workflowRunDAO.save(entity);
            } catch (MaPSeqDAOException e) {
                logger.error("MaPSeqDAOException", e);
            }
        }

        return null;
    }

    public MaPSeqDAOBean getMaPSeqDAOBean() {
        return maPSeqDAOBean;
    }

    public void setMaPSeqDAOBean(MaPSeqDAOBean maPSeqDAOBean) {
        this.maPSeqDAOBean = maPSeqDAOBean;
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
