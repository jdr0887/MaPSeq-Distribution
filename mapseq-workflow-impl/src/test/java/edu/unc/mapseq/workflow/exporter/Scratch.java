package edu.unc.mapseq.workflow.exporter;

import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class Scratch {

    @Test
    public void scratch() {
        String file = "151123_UNC16-SN851_0629_AH5FG2ADXX_CASAVA_R2.fastq.gz";
        Pattern pattern = Pattern.compile("^.+_R2\\.fastq\\.gz$");
        Matcher matcher = pattern.matcher(file);
        assertTrue(matcher.matches());
    }
}
