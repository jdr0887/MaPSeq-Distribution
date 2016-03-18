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
import org.apache.commons.lang.StringUtils;

import edu.unc.mapseq.dao.MaPSeqDAOBeanService;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SOAPDAOManager;
import edu.unc.mapseq.dao.model.Attribute;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.model.Sample;
import edu.unc.mapseq.dao.model.Study;

public class CreateFlowcellFromSampleSheet implements Runnable {

    private final static HelpFormatter helpFormatter = new HelpFormatter();

    private final static Options cliOptions = new Options();

    private String baseDirectory;

    private String runName;

    private File sampleSheet;

    public CreateFlowcellFromSampleSheet() {
        super();
    }

    @SuppressWarnings("unused")
    @Override
    public void run() {

        SOAPDAOManager daoMgr = SOAPDAOManager.getInstance();
        // RSDAOManager daoMgr = RSDAOManager.getInstance();

        MaPSeqDAOBeanService maPSeqDAOBean = daoMgr.getMaPSeqDAOBeanService();

        Flowcell flowcell = new Flowcell();
        flowcell.setBaseDirectory(baseDirectory);
        flowcell.setName(runName);

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
                String ssSampleId = st[2];
                String sampleRef = st[3];
                String index = st[4];
                String description = st[5];
                String control = st[6];
                String recipe = st[7];
                String operator = st[8];
                String sampleProject = st[9];

                if (StringUtils.isEmpty(sampleProject)) {
                    System.err.printf("sampleProject is empty/null");
                    return;
                }

                List<Study> studyList = maPSeqDAOBean.getStudyDAO().findByName(sampleProject.trim());

                if (studyList == null || (studyList != null && studyList.isEmpty())) {
                    System.err.printf("Study doesn't exist...fix your sample sheet for column 9 (sampleProject)");
                    return;
                }
                Study study = studyList.get(0);

                Sample sample = new Sample();
                sample.setBarcode(index);
                sample.setLaneIndex(Integer.valueOf(laneIndex));
                sample.setName(ssSampleId);
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

                Long sampleId = maPSeqDAOBean.getSampleDAO().save(sample);
                sample.setId(sampleId);

            }

        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(flowcell.toString());
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

        cliOptions.addOption(OptionBuilder.withLongOpt("baseDirectory").isRequired().hasArg().create());
        cliOptions.addOption(OptionBuilder.withLongOpt("runName").isRequired().hasArg().create());
        cliOptions.addOption(OptionBuilder.withLongOpt("sampleSheet").isRequired().hasArg().create());
        cliOptions.addOption(OptionBuilder.withArgName("h").withDescription("print this help message")
                .withLongOpt("help").create("?"));

        CommandLineParser commandLineParser = new GnuParser();
        CreateFlowcellFromSampleSheet main = new CreateFlowcellFromSampleSheet();
        try {
            CommandLine commandLine = commandLineParser.parse(cliOptions, args);
            if (commandLine.hasOption("?")) {
                helpFormatter.printHelp(main.getClass().getSimpleName(), cliOptions);
                return;
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
