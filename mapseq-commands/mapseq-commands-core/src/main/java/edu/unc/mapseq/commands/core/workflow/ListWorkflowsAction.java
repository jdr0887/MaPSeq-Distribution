package edu.unc.mapseq.commands.core.workflow;

import java.text.DateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.WorkflowDAO;
import edu.unc.mapseq.dao.model.Workflow;

@Command(scope = "mapseq", name = "list-workflows", description = "List Workflows")
@Service
public class ListWorkflowsAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(ListWorkflowsAction.class);

    @Reference
    private WorkflowDAO workflowDAO;

    public ListWorkflowsAction() {
        super();
    }

    @Override
    public Object execute() {
        logger.debug("ENTERING execute()");

        try {
            List<Workflow> workflowList = workflowDAO.findAll();

            if (CollectionUtils.isNotEmpty(workflowList)) {
                StringBuilder sb = new StringBuilder();
                String format = "%1$-12s %2$-20s %3$-30s %4$s%n";
                Formatter formatter = new Formatter(sb, Locale.US);
                formatter.format(format, "ID", "Created", "Name", "System");
                for (Workflow workflow : workflowList) {

                    Date createdDate = workflow.getCreated();
                    String formattedCreatedDate = "";
                    if (createdDate != null) {
                        formattedCreatedDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(createdDate);
                    }

                    formatter.format(format, workflow.getId(), formattedCreatedDate, workflow.getName(), workflow.getSystem().getValue());
                    formatter.flush();
                }
                System.out.println(formatter.toString());
                formatter.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
