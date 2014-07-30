package edu.unc.mapseq.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
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

    @Option(name = "-l", description = "long format", required = false, multiValued = false)
    private Boolean longFormat;

    @Option(name = "--sequencerRunId", description = "Sequencer Run Identifier", required = false, multiValued = false)
    private Long sequencerRunId;

    @Option(name = "--name", description = "like search by name", required = false, multiValued = false)
    private String name;

    public ListHTSFSamplesAction() {
        super();
    }

    @Override
    public Object doExecute() {

        SequencerRunDAO sequencerRunDAO = maPSeqDAOBean.getSequencerRunDAO();
        HTSFSampleDAO htsfSampleDAO = maPSeqDAOBean.getHTSFSampleDAO();

        if (sequencerRunId == null && StringUtils.isEmpty(name)) {
            System.out.println("sequencerRunId & name can't both be null/empty");
            return null;
        }

        List<HTSFSample> htsfSampleList = new ArrayList<HTSFSample>();

        if (sequencerRunId != null) {
            try {
                SequencerRun sequencerRun = sequencerRunDAO.findById(sequencerRunId);
                htsfSampleList.addAll(htsfSampleDAO.findBySequencerRunId(sequencerRun.getId()));
            } catch (MaPSeqDAOException e) {
            }
        }

        if (StringUtils.isNotEmpty(name)) {
            try {
                htsfSampleList.addAll(htsfSampleDAO.findByName(name));
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

        return null;
    }

    public MaPSeqDAOBean getMaPSeqDAOBean() {
        return maPSeqDAOBean;
    }

    public void setMaPSeqDAOBean(MaPSeqDAOBean maPSeqDAOBean) {
        this.maPSeqDAOBean = maPSeqDAOBean;
    }

    public Long getSequencerRunId() {
        return sequencerRunId;
    }

    public void setSequencerRunId(Long sequencerRunId) {
        this.sequencerRunId = sequencerRunId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getLongFormat() {
        return longFormat;
    }

    public void setLongFormat(Boolean longFormat) {
        this.longFormat = longFormat;
    }

}
