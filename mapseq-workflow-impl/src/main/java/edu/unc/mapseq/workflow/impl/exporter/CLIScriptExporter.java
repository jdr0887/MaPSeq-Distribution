package edu.unc.mapseq.workflow.impl.exporter;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jgrapht.Graph;
import org.renci.jlrm.condor.ClassAdvertisement;
import org.renci.jlrm.condor.CondorJob;
import org.renci.jlrm.condor.CondorJobEdge;
import org.renci.jlrm.condor.ext.CondorSubmitScriptExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CLIScriptExporter extends CondorSubmitScriptExporter {

    private final Logger logger = LoggerFactory.getLogger(CLIScriptExporter.class);

    public CLIScriptExporter() {
        super();
    }

    public CondorJob export(String dagName, File workDir, Graph<CondorJob, CondorJobEdge> graph) {
        logger.debug("ENTERING export");

        CondorJob dagSubmitJob = new CondorJob();
        dagSubmitJob.setName(dagName);

        try {

            if (graph != null && graph.vertexSet().size() > 0) {

                for (CondorJob job : graph.vertexSet()) {

                    File wrapperScript = new File(workDir, String.format("%s.sh", job.getName()));
                    wrapperScript.setExecutable(true);
                    FileUtils.writeStringToFile(wrapperScript, createWrapperScript(job, workDir));

                    // Process process = new ProcessBuilder(wrapperScript.getAbsolutePath()).start();
                    // try {
                    // process.waitFor();
                    // } catch (InterruptedException e) {
                    // e.printStackTrace();
                    // }
                    // int exitCode = process.exitValue();
                    // System.out.println(exitCode);
                }

            }

        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return dagSubmitJob;
    }

    protected String createWrapperScript(CondorJob job, File workDir) {
        logger.debug("ENTERING createWrapperScript");
        StringBuilder sb = new StringBuilder();
        sb.append("#!/bin/bash\n");
        sb.append("if [ -e ~/.bashrc ]; then . ~/.bashrc; fi\n");
        sb.append("if [ -e ~/.mapseqrc ]; then . ~/.mapseqrc; fi\n");
        sb.append("if [ -e ~/.jlrmrc ]; then . ~/.jlrmrc; fi\n");
        sb.append("if [ \"x$MAPSEQ_HOME\" = \"x\" ]; then echo \"ERROR: MAPSEQ_HOME has to be set\"; exit 1; fi\n");

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