package edu.unc.mapseq.commands.sample;

import java.util.Set;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOBeanService;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.model.Attribute;
import edu.unc.mapseq.dao.model.Sample;

@Command(scope = "mapseq", name = "edit-sample-attribute", description = "Edit Sample Attributes")
@Service
public class EditSampleAttributeAction implements Action {

    private final Logger logger = LoggerFactory.getLogger(EditSampleAttributeAction.class);

    @Reference
    private MaPSeqDAOBeanService maPSeqDAOBeanService;

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
    public Object execute() {

        SampleDAO sampleDAO = maPSeqDAOBeanService.getSampleDAO();
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
                        maPSeqDAOBeanService.getAttributeDAO().save(attribute);
                    } catch (MaPSeqDAOException e) {
                        logger.error("MaPSeqDAOException", e);
                    }
                    break;
                }
            }
        }

        return null;
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
