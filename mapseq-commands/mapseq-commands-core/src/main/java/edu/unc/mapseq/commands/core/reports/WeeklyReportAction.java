package edu.unc.mapseq.commands.core.reports;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOBeanService;
import edu.unc.mapseq.tasks.WeeklyReportTask;

@Command(scope = "mapseq", name = "generate-weekly-report", description = "")
@Service
public class WeeklyReportAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(WeeklyReportAction.class);

    @Argument(index = 0, name = "toEmailAddress", description = "To Email Address", required = true, multiValued = false)
    private String toEmailAddress;

    @Reference
    private MaPSeqDAOBeanService maPSeqDAOBeanService;

    @Override
    public Object execute() {
        logger.debug("ENTERING doExecute()");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        WeeklyReportTask task = new WeeklyReportTask();
        task.setMaPSeqDAOBeanService(maPSeqDAOBeanService);
        task.setToEmailAddress(toEmailAddress);
        executorService.submit(task);
        executorService.shutdown();
        return null;
    }

}
