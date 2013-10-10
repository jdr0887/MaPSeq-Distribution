package edu.unc.mapseq.commands.reports;

import java.util.concurrent.Executors;

import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.tasks.WeeklyReportTask;

@Command(scope = "mapseq", name = "generate-weekly-report", description = "")
public class WeeklyReportAction extends AbstractAction {

    private final Logger logger = LoggerFactory.getLogger(WeeklyReportAction.class);

    private MaPSeqDAOBean maPSeqDAOBean;

    @Override
    protected Object doExecute() throws Exception {
        logger.debug("ENTERING doExecute()");
        WeeklyReportTask task = new WeeklyReportTask();
        task.setMaPSeqDAOBean(maPSeqDAOBean);
        Executors.newSingleThreadExecutor().execute(task);
        return null;
    }

    public MaPSeqDAOBean getMaPSeqDAOBean() {
        return maPSeqDAOBean;
    }

    public void setMaPSeqDAOBean(MaPSeqDAOBean maPSeqDAOBean) {
        this.maPSeqDAOBean = maPSeqDAOBean;
    }

}
