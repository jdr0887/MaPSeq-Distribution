package edu.unc.mapseq.commands.sequencing.flowcell;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Flowcell;

@Command(scope = "mapseq", name = "list-flowcells", description = "List Flowcells")
@Service
public class ListFlowcellsAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(ListFlowcellsAction.class);

    @Reference
    private FlowcellDAO flowcellDAO;

    @Option(name = "--workflowRunId", description = "WorkflowRun Identifier", required = false, multiValued = false)
    private Long workflowRunId;

    @Option(name = "--studyName", description = "Study Name", required = false, multiValued = false)
    private String studyName;

    public ListFlowcellsAction() {
        super();
    }

    @Override
    public Object execute() {
        logger.debug("ENTERING execute()");

        try {

            List<Flowcell> flowcells = new ArrayList<Flowcell>();

            if (workflowRunId != null) {
                flowcells.addAll(flowcellDAO.findByWorkflowRunId(workflowRunId));
            }

            if (StringUtils.isNotEmpty(studyName)) {
                flowcells.addAll(flowcellDAO.findByStudyName(studyName));
            }

            if (CollectionUtils.isEmpty(flowcells)) {
                flowcells = flowcellDAO.findAll();
            }

            if (CollectionUtils.isNotEmpty(flowcells)) {

                flowcells.sort((a, b) -> a.getId().compareTo(b.getId()));

                String format = "%1$-12s %2$-20s %3$-38s %4$s%n";
                StringBuilder sb = new StringBuilder();
                Formatter formatter = new Formatter(sb, Locale.US);
                formatter.format(format, "ID", "Created", "Name", "Base Directory");
                for (Flowcell flowcell : flowcells) {

                    Date created = flowcell.getCreated();
                    String formattedCreated = "";
                    if (created != null) {
                        formattedCreated = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                                .format(created);
                    }

                    formatter.format(format, flowcell.getId(), formattedCreated, flowcell.getName(),
                            flowcell.getBaseDirectory());
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

    public Long getWorkflowRunId() {
        return workflowRunId;
    }

    public void setWorkflowRunId(Long workflowRunId) {
        this.workflowRunId = workflowRunId;
    }

    public String getStudyName() {
        return studyName;
    }

    public void setStudyName(String studyName) {
        this.studyName = studyName;
    }

}
