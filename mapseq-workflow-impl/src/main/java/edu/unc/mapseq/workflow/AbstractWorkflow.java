package edu.unc.mapseq.workflow;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jgrapht.Graph;
import org.renci.jlrm.IOUtils;
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
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.HTSFSample;
import edu.unc.mapseq.dao.model.WorkflowPlan;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.workflow.exporter.SecureCondorSubmitScriptExporter;

public abstract class AbstractWorkflow implements Workflow {

    private final Logger logger = LoggerFactory.getLogger(Workflow.class);

    private WorkflowPlan workflowPlan;

    private Integer backOffMultiplier = 5;

    private File homeDirectory;

    private File outputDirectory;

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
        if (this.workflowPlan == null) {
            logger.error("workflowPlan is null");
            throw new WorkflowException("workflowPlan is null");
        }

        String mapseqHome = System.getenv("MAPSEQ_HOME");
        if (StringUtils.isEmpty(mapseqHome)) {
            logger.error("MAPSEQ_HOME not set in env: {}", mapseqHome);
            throw new WorkflowException("MAPSEQ_HOME not set in env");
        }
        this.homeDirectory = new File(mapseqHome);
        if (!homeDirectory.exists()) {
            logger.error("MAPSEQ_HOME does not exist: {}", mapseqHome);
            throw new WorkflowException("MAPSEQ_HOME does not exist");
        }

        logger.debug("homeDirectory = {}", this.homeDirectory.getAbsolutePath());

        String outputDir = System.getenv("MAPSEQ_OUTPUT_DIRECTORY");
        if (StringUtils.isEmpty(outputDir)) {
            logger.error("MAPSEQ_OUTPUT_DIRECTORY not set in env: {}", outputDir);
            throw new WorkflowException("MAPSEQ_OUTPUT_DIRECTORY not set in env");
        }

        this.outputDirectory = new File(outputDir);
        if (!outputDirectory.exists()) {
            logger.error("MAPSEQ_OUTPUT_DIRECTORY does not exist: {}", outputDir);
            throw new WorkflowException("MAPSEQ_OUTPUT_DIRECTORY does not exist");
        }

        this.submitDirectory = new File(this.homeDirectory, "submit");
        if (!this.submitDirectory.exists()) {
            this.submitDirectory.mkdirs();
        }

        logger.debug("submitDirectory = {}", this.submitDirectory.getAbsolutePath());

    }

    @Override
    public CondorJob call() throws WorkflowException {
        logger.debug("ENTERING call()");

        CondorJob jobNode = null;
        File workDir = IOUtils.createWorkDirectory(getSubmitDirectory(), getName());
        logger.info("workDir = {}", workDir);

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
        jobNode = exporter.export(getName(), workDir, getGraph(), includeGlideinRequirements);
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
            File dotFile = new File(workDir, getName() + ".dag.dot");
            FileWriter fw = new FileWriter(dotFile);
            dotExporter.export(fw, graph);
        } catch (IOException e) {
            logger.warn("Problem writing dot file: ", e);
        }

        try {
            MaPSeqDAOBean maPSeqDAOBean = getWorkflowBeanService().getMaPSeqDAOBean();
            WorkflowRun workflowRun = maPSeqDAOBean.getWorkflowRunDAO().findById(
                    getWorkflowPlan().getWorkflowRun().getId());
            workflowRun.setStartDate(new Date());
            workflowRun.setCondorDAGClusterId(jobNode.getCluster());
            workflowRun.setSubmitDirectory(jobNode.getSubmitFile().getParentFile().getAbsolutePath());
            WorkflowRunDAO workflowRunDAO = maPSeqDAOBean.getWorkflowRunDAO();
            workflowRunDAO.save(workflowRun);
        } catch (MaPSeqDAOException e) {
            logger.error("Problem saving WorkflowRun: ", e);
            throw new WorkflowException(e);
        }

        return jobNode;
    }

    public Set<HTSFSample> getAggregateHTSFSampleSet() throws WorkflowException {

        Set<HTSFSample> htsfSampleSet = new HashSet<HTSFSample>();

        if (getWorkflowPlan().getSequencerRun() == null && getWorkflowPlan().getHTSFSamples() == null) {
            logger.error("Don't have either sequencerRun and htsfSample");
            throw new WorkflowException("Don't have either sequencerRun and htsfSample");
        }

        if (getWorkflowPlan().getSequencerRun() != null) {
            logger.info("sequencerRun: {}", getWorkflowPlan().getSequencerRun().toString());
            try {
                htsfSampleSet.addAll(getWorkflowBeanService().getMaPSeqDAOBean().getHTSFSampleDAO()
                        .findBySequencerRunId(getWorkflowPlan().getSequencerRun().getId()));
            } catch (MaPSeqDAOException e) {
                logger.error("problem getting HTSFSamples");
            }
        }

        if (getWorkflowPlan().getHTSFSamples() != null) {
            htsfSampleSet.addAll(getWorkflowPlan().getHTSFSamples());
        }

        return htsfSampleSet;
    }

    public File createOutputDirectory(String sequencerRunName, HTSFSample htsfSample, String workflowName,
            String version) throws WorkflowException {
        File baseDir;

        RunModeType runMode = RunModeType.PROD;
        if (StringUtils.isEmpty(version) || (StringUtils.isNotEmpty(version) && version.contains("SNAPSHOT"))) {
            runMode = RunModeType.DEV;
        }

        switch (runMode) {
            case DEV:
            case STAGING:
                baseDir = new File(getOutputDirectory(), runMode.toString().toLowerCase());
                break;
            case PROD:
            default:
                baseDir = getOutputDirectory();
                break;
        }
        File sequencerRunOutputDirectory = new File(baseDir, sequencerRunName);
        File workflowDir = new File(sequencerRunOutputDirectory, workflowName);
        File htsfSampleOutputDir = new File(workflowDir, String.format("L%03d_%s", htsfSample.getLaneIndex(),
                htsfSample.getBarcode()));
        File tmpDir = new File(htsfSampleOutputDir, "tmp");
        tmpDir.mkdirs();

        try {
            htsfSample.setOutputDirectory(htsfSampleOutputDir.getAbsolutePath());
            getWorkflowBeanService().getMaPSeqDAOBean().getHTSFSampleDAO().save(htsfSample);
        } catch (MaPSeqDAOException e1) {
            logger.error("Could not persist HTSFSample");
            throw new WorkflowException("Could not persist HTSFSample");
        }

        return htsfSampleOutputDir;
    }

    public Graph<CondorJob, CondorJobEdge> getGraph() {
        return graph;
    }

    public void setGraph(Graph<CondorJob, CondorJobEdge> graph) {
        this.graph = graph;
    }

    public WorkflowPlan getWorkflowPlan() {
        return workflowPlan;
    }

    public void setWorkflowPlan(WorkflowPlan workflowPlan) {
        this.workflowPlan = workflowPlan;
    }

    public File getHomeDirectory() {
        return homeDirectory;
    }

    public void setHomeDirectory(File homeDirectory) {
        this.homeDirectory = homeDirectory;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
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
