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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecureCondorSubmitScriptExporter extends DefaultCondorSubmitScriptExporter {

    private final Logger logger = LoggerFactory.getLogger(SecureCondorSubmitScriptExporter.class);

    public SecureCondorSubmitScriptExporter() {
        super();
    }

    public CondorJob export(String dagName, File workDir, Graph<CondorJob, CondorJobEdge> graph,
            boolean includeGlideinRequirements) {
        logger.debug("ENTERING export(String dagName, File workDir, Graph<JobNode, JobEdge> graph)");

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
                    if (StringUtils.isNotEmpty(job.getSiteName()) && "Kure".equals(job.getSiteName())) {
                        classAd.setValue(Boolean.TRUE.toString());
                    } else {
                        classAd.setValue(Boolean.FALSE.toString());
                    }
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

                    StringBuilder requirements = new StringBuilder();
                    if (includeGlideinRequirements) {
                        requirements.append(String.format(
                                "(TARGET.JLRM_USER == \"%s\") && (TARGET.IS_GLIDEIN == True)",
                                System.getProperty("user.name")));
                        if (StringUtils.isNotEmpty(job.getSiteName())) {
                            requirements.append(String.format(" && (TARGET.JLRM_SITE_NAME == \"%s\")",
                                    job.getSiteName()));
                        }
                    }

                    classAd = ClassAdvertisementFactory.getClassAd(CLASS_AD_KEY_REQUIREMENTS).clone();
                    classAd.setValue(requirements.toString());
                    job.getClassAdvertisments().add(classAd);

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

    protected String createWrapperScript(CondorJob job, File workDir) {
        logger.debug("ENTERING createWrapperScript");

        String username = System.getProperty("user.name");

        StringBuilder scriptSB = new StringBuilder();
        scriptSB.append("#!/bin/bash\n");
        scriptSB.append("if [ -e ~/.bashrc ]; then . ~/.bashrc; fi\n");
        scriptSB.append("if [ -e ~/.mapseqrc ]; then . ~/.mapseqrc; fi\n");
        scriptSB.append("if [ -e ~/.jlrmrc ]; then . ~/.jlrmrc; fi\n");
        scriptSB.append("if [ \"x$MAPSEQ_HOME\" = \"x\" ]; then echo \"ERROR: MAPSEQ_HOME has to be set\"; exit 1; fi\n");
        scriptSB.append("/bin/hostname -f\n");
        scriptSB.append("/usr/bin/id\n");

        String commandFormat = "%nRC=0; %s; RC=$?%n%sif [ $RC != 0 ]; then exit $RC; fi%n%n";

        StringBuilder transferInputCommandSB = new StringBuilder();

        if (job.getInitialDirectory() != null && StringUtils.isEmpty(job.getSiteName())) {
            scriptSB.append("DATA_MOVER=172.26.128.18\n");
            scriptSB.append("if [ $JLRM_SITE_NAME != \"Kure\" ]; then DATA_MOVER=152.19.197.219; fi\n");
        }

        if (job.getInitialDirectory() != null && job.getTransferInputList().size() > 0) {
            transferInputCommandSB.append("$MAPSEQ_HOME/bin/mapseq-transfer-input-files.sh --host=$DATA_MOVER");
            transferInputCommandSB.append(String.format(" --username=%s", username));
            String initialDir = job.getInitialDirectory();
            transferInputCommandSB.append(String.format(" --remoteDirectory=%s", initialDir));
            for (String file : job.getTransferInputList()) {
                transferInputCommandSB.append(String.format(" --fileName=%s", file));
            }
        }

        if (transferInputCommandSB.length() > 0) {
            scriptSB.append(String.format(commandFormat, transferInputCommandSB.toString(), ""));
        }

        String command = String.format("%s", job.getExecutable().getPath());

        ClassAdvertisement argumentsClassAd = job.getArgumentsClassAd();
        if (argumentsClassAd != null && StringUtils.isNotEmpty(argumentsClassAd.getValue())) {
            command = String.format("%s %s", job.getExecutable().getPath(), argumentsClassAd.getValue());
        }

        scriptSB.append(String.format("CMD=\"%s\"%necho $CMD", command));
        scriptSB.append(String.format(commandFormat, "$CMD", ""));

        StringBuilder removeInputFilesSB = new StringBuilder();
        if (job.getInitialDirectory() != null && job.getTransferInputList().size() > 0) {
            for (String file : job.getTransferInputList()) {
                removeInputFilesSB.append(String.format("if [ -e %1$s ]; then /bin/rm %1$s; fi%n", file));
            }
        }

        if (removeInputFilesSB.length() > 0) {
            scriptSB.append(removeInputFilesSB.toString());
        }

        if (job.getInitialDirectory() != null) {

            StringBuilder transferOutputCommandSB = new StringBuilder();
            transferOutputCommandSB.append("$MAPSEQ_HOME/bin/mapseq-transfer-output-files.sh --host=$DATA_MOVER");
            transferOutputCommandSB.append(String.format(" --username=%s", username));
            String initialDir = job.getInitialDirectory();
            transferOutputCommandSB.append(String.format(" --remoteDirectory=%s", initialDir));

            StringBuilder removeOutputFilesSB = new StringBuilder();

            if (StringUtils.isEmpty(job.getSiteName()) && StringUtils.isNotEmpty(job.getName())) {
                transferOutputCommandSB.append(String.format(" --file=%s.xml", job.getName()));
                removeOutputFilesSB.append(String.format("if [ -e %1$s.xml ]; then /bin/rm %1$s.xml; fi%n",
                        job.getName()));
            }

            if (job.getTransferOutputList().size() > 0) {
                for (String file : job.getTransferOutputList()) {
                    transferOutputCommandSB.append(String.format(" --file=%s", file));
                }
                scriptSB.append(String.format(commandFormat, transferOutputCommandSB.toString(), ""));

                for (String file : job.getTransferOutputList()) {
                    removeOutputFilesSB.append(String.format("if [ -e %1$s ]; then /bin/rm %1$s; fi%n", file));
                }
            }

            scriptSB.append(removeOutputFilesSB.toString());

        }

        scriptSB.append("exit $RC");

        return scriptSB.toString();
    }

    protected File writeSubmitFile(File submitDir, CondorJob job) throws IOException {
        logger.debug("ENTERING writeSubmitFile");
        File submitFile = new File(submitDir, String.format("%s.sub", job.getName()));
        FileWriter submitFileWriter = new FileWriter(submitFile);
        for (ClassAdvertisement classAd : job.getClassAdvertisments()) {

            if (classAd.getKey().equals(ClassAdvertisementFactory.CLASS_AD_KEY_ARGUMENTS)
                    || classAd.getKey().equals(ClassAdvertisementFactory.CLASS_AD_KEY_TRANSFER_INPUT_FILES)
                    || classAd.getKey().equals(ClassAdvertisementFactory.CLASS_AD_KEY_TRANSFER_OUTPUT_FILES)) {
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

}