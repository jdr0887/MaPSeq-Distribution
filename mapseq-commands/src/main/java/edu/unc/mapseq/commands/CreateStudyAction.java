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
        try {
            Study study = new Study();
            study.setApproved(approved);
            study.setCreator(maPSeqDAOBean.getAccountDAO().findByName(System.getProperty("user.name")));
            study.setGrant(grant);
            study.setName(name);
            if (primaryContactId != null) {
                study.setPrimaryContact(maPSeqDAOBean.getAccountDAO().findById(primaryContactId));
            }
            if (principalInvestigatorId != null) {
                study.setPrincipalInvestigator(maPSeqDAOBean.getAccountDAO().findById(principalInvestigatorId));
            }
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

    public String getGrant() {
        return grant;
    }

    public void setGrant(String grant) {
        this.grant = grant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPrimaryContactId() {
        return primaryContactId;
    }

    public void setPrimaryContactId(Long primaryContactId) {
        this.primaryContactId = primaryContactId;
    }

    public Long getPrincipalInvestigatorId() {
        return principalInvestigatorId;
    }

    public void setPrincipalInvestigatorId(Long principalInvestigatorId) {
        this.principalInvestigatorId = principalInvestigatorId;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

}
