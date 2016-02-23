package edu.unc.mapseq.commands.study;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import edu.unc.mapseq.dao.MaPSeqDAOBeanService;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Study;

@Command(scope = "mapseq", name = "create-study", description = "Create Study")
@Service
public class CreateStudyAction implements Action {

    @Reference
    private MaPSeqDAOBeanService maPSeqDAOBeanService;

    @Argument(index = 0, name = "name", description = "Name", required = true, multiValued = false)
    private String name;

    public CreateStudyAction() {
        super();
    }

    @Override
    public Object execute() {
        try {
            Study study = new Study(name);
            study.setId(maPSeqDAOBeanService.getStudyDAO().save(study));
            System.out.println(study.toString());
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
