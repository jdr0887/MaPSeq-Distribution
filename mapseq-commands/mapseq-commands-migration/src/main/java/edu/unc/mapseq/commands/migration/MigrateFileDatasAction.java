package edu.unc.mapseq.commands.migration;

import java.util.concurrent.Executors;

import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.commands.Option;
import org.apache.karaf.shell.console.AbstractAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOBean;

@Command(scope = "mapseq", name = "migrate-file-datas", description = "Migrate FileData instances")
public class MigrateFileDatasAction extends AbstractAction {

    private final Logger logger = LoggerFactory.getLogger(MigrateFileDatasAction.class);

    @Option(name = "--dryRun", description = "Don't move anything", required = false, multiValued = false)
    private Boolean dryRun = Boolean.FALSE;

    private MaPSeqDAOBean maPSeqDAOBean;

    @Override
    protected Object doExecute() throws Exception {
        logger.debug("ENTERING doExecute()");
        MigrateFileDatasRunnable runnable = new MigrateFileDatasRunnable();
        runnable.setMaPSeqDAOBean(maPSeqDAOBean);
        runnable.setDryRun(dryRun);
        Executors.newSingleThreadExecutor().execute(runnable);
        return null;
    }

    public Boolean getDryRun() {
        return dryRun;
    }

    public void setDryRun(Boolean dryRun) {
        this.dryRun = dryRun;
    }

    public MaPSeqDAOBean getMaPSeqDAOBean() {
        return maPSeqDAOBean;
    }

    public void setMaPSeqDAOBean(MaPSeqDAOBean maPSeqDAOBean) {
        this.maPSeqDAOBean = maPSeqDAOBean;
    }

}
