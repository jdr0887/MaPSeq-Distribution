package edu.unc.mapseq.commands.study;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Study;

@Command(scope = "mapseq", name = "create-study", description = "Create Study")
public class CreateStudyAction extends AbstractAction {

    private MaPSeqDAOBean maPSeqDAOBean;

    @Argument(index = 0, name = "name", description = "Name", required = true, multiValued = false)
    private String name;

    public CreateStudyAction() {
        super();
    }

    @Override
    public Object doExecute() {

        try {
            Study study = new Study();
            study.setName(name);
            Long studyId = maPSeqDAOBean.getStudyDAO().save(study);
            study.setId(studyId);
            System.out.println(study.toString());
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
