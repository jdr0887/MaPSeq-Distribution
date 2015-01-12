package edu.unc.mapseq.commands.sample;

import java.util.Formatter;
import java.util.Locale;
import java.util.Set;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.model.Attribute;
import edu.unc.mapseq.dao.model.Sample;

@Command(scope = "mapseq", name = "list-sample-attributes", description = "List Sample Attributes")
public class ListSampleAttributesAction extends AbstractAction {

    private MaPSeqDAOBean maPSeqDAOBean;

    @Argument(index = 0, name = "sampleId", description = "Sample Identifier", required = true, multiValued = false)
    private Long sampleId;

    public ListSampleAttributesAction() {
        super();
    }

    @Override
    public Object doExecute() {

        SampleDAO sampleDAO = maPSeqDAOBean.getSampleDAO();
        Sample entity = null;
        try {
            entity = sampleDAO.findById(sampleId);
        } catch (MaPSeqDAOException e) {
        }
        if (entity == null) {
            System.out.println("Sample was not found");
            return null;
        }

        StringBuilder sb = new StringBuilder();
        String format = "%1$-12s %2$-40s %3$s%n";
        Formatter formatter = new Formatter(sb, Locale.US);
        formatter.format(format, "ID", "Name", "Value");

        Set<Attribute> attributeSet = entity.getAttributes();
        if (attributeSet != null && !attributeSet.isEmpty()) {
            for (Attribute attribute : attributeSet) {
                formatter.format(format, attribute.getId(), attribute.getName(), attribute.getValue());
                formatter.flush();
            }
        }
        System.out.println(formatter.toString());
        formatter.close();

        return null;
    }

    public MaPSeqDAOBean getMaPSeqDAOBean() {
        return maPSeqDAOBean;
    }

    public void setMaPSeqDAOBean(MaPSeqDAOBean maPSeqDAOBean) {
        this.maPSeqDAOBean = maPSeqDAOBean;
    }

    public Long getSampleId() {
        return sampleId;
    }

    public void setSampleId(Long sampleId) {
        this.sampleId = sampleId;
    }

}
