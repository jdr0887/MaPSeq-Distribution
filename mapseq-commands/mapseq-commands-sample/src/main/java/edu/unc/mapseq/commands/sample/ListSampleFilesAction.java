package edu.unc.mapseq.commands.sample;

import java.text.DateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOBeanService;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.Sample;

@Command(scope = "mapseq", name = "list-sample-files", description = "List Sample Files")
@Service
public class ListSampleFilesAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(ListSampleFilesAction.class);

    @Reference
    private MaPSeqDAOBeanService maPSeqDAOBeanService;

    @Argument(index = 0, name = "sampleId", description = "Sample Identifier", required = true, multiValued = false)
    private Long sampleId;

    public ListSampleFilesAction() {
        super();
    }

    @Override
    public Object execute() {
        logger.debug("ENTERING execute()");

        SampleDAO sampleDAO = maPSeqDAOBeanService.getSampleDAO();
        Sample entity = null;
        try {
            entity = sampleDAO.findById(sampleId);
            if (entity == null) {
                System.out.println("Sample was not found");
                return null;
            }

            String format = "%1$-12s %2$-20s %3$-24s %4$-80s %5$s%n";
            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter(sb, Locale.US);
            formatter.format(format, "ID", "Created", "MimeType", "Name", "Path");

            Set<FileData> fileDataSet = entity.getFileDatas();
            if (CollectionUtils.isNotEmpty(fileDataSet)) {
                for (FileData fileData : fileDataSet) {

                    Date created = fileData.getCreated();
                    String formattedCreated = "";
                    if (created != null) {
                        formattedCreated = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                                .format(created);
                    }

                    formatter.format(format, fileData.getId(), formattedCreated, fileData.getMimeType(),
                            fileData.getName(), fileData.getPath());
                    formatter.flush();
                }
            }
            System.out.println(formatter.toString());
            formatter.close();
        } catch (MaPSeqDAOException e) {
        }

        return null;
    }

    public Long getSampleId() {
        return sampleId;
    }

    public void setSampleId(Long sampleId) {
        this.sampleId = sampleId;
    }

}
