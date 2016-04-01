package edu.unc.mapseq.commands.core;

import java.text.DateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import edu.unc.mapseq.dao.FileDataDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.FileData;

@Command(scope = "mapseq", name = "find-files", description = "Find Files")
@Service
public class FindFilesAction implements Action {

    @Reference
    private FileDataDAO fileDataDAO;

    @Option(name = "--path", description = "path", required = false, multiValued = false)
    private String path;

    @Option(name = "--name", description = "name", required = false, multiValued = false)
    private String name;

    public FindFilesAction() {
        super();
    }

    @Override
    public Object execute() {

        FileData example = new FileData();

        if (StringUtils.isEmpty(name) && StringUtils.isEmpty(path)) {
            System.out.println("both name and path can't be null/empty");
            return null;
        }

        if (StringUtils.isNotEmpty(name)) {
            example.setName(name);
        }

        if (StringUtils.isNotEmpty(path)) {
            example.setPath(path);
        }

        StringBuilder sb = new StringBuilder();
        String format = "%1$-12s %2$-20s %3$-24s %4$-80s %5$s%n";
        Formatter formatter = new Formatter(sb, Locale.US);
        formatter.format(format, "ID", "Created", "MimeType", "Path", "Name");

        try {
            List<FileData> fileDataSet = fileDataDAO.findByExample(example);
            if (CollectionUtils.isNotEmpty(fileDataSet)) {
                for (FileData fileData : fileDataSet) {
                    Date created = fileData.getCreated();
                    String formattedCreated = "";
                    if (created != null) {
                        formattedCreated = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                                .format(created);
                    }
                    formatter.format(format, fileData.getId(), formattedCreated, fileData.getMimeType(),
                            fileData.getPath(), fileData.getName());
                    formatter.flush();
                }
            }
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        System.out.println(formatter.toString());
        formatter.close();

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

}
