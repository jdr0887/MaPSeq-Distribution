package edu.unc.mapseq.commands.flowcell;

import java.text.DateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.Set;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBeanService;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.Flowcell;

@Command(scope = "mapseq", name = "list-flowcell-files", description = "List Flowcell Files")
@Service
public class ListFlowcellFilesAction implements Action {

    @Reference
    private MaPSeqDAOBeanService maPSeqDAOBeanService;

    @Argument(index = 0, name = "flowcellId", description = "Flowcell Identifier", required = true, multiValued = false)
    private Long flowcellId;

    public ListFlowcellFilesAction() {
        super();
    }

    @Override
    public Object execute() {

        FlowcellDAO flowcellDAO = maPSeqDAOBeanService.getFlowcellDAO();
        Flowcell entity = null;
        try {
            entity = flowcellDAO.findById(flowcellId);
        } catch (MaPSeqDAOException e) {
        }
        if (entity == null) {
            System.out.println("Flowcell was not found");
            return null;
        }

        String format = "%1$-12s %2$-20s %3$-24s %4$-80s %5$s%n";
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);
        formatter.format(format, "ID", "Created", "MimeType", "Name", "Path");

        Set<FileData> fileDataSet = entity.getFileDatas();
        if (fileDataSet != null && !fileDataSet.isEmpty()) {
            for (FileData fileData : fileDataSet) {

                Date created = fileData.getCreated();
                String formattedCreated = "";
                if (created != null) {
                    formattedCreated = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                            .format(created);
                }

                formatter.format(format, fileData.getId(), formattedCreated, fileData.getMimeType(), fileData.getName(),
                        fileData.getPath());
                formatter.flush();
            }
        }
        System.out.println(formatter.toString());
        formatter.close();

        return null;
    }

    public Long getFlowcellId() {
        return flowcellId;
    }

    public void setFlowcellId(Long flowcellId) {
        this.flowcellId = flowcellId;
    }

}
