package edu.unc.mapseq.dao.ws;

import java.util.List;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.ConnectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowPlanDAO;
import edu.unc.mapseq.dao.model.WorkflowPlan;
import edu.unc.mapseq.ws.WorkflowPlanService;

/**
 * 
 * @author jdr0887
 */
public class WorkflowPlanDAOImpl extends BaseDAOImpl<WorkflowPlan, Long> implements WorkflowPlanDAO {

    private final Logger logger = LoggerFactory.getLogger(WorkflowPlanDAOImpl.class);

    private WorkflowPlanService workflowPlanService;

    public WorkflowPlanDAOImpl() {
        super(WorkflowPlan.class);
    }

    public void init() {
        workflowPlanService = getService().getPort(WorkflowPlanService.class);
        Client cl = ClientProxy.getClient(workflowPlanService);
        HTTPConduit httpConduit = (HTTPConduit) cl.getConduit();
        httpConduit.getClient().setReceiveTimeout(getConfigurationService().getWebServiceTimeout());
        httpConduit.getClient().setConnectionTimeout(0);
        httpConduit.getClient().setConnection(ConnectionType.CLOSE);
    }

    @Override
    public Long save(WorkflowPlan entity) throws MaPSeqDAOException {
        logger.debug("ENTERING save(WorkflowPlan)");
        Long id = workflowPlanService.save(entity);
        return id;
    }

    @Override
    public List<WorkflowPlan> findBySequencerRunId(Long sequencerRunId) throws MaPSeqDAOException {
        logger.info("ENTERING findBySequencerRunId(Long)");
        List<WorkflowPlan> ret = workflowPlanService.findBySequencerRunId(sequencerRunId);
        return ret;
    }

    @Override
    public List<WorkflowPlan> findByHTSFSampleId(Long htsfSampleId) throws MaPSeqDAOException {
        logger.info("ENTERING findByHTSFSampleId(Long)");
        WorkflowPlanService workflowPlanService = getService().getPort(WorkflowPlanService.class);
        Client cl = ClientProxy.getClient(workflowPlanService);
        HTTPConduit httpConduit = (HTTPConduit) cl.getConduit();
        httpConduit.getClient().setReceiveTimeout(getConfigurationService().getWebServiceTimeout());
        List<WorkflowPlan> ret = workflowPlanService.findByHTSFSampleId(htsfSampleId);
        return ret;
    }

    @Override
    public List<WorkflowPlan> findByWorkflowRunId(Long workflowRunId) throws MaPSeqDAOException {
        logger.info("ENTERING findByWorkflowRunId(Long)");
        WorkflowPlanService workflowPlanService = getService().getPort(WorkflowPlanService.class);
        Client cl = ClientProxy.getClient(workflowPlanService);
        HTTPConduit httpConduit = (HTTPConduit) cl.getConduit();
        httpConduit.getClient().setReceiveTimeout(getConfigurationService().getWebServiceTimeout());
        List<WorkflowPlan> ret = workflowPlanService.findByWorkflowRunId(workflowRunId);
        return ret;
    }

    @Override
    public WorkflowPlan findById(Long id) throws MaPSeqDAOException {
        logger.info("ENTERING findById(Long)");
        return null;
    }

    @Override
    public List<WorkflowPlan> findByStudyName(String studyName) throws MaPSeqDAOException {
        logger.info("ENTERING findByStudyName(String)");
        List<WorkflowPlan> workflowPlanList = workflowPlanService.findByStudyName(studyName);
        return workflowPlanList;
    }

    @Override
    public List<WorkflowPlan> findEnqueued(Long workflowId) throws MaPSeqDAOException {
        logger.info("ENTERING findEnqueued(Long)");
        return null;
    }

    @Override
    public List<WorkflowPlan> findEnqueued(Long workflowId, int maxResults) throws MaPSeqDAOException {
        logger.info("ENTERING findEnqueued(Long)");
        return null;
    }

    @Override
    public List<WorkflowPlan> findBySequencerRunAndWorkflowName(Long sequencerRunId, String workflowName)
            throws MaPSeqDAOException {
        logger.info("ENTERING findBySequencerRunAndWorkflowName(Long, String)");
        List<WorkflowPlan> ret = workflowPlanService.findBySequencerRunAndWorkflowName(sequencerRunId, workflowName);
        return ret;
    }

    @Override
    public List<WorkflowPlan> findByStudyNameAndSampleNameAndWorkflowName(String studyName, String sampleName,
            String workflowName) throws MaPSeqDAOException {
        logger.info("ENTERING findByStudyNameAndSampleNameAndWorkflowName(String, String, String)");
        List<WorkflowPlan> workflowPlanList = workflowPlanService.findByStudyNameAndSampleNameAndWorkflowName(
                studyName, sampleName, workflowName);
        return workflowPlanList;
    }

}
