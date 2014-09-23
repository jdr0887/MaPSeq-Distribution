package edu.unc.mapseq.workflow.impl;

import java.io.File;

import org.renci.jlrm.condor.ClassAdvertisement;
import org.renci.jlrm.condor.ClassAdvertisementFactory;
import org.renci.jlrm.condor.CondorJobBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.model.WorkflowRunAttempt;

public class WorkflowJobFactory {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowJobFactory.class);

    public static CondorJobBuilder createJob(int count, Class<?> moduleClass, WorkflowRunAttempt workflowRunAttempt) {
        return createJob(count, moduleClass, workflowRunAttempt);
    }

    public static CondorJobBuilder createJob(int count, Class<?> moduleClass, WorkflowRunAttempt workflowRunAttempt,
            boolean persistFileData) {
        return createJob(count, moduleClass, workflowRunAttempt, persistFileData, 3);
    }

    public static CondorJobBuilder createJob(int count, Class<?> moduleClass, WorkflowRunAttempt workflowRunAttempt,
            boolean persistFileData, Integer retry) {
        logger.debug("ENTERING createJob(int, Class<?>, WorkflowPlan, Sample, boolean, Integer)");
        logger.debug("moduleClass.getSimpleName(): {}", moduleClass.getSimpleName());

        File executable = new File("$MAPSEQ_HOME/bin/mapseq-run-module.sh");
        String jobName = String.format("%s_%d", moduleClass.getSimpleName(), count);
        CondorJobBuilder builder = new CondorJobBuilder().name(jobName).executable(executable).retry(retry);

        if (count > 0) {
            builder.priority(count * 10);
        }

        builder.addArgument(moduleClass.getName()).addArgument("--serializeFile", String.format("%s.xml", jobName));

        for (ClassAdvertisement classAd : ClassAdvertisementFactory.getDefaultClassAds()) {
            try {
                builder.classAdvertisments().add(classAd.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        if (workflowRunAttempt != null) {
            builder.addArgument("--workflowRunAttemptId", workflowRunAttempt.getId().toString());
        }

        if (persistFileData) {
            builder.addArgument("--persistFileData");
        }

        return builder;
    }

    public static CondorJobBuilder createDryRunJob(int count, Class<?> moduleClass) {
        logger.debug("ENTERING createDryRunJob(int, Class<?>)");
        logger.debug("moduleClass.getSimpleName(): {}", moduleClass.getSimpleName());

        File executable = new File("$MAPSEQ_HOME/bin/mapseq-run-module.sh");
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
