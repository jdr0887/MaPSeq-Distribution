package edu.unc.mapseq.workflow.impl;

import java.io.File;

import org.renci.jlrm.condor.ClassAdvertisement;
import org.renci.jlrm.condor.ClassAdvertisementFactory;
import org.renci.jlrm.condor.CondorJobBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.model.HTSFSample;
import edu.unc.mapseq.dao.model.SequencerRun;
import edu.unc.mapseq.dao.model.WorkflowPlan;
import edu.unc.mapseq.dao.model.WorkflowRun;

public class WorkflowJobFactory {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowJobFactory.class);

    public static CondorJobBuilder createJob(int count, Class<?> moduleClass, WorkflowPlan workflowPlan) {
        return createJob(count, moduleClass, workflowPlan, null);
    }

    public static CondorJobBuilder createJob(int count, Class<?> moduleClass, WorkflowPlan workflowPlan,
            HTSFSample htsfSample) {
        return createJob(count, moduleClass, workflowPlan, htsfSample, true);
    }

    public static CondorJobBuilder createJob(int count, Class<?> moduleClass, WorkflowPlan workflowPlan,
            HTSFSample htsfSample, boolean persistFileData) {
        return createJob(count, moduleClass, workflowPlan, htsfSample, persistFileData, 3);
    }

    public static CondorJobBuilder createJob(int count, Class<?> moduleClass, WorkflowPlan workflowPlan,
            HTSFSample htsfSample, boolean persistFileData, Integer retry) {
        logger.debug("ENTERING createJob(int, Class<?>, WorkflowPlan, HTSFSample, boolean, Integer)");
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

        if (workflowPlan != null) {

            WorkflowRun workflowRun = workflowPlan.getWorkflowRun();

            if (workflowRun != null) {
                builder.addArgument("--workflowRunId", workflowRun.getId().toString());
                if (workflowRun.getCreator() != null) {
                    builder.addArgument("--accountId", workflowRun.getCreator().getId().toString());
                }
            }

            SequencerRun sequencerRun = workflowPlan.getSequencerRun();

            if (sequencerRun != null) {
                logger.debug("sequencerRun.getId().toString(): {}", sequencerRun.getId().toString());
                builder.addArgument("--sequencerRunId", sequencerRun.getId().toString());
            } else if (sequencerRun == null && htsfSample != null) {
                logger.debug("htsfSample.getSequencerRun().getId().toString(): {}", htsfSample.getSequencerRun()
                        .getId().toString());
                builder.addArgument("--sequencerRunId", htsfSample.getSequencerRun().getId().toString());
            }

        }

        if (persistFileData) {
            builder.addArgument("--persistFileData");
        }

        if (htsfSample != null) {
            logger.debug("htsfSample.getId().toString(): {}", htsfSample.getId().toString());
            builder.addArgument("--htsfSampleId", htsfSample.getId().toString());
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
