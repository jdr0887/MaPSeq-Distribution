package edu.unc.mapseq.commands;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

public class Scratch {

    @Test
    public void test() {

        try {
            String s = "06/30/14 12:38:16 All jobs Completed!";
            String[] lineSplit = s.split(" ");
            Date endDate = DateUtils.parseDate(String.format("%s %s", lineSplit[0], lineSplit[1]),
                    new String[] { "MM/dd/yy HH:mm:ss" });
            System.out.println(endDate.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
