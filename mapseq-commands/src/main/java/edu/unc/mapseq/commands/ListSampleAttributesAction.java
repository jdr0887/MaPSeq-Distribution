package edu.unc.mapseq.commands;

import java.util.Formatter;
import java.util.Locale;
import java.util.Set;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.HTSFSampleDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.EntityAttribute;
import edu.unc.mapseq.dao.model.HTSFSample;

@Command(scope = "mapseq", name = "list-htsf-sample-attributes", description = "List HTSFSample Attributes")
public class ListHTSFSampleAttributesAction extends AbstractAction {

    private MaPSeqDAOBean maPSeqDAOBean;

    @Argument(index = 0, name = "htsfSampleId", description = "HTSFSample Identifier", required = true, multiValued = false)
    private Long htsfSampleId;

    public ListHTSFSampleAttributesAction() {
        super();
    }

    @Override
    public Object doExecute() {

        HTSFSampleDAO htsfSampleDAO = maPSeqDAOBean.getHTSFSampleDAO();
        HTSFSample entity = null;
        try {
            entity = htsfSampleDAO.findById(htsfSampleId);
        } catch (MaPSeqDAOException e) {
        }
        if (entity == null) {
            System.out.println("HTSFSample was not found");
            return null;
        }

        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);
        formatter.format("%1$-12s %2$-40s %3$s%n", "Attribute ID", "Name", "Value");

        Set<EntityAttribute> attributeSet = entity.getAttributes();
        if (attributeSet != null && attributeSet.size() > 0) {
            for (EntityAttribute attribute : attributeSet) {
                formatter
                        .format("%1$-12s %2$-40s %3$s%n", attribute.getId(), attribute.getName(), attribute.getValue());
                formatter.flush();
            }
        }
        System.out.println(formatter.toString());
        formatter.close();

        return null;
    }

    public MaPSeqDAOBean getMaPSeqDAOBean() {
        return maPSeqDAOBean;
    }

    public void setMaPSeqDAOBean(MaPSeqDAOBean maPSeqDAOBean) {
        this.maPSeqDAOBean = maPSeqDAOBean;
    }

    public Long getHtsfSampleId() {
        return htsfSampleId;
    }

    public void setHtsfSampleId(Long htsfSampleId) {
        this.htsfSampleId = htsfSampleId;
    }

}
