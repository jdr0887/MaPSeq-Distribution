package edu.unc.mapseq.main;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;

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
import edu.unc.mapseq.dao.ws.WSDAOManager;

public class CreateSequencerRunFromSampleSheet implements Runnable {

    private final static HelpFormatter helpFormatter = new HelpFormatter();

    private final static Options cliOptions = new Options();

    private Long platformId;

    private String baseDirectory;

    private String runName;

    private File sampleSheet;

    public CreateSequencerRunFromSampleSheet() {
        super();
    }

    @SuppressWarnings("unused")
    @Override
    public void run() {

        WSDAOManager daoMgr = WSDAOManager.getInstance();
        // RSDAOManager daoMgr = RSDAOManager.getInstance();

        MaPSeqDAOBean maPSeqDAOBean = daoMgr.getMaPSeqDAOBean();

        List<Account> accountList = null;
        try {
            accountList = maPSeqDAOBean.getAccountDAO().findByName(System.getProperty("user.name"));
            if (accountList == null || (accountList != null && accountList.isEmpty())) {
                System.err.printf("Account doesn't exist: %s%n", System.getProperty("user.name"));
                System.err.println("Must register account first");
                return;
            }
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

        Account account = accountList.get(0);

        Platform platform = null;
        try {
            PlatformDAO platformDAO = maPSeqDAOBean.getPlatformDAO();
            platform = platformDAO.findById(platformId);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

        SequencerRun sequencerRun = new SequencerRun();
        sequencerRun.setCreator(account);
        sequencerRun.setStatus(SequencerRunStatusType.COMPLETED);
        sequencerRun.setBaseDirectory(baseDirectory);
        sequencerRun.setName(runName);
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
                    return;
                }
                Study study = studyList.get(0);
                
                HTSFSample htsfSample = new HTSFSample();
                htsfSample.setBarcode(index);
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

                Long htsfSampleId = maPSeqDAOBean.getHTSFSampleDAO().save(htsfSample);
                htsfSample.setId(htsfSampleId);

            }

        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("SequencerRun ID: " + sequencerRun.getId());
    }

    public Long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
    }

    public String getBaseDirectory() {
        return baseDirectory;
    }

    public void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public String getRunName() {
        return runName;
    }

    public void setRunName(String runName) {
        this.runName = runName;
    }

    public File getSampleSheet() {
        return sampleSheet;
    }

    public void setSampleSheet(File sampleSheet) {
        this.sampleSheet = sampleSheet;
    }

    @SuppressWarnings("static-access")
    public static void main(String[] args) {

        cliOptions.addOption(OptionBuilder.withLongOpt("platformId").isRequired().hasArg().create());
        cliOptions.addOption(OptionBuilder.withLongOpt("baseDirectory").isRequired().hasArg().create());
        cliOptions.addOption(OptionBuilder.withLongOpt("runName").isRequired().hasArg().create());
        cliOptions.addOption(OptionBuilder.withLongOpt("sampleSheet").isRequired().hasArg().create());
        cliOptions.addOption(OptionBuilder.withArgName("h").withDescription("print this help message")
                .withLongOpt("help").create("?"));

        CommandLineParser commandLineParser = new GnuParser();
        CreateSequencerRunFromSampleSheet main = new CreateSequencerRunFromSampleSheet();
        try {
            CommandLine commandLine = commandLineParser.parse(cliOptions, args);
            if (commandLine.hasOption("?")) {
                helpFormatter.printHelp(main.getClass().getSimpleName(), cliOptions);
                return;
            }

            if (commandLine.hasOption("platformId")) {
                main.setPlatformId(Long.valueOf(commandLine.getOptionValue("platformId")));
            }

            if (commandLine.hasOption("baseDirectory")) {
                main.setBaseDirectory(commandLine.getOptionValue("baseDirectory"));
            }

            if (commandLine.hasOption("runName")) {
                main.setRunName(commandLine.getOptionValue("runName"));
            }

            if (commandLine.hasOption("sampleSheet")) {
                File sampleSheet = new File(commandLine.getOptionValue("sampleSheet"));
                if (!sampleSheet.exists()) {
                    System.err.println(("Failed: sampleSheet doesn't exist"));
                    return;
                }
                main.setSampleSheet(sampleSheet);
            }

            main.run();
        } catch (ParseException e) {
            System.err.println(("Parsing Failed: " + e.getMessage()));
            helpFormatter.printHelp(main.getClass().getSimpleName(), cliOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
