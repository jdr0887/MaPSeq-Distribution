package edu.unc.mapseq.main;

import java.util.Arrays;

import org.junit.Test;

public class SFTPTest {

    @Test
    public void testTransfer() {
        TransferOutputFiles sftp = new TransferOutputFiles();
        sftp.setHost("biodev2.its.unc.edu");
        sftp.setRemoteDirectory("/nas02/home/r/c/rc_renci.svc/tmp");
        sftp.setUsername("rc_renci.svc");
        sftp.setFileList(Arrays.asList("/home/jdr0887/tmp/130513_UNC13-SN749_0260_AD1WMHACXX_GTCCGC_L008_R1.fastq"));
        System.out.println(sftp.call());
    }

}
