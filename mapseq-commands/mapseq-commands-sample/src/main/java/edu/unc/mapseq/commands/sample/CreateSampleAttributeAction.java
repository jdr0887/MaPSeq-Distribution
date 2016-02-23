package edu.unc.mapseq.commands.sample;

import java.util.Set;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.AttributeDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBeanService;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.model.Attribute;
import edu.unc.mapseq.dao.model.Sample;

@Command(scope = "mapseq", name = "create-sample-attribute", description = "Create Sample Attribute")
@Service
public class CreateSampleAttributeAction implements Action {

    private final Logger logger = LoggerFactory.getLogger(CreateSampleAttributeAction.class);

    @Reference
    private MaPSeqDAOBeanService maPSeqDAOBeanService;

    @Argument(index = 0, name = "sampleId", description = "Sample Identifier", required = true, multiValued = false)
    private Long sampleId;

    @Argument(index = 1, name = "name", description = "the attribute key", required = true, multiValued = false)
    private String name;

    @Argument(index = 2, name = "value", description = "the attribute value", required = true, multiValued = false)
    private String value;

    public CreateSampleAttributeAction() {
        super();
    }

    @Override
    public Object execute() {

        AttributeDAO attributeDAO = maPSeqDAOBeanService.getAttributeDAO();
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
        if (attributeSet != null) {
            try {
                Attribute attribute = new Attribute(name, value);
                attribute.setId(attributeDAO.save(attribute));
                attributeSet.add(attribute);
                sampleDAO.save(entity);
            } catch (MaPSeqDAOException e) {
                logger.error("MaPSeqDAOException", e);
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
