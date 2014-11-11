package edu.unc.mapseq.workflow.impl.exporter;

import static org.renci.jlrm.condor.ClassAdvertisementFactory.CLASS_AD_KEY_ERROR;
import static org.renci.jlrm.condor.ClassAdvertisementFactory.CLASS_AD_KEY_EXECUTABLE;
import static org.renci.jlrm.condor.ClassAdvertisementFactory.CLASS_AD_KEY_GET_ENV;
import static org.renci.jlrm.condor.ClassAdvertisementFactory.CLASS_AD_KEY_INITIAL_DIR;
import static org.renci.jlrm.condor.ClassAdvertisementFactory.CLASS_AD_KEY_LOG;
import static org.renci.jlrm.condor.ClassAdvertisementFactory.CLASS_AD_KEY_OUTPUT;
import static org.renci.jlrm.condor.ClassAdvertisementFactory.CLASS_AD_KEY_PRIORITY;
import static org.renci.jlrm.condor.ClassAdvertisementFactory.CLASS_AD_KEY_REQUEST_CPUS;
import static org.renci.jlrm.condor.ClassAdvertisementFactory.CLASS_AD_KEY_REQUEST_MEMORY;
import static org.renci.jlrm.condor.ClassAdvertisementFactory.CLASS_AD_KEY_REQUIREMENTS;
import static org.renci.jlrm.condor.ClassAdvertisementFactory.CLASS_AD_KEY_TRANSFER_EXECUTABLE;
import static org.renci.jlrm.condor.ClassAdvertisementFactory.CLASS_AD_KEY_TRANSFER_INPUT_FILES;
import static org.renci.jlrm.condor.ClassAdvertisementFactory.CLASS_AD_KEY_TRANSFER_OUTPUT_FILES;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jgrapht.Graph;
import org.renci.jlrm.condor.ClassAdvertisement;
import org.renci.jlrm.condor.ClassAdvertisementFactory;
import org.renci.jlrm.condor.CondorJob;
import org.renci.jlrm.condor.CondorJobEdge;
import org.renci.jlrm.condor.ext.CondorSubmitScriptExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultCondorSubmitScriptExporter extends CondorSubmitScriptExporter {

    private final Logger logger = LoggerFactory.getLogger(DefaultCondorSubmitScriptExporter.class);

    public DefaultCondorSubmitScriptExporter() {
        super();
    }

    public CondorJob export(String dagName, File workDir, Graph<CondorJob, CondorJobEdge> graph,
            boolean includeGlideinRequirements) {
        logger.debug("ENTERING export");

        CondorJob dagSubmitJob = new CondorJob();
        dagSubmitJob.setName(dagName);
        File dagFile = new File(workDir, dagName + ".dag");
        dagSubmitJob.setSubmitFile(dagFile);

        try {

            if (graph != null && graph.vertexSet().size() > 0) {

                ClassAdvertisement classAd = null;
                for (CondorJob job : graph.vertexSet()) {

                    File wrapperScript = new File(workDir, String.format("%s.sh", job.getName()));
                    wrapperScript.setExecutable(true);
                    FileUtils.writeStringToFile(wrapperScript, createWrapperScript(job, workDir));

                    classAd = ClassAdvertisementFactory.getClassAd(CLASS_AD_KEY_EXECUTABLE).clone();
                    classAd.setValue(wrapperScript.getName());
                    job.getClassAdvertisments().add(classAd);

                    classAd = ClassAdvertisementFactory.getClassAd(CLASS_AD_KEY_OUTPUT).clone();
                    classAd.setValue(new File(workDir, String.format("%s.out", job.getName())).getAbsolutePath());
                    job.getClassAdvertisments().add(classAd);

                    classAd = ClassAdvertisementFactory.getClassAd(CLASS_AD_KEY_ERROR).clone();
                    classAd.setValue(new File(workDir, String.format("%s.err", job.getName())).getAbsolutePath());
                    job.getClassAdvertisments().add(classAd);

                    classAd = ClassAdvertisementFactory.getClassAd(CLASS_AD_KEY_LOG).clone();
                    classAd.setValue(new File(workDir, String.format("%s.log", job.getName())).getAbsolutePath());
                    job.getClassAdvertisments().add(classAd);

                    classAd = ClassAdvertisementFactory.getClassAd(CLASS_AD_KEY_GET_ENV).clone();
                    // if (StringUtils.isNotEmpty(job.getSiteName()) && "Kure".equals(job.getSiteName())) {
                    // classAd.setValue(Boolean.TRUE.toString());
                    // } else {
                    classAd.setValue(Boolean.FALSE.toString());
                    // }
                    job.getClassAdvertisments().add(classAd);

                    classAd = ClassAdvertisementFactory.getClassAd(CLASS_AD_KEY_REQUEST_CPUS).clone();
                    classAd.setValue(job.getNumberOfProcessors().toString());
                    job.getClassAdvertisments().add(classAd);

                    classAd = ClassAdvertisementFactory.getClassAd(CLASS_AD_KEY_REQUEST_MEMORY).clone();
                    classAd.setValue(job.getMemory().toString());
                    job.getClassAdvertisments().add(classAd);

                    if (job.getInitialDirectory() != null) {
                        classAd = ClassAdvertisementFactory.getClassAd(CLASS_AD_KEY_INITIAL_DIR).clone();
                        classAd.setValue(job.getInitialDirectory());
                        job.getClassAdvertisments().add(classAd);
                    }

                    if (job.getPriority() != null) {
                        classAd = ClassAdvertisementFactory.getClassAd(CLASS_AD_KEY_PRIORITY).clone();
                        classAd.setValue(job.getPriority().toString());
                        job.getClassAdvertisments().add(classAd);
                    }

                    classAd = ClassAdvertisementFactory.getClassAd(CLASS_AD_KEY_TRANSFER_EXECUTABLE).clone();
                    classAd.setValue(Boolean.TRUE.toString());
                    job.getClassAdvertisments().add(classAd);

                    StringBuilder requirements = new StringBuilder("(Arch == \"X86_64\") && (OpSys == \"LINUX\")");
                    if (includeGlideinRequirements) {
                        requirements.append(String.format(
                                " && (TARGET.JLRM_USER == \"%s\") && (TARGET.IS_GLIDEIN == True)",
                                System.getProperty("user.name")));
                        if (StringUtils.isNotEmpty(job.getSiteName())) {
                            requirements.append(String.format(" && (TARGET.JLRM_SITE_NAME == \"%s\")",
                                    job.getSiteName()));
                        }
                    }

                    classAd = ClassAdvertisementFactory.getClassAd(CLASS_AD_KEY_REQUIREMENTS).clone();
                    classAd.setValue(requirements.toString());
                    job.getClassAdvertisments().add(classAd);

                    if (job.getTransferInputList().size() > 0) {
                        classAd = ClassAdvertisementFactory.getClassAd(CLASS_AD_KEY_TRANSFER_INPUT_FILES).clone();
                        classAd.setValue(StringUtils.join(job.getTransferInputList(), ","));
                        job.getClassAdvertisments().add(classAd);
                    }

                    if (StringUtils.isEmpty(job.getSiteName())) {
                        job.addTransferOutput(String.format("%s.xml", job.getName()));
                    }

                    if (job.getTransferOutputList().size() > 0) {
                        classAd = ClassAdvertisementFactory.getClassAd(CLASS_AD_KEY_TRANSFER_OUTPUT_FILES).clone();
                        classAd.setValue(StringUtils.join(job.getTransferOutputList(), ","));
                        job.getClassAdvertisments().add(classAd);
                    }

                }

                FileWriter dagFileWriter = new FileWriter(dagFile);

                for (CondorJob job : graph.vertexSet()) {
                    writeSubmitFile(workDir, job);
                    dagFileWriter.write(String.format("%n%1$-10s %2$-10s %2$s.sub", "JOB", job.getName()));
                    if (StringUtils.isNotEmpty(job.getPreScript())) {
                        dagFileWriter.write(String.format("%n%1$-10s %2$-10s %3$-10s %4$-10s", "SCRIPT", "PRE",
                                job.getName(), job.getPreScript()));
                    }
                    if (StringUtils.isNotEmpty(job.getPostScript())) {
                        dagFileWriter.write(String.format("%n%1$-10s %2$-10s %3$-10s %4$-10s", "SCRIPT", "POST",
                                job.getName(), job.getPostScript()));
                    }
                    if (job.getRetry() != null && job.getRetry() > 1) {
                        dagFileWriter.write(String.format("%n%1$-10s %2$-10s %3$d%n", "RETRY", job.getName(),
                                job.getRetry()));
                    }
                    dagFileWriter.flush();
                }

                dagFileWriter.write(System.getProperty("line.separator"));

                for (CondorJobEdge edge : graph.edgeSet()) {
                    CondorJob source = (CondorJob) edge.getSource();
                    CondorJob target = (CondorJob) edge.getTarget();
                    String format = "%1$-10s %2$-10s %3$-10s %4$s%n";
                    dagFileWriter.write(String.format(format, "PARENT", source.getName(), "CHILD", target.getName()));
                    dagFileWriter.flush();
                }

                dagFileWriter.close();
            }

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return dagSubmitJob;
    }

    protected File writeSubmitFile(File submitDir, CondorJob job) throws IOException {
        logger.debug("ENTERING writeSubmitFile");
        File submitFile = new File(submitDir, String.format("%s.sub", job.getName()));
        FileWriter submitFileWriter = new FileWriter(submitFile);
        for (ClassAdvertisement classAd : job.getClassAdvertisments()) {

            if (classAd.getKey().equals(ClassAdvertisementFactory.CLASS_AD_KEY_ARGUMENTS)) {
                continue;
            }

            switch (classAd.getType()) {
                case BOOLEAN:
                case EXPRESSION:
                case INTEGER:
                    submitFileWriter.write(String.format("%1$-25s = %2$s%n", classAd.getKey(), classAd.getValue()));
                    break;
                case STRING:
                default:
                    submitFileWriter.write(String.format("%1$-25s = \"%2$s\"%n", classAd.getKey(), classAd.getValue()));
                    break;
            }
            submitFileWriter.flush();
        }
        submitFileWriter.write(String.format("%s%n",
                ClassAdvertisementFactory.getClassAd(ClassAdvertisementFactory.CLASS_AD_KEY_QUEUE).getKey()));
        submitFileWriter.flush();
        submitFileWriter.close();
        return submitFile;
    }

    protected String createWrapperScript(CondorJob job, File workDir) {
        logger.debug("ENTERING createWrapperScript");
        StringBuilder sb = new StringBuilder();
        sb.append("#!/bin/bash\n");
        sb.append("if [ -e ~/.bashrc ]; then . ~/.bashrc; fi\n");
        sb.append("if [ -e ~/.mapseqrc ]; then . ~/.mapseqrc; fi\n");
        sb.append("if [ -e ~/.jlrmrc ]; then . ~/.jlrmrc; fi\n");
        sb.append("if [ \"x$MAPSEQ_HOME\" = \"x\" ]; then echo \"ERROR: MAPSEQ_HOME has to be set\"; exit 1; fi\n");
        sb.append("/bin/hostname -f; /usr/bin/id; /bin/env\n");

        String commandFormat = "%nRC=0%n%s%nRC=$?; if [ $RC != 0 ]; then exit $RC; fi%n";

        String command = null;

        ClassAdvertisement argumentsClassAd = job.getArgumentsClassAd();
        if (argumentsClassAd != null && StringUtils.isNotEmpty(argumentsClassAd.getValue())) {
            command = String.format("%s %s", job.getExecutable().getPath(), argumentsClassAd.getValue());
        } else {
            command = String.format("%s", job.getExecutable().getPath());
        }
        logger.debug("command: {}", command);
        sb.append("\nCMD=\"").append(command).append("\"\n");
        sb.append("echo $CMD\n");
        sb.append(String.format(commandFormat, "$CMD"));
        return sb.toString();
    }

}