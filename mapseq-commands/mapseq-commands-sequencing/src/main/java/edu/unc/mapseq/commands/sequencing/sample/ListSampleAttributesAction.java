package edu.unc.mapseq.commands.sequencing.sample;

import java.util.Formatter;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.model.Attribute;
import edu.unc.mapseq.dao.model.Sample;

@Command(scope = "mapseq", name = "list-sample-attributes", description = "List Sample Attributes")
@Service
public class ListSampleAttributesAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(ListSampleAttributesAction.class);

    @Reference
    private SampleDAO sampleDAO;

    @Argument(index = 0, name = "sampleId", description = "Sample Identifier", required = true, multiValued = false)
    private Long sampleId;

    public ListSampleAttributesAction() {
        super();
    }

    @Override
    public Object execute() {
        logger.debug("ENTERING execute()");

        Sample entity = null;
        try {
            entity = sampleDAO.findById(sampleId);
            if (entity == null) {
                System.out.println("Sample was not found");
                return null;
            }

            StringBuilder sb = new StringBuilder();
            String format = "%1$-12s %2$-40s %3$s%n";
            Formatter formatter = new Formatter(sb, Locale.US);
            formatter.format(format, "ID", "Name", "Value");

            Set<Attribute> attributeSet = entity.getAttributes();
            if (CollectionUtils.isNotEmpty(attributeSet)) {
                for (Attribute attribute : attributeSet) {
                    formatter.format(format, attribute.getId(), attribute.getName(), attribute.getValue());
                    formatter.flush();
                }
            }
            System.out.println(formatter.toString());
            formatter.close();
        } catch (MaPSeqDAOException e) {
        }

        return null;
    }

    public Long getSampleId() {
        return sampleId;
    }

    public void setSampleId(Long sampleId) {
        this.sampleId = sampleId;
    }

}
