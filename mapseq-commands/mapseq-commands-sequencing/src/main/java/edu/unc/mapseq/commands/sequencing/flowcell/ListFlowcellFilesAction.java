package edu.unc.mapseq.commands.sequencing.flowcell;

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
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.Flowcell;

@Command(scope = "mapseq", name = "list-flowcell-files", description = "List Flowcell Files")
@Service
public class ListFlowcellFilesAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(ListFlowcellFilesAction.class);

    @Reference
    private FlowcellDAO flowcellDAO;

    @Argument(index = 0, name = "flowcellId", description = "Flowcell Identifier", required = true, multiValued = false)
    private Long flowcellId;

    public ListFlowcellFilesAction() {
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

            String format = "%1$-12s %2$-20s %3$-24s %4$-80s %5$s%n";
            formatter.format(format, "ID", "Created", "MimeType", "Name", "Path");

            Set<FileData> fileDataSet = entity.getFileDatas();
            if (CollectionUtils.isNotEmpty(fileDataSet)) {
                for (FileData fileData : fileDataSet) {

                    Date created = fileData.getCreated();
                    String formattedCreated = "";
                    if (created != null) {
                        formattedCreated = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                                .format(created);
                    }

                    formatter.format(format, fileData.getId(), formattedCreated, fileData.getMimeType(),
                            fileData.getName(), fileData.getPath());
                    formatter.flush();
                }
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
