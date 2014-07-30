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
import edu.unc.mapseq.dao.SequencerRunDAO;
import edu.unc.mapseq.dao.model.SequencerRun;
import edu.unc.mapseq.ws.SequencerRunService;

/**
 * 
 * @author jdr0887
 */
public class SequencerRunDAOImpl extends BaseEntityDAOImpl<SequencerRun, Long> implements SequencerRunDAO {

    private final Logger logger = LoggerFactory.getLogger(SequencerRunDAOImpl.class);

    private SequencerRunService sequencerRunService;

    public SequencerRunDAOImpl() {
        super(SequencerRun.class);
    }

    public void init() {
        sequencerRunService = getService().getPort(SequencerRunService.class);
        Client cl = ClientProxy.getClient(sequencerRunService);
        HTTPConduit httpConduit = (HTTPConduit) cl.getConduit();
        httpConduit.getClient().setReceiveTimeout(getConfigurationService().getWebServiceTimeout());
        httpConduit.getClient().setConnectionTimeout(0);
        httpConduit.getClient().setConnection(ConnectionType.CLOSE);
    }

    @Override
    public Long save(SequencerRun entity) throws MaPSeqDAOException {
        logger.debug("ENTERING save(SequencerRun)");
        Long id = sequencerRunService.save(entity);
        return id;
    }

    @Override
    public List<SequencerRun> findByAccountId(Long id) throws MaPSeqDAOException {
        logger.debug("ENTERING findByAccountId(Long)");
        List<SequencerRun> sequencerRunList = sequencerRunService.findByAccountId(id);
        return sequencerRunList;
    }

    @Override
    public List<SequencerRun> findByName(String flowcellName) throws MaPSeqDAOException {
        logger.debug("ENTERING findByName(String)");
        List<SequencerRun> ret = sequencerRunService.findByName(flowcellName);
        return ret;
    }

    @Override
    public SequencerRun findById(Long id) throws MaPSeqDAOException {
        logger.debug("ENTERING findById(Long)");
        SequencerRun sequencerRun = sequencerRunService.findById(id);
        return sequencerRun;
    }

    @Override
    public List<SequencerRun> findByStudyName(String name) throws MaPSeqDAOException {
        logger.debug("ENTERING findByStudyName(String)");
        List<SequencerRun> sequencerRunList = sequencerRunService.findByStudyName(name);
        return sequencerRunList;
    }

    @Override
    public List<SequencerRun> findByCreationDateRange(Date startDate, Date endDate) throws MaPSeqDAOException {
        logger.debug("ENTERING findByCreationDateRange(Date, Date)");
        String formattedStartDate = DateFormatUtils.ISO_DATE_FORMAT.format(startDate);
        String formattedEndDate = DateFormatUtils.ISO_DATE_FORMAT.format(endDate);
        List<SequencerRun> sequencerRunList = sequencerRunService.findByCreationDateRange(formattedStartDate,
                formattedEndDate);
        return sequencerRunList;
    }

    @Override
    public List<SequencerRun> findByExample(SequencerRun sequencerRun) throws MaPSeqDAOException {
        return null;
    }

    @Override
    public List<SequencerRun> findAll() throws MaPSeqDAOException {
        logger.debug("ENTERING findAll()");
        List<SequencerRun> sequencerRunList = sequencerRunService.findAll();
        return sequencerRunList;
    }

}
