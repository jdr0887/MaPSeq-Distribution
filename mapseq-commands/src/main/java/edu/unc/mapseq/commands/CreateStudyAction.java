package edu.unc.mapseq.commands;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Study;

@Command(scope = "mapseq", name = "create-study", description = "Create Study")
public class CreateStudyAction extends AbstractAction {

    private MaPSeqDAOBean maPSeqDAOBean;

    @Argument(index = 0, name = "name", description = "Name", required = true, multiValued = false)
    private String name;

    @Argument(index = 1, name = "approved", description = "Approved", required = true, multiValued = false)
    private Boolean approved;

    public CreateStudyAction() {
        super();
    }

    @Override
    public Object doExecute() {

        try {
            Study study = new Study();
            study.setApproved(approved);
            study.setName(name);
            Long studyId = maPSeqDAOBean.getStudyDAO().save(study);
            return studyId;
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

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

}
