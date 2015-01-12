package edu.unc.mapseq.tasks;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeeklyReportService {

    private final Logger logger = LoggerFactory.getLogger(WeeklyReportService.class);

    private final ScheduledExecutorService scheduler;

    private WeeklyReportTask task;

    private Boolean enabled = Boolean.TRUE;

    public WeeklyReportService() {
        super();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() throws Exception {
        logger.debug("ENTERING start()");
        logger.info("enabled: {}", enabled.toString());
        if (enabled) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
            c.set(Calendar.HOUR_OF_DAY, 1);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.add(Calendar.DATE, 7);
            long delay = c.getTimeInMillis() - System.currentTimeMillis();
            long period = 1000 * 60 * 60 * 24 * 7;
            scheduler.scheduleAtFixedRate(task, delay, period, TimeUnit.MILLISECONDS);
        }
    }

    public void stop() throws Exception {
        logger.info("ENTERING stop()");
        scheduler.shutdownNow();
    }

    public WeeklyReportTask getTask() {
        return task;
    }

    public void setTask(WeeklyReportTask task) {
        this.task = task;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

}
