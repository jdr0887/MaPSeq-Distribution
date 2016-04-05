package edu.unc.mapseq.commands.sequencing.study;

import java.text.DateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.StudyDAO;
import edu.unc.mapseq.dao.model.Study;

@Command(scope = "mapseq", name = "list-studies", description = "List Studies")
@Service
public class ListStudiesAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(ListStudiesAction.class);

    @Reference
    private StudyDAO studyDAO;

    public ListStudiesAction() {
        super();
    }

    @Override
    public Object execute() {
        logger.debug("ENTERING doExecute()");

        try {
            List<Study> studyList = studyDAO.findAll();

            if (CollectionUtils.isNotEmpty(studyList)) {
                StringBuilder sb = new StringBuilder();
                String format = "%1$-12s %2$-20s %3$-40s%n";
                Formatter formatter = new Formatter(sb, Locale.US);
                formatter.format(format, "ID", "Created", "Name");
                for (Study study : studyList) {

                    Date created = study.getCreated();
                    String formattedCreated = "";
                    if (created != null) {
                        formattedCreated = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                                .format(created);
                    }

                    formatter.format(format, study.getId(), formattedCreated, study.getName());
                    formatter.flush();
                }
                System.out.println(formatter.toString());
                formatter.close();
            }
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
