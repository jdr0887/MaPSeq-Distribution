package edu.unc.mapseq.commands;

import java.util.Set;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.HTSFSampleDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.EntityAttribute;
import edu.unc.mapseq.dao.model.HTSFSample;

@Command(scope = "mapseq", name = "create-htsf-sample-attribute", description = "Create HTSFSample Attributes")
public class CreateHTSFSampleAttributeAction extends AbstractAction {

    private final Logger logger = LoggerFactory.getLogger(CreateHTSFSampleAttributeAction.class);

    private MaPSeqDAOBean maPSeqDAOBean;

    @Argument(index = 0, name = "htsfSampleId", description = "HTSFSample Identifier", required = true, multiValued = false)
    private Long htsfSampleId;

    @Argument(index = 1, name = "name", description = "the attribute key", required = true, multiValued = false)
    private String name;

    @Argument(index = 2, name = "value", description = "the attribute value", required = true, multiValued = false)
    private String value;

    public CreateHTSFSampleAttributeAction() {
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

        Set<EntityAttribute> attributeSet = entity.getAttributes();
        if (attributeSet != null && attributeSet.size() > 0) {
            attributeSet.add(new EntityAttribute(name, value));
            try {
                htsfSampleDAO.save(entity);
            } catch (MaPSeqDAOException e) {
                logger.error("MaPSeqDAOException", e);
            }
        }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
