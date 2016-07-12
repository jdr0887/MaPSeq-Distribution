package edu.unc.mapseq.commands.sequencing.flowcell;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import edu.unc.mapseq.dao.AttributeDAO;
import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.StudyDAO;
import edu.unc.mapseq.dao.model.Attribute;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.model.Sample;
import edu.unc.mapseq.dao.model.Study;

@Command(scope = "mapseq", name = "create-flowcell-from-samplesheet", description = "Create Flowcell from SampleSheet")
@Service
public class CreateFlowcellFromSampleSheetAction implements Action {

    @Reference
    private FlowcellDAO flowcellDAO;

    @Reference
    private StudyDAO studyDAO;

    @Reference
    private SampleDAO sampleDAO;

    @Reference
    private AttributeDAO attributeDAO;

    @Argument(index = 0, name = "baseRunFolder", description = "The folder parent to the flowcell directory", required = true, multiValued = false)
    private String baseRunFolder;

    @Argument(index = 1, name = "name", description = "Name", required = true, multiValued = false)
    private String name;

    @Argument(index = 2, name = "sampleSheet", description = "Sample Sheet", required = true, multiValued = false)
    private File sampleSheet;

    public CreateFlowcellFromSampleSheetAction() {
        super();
    }

    @Override
    public Object execute() {

        try {
            Flowcell flowcell = new Flowcell(name);
            flowcell.setBaseDirectory(baseRunFolder);
            flowcell.setId(flowcellDAO.save(flowcell));

            Reader in = new FileReader(sampleSheet);
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader("FCID", "Lane", "SampleID", "SampleRef", "Index", "Description",
                    "Control", "Recipe", "Operator", "SampleProject").parse(in);
            final Set<String> studyNameSet = new HashSet<>();
            records.forEach(a -> studyNameSet.add(a.get("SampleProject")));
            Collections.synchronizedSet(studyNameSet);

            if (CollectionUtils.isEmpty(studyNameSet)) {
                System.out.println("No Study names in SampleSheet");
                return null;
            }

            if (studyNameSet.size() > 1) {
                System.out.println("More than one Study in SampleSheet");
                return null;
            }

            String foundStudyName = studyNameSet.iterator().next();

            List<Study> foundStudies = studyDAO.findByName(foundStudyName);
            if (CollectionUtils.isEmpty(foundStudies)) {
                System.out.printf("No Studies found for: %s%n", foundStudyName);
                return null;
            }

            Study study = foundStudies.get(0);

            for (CSVRecord record : records) {
                String laneIndex = record.get("Lane");
                String sampleId = record.get("SampleID");
                String barcode = record.get("Index");
                String description = record.get("Description");

                Sample sample = new Sample(sampleId);
                sample.setBarcode(barcode);
                sample.setLaneIndex(Integer.valueOf(laneIndex));
                sample.setFlowcell(flowcell);
                sample.setStudy(study);
                sample.setId(sampleDAO.save(sample));
                if (StringUtils.isNotEmpty(description)) {
                    Attribute attribute = new Attribute("production.id.description", description);
                    attribute.setId(attributeDAO.save(attribute));
                    sample.getAttributes().add(attribute);
                }
                sampleDAO.save(sample);
            }

            System.out.println("Flowcell ID: " + flowcell.getId());

        } catch (IOException | MaPSeqDAOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getBaseRunFolder() {
        return baseRunFolder;
    }

    public void setBaseRunFolder(String baseRunFolder) {
        this.baseRunFolder = baseRunFolder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getSampleSheet() {
        return sampleSheet;
    }

    public void setSampleSheet(File sampleSheet) {
        this.sampleSheet = sampleSheet;
    }

}
