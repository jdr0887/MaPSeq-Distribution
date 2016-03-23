package edu.unc.mapseq.module;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.biojava3.sequencing.io.fastq.Fastq;
import org.biojava3.sequencing.io.fastq.FastqReader;
import org.biojava3.sequencing.io.fastq.FastqTools;
import org.biojava3.sequencing.io.fastq.SangerFastqReader;
import org.biojava3.sequencing.io.fastq.StreamListener;
import org.junit.Test;

import com.google.common.io.Files;
import com.google.common.io.InputSupplier;

public class Scratch {

    @Test
    public void scratch() {

        System.out.println(String.format("%2$s%1$s%3$s%1$s%4$s", File.separator, "asdf", "generate-sources", "modules"));

        // DecimalFormat df = new DecimalFormat();
        // df.setMaximumFractionDigits(6);
        // System.out.println(df.format(Double.valueOf("-0.0001")));
        // System.out.println(String.format("%f", Double.valueOf("-0.0001")));
    }

    @Test
    public void testCalculatePMeanScore() {
        FastqReader fastqReader = new SangerFastqReader();
        InputSupplier inputSupplier = Files.newReaderSupplier(new File("sanger.fastq"), Charset.defaultCharset());
        final SummaryStatistics stats = new SummaryStatistics();
        final StringBuilder sb = new StringBuilder(512);

        try {
            fastqReader.stream(inputSupplier, new StreamListener() {
                @Override
                public void fastq(final Fastq fastq) {
                    stats.clear();
                    int size = fastq.getSequence().length();
                    double[] errorProbabilities = FastqTools.errorProbabilities(fastq, new double[size]);
                    for (int i = 0; i < size; i++) {
                        stats.addValue(errorProbabilities[i]);
                    }
                    sb.delete(0, sb.length());
                    sb.append(fastq.getDescription());
                    sb.append("\t");
                    sb.append(stats.getMean());
                    sb.append("\t");
                    sb.append(stats.getStandardDeviation());
                    System.out.println(sb.toString());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
