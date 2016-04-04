package edu.unc.mapseq.workflow.core;

import java.io.File;

import org.renci.jlrm.condor.ClassAdvertisement;
import org.renci.jlrm.condor.ClassAdvertisementFactory;
import org.renci.jlrm.condor.CondorJobBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.workflow.WorkflowException;

public class WorkflowJobFactory {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowJobFactory.class);

    public static CondorJobBuilder createJob(int count, Class<?> moduleClass, Long workflowRunAttemptId)
            throws WorkflowException {
        return createJob(count, moduleClass, workflowRunAttemptId, false);
    }

    public static CondorJobBuilder createJob(int count, Class<?> moduleClass, Long workflowRunAttemptId,
            boolean persistFileData) throws WorkflowException {
        return createJob(count, moduleClass, workflowRunAttemptId, persistFileData, 3);
    }

    public static CondorJobBuilder createJob(int count, Class<?> moduleClass, Long workflowRunAttemptId,
            boolean persistFileData, Integer retry) throws WorkflowException {
        logger.debug("ENTERING createJob(int, Class<?>, WorkflowPlan, boolean, Integer)");
        logger.debug("moduleClass.getSimpleName(): {}", moduleClass.getSimpleName());

        File executable = new File("$MAPSEQ_CLIENT_HOME/bin/mapseq-run-module.sh");
        String jobName = String.format("%s_%d", moduleClass.getSimpleName(), count);
        CondorJobBuilder builder = new CondorJobBuilder().name(jobName).executable(executable).retry(retry);

        if (count > 0) {
            builder.priority(count * 10);
        }

        builder.addArgument(moduleClass.getName()).addArgument("--serialize", String.format("%s.xml", jobName));

        for (ClassAdvertisement classAd : ClassAdvertisementFactory.getDefaultClassAds()) {
            try {
                builder.classAdvertisments().add(classAd.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        if (workflowRunAttemptId != null) {
            builder.addArgument("--workflowRunAttemptId", workflowRunAttemptId.toString());
        }

        if (persistFileData) {
            builder.addArgument("--persistFileData");
        }

        return builder;
    }

    public static CondorJobBuilder createDryRunJob(int count, Class<?> moduleClass) {
        logger.debug("ENTERING createDryRunJob(int, Class<?>)");
        logger.debug("moduleClass.getSimpleName(): {}", moduleClass.getSimpleName());

        File executable = new File("$MAPSEQ_CLIENT_HOME/bin/mapseq-run-module.sh");
        String jobName = String.format("%s_%d", moduleClass.getSimpleName(), count);
        CondorJobBuilder builder = new CondorJobBuilder().name(jobName).executable(executable);

        if (count > 0) {
            builder.priority(count * 10);
        }

        builder.addArgument(moduleClass.getName()).addArgument("--dryRun");

        for (ClassAdvertisement classAd : ClassAdvertisementFactory.getDefaultClassAds()) {
            try {
                builder.classAdvertisments().add(classAd.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        return builder;
    }

}
