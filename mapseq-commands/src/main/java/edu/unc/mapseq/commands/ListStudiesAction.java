package edu.unc.mapseq.commands;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.StudyDAO;
import edu.unc.mapseq.dao.model.Study;

@Command(scope = "mapseq", name = "list-studies", description = "List Studies")
public class ListStudiesAction extends AbstractAction {

    private final Logger logger = LoggerFactory.getLogger(ListStudiesAction.class);

    private MaPSeqDAOBean maPSeqDAOBean;

    public ListStudiesAction() {
        super();
    }

    @Override
    public Object doExecute() {
        logger.debug("ENTERING doExecute()");

        List<Study> studyList = new ArrayList<Study>();
        StudyDAO studyDAO = maPSeqDAOBean.getStudyDAO();

        try {
            studyList.addAll(studyDAO.findAll());
        } catch (Exception e) {
        }

        if (studyList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            String format = "%1$-12s %2$-20s %3$-40s%n";
            Formatter formatter = new Formatter(sb, Locale.US);
            formatter.format(format, "ID", "Created", "Name");
            for (Study study : studyList) {

                Date created = study.getCreated();
                String formattedCreated = "";
                if (created != null) {
                    formattedCreated = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(
                            created);
                }

                formatter.format(format, study.getId(), formattedCreated, study.getName());
                formatter.flush();
            }
            System.out.println(formatter.toString());
            formatter.close();
        }
        return null;
    }

    public MaPSeqDAOBean getMaPSeqDAOBean() {
        return maPSeqDAOBean;
    }

    public void setMaPSeqDAOBean(MaPSeqDAOBean maPSeqDAOBean) {
        this.maPSeqDAOBean = maPSeqDAOBean;
    }

}
