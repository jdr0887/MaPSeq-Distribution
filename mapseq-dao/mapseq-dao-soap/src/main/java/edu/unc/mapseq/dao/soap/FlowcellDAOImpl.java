package edu.unc.mapseq.dao.soap;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.ConnectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.ws.FlowcellService;

/**
 * 
 * @author jdr0887
 */
@Component
public class FlowcellDAOImpl extends NamedEntityDAOImpl<Flowcell, Long> implements FlowcellDAO {

    private final Logger logger = LoggerFactory.getLogger(FlowcellDAOImpl.class);

    private FlowcellService flowcellService;

    public FlowcellDAOImpl() {
        super(Flowcell.class);
    }

    @PostConstruct
    public void init() {
        flowcellService = getService().getPort(FlowcellService.class);
        Client cl = ClientProxy.getClient(flowcellService);
        HTTPConduit httpConduit = (HTTPConduit) cl.getConduit();
        httpConduit.getClient().setReceiveTimeout(getConfigurationService().getWebServiceTimeout());
        httpConduit.getClient().setConnectionTimeout(0);
        httpConduit.getClient().setConnection(ConnectionType.CLOSE);
    }

    @Override
    public Long save(Flowcell entity) throws MaPSeqDAOException {
        logger.debug("ENTERING save(Flowcell)");
        Long id = flowcellService.save(entity);
        return id;
    }

    @Override
    public List<Flowcell> findByName(String flowcellName) throws MaPSeqDAOException {
        logger.debug("ENTERING findByName(String)");
        List<Flowcell> ret = flowcellService.findByName(flowcellName);
        return ret;
    }

    @Override
    public Flowcell findById(Long id) throws MaPSeqDAOException {
        logger.debug("ENTERING findById(Long)");
        Flowcell sequencerRun = flowcellService.findById(id);
        return sequencerRun;
    }

    @Override
    public List<Flowcell> findByStudyName(String name) throws MaPSeqDAOException {
        logger.debug("ENTERING findByStudyName(String)");
        List<Flowcell> sequencerRunList = flowcellService.findByStudyName(name);
        return sequencerRunList;
    }

    @Override
    public List<Flowcell> findByCreatedDateRange(Date startDate, Date endDate) throws MaPSeqDAOException {
        logger.debug("ENTERING findByCreatedDateRange(Date, Date)");
        String formattedStartDate = DateFormatUtils.ISO_DATE_FORMAT.format(startDate);
        String formattedEndDate = DateFormatUtils.ISO_DATE_FORMAT.format(endDate);
        List<Flowcell> sequencerRunList = flowcellService.findByCreatedDateRange(formattedStartDate, formattedEndDate);
        return sequencerRunList;
    }

    @Override
    public List<Flowcell> findAll() throws MaPSeqDAOException {
        logger.debug("ENTERING findAll()");
        List<Flowcell> flowcellList = flowcellService.findAll();
        return flowcellList;
    }

    @Override
    public List<Flowcell> findByWorkflowRunId(Long workflowRunId) throws MaPSeqDAOException {
        logger.debug("ENTERING findByWorkflowRunId(Long)");
        List<Flowcell> flowcellList = flowcellService.findByWorkflowRunId(workflowRunId);
        return flowcellList;
    }

    @Override
    public List<Flowcell> findByExample(Flowcell flowcell) throws MaPSeqDAOException {
        return null;
    }

    @Override
    public void addFileData(Long fileDataId, Long flowcellId) throws MaPSeqDAOException {
        logger.debug("ENTERING addFileData(Long, Long)");
        flowcellService.addFileData(fileDataId, flowcellId);
    }

}
