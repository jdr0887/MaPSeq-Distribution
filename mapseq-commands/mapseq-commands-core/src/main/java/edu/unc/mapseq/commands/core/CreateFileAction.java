package edu.unc.mapseq.commands.core;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import edu.unc.mapseq.dao.FileDataDAO;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.MimeType;

@Command(scope = "mapseq", name = "create-file", description = "Create file")
@Service
public class CreateFileAction implements Action {

    @Reference
    private FileDataDAO fileDataDAO;

    @Option(name = "--path", description = "path", required = true, multiValued = false)
    private String path;

    @Option(name = "--name", description = "name", required = true, multiValued = false)
    private String name;

    @Option(name = "--mimeType", description = "mimeType", required = true, multiValued = false)
    private String mimeType;

    public CreateFileAction() {
        super();
    }

    @Override
    public Object execute() {

        try {
            FileData example = new FileData(name, path, MimeType.valueOf(mimeType));
            List<FileData> fileDataSet = fileDataDAO.findByExample(example);
            if (CollectionUtils.isEmpty(fileDataSet)) {
                example.setId(fileDataDAO.save(example));
            } else {
                example = fileDataSet.get(0);
            }
            System.out.println(example.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

}
