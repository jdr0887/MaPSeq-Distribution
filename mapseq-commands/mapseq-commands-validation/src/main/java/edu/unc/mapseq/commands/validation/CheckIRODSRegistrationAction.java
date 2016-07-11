package edu.unc.mapseq.commands.validation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.renci.common.exec.BashExecutor;
import org.renci.common.exec.CommandInput;
import org.renci.common.exec.CommandOutput;
import org.renci.common.exec.Executor;
import org.renci.common.exec.ExecutorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.FileDataDAO;
import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.StudyDAO;
import edu.unc.mapseq.dao.WorkflowDAO;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.model.Sample;
import edu.unc.mapseq.dao.model.Study;
import edu.unc.mapseq.dao.model.Workflow;

@Command(scope = "mapseq", name = "check-irods-registration", description = "Check IRODS Registration")
@Service
public class CheckIRODSRegistrationAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(CheckIRODSRegistrationAction.class);

    private static final List<String> participantIdList = Arrays.asList("028-1", "029-1", "029-2", "029-3", "029-4", "040-1", "044-1",
            "046-1", "047-1", "053-1", "061-1", "068-1", "070-1", "076-2", "077-1", "088-1", "098-1", "099-1", "122-1", "123-1",
            "30072-1TWA", "30072-2TWA", "30072-3TWA", "30072-M1TWA", "30072-M1TWB", "32837-1TWA", "32837-2TWA", "32837-3TWA", "32837-M1TWA",
            "32837-M1TWB", "35840-1TWA", "35840-2TWA", "35840-3TWA", "35840-M1TWA", "35840-M1TWB", "FES-0001", "FES-0001-1", "FES-0001-2",
            "FES-0002", "FES-0003", "FES-0004", "FES-0005", "FES-0005-1", "FES-0005-2", "FES-0006", "FES-0006-1", "FES-0006-2", "FES-0007",
            "FES-0007-1", "FES-0007-2", "FES-002-1", "FES-002-2", "FES-003-1", "FES-003-2", "FES-004-1", "FES-004-2", "NCG_00001",
            "NCG_00002", "NCG_00007", "NCG_00009", "NCG_00010", "NCG_00011", "NCG_00012", "NCG_00014", "NCG_00017", "NCG_00020",
            "NCG_00024", "NCG_00039", "NCG_00040", "NCG_00041", "NCG_00044", "NCG_00046", "NCG_00048", "NCG_00049", "NCG_00061",
            "NCG_00062", "NCG_00064", "NCG_00065", "NCG_00066", "NCG_00067", "NCG_00068", "NCG_00070", "NCG_00071", "NCG_00073",
            "NCG_00079", "NCG_00080", "NCG_00081", "NCG_00082", "NCG_00083", "NCG_00084", "NCG_00087", "NCG_00091", "NCG_00094",
            "NCG_00095", "NCG_00096", "NCG_00097", "NCG_00098", "NCG_00099", "NCG_00101", "NCG_00102", "NCG_00104", "NCG_00105",
            "NCG_00106", "NCG_00108", "NCG_00109", "NCG_00110", "NCG_00112", "NCG_00121", "NCG_00124", "NCG_00126", "NCG_00127",
            "NCG_00134", "NCG_00135", "NCG_00136", "NCG_00139", "NCG_00140", "NCG_00142", "NCG_00145", "NCG_00147", "NCG_00151",
            "NCG_00153", "NCG_00155", "NCG_00156", "NCG_00157", "NCG_00158", "NCG_00161", "NCG_00162", "NCG_00169", "NCG_00171",
            "NCG_00174", "NCG_00178", "NCG_00183", "NCG_00185", "NCG_00191", "NCG_00192", "NCG_00197", "NCG_00202", "NCG_00203",
            "NCG_00208", "NCG_00209", "NCG_00210", "NCG_00211", "NCG_00212", "NCG_00213", "NCG_00215", "NCG_00216", "NCG_00221",
            "NCG_00223", "NCG_00224", "NCG_00225", "NCG_00226", "NCG_00230", "NCG_00231", "NCG_00232", "NCG_00233", "NCG_00235",
            "NCG_00236", "NCG_00237", "NCG_00238", "NCG_00239", "NCG_00240", "NCG_00241", "NCG_00242", "NCG_00243", "NCG_00245",
            "NCG_00246", "NCG_00248", "NCG_00252", "NCG_00253", "NCG_00254", "NCG_00256", "NCG_00257", "NCG_00260", "NCG_00261",
            "NCG_00263", "NCG_00274", "NCG_00275", "NCG_00280", "NCG_00284", "NCG_00286", "NCG_00291", "NCG_00297", "NCG_00298",
            "NCG_00300", "NCG_00303", "NCG_00304", "NCG_00309", "NCG_00311", "NCG_00312", "NCG_00314", "NCG_00317", "NCG_00318",
            "NCG_00319", "NCG_00321", "NCG_00322", "NCG_00323", "NCG_00324", "NCG_00325", "NCG_00327", "NCG_00330", "NCG_00334",
            "NCG_00345", "NCG_00352", "NCG_00353", "NCG_00354", "NCG_00355", "NCG_00357", "NCG_00359", "NCG_00361", "NCG_00362",
            "NCG_00363", "NCG_00364", "NCG_00365", "NCG_00366", "NCG_00368", "NCG_00369", "NCG_00370", "NCG_00373", "NCG_00374",
            "NCG_00380", "NCG_00384", "NCG_00385", "NCG_00386", "NCG_00387", "NCG_00389", "NCG_00392", "NCG_00393", "NCG_00394",
            "NCG_00397", "NCG_00398", "NCG_00399", "NCG_00401", "NCG_00402", "NCG_00404", "NCG_00406", "NCG_00408", "NCG_00409",
            "NCG_00411", "NCG_00415", "NCG_00418", "NCG_00420", "NCG_00421", "NCG_00423", "NCG_00432", "NCG_00434", "NCG_00435",
            "NCG_00436", "NCG_00437", "NCG_00439", "NCG_00440", "NCG_00442", "NCG_00443", "NCG_00444", "NCG_00448", "NCG_00449",
            "NCG_00450", "NCG_00451", "NCG_00452", "NCG_00454", "NCG_00457", "NCG_00458", "NCG_00459", "NCG_00460", "NCG_00461",
            "NCG_00462", "NCG_00463", "NCG_00464", "NCG_00468", "NCG_00471", "NCG_00473", "NCG_00474", "NCG_00475", "NCG_00476",
            "NCG_00478", "NCG_00479", "NCG_00481", "NCG_00483", "NCG_00484", "NCG_00487", "NCG_00490", "NCG_00492", "NCG_00493",
            "NCG_00494", "NCG_00495", "NCG_00496", "NCG_00497", "NCG_00498", "NCG_00499", "NCG_00500", "NCG_00501", "NCG_00503",
            "NCG_00505", "NCG_00506", "NCG_00507", "NCG_00508", "NCG_00509", "NCG_00511", "NCG_00513", "NCG_00514", "NCG_00516",
            "NCG_00517", "NCG_00522", "NCG_00524", "NCG_00525", "NCG_00526", "NCG_00527", "NCG_00528", "NCG_00529", "NCG_00530",
            "NCG_00531", "NCG_00534", "NCG_00536", "NCG_00537", "NCG_00540", "NCG_00541", "NCG_00542", "NCG_00543", "NCG_00544",
            "NCG_00546", "NCG_00547", "NCG_00548", "NCG_00549", "NCG_00551", "NCG_00552", "NCG_00553", "NCG_00555", "NCG_00556",
            "NCG_00558", "NCG_00560", "NCG_00561", "NCG_00562", "NCG_00564", "NCG_00565", "NCG_00567", "NCG_00569", "NCG_00571",
            "NCG_00573", "NCG_00574", "NCG_00575", "NCG_00577", "NCG_00578", "NCG_00579", "NCG_00584", "NCG_00586", "NCG_00588",
            "NCG_00589", "NCG_00590", "NCG_00591", "NCG_00592", "NCG_00593", "NCG_00594", "NCG_00595", "NCG_00596", "NCG_00598",
            "NCG_00604", "NCG_00605", "NCG_00606", "NCG_00607", "NCG_00608", "NCG_00609", "NCG_00611", "NCG_00614", "NCG_00615",
            "NCG_00620", "NCG_00624", "NCG_00625", "NCG_00627", "NCG_00628", "NCG_00631", "NCG_00632", "NCG_00640", "NCG_00642",
            "NCG_00644", "NCG_00645", "NCG_00646", "NCG_00649", "NCG_00650", "NCG_00652", "NCG_00653", "NCG_00654", "NCG_00655",
            "NCG_00656", "NCG_00657", "NCG_00659", "NCG_00660", "NCG_00661", "NCG_00663", "NCG_00664", "NCG_00665", "NCG_00667",
            "NCG_00669", "NCG_00670", "NCG_00671", "NCG_00672", "NCG_00673", "NCG_00674", "NCG_00675", "NCG_00676", "NCG_00678",
            "NCG_00680", "NCG_00682", "NCG_00683", "NCG_00687", "NCG_00689", "NCG_00700", "NCG_00701", "NCG_00703", "NCG_00704",
            "NCG_00705", "NCG_00707", "NCG_00709", "NCG_00710", "NCG_00711", "NCG_00712", "NCG_00713", "NCG_00714", "NCG_00716",
            "NCG_00718", "NCG_00719", "NCG_00721", "NCG_00722", "NCG_00724", "NCG_00727", "NCG_00728", "NCG_00730", "NCG_00731",
            "NCG_00732", "NCG_00733", "NCG_00738", "NCG_00739", "NCG_00740", "NCG_00741", "NCG_00742", "NCG_00744", "NCG_00745",
            "NCG_00746", "NCG_00751", "NCG_00752", "NCG_00756", "NCG_00758", "NCG_00759", "NCG_00765", "NCG_00766", "NCG_00767",
            "NCG_00768", "NCG_00769", "NCG_00770", "NCG_00771", "NCG_00773", "NCG_00778", "NCG_00781", "NCG_00782", "NCG_00783",
            "NCG_00784", "NCG_00785", "NCG_00786", "NCG_00787", "NCG_00788", "NCG_00790", "NCG_00792", "NCG_00793", "NCG_00794",
            "NCG_00795", "NCG_00798", "NCG_00799", "NCG_00802", "NCG_00804", "NCG_00805", "NCG_00806", "NCG_00807", "NCG_00808",
            "NCG_00810", "NCG_00812", "NCG_00813", "NCG_00814", "NCG_00819", "NCG_00820", "NCG_00822", "NCG_00824", "NCG_00826",
            "NCG_00828", "NCG_00829", "NCG_00830", "NCG_00831", "NCG_00837", "NCG_00838", "NCG_00839", "NCG_00840", "NCG_00841",
            "NCG_00843", "NCG_00844", "NCG_00845", "NCG_00846", "NCG_00850", "NCG_00851", "NCG_00854", "NCG_00856", "NCG_00857",
            "NCG_00858", "NCG_00859", "NCG_00860", "NCG_00863", "NCG_00864", "NCG_00865", "NCG_00866", "NCG_00867", "NCG_00872",
            "NCG_00873", "NCG_00878", "NCG_00880", "NCG_00882", "NCG_00883", "NCG_00887", "NCG_00888", "NCG_00894", "NCG_00896",
            "NCG_00900", "NCG_00904", "NCG_00905", "NCG_00906", "NCG_00908", "NCG_00917", "NCG_00918", "NCG_00919", "NCG_00922",
            "NCG_00925", "NCG_00926", "NCG_00928", "NCG_00929", "NCG_00930", "NCG_00931", "NCG_00933", "NCG_00934", "NCG_00935",
            "NCG_00936", "NCG_00937", "NCG_00942", "NCG_00945", "NCG_00946", "NCG_00947", "NCG_00948", "NCG_00949", "NCG_00952",
            "NCG_00958", "NCG_00960", "NCG_00965", "NCG_00967", "NCG_00971", "NCG_00973", "NCG_00974", "NCG_00975", "NCG_00976",
            "NCG_00977", "NCG_00979", "NCG_00980", "NCG_00982", "NCG_00983", "NCG_00984", "NCG_00985", "NCG_00987", "NCG_00988",
            "NCG_00990", "NCG_00994", "NCG_00995", "NCG_00996", "NCG_00997", "NCG_00998", "NCG_01001", "NCG_01003", "NCG_01007",
            "NCG_01009", "NCG_01013", "NCG_01014", "NCG_01016", "NCG_01020", "NCG_01021", "NCG_01022", "NCG_01023", "NCG_01024",
            "NCG_01027", "NCG_01029", "NCG_01030", "NCG_01031", "NCG_01033", "NCG_01034", "NCG_01035", "NCG_01036", "NCG_01039",
            "NCG_01040", "NCG_01042", "NCG_01043", "NCG_01044", "NCG_01045", "NCG_01046", "NCG_01049", "NCG_01050", "NCG_01051",
            "NCG_01052", "NCG_01053", "NCG_01055", "NCG_01056", "NCG_01057", "NCG_01058", "NCG_01060", "NCG_01061", "NCG_01062",
            "NCG_01069", "NCG_01070", "NCG_01072", "NCG_01073", "NCG_01074", "NCG_01075", "NCG_01076", "NCG_01077", "NCG_01078",
            "NCG_01080", "NCG_01081", "NCG_01086", "NCG_01089", "NCG_01091", "NCG_01092", "NCG_01093", "NCG_01094", "NCG_01097",
            "NCG_01100", "NCG_01104", "NCG_01105", "NCG_01107", "NCG_01109", "NCG_01110", "NCG_01111", "NCG_01113", "NCG_01114",
            "NCG_01115", "NCG_01116", "NCG_01117", "NCG_01118", "NCG_01119", "NCG_01120", "NCG_01121", "NCG_01125", "NCG_01126",
            "NCG_01127", "NCG_01130", "NCG_01131", "NCG_01132", "NCG_01133", "NCG_01134", "NCG_01136", "NCG_01137", "NCG_01140",
            "NCG_01141", "NCG_01143", "NCG_01144", "NCG_01146", "NCG_01149", "NCG_01153", "NCG_01154", "NCG_01157", "NCG_01158",
            "NCG_01161", "NCG_01163", "NCG_01164", "NCG_01167", "NCG_01169", "NCG_01173", "NCG_01179", "NCG_01187", "NCG_01188",
            "NCG_01190", "NCG_01191", "NCG_01193", "NCG_01195", "NCG_01198", "NCG_01210", "NCG_01212", "NCG_01218", "NCG_01219",
            "NCG_01221", "NCG_01223", "NCG_01225", "NCG_01228", "NCG_01233", "NCG_01236", "NCG_01237", "NCG_01240", "NCG_01241",
            "NCG_01244", "NCG_01245", "NCG_01250", "NCG_01251", "NCG_01252", "NCG_01253", "NCG_01254", "NCG_01256", "NCG_01259",
            "NCG_01260", "NCG_01262", "NCG_01265", "NCG_01267", "NCG_01268", "NCG_01269", "OPH_00114", "OPH_00115", "OPH_00117",
            "OPH_00119", "OPH_00122", "OPH_00123", "OPH_00130", "OPH_00132", "OPH_00141", "OPH_00146", "OPH_00165", "OPH_00166",
            "OPH_00167", "OPH_00173", "OPH_00175", "OPH_00179", "OPH_00180", "OPH_00189", "OPH_00196", "OPH_00201", "OPH_00204",
            "OPH_00206", "OPH_00207", "OPH_00217", "OPH_00218", "OPH_00222", "SDN_00001", "SDN_00002");

    @Reference
    private FlowcellDAO flowcellDAO;

    @Reference
    private StudyDAO studyDAO;

    @Reference
    private SampleDAO sampleDAO;

    @Reference
    private WorkflowDAO workflowDAO;

    @Reference
    private FileDataDAO fileDataDAO;

    @Option(name = "--sampleId", description = "sampleId", required = false, multiValued = false)
    private Long sampleId;

    @Option(name = "--includeDuplicateFilter", description = "includeDuplicateFilter", required = false, multiValued = false)
    private Boolean includeDuplicateFilter = Boolean.TRUE;

    public CheckIRODSRegistrationAction() {
        super();
    }

    @Override
    public Object execute() {
        final Set<Sample> samples = new HashSet<Sample>();
        try {
            if (sampleId != null) {
                // do one sample
                Sample sample = sampleDAO.findById(sampleId);
                samples.add(sample);
            } else {
                // do all samples
                List<Study> studyList = studyDAO.findByName("NC_GENES");
                if (CollectionUtils.isNotEmpty(studyList)) {
                    Study ncgenesStudy = studyList.get(0);
                    List<Sample> sampleList = sampleDAO.findByStudyId(ncgenesStudy.getId());
                    if (CollectionUtils.isNotEmpty(sampleList)) {
                        samples.addAll(sampleList);
                    }
                }
            }
        } catch (MaPSeqDAOException e1) {
            e1.printStackTrace();
        }

        final Map<String, Integer> sampleNamesCountMap = new HashMap<String, Integer>();
        for (Sample sample : samples) {
            if (!sampleNamesCountMap.containsKey(sample.getName())) {
                sampleNamesCountMap.put(sample.getName(), 1);
                continue;
            } else {
                sampleNamesCountMap.put(sample.getName(), sampleNamesCountMap.get(sample.getName()) + 1);
            }
        }

        try {
            ExecutorService es = Executors.newSingleThreadExecutor();
            final File irodsValidationFile = new File("/tmp", "irods-validation.txt");
            es.submit(() -> {
                try (FileWriter fw = new FileWriter(irodsValidationFile); BufferedWriter bw = new BufferedWriter(fw)) {
                    if (CollectionUtils.isNotEmpty(samples)) {
                        for (Sample sample : samples) {

                            if ("Undetermined".equals(sample.getBarcode())) {
                                continue;
                            }

                            if (sample.getBarcode().length() > 6) {
                                continue;
                            }

                            if (sample.getName().equals(String.format("L%03d_%s", sample.getLaneIndex(), sample.getBarcode()))) {
                                continue;
                            }

                            if (includeDuplicateFilter && sampleNamesCountMap.get(sample.getName()) > 1) {
                                continue;
                            }

                            int idx = sample.getName().lastIndexOf("-");
                            String participantId = idx != -1 ? sample.getName().substring(0, idx) : sample.getName();

                            if (!participantIdList.contains(participantId)) {
                                continue;
                            }

                            bw.write(sample.toString());
                            bw.newLine();
                            check(bw, sample.getFlowcell(), sample);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            es.shutdown();
            es.awaitTermination(2L, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void check(BufferedWriter bw, Flowcell flowcell, Sample sample) throws IOException, MaPSeqDAOException {

        int idx = sample.getName().lastIndexOf("-");
        String participantId = idx != -1 ? sample.getName().substring(0, idx) : sample.getName();

        String ncgenesIRODSDirectory = String.format("/MedGenZone/sequence_data/ncgenes/%s", participantId);
        CommandOutput commandOutput = checkForDirectoryExistence(ncgenesIRODSDirectory);
        if (commandOutput.getExitCode() != 0) {
            bw.write(ncgenesIRODSDirectory);
            bw.newLine();
        }

        // first check for casava generated files

        String outputDirectory = System.getenv("MAPSEQ_OUTPUT_DIRECTORY");
        Workflow workflow = null;
        List<Workflow> workflowList = workflowDAO.findByName("NCGenesCASAVA");
        if (CollectionUtils.isEmpty(workflowList)) {
            logger.error("Cound not find NCGenesCASAVA workflow");
            return;
        }

        workflow = workflowList.get(0);

        File systemDirectory = new File(outputDirectory, workflow.getSystem().getValue());
        File studyDirectory = new File(systemDirectory, sample.getStudy().getName());
        File analysisDirectory = new File(studyDirectory, "analysis");
        File flowcellDirectory = new File(analysisDirectory, sample.getFlowcell().getName());
        File sampleOutputDir = new File(flowcellDirectory, String.format("L%03d_%s", sample.getLaneIndex(), sample.getBarcode()));
        File workflowDirectory = new File(sampleOutputDir, workflow.getName());

        File fastqR1File = new File(workflowDirectory,
                String.format("%s_%s_L%03d_R%d.fastq.gz", flowcell.getName(), sample.getBarcode(), sample.getLaneIndex(), 1));
        String fastqR1FileInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId, fastqR1File.getName());
        commandOutput = checkForFileExistence(fastqR1FileInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(fastqR1FileInIrods);
            bw.newLine();
        }

        File fastqR2File = new File(workflowDirectory,
                String.format("%s_%s_L%03d_R%d.fastq.gz", flowcell.getName(), sample.getBarcode(), sample.getLaneIndex(), 2));
        String fastqR2FileInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId, fastqR2File.getName());
        commandOutput = checkForFileExistence(fastqR2FileInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(fastqR2FileInIrods);
            bw.newLine();
        }

        // first check for baseline generated files
        workflowList = workflowDAO.findByName("NCGenesBaseline");
        if (CollectionUtils.isEmpty(workflowList)) {
            logger.error("Cound not find NCGenesBaseline workflow");
            return;
        }

        workflow = workflowList.get(0);

        systemDirectory = new File(outputDirectory, workflow.getSystem().getValue());
        studyDirectory = new File(systemDirectory, sample.getStudy().getName());
        analysisDirectory = new File(studyDirectory, "analysis");
        flowcellDirectory = new File(analysisDirectory, sample.getFlowcell().getName());
        sampleOutputDir = new File(flowcellDirectory, String.format("L%03d_%s", sample.getLaneIndex(), sample.getBarcode()));
        workflowDirectory = new File(sampleOutputDir, workflow.getName());

        String fastqLaneRootName = String.format("%s_%s_L%03d", flowcell.getName(), sample.getBarcode(), sample.getLaneIndex());

        File writeVCFHeaderOut = new File(workflowDirectory, fastqLaneRootName + ".vcf.hdr");
        String writeVCFHeaderOutInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId,
                writeVCFHeaderOut.getName());
        commandOutput = checkForFileExistence(writeVCFHeaderOutInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(writeVCFHeaderOutInIrods);
            bw.newLine();
        }

        File fastqcR1Output = new File(workflowDirectory, fastqLaneRootName + "_R1.fastqc.zip");
        String fastqcR1OutputInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId, fastqcR1Output.getName());
        commandOutput = checkForFileExistence(fastqcR1OutputInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(fastqcR1OutputInIrods);
            bw.newLine();
        }

        File fastqcR2Output = new File(workflowDirectory, fastqLaneRootName + "_R1.fastqc.zip");
        String fastqcR2OutputInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId, fastqcR2Output.getName());
        commandOutput = checkForFileExistence(fastqcR2OutputInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(fastqcR2OutputInIrods);
            bw.newLine();
        }

        File bwaSAMPairedEndOutFile = new File(workflowDirectory, fastqLaneRootName + ".sam");
        File fixRGOutput = new File(workflowDirectory, bwaSAMPairedEndOutFile.getName().replace(".sam", ".fixed-rg.bam"));
        File picardMarkDuplicatesOutput = new File(workflowDirectory, fixRGOutput.getName().replace(".bam", ".deduped.bam"));
        File indelRealignerOut = new File(workflowDirectory, picardMarkDuplicatesOutput.getName().replace(".bam", ".realign.bam"));
        File picardFixMateOutput = new File(workflowDirectory, indelRealignerOut.getName().replace(".bam", ".fixmate.bam"));
        File gatkTableRecalibrationOut = new File(workflowDirectory, picardFixMateOutput.getName().replace(".bam", ".recal.bam"));
        String gatkTableRecalibrationOutInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId,
                gatkTableRecalibrationOut.getName());
        commandOutput = checkForFileExistence(gatkTableRecalibrationOutInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(gatkTableRecalibrationOutInIrods);
            bw.newLine();
        }

        File gatkTableRecalibrationIndexOut = new File(workflowDirectory, gatkTableRecalibrationOut.getName().replace(".bam", ".bai"));
        String gatkTableRecalibrationIndexOutInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId,
                gatkTableRecalibrationIndexOut.getName());
        commandOutput = checkForFileExistence(gatkTableRecalibrationIndexOutInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(gatkTableRecalibrationIndexOutInIrods);
            bw.newLine();
        }

        File sampleCumulativeCoverageCountsFile = new File(workflowDirectory,
                gatkTableRecalibrationOut.getName().replace(".bam", ".coverage.sample_cumulative_coverage_counts"));
        String sampleCumulativeCoverageCountsFileInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId,
                sampleCumulativeCoverageCountsFile.getName());
        commandOutput = checkForFileExistence(sampleCumulativeCoverageCountsFileInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(sampleCumulativeCoverageCountsFileInIrods);
            bw.newLine();
        }

        File sampleCumulativeCoverageProportionsFile = new File(workflowDirectory,
                gatkTableRecalibrationOut.getName().replace(".bam", ".coverage.sample_cumulative_coverage_proportions"));
        String sampleCumulativeCoverageProportionsFileInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId,
                sampleCumulativeCoverageProportionsFile.getName());
        commandOutput = checkForFileExistence(sampleCumulativeCoverageProportionsFileInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(sampleCumulativeCoverageProportionsFileInIrods);
            bw.newLine();
        }

        File sampleIntervalStatisticsFile = new File(workflowDirectory,
                gatkTableRecalibrationOut.getName().replace(".bam", ".coverage.sample_interval_statistics"));
        String sampleIntervalStatisticsFileInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId,
                sampleIntervalStatisticsFile.getName());
        commandOutput = checkForFileExistence(sampleIntervalStatisticsFileInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(sampleIntervalStatisticsFileInIrods);
            bw.newLine();
        }

        File sampleIntervalSummaryFile = new File(workflowDirectory,
                gatkTableRecalibrationOut.getName().replace(".bam", ".coverage.sample_interval_summary"));
        String sampleIntervalSummaryFileInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId,
                sampleIntervalSummaryFile.getName());
        commandOutput = checkForFileExistence(sampleIntervalSummaryFileInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(sampleIntervalSummaryFileInIrods);
            bw.newLine();
        }

        File sampleStatisticsFile = new File(workflowDirectory,
                gatkTableRecalibrationOut.getName().replace(".bam", ".coverage.sample_statistics"));
        String sampleStatisticsFileInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId,
                sampleStatisticsFile.getName());
        commandOutput = checkForFileExistence(sampleStatisticsFileInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(sampleStatisticsFileInIrods);
            bw.newLine();
        }

        File sampleSummaryFile = new File(workflowDirectory,
                gatkTableRecalibrationOut.getName().replace(".bam", ".coverage.sample_summary"));
        String sampleSummaryFileInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId,
                sampleSummaryFile.getName());
        commandOutput = checkForFileExistence(sampleSummaryFileInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(sampleSummaryFileInIrods);
            bw.newLine();
        }

        File samtoolsFlagstatOut = new File(workflowDirectory, gatkTableRecalibrationOut.getName().replace(".bam", ".samtools.flagstat"));
        String samtoolsFlagstatOutInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId,
                samtoolsFlagstatOut.getName());
        commandOutput = checkForFileExistence(samtoolsFlagstatOutInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(samtoolsFlagstatOutInIrods);
            bw.newLine();
        }

        File gatkFlagstatOut = new File(workflowDirectory, gatkTableRecalibrationOut.getName().replace(".bam", ".gatk.flagstat"));
        String gatkFlagstatOutInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId, gatkFlagstatOut.getName());
        commandOutput = checkForFileExistence(gatkFlagstatOutInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(gatkFlagstatOutInIrods);
            bw.newLine();
        }

        File filterVariant1Output = new File(workflowDirectory, gatkTableRecalibrationOut.getName().replace(".bam", ".variant.vcf"));
        File gatkApplyRecalibrationOut = new File(workflowDirectory,
                filterVariant1Output.getName().replace(".vcf", ".recalibrated.filtered.vcf"));
        String gatkApplyRecalibrationOutInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId,
                gatkApplyRecalibrationOut.getName());
        commandOutput = checkForFileExistence(gatkApplyRecalibrationOutInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(gatkApplyRecalibrationOutInIrods);
            bw.newLine();
        }

        File filterVariant2Output = new File(workflowDirectory, filterVariant1Output.getName().replace(".vcf", ".ic_snps.vcf"));
        String filterVariant2OutputInIrods = String.format("/MedGenZone/sequence_data/ncgenes/%s/%s", participantId,
                filterVariant2Output.getName());
        commandOutput = checkForFileExistence(filterVariant2OutputInIrods);
        if (commandOutput.getExitCode() != 0) {
            bw.write(filterVariant2OutputInIrods);
            bw.newLine();
        }
        bw.flush();

    }

    private CommandOutput checkForDirectoryExistence(String directory) {
        CommandOutput ret = null;
        try {
            File mapseqrc = new File(System.getProperty("user.home"), ".mapseqrc");
            Executor executor = BashExecutor.getInstance();
            CommandInput commandInput = new CommandInput(String.format("$NCGENESBASELINE_IRODS_HOME/ils %s%n", directory));
            ret = executor.execute(commandInput, mapseqrc);
        } catch (ExecutorException e) {
            logger.error(e.getMessage(), e);
        }
        return ret;
    }

    private CommandOutput checkForFileExistence(String file) {
        CommandOutput ret = null;
        try {
            File mapseqrc = new File(System.getProperty("user.home"), ".mapseqrc");
            Executor executor = BashExecutor.getInstance();
            CommandInput commandInput = new CommandInput(String.format("$NCGENESBASELINE_IRODS_HOME/ils %s%n", file));
            ret = executor.execute(commandInput, mapseqrc);
        } catch (ExecutorException e) {
            logger.error(e.getMessage(), e);
        }
        return ret;
    }

    public Long getSampleId() {
        return sampleId;
    }

    public void setSampleId(Long sampleId) {
        this.sampleId = sampleId;
    }

    public Boolean getIncludeDuplicateFilter() {
        return includeDuplicateFilter;
    }

    public void setIncludeDuplicateFilter(Boolean includeDuplicateFilter) {
        this.includeDuplicateFilter = includeDuplicateFilter;
    }

}
