package edu.unc.mapseq.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.model.Flowcell;

@Command(scope = "mapseq", name = "list-sequencer-runs", description = "List SequencerRuns")
public class ListFlowcellsAction extends AbstractAction {

    private final Logger logger = LoggerFactory.getLogger(ListFlowcellsAction.class);

    private MaPSeqDAOBean maPSeqDAOBean;

    @Override
    protected Object doExecute() throws Exception {
        logger.debug("ENTERING doExecute()");

        List<Flowcell> flowcellList = new ArrayList<Flowcell>();
        FlowcellDAO flowcellDAO = maPSeqDAOBean.getFlowcellDAO();
        try {
            flowcellList.addAll(flowcellDAO.findAll());
        } catch (Exception e) {
        }

        if (flowcellList.size() > 0) {

            Collections.sort(flowcellList, new Comparator<Flowcell>() {
                @Override
                public int compare(Flowcell sr1, Flowcell sr2) {
                    return sr1.getId().compareTo(sr2.getId());
                }
            });

            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter(sb, Locale.US);
            formatter.format("%1$-8s %2$-38s %3$-42s %4$s%n", "ID", "Name", "Base Directory");
            for (Flowcell flowcell : flowcellList) {
                formatter.format("%1$-8s %2$-38s %3$-42s%n", flowcell.getId(), flowcell.getName(),
                        flowcell.getBaseDirectory());
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
