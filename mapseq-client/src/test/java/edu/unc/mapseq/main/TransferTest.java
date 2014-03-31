package edu.unc.mapseq.main;

import java.util.Arrays;

import org.junit.Test;

public class TransferTest {

    @Test
    public void testTransferInputFiles() {
        TransferInputFiles transfer = new TransferInputFiles();
        transfer.setHost("biodev1.its.unc.edu");
        transfer.setRemoteDirectory("/nas02/home/j/r/jreilly");
        transfer.setUsername("jreilly");
        transfer.setFileList(Arrays.asList("GenomeAnalysisTK-2.1-8.tgz"));
        System.out.println(transfer.call());
    }

    @Test
    public void testTransferOutputFiles() {
        TransferOutputFiles transfer = new TransferOutputFiles();
        transfer.setHost("biodev1.its.unc.edu");
        transfer.setRemoteDirectory("/nas02/home/j/r/jreilly");
        transfer.setUsername("jreilly");
        transfer.setFileList(Arrays.asList("GenomeAnalysisTK-2.1-8.tgz"));
        System.out.println(transfer.call());
    }

}
