package edu.unc.mapseq.commands.sequencing.sample;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.StudyDAO;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.model.Sample;

@Command(scope = "mapseq", name = "create-sample", description = "Create Sample")
@Service
public class CreateSampleAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(CreateSampleAction.class);

    @Argument(index = 0, name = "flowcellId", description = "Flowcell Identifier", required = true, multiValued = false)
    private Long flowcellId;

    @Argument(index = 1, name = "laneIndex", description = "Lane Index", required = true, multiValued = false)
    private Integer laneIndex;

    @Argument(index = 2, name = "barcode", description = "barcode", required = true, multiValued = false)
    private String barcode;

    @Argument(index = 3, name = "studyId", description = "Study Identifier", required = true, multiValued = false)
    private Long studyId;

    @Argument(index = 4, name = "name", description = "Name", required = true, multiValued = false)
    private String name;

    @Reference
    private FlowcellDAO flowcellDAO;

    @Reference
    private SampleDAO sampleDAO;

    @Reference
    private StudyDAO studyDAO;

    public CreateSampleAction() {
        super();
    }

    @Override
    public Object execute() {
        logger.debug("ENTERING execute()");

        try {
            Flowcell flowcell = flowcellDAO.findById(this.flowcellId);

            if (flowcell == null) {
                System.err.println("Flowcell not found: " + this.flowcellId);
                System.err.println("Please run list-flowcells and use a valid Flowcell Identifier.");
                return null;
            }

            Sample sample = new Sample(name);
            sample.setBarcode(barcode);
            sample.setStudy(studyDAO.findById(this.studyId));
            sample.setLaneIndex(laneIndex);
            sample.setFlowcell(flowcell);
            sample.setId(sampleDAO.save(sample));

            System.out.println(sample.toString());
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

    public Integer getLaneIndex() {
        return laneIndex;
    }

    public void setLaneIndex(Integer laneIndex) {
        this.laneIndex = laneIndex;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
