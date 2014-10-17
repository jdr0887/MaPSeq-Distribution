package edu.unc.mapseq.commands;

import java.text.DateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.Set;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.Sample;

@Command(scope = "mapseq", name = "list-sample-files", description = "List Sample Files")
public class ListSampleFilesAction extends AbstractAction {

    private MaPSeqDAOBean maPSeqDAOBean;

    @Argument(index = 0, name = "sampleId", description = "Sample Identifier", required = true, multiValued = false)
    private Long sampleId;

    public ListSampleFilesAction() {
        super();
    }

    @Override
    public Object doExecute() {

        SampleDAO sampleDAO = maPSeqDAOBean.getSampleDAO();
        Sample entity = null;
        try {
            entity = sampleDAO.findById(sampleId);
        } catch (MaPSeqDAOException e) {
        }
        if (entity == null) {
            System.out.println("Sample was not found");
            return null;
        }

        String format = "%1$-12s %2$-20s %3$-24s %4$-80s %5$s%n";
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);
        formatter.format(format, "ID", "Created", "MimeType", "Path", "Name");

        Set<FileData> fileDataSet = entity.getFileDatas();
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

    public Long getSampleId() {
        return sampleId;
    }

    public void setSampleId(Long sampleId) {
        this.sampleId = sampleId;
    }

}
