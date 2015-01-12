package edu.unc.mapseq.commands.sample;

import java.util.Set;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.model.Attribute;
import edu.unc.mapseq.dao.model.Sample;

@Command(scope = "mapseq", name = "edit-sample-attribute", description = "Edit Sample Attributes")
public class EditSampleAttributeAction extends AbstractAction {

    private final Logger logger = LoggerFactory.getLogger(EditSampleAttributeAction.class);

    private MaPSeqDAOBean maPSeqDAOBean;

    @Argument(index = 0, name = "sampleId", description = "Sample Identifier", required = true, multiValued = false)
    private Long sampleId;

    @Argument(index = 1, name = "name", description = "the attribute key", required = true, multiValued = false)
    private String name;

    @Argument(index = 2, name = "value", description = "the attribute value", required = true, multiValued = false)
    private String value;

    public EditSampleAttributeAction() {
        super();
    }

    @Override
    public Object doExecute() {

        SampleDAO sampleDAO = maPSeqDAOBean.getSampleDAO();
        Sample entity = null;
        try {
            entity = sampleDAO.findById(sampleId);
        } catch (MaPSeqDAOException e) {
        }

        if (entity == null) {
            System.out.println("Sample was not found");
            return null;
        }

        Set<Attribute> attributeSet = entity.getAttributes();
        if (attributeSet != null && !attributeSet.isEmpty()) {
            for (Attribute attribute : attributeSet) {
                if (attribute.getName().equals(name)) {
                    attribute.setValue(value);
                    try {
                        maPSeqDAOBean.getAttributeDAO().save(attribute);
                    } catch (MaPSeqDAOException e) {
                        logger.error("MaPSeqDAOException", e);
                    }
                    break;
                }
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

    public Long getSampleId() {
        return sampleId;
    }

    public void setSampleId(Long sampleId) {
        this.sampleId = sampleId;
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
