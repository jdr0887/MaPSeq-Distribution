package edu.unc.mapseq.commands.flowcell;

import java.text.DateFormat;
import java.util.Date;
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

import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Attribute;
import edu.unc.mapseq.dao.model.Flowcell;

@Command(scope = "mapseq", name = "list-flowcell-attributes", description = "List Flowcell Attributes")
@Service
public class ListFlowcellAttributesAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(ListFlowcellAttributesAction.class);

    @Reference
    private FlowcellDAO flowcellDAO;

    @Argument(index = 0, name = "flowcellId", description = "Flowcell Identifier", required = true, multiValued = false)
    private Long flowcellId;

    public ListFlowcellAttributesAction() {
        super();
    }

    @Override
    public Object execute() {
        logger.debug("ENTERING execute()");
        StringBuilder sb = new StringBuilder();
        try (Formatter formatter = new Formatter(sb, Locale.US)) {
            Flowcell entity = flowcellDAO.findById(flowcellId);
            if (entity == null) {
                System.out.println("Flowcell was not found");
                return null;
            }

            String format = "%1$-12s %2$-24s %3$s%n";
            formatter.format(format, "ID", "Name", "Value");

            Set<Attribute> attributeSet = entity.getAttributes();
            if (CollectionUtils.isEmpty(attributeSet)) {
                System.out.println("No Attibutes found");
                return null;
            }

            for (Attribute attribute : attributeSet) {
                formatter.format(format, attribute.getId(), attribute.getName(), attribute.getValue());
                formatter.flush();
            }
            System.out.println(formatter.toString());
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Long getFlowcellId() {
        return flowcellId;
    }

    public void setFlowcellId(Long flowcellId) {
        this.flowcellId = flowcellId;
    }

}
