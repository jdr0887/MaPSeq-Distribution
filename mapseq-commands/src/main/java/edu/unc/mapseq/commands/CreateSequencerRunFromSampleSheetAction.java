package edu.unc.mapseq.commands;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.PlatformDAO;
import edu.unc.mapseq.dao.model.Account;
import edu.unc.mapseq.dao.model.EntityAttribute;
import edu.unc.mapseq.dao.model.HTSFSample;
import edu.unc.mapseq.dao.model.Platform;
import edu.unc.mapseq.dao.model.SequencerRun;
import edu.unc.mapseq.dao.model.SequencerRunStatusType;
import edu.unc.mapseq.dao.model.Study;

@Command(scope = "mapseq", name = "create-sequencer-run-from-samplesheet", description = "Create SequencerRun from SampleSheet")
public class CreateSequencerRunFromSampleSheetAction extends AbstractAction {

    private MaPSeqDAOBean mapseqDAOBean;

    @Argument(index = 0, name = "platformId", description = "Platform Id", required = true, multiValued = false)
    private Long platformId;

    @Argument(index = 1, name = "baseRunFolder", description = "The folder parent to the flowcell directory", required = true, multiValued = false)
    private String baseRunFolder;

    @Argument(index = 2, name = "name", description = "Name", required = true, multiValued = false)
    private String name;

    @Argument(index = 3, name = "sampleSheet", description = "Sample Sheet", required = true, multiValued = false)
    private File sampleSheet;

    public CreateSequencerRunFromSampleSheetAction() {
        super();
    }

    @SuppressWarnings("unused")
    @Override
    public Object doExecute() {

        Account account = null;
        try {
            account = mapseqDAOBean.getAccountDAO().findByName(System.getProperty("user.name"));
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

        if (account == null) {
            System.err.println("Must register account first");
            return null;
        }

        Date creationDate = new Date();
        Platform platform = null;
        try {
            PlatformDAO platformDAO = mapseqDAOBean.getPlatformDAO();
            platform = platformDAO.findById(platformId);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

        SequencerRun sequencerRun = new SequencerRun();
        sequencerRun.setCreator(account);
        sequencerRun.setStatus(SequencerRunStatusType.COMPLETED);
        sequencerRun.setBaseDirectory(baseRunFolder);
        sequencerRun.setCreationDate(creationDate);
        sequencerRun.setModificationDate(creationDate);
        sequencerRun.setName(name);
        sequencerRun.setPlatform(platform);

        try {
            Long sequencerRunId = mapseqDAOBean.getSequencerRunDAO().save(sequencerRun);
            sequencerRun.setId(sequencerRunId);
        } catch (MaPSeqDAOException e1) {
            e1.printStackTrace();
        }

        try {
            LineNumberReader lnr = new LineNumberReader(new StringReader(FileUtils.readFileToString(sampleSheet)));
            lnr.readLine();
            String line;

            while ((line = lnr.readLine()) != null) {

                String[] st = line.split(",");
                String flowcell = st[0];
                String laneIndex = st[1];
                String sampleId = st[2];
                String sampleRef = st[3];
                String index = st[4];
                String description = st[5];
                String control = st[6];
                String recipe = st[7];
                String operator = st[8];
                String sampleProject = st[9];

                Study study = null;
                try {
                    study = mapseqDAOBean.getStudyDAO().findByName(sampleProject);
                } catch (Exception e) {
                    // swallow exceptions
                }
                if (study == null) {
                    study = new Study();
                    study.setCreationDate(creationDate);
                    study.setModificationDate(creationDate);
                    study.setCreator(account);
                    study.setName(sampleProject);
                    Long studyId = mapseqDAOBean.getStudyDAO().save(study);
                    study.setId(studyId);
                }

                if (study != null) {

                    HTSFSample htsfSample = new HTSFSample();
                    htsfSample.setBarcode(index);
                    htsfSample.setCreationDate(creationDate);
                    htsfSample.setModificationDate(creationDate);
                    htsfSample.setCreator(account);
                    htsfSample.setLaneIndex(Integer.valueOf(laneIndex));
                    htsfSample.setName(sampleId);
                    htsfSample.setSequencerRun(sequencerRun);
                    htsfSample.setStudy(study);

                    Set<EntityAttribute> attributes = htsfSample.getAttributes();
                    if (attributes == null) {
                        attributes = new HashSet<EntityAttribute>();
                    }
                    EntityAttribute descAttribute = new EntityAttribute();
                    descAttribute.setName("production.id.description");
                    descAttribute.setValue(description);
                    attributes.add(descAttribute);
                    htsfSample.setAttributes(attributes);

                    Long htsfSampleId = mapseqDAOBean.getHTSFSampleDAO().save(htsfSample);
                    htsfSample.setId(htsfSampleId);

                }

            }

        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("SequencerRun ID: " + sequencerRun.getId());
        return null;
    }

    public MaPSeqDAOBean getMapseqDAOBean() {
        return mapseqDAOBean;
    }

    public void setMapseqDAOBean(MaPSeqDAOBean mapseqDAOBean) {
        this.mapseqDAOBean = mapseqDAOBean;
    }

}
