package edu.unc.mapseq.commands.migration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.FileDataDAO;
import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBeanService;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.model.Sample;

public class MigrateNECFileDatasRunnable implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(MigrateNECFileDatasRunnable.class);

    private Boolean dryRun = Boolean.FALSE;

    private MaPSeqDAOBeanService maPSeqDAOBeanService;

    public MigrateNECFileDatasRunnable(Boolean dryRun, MaPSeqDAOBeanService maPSeqDAOBeanService) {
        super();
        this.dryRun = dryRun;
        this.maPSeqDAOBeanService = maPSeqDAOBeanService;
    }

    @Override
    public void run() {
        logger.debug("ENTERING run()");

        try (BufferedWriter restorationScript = new BufferedWriter(
                new FileWriter(new File("/tmp", "mpsNECRestoration.sh")))) {
            
            FlowcellDAO flowcellDAO = maPSeqDAOBeanService.getFlowcellDAO();
            SampleDAO sampleDAO = maPSeqDAOBeanService.getSampleDAO();
            FileDataDAO fileDataDAO = maPSeqDAOBeanService.getFileDataDAO();

            List<Flowcell> flowcells = flowcellDAO.findAll();

            String basePath = System.getenv("MAPSEQ_OUTPUT_DIRECTORY");
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

                                        if (!path
                                                .startsWith(String.format("%s/%s/NEC", basePath, flowcell.getName()))) {
                                            continue;
                                        }

                                        String outputDirectory = String.format("%s/%s/L%03d_%s/NEC", basePath,
                                                flowcell.getName(), sample.getLaneIndex(), sample.getBarcode());

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

                        }

                        for (Sample sample : samples) {

                            logger.debug(sample.toString());

                            File sampleDir = new File(String.format("%s/%s/NEC/L%03d_%s", basePath, flowcell.getName(),
                                    sample.getLaneIndex(), sample.getBarcode()));

                            File[] files = sampleDir.listFiles();
                            if (files != null && files.length > 0) {

                                for (File file : files) {

                                    if (file.isDirectory()) {
                                        continue;
                                    }

                                    File workflowDirectory = new File(String.format("%s/%s/L%03d_%s/NEC", basePath,
                                            flowcell.getName(), sample.getLaneIndex(), sample.getBarcode()));
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

                    }

                }
            }

            restorationScript.flush();
            restorationScript.close();
            logger.info("DONE");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

    }

}
