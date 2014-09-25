package edu.unc.mapseq.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
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
            Formatter formatter = new Formatter(sb, Locale.US);
            formatter.format("%1$-8s %2$-40s%n", "ID", "Name");
            for (Study study : studyList) {
                formatter.format("%1$-8s %2$-40s%n", study.getId(), study.getName());
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
