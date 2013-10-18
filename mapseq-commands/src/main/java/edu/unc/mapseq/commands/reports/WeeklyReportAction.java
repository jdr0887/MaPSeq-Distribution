package edu.unc.mapseq.commands.reports;

import java.util.concurrent.Executors;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.tasks.WeeklyReportTask;

@Command(scope = "mapseq", name = "generate-weekly-report", description = "")
public class WeeklyReportAction extends AbstractAction {

    private final Logger logger = LoggerFactory.getLogger(WeeklyReportAction.class);

    @Argument(index = 0, name = "toEmailAddress", description = "To Email Address", required = true, multiValued = false)
    private String toEmailAddress;

    private MaPSeqDAOBean maPSeqDAOBean;

    @Override
    protected Object doExecute() throws Exception {
        logger.debug("ENTERING doExecute()");
        WeeklyReportTask task = new WeeklyReportTask();
        task.setMaPSeqDAOBean(maPSeqDAOBean);
        task.setToEmailAddress(toEmailAddress);
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
