package edu.unc.mapseq.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.HTSFSampleDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SequencerRunDAO;
import edu.unc.mapseq.dao.model.HTSFSample;
import edu.unc.mapseq.dao.model.SequencerRun;

@Command(scope = "mapseq", name = "list-htsf-samples", description = "List HTSFSamples")
public class ListHTSFSamplesAction extends AbstractAction {

    private MaPSeqDAOBean maPSeqDAOBean;

    @Argument(index = 0, name = "sequencerRunId", description = "Sequencer Run Identifier", required = true, multiValued = true)
    private List<Long> sequencerRunIdList;

    @Option(name = "-l", description = "long format", required = false, multiValued = false)
    private Boolean longFormat;

    public ListHTSFSamplesAction() {
        super();
    }

    @Override
    public Object doExecute() {

        List<SequencerRun> srList = new ArrayList<SequencerRun>();
        SequencerRunDAO sequencerRunDAO = maPSeqDAOBean.getSequencerRunDAO();
        for (Long sequencerRunId : sequencerRunIdList) {
            SequencerRun sequencerRun = null;
            try {
                sequencerRun = sequencerRunDAO.findById(sequencerRunId);
            } catch (MaPSeqDAOException e) {
            }
            if (sequencerRun != null) {
                srList.add(sequencerRun);
            }
        }

        if (srList.size() > 0) {

            Collections.sort(srList, new Comparator<SequencerRun>() {
                @Override
                public int compare(SequencerRun sr1, SequencerRun sr2) {
                    return sr1.getId().compareTo(sr2.getId());
                }
            });

            List<HTSFSample> htsfSampleList = new ArrayList<HTSFSample>();
            for (SequencerRun sequencerRun : srList) {
                HTSFSampleDAO htsfSampleDAO = maPSeqDAOBean.getHTSFSampleDAO();
                try {
                    htsfSampleList.addAll(htsfSampleDAO.findBySequencerRunId(sequencerRun.getId()));
                } catch (MaPSeqDAOException e) {
                }
            }

            if (htsfSampleList != null && htsfSampleList.size() > 0) {
                StringBuilder sb = new StringBuilder();
                Formatter formatter = new Formatter(sb, Locale.US);
                if (longFormat != null && longFormat) {
                    formatter.format("%1$-12s %2$-40s %3$-6s %4$-16s %5$s%n", "Sample ID", "Sample Name", "Lane",
                            "Barcode", "Output Directory");
                } else {
                    formatter.format("%1$-12s %2$-40s %3$-6s %4$s%n", "Sample ID", "Sample Name", "Lane", "Barcode");
                }
                Collections.sort(htsfSampleList, new Comparator<HTSFSample>() {
                    @Override
                    public int compare(HTSFSample sr1, HTSFSample sr2) {
                        return sr1.getId().compareTo(sr2.getId());
                    }
                });

                for (HTSFSample sample : htsfSampleList) {
                    if (longFormat != null && longFormat) {
                        formatter.format("%1$-12s %2$-40s %3$-6s %4$-16s %5$s%n", sample.getId(), sample.getName(),
                                sample.getLaneIndex(), sample.getBarcode(), sample.getOutputDirectory());
                    } else {
                        formatter.format("%1$-12s %2$-40s %3$-6s %4$s%n", sample.getId(), sample.getName(),
                                sample.getLaneIndex(), sample.getBarcode());
                    }
                    formatter.flush();
                }
                System.out.println(formatter.toString());
                formatter.close();
            }

        }
        return null;
    }

    public MaPSeqDAOBean getMaPSeqDAOBean() {
        return maPSeqDAOBean;
    }

    public void setMaPSeqDAOBean(MaPSeqDAOBean maPSeqDAOBean) {
        this.maPSeqDAOBean = maPSeqDAOBean;
    }

    public List<Long> getSequencerRunIdList() {
        return sequencerRunIdList;
    }

    public void setSequencerRunIdList(List<Long> sequencerRunIdList) {
        this.sequencerRunIdList = sequencerRunIdList;
    }

    public Boolean getLongFormat() {
        return longFormat;
    }

    public void setLongFormat(Boolean longFormat) {
        this.longFormat = longFormat;
    }

}
