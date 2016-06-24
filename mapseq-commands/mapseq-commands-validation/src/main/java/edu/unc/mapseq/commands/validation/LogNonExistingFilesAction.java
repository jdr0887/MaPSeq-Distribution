package edu.unc.mapseq.commands.validation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.FileDataDAO;
import edu.unc.mapseq.dao.model.FileData;

@Command(scope = "mapseq", name = "log-non-existing-files", description = "Log non existing files")
@Service
public class LogNonExistingFilesAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(LogNonExistingFilesAction.class);

    @Reference
    private FileDataDAO fileDataDAO;

    public LogNonExistingFilesAction() {
        super();
    }

    @Override
    public Object execute() {
        logger.debug("ENTERING execute()");
        File output = new File("/tmp", "mapseq-non-existing-files.txt");
        try (FileWriter fw = new FileWriter(output); BufferedWriter bw = new BufferedWriter(fw)) {
            List<FileData> fileDataList = fileDataDAO.findAll();
            fileDataList.forEach(a -> {
                try {
                    File f = a.toFile();
                    if (!f.exists()) {
                        bw.write(a.toString());
                        bw.newLine();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
