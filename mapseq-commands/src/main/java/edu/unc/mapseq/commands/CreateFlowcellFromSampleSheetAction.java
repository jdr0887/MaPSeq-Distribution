package edu.unc.mapseq.commands;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
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

    private MaPSeqDAOBean maPSeqDAOBean;

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

        List<Account> accountList = null;
        try {
            accountList = maPSeqDAOBean.getAccountDAO().findByName(System.getProperty("user.name"));
            if (accountList == null || (accountList != null && accountList.isEmpty())) {
                System.err.printf("Account doesn't exist: %s%n", System.getProperty("user.name"));
                System.err.println("Must register account first");
                return null;
            }
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

        Platform platform = null;
        try {
            PlatformDAO platformDAO = maPSeqDAOBean.getPlatformDAO();
            platform = platformDAO.findById(platformId);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

        SequencerRun sequencerRun = new SequencerRun();
        sequencerRun.setCreator(accountList.get(0));
        sequencerRun.setStatus(SequencerRunStatusType.COMPLETED);
        sequencerRun.setBaseDirectory(baseRunFolder);
        sequencerRun.setName(name);
        sequencerRun.setPlatform(platform);

        try {
            Long sequencerRunId = maPSeqDAOBean.getSequencerRunDAO().save(sequencerRun);
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

                List<Study> studyList = maPSeqDAOBean.getStudyDAO().findByName(sampleProject);
                if (studyList == null || (studyList != null && studyList.isEmpty())) {
                    System.err.printf("Study doesn't exist...fix your sample sheet for column 9 (sampleProject)");
                    return null;
                }

                Study study = studyList.get(0);

                HTSFSample htsfSample = new HTSFSample();
                htsfSample.setBarcode(index);
                htsfSample.setCreator(accountList.get(0));
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

                Long htsfSampleId = maPSeqDAOBean.getHTSFSampleDAO().save(htsfSample);
                htsfSample.setId(htsfSampleId);

            }

        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("SequencerRun ID: " + sequencerRun.getId());
        return null;
    }

    public MaPSeqDAOBean getMaPSeqDAOBean() {
        return maPSeqDAOBean;
    }

    public void setMaPSeqDAOBean(MaPSeqDAOBean maPSeqDAOBean) {
        this.maPSeqDAOBean = maPSeqDAOBean;
    }

    public Long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
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
