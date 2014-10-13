package edu.unc.mapseq.workflow.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jgrapht.Graph;
import org.renci.jlrm.condor.CondorJob;
import org.renci.jlrm.condor.CondorJobEdge;
import org.renci.jlrm.condor.cli.CondorSubmitDAGCallable;
import org.renci.jlrm.condor.ext.CondorDOTExporter;
import org.renci.jlrm.condor.ext.CondorJobVertexNameProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.config.MaPSeqConfigurationService;
import edu.unc.mapseq.config.RunModeType;
import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowRunAttemptDAO;
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;
import edu.unc.mapseq.workflow.Workflow;
import edu.unc.mapseq.workflow.WorkflowBeanService;
import edu.unc.mapseq.workflow.WorkflowException;
import edu.unc.mapseq.workflow.impl.exporter.SecureCondorSubmitScriptExporter;

public abstract class AbstractWorkflow implements Workflow {

    private final Logger logger = LoggerFactory.getLogger(AbstractWorkflow.class);

    private WorkflowRunAttempt workflowRunAttempt;

    private Integer backOffMultiplier = 5;

    private File baseOutputDirectory;

    private File submitDirectory;

    private WorkflowBeanService workflowBeanService;

    private Graph<CondorJob, CondorJobEdge> graph;

    public AbstractWorkflow() {
        super();
    }

    @Override
    public void validate() throws WorkflowException {
        logger.info("ENTERING validate()");

        try {
            this.graph = createGraph();
        } catch (WorkflowException e) {
            logger.error("Problem running before start command", e);
            throw new WorkflowException(e);
        }

        if (graph == null || (graph != null && graph.vertexSet().size() == 0)) {
            logger.error("graph is null");
            throw new WorkflowException("graph is null");
        }

        Set<CondorJob> condorJobSet = graph.vertexSet();
        for (CondorJob condorJob : condorJobSet) {
            if (StringUtils.isEmpty(condorJob.getSiteName())
                    && (condorJob.getTransferInputList().size() == 0 && condorJob.getTransferOutputList().size() == 0)) {
                throw new WorkflowException("can't have a job where both siteName & list of inputs/outputs are empty");
            }
        }

    }

    @Override
    public void preRun() throws WorkflowException {
    }

    @Override
    public void postRun() throws WorkflowException {

    }

    @Override
    public void init() throws WorkflowException {
        logger.debug("ENTERING init()");
        if (this.workflowRunAttempt == null) {
            logger.error("workflowRunAttempt is null");
            throw new WorkflowException("workflowRunAttempt is null");
        }

        String mapseqHome = System.getenv("MAPSEQ_HOME");
        if (StringUtils.isEmpty(mapseqHome)) {
            logger.error("MAPSEQ_HOME not set in env: {}", mapseqHome);
            throw new WorkflowException("MAPSEQ_HOME not set in env");
        }
        File homeDirectory = new File(mapseqHome);
        if (!homeDirectory.exists()) {
            logger.error("MAPSEQ_HOME does not exist: {}", mapseqHome);
            throw new WorkflowException("MAPSEQ_HOME does not exist");
        }

        logger.debug("homeDirectory = {}", homeDirectory.getAbsolutePath());

        String outputDir = System.getenv("MAPSEQ_OUTPUT_DIRECTORY");
        if (StringUtils.isEmpty(outputDir)) {
            logger.error("MAPSEQ_OUTPUT_DIRECTORY not set in env: {}", outputDir);
            throw new WorkflowException("MAPSEQ_OUTPUT_DIRECTORY not set in env");
        }

        this.baseOutputDirectory = new File(outputDir);
        if (!baseOutputDirectory.exists()) {
            logger.error("MAPSEQ_OUTPUT_DIRECTORY does not exist: {}", outputDir);
            throw new WorkflowException("MAPSEQ_OUTPUT_DIRECTORY does not exist");
        }

        File submitDir = new File(homeDirectory, "submit");
        File datedDir = new File(submitDir, DateFormatUtils.ISO_DATE_FORMAT.format(new Date()));
        File namedDir = new File(datedDir, getName());
        this.submitDirectory = new File(namedDir, UUID.randomUUID().toString());
        this.submitDirectory.mkdirs();

        logger.debug("submitDirectory = {}", this.submitDirectory.getAbsolutePath());
    }

    @Override
    public CondorJob call() throws WorkflowException {
        logger.debug("ENTERING call()");

        CondorJob jobNode = null;

        int backOffCount = 0;
        boolean hasSubmittedSuccessfully = false;

        boolean includeGlideinRequirements = true;
        try {
            MaPSeqConfigurationService configService = getWorkflowBeanService().getMaPSeqConfigurationService();
            if (configService != null && configService.getRunMode().equals(RunModeType.DEV)) {
                includeGlideinRequirements = false;
            }
        } catch (Exception e) {
            logger.warn("Error", e);
        }

        // DefaultCondorSubmitScriptExporter exporter = new DefaultCondorSubmitScriptExporter();
        SecureCondorSubmitScriptExporter exporter = new SecureCondorSubmitScriptExporter();
        jobNode = exporter.export(getName(), this.submitDirectory, getGraph(), includeGlideinRequirements);
        if (!jobNode.getSubmitFile().exists()) {
            logger.info("jobNode.getSubmitFile().getAbsolutePath() = {}", jobNode.getSubmitFile().getAbsolutePath());
            throw new WorkflowException("jobNode.getSubmitFile() doesn't exist");
        }

        while (!hasSubmittedSuccessfully) {

            if (backOffCount > getBackOffMultiplier()) {
                break;
            }

            if (backOffCount > 0) {
                try {
                    Thread.sleep(backOffCount * 60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                CondorSubmitDAGCallable submitDAGCallable = new CondorSubmitDAGCallable(jobNode.getSubmitFile());
                Integer clusterId = submitDAGCallable.call();
                jobNode.setCluster(clusterId);
                jobNode.setJobId(0);
                hasSubmittedSuccessfully = true;
                logger.info("jobNode.getSubmitFile().getAbsolutePath() = {}", jobNode.getSubmitFile().getAbsolutePath());
            } catch (Exception e) {
                ++backOffCount;
                hasSubmittedSuccessfully = false;
                logger.warn("Error", e);
                logger.warn("hasSubmittedSuccessfully: {}", hasSubmittedSuccessfully);
            }
            logger.info("backOffCount: {}", backOffCount);

        }

        if (!hasSubmittedSuccessfully) {
            throw new WorkflowException(String.format("Backed off %d times & still could not submit to condor",
                    getBackOffMultiplier()));
        }

        try {
            Properties props = new Properties();
            props.setProperty("rankdir", "LR");
            CondorDOTExporter<CondorJob, CondorJobEdge> dotExporter = new CondorDOTExporter<CondorJob, CondorJobEdge>(
                    new CondorJobVertexNameProvider(), new CondorJobVertexNameProvider(), null, null, null, props);
            File dotFile = new File(this.submitDirectory, getName() + ".dag.dot");
            FileWriter fw = new FileWriter(dotFile);
            dotExporter.export(fw, graph);
        } catch (IOException e) {
            logger.warn("Problem writing dot file: ", e);
        }

        try {
            workflowRunAttempt.setStarted(new Date());
            workflowRunAttempt.setCondorDAGClusterId(jobNode.getCluster());
            workflowRunAttempt.setSubmitDirectory(this.submitDirectory.getAbsolutePath());
            MaPSeqDAOBean maPSeqDAOBean = getWorkflowBeanService().getMaPSeqDAOBean();
            WorkflowRunAttemptDAO workflowRunAttemptDAO = maPSeqDAOBean.getWorkflowRunAttemptDAO();
            workflowRunAttemptDAO.save(workflowRunAttempt);
        } catch (MaPSeqDAOException e) {
            logger.error("Problem saving WorkflowRun: ", e);
            throw new WorkflowException(e);
        }

        return jobNode;
    }

    @Override
    public void cleanUp() throws WorkflowException {
        logger.debug("ENTERING cleanUp()");

        RunModeType runMode = RunModeType.PROD;
        String version = getVersion();
        if (StringUtils.isEmpty(version) || (StringUtils.isNotEmpty(version) && version.contains("SNAPSHOT"))) {
            runMode = RunModeType.DEV;
        }

        if (runMode.equals(RunModeType.PROD) && this.submitDirectory != null && this.submitDirectory.exists()) {
            File[] submitDirFileArray = submitDirectory.listFiles();
            for (File f : submitDirFileArray) {
                f.delete();
            }
        }
    }

    public Graph<CondorJob, CondorJobEdge> getGraph() {
        return graph;
    }

    public void setGraph(Graph<CondorJob, CondorJobEdge> graph) {
        this.graph = graph;
    }

    public WorkflowRunAttempt getWorkflowRunAttempt() {
        return workflowRunAttempt;
    }

    public void setWorkflowRunAttempt(WorkflowRunAttempt workflowRunAttempt) {
        this.workflowRunAttempt = workflowRunAttempt;
    }

    public File getBaseOutputDirectory() {
        return baseOutputDirectory;
    }

    public void setBaseOutputDirectory(File baseOutputDirectory) {
        this.baseOutputDirectory = baseOutputDirectory;
    }

    public File getSubmitDirectory() {
        return submitDirectory;
    }

    public void setSubmitDirectory(File submitDirectory) {
        this.submitDirectory = submitDirectory;
    }

    public WorkflowBeanService getWorkflowBeanService() {
        return workflowBeanService;
    }

    public void setWorkflowBeanService(WorkflowBeanService workflowBeanService) {
        this.workflowBeanService = workflowBeanService;
    }

    public Integer getBackOffMultiplier() {
        return backOffMultiplier;
    }

    public void setBackOffMultiplier(Integer backOffMultiplier) {
        this.backOffMultiplier = backOffMultiplier;
    }

}
