package edu.unc.mapseq.tasks;

import java.util.Calendar;

import org.junit.Test;

public class Scratch {

    @Test
    public void test() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.add(Calendar.DATE, 7);
        long delay = c.getTimeInMillis() - System.currentTimeMillis();
        System.out.println(delay);
    }
}
