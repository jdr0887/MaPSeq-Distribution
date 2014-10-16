package edu.unc.mapseq.commands;

import java.util.Formatter;
import java.util.Locale;
import java.util.Set;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.Flowcell;

@Command(scope = "mapseq", name = "list-flowcell-files", description = "List Flowcell Files")
public class ListFlowcellFilesAction extends AbstractAction {

    private MaPSeqDAOBean maPSeqDAOBean;

    @Argument(index = 0, name = "flowcellId", description = "Flowcell Identifier", required = true, multiValued = false)
    private Long flowcellId;

    public ListFlowcellFilesAction() {
        super();
    }

    @Override
    public Object doExecute() {

        FlowcellDAO flowcellDAO = maPSeqDAOBean.getFlowcellDAO();
        Flowcell entity = null;
        try {
            entity = flowcellDAO.findById(flowcellId);
        } catch (MaPSeqDAOException e) {
        }
        if (entity == null) {
            System.out.println("Flowcell was not found");
            return null;
        }

        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);
        formatter.format("%1$-9s %2$-24s %3$-80s %4$s%n", "ID", "MimeType", "Path", "Name");

        Set<FileData> fileDataSet = entity.getFileDatas();
        if (fileDataSet != null && !fileDataSet.isEmpty()) {
            for (FileData fileData : fileDataSet) {
                formatter.format("%1$-9s %2$-24s %3$-80s %4$s%n", fileData.getId(), fileData.getMimeType(),
                        fileData.getPath(), fileData.getName());
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

    public Long getFlowcellId() {
        return flowcellId;
    }

    public void setFlowcellId(Long flowcellId) {
        this.flowcellId = flowcellId;
    }

}
