package edu.unc.mapseq.commands;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.commands.Option;
import org.apache.karaf.shell.console.AbstractAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.FileDataDAO;
import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.WorkflowDAO;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.model.Sample;
import edu.unc.mapseq.dao.model.Workflow;

@Command(scope = "mapseq", name = "migrate-file-datas", description = "Migrate FileData instances")
public class MigrateFileDatasAction extends AbstractAction {

    private final Logger logger = LoggerFactory.getLogger(MigrateFileDatasAction.class);

    @Option(name = "--dryRun", description = "Don't move anything", required = false, multiValued = false)
    private Boolean dryRun = Boolean.FALSE;

    private MaPSeqDAOBean maPSeqDAOBean;

    @Override
    protected Object doExecute() throws Exception {
        logger.debug("ENTERING doExecute()");

        FlowcellDAO flowcellDAO = maPSeqDAOBean.getFlowcellDAO();
        SampleDAO sampleDAO = maPSeqDAOBean.getSampleDAO();
        FileDataDAO fileDataDAO = maPSeqDAOBean.getFileDataDAO();
        WorkflowDAO workflowDAO = maPSeqDAOBean.getWorkflowDAO();
        BufferedWriter restorationScript = new BufferedWriter(new FileWriter(new File("/tmp", "mpsRestoration.sh")));

        List<Workflow> workflows = workflowDAO.findAll();
        List<Flowcell> flowcells = flowcellDAO.findAll();

        if (flowcells != null && !flowcells.isEmpty()) {

            for (Flowcell flowcell : flowcells) {

                logger.debug(flowcell.toString());

                List<Sample> samples = sampleDAO.findByFlowcellId(flowcell.getId());

                if (samples != null && !samples.isEmpty()) {

                    for (Sample sample : samples) {

                        logger.debug(sample.toString());

                        Set<FileData> fileDatas = sample.getFileDatas();

                        if (fileDatas != null && !fileDatas.isEmpty()) {

                            for (FileData fileData : fileDatas) {

                                logger.debug(fileData.toString());

                                String path = fileData.getPath();
                                File originalFile = new File(path, fileData.getName());

                                if (originalFile.exists()) {

                                    Workflow workflow = null;
                                    for (Workflow w : workflows) {
                                        if (path.contains(w.getName())) {
                                            workflow = w;
                                        }
                                    }

                                    if (workflow == null) {
                                        continue;
                                    }

                                    String basePath = path.substring(0, path.indexOf(flowcell.getName()) - 1);

                                    String outputDirectory = String.format("%s/%s/L%03d_%s/%s", basePath,
                                            flowcell.getName(), sample.getLaneIndex(), sample.getBarcode(),
                                            workflow.getName());

                                    File workflowDirectory = new File(outputDirectory);

                                    File destinationFile = new File(workflowDirectory, fileData.getName());

                                    if (destinationFile.exists()) {
                                        continue;
                                    }

                                    String msg = String.format("moving %s to %s", originalFile.getAbsolutePath(),
                                            destinationFile.getAbsolutePath());

                                    logger.info(msg);

                                    restorationScript.write(String.format("mv %s %s",
                                            destinationFile.getAbsolutePath(), originalFile.getAbsolutePath()));
                                    restorationScript.newLine();
                                    restorationScript.flush();

                                    if (dryRun) {
                                        continue;
                                    }

                                    workflowDirectory.mkdirs();
                                    FileUtils.moveFile(originalFile, destinationFile);

                                    fileData.setPath(workflowDirectory.getAbsolutePath());
                                    fileDataDAO.save(fileData);

                                }

                            }

                        }

                        // moving /proj/seq/mapseq/LBG/<flowcell>/<workflow>/<sample>
                        String basePath = System.getenv("MAPSEQ_OUTPUT_DIRECTORY");
                        for (Workflow workflow : workflows) {

                            File sampleDir = new File(String.format("%s/%s/%s/%s", basePath, flowcell.getName(),
                                    workflow.getName(), sample.getName()));

                            File[] files = sampleDir.listFiles();
                            if (files != null && files.length > 0) {

                                for (File file : files) {

                                    if (file.isDirectory()) {
                                        continue;
                                    }

                                    File workflowDirectory = new File(String.format("%s/%s/L%03d_%s/%s", basePath,
                                            flowcell.getName(), sample.getLaneIndex(), sample.getBarcode(),
                                            workflow.getName()));
                                    workflowDirectory.mkdirs();

                                    File destinationFile = new File(workflowDirectory, file.getName());

                                    if (destinationFile.exists()) {
                                        continue;
                                    }

                                    String msg = String.format("moving %s to %s", file.getAbsolutePath(),
                                            destinationFile.getAbsolutePath());

                                    logger.info(msg);

                                    restorationScript.write(String.format("mv %s %s",
                                            destinationFile.getAbsolutePath(), file.getAbsolutePath()));
                                    restorationScript.newLine();
                                    restorationScript.flush();

                                    if (dryRun) {
                                        continue;
                                    }

                                    FileUtils.moveFile(file, destinationFile);
                                }

                            }

                        }

                        // moving /proj/seq/mapseq/LBG/<flowcell>/<workflow>/<lane>_<barcode>
                        for (Workflow workflow : workflows) {

                            File sampleDir = new File(String.format("%s/%s/%s/L%03d_%s", basePath, flowcell.getName(),
                                    workflow.getName(), sample.getLaneIndex(), sample.getBarcode()));

                            File[] files = sampleDir.listFiles();
                            if (files != null && files.length > 0) {

                                for (File file : files) {

                                    if (file.isDirectory()) {
                                        continue;
                                    }

                                    File workflowDirectory = new File(String.format("%s/%s/L%03d_%s/%s", basePath,
                                            flowcell.getName(), sample.getLaneIndex(), sample.getBarcode(),
                                            workflow.getName()));
                                    workflowDirectory.mkdirs();

                                    File destinationFile = new File(workflowDirectory, file.getName());

                                    if (destinationFile.exists()) {
                                        continue;
                                    }

                                    String msg = String.format("moving %s to %s", file.getAbsolutePath(),
                                            destinationFile.getAbsolutePath());
                                    logger.info(msg);

                                    restorationScript.write(String.format("mv %s %s",
                                            destinationFile.getAbsolutePath(), file.getAbsolutePath()));
                                    restorationScript.newLine();
                                    restorationScript.flush();

                                    if (dryRun) {
                                        continue;
                                    }

                                    FileUtils.moveFile(file, destinationFile);
                                }

                            }

                        }

                        String mpsOutputDir = System.getenv("MAPSEQ_OUTPUT_DIRECTORY");
                        sample.setOutputDirectory(String.format("%s/%s/L%03d_%s", mpsOutputDir, flowcell.getName(),
                                sample.getLaneIndex(), sample.getBarcode()));
                        sampleDAO.save(sample);

                    }
                }

            }
        }

        restorationScript.flush();
        restorationScript.close();
        logger.info("DONE");

        return null;
    }

    public Boolean getDryRun() {
        return dryRun;
    }

    public void setDryRun(Boolean dryRun) {
        this.dryRun = dryRun;
    }

    public MaPSeqDAOBean getMaPSeqDAOBean() {
        return maPSeqDAOBean;
    }

    public void setMaPSeqDAOBean(MaPSeqDAOBean maPSeqDAOBean) {
        this.maPSeqDAOBean = maPSeqDAOBean;
    }

}
