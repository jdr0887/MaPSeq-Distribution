package edu.unc.mapseq.dao.ws;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateFormatUtils;
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
    public WorkflowRunAttempt findById(Long id) throws MaPSeqDAOException {
        logger.debug("ENTERING findById(Long)");
        WorkflowRunAttempt ret = workflowRunAttemptService.findById(id);
        return ret;
    }

    @Override
    public Long save(WorkflowRunAttempt attempt) throws MaPSeqDAOException {
        logger.debug("ENTERING save(WorkflowRunAttempt)");
        Long ret = workflowRunAttemptService.save(attempt);
        return ret;
    }

    @Override
    public List<WorkflowRunAttempt> findByWorkflowRunId(Long workflowRunId) throws MaPSeqDAOException {
        logger.debug("ENTERING findByWorkflowRunId(Long)");
        List<WorkflowRunAttempt> ret = workflowRunAttemptService.findByWorkflowRunId(workflowRunId);
        return ret;
    }

    @Override
    public List<WorkflowRunAttempt> findByCreatedDateRangeAndWorkflowId(Date startDate, Date endDate, Long workflowId)
            throws MaPSeqDAOException {
        logger.debug("ENTERING findByCreatedDateRangeAndWorkflowId(Date, Date, Long)");
        String formattedStartDate = DateFormatUtils.ISO_DATE_FORMAT.format(startDate);
        String formattedEndDate = DateFormatUtils.ISO_DATE_FORMAT.format(endDate);
        List<WorkflowRunAttempt> ret = workflowRunAttemptService.findByCreatedDateRangeAndWorkflowId(
                formattedStartDate, formattedEndDate, workflowId);
        return ret;
    }

    @Override
    public List<WorkflowRunAttempt> findByWorkflowId(Long workflowId) throws MaPSeqDAOException {
        logger.debug("ENTERING findByWorkflowId(Long)");
        List<WorkflowRunAttempt> ret = workflowRunAttemptService.findByWorkflowId(workflowId);
        return ret;
    }

    @Override
    public List<WorkflowRunAttempt> findEnqueued(Long arg0, int arg1) throws MaPSeqDAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<WorkflowRunAttempt> findEnqueued(Long arg0) throws MaPSeqDAOException {
        // TODO Auto-generated method stub
        return null;
    }

}
