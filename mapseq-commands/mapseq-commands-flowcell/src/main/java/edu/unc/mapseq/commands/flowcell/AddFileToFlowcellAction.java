package edu.unc.mapseq.commands.flowcell;

import java.util.Set;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import edu.unc.mapseq.dao.FileDataDAO;
import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBeanService;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.Flowcell;

@Command(scope = "mapseq", name = "add-file-to-flowcell", description = "Add File to Flowcell")
@Service
public class AddFileToFlowcellAction implements Action {

    @Reference
    private MaPSeqDAOBeanService maPSeqDAOBeanService;

    @Option(name = "--flowcellId", description = "flowcellId", required = false, multiValued = false)
    private Long flowcellId;

    @Option(name = "--fileDataId", description = "fileDataId", required = false, multiValued = false)
    private Long fileDataId;

    public AddFileToFlowcellAction() {
        super();
    }

    @Override
    public Object execute() {

        FlowcellDAO flowcellDAO = maPSeqDAOBeanService.getFlowcellDAO();
        FileDataDAO fileDataDAO = maPSeqDAOBeanService.getFileDataDAO();

        try {
            Flowcell entity = flowcellDAO.findById(flowcellId);
            if (entity == null) {
                System.out.println("Flowcell was not found");
                return null;
            }

            Set<FileData> fileDataSet = entity.getFileDatas();
            FileData fileData = fileDataDAO.findById(fileDataId);
            if (!fileDataSet.contains(fileData)) {
                fileDataSet.add(fileData);
            }
            entity.setFileDatas(fileDataSet);
            flowcellDAO.save(entity);
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

    public Long getFileDataId() {
        return fileDataId;
    }

    public void setFileDataId(Long fileDataId) {
        this.fileDataId = fileDataId;
    }

}
