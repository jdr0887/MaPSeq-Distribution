package edu.unc.mapseq.commands.migration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.FileDataDAO;
import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBeanService;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.WorkflowDAO;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.model.Sample;
import edu.unc.mapseq.dao.model.Workflow;

public class MigrateFileDatasRunnable implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(MigrateFileDatasRunnable.class);

    private Boolean dryRun = Boolean.FALSE;

    private MaPSeqDAOBeanService maPSeqDAOBeanService;

    public MigrateFileDatasRunnable(Boolean dryRun, MaPSeqDAOBeanService maPSeqDAOBeanService) {
        super();
        this.dryRun = dryRun;
        this.maPSeqDAOBeanService = maPSeqDAOBeanService;
    }

    @Override
    public void run() {
        logger.debug("ENTERING run()");

        try (BufferedWriter restorationScript = new BufferedWriter(
                new FileWriter(new File("/tmp", "mpsRestoration.sh")))) {

            FlowcellDAO flowcellDAO = maPSeqDAOBeanService.getFlowcellDAO();
            SampleDAO sampleDAO = maPSeqDAOBeanService.getSampleDAO();
            FileDataDAO fileDataDAO = maPSeqDAOBeanService.getFileDataDAO();
            WorkflowDAO workflowDAO = maPSeqDAOBeanService.getWorkflowDAO();

            List<Workflow> workflows = workflowDAO.findAll();
            List<Flowcell> flowcells = flowcellDAO.findAll();

            if (CollectionUtils.isEmpty(flowcells)) {
                logger.info("No Flowcells found");
                return;
            }

            for (Flowcell flowcell : flowcells) {

                logger.debug(flowcell.toString());

                List<Sample> samples = sampleDAO.findByFlowcellId(flowcell.getId());

                if (CollectionUtils.isEmpty(samples)) {
                    logger.info("No Samples found");
                    continue;
                }

                for (Sample sample : samples) {

                    logger.debug(sample.toString());

                    Set<FileData> fileDatas = sample.getFileDatas();

                    if (CollectionUtils.isEmpty(fileDatas)) {
                        logger.info("No FileDatas found");
                        continue;
                    }

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
                            String outputDirectory = String.format("%s/%s/L%03d_%s/%s", basePath, flowcell.getName(),
                                    sample.getLaneIndex(), sample.getBarcode(), workflow.getName());

                            File workflowDirectory = new File(outputDirectory);

                            File destinationFile = new File(workflowDirectory, fileData.getName());
                            if (destinationFile.exists()) {
                                continue;
                            }

                            String msg = String.format("moving %s to %s", originalFile.getAbsolutePath(),
                                    destinationFile.getAbsolutePath());
                            logger.info(msg);

                            restorationScript.write(String.format("mv %s %s", destinationFile.getAbsolutePath(),
                                    originalFile.getAbsolutePath()));
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

                                File workflowDirectory = new File(
                                        String.format("%s/%s/L%03d_%s/%s", basePath, flowcell.getName(),
                                                sample.getLaneIndex(), sample.getBarcode(), workflow.getName()));
                                workflowDirectory.mkdirs();

                                File destinationFile = new File(workflowDirectory, file.getName());
                                if (destinationFile.exists()) {
                                    continue;
                                }

                                String msg = String.format("moving %s to %s", file.getAbsolutePath(),
                                        destinationFile.getAbsolutePath());
                                logger.info(msg);

                                restorationScript.write(String.format("mv %s %s", destinationFile.getAbsolutePath(),
                                        file.getAbsolutePath()));
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

                                File workflowDirectory = new File(
                                        String.format("%s/%s/L%03d_%s/%s", basePath, flowcell.getName(),
                                                sample.getLaneIndex(), sample.getBarcode(), workflow.getName()));
                                workflowDirectory.mkdirs();

                                File destinationFile = new File(workflowDirectory, file.getName());

                                if (destinationFile.exists()) {
                                    continue;
                                }

                                String msg = String.format("moving %s to %s", file.getAbsolutePath(),
                                        destinationFile.getAbsolutePath());
                                logger.info(msg);

                                restorationScript.write(String.format("mv %s %s", destinationFile.getAbsolutePath(),
                                        file.getAbsolutePath()));
                                restorationScript.newLine();
                                restorationScript.flush();

                                if (dryRun) {
                                    continue;
                                }

                                FileUtils.moveFile(file, destinationFile);
                            }

                        }

                    }

                    if (!dryRun) {
                        String mpsOutputDir = System.getenv("MAPSEQ_OUTPUT_DIRECTORY");
                        sample.setOutputDirectory(String.format("%s/%s/L%03d_%s", mpsOutputDir, flowcell.getName(),
                                sample.getLaneIndex(), sample.getBarcode()));
                        sampleDAO.save(sample);
                    }

                }
            }

            restorationScript.flush();
            logger.info("DONE");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

    }

}
