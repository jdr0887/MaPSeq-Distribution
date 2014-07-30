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

import edu.unc.mapseq.dao.HTSFSampleDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.HTSFSample;
import edu.unc.mapseq.ws.HTSFSampleService;

/**
 * 
 * @author jdr0887
 */
public class HTSFSampleDAOImpl extends BaseEntityDAOImpl<HTSFSample, Long> implements HTSFSampleDAO {

    private final Logger logger = LoggerFactory.getLogger(HTSFSampleDAOImpl.class);

    private HTSFSampleService htsfSampleService;

    public HTSFSampleDAOImpl() {
        super(HTSFSample.class);
    }

    public void init() {
        htsfSampleService = getService().getPort(HTSFSampleService.class);
        Client cl = ClientProxy.getClient(htsfSampleService);
        HTTPConduit httpConduit = (HTTPConduit) cl.getConduit();
        httpConduit.getClient().setReceiveTimeout(getConfigurationService().getWebServiceTimeout());
        httpConduit.getClient().setConnectionTimeout(0);
        httpConduit.getClient().setConnection(ConnectionType.CLOSE);
    }

    @Override
    public HTSFSample findById(Long id) throws MaPSeqDAOException {
        logger.debug("ENTERING findById(Long)");
        HTSFSample sample = htsfSampleService.findById(id);
        return sample;
    }

    @Override
    public Long save(HTSFSample entity) throws MaPSeqDAOException {
        logger.debug("ENTERING save(HTSFSample)");
        Long id = htsfSampleService.save(entity);
        return id;
    }

    @Override
    public List<HTSFSample> findBySequencerRunId(Long sequencerRunId) throws MaPSeqDAOException {
        logger.debug("ENTERING findBySequencerRunId(Long)");
        List<HTSFSample> htsfSampleList = htsfSampleService.findBySequencerRunId(sequencerRunId);
        return htsfSampleList;
    }

    @Override
    public List<HTSFSample> findBySequencerRunIdAndSampleName(Long sequencerRunId, String sampleName)
            throws MaPSeqDAOException {
        logger.debug("ENTERING findBySequencerRunIdAndSampleName(Long, String)");
        List<HTSFSample> htsfSampleList = htsfSampleService.findBySequencerRunIdAndSampleName(sequencerRunId,
                sampleName);
        return htsfSampleList;
    }

    @Override
    public List<HTSFSample> findByCreationDateRange(Date startDate, Date endDate) throws MaPSeqDAOException {
        logger.debug("ENTERING findByCreationDateRange(Date, Date)");
        HTSFSampleService htsfSampleService = getService().getPort(HTSFSampleService.class);
        Client cl = ClientProxy.getClient(htsfSampleService);
        HTTPConduit httpConduit = (HTTPConduit) cl.getConduit();
        httpConduit.getClient().setReceiveTimeout(getConfigurationService().getWebServiceTimeout());
        String formattedStartDate = DateFormatUtils.ISO_DATE_FORMAT.format(startDate);
        String formattedEndDate = DateFormatUtils.ISO_DATE_FORMAT.format(endDate);
        List<HTSFSample> htsfSampleList = htsfSampleService.findByCreationDateRange(formattedStartDate,
                formattedEndDate);
        return htsfSampleList;
    }

    @Override
    public List<HTSFSample> findByName(String name) throws MaPSeqDAOException {
        logger.debug("ENTERING findByName(String)");
        HTSFSampleService htsfSampleService = getService().getPort(HTSFSampleService.class);
        Client cl = ClientProxy.getClient(htsfSampleService);
        HTTPConduit httpConduit = (HTTPConduit) cl.getConduit();
        httpConduit.getClient().setReceiveTimeout(getConfigurationService().getWebServiceTimeout());
        List<HTSFSample> htsfSampleList = htsfSampleService.findByName(name);
        return htsfSampleList;
    }

}
