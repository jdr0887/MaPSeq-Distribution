package edu.unc.mapseq.commands.sequencing.sample;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.FileDataDAO;
import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.StudyDAO;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.model.MimeType;
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

    @Argument(index = 5, name = "read1Fastq", description = "Read 1 Fastq File", required = true, multiValued = false)
    private String read1Fastq;

    @Argument(index = 6, name = "read2Fastq", description = "Read 2 Fastq File", required = true, multiValued = false)
    private String read2Fastq;

    @Reference
    private FlowcellDAO flowcellDAO;

    @Reference
    private FileDataDAO fileDataDAO;

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

            File read1FastqFile = new File(read1Fastq);
            if (!read1FastqFile.getName().startsWith(flowcell.getName())) {
                System.err.println("Invalid fastq name: " + read1FastqFile.getName());
                System.err.println("Fastq should start with Flowcell Name");
                return null;
            }

            File read2FastqFile = new File(read2Fastq);
            if (read2FastqFile != null && !read2FastqFile.getName().startsWith(flowcell.getName())) {
                System.err.println("Invalid fastq name: " + read2FastqFile.getName());
                System.err.println("Fastq should start with Flowcell Name");
                return null;
            }

            if (!read1FastqFile.getName().endsWith(".gz")) {
                System.err.println("Invalid fastq name: " + read1FastqFile.getName());
                System.err.println("Fastq should end with .gz...is it gzipped?");
                return null;
            }

            if (read2Fastq != null && !read2FastqFile.getName().endsWith(".gz")) {
                System.err.println("Invalid fastq name: " + read2FastqFile.getName());
                System.err.println("Fastq should end with .gz...is it gzipped?");
                return null;
            }

            Pattern pattern = Pattern.compile("^\\d+_.+_\\d+_.+_\\w+_L\\d+_R[1-2]\\.fastq\\.gz$");
            Matcher matcher = pattern.matcher(read1FastqFile.getName());
            if (!matcher.matches()) {
                System.err.println("Invalid fastq name: " + read1FastqFile.getName());
                System.err.println(
                        "Please use <date>_<machineID>_<technicianID>_<flowcell>_<barcode>_L<paddedLane>_R<read>.fastq.gz");
                System.err.println("For example: 120110_UNC13-SN749_0141_AD0J7WACXX_ATCACG_L007_R1.fastq.gz");
                return null;
            }

            if (read2Fastq != null) {
                matcher = pattern.matcher(read2FastqFile.getName());
                if (!matcher.matches()) {
                    System.err.println("Invalid fastq name: " + read2FastqFile.getName());
                    System.err.println(
                            "Please use <date>_<machineID>_<technicianID>_<flowcell>_<barcode>_L<paddedLane>_R<read>.fastq.gz");
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

            File sequencerRunOutputDir = new File(mapseqOutputDirectory, flowcell.getName());
            File sampleOutputDir = new File(sequencerRunOutputDir, String.format("L%03d_%s", laneIndex, barcode));
            File workflowOutputDir = new File(sampleOutputDir, "CASAVA");
            workflowOutputDir.mkdirs();

            if (!sampleOutputDir.canWrite()) {
                System.err.println("You don't have permission to write to: " + workflowOutputDir.getAbsolutePath());
                return null;
            }

            try {
                File newR1FastqFile = new File(workflowOutputDir, read1FastqFile.getName());
                if (!read1FastqFile.getAbsolutePath().equals(newR1FastqFile.getAbsolutePath())) {
                    FileUtils.copyFile(read1FastqFile, newR1FastqFile);
                }

                if (read2Fastq != null) {
                    File newR2FastqFile = new File(workflowOutputDir, read2FastqFile.getName());
                    if (!read2FastqFile.getAbsolutePath().equals(newR2FastqFile.getAbsolutePath())) {
                        FileUtils.copyFile(read2FastqFile, newR2FastqFile);
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            Set<FileData> fileDataSet = new HashSet<FileData>();

            FileData read1FastqFD = new FileData();
            read1FastqFD.setMimeType(MimeType.FASTQ);
            read1FastqFD.setName(read1FastqFile.getName());
            read1FastqFD.setPath(sampleOutputDir.getAbsolutePath());

            List<FileData> fileDataList = fileDataDAO.findByExample(read1FastqFD);
            if (fileDataList != null && fileDataList.size() > 0) {
                read1FastqFD = fileDataList.get(0);
            } else {
                read1FastqFD.setId(fileDataDAO.save(read1FastqFD));
            }
            fileDataSet.add(read1FastqFD);

            if (read2Fastq != null) {
                FileData read2FastqFD = new FileData();
                read2FastqFD.setMimeType(MimeType.FASTQ);
                read2FastqFD.setName(read2FastqFile.getName());
                read2FastqFD.setPath(sampleOutputDir.getAbsolutePath());
                fileDataList = fileDataDAO.findByExample(read2FastqFD);
                if (CollectionUtils.isNotEmpty(fileDataList)) {
                    read2FastqFD = fileDataList.get(0);
                } else {
                    read2FastqFD.setId(fileDataDAO.save(read2FastqFD));
                }
                fileDataSet.add(read2FastqFD);
            }

            Sample sample = new Sample(name);
            sample.setBarcode(barcode);
            sample.setStudy(studyDAO.findById(this.studyId));
            sample.setLaneIndex(laneIndex);
            sample.setFlowcell(flowcell);
            sample.setId(sampleDAO.save(sample));
            sample.setFileDatas(fileDataSet);
            sampleDAO.save(sample);

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

    public String getRead1Fastq() {
        return read1Fastq;
    }

    public void setRead1Fastq(String read1Fastq) {
        this.read1Fastq = read1Fastq;
    }

    public String getRead2Fastq() {
        return read2Fastq;
    }

    public void setRead2Fastq(String read2Fastq) {
        this.read2Fastq = read2Fastq;
    }

}
