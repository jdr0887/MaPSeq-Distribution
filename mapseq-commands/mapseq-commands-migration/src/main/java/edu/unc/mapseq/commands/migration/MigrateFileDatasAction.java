package edu.unc.mapseq.commands.migration;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.FileDataDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBeanService;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.StudyDAO;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.Study;

@Command(scope = "mapseq", name = "migrate-file-datas", description = "Migrate FileData instances")
@Service
public class MigrateFileDatasAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(MigrateFileDatasAction.class);

    @Reference
    private MaPSeqDAOBeanService maPSeqDAOBeanService;

    @Override
    public Object execute() {
        logger.debug("ENTERING execute()");
        StudyDAO studyDAO = maPSeqDAOBeanService.getStudyDAO();

        FileDataDAO fileDataDAO = maPSeqDAOBeanService.getFileDataDAO();
        ExecutorService es = Executors.newFixedThreadPool(4);
        try {
            List<FileData> fileDataList = fileDataDAO.findAll();

            List<Study> studyList = studyDAO.findAll();

            for (FileData fileData : fileDataList) {
                es.submit(() -> {
                    for (Study study : studyList) {

                        if (!fileData.getPath().startsWith(String.format("/projects/sequence_analysis/medgenwork/%s", study.getName()))) {
                            continue;
                        }

                        if (fileData.getPath().contains("NCGenesVCFCompare")) {
                            continue;
                        }

                        try {
                            logger.info(fileData.toString());
                            String path = fileData.getPath().replace(
                                    String.format("/projects/sequence_analysis/medgenwork/%s", study.getName()),
                                    String.format("/projects/sequence_analysis/medgenwork/prod/%s", study.getName()));
                            fileData.setPath(path);
                            fileDataDAO.save(fileData);
                            logger.info(fileData.toString());
                        } catch (MaPSeqDAOException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
            es.shutdown();
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
