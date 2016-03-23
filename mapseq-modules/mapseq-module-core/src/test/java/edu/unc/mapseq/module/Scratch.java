package edu.unc.mapseq.module;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;

import org.junit.Test;

public class Scratch {

    @Test
    public void scratch() {

        System.out.println(String.format("%2$s%1$s%3$s%1$s%4$s", File.separator, "asdf", "generate-sources", "modules"));

        // DecimalFormat df = new DecimalFormat();
        // df.setMaximumFractionDigits(6);
        // System.out.println(df.format(Double.valueOf("-0.0001")));
        // System.out.println(String.format("%f", Double.valueOf("-0.0001")));
    }

}
