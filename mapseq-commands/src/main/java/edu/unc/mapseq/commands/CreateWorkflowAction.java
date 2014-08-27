package edu.unc.mapseq.commands;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Workflow;

@Command(scope = "mapseq", name = "create-workflow", description = "Create Workflow")
public class CreateWorkflowAction extends AbstractAction {

    private MaPSeqDAOBean maPSeqDAOBean;

    @Argument(index = 0, name = "name", description = "Name", required = true, multiValued = false)
    private String name;

    public CreateWorkflowAction() {
        super();
    }

    @Override
    public Object doExecute() {
        try {
            Workflow workflow = new Workflow();
            workflow.setName(name);
            Long workflowId = maPSeqDAOBean.getWorkflowDAO().save(workflow);
            workflow.setId(workflowId);
            System.out.println(workflow.toString());
        } catch (MaPSeqDAOException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    public MaPSeqDAOBean getMaPSeqDAOBean() {
        return maPSeqDAOBean;
    }

    public void setMaPSeqDAOBean(MaPSeqDAOBean maPSeqDAOBean) {
        this.maPSeqDAOBean = maPSeqDAOBean;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
