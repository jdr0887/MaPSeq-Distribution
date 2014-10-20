package edu.unc.mapseq.commands;

import java.text.DateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.commands.Option;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.FileDataDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.FileData;

@Command(scope = "mapseq", name = "find-files", description = "Find Files")
public class FindFilesAction extends AbstractAction {

    private MaPSeqDAOBean maPSeqDAOBean;

    @Option(name = "--path", description = "path", required = false, multiValued = false)
    private String path;

    @Option(name = "--name", description = "name", required = false, multiValued = false)
    private String name;

    public FindFilesAction() {
        super();
    }

    @Override
    public Object doExecute() {

        FileDataDAO fileDataDAO = maPSeqDAOBean.getFileDataDAO();
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
            if (fileDataSet != null && !fileDataSet.isEmpty()) {
                for (FileData fileData : fileDataSet) {
                    Date created = fileData.getCreated();
                    String formattedCreated = "";
                    if (created != null) {
                        formattedCreated = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(
                                created);
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

    public MaPSeqDAOBean getMaPSeqDAOBean() {
        return maPSeqDAOBean;
    }

    public void setMaPSeqDAOBean(MaPSeqDAOBean maPSeqDAOBean) {
        this.maPSeqDAOBean = maPSeqDAOBean;
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
