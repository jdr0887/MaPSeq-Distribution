package edu.unc.mapseq.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.SftpProgressMonitor;

public class TransferProgressMonitor implements SftpProgressMonitor {

    private final Logger logger = LoggerFactory.getLogger(TransferProgressMonitor.class);

    private Long totalBytes = 0L;

    private Long startTime;

    private Long endTime;

    public TransferProgressMonitor() {
    }

    public void init(int op, String src, String dest, long max) {
        logger.debug("STARTED");
        this.startTime = System.currentTimeMillis();
        logger.info(String.format("source: %s, destination: %s, max: %d", src, dest, max));
    }

    public boolean count(long bytes) {
        logger.debug("bytes: {}", bytes);
        totalBytes += bytes;
        return true;
    }

    public void end() {
        this.endTime = System.currentTimeMillis();
        Long duration = (endTime - startTime);
        if (duration == 0) {
            duration = 1L;
        }
        logger.info(String.format("rate: %d bytes per second", totalBytes / (duration / 1000)));
        logger.info(String.format("duration: %d, totalBytes: %d", duration, totalBytes));
        logger.debug("\nFINISHED!");
    }

}
