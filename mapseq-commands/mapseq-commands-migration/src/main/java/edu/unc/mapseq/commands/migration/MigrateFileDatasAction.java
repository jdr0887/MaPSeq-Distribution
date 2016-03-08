package edu.unc.mapseq.commands.migration;

import java.util.concurrent.Executors;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOBeanService;

@Command(scope = "mapseq", name = "migrate-file-datas", description = "Migrate FileData instances")
@Service
public class MigrateFileDatasAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(MigrateFileDatasAction.class);

    @Reference
    private MaPSeqDAOBeanService maPSeqDAOBeanService;

    @Option(name = "--dryRun", description = "Don't move anything", required = false, multiValued = false)
    private Boolean dryRun = Boolean.FALSE;

    @Override
    public Object execute() {
        logger.debug("ENTERING doExecute()");
        MigrateFileDatasRunnable runnable = new MigrateFileDatasRunnable(dryRun, maPSeqDAOBeanService);
        Executors.newSingleThreadExecutor().execute(runnable);
        return null;
    }

    public Boolean getDryRun() {
        return dryRun;
    }

    public void setDryRun(Boolean dryRun) {
        this.dryRun = dryRun;
    }

}
