package edu.unc.mapseq.dao.ws;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import edu.unc.mapseq.dao.FileDataDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.MimeType;

public class FileDataDAOTest {

    @Test
    public void stressTestFindByExample() {

        List<String> fastqNameList = new ArrayList<String>();

        for (int i = 0; i < 400; ++i) {
            fastqNameList.add(String.format("sample_%d_r1.fastq.gz", i));
            fastqNameList.add(String.format("sample_%d_r2.fastq.gz", i));
        }

        long startTime = System.currentTimeMillis();
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(4);
            for (final String fastqName : fastqNameList) {
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        WSDAOManager daoMgr = WSDAOManager
                                .getInstance("edu/unc/mapseq/dao/ws/mapseq-dao-beans-test.xml");
                        FileDataDAO fileDataDAO = daoMgr.getMaPSeqDAOBeanService().getFileDataDAO();
                        FileData fileData = new FileData(fastqName, "/tmp", MimeType.FASTQ);
                        try {
                            fileDataDAO.save(fileData);
                        } catch (MaPSeqDAOException e) {
                            e.printStackTrace();
                        }
                    }

                });
            }
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println((endTime - startTime) / 1000);

        try {
            ExecutorService executorService = Executors.newFixedThreadPool(4);
            startTime = System.currentTimeMillis();
            final List<FileData> fileDataList = new ArrayList<FileData>();
            for (final String fastqName : fastqNameList) {
                executorService.submit(new Runnable() {

                    @Override
                    public void run() {
                        FileData fileData = new FileData(fastqName, "/tmp", MimeType.FASTQ);

                        try {
                            WSDAOManager daoMgr = WSDAOManager
                                    .getInstance("edu/unc/mapseq/dao/ws/mapseq-dao-beans-test.xml");
                            FileDataDAO fileDataDAO = daoMgr.getMaPSeqDAOBeanService().getFileDataDAO();
                            fileDataList.addAll(fileDataDAO.findByExample(fileData));
                        } catch (MaPSeqDAOException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
            assertTrue(fileDataList.size() == 800);
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        endTime = System.currentTimeMillis();
        System.out.println((endTime - startTime) / 1000);
    }

    @Test
    public void save() {

        WSDAOManager daoMgr = WSDAOManager.getInstance("edu/unc/mapseq/dao/ws/mapseq-dao-beans-test.xml");

        final FileDataDAO fileDataDAO = daoMgr.getMaPSeqDAOBeanService().getFileDataDAO();

        FileData fileData = new FileData("asdf.fastq.gz", "/tmp", MimeType.FASTQ);
        try {
            fileDataDAO.save(fileData);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
    }

}
