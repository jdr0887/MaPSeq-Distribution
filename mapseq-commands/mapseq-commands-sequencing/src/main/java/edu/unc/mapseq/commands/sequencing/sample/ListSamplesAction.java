package edu.unc.mapseq.commands.sequencing.sample;

import java.text.DateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.model.Sample;

@Command(scope = "mapseq", name = "list-samples", description = "List Samples")
@Service
public class ListSamplesAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(ListSamplesAction.class);

    @Reference
    private SampleDAO sampleDAO;

    @Option(name = "--flowcellId", description = "Flowcell Identifier", required = false, multiValued = false)
    private Long flowcellId;

    @Option(name = "--workflowRunId", description = "WorkflowRun Identifier", required = false, multiValued = false)
    private Long workflowRunId;

    @Option(name = "--name", description = "like search by name", required = false, multiValued = false)
    private String name;

    public ListSamplesAction() {
        super();
    }

    @Override
    public Object execute() {
        logger.debug("ENTERING execute()");

        try {

            Set<Sample> sampleList = new HashSet<Sample>();

            if (workflowRunId != null) {
                List<Sample> samples = sampleDAO.findByWorkflowRunId(workflowRunId);
                if (CollectionUtils.isNotEmpty(samples)) {
                    sampleList.addAll(samples);
                }
            }

            if (flowcellId != null) {
                List<Sample> samples = sampleDAO.findByFlowcellId(flowcellId);
                if (CollectionUtils.isNotEmpty(samples)) {
                    sampleList.addAll(samples);
                }
            }

            if (StringUtils.isNotEmpty(name)) {
                List<Sample> samples = sampleDAO.findByName(name);
                if (CollectionUtils.isNotEmpty(samples)) {
                    sampleList.addAll(samples);
                }
            }

            if (CollectionUtils.isNotEmpty(sampleList)) {
                StringBuilder sb = new StringBuilder();
                Formatter formatter = new Formatter(sb, Locale.US);
                String format = "%1$-12s %2$-20s %3$-40s %4$-8s %5$-20s %6$s%n";
                formatter.format(format, "ID", "Created", "Name", "Lane", "Barcode", "Output Directory");

                for (Sample sample : sampleList) {

                    Date created = sample.getCreated();
                    String formattedCreated = "";
                    if (created != null) {
                        formattedCreated = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                                .format(created);
                    }

                    formatter.format(format, sample.getId(), formattedCreated, sample.getName(), sample.getLaneIndex(),
                            sample.getBarcode(), sample.getOutputDirectory());
                    formatter.flush();
                }
                System.out.println(formatter.toString());
                formatter.close();
            }
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

        return null;
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

    public Long getWorkflowRunId() {
        return workflowRunId;
    }

    public void setWorkflowRunId(Long workflowRunId) {
        this.workflowRunId = workflowRunId;
    }

}
