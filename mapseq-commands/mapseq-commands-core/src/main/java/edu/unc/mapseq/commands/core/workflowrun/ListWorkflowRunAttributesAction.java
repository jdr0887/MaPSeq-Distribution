package edu.unc.mapseq.commands.core.workflowrun;

import java.util.Formatter;
import java.util.Locale;
import java.util.Set;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.Attribute;
import edu.unc.mapseq.dao.model.WorkflowRun;

@Command(scope = "mapseq", name = "list-workflow-run-attributes", description = "List WorkflowRun Attributes")
@Service
public class ListWorkflowRunAttributesAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(ListWorkflowRunAttributesAction.class);

    @Reference
    private WorkflowRunDAO workflowRunDAO;

    @Argument(index = 0, name = "workflowRunId", description = "WorkflowRun Identifier", required = true, multiValued = false)
    private Long workflowRunId;

    public ListWorkflowRunAttributesAction() {
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

            StringBuilder sb = new StringBuilder();
            String format = "%1$-12s %2$-40s %3$s%n";

            Formatter formatter = new Formatter(sb, Locale.US);
            formatter.format(format, "ID", "Name", "Value");

            Set<Attribute> attributeSet = entity.getAttributes();
            if (attributeSet != null && !attributeSet.isEmpty()) {
                for (Attribute attribute : attributeSet) {
                    formatter.format(format, attribute.getId(), attribute.getName(), attribute.getValue());
                    formatter.flush();
                }
            }
            System.out.println(formatter.toString());
            formatter.close();
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

}
