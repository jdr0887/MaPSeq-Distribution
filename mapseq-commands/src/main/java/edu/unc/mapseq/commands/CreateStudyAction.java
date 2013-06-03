package edu.unc.mapseq.commands;

import java.util.Date;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Study;

@Command(scope = "mapseq", name = "create-study", description = "Create Study")
public class CreateStudyAction extends AbstractAction {

    private MaPSeqDAOBean mapseqDAOBean;

    @Argument(index = 0, name = "grant", description = "Grant", required = true, multiValued = false)
    private String grant;

    @Argument(index = 1, name = "name", description = "Name", required = true, multiValued = false)
    private String name;

    @Argument(index = 2, name = "primaryContactId", description = "Primary Contact Id", required = true, multiValued = false)
    private Long primaryContactId;

    @Argument(index = 3, name = "principalInvestigatorId", description = "Principal Investigator Id", required = true, multiValued = false)
    private Long principalInvestigatorId;

    @Argument(index = 4, name = "approved", description = "Approved", required = true, multiValued = false)
    private Boolean approved;

    public CreateStudyAction() {
        super();
    }

    @Override
    public Object doExecute() {
        Date d = new Date();
        try {
            Study study = new Study();
            study.setCreationDate(d);
            study.setModificationDate(d);
            study.setApproved(approved);
            study.setCreator(mapseqDAOBean.getAccountDAO().findByName(System.getProperty("user.name")));
            study.setGrant(grant);
            study.setName(name);
            if (primaryContactId != null) {
                study.setPrimaryContact(mapseqDAOBean.getAccountDAO().findById(primaryContactId));
            }
            if (principalInvestigatorId != null) {
                study.setPrincipalInvestigator(mapseqDAOBean.getAccountDAO().findById(principalInvestigatorId));
            }
            Long studyId = mapseqDAOBean.getStudyDAO().save(study);
            return studyId;
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
