package edu.unc.mapseq.dao.soap;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.ConnectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleWorkflowRunDependencyDAO;
import edu.unc.mapseq.dao.model.SampleWorkflowRunDependency;
import edu.unc.mapseq.ws.SampleWorkflowRunDependencyService;

/**
 * 
 * @author jdr0887
 */
@Component
public class SampleWorkflowRunDependencyDAOImpl extends BaseDAOImpl<SampleWorkflowRunDependency, Long>
        implements SampleWorkflowRunDependencyDAO {

    private static final Logger logger = LoggerFactory.getLogger(SampleWorkflowRunDependencyDAOImpl.class);

    private SampleWorkflowRunDependencyService sampleWorkflowRunDependencyService;

    public SampleWorkflowRunDependencyDAOImpl() {
        super(SampleWorkflowRunDependency.class);
    }

    @PostConstruct
    public void init() {
        sampleWorkflowRunDependencyService = getService().getPort(SampleWorkflowRunDependencyService.class);
        Client cl = ClientProxy.getClient(sampleWorkflowRunDependencyService);
        HTTPConduit httpConduit = (HTTPConduit) cl.getConduit();
        httpConduit.getClient().setReceiveTimeout(getConfigurationService().getWebServiceTimeout());
        httpConduit.getClient().setConnectionTimeout(0);
        httpConduit.getClient().setConnection(ConnectionType.CLOSE);
    }

    @Override
    public SampleWorkflowRunDependency findById(Long id) throws MaPSeqDAOException {
        logger.debug("ENTERING findById(Long)");
        SampleWorkflowRunDependency sampleWorkflowRunDependency = sampleWorkflowRunDependencyService.findById(id);
        return sampleWorkflowRunDependency;
    }

    @Override
    public Long save(SampleWorkflowRunDependency entity) throws MaPSeqDAOException {
        logger.debug("ENTERING save(SampleWorkflowRunDependency)");
        Long id = sampleWorkflowRunDependencyService.save(entity);
        return id;
    }

    @Override
    public List<SampleWorkflowRunDependency> findBySampleIdAndChildWorkflowRunId(Long sampleId, Long workflowRunId)
            throws MaPSeqDAOException {
        logger.debug("ENTERING findBySampleIdAndChildWorkflowRunId(Long, Long)");
        List<SampleWorkflowRunDependency> results = sampleWorkflowRunDependencyService.findBySampleIdAndChildWorkflowRunId(sampleId,
                workflowRunId);
        return results;
    }

}
