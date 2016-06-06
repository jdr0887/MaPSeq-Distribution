package edu.unc.mapseq.dao.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleWorkflowRunDependencyDAO;
import edu.unc.mapseq.dao.model.SampleWorkflowRunDependency;

@Component
public class SampleWorkflowRunDependencyDAOImpl extends BaseDAOImpl<SampleWorkflowRunDependency, Long>
        implements SampleWorkflowRunDependencyDAO {

    private static final Logger logger = LoggerFactory.getLogger(SampleWorkflowRunDependencyDAOImpl.class);

    public SampleWorkflowRunDependencyDAOImpl() {
        super(SampleWorkflowRunDependency.class);
    }

    @Override
    public SampleWorkflowRunDependency findById(Long id) throws MaPSeqDAOException {
        logger.debug("ENTERING findById(Long)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        SampleWorkflowRunDependency sampleWorkflowRunDependency = client.path("findById/{id}", id).accept(MediaType.APPLICATION_JSON)
                .get(SampleWorkflowRunDependency.class);
        return sampleWorkflowRunDependency;
    }

    @Override
    public Long save(SampleWorkflowRunDependency entity) throws MaPSeqDAOException {
        logger.debug("ENTERING save(SampleWorkflowRunDependency)");
        WebClient client = WebClient.create(getRestServiceURL()).type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
        Response response = client.path("/").post(entity);
        Long id = response.readEntity(Long.class);
        return id;
    }

    @Override
    public List<SampleWorkflowRunDependency> findBySampleIdAndChildWorkflowRunId(Long sampleId, Long workflowRunId)
            throws MaPSeqDAOException {
        logger.debug("ENTERING findBySampleIdAndChildWorkflowRunId(Long, Long)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Collection<? extends SampleWorkflowRunDependency> results = client
                .path("findBySampleIdAndChildWorkflowRunId/{sampleId}/{workflowRunId}", sampleId, workflowRunId)
                .accept(MediaType.APPLICATION_JSON).getCollection(SampleWorkflowRunDependency.class);
        return new ArrayList<SampleWorkflowRunDependency>(results);
    }

    @Override
    public List<SampleWorkflowRunDependency> findBySampleId(Long sampleId) throws MaPSeqDAOException {
        logger.debug("ENTERING findBySampleId(Long)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Collection<? extends SampleWorkflowRunDependency> results = client.path("findBySampleId/{sampleId}", sampleId)
                .accept(MediaType.APPLICATION_JSON).getCollection(SampleWorkflowRunDependency.class);
        return new ArrayList<SampleWorkflowRunDependency>(results);
    }

}
