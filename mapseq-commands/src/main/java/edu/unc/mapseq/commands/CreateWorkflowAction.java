package edu.unc.mapseq.commands;

import java.util.Date;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Workflow;

@Command(scope = "mapseq", name = "create-workflow", description = "Create Workflow")
public class CreateWorkflowAction extends AbstractAction {

    private MaPSeqDAOBean mapseqDAOBean;

    @Argument(index = 0, name = "name", description = "Name", required = true, multiValued = false)
    private String name;

    public CreateWorkflowAction() {
        super();
    }

    @Override
    public Object doExecute() {
        Date d = new Date();
        try {
            Workflow workflow = new Workflow();
            workflow.setCreationDate(d);
            workflow.setModificationDate(d);
            workflow.setCreator(mapseqDAOBean.getAccountDAO().findByName(System.getProperty("user.name")));
            workflow.setName(name);
            Long workflowId = mapseqDAOBean.getWorkflowDAO().save(workflow);
            return workflowId;
        } catch (MaPSeqDAOException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    public MaPSeqDAOBean getMapseqDAOBean() {
        return mapseqDAOBean;
    }

    public void setMapseqDAOBean(MaPSeqDAOBean mapseqDAOBean) {
        this.mapseqDAOBean = mapseqDAOBean;
    }

}
