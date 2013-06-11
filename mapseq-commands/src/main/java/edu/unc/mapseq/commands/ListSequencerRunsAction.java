package edu.unc.mapseq.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.SequencerRunDAO;
import edu.unc.mapseq.dao.model.Account;
import edu.unc.mapseq.dao.model.SequencerRun;

@Command(scope = "mapseq", name = "list-sequencer-runs", description = "List SequencerRuns")
public class ListSequencerRunsAction extends AbstractAction {

    private MaPSeqDAOBean maPSeqDAOBean;

    @Override
    protected Object doExecute() throws Exception {

        List<SequencerRun> srList = new ArrayList<SequencerRun>();
        SequencerRunDAO sequencerRunDAO = maPSeqDAOBean.getSequencerRunDAO();
        try {
            srList.addAll(sequencerRunDAO.findAll());
        } catch (Exception e) {
        }

        if (srList.size() > 0) {

            Collections.sort(srList, new Comparator<SequencerRun>() {
                @Override
                public int compare(SequencerRun sr1, SequencerRun sr2) {
                    return sr1.getId().compareTo(sr2.getId());
                }
            });

            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter(sb, Locale.US);
            formatter.format("%1$-8s %2$-38s %3$-42s %4$s%n", "ID", "Name", "Base Directory", "Creator");
            for (SequencerRun sequencerRun : srList) {
                String creatorName = "";
                Account creator = sequencerRun.getCreator();
                if (creator != null) {
                    creatorName = creator.getName();
                }
                formatter.format("%1$-8s %2$-38s %3$-42s %4$s%n", sequencerRun.getId(), sequencerRun.getName(),
                        sequencerRun.getBaseDirectory(), creatorName);
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
