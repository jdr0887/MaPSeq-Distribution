package edu.unc.mapseq.commands.workflow;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import edu.unc.mapseq.dao.MaPSeqDAOBeanService;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Workflow;

@Command(scope = "mapseq", name = "create-workflow", description = "Create Workflow")
@Service
public class CreateWorkflowAction implements Action {

    @Reference
    private MaPSeqDAOBeanService maPSeqDAOBeanService;

    @Argument(index = 0, name = "name", description = "Name", required = true, multiValued = false)
    private String name;

    public CreateWorkflowAction() {
        super();
    }

    @Override
    public Object execute() {
        try {
            Workflow workflow = new Workflow(name);
            workflow.setId(maPSeqDAOBeanService.getWorkflowDAO().save(workflow));
            System.out.println(workflow.toString());
        } catch (MaPSeqDAOException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
