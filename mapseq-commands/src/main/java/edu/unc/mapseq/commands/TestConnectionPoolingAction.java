package edu.unc.mapseq.commands;

import java.util.Date;
import java.util.UUID;

import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.SequencerRunDAO;
import edu.unc.mapseq.dao.model.SequencerRun;

@Command(scope = "mapseq", name = "test-connection-pooling", description = "Test connection pooling")
public class TestConnectionPoolingAction extends AbstractAction {

    private MaPSeqDAOBean maPSeqDAOBean;

    @Override
    protected Object doExecute() throws Exception {

        SequencerRunDAO sequencerRunDAO = maPSeqDAOBean.getSequencerRunDAO();
        Date d = new Date();
        for (int i = 0; i < 10000; ++i) {
            SequencerRun entity = new SequencerRun();
            String uuid = UUID.randomUUID().toString();
            entity.setName(uuid);
            entity.setBaseDirectory(uuid);
            entity.setDescription(uuid);
            entity.setCreationDate(d);
            entity.setModificationDate(d);
            sequencerRunDAO.save(entity);
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
