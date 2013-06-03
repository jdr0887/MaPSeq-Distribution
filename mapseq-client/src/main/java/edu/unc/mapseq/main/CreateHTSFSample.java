package edu.unc.mapseq.main;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import edu.unc.mapseq.dao.HTSFSampleDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Account;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.HTSFSample;
import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.dao.model.SequencerRun;
import edu.unc.mapseq.dao.ws.WebServiceDAOManager;

public class CreateHTSFSample implements Callable<Long> {

    private final static HelpFormatter helpFormatter = new HelpFormatter();

    private final static Options cliOptions = new Options();

    private Long sequencerRunId;

    private Integer laneIndex;

    private String barcode;

    private Long studyId;

    private File read1Fastq;

    private File read2Fastq;

    private String name;

    public CreateHTSFSample() {
        super();
    }

    @Override
    public Long call() {
        WebServiceDAOManager daoMgr = WebServiceDAOManager.getInstance();

        Account account = null;
        try {
            account = daoMgr.getWSDAOBean().getAccountDAO().findByName(System.getProperty("user.name"));
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

        if (account == null) {
            System.err.println("Must register account first");
            return null;
        }

        SequencerRun sequencerRun = null;
        try {
            sequencerRun = daoMgr.getWSDAOBean().getSequencerRunDAO().findById(this.sequencerRunId);
        } catch (Exception e1) {
        }

        if (sequencerRun == null) {
            System.err.println("SequencerRun not found: " + this.sequencerRunId);
            System.err
                    .println("Please run <MAPSEQ_HOME>/bin/mapseq-list-sequencer-runs.sh and use a valid SequencerRun Identifier.");
            return null;
        }

        if (!read1Fastq.getName().startsWith(sequencerRun.getName())) {
            System.err.println("Invalid fastq name: " + this.read1Fastq.getName());
            System.err.println("Fastq should start with SequencerRun Name");
            return null;
        }

        if (read2Fastq != null && !read2Fastq.getName().startsWith(sequencerRun.getName())) {
            System.err.println("Invalid fastq name: " + this.read2Fastq.getName());
            System.err.println("Fastq should start with SequencerRun Name");
            return null;
        }

        if (!read1Fastq.getName().endsWith(".gz")) {
            System.err.println("Invalid fastq name: " + this.read1Fastq.getName());
            System.err.println("Fastq should end with .gz...is it gzipped?");
            return null;
        }

        if (read2Fastq != null && !read2Fastq.getName().endsWith(".gz")) {
            System.err.println("Invalid fastq name: " + this.read2Fastq.getName());
            System.err.println("Fastq should end with .gz...is it gzipped?");
            return null;
        }

        Pattern pattern = Pattern.compile("^\\d+_.+_\\d+_.+_\\w+_L\\d+_R[1-2]\\.fastq\\.gz$");
        Matcher matcher = pattern.matcher(read1Fastq.getName());
        if (!matcher.matches()) {
            System.err.println("Invalid fastq name: " + this.read1Fastq.getName());
            System.err
                    .println("Please use <date>_<machineID>_<technicianID>_<flowcell>_<barcode>_L<paddedLane>_R<read>.fastq.gz");
            System.err.println("For example: 120110_UNC13-SN749_0141_AD0J7WACXX_ATCACG_L007_R1.fastq.gz");
            return null;
        }

        if (read2Fastq != null) {
            matcher = pattern.matcher(read2Fastq.getName());
            if (!matcher.matches()) {
                System.err.println("Invalid fastq name: " + this.read2Fastq.getName());
                System.err
                        .println("Please use <date>_<machineID>_<technicianID>_<flowcell>_<barcode>_L<paddedLane>_R<read>.fastq.gz");
                System.err.println("For example: 120110_UNC13-SN749_0141_AD0J7WACXX_ATCACG_L007_R1.fastq.gz");
                return null;
            }
        }

        String mapseqOutputDir = System.getenv("MAPSEQ_OUTPUT_DIRECTORY");
        if (StringUtils.isEmpty(mapseqOutputDir)) {
            System.err.println("MAPSEQ_OUTPUT_DIRECTORY not set in env: " + mapseqOutputDir);
            return null;
        }

        File mapseqOutputDirectory = new File(mapseqOutputDir);
        if (!mapseqOutputDirectory.exists()) {
            System.err.println("MAPSEQ_OUTPUT_DIRECTORY does not exist: " + mapseqOutputDir);
            return null;
        }

        File sequencerRunOutputDir = new File(mapseqOutputDirectory, sequencerRun.getName());
        File workflowOutputDir = new File(sequencerRunOutputDir, "CASAVA");
        File htsfSampleOutputDir = new File(workflowOutputDir, this.name);

        htsfSampleOutputDir.mkdirs();

        if (!htsfSampleOutputDir.canWrite()) {
            System.err.println("You don't have permission to write to: " + htsfSampleOutputDir.getAbsolutePath());
            return null;
        }

        try {
            File newR1FastqFile = new File(htsfSampleOutputDir, read1Fastq.getName());
            FileUtils.copyFile(read1Fastq, newR1FastqFile);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        if (read2Fastq != null) {
            try {
                File newR2FastqFile = new File(htsfSampleOutputDir, read2Fastq.getName());
                FileUtils.copyFile(read2Fastq, newR2FastqFile);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        Set<FileData> fileDataSet = new HashSet<FileData>();

        try {

            FileData read1FastqFD = new FileData();
            read1FastqFD.setMimeType(MimeType.FASTQ);
            read1FastqFD.setName(read1Fastq.getName());
            read1FastqFD.setPath(read1Fastq.getParentFile().getAbsolutePath());

            List<FileData> fileDataList = daoMgr.getWSDAOBean().getFileDataDAO().findByExample(read1FastqFD);
            if (fileDataList != null && fileDataList.size() > 0) {
                read1FastqFD = fileDataList.get(0);
            } else {
                Long id = daoMgr.getWSDAOBean().getFileDataDAO().save(read1FastqFD);
                read1FastqFD.setId(id);
            }
            fileDataSet.add(read1FastqFD);

            if (read2Fastq != null) {
                FileData read2FastqFD = new FileData();
                read2FastqFD.setMimeType(MimeType.FASTQ);
                read2FastqFD.setName(read2Fastq.getName());
                read2FastqFD.setPath(read2Fastq.getParentFile().getAbsolutePath());
                fileDataList = daoMgr.getWSDAOBean().getFileDataDAO().findByExample(read2FastqFD);
                if (fileDataList != null && fileDataList.size() > 0) {
                    read2FastqFD = fileDataList.get(0);
                } else {
                    Long id = daoMgr.getWSDAOBean().getFileDataDAO().save(read2FastqFD);
                    read2FastqFD.setId(id);
                }
                fileDataSet.add(read2FastqFD);
            }

            HTSFSampleDAO htsfSampleDAO = daoMgr.getWSDAOBean().getHTSFSampleDAO();

            HTSFSample htsfSample = new HTSFSample();
            htsfSample.setName(getName());
            htsfSample.setCreator(daoMgr.getWSDAOBean().getAccountDAO().findByName(System.getProperty("user.name")));
            Date creationDate = new Date();
            htsfSample.setCreationDate(creationDate);
            htsfSample.setModificationDate(creationDate);
            htsfSample.setBarcode(barcode);
            htsfSample.setStudy(daoMgr.getWSDAOBean().getStudyDAO().findById(this.studyId));
            htsfSample.setLaneIndex(laneIndex);
            htsfSample.setSequencerRun(sequencerRun);
            htsfSample.setFileDatas(fileDataSet);
            Long id = htsfSampleDAO.save(htsfSample);
            htsfSample.setId(id);
            return id;
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Long getSequencerRunId() {
        return sequencerRunId;
    }

    public void setSequencerRunId(Long sequencerRunId) {
        this.sequencerRunId = sequencerRunId;
    }

    public Integer getLaneIndex() {
        return laneIndex;
    }

    public void setLaneIndex(Integer laneIndex) {
        this.laneIndex = laneIndex;
    }

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    public File getRead1Fastq() {
        return read1Fastq;
    }

    public void setRead1Fastq(File read1Fastq) {
        this.read1Fastq = read1Fastq;
    }

    public File getRead2Fastq() {
        return read2Fastq;
    }

    public void setRead2Fastq(File read2Fastq) {
        this.read2Fastq = read2Fastq;
    }

    @SuppressWarnings("static-access")
    public static void main(String[] args) {

        // optional
        cliOptions.addOption(OptionBuilder.withLongOpt("read2Fastq").hasArg().create());

        // required
        cliOptions.addOption(OptionBuilder.withLongOpt("name").isRequired().hasArg().create());
        cliOptions.addOption(OptionBuilder.withLongOpt("barcode").isRequired().hasArg().create());
        cliOptions.addOption(OptionBuilder.withLongOpt("read1Fastq").isRequired().hasArg().create());
        cliOptions.addOption(OptionBuilder.withLongOpt("studyId").isRequired().hasArg().create());
        cliOptions.addOption(OptionBuilder.withLongOpt("sequencerRunId").isRequired().hasArg().create());
        cliOptions.addOption(OptionBuilder.withLongOpt("laneIndex").isRequired().hasArg().create());

        cliOptions.addOption(OptionBuilder.withLongOpt("help").withDescription("print this help message").create("?"));

        CommandLineParser commandLineParser = new GnuParser();
        CreateHTSFSample main = new CreateHTSFSample();
        try {
            CommandLine commandLine = commandLineParser.parse(cliOptions, args);
            if (commandLine.hasOption("?")) {
                helpFormatter.printHelp(main.getClass().getSimpleName(), cliOptions);
                return;
            }

            main.setName(commandLine.getOptionValue("name"));
            main.setBarcode(commandLine.getOptionValue("barcode"));
            main.setSequencerRunId(Long.valueOf(commandLine.getOptionValue("sequencerRunId")));
            main.setLaneIndex(Integer.valueOf(commandLine.getOptionValue("laneIndex")));
            main.setStudyId(Long.valueOf(commandLine.getOptionValue("studyId")));

            File read1FastqFile = new File(commandLine.getOptionValue("read1Fastq"));
            if (!read1FastqFile.exists()) {
                throw new ParseException("read1Fastq does not exist...use absolute path");
            }
            main.setRead1Fastq(read1FastqFile);

            if (commandLine.hasOption("read2Fastq")) {
                File read2FastqFile = new File(commandLine.getOptionValue("read2Fastq"));
                if (!read2FastqFile.exists()) {
                    throw new ParseException("read2Fastq does not exist...use absolute path");
                }
                main.setRead2Fastq(read2FastqFile);
            }

            Long id = main.call();
            if (id == null) {
                System.exit(-1);
            }
            System.out.println("HTSFSample ID: " + main.call());
        } catch (ParseException e) {
            System.err.println(("Parsing Failed: " + e.getMessage()));
            helpFormatter.printHelp(main.getClass().getSimpleName(), cliOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
