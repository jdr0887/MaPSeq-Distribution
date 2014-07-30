package edu.unc.mapseq.dao.ws;

import java.util.List;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.ConnectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowRunAttemptDAO;
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;
import edu.unc.mapseq.ws.WorkflowRunAttemptService;

/**
 * 
 * @author jdr0887
 */
public class WorkflowRunAttemptDAOImpl extends BaseDAOImpl<WorkflowRunAttempt, Long> implements WorkflowRunAttemptDAO {

    private final Logger logger = LoggerFactory.getLogger(WorkflowRunAttemptDAOImpl.class);

    private WorkflowRunAttemptService workflowRunAttemptService;

    public WorkflowRunAttemptDAOImpl() {
        super(WorkflowRunAttempt.class);
    }

    public void init() {
        workflowRunAttemptService = getService().getPort(WorkflowRunAttemptService.class);
        Client cl = ClientProxy.getClient(workflowRunAttemptService);
        HTTPConduit httpConduit = (HTTPConduit) cl.getConduit();
        httpConduit.getClient().setReceiveTimeout(getConfigurationService().getWebServiceTimeout());
        httpConduit.getClient().setConnectionTimeout(0);
        httpConduit.getClient().setConnection(ConnectionType.CLOSE);
    }

    @Override
    public Long save(WorkflowRunAttempt entity) throws MaPSeqDAOException {
        logger.debug("ENTERING save(WorkflowPlan)");
        return null;
    }

    @Override
    public List<WorkflowRunAttempt> findByWorkflowRunId(Long workflowRunId) throws MaPSeqDAOException {
        logger.info("ENTERING findByWorkflowRunId(Long)");
        List<WorkflowRunAttempt> ret = workflowRunAttemptService.findByWorkflowRunId(workflowRunId);
        return ret;
    }

    @Override
    public WorkflowRunAttempt findById(Long id) throws MaPSeqDAOException {
        logger.info("ENTERING findById(Long)");
        return null;
    }

    @Override
    public List<WorkflowRunAttempt> findEnqueued(Long workflowId) throws MaPSeqDAOException {
        logger.info("ENTERING findEnqueued(Long)");
        return null;
    }

    @Override
    public List<WorkflowRunAttempt> findEnqueued(Long workflowId, int maxResults) throws MaPSeqDAOException {
        logger.info("ENTERING findEnqueued(Long)");
        return null;
    }

}
