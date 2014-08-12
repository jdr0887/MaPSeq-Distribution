package edu.unc.mapseq.dao.rs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowRunAttemptDAO;
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;

/**
 * 
 * @author jdr0887
 */
public class WorkflowRunAttemptDAOImpl extends BaseDAOImpl<WorkflowRunAttempt, Long> implements WorkflowRunAttemptDAO {

    private final Logger logger = LoggerFactory.getLogger(WorkflowRunAttemptDAOImpl.class);

    public WorkflowRunAttemptDAOImpl() {
        super(WorkflowRunAttempt.class);
    }

    @Override
    public Long save(WorkflowRunAttempt entity) throws MaPSeqDAOException {
        logger.info("ENTERING save(WorkflowRunAttempt)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true).type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        Response response = client.path("/").post(entity);
        Long id = response.readEntity(Long.class);
        return id;
    }

    @Override
    public List<WorkflowRunAttempt> findByWorkflowRunId(Long workflowRunId) throws MaPSeqDAOException {
        logger.info("ENTERING findByWorkflowRunId(Long)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Collection<? extends WorkflowRunAttempt> ret = client
                .path("findByWorkflowRunId/{workflowRunId}", workflowRunId).accept(MediaType.APPLICATION_JSON)
                .getCollection(WorkflowRunAttempt.class);
        return new ArrayList<WorkflowRunAttempt>(ret);
    }

    @Override
    public List<WorkflowRunAttempt> findByWorkflowId(Long workflowId) throws MaPSeqDAOException {
        logger.info("ENTERING findByWorkflowId(Long)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Collection<? extends WorkflowRunAttempt> ret = client.path("findByWorkflowId/{workflowId}", workflowId)
                .accept(MediaType.APPLICATION_JSON).getCollection(WorkflowRunAttempt.class);
        return new ArrayList<WorkflowRunAttempt>(ret);
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
