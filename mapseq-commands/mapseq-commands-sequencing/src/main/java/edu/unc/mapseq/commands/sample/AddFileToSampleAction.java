package edu.unc.mapseq.commands.sample;

import java.util.Set;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.FileDataDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.Sample;

@Command(scope = "mapseq", name = "add-file-to-sample", description = "Add File to Sample")
@Service
public class AddFileToSampleAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(AddFileToSampleAction.class);

    @Reference
    private SampleDAO sampleDAO;

    @Reference
    private FileDataDAO fileDataDAO;

    @Option(name = "--sampleId", description = "sampleId", required = false, multiValued = false)
    private Long sampleId;

    @Option(name = "--fileDataId", description = "fileDataId", required = false, multiValued = false)
    private Long fileDataId;

    public AddFileToSampleAction() {
        super();
    }

    @Override
    public Object execute() {
        logger.debug("ENTERING execute()");

        try {
            Sample entity = sampleDAO.findById(sampleId);
            if (entity == null) {
                System.out.println("Sample was not found");
                return null;
            }

            Set<FileData> fileDataSet = entity.getFileDatas();
            FileData fileData = fileDataDAO.findById(fileDataId);
            if (!fileDataSet.contains(fileData)) {
                fileDataSet.add(fileData);
            }
            entity.setFileDatas(fileDataSet);
            sampleDAO.save(entity);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Long getSampleId() {
        return sampleId;
    }

    public void setSampleId(Long sampleId) {
        this.sampleId = sampleId;
    }

    public Long getFileDataId() {
        return fileDataId;
    }

    public void setFileDataId(Long fileDataId) {
        this.fileDataId = fileDataId;
    }

}
