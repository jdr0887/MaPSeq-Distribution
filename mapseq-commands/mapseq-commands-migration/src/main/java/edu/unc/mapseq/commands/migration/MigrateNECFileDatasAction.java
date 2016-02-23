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

@Command(scope = "mapseq", name = "migrate-nec-file-datas", description = "Migrate NEC FileData instances")
@Service
public class MigrateNECFileDatasAction implements Action {

    private final Logger logger = LoggerFactory.getLogger(MigrateNECFileDatasAction.class);

    @Option(name = "--dryRun", description = "Don't move anything", required = false, multiValued = false)
    private Boolean dryRun = Boolean.FALSE;

    @Reference
    private MaPSeqDAOBeanService maPSeqDAOBeanService;

    @Override
    public Object execute() {
        logger.debug("ENTERING doExecute()");
        MigrateNECFileDatasRunnable runnable = new MigrateNECFileDatasRunnable(dryRun, maPSeqDAOBeanService);
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
