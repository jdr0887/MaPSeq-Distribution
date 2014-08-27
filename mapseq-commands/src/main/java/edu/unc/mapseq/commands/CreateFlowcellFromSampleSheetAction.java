package edu.unc.mapseq.commands;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Attribute;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.model.Sample;
import edu.unc.mapseq.dao.model.Study;

@Command(scope = "mapseq", name = "create-flowcell-from-samplesheet", description = "Create Flowcell from SampleSheet")
public class CreateFlowcellFromSampleSheetAction extends AbstractAction {

    private MaPSeqDAOBean maPSeqDAOBean;

    @Argument(index = 0, name = "baseRunFolder", description = "The folder parent to the flowcell directory", required = true, multiValued = false)
    private String baseRunFolder;

    @Argument(index = 1, name = "name", description = "Name", required = true, multiValued = false)
    private String name;

    @Argument(index = 2, name = "sampleSheet", description = "Sample Sheet", required = true, multiValued = false)
    private File sampleSheet;

    public CreateFlowcellFromSampleSheetAction() {
        super();
    }

    @SuppressWarnings("unused")
    @Override
    public Object doExecute() {

        Flowcell flowcell = new Flowcell();
        flowcell.setBaseDirectory(baseRunFolder);
        flowcell.setName(name);

        try {
            Long flowcellId = maPSeqDAOBean.getFlowcellDAO().save(flowcell);
            flowcell.setId(flowcellId);
        } catch (MaPSeqDAOException e1) {
            e1.printStackTrace();
        }

        try {
            LineNumberReader lnr = new LineNumberReader(new StringReader(FileUtils.readFileToString(sampleSheet)));
            lnr.readLine();
            String line;

            while ((line = lnr.readLine()) != null) {

                String[] st = line.split(",");
                String flowcellProper = st[0];
                String laneIndex = st[1];
                String sampleId = st[2];
                String sampleRef = st[3];
                String index = st[4];
                String description = st[5];
                String control = st[6];
                String recipe = st[7];
                String operator = st[8];
                String sampleProject = st[9];

                List<Study> studyList = maPSeqDAOBean.getStudyDAO().findByName(sampleProject);
                if (studyList == null || (studyList != null && studyList.isEmpty())) {
                    System.err.printf("Study doesn't exist...fix your sample sheet for column 9 (sampleProject)");
                    return null;
                }

                Study study = studyList.get(0);

                Sample sample = new Sample();
                sample.setBarcode(index);
                sample.setLaneIndex(Integer.valueOf(laneIndex));
                sample.setName(sampleId);
                sample.setFlowcell(flowcell);
                sample.setStudy(study);

                Set<Attribute> attributes = sample.getAttributes();
                if (attributes == null) {
                    attributes = new HashSet<Attribute>();
                }
                Attribute descAttribute = new Attribute();
                descAttribute.setName("production.id.description");
                descAttribute.setValue(description);
                attributes.add(descAttribute);
                sample.setAttributes(attributes);

                Long id = maPSeqDAOBean.getSampleDAO().save(sample);
                sample.setId(id);

            }

        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Flowcell ID: " + flowcell.getId());
        return null;
    }

    public MaPSeqDAOBean getMaPSeqDAOBean() {
        return maPSeqDAOBean;
    }

    public void setMaPSeqDAOBean(MaPSeqDAOBean maPSeqDAOBean) {
        this.maPSeqDAOBean = maPSeqDAOBean;
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
