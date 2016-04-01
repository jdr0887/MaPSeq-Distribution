package edu.unc.mapseq.workflow.exporter;

import java.io.File;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.renci.jlrm.condor.ClassAdvertisement;
import org.renci.jlrm.condor.ClassAdvertisementFactory;
import org.renci.jlrm.condor.CondorJob;
import org.renci.jlrm.condor.CondorJobBuilder;

public class CondorSubmitScriptExporterTest {

    @Test
    public void testRsyncCreateScript() {

        CondorJobBuilder builder = new CondorJobBuilder().name("PicardAddOrReplaceReadGroupsCLI")
                .initialDirectory("/home/jdr0887/tmp").executable(new File("/bin/echo")).addArgument("foo");
        // builder.siteName("Kure");
        builder.addTransferInput("asdf").addTransferInput("asdfasdf").addTransferOutput("zxcv")
                .addTransferOutput("zxcvzxcv");

        CondorJob job = builder.build();
        String username = System.getProperty("user.name");

        StringBuilder scriptSB = new StringBuilder();
        scriptSB.append("#!/bin/bash\n");
        scriptSB.append("if [ -e ~/.bashrc ]; then . ~/.bashrc; fi\n");
        scriptSB.append("if [ -e ~/.mapseqrc ]; then . ~/.mapseqrc; fi\n");
        scriptSB.append("if [ -e ~/.jlrmrc ]; then . ~/.jlrmrc; fi\n");
        scriptSB.append("if [ \"x$MAPSEQ_CLIENT_HOME\" = \"x\" ]; then echo \"ERROR: MAPSEQ_CLIENT_HOME has to be set\"; exit 1; fi\n");
        scriptSB.append("/bin/hostname -f\n");
        scriptSB.append("/usr/bin/id\n");

        String commandFormat = "%nRC=0; %s; RC=$?%nif [ $RC != 0 ]; then exit $RC; fi%n%s%n%n";

        StringBuilder transferInputCommandSB = new StringBuilder();

        if (job.getInitialDirectory() != null && StringUtils.isNotEmpty(job.getSiteName())) {
            scriptSB.append("DATA_MOVER=172.26.128.18\n");
            scriptSB.append("if [ $JLRM_SITE_NAME != \"Kure\" ]; then DATA_MOVER=152.19.197.219; fi\n");
        }

        if (job.getInitialDirectory() != null && job.getTransferInputList().size() > 0) {
            transferInputCommandSB.append("$MAPSEQ_CLIENT_HOME/bin/mapseq-transfer-input-files.sh --host=$DATA_MOVER");
            transferInputCommandSB.append(String.format(" --username=%s", username));
            String initialDir = job.getInitialDirectory();
            transferInputCommandSB.append(String.format(" --remoteDirectory=%s", initialDir));
            for (String file : job.getTransferInputList()) {
                transferInputCommandSB.append(String.format(" --fileName=%s", file));
            }
        }

        if (transferInputCommandSB.length() > 0) {
            scriptSB.append(String.format(commandFormat, transferInputCommandSB.toString(),
                    "echo \"Successfully transferred input files\""));
        }

        String command = String.format("%s", job.getExecutable().getPath());

        ClassAdvertisement argumentsClassAd = job.getArgumentsClassAd();
        if (argumentsClassAd != null && StringUtils.isNotEmpty(argumentsClassAd.getValue())) {
            command = String.format("%s %s", job.getExecutable().getPath(), argumentsClassAd.getValue());
        }

        scriptSB.append(String.format("CMD=\"%s\"%necho $CMD", command));
        scriptSB.append(String.format(commandFormat, "$CMD", "echo \"Successfully executed command\""));

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
            transferOutputCommandSB.append("$MAPSEQ_CLIENT_HOME/bin/mapseq-transfer-output-files.sh --host=$DATA_MOVER");
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
                scriptSB.append(String.format(commandFormat, transferOutputCommandSB.toString(),
                        "echo \"Successfully transferred output files\""));

                for (String file : job.getTransferOutputList()) {
                    removeOutputFilesSB.append(String.format("if [ -e %1$s ]; then /bin/rm %1$s; fi%n", file));
                }
            }

            scriptSB.append(removeOutputFilesSB.toString());

        }

        scriptSB.append("exit $RC");

        System.out.println(scriptSB.toString());
    }

    @Test
    public void testCreateScript() {

        CondorJob job = new CondorJob();
        job.setInitialDirectory("/home/jdr0887/tmp");
        job.setExecutable(new File("/bin/echo"));
        // job.addTransferInput(new File("asdf"));
        // job.addTransferInput(new File("asdfasdf"));
        // job.addTransferOutput(new File("zxcv"));
        // job.addTransferOutput(new File("zxcvzxcv"));

        StringBuilder sb = new StringBuilder();
        sb.append("#!/bin/bash\n");
        sb.append("if [ -e ~/.bashrc ]; then . ~/.bashrc; fi\n");
        sb.append("if [ -e ~/.mapseqrc ]; then . ~/.mapseqrc; fi\n");
        sb.append("if [ -e ~/.jlrmrc ]; then . ~/.jlrmrc; fi\n");
        sb.append("if [ \"x$MAPSEQ_CLIENT_HOME\" = \"x\" ]; then echo \"ERROR: MAPSEQ_CLIENT_HOME has to be set\"; exit 1; fi\n");
        sb.append("/bin/hostname -f; /usr/bin/id\n");

        String commandFormat = "%nRC=0%n%s%nRC=$?; if [ $RC != 0 ]; then exit $RC; fi%n";

        Set<ClassAdvertisement> classAdSet = job.getClassAdvertisments();

        String command = String.format("%s", job.getExecutable().getPath());

        for (ClassAdvertisement classAd : classAdSet) {
            if (classAd.getKey().equals(ClassAdvertisementFactory.CLASS_AD_KEY_ARGUMENTS)) {
                command += String.format(" %s", classAd.getValue());
            }
        }
        sb.append("\nCMD=\"").append(command).append("\"\n");
        sb.append("echo $CMD\n");
        sb.append(String.format(commandFormat, "$CMD"));

        System.out.println(sb.toString());
    }

}
