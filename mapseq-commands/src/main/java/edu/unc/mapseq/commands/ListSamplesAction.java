package edu.unc.mapseq.commands;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.karaf.shell.commands.Option;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.model.Sample;

@Command(scope = "mapseq", name = "list-samples", description = "List Samples")
public class ListSamplesAction extends AbstractAction {

    private MaPSeqDAOBean maPSeqDAOBean;

    @Option(name = "-l", description = "long format", required = false, multiValued = false)
    private Boolean longFormat;

    @Option(name = "--flowcellId", description = "Flowcell Identifier", required = false, multiValued = false)
    private Long flowcellId;

    @Option(name = "--name", description = "like search by name", required = false, multiValued = false)
    private String name;

    public ListSamplesAction() {
        super();
    }

    @Override
    public Object doExecute() {

        FlowcellDAO flowcellDAO = maPSeqDAOBean.getFlowcellDAO();
        SampleDAO sampleDAO = maPSeqDAOBean.getSampleDAO();

        if (flowcellId == null && StringUtils.isEmpty(name)) {
            System.out.println("flowcellId & name can't both be null/empty");
            return null;
        }

        List<Sample> sampleList = new ArrayList<Sample>();

        if (flowcellId != null) {
            try {
                Flowcell flowcell = flowcellDAO.findById(flowcellId);
                sampleList.addAll(sampleDAO.findByFlowcellId(flowcell.getId()));
            } catch (MaPSeqDAOException e) {
            }
        }

        if (StringUtils.isNotEmpty(name)) {
            try {
                sampleList.addAll(sampleDAO.findByName(name));
            } catch (MaPSeqDAOException e) {
            }
        }

        if (sampleList != null && !sampleList.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter(sb, Locale.US);
            if (longFormat != null && longFormat) {
                formatter.format("%1$-12s %2$-40s %3$-6s %4$-16s %5$s%n", "Sample ID", "Sample Name", "Lane",
                        "Barcode", "Output Directory");
            } else {
                formatter.format("%1$-12s %2$-40s %3$-6s %4$s%n", "Sample ID", "Sample Name", "Lane", "Barcode");
            }

            for (Sample sample : sampleList) {
                if (longFormat != null && longFormat) {
                    formatter.format("%1$-12s %2$-40s %3$-6s %4$-16s%n", sample.getId(), sample.getName(),
                            sample.getLaneIndex(), sample.getBarcode());
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

    public Long getFlowcellId() {
        return flowcellId;
    }

    public void setFlowcellId(Long flowcellId) {
        this.flowcellId = flowcellId;
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
