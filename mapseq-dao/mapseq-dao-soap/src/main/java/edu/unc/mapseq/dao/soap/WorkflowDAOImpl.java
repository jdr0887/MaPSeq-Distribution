package edu.unc.mapseq.dao.soap;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.ConnectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowDAO;
import edu.unc.mapseq.dao.model.Workflow;
import edu.unc.mapseq.ws.WorkflowService;

/**
 * 
 * @author jdr0887
 */
@Component
public class WorkflowDAOImpl extends DictionaryEntityDAOImpl<Workflow, Long> implements WorkflowDAO {

    private final Logger logger = LoggerFactory.getLogger(WorkflowDAOImpl.class);

    private WorkflowService workflowService;

    public WorkflowDAOImpl() {
        super(Workflow.class);
    }

    public void init() {
        workflowService = getService().getPort(WorkflowService.class);
        Client cl = ClientProxy.getClient(workflowService);
        HTTPConduit httpConduit = (HTTPConduit) cl.getConduit();
        httpConduit.getClient().setReceiveTimeout(getConfigurationService().getWebServiceTimeout());
        httpConduit.getClient().setConnectionTimeout(0);
        httpConduit.getClient().setConnection(ConnectionType.CLOSE);
    }

    @Override
    public Long save(Workflow entity) throws MaPSeqDAOException {
        logger.debug("ENTERING save(Workflow)");
        return null;
    }

    @Override
    public Workflow findById(Long id) throws MaPSeqDAOException {
        Workflow workflow = workflowService.findById(id);
        return workflow;
    }

    @Override
    public List<Workflow> findAll() throws MaPSeqDAOException {
        List<Workflow> ret = workflowService.findAll();
        return ret;
    }

    @Override
    public List<Workflow> findByName(String name) throws MaPSeqDAOException {
        List<Workflow> ret = workflowService.findByName(name);
        return ret;
    }

    @Override
    public List<Workflow> findByCreatedDateRange(Date started, Date finished) throws MaPSeqDAOException {
        String formattedStartDate = DateFormatUtils.ISO_DATE_FORMAT.format(started);
        String formattedEndDate = DateFormatUtils.ISO_DATE_FORMAT.format(finished);
        List<Workflow> ret = workflowService.findByCreatedDateRange(formattedStartDate, formattedEndDate);
        return ret;
    }

}
